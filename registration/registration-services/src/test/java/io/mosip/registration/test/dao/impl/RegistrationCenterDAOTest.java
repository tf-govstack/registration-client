package io.github.tf-govstack.registration.test.dao.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.context.SessionContext;
import io.github.tf-govstack.registration.dao.impl.RegistrationCenterDAOImpl;
import io.github.tf-govstack.registration.entity.MachineMaster;
import io.github.tf-govstack.registration.entity.RegistrationCenter;
import io.github.tf-govstack.registration.entity.id.RegistartionCenterId;
import io.github.tf-govstack.registration.repositories.MachineMasterRepository;
import io.github.tf-govstack.registration.repositories.RegistrationCenterRepository;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@PrepareForTest({ SessionContext.class, ApplicationContext.class, RegistrationSystemPropertiesChecker.class })
public class RegistrationCenterDAOTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	@InjectMocks
	private RegistrationCenterDAOImpl registrationCenterDAOImpl;

	@Mock
	private RegistrationCenterRepository registrationCenterRepository;
	
	@Mock
	private MachineMasterRepository machineMasterRepository;

	@Before
	public void initialize() throws Exception {
		Map<String,Object> appMap = new HashMap<>();
		PowerMockito.mockStatic(ApplicationContext.class, SessionContext.class, RegistrationSystemPropertiesChecker.class);
		PowerMockito.doReturn(appMap).when(ApplicationContext.class, "map");
		PowerMockito.doReturn("eng").when(ApplicationContext.class, "applicationLanguage");
		PowerMockito.doReturn("test").when(RegistrationSystemPropertiesChecker.class, "getMachineId");
	}	
	@Test
	public void getRegistrationCenterDetailsSuccessTest() {

		RegistrationCenter registrationCenter = new RegistrationCenter();
		RegistartionCenterId registartionCenterId = new RegistartionCenterId();
		registartionCenterId.setId("10011");
		registrationCenter.setRegistartionCenterId(registartionCenterId);
		
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(registrationCenterRepository.findByIsActiveTrueAndRegistartionCenterIdIdAndRegistartionCenterIdLangCode("mosip","eng"))
				.thenReturn(registrationCenterList);
		assertTrue(registrationCenterList.isPresent());
		assertNotNull(registrationCenterDAOImpl.getRegistrationCenterDetails("mosip","eng"));
	}

	@Test
	public void isMachineCenterActiveFalseTest() {

		MachineMaster machineMaster = null;
		RegistrationCenter registrationCenter = new RegistrationCenter();
		RegistartionCenterId registartionCenterId = new RegistartionCenterId();
		registartionCenterId.setId("10011");
		registrationCenter.setRegistartionCenterId(registartionCenterId);		
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(machineMasterRepository.findByNameIgnoreCase("machineName")).thenReturn(machineMaster);
		assertTrue(registrationCenterList.isPresent());
		assertFalse(registrationCenterDAOImpl.isMachineCenterActive());
	}
	
	@Test
	public void isMachineCenterActiveTrueTest() {
		MachineMaster machineMaster = new MachineMaster() ;
		machineMaster.setName("name");
		RegistrationCenter registrationCenter = new RegistrationCenter();
		Optional<RegistrationCenter> registrationCenterList = Optional.of(registrationCenter);
		Mockito.when(machineMasterRepository.findByNameIgnoreCase(Mockito.any())).thenReturn(machineMaster);
		Mockito.when(registrationCenterRepository.findByIsActiveTrueAndRegistartionCenterIdIdAndRegistartionCenterIdLangCode("mosip","eng")).thenReturn(registrationCenterList);
		assertTrue(registrationCenterList.isPresent());
		assertFalse(registrationCenterDAOImpl.isMachineCenterActive());
	}
}
