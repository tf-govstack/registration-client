package io.github.tf-govstack.registration.test.service;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.BeforeClass;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import io.github.tf-govstack.kernel.core.exception.IOException;
import io.github.tf-govstack.kernel.core.util.FileUtils;
import io.github.tf-govstack.registration.audit.AuditManagerService;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.dao.GlobalParamDAO;
import io.github.tf-govstack.registration.dao.PreRegistrationDataSyncDAO;
import io.github.tf-govstack.registration.dao.RegistrationDAO;
import io.github.tf-govstack.registration.dao.SyncJobConfigDAO;
import io.github.tf-govstack.registration.entity.GlobalParam;
import io.github.tf-govstack.registration.entity.PreRegistrationList;
import io.github.tf-govstack.registration.entity.Registration;
import io.github.tf-govstack.registration.entity.SyncJobDef;
import io.github.tf-govstack.registration.exception.RemapException;
import io.github.tf-govstack.registration.service.config.GlobalParamService;
import io.github.tf-govstack.registration.service.packet.PacketUploadService;
import io.github.tf-govstack.registration.service.packet.RegPacketStatusService;
import io.github.tf-govstack.registration.service.remap.impl.CenterMachineReMapServiceImpl;
import io.github.tf-govstack.registration.service.sync.PacketSynchService;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationAppHealthCheckUtil;
import io.github.tf-govstack.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ RegistrationAppHealthCheckUtil.class, FileUtils.class, ScriptUtils.class,SessionContext.class })
public class CenterMachineReMapServiceTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private CenterMachineReMapServiceImpl centerMachineReMapServiceImpl;
	@Mock
	private PacketSynchService packetSynchService;
	@Mock
	private PacketUploadService packetUploadService;
	@Mock
	private RegPacketStatusService regPacketStatusService;
	@Mock
	private RegistrationDAO registrationDAO;
	@Mock
	private SyncJobConfigDAO syncJobConfigDAO;
	@Mock
	private GlobalParamDAO globalParamDAO;
	@Mock
	private JdbcTemplate jdbcTemplate;
	@Mock
	private DataSource dataSource;
	@Mock
	private Resource resource;

	@Mock
	private Connection connection;
	@Mock
	private AuditManagerService auditFactory;
	@Mock
	private PreRegistrationDataSyncDAO preRegistrationDataSyncDAO;
	@Autowired
	FileUtils fileUtils;
	@Mock
	GlobalParamService globalParamService;
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;

	@BeforeClass
	public static void initialize() throws IOException, java.io.IOException {
		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put("mosip.registration.registration_pre_reg_packet_location", "..//PreRegPacketStore");
		ApplicationContext.getInstance();
		ApplicationContext.setApplicationMap(applicationMap);

	}

	@Test(expected = RemapException.class)
	public void handleRemapProcessTestWithPendingActivity() throws Exception {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.mockStatic(ScriptUtils.class);

		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);

		GlobalParam globalParam = new GlobalParam();

		globalParam.setVal("true");
		Mockito.when(globalParamDAO.get(Mockito.any())).thenReturn(globalParam);
		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setClientStatusCode(RegistrationConstants.SYNCED_STATUS);
		registrationList.add(registration);
		Mockito.when(registrationDAO.findByServerStatusCodeNotIn(Mockito.anyList())).thenReturn(registrationList);
		List<SyncJobDef> syncJobDefList = new ArrayList<>();
		Mockito.when(syncJobConfigDAO.getActiveJobs()).thenReturn(syncJobDefList);
		Mockito.when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		List<PreRegistrationList> preRegistrationList = new ArrayList<>();
		Mockito.when(preRegistrationDataSyncDAO.getAllPreRegPackets()).thenReturn(preRegistrationList);

		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.when(SessionContext.map()).thenReturn(new HashMap<>());		PowerMockito.doNothing().when(FileUtils.class, "deleteDirectory", Mockito.any(File.class));

		for (int i = 1; i < 5; i++) {
			centerMachineReMapServiceImpl.handleReMapProcess(i);
		}

	}


	@Test
	public void handleRemapProcessTest() throws Exception {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.mockStatic(ScriptUtils.class);

		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);

		GlobalParam globalParam = new GlobalParam();

		globalParam.setVal("true");
		Mockito.when(globalParamDAO.get(Mockito.any())).thenReturn(globalParam);
		List<Registration> registrationList = new ArrayList<>();
		Registration registration = new Registration();
		registration.setClientStatusCode("RE_REGISTER_APPROVED");
		registrationList.add(registration);
		Mockito.when(registrationDAO.findByServerStatusCodeNotIn(Mockito.anyList())).thenReturn(registrationList);
		List<SyncJobDef> syncJobDefList = new ArrayList<>();
		Mockito.when(syncJobConfigDAO.getActiveJobs()).thenReturn(syncJobDefList);
		Mockito.when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		List<PreRegistrationList> preRegistrationList = new ArrayList<>();
		Mockito.when(preRegistrationDataSyncDAO.getAllPreRegPackets()).thenReturn(preRegistrationList);

		PowerMockito.mockStatic(SessionContext.class);
		PowerMockito.when(SessionContext.map()).thenReturn(new HashMap<>());
		PowerMockito.doNothing().when(FileUtils.class, "deleteDirectory", Mockito.any(File.class));

		for (int i = 1; i < 5; i++) {
			centerMachineReMapServiceImpl.handleReMapProcess(i);
		}

	}

	@Test
	public void HandleRemapTest() throws Exception {

		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(FileUtils.class);

		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);

		GlobalParam globalParam = new GlobalParam();

		globalParam.setVal("true");
		Mockito.when(globalParamDAO.get(Mockito.any())).thenReturn(globalParam);
		List<Registration> registrationList = new ArrayList<>();

		Mockito.when(registrationDAO.findByServerStatusCodeNotIn(Mockito.anyList())).thenReturn(registrationList);
		SyncJobDef syncJobDef = new SyncJobDef();
		List<SyncJobDef> syncJobDefList = new ArrayList<>();
		syncJobDefList.add(syncJobDef);
		Mockito.when(syncJobConfigDAO.getActiveJobs()).thenReturn(syncJobDefList);
		Mockito.when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		List<PreRegistrationList> list = new ArrayList<>();
		PreRegistrationList preRegistrationList = new PreRegistrationList();
		list.add(preRegistrationList);
		Mockito.when(preRegistrationDataSyncDAO.getAllPreRegPackets()).thenReturn(list);
		List<Registration> regList = new ArrayList<>();
		Mockito.when(registrationDAO.getEnrollmentByStatus(Mockito.anyString())).thenReturn(regList);

		PowerMockito.doNothing().when(FileUtils.class, "deleteDirectory", Mockito.any(File.class));

		Mockito.doNothing().when(globalParamService).update("mosip.registration.initial_setup", "Y");

		for (int i = 1; i < 5; i++) {
			centerMachineReMapServiceImpl.handleReMapProcess(i);
		}
	}

	@Test
	public void PacketsPendingForEODTest() {
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		PowerMockito.mockStatic(FileUtils.class);
		List<Registration> regList = new ArrayList<>();
		Mockito.when(registrationDAO.getEnrollmentByStatus(Mockito.anyString())).thenReturn(regList);
		assertFalse(centerMachineReMapServiceImpl.isPacketsPendingForEOD());

	}

	@Test
	public void handleRemapProcessTestFailure() throws Exception {
		PowerMockito.mockStatic(FileUtils.class);
		PowerMockito.mockStatic(RegistrationAppHealthCheckUtil.class);
		Mockito.when(serviceDelegateUtil.isNetworkAvailable()).thenReturn(true);

		GlobalParam globalParam = new GlobalParam();

		globalParam.setVal("true");
		Mockito.when(globalParamDAO.get(Mockito.any())).thenReturn(globalParam);
		PowerMockito.doThrow(new IOException("error", "error")).when(FileUtils.class, "deleteDirectory",
				Mockito.any(File.class));
		centerMachineReMapServiceImpl.handleReMapProcess(3);

	}
	@Test
	public void startRemapProcessTest() throws RemapException {
		centerMachineReMapServiceImpl.startRemapProcess();
	}

}
