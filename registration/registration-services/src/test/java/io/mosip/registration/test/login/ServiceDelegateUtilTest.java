package io.github.tf-govstack.registration.test.login;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
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
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;

import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.dto.LoginUserDTO;
import io.github.tf-govstack.registration.dto.OtpGeneratorRequestDTO;
import io.github.tf-govstack.registration.dto.ResponseDTO;
import io.github.tf-govstack.registration.exception.ConnectionException;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.update.SoftwareUpdateHandler;
import io.github.tf-govstack.registration.util.restclient.RequestHTTPDTO;
import io.github.tf-govstack.registration.util.restclient.RestClientUtil;
import io.github.tf-govstack.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ ApplicationContext.class, SessionContext.class })
public class ServiceDelegateUtilTest {
	@Mock
	private RestClientUtil restClientUtil;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private ServiceDelegateUtil delegateUtil;

	@Mock
	private Environment environment;
	
	@Mock
	private RequestHTTPDTO requestHTTPDTO;
	
	@Mock
	private Path pMock;
	
	@Mock
	private SoftwareUpdateHandler softwareUpdateHandler;
	
	@Before
	public void initialize() throws IOException, URISyntaxException {

		LoginUserDTO loginDto = new LoginUserDTO();
		loginDto.setUserId("super_admin");
		loginDto.setPassword("super_admin");
		loginDto.setOtp("123456");

		PowerMockito.mockStatic(ApplicationContext.class);
		Map<String, Object> globalParams = new HashMap<>();
		globalParams.put(RegistrationConstants.USER_DTO, loginDto);
		globalParams.put(RegistrationConstants.REGISTRATION_CLIENT, "registrationclient");
		globalParams.put(RegistrationConstants.HTTP_API_READ_TIMEOUT, "60000");
		globalParams.put(RegistrationConstants.HTTP_API_WRITE_TIMEOUT, "60000");
		globalParams.put(RegistrationConstants.MOSIP_HOSTNAME, "test.mosip.net");
		PowerMockito.when(ApplicationContext.map()).thenReturn(globalParams);
		PowerMockito.when(ApplicationContext.getStringValueFromApplicationMap(Mockito.anyString())).thenReturn("test");
		
		Mockito.when(softwareUpdateHandler.getCurrentVersion()).thenReturn("1.2.0.1");
	}

	/*
	 * @Test public void getURITest() {
	 * 
	 * Map<String, String> requestParamMap = new HashMap<String, String>();
	 * requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
	 * requestParamMap.put(RegistrationConstants.OTP_GENERATED, "099887");
	 * Assert.assertEquals(delegateUtil.getUri(requestParamMap,
	 * "http://localhost:8080/otpmanager/otps").toString(),
	 * "http://localhost:8080/otpmanager/otps?otp=099887&key=yashReddy"); }
	 */

	@Test
	public void getRequestTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, false,"System"));
	}

	 @Test
	public void postRequestTest() throws URISyntaxException, RegBaseCheckedException, ConnectionException {

		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_generator.service.httpmethod")).thenReturn("POST");
		when(environment.getProperty("otp_generator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_generator.service.requestType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpGeneratorResponseDTO");
		when(environment.getProperty("otp_generator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_generator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_generator.service.authheader")).thenReturn("Authorization:BASIC");
		Map<String,Object> responseMap=new HashMap<>();
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);		
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		OtpGeneratorRequestDTO generatorRequestDto = new OtpGeneratorRequestDTO();
		generatorRequestDto.setKey("yashReddy");
		assertNotNull(delegateUtil.post("otp_generator", generatorRequestDto,"System"));
	}
	
	@Test
	public void getRequestTestTrue() throws Exception {

		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("true");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
		Map<String,Object> responseMap=new HashMap<>();
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, false,"System"));
	}
	@Test
	public void getRequestFailureTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("true");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
		Map<String,Object> responseMap=new HashMap<>();
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		assertNotNull(delegateUtil.get("otp_validator", new HashMap<>(), false,"System"));
	}
	
	@Test
	public void postRequest() throws URISyntaxException, RegBaseCheckedException, ConnectionException {

		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_generator.service.httpmethod")).thenReturn("POST");
		when(environment.getProperty("otp_generator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_generator.service.requestType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpGeneratorResponseDTO");
		when(environment.getProperty("otp_generator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_generator.service.authrequired")).thenReturn("true");
		when(environment.getProperty("otp_generator.service.authheader")).thenReturn("Authorization:oauth");
		Map<String,Object> responseMap=new HashMap<>();
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);		
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		OtpGeneratorRequestDTO generatorRequestDto = new OtpGeneratorRequestDTO();
		generatorRequestDto.setKey("yashReddy");
		assertNotNull(delegateUtil.post("otp_generator", generatorRequestDto,"System"));
	}	
	
	@Test
	public void prepareURLByHostNameTest() throws Exception {
			assertNull(delegateUtil.prepareURLByHostName(null));
	}
	
	@Test
	public void isNetworkAvailableTest() throws Exception {
			delegateUtil.isNetworkAvailable();
			assertEquals(Boolean.FALSE,delegateUtil.isNetworkAvailable());
	}
	
	@Test
	public void isNetworkAvailableExceptoinTest() throws Exception {
		Mockito.when(ApplicationContext.getStringValueFromApplicationMap(Mockito.anyString())).thenReturn(null);
		assertEquals(Boolean.FALSE, delegateUtil.isNetworkAvailable());
	}
	
	@Test
	public void getRequestQueryParamsTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		assertNotNull(delegateUtil.get("otp_validator", requestParamMap, true,"System"));
	}
	
	@Test
	public void downLoadHederDataAsNullTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		delegateUtil.download("http://localhost:8080/otpmanager/otps", requestParamMap,null, Boolean.TRUE, "authHeader","triggerPoint", pMock, Boolean.TRUE);		
	}
	
	
	@Test
	public void downLoadTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		delegateUtil.download("http://localhost:8080/otpmanager/otps", requestParamMap,"Content-Type:APPLICATION/JSON", Boolean.TRUE, "authHeader","triggerPoint", pMock, Boolean.TRUE);
	}	
	
	@Test
	public void downLoadSignatureHeaderTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		delegateUtil.download("http://localhost:8080/otpmanager/otps", requestParamMap,"Content-Type:APPLICATION/JSON,authorization:auth,signature:sign", Boolean.TRUE, "authHeader","triggerPoint", pMock, Boolean.TRUE);
	}

	@Test
	public void downLoadTimeStampHeaderTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		delegateUtil.download("http://localhost:8080/otpmanager/otps", requestParamMap,"Content-Type:APPLICATION/JSON,timestamp:timestamp,Center-Machine-RefId:centerId", Boolean.TRUE, "authHeader","triggerPoint", pMock, Boolean.TRUE);
	}
	
	@Test(expected=RegBaseCheckedException.class)
	public void downLoadRestClientExceptionTest() throws Exception {
		ResponseDTO response = new ResponseDTO();
		when(environment.getProperty("otp_validator.service.httpmethod")).thenReturn("GET");
		when(environment.getProperty("otp_validator.service.url")).thenReturn("http://localhost:8080/otpmanager/otps");
		when(environment.getProperty("otp_validator.service.responseType"))
				.thenReturn("io.github.tf-govstack.registration.dto.OtpValidatorResponseDTO");
		when(environment.getProperty("otp_validator.service.headers")).thenReturn("Content-Type:APPLICATION/JSON");
		when(environment.getProperty("otp_validator.service.authrequired")).thenReturn("false");
		when(environment.getProperty("otp_validator.service.authheader")).thenReturn("Authorization:BASIC");
        Map<String,Object> responseMap=new HashMap<>();
		when(restClientUtil.invokeURL(Mockito.any())).thenReturn(responseMap);
		Map<String, String> requestParamMap = new HashMap<String, String>();
		requestParamMap.put(RegistrationConstants.USERNAME_KEY, "yashReddy");
		requestParamMap.put(RegistrationConstants.OTP, "099886");
		HttpHeaders header=new HttpHeaders();
		header.add("authorization", "Mosip-TokeneyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdXBlcl9hZG1pbiIsIm1vYmlsZSI6Ijc1ODU2NzUzNjQiLCJtYWlsIjoic3VwZXJfYWRtaW5AbW9zaXAuaW8iLCJyb2xlIjoiU1VQRVJBRE1JTiIsImlhdCI6MTU0ODkxODQ5NywiZXhwIjoxNTQ4OTI0NDk3fQ.illxy8uqsiCVfi7bkZQWMbBOCR1ly3XjuwLMDH12GJNvg2prdWWl4_Fv52Flar32qFXZY6Bir144hCrVrUi-VQ");
		responseMap.put("responseHeader", header);
		responseMap.put("responseBody", response);
		Mockito.when(restClientUtil.invokeURL((Mockito.anyObject()))).thenReturn(responseMap);
		delegateUtil.download("http://localhost:8080/otpmanager/otps", requestParamMap,"timestamp",Boolean.TRUE, "authHeader","triggerPoint", pMock, Boolean.TRUE);
	}
}
