package io.github.tf-govstack.registration.test.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.tf-govstack.kernel.clientcrypto.service.impl.ClientCryptoFacade;
import io.github.tf-govstack.kernel.clientcrypto.service.spi.ClientCryptoService;
import io.github.tf-govstack.kernel.clientcrypto.util.ClientCryptoUtils;
import io.github.tf-govstack.kernel.core.util.CryptoUtil;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.dao.IdentitySchemaDao;
import io.github.tf-govstack.registration.dao.impl.MasterSyncDaoImpl;
import io.github.tf-govstack.registration.dto.response.SyncDataResponseDto;
import io.github.tf-govstack.registration.dto.schema.SchemaDto;
import io.github.tf-govstack.registration.exception.ConnectionException;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.exception.RegBaseUncheckedException;
import io.github.tf-govstack.registration.repositories.AppAuthenticationRepository;
import io.github.tf-govstack.registration.repositories.AppRolePriorityRepository;
import io.github.tf-govstack.registration.repositories.ApplicantValidDocumentRepository;
import io.github.tf-govstack.registration.repositories.BiometricAttributeRepository;
import io.github.tf-govstack.registration.repositories.BiometricTypeRepository;
import io.github.tf-govstack.registration.repositories.BlocklistedWordsRepository;
import io.github.tf-govstack.registration.repositories.DocumentCategoryRepository;
import io.github.tf-govstack.registration.repositories.DocumentTypeRepository;
import io.github.tf-govstack.registration.repositories.DynamicFieldRepository;
import io.github.tf-govstack.registration.repositories.LanguageRepository;
import io.github.tf-govstack.registration.repositories.LocationHierarchyRepository;
import io.github.tf-govstack.registration.repositories.LocationRepository;
import io.github.tf-govstack.registration.repositories.MachineMasterRepository;
import io.github.tf-govstack.registration.repositories.MachineSpecificationRepository;
import io.github.tf-govstack.registration.repositories.MachineTypeRepository;
import io.github.tf-govstack.registration.repositories.PermittedLocalConfigRepository;
import io.github.tf-govstack.registration.repositories.ProcessListRepository;
import io.github.tf-govstack.registration.repositories.ReasonCategoryRepository;
import io.github.tf-govstack.registration.repositories.ReasonListRepository;
import io.github.tf-govstack.registration.repositories.RegistrationCenterRepository;
import io.github.tf-govstack.registration.repositories.RegistrationCenterTypeRepository;
import io.github.tf-govstack.registration.repositories.ScreenAuthorizationRepository;
import io.github.tf-govstack.registration.repositories.ScreenDetailRepository;
import io.github.tf-govstack.registration.repositories.SyncJobControlRepository;
import io.github.tf-govstack.registration.repositories.SyncJobDefRepository;
import io.github.tf-govstack.registration.repositories.TemplateRepository;
import io.github.tf-govstack.registration.repositories.UserMachineMappingRepository;
import io.github.tf-govstack.registration.service.config.LocalConfigService;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.github.tf-govstack.registration.util.mastersync.ClientSettingSyncHelper;
import io.github.tf-govstack.registration.util.mastersync.MetaDataUtils;
import io.github.tf-govstack.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*" })
@PrepareForTest({ MetaDataUtils.class, RegBaseUncheckedException.class, SessionContext.class, MasterSyncDaoImpl.class,
		BiometricAttributeRepository.class, RegistrationAppHealthCheckUtil.class, Paths.class, CryptoUtil.class, FileUtils.class, ClientCryptoUtils.class })
public class ClientSettingsHelperTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private SyncJobControlRepository syncStatusRepository;

	@Mock
	private BiometricAttributeRepository biometricAttributeRepository;

	@Mock
	private BiometricTypeRepository masterSyncBiometricTypeRepository;

	@Mock
	private BlocklistedWordsRepository masterSyncBlocklistedWordsRepository;

	@Mock
	private DocumentCategoryRepository masterSyncDocumentCategoryRepository;

	@Mock
	private DocumentTypeRepository masterSyncDocumentTypeRepository;


	@Mock
	private LanguageRepository masterSyncLanguageRepository;

	@Mock
	private LocationRepository masterSyncLocationRepository;

	@Mock
	private LocationHierarchyRepository locationHierarchyRepository;


	@Mock
	private MachineMasterRepository masterSyncMachineRepository;

	@Mock
	private MachineSpecificationRepository masterSyncMachineSpecificationRepository;

	@Mock
	private MachineTypeRepository masterSyncMachineTypeRepository;

	@Mock
	private ReasonCategoryRepository reasonCategoryRepository;

	@Mock
	private ReasonListRepository masterSyncReasonListRepository;

	@Mock
	private RegistrationCenterRepository masterSyncRegistrationCenterRepository;

	@Mock
	private RegistrationCenterTypeRepository masterSyncRegistrationCenterTypeRepository;

	@Mock
	private TemplateRepository masterSyncTemplateRepository;

	@Mock
	private ApplicantValidDocumentRepository masterSyncValidDocumentRepository;

	@Mock
	private AppAuthenticationRepository appAuthenticationRepository;

	@Mock
	private AppRolePriorityRepository appRolePriorityRepository;

	@Mock
	private ScreenAuthorizationRepository screenAuthorizationRepository;

	@Mock
	private ProcessListRepository processListRepository;

	@Mock
	private UserMachineMappingRepository userMachineMappingRepository;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;

	@Mock
	private RegistrationCenterTypeRepository registrationCenterTypeRepository;

	@Mock
	private ScreenDetailRepository screenDetailRepository;

	@Mock
	private SyncJobDefRepository syncJobDefRepository;

	@InjectMocks
	private ClientSettingSyncHelper clientSettingSyncHelper;

	@Mock
	private ClientCryptoFacade clientCryptoFacade;

	@Mock
	private ClientCryptoService clientCryptoService;

	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@Mock
	private IdentitySchemaDao identitySchemaDao;

	@Mock
	private LocalConfigService localConfigService;

	@Mock
	private PermittedLocalConfigRepository permittedLocalConfigRepository;
	
	@Mock
	private DynamicFieldRepository dynamicFieldRepository;
	
	@Mock
	private Path pMock;
	
	@Mock
	private File file;

	@Test(expected = RegBaseUncheckedException.class)
	public void testSingleEntity() {
		String response = null;
		SyncDataResponseDto syncDataResponseDto = getSyncDataResponseDto("biometricJson.json");
		response = clientSettingSyncHelper.saveClientSettings(syncDataResponseDto);
		assertEquals(RegistrationConstants.SUCCESS, response);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = RegBaseUncheckedException.class)
	public void testEmptyJsonRegBaseUncheckedException() {
		String response = null;
		SyncDataResponseDto syncDataResponseDto = getSyncDataResponseDto("emptyJson.json");
		clientSettingSyncHelper.saveClientSettings(syncDataResponseDto);
	}

	@Test
	public void testClientSettingsSyncForValidJson() throws RegBaseCheckedException, ConnectionException, IOException, io.github.tf-govstack.kernel.core.exception.IOException {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(Paths.class);
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.mockStatic(CryptoUtil.class);
		PowerMockito.mockStatic(ClientCryptoUtils.class);
		Mockito.when(Paths.get(Mockito.anyString(), Mockito.anyString())).thenReturn(pMock);
		Mockito.when(pMock.toFile()).thenReturn(file);
		
		String data = "[{\n" + 
				"	\"headers\": \"test-headers\",\n" + 
				"	\"auth-required\": true,\n" + 
				"	\"auth-token\": \"test-token\",\n" + 
				"	\"encrypted\": true\n" + 
				"}]";
		
		Mockito.when(clientCryptoFacade.decrypt(Mockito.any())).thenReturn(data.getBytes(StandardCharsets.UTF_8));
		
		String jsonObjData = "{\n" + 
				"  	\"url\": \"https://dev.mosip.net\",\n" + 
				"	\"headers\": \"test-headers\",\n" + 
				"	\"auth-required\": true,\n" + 
				"	\"auth-token\": \"test-token\",\n" + 
				"	\"encrypted\": false\n" + 
				"}";
		byte[] bytes = "test".getBytes();
		Mockito.when(ClientCryptoUtils.decodeBase64Data("BVlY")).thenReturn(bytes);
		Mockito.when(clientCryptoFacade.decrypt(bytes)).thenReturn(jsonObjData.getBytes(StandardCharsets.UTF_8));
		
		Mockito.when(clientCryptoFacade.getClientSecurity()).thenReturn(clientCryptoService);
		Mockito.when(CryptoUtil.computeFingerPrint(Mockito.any(byte[].class), Mockito.anyString())).thenReturn("test");
		Mockito.doNothing().when(serviceDelegateUtil).download(Mockito.anyString(), Mockito.anyMap(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.anyBoolean());
		
		Mockito.when(FileUtils.readFileToString(Mockito.any(File.class), Mockito.any(Charset.class))).thenReturn("");
		
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);

		Map<String, Object> map = new LinkedHashMap<>();
		SchemaDto schemaDto = new SchemaDto();
		schemaDto.setSchemaJson(""); 
		map.put("response", schemaDto);
		Mockito.when(serviceDelegateUtil.get(Mockito.anyString(),Mockito.anyMap(), Mockito.anyBoolean(),
				Mockito.anyString())).thenReturn(map);

		String response = null;
		SyncDataResponseDto syncDataResponseDto = getSyncDataResponseDto("responseJson.json");
		response = clientSettingSyncHelper.saveClientSettings(syncDataResponseDto);
		assertEquals(RegistrationConstants.SUCCESS, response);
	}

	private SyncDataResponseDto getSyncDataResponseDto(String fileName) {

		ObjectMapper mapper = new ObjectMapper();
		SyncDataResponseDto syncDataResponseDto = null;

		try {
			syncDataResponseDto = mapper.readValue(
					new File(getClass().getClassLoader().getResource(fileName).getFile()), SyncDataResponseDto.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return syncDataResponseDto;
	}

}
