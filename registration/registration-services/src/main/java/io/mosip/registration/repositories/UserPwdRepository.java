package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.UserPassword;

/**
 * Interface for {@link UserPassword}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface UserPwdRepository extends BaseRepository<UserPassword, String> {
	
	void deleteByUsrId(String id);

}
