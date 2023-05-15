package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.ApplicantValidDocument;
import io.github.tf-govstack.registration.entity.id.ApplicantValidDocumentID;

/**
 * Interface for {@link ApplicantValidDocument} 
 * 
 * @author Brahmananda Reddy
 *
 */

public interface ApplicantValidDocumentRepository extends BaseRepository<ApplicantValidDocument, ApplicantValidDocumentID> {

	List<ApplicantValidDocument> findByIsActiveTrueAndDocumentCategoryCodeAndDocumentCategoryLangCode(String docCategoryCode,
			String langCode);

	List<ApplicantValidDocument> findByIsActiveTrueAndValidDocumentAppTypeCodeAndValidDocumentDocCatCode(
			String applicantType, String docCategoryCode);
	
}
