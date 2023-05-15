package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.DynamicField;

public interface DynamicFieldRepository extends BaseRepository<DynamicField, String> {

	DynamicField findByNameAndLangCode(String fieldName, String langCode);

	DynamicField findByIsActiveTrueAndNameAndLangCode(String fieldName, String langCode);
}
