package io.github.tf-govstack.registration.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.github.tf-govstack.commons.packet.dto.packet.BiometricsException;
import io.github.tf-govstack.kernel.clientcrypto.service.impl.ClientCryptoFacade;
import io.github.tf-govstack.kernel.core.util.CryptoUtil;
import io.github.tf-govstack.kernel.core.util.HMACUtils2;
import io.github.tf-govstack.kernel.keymanagerservice.dto.KeyPairGenerateResponseDto;
import io.github.tf-govstack.kernel.keymanagerservice.service.KeymanagerService;
import io.github.tf-govstack.kernel.signature.constant.SignatureConstant;
import io.github.tf-govstack.kernel.signature.dto.JWTSignatureVerifyResponseDto;
import io.github.tf-govstack.kernel.signature.service.SignatureService;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.dto.RegistrationDTO;
import io.github.tf-govstack.registration.dto.packetmanager.BiometricsDto;
import io.github.tf-govstack.registration.dto.packetmanager.DocumentDto;
import io.github.tf-govstack.registration.dto.schema.ConditionalBioAttributes;
import io.github.tf-govstack.registration.dto.schema.RequiredOnExpr;
import io.github.tf-govstack.registration.dto.schema.UiFieldDTO;
import io.github.tf-govstack.registration.entity.FileSignature;
import io.github.tf-govstack.registration.enums.FlowType;
import io.github.tf-govstack.registration.exception.ConnectionException;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.repositories.FileSignatureRepository;
import io.github.tf-govstack.registration.service.IdentitySchemaService;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.github.tf-govstack.registration.util.mastersync.MapperUtils;

/**
 * 
 * @author M1063027
 *
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ RegistrationAppHealthCheckUtil.class, SessionContext.class, ApplicationContext.class,
		RegistrationSystemPropertiesChecker.class, FileUtils.class, CryptoUtil.class, MapperUtils.class,
		HMACUtils2.class})
public class RequiredFieldValidatorTest {

	@Mock
	private IdentitySchemaService identitySchemaService;

	@Mock
	private FileSignatureRepository fileSignatureRepository;

	@Mock
	private KeymanagerService keymanagerService;

	@Mock
	private SignatureService signatureService;

	@Mock
	private ClientCryptoFacade clientCryptoFacade;

	@InjectMocks
	private RequiredFieldValidator requiredFieldValidator;
	
	@Mock
	io.github.tf-govstack.registration.context.ApplicationContext context;	

//	private static final String SCRIPT_NAME = {'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}";

	@Before
	public void init() throws Exception {
		Map<String,Object> appMap = new HashMap<>();
		appMap.put(RegistrationConstants.HTTP_API_READ_TIMEOUT, "30");
		appMap.put(RegistrationConstants.HTTP_API_WRITE_TIMEOUT, "30");
		appMap.put(RegistrationConstants.AGE_GROUP_CONFIG,"{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}" );		
		PowerMockito.mockStatic(ApplicationContext.class);
		Mockito.when(ApplicationContext.map()).thenReturn(appMap);
	}

	@Test
	public void isRequiredFieldTest() {
		UiFieldDTO schemaField = getUiFieldDTO();
		RegistrationDTO registrationDTO = getRegistrationDTO();
		assertEquals(Boolean.FALSE, requiredFieldValidator.isRequiredField(schemaField, registrationDTO));
	}
	@Test
	public void isRequiredFieldExecuteMVLFalseTest() {   
		UiFieldDTO schemaField = getUiFieldDTO();
		List<RequiredOnExpr> requiredOn = new ArrayList<RequiredOnExpr>();
		RegistrationDTO registrationDTO = getRegistrationDTO();
		RequiredOnExpr requiredOnExpr = new RequiredOnExpr();
		requiredOnExpr.setEngine("MVEL");
		requiredOnExpr.setExpr("engine");
		requiredOn.add(requiredOnExpr);
		schemaField.setRequiredOn(requiredOn);
		assertEquals(Boolean.FALSE, requiredFieldValidator.isRequiredField(schemaField, registrationDTO));
	}
	
	@Test
	public void isRequiredFieldFalseTest() {
		UiFieldDTO schemaField = null;
		RegistrationDTO registrationDTO = new RegistrationDTO();
		RequiredOnExpr requiredOnExpr = new RequiredOnExpr();
		requiredOnExpr.setEngine("engine");
		assertEquals(Boolean.FALSE, requiredFieldValidator.isRequiredField(schemaField, registrationDTO));
	}

	@Test
	public void getRequiredBioAttributesTest() {
		UiFieldDTO schemaField =  new UiFieldDTO();
		RegistrationDTO registrationDTO = new RegistrationDTO();
		schemaField.setRequired(Boolean.TRUE);
		schemaField.setConditionalBioAttributes(getConditionalBioAttributes());
		
		registrationDTO.setProcessId("processId");
		registrationDTO.AGE_GROUPS.put("ageGroup", "ageGroup");	
		assertNull(requiredFieldValidator.getRequiredBioAttributes(schemaField, registrationDTO));
	}

	@Test
	public void getRequiredBioAttributesEmptyListTest() {
		UiFieldDTO schemaField =  getUiFieldDTO();
		List<RequiredOnExpr> requiredOn = new ArrayList<RequiredOnExpr>();
		RequiredOnExpr requiredOnExpr1 = new RequiredOnExpr();
		requiredOnExpr1.setEngine("MVEL");
		requiredOnExpr1.setExpr("engine1");
		requiredOn.add(requiredOnExpr1);
		schemaField.setRequiredOn(requiredOn);	
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setProcessId("processId");
		registrationDTO.AGE_GROUPS.put("ageGroup", "ageGroup");		
		assertEquals(Collections.EMPTY_LIST,requiredFieldValidator.getRequiredBioAttributes(schemaField, registrationDTO));
	}

	@Test
	public void getRequiredBioAttributesFalseTest() {
		UiFieldDTO schemaField =  getUiFieldDTO();
		schemaField.setConditionalBioAttributes(null);
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setProcessId("processId");
		registrationDTO.AGE_GROUPS.put("ageGroup", "ageGroup");		
		assertEquals(requiredFieldValidator.getRequiredBioAttributes(schemaField, registrationDTO).size(), 0);
	}

	
	@Test
	public void isFieldVisibleFalseTest() {
		UiFieldDTO schemaField = null;
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setProcessId("processId");
	    assertEquals(Boolean.TRUE, requiredFieldValidator.isFieldVisible(schemaField, registrationDTO));
	}

	@Test
	public void isFieldVisibleTest() {
		UiFieldDTO schemaField = getUiFieldDTO();
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setProcessId("processId");	
		assertEquals(Boolean.FALSE,requiredFieldValidator.isFieldVisible(schemaField, registrationDTO));
	}
	
	@Test
	public void getConditionalBioAttributesFalseTest() {
		UiFieldDTO schemaField = new UiFieldDTO();
		RegistrationDTO registrationDTO = new RegistrationDTO();		
		registrationDTO.setProcessId("processId");
		assertEquals(null, requiredFieldValidator.getConditionalBioAttributes(schemaField, registrationDTO));
	}

	@Test
	public void getConditionalBioAttributesTest() {
		UiFieldDTO schemaField =  getUiFieldDTO();
		List<ConditionalBioAttributes> conditionalAttributes = new ArrayList<ConditionalBioAttributes>();
		ConditionalBioAttributes condAttr = new ConditionalBioAttributes();
		condAttr.setProcess("ALL");	
		condAttr.setAgeGroup("ageGroup");
		conditionalAttributes.add(condAttr);
		schemaField.setConditionalBioAttributes(conditionalAttributes);
		
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setProcessId("processId");		
		assertEquals(null,requiredFieldValidator.getConditionalBioAttributes(schemaField, registrationDTO));
	}
	@Test
	public void evaluateMvelScriptFalseTest() {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setProcessId("processId");
		assertEquals(null, requiredFieldValidator.evaluateMvelScript("scriptName", registrationDTO));
	}
	
	@Test
	public void evaluateMvelScriptExceptionTest() throws RegBaseCheckedException{
		RegistrationDTO registrationDTO = getRegistrationDTO();
		registrationDTO.setProcessId("processId");
		Map<String, Object> allIdentityDetails = new LinkedHashMap<String, Object>();
		allIdentityDetails.put("IDSchemaVersion", 3.50);
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put(RegistrationConstants.AGE_GROUP_CONFIG, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}" );
		applicationMap.put(RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}");
		when(context.map()).thenReturn(applicationMap);		
		requiredFieldValidator.evaluateMvelScript("scriptName", registrationDTO);
	}
	
	@Test
	public void getScriptCacheFailureTest() throws NoSuchAlgorithmException,RegBaseCheckedException,IOException,ConnectionException,CertificateEncodingException{
		RegistrationDTO registrationDTO = getRegistrationDTO();
		registrationDTO.setProcessId("processId");
		Map<String, Object> allIdentityDetails = new LinkedHashMap<String, Object>();
		allIdentityDetails.put("IDSchemaVersion", 3.50);
		Map<String, Object> applicationMap = new HashMap<>();
		FileSignature fileSign = new FileSignature();
		fileSign.setContentLength(10);
		fileSign.setEncrypted(Boolean.TRUE);
		Optional<FileSignature> fileSignature = Optional.of(fileSign);		
		KeyPairGenerateResponseDto certificateDto = new KeyPairGenerateResponseDto();
		PowerMockito.mockStatic(FileUtils.class,CryptoUtil.class, MapperUtils.class,HMACUtils2.class);
		JWTSignatureVerifyResponseDto jwtSignatureVerifyResponseDto = new JWTSignatureVerifyResponseDto();
		jwtSignatureVerifyResponseDto.setSignatureValid(true);
		jwtSignatureVerifyResponseDto.setTrustValid(SignatureConstant.TRUST_VALID);
		Mockito.when(signatureService.jwtVerify(Mockito.any())).thenReturn(jwtSignatureVerifyResponseDto);
		
		Mockito.when(HMACUtils2.digestAsPlainText(Mockito.any())).thenReturn("testing");
		Mockito.when(FileUtils.readFileToString(Mockito.any(File.class), Mockito.any(Charset.class))).thenReturn("test-content");
		Mockito.when(keymanagerService.getCertificate(Mockito.anyString(), Mockito.any())).thenReturn(certificateDto);		
		Mockito.when(fileSignatureRepository.findByFileName(Mockito.anyString())).thenReturn(fileSignature);
		applicationMap.put(RegistrationConstants.AGE_GROUP_CONFIG, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}" );
		applicationMap.put(RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}");
		when(context.map()).thenReturn(applicationMap);	
		assertNull(requiredFieldValidator.evaluateMvelScript("MINOR", registrationDTO));
	}
	
	@Test
	public void getScriptFileSignatureEncryptionFailureTest() throws NoSuchAlgorithmException,RegBaseCheckedException,IOException,ConnectionException,CertificateEncodingException{
		RegistrationDTO registrationDTO = getRegistrationDTO();
		registrationDTO.setProcessId("processId");
		Map<String, Object> allIdentityDetails = new LinkedHashMap<String, Object>();
		allIdentityDetails.put("IDSchemaVersion", 3.50);
		Map<String, Object> applicationMap = new HashMap<>();
		FileSignature fileSign = new FileSignature();
		fileSign.setContentLength(10);
		fileSign.setEncrypted(Boolean.FALSE);
		Optional<FileSignature> fileSignature = Optional.of(fileSign);		
		KeyPairGenerateResponseDto certificateDto = new KeyPairGenerateResponseDto();
		PowerMockito.mockStatic(FileUtils.class,CryptoUtil.class, MapperUtils.class,HMACUtils2.class);
		JWTSignatureVerifyResponseDto jwtSignatureVerifyResponseDto = new JWTSignatureVerifyResponseDto();
		jwtSignatureVerifyResponseDto.setSignatureValid(true);
		jwtSignatureVerifyResponseDto.setTrustValid(SignatureConstant.TRUST_VALID);
		Mockito.when(signatureService.jwtVerify(Mockito.any())).thenReturn(jwtSignatureVerifyResponseDto);
		
		Mockito.when(HMACUtils2.digestAsPlainText(Mockito.any())).thenReturn("testing");
		Mockito.when(FileUtils.readFileToString(Mockito.any(File.class), Mockito.any(Charset.class))).thenReturn("test-content");
		Mockito.when(keymanagerService.getCertificate(Mockito.anyString(), Mockito.any())).thenReturn(certificateDto);		
		Mockito.when(fileSignatureRepository.findByFileName(Mockito.anyString())).thenReturn(fileSignature);
		applicationMap.put(RegistrationConstants.AGE_GROUP_CONFIG, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}" );
		applicationMap.put(RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}");
		when(context.map()).thenReturn(applicationMap);	
		assertNull(requiredFieldValidator.evaluateMvelScript("MINOR", registrationDTO));
	}
	
	
	@Test
	public void getScriptFileSignatureFailureTest() throws NoSuchAlgorithmException,RegBaseCheckedException,IOException,ConnectionException,CertificateEncodingException{
		RegistrationDTO registrationDTO = getRegistrationDTO();
		registrationDTO.setProcessId("processId");
		Map<String, Object> allIdentityDetails = new LinkedHashMap<String, Object>();
		allIdentityDetails.put("IDSchemaVersion", 3.50);
		Map<String, Object> applicationMap = new HashMap<>();
		FileSignature fileSign = new FileSignature();
		fileSign.setContentLength(10);
		fileSign.setEncrypted(Boolean.FALSE);
		Optional<FileSignature> fileSignature = Optional.empty();
		
		KeyPairGenerateResponseDto certificateDto = new KeyPairGenerateResponseDto();
		PowerMockito.mockStatic(FileUtils.class,CryptoUtil.class, MapperUtils.class,HMACUtils2.class);
		JWTSignatureVerifyResponseDto jwtSignatureVerifyResponseDto = new JWTSignatureVerifyResponseDto();
		jwtSignatureVerifyResponseDto.setSignatureValid(true);
		jwtSignatureVerifyResponseDto.setTrustValid(SignatureConstant.TRUST_VALID);
		Mockito.when(signatureService.jwtVerify(Mockito.any())).thenReturn(jwtSignatureVerifyResponseDto);
		
		Mockito.when(HMACUtils2.digestAsPlainText(Mockito.any())).thenReturn("testing");
		Mockito.when(FileUtils.readFileToString(Mockito.any(File.class), Mockito.any(Charset.class))).thenReturn("test-content");
		Mockito.when(keymanagerService.getCertificate(Mockito.anyString(), Mockito.any())).thenReturn(certificateDto);		
		Mockito.when(fileSignatureRepository.findByFileName(Mockito.anyString())).thenReturn(fileSignature);
		applicationMap.put(RegistrationConstants.AGE_GROUP_CONFIG, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}" );
		applicationMap.put(RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}");
		when(context.map()).thenReturn(applicationMap);	
		assertNull(requiredFieldValidator.evaluateMvelScript("MINOR", registrationDTO));
	}
	
	@Test
	public void getScriptValidateScriptSignatureFailureTest() throws NoSuchAlgorithmException,RegBaseCheckedException,IOException,ConnectionException,CertificateEncodingException{
		RegistrationDTO registrationDTO = getRegistrationDTO();
		registrationDTO.setProcessId("processId");
		Map<String, Object> allIdentityDetails = new LinkedHashMap<String, Object>();
		allIdentityDetails.put("IDSchemaVersion", 3.50);
		Map<String, Object> applicationMap = new HashMap<>();
		FileSignature fileSign = new FileSignature();
		fileSign.setContentLength(10);
		fileSign.setEncrypted(Boolean.TRUE);
		Optional<FileSignature> fileSignature = Optional.of(fileSign);
		
		KeyPairGenerateResponseDto certificateDto = new KeyPairGenerateResponseDto();
		PowerMockito.mockStatic(FileUtils.class,CryptoUtil.class, MapperUtils.class,HMACUtils2.class);
		JWTSignatureVerifyResponseDto jwtSignatureVerifyResponseDto = new JWTSignatureVerifyResponseDto();
		jwtSignatureVerifyResponseDto.setSignatureValid(Boolean.FALSE);
		jwtSignatureVerifyResponseDto.setTrustValid(SignatureConstant.TRUST_VALID);
		Mockito.when(signatureService.jwtVerify(Mockito.any())).thenReturn(jwtSignatureVerifyResponseDto);
		
		Mockito.when(HMACUtils2.digestAsPlainText(Mockito.any())).thenReturn("testing");
		Mockito.when(FileUtils.readFileToString(Mockito.any(File.class), Mockito.any(Charset.class))).thenReturn("test-content");
		Mockito.when(keymanagerService.getCertificate(Mockito.anyString(), Mockito.any())).thenReturn(certificateDto);		
		Mockito.when(fileSignatureRepository.findByFileName(Mockito.anyString())).thenReturn(fileSignature);
		applicationMap.put(RegistrationConstants.AGE_GROUP_CONFIG, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}" );
		applicationMap.put(RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}");
		when(context.map()).thenReturn(applicationMap);
		assertNull(requiredFieldValidator.evaluateMvelScript("scriptName", registrationDTO));
		
	}
	@Test
	public void getScriptTest() throws NoSuchAlgorithmException,RegBaseCheckedException,IOException,ConnectionException,CertificateEncodingException{
		RegistrationDTO registrationDTO = getRegistrationDTO();
		registrationDTO.setProcessId("processId");
		Map<String, Object> allIdentityDetails = new LinkedHashMap<String, Object>();
		allIdentityDetails.put("IDSchemaVersion", 3.50);
		Map<String, Object> applicationMap = new HashMap<>();
		FileSignature fileSign = new FileSignature();
		fileSign.setContentLength(10);
		fileSign.setEncrypted(Boolean.TRUE);
		Optional<FileSignature> fileSignature = Optional.of(fileSign);
		KeyPairGenerateResponseDto certificateDto = new KeyPairGenerateResponseDto();
		PowerMockito.mockStatic(FileUtils.class,CryptoUtil.class, MapperUtils.class,HMACUtils2.class);
		
		Mockito.when(HMACUtils2.digestAsPlainText(Mockito.any())).thenReturn("testing");
		Mockito.when(FileUtils.readFileToString(Mockito.any(File.class), Mockito.any(Charset.class))).thenReturn("test-content");
		Mockito.when(keymanagerService.getCertificate(Mockito.anyString(), Mockito.any())).thenReturn(certificateDto);		
		Mockito.when(fileSignatureRepository.findByFileName(Mockito.anyString())).thenReturn(fileSignature);
		applicationMap.put(RegistrationConstants.AGE_GROUP_CONFIG, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}" );
		applicationMap.put(RegistrationConstants.APPLICANT_TYPE_MVEL_SCRIPT, "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}");
		when(context.map()).thenReturn(applicationMap);	
		assertNull(requiredFieldValidator.evaluateMvelScript("scriptName", registrationDTO));
	}
	
	
	
	@Test
	public void isRequiredFieldExpressionTest() {
		UiFieldDTO schemaField = getUiFieldDTO();
		List<RequiredOnExpr> requiredOn = new ArrayList<RequiredOnExpr>();
		RequiredOnExpr requiredOnExpr1 = new RequiredOnExpr();
		requiredOnExpr1.setEngine("MVEL");
		requiredOnExpr1.setExpr("engine1");
		requiredOn.add(requiredOnExpr1);
		schemaField.setRequiredOn(requiredOn);		
		RegistrationDTO registrationDTO = new RegistrationDTO();
		RequiredOnExpr requiredOnExpr = new RequiredOnExpr();
		requiredOnExpr.setEngine("engine");
		assertEquals(Boolean.FALSE, requiredFieldValidator.isRequiredField(schemaField, registrationDTO));
	}
	
	private ConditionalBioAttributes ConditionalBioAttributes() {
		List<String> bioAttributes = new ArrayList<String>();
		bioAttributes.add(new String("str1"));
		ConditionalBioAttributes condAttr = new ConditionalBioAttributes();
		condAttr.setProcess("processId");
		condAttr.setBioAttributes(bioAttributes);
		return condAttr;
	}

	private RegistrationDTO getRegistrationDTO() {
		FlowType flowtype = FlowType.NEW;
		flowtype.setCategory("category");
		List<String> selectedLanguagesByApplicant = new ArrayList<String>();
		selectedLanguagesByApplicant.add("English");
		List<String> updatableFields = new ArrayList<String>();
		updatableFields.add("updateFields");
		List<String> updatableFieldGroups = new ArrayList<String>();
		updatableFieldGroups.add("updatableFieldGroups");
		Map<String, Object> demographics = new HashMap<>();
		Map<String, DocumentDto> documents = new HashMap<>();
		Map<String, BiometricsDto> biometrics = new HashMap<>();
		Map<String, BiometricsException> biometricExceptions = new HashMap<>();
		
		RegistrationDTO registrationDTO = new RegistrationDTO();
		registrationDTO.setIdSchemaVersion(1.6);
		registrationDTO.setFlowType(FlowType.NEW);
		registrationDTO.setProcessId("processId");
		registrationDTO.setSelectedLanguagesByApplicant(selectedLanguagesByApplicant);
		registrationDTO.setUpdatableFields(updatableFields);
		registrationDTO.setUpdatableFieldGroups(updatableFieldGroups);
		registrationDTO.setDocuments(documents);
		registrationDTO.setDemographics(demographics);
		registrationDTO.setBiometrics(biometrics);
		registrationDTO.setAGE_GROUPS(registrationDTO.AGE_GROUPS);
		registrationDTO.setSELECTED_CODES(registrationDTO.SELECTED_CODES);
		registrationDTO.getSELECTED_CODES();
		registrationDTO.setBiometricExceptions(biometricExceptions);
		return registrationDTO;	
	}
	
	private UiFieldDTO getUiFieldDTO() {

		UiFieldDTO schemaField = new UiFieldDTO();
		List<RequiredOnExpr> requiredOn = new ArrayList<RequiredOnExpr>();
		
		List<String> bioAttributes = getBioAttributes();		
		RequiredOnExpr requiredOnExpr1 = new RequiredOnExpr();
		requiredOnExpr1.setEngine("MVEL");
		requiredOnExpr1.setExpr("identity.get('ageGroup') == 'INFANT' && (identity.get('introducerRID') == nil || identity.get('introducerRID') == empty)");
		requiredOn.add(requiredOnExpr1);

		List<ConditionalBioAttributes> conditionalAttributes = getConditionalBioAttributes();
	
		schemaField.setId("id");
		schemaField.setRequired(Boolean.TRUE);
		schemaField.setRequiredOn(requiredOn);
		schemaField.setConditionalBioAttributes(conditionalAttributes);
		schemaField.setBioAttributes(bioAttributes);
		schemaField.setVisible(requiredOnExpr1);
		return schemaField;

	}

	private List<ConditionalBioAttributes> getConditionalBioAttributes() {
		List<ConditionalBioAttributes> conditionalAttributes = new ArrayList<ConditionalBioAttributes>();
		ConditionalBioAttributes condAttr = new ConditionalBioAttributes();
		condAttr.setProcess("ALL");
		condAttr.setAgeGroup("INFANT");
		condAttr.setValidationExpr(
				"leftEye || rightEye || rightIndex || rightLittle || rightRing || rightMiddle || leftIndex || leftLittle || leftRing || leftMiddle || leftThumb || rightThumb || face");
		condAttr.setBioAttributes(getBioAttributes());
		conditionalAttributes.add(condAttr);
		return conditionalAttributes;
	}

	private List<String> getBioAttributes() {
		List<String> bioAttributes = new ArrayList<String>();
		bioAttributes.add(new String("leftEye"));
		bioAttributes.add(new String("rightEye"));
		bioAttributes.add(new String("rightLittle"));
		bioAttributes.add(new String("rightIndex"));
		bioAttributes.add(new String("rightRing"));
		bioAttributes.add(new String("rightMiddle"));
		bioAttributes.add(new String("leftIndex"));
		bioAttributes.add(new String("leftLittle"));
		return bioAttributes;
	}
}
