package io.github.tf-govstack.registration.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.tf-govstack.kernel.clientcrypto.service.impl.ClientCryptoFacade;
import io.github.tf-govstack.kernel.clientcrypto.service.spi.ClientCryptoService;
import io.github.tf-govstack.kernel.clientcrypto.util.ClientCryptoUtils;
import io.github.tf-govstack.kernel.core.util.CryptoUtil;
import io.github.tf-govstack.registration.audit.AuditManagerSerivceImpl;
import io.github.tf-govstack.registration.constants.AuditEvent;
import io.github.tf-govstack.registration.constants.Components;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.dao.UserOnboardDAO;
import io.github.tf-govstack.registration.dao.impl.GlobalParamDAOImpl;
import io.github.tf-govstack.registration.dto.ResponseDTO;
import io.github.tf-govstack.registration.entity.GlobalParam;
import io.github.tf-govstack.registration.entity.id.GlobalParamId;
import io.github.tf-govstack.registration.exception.ConnectionException;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.service.config.impl.GlobalParamServiceImpl;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationSystemPropertiesChecker;
import io.github.tf-govstack.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ RegistrationAppHealthCheckUtil.class, CryptoUtil.class, ClientCryptoUtils.class })
public class GlobalParamServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private AuditManagerSerivceImpl auditFactory;

	@InjectMocks
	private GlobalParamServiceImpl gloablContextParamServiceImpl;

	@Mock
	private GlobalParamDAOImpl globalParamDAOImpl;

	@Mock
	RegistrationAppHealthCheckUtil registrationAppHealthCheckUtil;

	@Mock
	RegistrationSystemPropertiesChecker registrationSystemPropertiesChecker;

	@Mock
	UserOnboardDAO onboardDAO;

	@Mock
	ServiceDelegateUtil serviceDelegateUtil;
	
	@Mock
	private ClientCryptoFacade clientCryptoFacade;
	
	@Mock
	private ClientCryptoService clientCryptoService;
	
	@Before
	public void initialize() {
		PowerMockito.mockStatic(CryptoUtil.class);
		Mockito.when(clientCryptoFacade.getClientSecurity()).thenReturn(clientCryptoService);
		Mockito.when(clientCryptoService.getEncryptionPublicPart()).thenReturn("test".getBytes());
	}

	@Test
	public void getGlobalParamsTest() {
		doNothing().when(auditFactory).audit(Mockito.any(AuditEvent.class), Mockito.any(Components.class),
				Mockito.anyString(), Mockito.anyString());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		assertEquals(globalParamMap, gloablContextParamServiceImpl.getGlobalParams());
	}

	@Test
	public void syncConfigDataTestError() throws RegBaseCheckedException, ConnectionException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);
		
		HashMap<String, Object> globalParamJsonMap = new HashMap<>();
		globalParamJsonMap.put("retryAttempts", "3");
		globalParamJsonMap.put("kernel", "5");
		HashMap<String, Object> globalParamJsonMap2 = new HashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");
		globalParamJsonMap.put("response", globalParamJsonMap2);

		globalParamJsonMap.put("map", globalParamJsonMap2);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("2");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("retryAttempts");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);
		gloablContextParamServiceImpl.synchConfigData(false);
	}

	@Test
	public void syncConfigData() throws RegBaseCheckedException, ConnectionException, JsonProcessingException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class, ClientCryptoUtils.class);
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);
		Mockito.when(ClientCryptoUtils.decodeBase64Data(Mockito.anyString())).thenReturn("test".getBytes());
		
		Map<String, Object> paramMap = new HashMap<>();
		Map<String, Object> nestedMap = new HashMap<>();
		nestedMap.put("mosip.ida.ref-id", "INTERNAL");
		paramMap.put("mosip.registration.iris_threshold", "60");
		paramMap.put("mosip.kernel.transliteration.arabic-language-code", "ara");
		paramMap.put("mosip.registration.document_enable_flag", "y");
		paramMap.put("mosip.registration.otp_channels", "email");
		paramMap.put("mosip.registration.packet_upload_batch_size", "5");
		paramMap.put("mosip.kernel.machineid.length", "5");
		paramMap.put("mosip.ida.ref-id", nestedMap);
		paramMap.put("Retry", 3);
		
		Mockito.when(clientCryptoFacade.decrypt(Mockito.any(byte[].class))).thenReturn(new ObjectMapper().writeValueAsBytes(paramMap));
		
		HashMap<String, Object> globalParamJsonMap = new LinkedHashMap<>();
		HashMap<String, Object> globalParamJsonMap1 = new LinkedHashMap<>();
		globalParamJsonMap1.put("globalConfiguration", "testEncodedCipher");
		globalParamJsonMap1.put("registrationConfiguration", "testEncodedCipher");	
		HashMap<String, Object> globalParamJsonMap2 = new LinkedHashMap<>();
		globalParamJsonMap2.put("configDetail", globalParamJsonMap1);
		globalParamJsonMap.put("response", globalParamJsonMap2);
		
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("retryAttempts", "2");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("2");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("Retry");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		
		GlobalParam globalParam2 = new GlobalParam();
		globalParam2.setVal("5");
		GlobalParamId globalParamId2 = new GlobalParamId();
		globalParamId2.setCode("mosip.kernel.machineid.length");
		globalParam2.setIsActive(false);
		globalParam2.setGlobalParamId(globalParamId2);
		globalParamList.add(globalParam2);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);

		assertNotNull(gloablContextParamServiceImpl.synchConfigData(false).getSuccessResponseDTO());
	}
	
	@Test
	public void syncConfigDataInternetFailureTest() {
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(false);
		assertNotNull(gloablContextParamServiceImpl.synchConfigData(true).getErrorResponseDTOs());
	}
	
	@Test
	public void syncConfigDataFailureTest() throws RegBaseCheckedException, ConnectionException {
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);
		HashMap<String, Object> globalParamJsonMap = new LinkedHashMap<>();
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		assertNotNull(gloablContextParamServiceImpl.synchConfigData(false).getErrorResponseDTOs());
	}

	@Test
	public void syncConfigDataExceptionTest() throws RegBaseCheckedException, ConnectionException {
		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenThrow(HttpClientErrorException.class);

		gloablContextParamServiceImpl.synchConfigData(false);
	}

	@Test
	public void syncConfigTest() throws RegBaseCheckedException, ConnectionException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);
		HashMap<String, Object> globalParamJsonMap = new HashMap<>();
		globalParamJsonMap.put("retryAttempts", "3");
		globalParamJsonMap.put("kernel", "5");
		HashMap<String, Object> globalParamJsonMap2 = new HashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");

		globalParamJsonMap.put("map", globalParamJsonMap2);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("ANY", "ANY");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("ANY");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("ANY");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);

		gloablContextParamServiceImpl.synchConfigData(false);
	}

	@Test
	public void updateSoftwareUpdateStatusSuccessCaseTest() {
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");

		GlobalParam globalParam = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("Y");

		Mockito.when(globalParamDAOImpl.updateSoftwareUpdateStatus(Mockito.anyBoolean(), Mockito.any(Timestamp.class)))
				.thenReturn(globalParam);
		ResponseDTO responseDTO = gloablContextParamServiceImpl.updateSoftwareUpdateStatus(true,
				Timestamp.from(Instant.now()));
		assertEquals(responseDTO.getSuccessResponseDTO().getMessage(),
				RegistrationConstants.SOFTWARE_UPDATE_SUCCESS_MSG);
	}

	@Test
	public void updateSoftwareUpdateStatusFailureCaseTest() {
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParamId.setLangCode("eng");

		GlobalParam globalParam = new GlobalParam();
		globalParam.setName(RegistrationConstants.IS_SOFTWARE_UPDATE_AVAILABLE);
		globalParam.setGlobalParamId(globalParamId);
		globalParam.setVal("N");

		Mockito.when(globalParamDAOImpl.updateSoftwareUpdateStatus(Mockito.anyBoolean(), Mockito.any(Timestamp.class)))
				.thenReturn(globalParam);
		ResponseDTO responseDTO = gloablContextParamServiceImpl.updateSoftwareUpdateStatus(false,
				Timestamp.from(Instant.now()));
		assertEquals(responseDTO.getSuccessResponseDTO().getMessage(),
				RegistrationConstants.SOFTWARE_UPDATE_FAILURE_MSG);
	}

	@Test
	public void updatetest() {
		GlobalParam globalParam = new GlobalParam();
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode(RegistrationConstants.INITIAL_SETUP);
		globalParamId.setLangCode("en");
		globalParam.setGlobalParamId(globalParamId);
		Mockito.when(globalParamDAOImpl.update(globalParam)).thenReturn(globalParam);

		gloablContextParamServiceImpl.update(RegistrationConstants.INITIAL_SETUP, RegistrationConstants.DISABLE);
	}
	
	@Test
	public void updateNulltest() {		
		gloablContextParamServiceImpl.update(null,null);
	}

	@Test
	public void syncConfigDataUpdate() throws RegBaseCheckedException, ConnectionException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);
//		doNothing().when(pageFlow).getInitialPageDetails();
		HashMap<String, Object> globalParamJsonMap = new LinkedHashMap<>();

		globalParamJsonMap.put("kernel", "5");
		HashMap<String, Object> globalParamJsonMap2 = new LinkedHashMap<>();
		globalParamJsonMap2.put("loginSequence1", "OTP");
		globalParamJsonMap.put("response", globalParamJsonMap2);

		globalParamJsonMap.put("map", globalParamJsonMap2);

		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(), Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(globalParamJsonMap);
		Mockito.doNothing().when(globalParamDAOImpl).saveAll(Mockito.anyList());

		Map<String, Object> globalParamMap = new LinkedHashMap<>();
		globalParamMap.put("retryAttempts", "2");
		Mockito.when(globalParamDAOImpl.getGlobalParams()).thenReturn(globalParamMap);
		java.util.List<GlobalParam> globalParamList = new ArrayList<>();
		GlobalParam globalParam = new GlobalParam();
		globalParam.setVal("2");
		GlobalParamId globalParamId = new GlobalParamId();
		globalParamId.setCode("retryAttempts");
		globalParam.setGlobalParamId(globalParamId);
		globalParamList.add(globalParam);
		Mockito.when(globalParamDAOImpl.getAllEntries()).thenReturn(globalParamList);

		gloablContextParamServiceImpl.synchConfigData(false);
	}

}
