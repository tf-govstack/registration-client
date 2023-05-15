package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.DocumentCategory;

/**
 * Interface for {@link DocumentCategory}
 * 
 * @author Brahmananda Reddy
 *
 */
public interface DocumentCategoryRepository extends BaseRepository<DocumentCategory, String> {

	List<DocumentCategory> findByIsActiveTrueAndLangCode(String langCode);

	List<DocumentCategory> findAllByIsActiveTrue();

	DocumentCategory findByIsActiveTrueAndCodeAndLangCode(String docCategeoryCode, String langCode);
	
	DocumentCategory findByIsActiveTrueAndCodeAndNameAndLangCode(String docCategeoryCode,String docName, String langCode);

}
