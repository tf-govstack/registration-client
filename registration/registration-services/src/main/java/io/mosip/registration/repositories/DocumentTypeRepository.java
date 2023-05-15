package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.DocumentType;

/**
 * Interface for {@link DocumentType}
 * 
 * @author Brahmananda Reddy
 *
 */

public interface DocumentTypeRepository extends BaseRepository<DocumentType, String> {

	List<DocumentType> findByIsActiveTrueAndLangCodeAndCodeIn(String langCode, List<String> docCode);

	List<DocumentType> findByIsActiveTrueAndName(String docTypeName);
	
	List<DocumentType> findAllByIsActiveTrue();

	DocumentType findByIsActiveTrueAndLangCodeAndCode(String langCode, String docCode);
}
