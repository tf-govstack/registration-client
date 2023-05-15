package io.github.tf-govstack.registration.service.operator.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.github.tf-govstack.registration.exception.ConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.kernel.core.util.DateUtils;
import io.github.tf-govstack.kernel.core.util.JsonUtils;
import io.github.tf-govstack.kernel.core.util.exception.JsonProcessingException;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.dao.MachineMappingDAO;
import io.github.tf-govstack.registration.dto.ErrorResponseDTO;
import io.github.tf-govstack.registration.dto.RegCenterMachineUserReqDto;
import io.github.tf-govstack.registration.dto.RegistrationCenterUserMachineMappingDto;
import io.github.tf-govstack.registration.dto.ResponseDTO;
import io.github.tf-govstack.registration.entity.UserMachineMapping;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.exception.RegBaseUncheckedException;
import io.github.tf-govstack.registration.service.BaseService;
import io.github.tf-govstack.registration.service.operator.UserMachineMappingService;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * Implementation for {@link UserMachineMappingService}
 * 
 * @author Brahmananda Reddy
 *
 */
@Service
public class UserMachineMappingServiceImpl extends BaseService implements UserMachineMappingService {


	@Autowired
	private MachineMappingDAO machineMappingDAO;

	private static final Logger LOGGER = AppConfig.getLogger(UserMachineMappingServiceImpl.class);

	/**
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.service.operator.UserMachineMappingService#syncUserDetails()
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ResponseDTO syncUserDetails() {
		LOGGER.info("REGISTRATION-CENTER-USER-MACHINE-MAPPING-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
				"sync user details is started");
		
		String stationId = null;
		String centerId = null;
		List<UserMachineMapping> userMachineMappingList = null;
		List<Map<String, Object>> list = new ArrayList<>();
		ResponseDTO responseDTO = new ResponseDTO();

		if (!serviceDelegateUtil.isNetworkAvailable()) {
			buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_CODE,
					RegistrationConstants.POLICY_SYNC_CLIENT_NOT_ONLINE_ERROR_MESSAGE);
		} else {

			try {				
				stationId = getStationId();
				centerId = getCenterId();
				userMachineMappingList = machineMappingDAO.getUserMappingDetails(stationId);
				Map<String, Object> requestMap = new LinkedHashMap<>();
				requestMap.put(RegistrationConstants.ID, RegistrationConstants.APPLICATION_NAME);
				requestMap.put(RegistrationConstants.REQ_TIME, DateUtils.getUTCCurrentDateTimeString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
				requestMap.put("metadata", new HashMap<>());
				RegCenterMachineUserReqDto<RegistrationCenterUserMachineMappingDto> regCenterMachineUserReqDto = new RegCenterMachineUserReqDto<>();
				regCenterMachineUserReqDto.setId("REGISTRATION");
				regCenterMachineUserReqDto.setRequesttime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
				for (UserMachineMapping userMachineMapping : userMachineMappingList) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("cntrId", centerId);
					userMap.put("machineId", stationId);
					userMap.put("isActive", true);
					userMap.put("langCode", ApplicationContext.applicationLanguage());
					userMap.put("usrId", userMachineMapping.getUserDetail().getId());
					list.add(userMap);
				}
				requestMap.put("request", list);

				@SuppressWarnings("unchecked")
				LinkedHashMap<String, Object> userMachineMappingSyncResponse = (LinkedHashMap<String, Object>) serviceDelegateUtil
						.post("user_machine_mapping", requestMap, RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM);
				if (null != userMachineMappingSyncResponse.get(RegistrationConstants.RESPONSE)) {
					LOGGER.info("REGISTRATION-CENTER-USER-MACHINE-MAPPING-DETAILS- SYNC", APPLICATION_NAME,
							APPLICATION_ID, RegistrationConstants.SUCCESS);
					setSuccessResponse(responseDTO, RegistrationConstants.SUCCESS, null);
				} else {
					LOGGER.info("REGISTRATION-CENTER-USER-MACHINE-MAPPING-DETAILS- SYNC", APPLICATION_NAME,
							APPLICATION_ID,
							userMachineMappingSyncResponse.size() > 0
									? ((List<LinkedHashMap<String, String>>) userMachineMappingSyncResponse
											.get(RegistrationConstants.ERRORS)).get(0)
													.get(RegistrationConstants.ERROR_MSG)
									: "sync user details Restful service error");
					setErrorResponse(responseDTO, userMachineMappingSyncResponse.size() > 0
							? ((List<LinkedHashMap<String, String>>) userMachineMappingSyncResponse
									.get(RegistrationConstants.ERRORS)).get(0).get(RegistrationConstants.ERROR_MSG)
							: "sync user details Restful service error", null);
				}

				LOGGER.info("REGISTRATION-ONBOARDED-USER-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
						"sync user details is ended");
			} catch (ConnectionException | RegBaseCheckedException | RegBaseUncheckedException exception) {
				LOGGER.error("REGISTRATION-CENTER-USER-MACHINE-MAPPING-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
						exception.getMessage());
				responseDTO = buildErrorRespone(responseDTO, RegistrationConstants.POLICY_SYNC_ERROR_CODE,
						RegistrationConstants.POLICY_SYNC_ERROR_MESSAGE);

			}
		}

		return responseDTO;
	}

	private ResponseDTO buildErrorRespone(ResponseDTO response, final String errorCode, final String message) {
		/* Create list of Error Response */
		LinkedList<ErrorResponseDTO> errorResponses = new LinkedList<>();

		/* Error response */
		ErrorResponseDTO errorResponse = new ErrorResponseDTO();
		errorResponse.setCode(errorCode);
		errorResponse.setInfoType(RegistrationConstants.ERROR);
		errorResponse.setMessage(message);
		errorResponses.add(errorResponse);

		/* Adding list of error responses to response */
		response.setErrorResponseDTOs(errorResponses);

		return response;
	}

	@Override
	public ResponseDTO isUserNewToMachine(String userId) {
		LOGGER.info("REGISTRATION-CENTER-USER-MACHINE-MAPPING-DETAILS- SYNC", APPLICATION_NAME, APPLICATION_ID,
				"Started to find whether the user new to machine or not");
		ResponseDTO responseDTO = new ResponseDTO();
		boolean isExists = machineMappingDAO.isExists(userId);

		if (isExists) {
			setSuccessResponse(responseDTO, null, null);
		} else {
			setErrorResponse(responseDTO, null, null);
		}
		return responseDTO;

	}

}
