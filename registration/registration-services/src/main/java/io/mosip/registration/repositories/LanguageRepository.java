package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.Language;

/**
 * Repository to perform CRUD operations on Language.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
public interface LanguageRepository extends BaseRepository<Language, String> {

	
		List<Language> findAllByIsActiveTrue();
}
