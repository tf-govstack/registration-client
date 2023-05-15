package io.github.tf-govstack.registration.service.security.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.micrometer.core.annotation.Counted;
import io.github.tf-govstack.kernel.biometrics.commons.CbeffValidator;
import io.github.tf-govstack.kernel.biometrics.constant.BiometricFunction;
import io.github.tf-govstack.kernel.biometrics.constant.BiometricType;
import io.github.tf-govstack.kernel.biometrics.constant.ProcessedLevelType;
import io.github.tf-govstack.kernel.biometrics.entities.BIR;
import io.github.tf-govstack.kernel.biosdk.provider.factory.BioAPIFactory;
import io.github.tf-govstack.kernel.biosdk.provider.spi.iBioProviderApi;
import io.github.tf-govstack.kernel.clientcrypto.util.ClientCryptoUtils;
import io.github.tf-govstack.kernel.core.bioapi.exception.BiometricException;
import io.github.tf-govstack.kernel.core.exception.ExceptionUtils;
import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.kernel.core.util.HMACUtils2;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.LoginMode;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.dao.UserDetailDAO;
import io.github.tf-govstack.registration.dto.AuthTokenDTO;
import io.github.tf-govstack.registration.dto.AuthenticationValidatorDTO;
import io.github.tf-govstack.registration.dto.UserDTO;
import io.github.tf-govstack.registration.dto.packetmanager.BiometricsDto;
import io.github.tf-govstack.registration.entity.UserBiometric;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.service.login.LoginService;
import io.github.tf-govstack.registration.service.security.AuthenticationService;
import io.github.tf-govstack.registration.util.common.BIRBuilder;
import io.github.tf-govstack.registration.util.common.OTPManager;
import io.github.tf-govstack.registration.util.restclient.AuthTokenUtilService;
import io.github.tf-govstack.registration.util.restclient.ServiceDelegateUtil;

/**
 * Service class for Authentication
 * 
 * @author SaravanaKumar G
 *
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(AuthenticationServiceImpl.class);

	@Autowired
	private LoginService loginService;

	@Autowired
	private OTPManager otpManager;

	@Autowired
	private BioAPIFactory bioAPIFactory;

	@Autowired
	private UserDetailDAO userDetailDAO;
	
	@Autowired
	protected BIRBuilder birBuilder;

	@Autowired
	private AuthTokenUtilService authTokenUtilService;

	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.service.security.AuthenticationServiceImpl#
	 * authValidator(java.lang.String,
	 * io.github.tf-govstack.registration.dto.AuthenticationValidatorDTO)
	 */
	@Counted(recordFailuresOnly = true, extraTags = {"type" , "biometric-login"})
	public Boolean authValidator(String userId, String modality, List<BiometricsDto> biometrics) {
		LOGGER.info("OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				modality + " >> authValidator invoked.");
		try {
			BiometricType biometricType = BiometricType.fromValue(modality);
			List<BIR> record = new ArrayList<>();
			List<UserBiometric> userBiometrics = userDetailDAO.getUserSpecificBioDetails(userId, biometricType.value());
			if (userBiometrics.isEmpty())
				return false;
			userBiometrics.forEach(userBiometric -> {
				try {
					BIR bir = CbeffValidator.getBIRFromXML(userBiometric.getBioRawImage());
					record.add(bir.getBirs().get(0));
				} catch (Exception e) {
					LOGGER.error("Failed deserialization of BIR data of operator with exception >> ", e);
					// Since de-serialization failed, we assume that we stored BDB in database and
					// generating BIR from it
					record.add(birBuilder.buildBir(userBiometric.getUserBiometricId().getBioAttributeCode(),
							userBiometric.getQualityScore(), userBiometric.getBioIsoImage(), ProcessedLevelType.PROCESSED));
				}
			});

			List<BIR> sample = new ArrayList<>(biometrics.size());
			biometrics.forEach(biometricDto -> {
				sample.add(birBuilder.buildBir(biometricDto, ProcessedLevelType.RAW));
			});

			return verifyBiometrics(biometricType, modality, sample, record);

		} catch (BiometricException | RuntimeException e) {
			LOGGER.error("REGISTRATION - OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
					ExceptionUtils.getStackTrace(e));
		}
		return false;
	}

	private boolean verifyBiometrics(BiometricType biometricType, String modality,
									 List<BIR> sample, List<BIR> record) throws BiometricException {
		iBioProviderApi bioProvider = bioAPIFactory.getBioProvider(biometricType, BiometricFunction.MATCH);
		if (Objects.isNull(bioProvider))
			return false;

		LOGGER.info("OPERATOR_AUTHENTICATION", APPLICATION_NAME, APPLICATION_ID,
				modality + " >> Bioprovider instance found : " + bioProvider);
		return bioProvider.verify(sample, record, biometricType, null);
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.service.security.AuthenticationServiceImpl#
	 * authValidator(java.lang.String, java.lang.String, java.lang.String)
	 */
	public AuthTokenDTO authValidator(String validatorType, String userId, String otp, boolean haveToSaveAuthToken) {
		return otpManager.validateOTP(userId, otp, haveToSaveAuthToken);
	}



	/**
	 * to validate the password and send appropriate message to display.
	 *
	 * @param authenticationValidatorDTO - DTO which contains the username and
	 *                                   password entered by the user
	 * @return appropriate message after validation
	 */
	@Counted(recordFailuresOnly = true, extraTags = {"type" , "pwd-login"})
	public Boolean validatePassword(AuthenticationValidatorDTO authenticationValidatorDTO) throws  RegBaseCheckedException {
		LOGGER.debug("Validating credentials using database >>>> {}", authenticationValidatorDTO.getUserId());
		try {
			//Always mandate user to reach server to validate pwd when machine is online
			//As in case of new user, any valid authtoken will be simply allowed
			//to avoid any such scenario, mandate to fetch new token when login
			if(serviceDelegateUtil.isNetworkAvailable()) {
				authTokenUtilService.getAuthTokenAndRefreshToken(LoginMode.PASSWORD);
			}

			UserDTO userDTO = loginService.getUserDetail(authenticationValidatorDTO.getUserId());

			if (null != userDTO && null != userDTO.getSalt() && HMACUtils2
							.digestAsPlainTextWithSalt(authenticationValidatorDTO.getPassword().getBytes(),
									ClientCryptoUtils.decodeBase64Data(userDTO.getSalt()))
							.equals(userDTO.getUserPassword().getPwd())) {
				return  true;
			}

			if (null != userDTO && null == userDTO.getSalt()) {
				throw new RegBaseCheckedException(RegistrationConstants.CREDS_NOT_FOUND,
						RegistrationConstants.CREDS_NOT_FOUND);
			}

		} catch (RegBaseCheckedException e) {
			throw e;
		} catch (RuntimeException | NoSuchAlgorithmException runtimeException) {
			LOGGER.error("Pwd validation failed", runtimeException);
		}
		throw new RegBaseCheckedException(RegistrationConstants.PWD_MISMATCH, RegistrationConstants.PWD_MISMATCH);
	}

}
