package io.github.tf-govstack.registration.dto;

import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.context.ApplicationContext;
import lombok.Data;

@Data
public class ScanDeviceInfo {

	private String id;
	private String name;
	private String model;
	
	
	@Override
	public String toString() {
		return new StringBuilder(
				ApplicationContext.getInstance().getApplicationLanguageLabelBundle().getString("deviceId"))
						.append(id).append(RegistrationConstants.NEW_LINE)
						.append(ApplicationContext.getInstance().getApplicationLanguageLabelBundle().getString("deviceName"))
						.append(name).append(RegistrationConstants.NEW_LINE)
						.append(ApplicationContext.getInstance().getApplicationLanguageLabelBundle().getString("model"))
						.append(model).toString();
	}	
}
