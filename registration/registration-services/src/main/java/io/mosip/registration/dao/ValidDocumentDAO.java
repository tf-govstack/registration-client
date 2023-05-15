package io.github.tf-govstack.registration.dao;

import java.util.List;

import io.github.tf-govstack.registration.entity.ApplicantValidDocument;

/**
 * This class is used to fetch all the ValidDocument from {@link ValidDocument} table.
 * 
 * @author Brahmananda Reddy
 *
 */
public interface ValidDocumentDAO {


	/**
	 * This method is used to get the valid documents from {@link ValidDocument} table.
	 *
	 * @param applicantType 
	 * 				the applicant type
	 * @param docCategoryCode 
	 * 				the document category code
	 * @return the list of valid documents
	 */
	List<ApplicantValidDocument> getValidDocuments(String applicantType, String docCategoryCode);

}
