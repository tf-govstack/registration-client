package io.github.tf-govstack.registration.dto;

import java.util.List;

import io.github.tf-govstack.registration.dto.biometric.FaceDetailsDTO;
import io.github.tf-govstack.registration.dto.biometric.FingerprintDetailsDTO;
import io.github.tf-govstack.registration.dto.biometric.IrisDetailsDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationValidatorDTO {
	private String userId;
	private String password;
	private String otp;
	private List<FingerprintDetailsDTO> fingerPrintDetails;
	private String authValidationType;
	private List<IrisDetailsDTO> irisDetails;
	private FaceDetailsDTO faceDetail;
	private boolean authValidationFlag;
	
}
