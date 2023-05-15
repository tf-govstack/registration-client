package io.github.tf-govstack.registration.dto;

import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import lombok.Data;

@Data
public class BiometricDeviceInfo {
	
	private String serialNumber;
	private String make;
	private String model;
	private String deviceType;
	
	
	@Override
	public String toString() {
		return new StringBuilder(
				ApplicationContext.getInstance().getApplicationLanguageLabelBundle().getString("serialNumber"))
						.append(serialNumber).append(RegistrationConstants.NEW_LINE)
						.append(ApplicationContext.getInstance().getApplicationLanguageLabelBundle().getString("make"))
						.append(make).append(RegistrationConstants.NEW_LINE)
						.append(ApplicationContext.getInstance().getApplicationLanguageLabelBundle().getString("model"))
						.append(model).toString();
	}	

}
