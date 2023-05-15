package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.UserBiometric;
import io.github.tf-govstack.registration.entity.id.UserBiometricId;

/**
 * Interface for {@link UserBiometric}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface UserBiometricRepository extends BaseRepository<UserBiometric, UserBiometricId>{

	public List<UserBiometric> findByUserBiometricIdBioAttributeCodeAndIsActiveTrue(String attrCode);
	
	public List<UserBiometric> findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeIgnoreCase(String userId, String bioType);
	
	public UserBiometric findByUserBiometricIdUsrIdAndIsActiveTrueAndUserBiometricIdBioTypeCodeAndUserBiometricIdBioAttributeCodeIgnoreCase(String userId, String bioType, String bioSubType);

	void deleteByUserBiometricIdUsrId(String userID);
	
	public List<UserBiometric> findByUserBiometricIdBioTypeCodeAndIsActiveTrue(String bioType);

	public List<UserBiometric> findByUserBiometricIdUsrId(String userId);

	public List<UserBiometric> findByUserBiometricIdUsrIdNotAndUserBiometricIdBioTypeCodeAndIsActiveTrue(String userId, String bioType);
}
