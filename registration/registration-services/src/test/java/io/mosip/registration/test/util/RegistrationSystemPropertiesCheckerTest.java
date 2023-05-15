package io.github.tf-govstack.registration.test.util;

import org.junit.Assert;
import org.junit.Test;

import io.github.tf-govstack.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

public class RegistrationSystemPropertiesCheckerTest {

	@Test
	public void testGetMachineId() {
		String machineId = RegistrationSystemPropertiesChecker.getMachineId();
		Assert.assertNotNull(machineId);
	}


}
