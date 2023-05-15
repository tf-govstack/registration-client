package io.github.tf-govstack.registration.test.template;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
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

import io.github.tf-govstack.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.github.tf-govstack.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.github.tf-govstack.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.context.SessionContext.UserContext;
import io.github.tf-govstack.registration.dto.RegistrationCenterDetailDTO;
import io.github.tf-govstack.registration.dto.RegistrationDTO;
import io.github.tf-govstack.registration.dto.ResponseDTO;
import io.github.tf-govstack.registration.dto.biometric.FingerprintDetailsDTO;
import io.github.tf-govstack.registration.entity.SyncControl;
import io.github.tf-govstack.registration.entity.SyncJobDef;
import io.github.tf-govstack.registration.entity.UserDetail;
import io.github.tf-govstack.registration.exception.RegBaseCheckedException;
import io.github.tf-govstack.registration.service.IdentitySchemaService;
import io.github.tf-govstack.registration.service.config.JobConfigurationService;
import io.github.tf-govstack.registration.service.operator.UserDetailService;
import io.github.tf-govstack.registration.service.operator.UserMachineMappingService;
import io.github.tf-govstack.registration.service.packet.PacketHandlerService;
import io.github.tf-govstack.registration.service.packet.RegistrationApprovalService;
import io.github.tf-govstack.registration.service.sync.MasterSyncService;
import io.github.tf-govstack.registration.service.sync.PacketSynchService;
import io.github.tf-govstack.registration.test.util.datastub.DataProvider;
import io.github.tf-govstack.registration.update.SoftwareUpdateHandler;
import io.github.tf-govstack.registration.util.acktemplate.TemplateGenerator;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ ImageIO.class, ApplicationContext.class, SessionContext.class, IOUtils.class })
public class TemplateGeneratorTest {
	TemplateManagerBuilderImpl template = new TemplateManagerBuilderImpl();

	@InjectMocks
	TemplateGenerator templateGenerator;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	QrCodeGenerator<QrVersion> qrCodeGenerator;

	@Mock
	private IdentitySchemaService identitySchemaService;

	@Mock
	private ApplicationContext applicationContext;
	
	@Mock
	private PacketHandlerService packetHandlerServiceImpl;
	
	@Mock
	private RegistrationApprovalService registrationApprovalService;
	
	@Mock
	private PacketSynchService packetSynchService;
	
	@Mock
	private UserMachineMappingService userMachineMappingService;
	
	@Mock
	private MasterSyncService masterSyncServiceImpl;
	
	@Mock
	private JobConfigurationService jobConfigurationService;
	
	@Mock
	private SoftwareUpdateHandler softwareUpdateHandler;
	
	@Mock
	private UserDetailService userDetailService;
	
	Map<String,Object> appMap = new HashMap<>();
	
	private RegistrationDTO registrationDTO;
	
	@Before
	public void initialize() throws Exception {
		registrationDTO = DataProvider.getPacketDTO();
		List<FingerprintDetailsDTO> segmentedFingerprints = new ArrayList<>();
		segmentedFingerprints.add(new FingerprintDetailsDTO());

		appMap.put(RegistrationConstants.DOC_DISABLE_FLAG, "Y");
		appMap.put(RegistrationConstants.PRIMARY_LANGUAGE, "ara");
		appMap.put(RegistrationConstants.SECONDARY_LANGUAGE, "fra");

		when(qrCodeGenerator.generateQrCode(Mockito.anyString(), Mockito.any())).thenReturn(new byte[1024]);
		BufferedImage image = null;
		PowerMockito.mockStatic(ImageIO.class);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_EYE_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_LEFT_SLAP_IMAGE_PATH)))
						.thenReturn(image);
		when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_RIGHT_SLAP_IMAGE_PATH)))
						.thenReturn(image);
		/*when(ImageIO.read(
				templateGenerator.getClass().getResourceAsStream(RegistrationConstants.TEMPLATE_THUMBS_IMAGE_PATH)))
						.thenReturn(image);
		*/
		UserContext userContext = Mockito.mock(SessionContext.UserContext.class);
		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.doReturn(userContext).when(SessionContext.class, "userContext");
		RegistrationCenterDetailDTO centerDetailDTO = new RegistrationCenterDetailDTO();
		centerDetailDTO.setRegistrationCenterId("mosip");
		PowerMockito.when(userContext.getRegistrationCenterDetailDTO()).thenReturn(centerDetailDTO);

		Map<String,Object> map = new LinkedHashMap<>();
		map.put(RegistrationConstants.IS_Child, false);
		map.put(RegistrationConstants.REGISTRATION_DATA, registrationDTO);
		PowerMockito.when(SessionContext.map()).thenReturn(map);

		PowerMockito.mockStatic(ApplicationContext.class);
		ApplicationContext.setApplicationMap(appMap);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
		PowerMockito.doReturn(applicationContext).when(ApplicationContext.class, "getInstance");
		Mockito.when(ApplicationContext.applicationLanguage()).thenReturn("eng");
		Mockito.when(ApplicationContext.getBundle(Mockito.anyString(), Mockito.anyString())).thenReturn(dummyResourceBundle);

		Mockito.when(identitySchemaService.getAllFieldSpec(Mockito.anyString(),Mockito.anyDouble())).thenReturn(Collections.EMPTY_LIST);
	}
	
	ResourceBundle dummyResourceBundle = new ResourceBundle() {
		@Override
		protected Object handleGetObject(String key) {
			return "fake_translated_value";
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.emptyEnumeration();
		}
	};

	@Test
	public void generateTemplateWithEmptyFieldsTest() throws RegBaseCheckedException {
		ResponseDTO response = templateGenerator.generateTemplate("sample text", registrationDTO, template,
				RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE, "");
		assertNotNull(response.getSuccessResponseDTO());
	}
	
	@Test
	public void generateTemplateWithDemographicFieldsTest() throws  RegBaseCheckedException {
		registrationDTO = DataProvider.getFilledPacketDTO(Arrays.asList("eng", "ara"));
		Mockito.when(identitySchemaService.getAllFieldSpec(Mockito.anyString(),Mockito.anyDouble())).thenReturn(DataProvider.getFields());
		ResponseDTO response = templateGenerator.generateTemplate("sample text", registrationDTO, template, RegistrationConstants.TEMPLATE_PREVIEW, "");
		assertNotNull(response.getSuccessResponseDTO());
	}
	
	@Test
	public void getDashboardTemplateTest() throws RegBaseCheckedException {
		Mockito.when(packetHandlerServiceImpl.getAllRegistrations()).thenReturn(new ArrayList<>());
		Mockito.when(registrationApprovalService.getEnrollmentByStatus(Mockito.anyString())).thenReturn(new ArrayList<>());
		Mockito.when(packetSynchService.fetchPacketsToBeSynched()).thenReturn(new ArrayList<>());
		List<UserDetail> users = new ArrayList<>();
		UserDetail user1 = new UserDetail();
		user1.setId("110011");
		user1.setIsActive(true);
		user1.setName("test user 1");
		UserDetail user2 = new UserDetail();
		user2.setId("110012");
		user2.setIsActive(true);
		user2.setName("test user 2");
		users.add(user1);users.add(user2);
		Mockito.when(userDetailService.getAllUsers()).thenReturn(users);
		Mockito.when(userDetailService.getUserRoleByUserId("110011")).thenReturn(Arrays.asList("REGISTRATION_SUPERVISOR", "REGISTRATION_OFFICER"));
		Mockito.when(userDetailService.getUserRoleByUserId("110012")).thenReturn(Arrays.asList("REGISTRATION_OFFICER"));
		ResponseDTO resp = new ResponseDTO();
		resp.setErrorResponseDTOs(new ArrayList<>());
		Mockito.when(userMachineMappingService.isUserNewToMachine("110011")).thenReturn(resp);
		Mockito.when(userMachineMappingService.isUserNewToMachine("110012")).thenReturn(new ResponseDTO());
		List<SyncJobDef> syncJobs = new ArrayList<>();
		SyncJobDef syncJob = new SyncJobDef();
		syncJob.setId("RC_001");
		syncJob.setName("test job 1");
		syncJob.setJobType("Sync Job");
		syncJobs.add(syncJob);
		SyncJobDef syncJob1 = new SyncJobDef();
		syncJob1.setId("RC_002");
		syncJob1.setName("test job 2");
		syncJob1.setJobType("Sync Job");
		syncJobs.add(syncJob1);
		Mockito.when(masterSyncServiceImpl.getSyncJobs()).thenReturn(syncJobs);
		SyncControl syncControl = new SyncControl();
		syncControl.setLastSyncDtimes(Timestamp.from(Instant.now()));
		Mockito.when(jobConfigurationService.getSyncControlOfJob(Mockito.anyString())).thenReturn(syncControl);
		Mockito.when(softwareUpdateHandler.getCurrentVersion()).thenReturn("1.2.0-rc2-SNAPSHOT");
		Mockito.when(identitySchemaService.getLatestEffectiveSchemaVersion()).thenReturn(0.5);
		assertNotNull(templateGenerator.generateDashboardTemplate("sample text", template, "test template", Timestamp.from(Instant.now()).toString()).getSuccessResponseDTO());
	}
	
	@Test
	public void getDashboardTemplateExceptionTest() throws RegBaseCheckedException, IOException {
		Mockito.when(packetHandlerServiceImpl.getAllRegistrations()).thenReturn(new ArrayList<>());
		Mockito.when(registrationApprovalService.getEnrollmentByStatus(Mockito.anyString())).thenReturn(new ArrayList<>());
		Mockito.when(packetSynchService.fetchPacketsToBeSynched()).thenReturn(new ArrayList<>());
		List<UserDetail> users = new ArrayList<>();
		UserDetail user = new UserDetail();
		user.setId("110013");
		user.setIsActive(false);
		user.setName("test user 3");
		users.add(user);
		Mockito.when(userDetailService.getAllUsers()).thenReturn(users);
		Mockito.when(userDetailService.getUserRoleByUserId("110013")).thenReturn(Arrays.asList("Default"));
		PowerMockito.mockStatic(IOUtils.class);
		Mockito.when(IOUtils.toByteArray(Mockito.any(InputStream.class))).thenThrow(IOException.class);
		ResponseDTO resp = new ResponseDTO();
		resp.setErrorResponseDTOs(new ArrayList<>());
		Mockito.when(userMachineMappingService.isUserNewToMachine("110013")).thenReturn(new ResponseDTO());
		assertNotNull(templateGenerator.generateDashboardTemplate("sample text", template, "test template", Timestamp.from(Instant.now()).toString()).getErrorResponseDTOs());
	}
	
	@Test
	public void getDashboardTemplateTest2() throws RegBaseCheckedException {
		appMap.put(RegistrationConstants.LAST_SOFTWARE_UPDATE, Timestamp.from(Instant.now()).toString());
		appMap.put(RegistrationConstants.REGCLIENT_INSTALLED_TIME, Timestamp.from(Instant.now()).toString());
		Mockito.when(ApplicationContext.map()).thenReturn(appMap);
		
		Mockito.when(packetHandlerServiceImpl.getAllRegistrations()).thenReturn(new ArrayList<>());
		Mockito.when(registrationApprovalService.getEnrollmentByStatus(Mockito.anyString())).thenReturn(new ArrayList<>());
		Mockito.when(packetSynchService.fetchPacketsToBeSynched()).thenReturn(new ArrayList<>());
		List<UserDetail> users = new ArrayList<>();
		UserDetail user1 = new UserDetail();
		user1.setId("110011");
		user1.setIsActive(true);
		user1.setName("test user 1");
		UserDetail user2 = new UserDetail();
		user2.setId("110012");
		user2.setIsActive(true);
		user2.setName("test user 2");
		users.add(user1);users.add(user2);
		Mockito.when(userDetailService.getAllUsers()).thenReturn(users);
		Mockito.when(userDetailService.getUserRoleByUserId("110011")).thenReturn(Arrays.asList("REGISTRATION_SUPERVISOR", "REGISTRATION_OFFICER"));
		Mockito.when(userDetailService.getUserRoleByUserId("110012")).thenReturn(Arrays.asList("REGISTRATION_OFFICER"));
		ResponseDTO resp = new ResponseDTO();
		resp.setErrorResponseDTOs(new ArrayList<>());
		Mockito.when(userMachineMappingService.isUserNewToMachine("110011")).thenReturn(resp);
		Mockito.when(userMachineMappingService.isUserNewToMachine("110012")).thenReturn(new ResponseDTO());
		List<SyncJobDef> syncJobs = new ArrayList<>();
		SyncJobDef syncJob = new SyncJobDef();
		syncJob.setId("RC_001");
		syncJob.setName("test job 1");
		syncJobs.add(syncJob);
		SyncJobDef syncJob1 = new SyncJobDef();
		syncJob1.setId("RC_002");
		syncJob1.setName("test job 2");
		syncJob1.setJobType("Sync Job");
		syncJobs.add(syncJob1);
		Mockito.when(masterSyncServiceImpl.getSyncJobs()).thenReturn(syncJobs);
		SyncControl syncControl = new SyncControl();
		syncControl.setLastSyncDtimes(Timestamp.from(Instant.now()));
		Mockito.when(jobConfigurationService.getSyncControlOfJob(Mockito.anyString())).thenReturn(syncControl);
		Mockito.when(softwareUpdateHandler.getCurrentVersion()).thenReturn("1.2.0-rc2-SNAPSHOT");
		Mockito.when(identitySchemaService.getLatestEffectiveSchemaVersion()).thenReturn(0.5);
		assertNotNull(templateGenerator.generateDashboardTemplate("sample text", template, "test template", Timestamp.from(Instant.now()).toString()).getSuccessResponseDTO());
	}
}
