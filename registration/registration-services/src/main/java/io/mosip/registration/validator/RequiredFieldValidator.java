package io.github.tf-govstack.registration.validator;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import io.github.tf-govstack.kernel.clientcrypto.service.impl.ClientCryptoFacade;
import io.github.tf-govstack.kernel.clientcrypto.util.ClientCryptoUtils;
import io.github.tf-govstack.kernel.core.exception.IOException;
import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.kernel.core.util.CryptoUtil;
import io.github.tf-govstack.kernel.core.util.FileUtils;
import io.github.tf-govstack.kernel.core.util.HMACUtils2;
import io.github.tf-govstack.kernel.keymanagerservice.dto.KeyPairGenerateResponseDto;
import io.github.tf-govstack.kernel.keymanagerservice.service.KeymanagerService;
import io.github.tf-govstack.kernel.signature.dto.JWTSignatureVerifyRequestDto;
import io.github.tf-govstack.kernel.signature.dto.JWTSignatureVerifyResponseDto;
import io.github.tf-govstack.kernel.signature.service.SignatureService;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.dto.schema.ConditionalBioAttributes;
import io.github.tf-govstack.registration.entity.FileSignature;
import io.github.tf-govstack.registration.repositories.FileSignatureRepository;
import org.json.JSONObject;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.tf-govstack.registration.dto.RegistrationDTO;
import io.github.tf-govstack.registration.dto.schema.RequiredOnExpr;
import io.github.tf-govstack.registration.dto.schema.UiFieldDTO;
import io.github.tf-govstack.registration.service.IdentitySchemaService;

@Component
public class RequiredFieldValidator {

	private static final Logger LOGGER = AppConfig.getLogger(RequiredFieldValidator.class);
	private static final Map<String, String> SCRIPT_CACHE = new HashMap<>();

	@Autowired
	private IdentitySchemaService identitySchemaService;

	@Autowired
	private FileSignatureRepository fileSignatureRepository;

	@Autowired
	private KeymanagerService keymanagerService;

	@Autowired
	private SignatureService signatureService;

	@Autowired
	private ClientCryptoFacade clientCryptoFacade;


	public boolean isRequiredField(UiFieldDTO schemaField, RegistrationDTO registrationDTO) {
		boolean required = schemaField != null ? schemaField.isRequired() : false;
		if (schemaField != null && schemaField.getRequiredOn() != null && !schemaField.getRequiredOn().isEmpty()) {
			Optional<RequiredOnExpr> expression = schemaField.getRequiredOn().stream()
					.filter(field -> "MVEL".equalsIgnoreCase(field.getEngine()) && field.getExpr() != null).findFirst();

			if (expression.isPresent()) {
				required = executeMVEL(expression.get().getExpr(), registrationDTO);
				LOGGER.info("Refreshed {} field isRequired check, required ? {} ", schemaField.getId(), required);
			}
		}
		return required;
	}

	public boolean isFieldVisible(UiFieldDTO schemaField, RegistrationDTO registrationDTO) {
		boolean visible = true;

		if (schemaField != null && schemaField.getVisible() != null && schemaField.getVisible().getEngine().equalsIgnoreCase("MVEL")
				&& schemaField.getVisible().getExpr() != null) {
			visible = executeMVEL(schemaField.getVisible().getExpr(), registrationDTO);
			LOGGER.info("Refreshed {} field visibility : {} ", schemaField.getId(), visible);
		}
		return visible;
	}

	public List<String> getRequiredBioAttributes(UiFieldDTO field, RegistrationDTO registrationDTO) {
		if(!isRequiredField(field, registrationDTO))
			return Collections.EMPTY_LIST;

		if(field.getConditionalBioAttributes() != null) {
			ConditionalBioAttributes selectedCondition = getConditionalBioAttributes(field, registrationDTO);
			if(selectedCondition != null)
				return selectedCondition.getBioAttributes();
		}
		return field.getBioAttributes();
	}

	public ConditionalBioAttributes getConditionalBioAttributes(UiFieldDTO uiFieldDTO, RegistrationDTO registrationDTO) {
		if(uiFieldDTO.getConditionalBioAttributes() == null || uiFieldDTO.getConditionalBioAttributes().isEmpty())
			return null;

		Optional<ConditionalBioAttributes> result = uiFieldDTO.getConditionalBioAttributes().stream().filter(c ->
				c.getAgeGroup().equalsIgnoreCase(registrationDTO.getAgeGroup()) &&
						c.getProcess().equalsIgnoreCase(registrationDTO.getProcessId())).findFirst();

		if(!result.isPresent()) {
			result = uiFieldDTO.getConditionalBioAttributes().stream().filter(c ->
					(c.getAgeGroup().equalsIgnoreCase(registrationDTO.getAgeGroup()) &&
							c.getProcess().equalsIgnoreCase("ALL")) ||
							(c.getAgeGroup().equalsIgnoreCase("ALL") &&
									c.getProcess().equalsIgnoreCase(registrationDTO.getProcessId())) ||
							(c.getAgeGroup().equalsIgnoreCase("ALL") &&
									c.getProcess().equalsIgnoreCase("ALL"))).findFirst();
		}
		return result.isPresent() ? result.get() : null;
	}

	private boolean executeMVEL(String expression, RegistrationDTO registrationDTO) {
		try {
			Map context = new HashMap();
			context.put("identity", registrationDTO.getMVELDataContext());
			VariableResolverFactory resolverFactory = new MapVariableResolverFactory(context);
			return MVEL.evalToBoolean(expression, resolverFactory);
		} catch (Throwable t) {
			LOGGER.error("Failed to evaluate mvel expr", t);
		}
		return false;
	}

	public Object evaluateMvelScript(String scriptName, RegistrationDTO registrationDTO) {
		try {
			Map<String, String>  ageGroups = new HashMap<String, String>();
			JSONObject ageGroupConfig = new JSONObject((String) ApplicationContext.map().get(RegistrationConstants.AGE_GROUP_CONFIG));
			for(String key : ageGroupConfig.keySet()) {
				ageGroups.put(key, ageGroupConfig.getString(key));
			}

			Map context = new HashMap();
			MVEL.eval(getScript(scriptName), context);
			context.put("identity", registrationDTO.getMVELDataContext());
			context.put("ageGroups", ageGroups);
			return MVEL.eval("return getApplicantType();", context, String.class);

		} catch (Throwable t) {
			LOGGER.error("Failed to evaluate mvel script", t);
		}
		return null;
	}

	private String getScript(String scriptName) {
		if(SCRIPT_CACHE.containsKey(scriptName) && SCRIPT_CACHE.get(scriptName) != null)
			return SCRIPT_CACHE.get(scriptName);

		try {
			Optional<FileSignature> fileSignature = fileSignatureRepository.findByFileName(scriptName);
			if(!fileSignature.isPresent()) {
				LOGGER.error("File signature not found : {}", scriptName);
				return null;
			}

			Path path = Paths.get(System.getProperty("user.dir"), scriptName);
			byte[] bytes = fileSignature.get().getEncrypted() ?
					clientCryptoFacade.decrypt(ClientCryptoUtils.decodeBase64Data(
							FileUtils.readFileToString(path.toFile(), StandardCharsets.UTF_8))) :
					FileUtils.readFileToByteArray(path.toFile());
			String actualData = String.format("{\"hash\":\"%s\"}", HMACUtils2.digestAsPlainText(bytes));

			if(!validateScriptSignature(fileSignature.get().getSignature(), actualData)) {
				LOGGER.error("File signature validation failed : {}", scriptName);
				return null;
			}
			SCRIPT_CACHE.put(scriptName, new String(bytes));

		} catch (Throwable t) {
			LOGGER.error("Failed to get mvel script", t);
		}
		return SCRIPT_CACHE.get(scriptName);
	}

	private boolean validateScriptSignature(String signature, String actualData) throws Exception {
		KeyPairGenerateResponseDto certificateDto = keymanagerService
				.getCertificate(RegistrationConstants.RESPONSE_SIGNATURE_PUBLIC_KEY_APP_ID,
						Optional.of(RegistrationConstants.RESPONSE_SIGNATURE_PUBLIC_KEY_REF_ID));

		JWTSignatureVerifyRequestDto jwtSignatureVerifyRequestDto = new JWTSignatureVerifyRequestDto();
		jwtSignatureVerifyRequestDto.setJwtSignatureData(signature);
		jwtSignatureVerifyRequestDto.setActualData(CryptoUtil.encodeToURLSafeBase64(actualData.getBytes(StandardCharsets.UTF_8)));
		jwtSignatureVerifyRequestDto.setCertificateData(certificateDto.getCertificate());

		JWTSignatureVerifyResponseDto verifyResponseDto =  signatureService.jwtVerify(jwtSignatureVerifyRequestDto);
		return verifyResponseDto.isSignatureValid();
	}

}
