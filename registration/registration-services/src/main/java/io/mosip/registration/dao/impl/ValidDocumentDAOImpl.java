package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.dao.ValidDocumentDAO;
import io.github.tf-govstack.registration.entity.ApplicantValidDocument;
import io.github.tf-govstack.registration.repositories.ApplicantValidDocumentRepository;

/**
 * implementation class of RegistrationValidDocumentDAO.
 *
 * @author Brahmanada Reddy
 * @since 1.0.0
 */
@Repository
public class ValidDocumentDAOImpl implements ValidDocumentDAO {
	
	/**  instance of {@link ApplicantValidDocumentRepository}. */
	@Autowired
	private ApplicantValidDocumentRepository applicantValidDocumentRepository;
	
	/**  instance of {@link Logger}. */
	private static final Logger LOGGER = AppConfig.getLogger(ValidDocumentDAOImpl.class);

	/* (non-Javadoc)
	 * @see io.github.tf-govstack.registration.dao.ValidDocumentDAO#getValidDocuments(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<ApplicantValidDocument> getValidDocuments(String applicantType, String docCategoryCode) {
		LOGGER.info("Fetching Document details applicantType: {}, docCategoryCode: {}", applicantType, docCategoryCode);
		return applicantValidDocumentRepository.findByIsActiveTrueAndValidDocumentAppTypeCodeAndValidDocumentDocCatCode(applicantType,
				docCategoryCode);

	}

}
