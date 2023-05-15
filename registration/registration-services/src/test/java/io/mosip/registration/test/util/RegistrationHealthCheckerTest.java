package io.github.tf-govstack.registration.test.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import io.github.tf-govstack.registration.util.healthcheck.RegistrationAppHealthCheckUtil;

public class RegistrationHealthCheckerTest {


	@Test
	public void diskSpaceAvailableTest() {
		RegistrationAppHealthCheckUtil.isDiskSpaceAvailable();
	}

	/*@Test
	public void networkAvailableTest() {
		RegistrationAppHealthCheckUtil.isNetworkAvailable();
	}*/
}
