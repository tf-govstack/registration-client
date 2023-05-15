package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.dao.DocumentCategoryDAO;
import io.github.tf-govstack.registration.dto.packetmanager.DocumentDto;
import io.github.tf-govstack.registration.entity.DocumentCategory;
import io.github.tf-govstack.registration.repositories.DocumentCategoryRepository;

/**
 * implementation class of RegistrationDocumentCategoryDAOImpl
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class DocumentCategoryDAOImpl implements DocumentCategoryDAO {
	/** instance of {@link DocumentCategoryRepository} */
	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;
	/** instance of {@link Logger} */
	private static final Logger LOGGER = AppConfig.getLogger(DocumentCategoryDAOImpl.class);

	@Override
	public List<DocumentCategory> getDocumentCategories() {
		LOGGER.info("REGISTRATION-PACKET_CREATION-DOCUMENTCATEGORY", APPLICATION_NAME, APPLICATION_ID,
				"fetching the document categories");

		return documentCategoryRepository.findAll();
	}

	@Override
	public List<DocumentCategory> getDocumentCategoriesByLangCode(String langCode) {
		LOGGER.info("REGISTRATION-PACKET_CREATION-DOCUMENTCATEGORY", APPLICATION_NAME, APPLICATION_ID,
				"fetching the document categories by lang code");

		return documentCategoryRepository.findByIsActiveTrueAndLangCode(langCode);
	}

	@Override
	public DocumentCategory getDocumentCategoryByCodeAndByLangCode(String docCategeoryCode, String langCode) {
		LOGGER.info("REGISTRATION-PACKET_CREATION-DOCUMENTCATEGORY", APPLICATION_NAME, APPLICATION_ID,
				"fetching the document categories by lang code");

		return documentCategoryRepository.findByIsActiveTrueAndCodeAndLangCode(docCategeoryCode, langCode);

	}
	
}
