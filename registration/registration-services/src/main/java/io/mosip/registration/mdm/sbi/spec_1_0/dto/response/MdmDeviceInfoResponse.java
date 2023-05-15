package io.github.tf-govstack.registration.mdm.sbi.spec_1_0.dto.response;

import lombok.Data;

@Data
public class MdmDeviceInfoResponse {

	private Error error;
	private String deviceInfo;
}
