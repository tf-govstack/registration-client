package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.BiometricAttribute;

/**
 * Interface for {@link BiometricAttribute} 
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface BiometricAttributeRepository extends BaseRepository<BiometricAttribute, String> {
	
	List<BiometricAttribute> findByLangCodeAndBiometricTypeCodeIn(String langCode, List<String> biometricType);

}
