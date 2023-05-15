package io.github.tf-govstack.registration.service.template.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.EMAIL_SERVICE;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.EMAIL_SUBJECT;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.NOTIFICATION_SERVICE;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.SMS_SERVICE;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.github.tf-govstack.kernel.core.exception.ExceptionUtils;
import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.kernel.core.util.DateUtils;
import io.github.tf-govstack.kernel.core.util.StringUtils;
import io.github.tf-govstack.registration.audit.AuditManagerService;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.AuditEvent;
import io.github.tf-govstack.registration.constants.AuditReferenceIdTypes;
import io.github.tf-govstack.registration.constants.Components;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.dto.ErrorResponseDTO;
import io.github.tf-govstack.registration.dto.NotificationDTO;
import io.github.tf-govstack.registration.dto.ResponseDTO;
import io.github.tf-govstack.registration.dto.SuccessResponseDTO;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.exception.RegistrationExceptionConstants;
import io.github.tf-govstack.registration.service.template.NotificationService;
import io.github.tf-govstack.registration.util.restclient.ServiceDelegateUtil;

/**
 * SMS and Email notification service
 * 
 * @author Dinesh Ashokan
 *
 */
@Service
public class NotificationServiceImpl implements NotificationService {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(NotificationServiceImpl.class);

	/**
	 * Instance of {@code AuditFactory}
	 */
	@Autowired
	private AuditManagerService auditFactory;

	/**
	 * serviceDelegateUtil which processes the HTTPRequestDTO requests
	 */
	@Autowired
	private ServiceDelegateUtil serviceDelegateUtil;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.service.NotificationService#sendSMS(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO sendSMS(String message, String number, String regId) throws RegBaseCheckedException {

		LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, "sendSMS Method called");
		ResponseDTO responseDTO = new ResponseDTO();
		if (mandatoryCheck(message, number)) {
			NotificationDTO smsdto = new NotificationDTO();
			Map<String, String> requestMap = new HashMap<>();
			requestMap.put("message", message);
			requestMap.put("number", number);
			smsdto.setRequest(requestMap);
			smsdto.setRequesttime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
			sendNotification(regId, responseDTO, smsdto, SMS_SERVICE, RegistrationConstants.OTP_VALIDATION_SUCCESS);
		} else {
			LOGGER.error(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,"Message and Number can not be null");
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.NOTIFICATION_MANDATORY_CHECK_SMS_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.NOTIFICATION_MANDATORY_CHECK_SMS_EXCEPTION.getErrorMessage());
		}
		return responseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.service.NotificationService#sendEmail(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public ResponseDTO sendEmail(String message, String emailId, String regId) throws RegBaseCheckedException {

		LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, "sendEmail Method called");
		ResponseDTO responseDTO = new ResponseDTO();
		if (mandatoryCheck(message, emailId)) {
			auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
					SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
			LinkedMultiValueMap<String, Object> emailDetails = new LinkedMultiValueMap<>();
			emailDetails.add("mailTo", emailId);
			emailDetails.add("mailSubject", EMAIL_SUBJECT);
			emailDetails.add("mailContent", message);

			sendNotification(regId, responseDTO, emailDetails, EMAIL_SERVICE,
					RegistrationConstants.OTP_VALIDATION_SUCCESS);
		} else {
			LOGGER.error(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,"Message and Email can not be null");
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.NOTIFICATION_MANDATORY_CHECK_EMAIL_EXCEPTION.getErrorCode(),
					RegistrationExceptionConstants.NOTIFICATION_MANDATORY_CHECK_EMAIL_EXCEPTION.getErrorMessage());
		}
		return responseDTO;
	}

	private boolean mandatoryCheck(String message, String id) {
		return !StringUtils.isEmpty(message) && !StringUtils.isEmpty(id);
	}

	/**
	 * To send notification
	 * @param regId
	 * @param responseDTO
	 * @param object
	 * @param service
	 * @param expectedStatus
	 */
	@SuppressWarnings("unchecked")
	private void sendNotification(String regId, ResponseDTO responseDTO, Object object, String service,
			String expectedStatus) {
		StringBuilder sb;
		try {
			Map<String, List<Map<String, String>>> response = (Map<String, List<Map<String, String>>>) serviceDelegateUtil
					.post(service, object, RegistrationConstants.JOB_TRIGGER_POINT_USER);
			if (response != null && response.get(RegistrationConstants.RESPONSE) != null) {
				Map<String, Object> resMap = (Map<String, Object>) response
						.get(RegistrationConstants.RESPONSE);
				String res = (String) resMap.get(RegistrationConstants.UPLOAD_STATUS);
				if (res.contains(expectedStatus)) {
					LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, resMap.toString());
					auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
							SessionContext.userContext().getUserId(),
							AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
					// creating success response
					SuccessResponseDTO successResponseDTO = new SuccessResponseDTO();
					successResponseDTO.setMessage(RegistrationConstants.OTP_VALIDATION_SUCCESS);
					responseDTO.setSuccessResponseDTO(successResponseDTO);
				}
			} else if (response != null) {
				String errorMessage = Optional.ofNullable(response.get(RegistrationConstants.ERRORS))
						.filter(list -> !list.isEmpty()).flatMap(list -> list.stream()
								.filter(map -> map.containsKey(RegistrationConstants.ERROR_MSG)).findAny())
						.map(map -> map.get(RegistrationConstants.ERROR_MSG)).orElse("");

				ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
				errorResponseDTO.setMessage(errorMessage);
				List<ErrorResponseDTO> errorResponse = new ArrayList<>();
				errorResponse.add(errorResponseDTO);
				responseDTO.setErrorResponseDTOs(errorResponse);

				LOGGER.info(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID, errorMessage);
				auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
						SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());
			}
		} catch (Exception exception) {
			sb = new StringBuilder();
			sb.append("Exception in sending ").append(service.toUpperCase()).append(" Notification - ")
					.append(exception.getMessage());

			LOGGER.error(NOTIFICATION_SERVICE, APPLICATION_NAME, APPLICATION_ID,
					exception.getMessage() + ExceptionUtils.getStackTrace(exception));
			auditFactory.audit(AuditEvent.NOTIFICATION_STATUS, Components.NOTIFICATION_SERVICE,
					SessionContext.userContext().getUserId(), AuditReferenceIdTypes.USER_ID.getReferenceTypeId());

			// creating error response
			ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
			errorResponseDTO.setMessage("Unable to send " + service.toUpperCase() + " Notification");
			List<ErrorResponseDTO> errorResponse = new ArrayList<>();
			errorResponse.add(errorResponseDTO);
			responseDTO.setErrorResponseDTOs(errorResponse);
		}
	}

}
