package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.LocalPreferences;

public interface LocalPreferencesRepository extends BaseRepository<LocalPreferences, String> {
	
	LocalPreferences findByIsDeletedFalseAndName(String name);
	
	List<LocalPreferences> findByIsDeletedFalseAndConfigType(String configType);

}
