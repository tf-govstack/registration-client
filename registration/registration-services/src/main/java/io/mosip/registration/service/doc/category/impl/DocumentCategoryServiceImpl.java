package io.github.tf-govstack.registration.service.doc.category.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.dao.DocumentCategoryDAO;
import io.github.tf-govstack.registration.dao.impl.DocumentCategoryDAOImpl;
import io.github.tf-govstack.registration.entity.DocumentCategory;
import io.github.tf-govstack.registration.service.doc.category.DocumentCategoryService;

/**
 * Implementation for {@link DocumentCategoryService} 
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class DocumentCategoryServiceImpl implements DocumentCategoryService {

	@Autowired
	private DocumentCategoryDAO documentCategoryDAO;

	private static final Logger LOGGER = AppConfig.getLogger(DocumentCategoryDAOImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.service.doc.category.DocumentCategoryService#
	 * getDocumentCategories()
	 */
	@Override
	public List<DocumentCategory> getDocumentCategories() {
		LOGGER.info("REGISTRATION-PACKET_CREATION-DOCUMENTCATEGORY", APPLICATION_NAME, APPLICATION_ID,
				"fetching the document categories");

		return documentCategoryDAO.getDocumentCategories();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.service.doc.category.DocumentCategoryService#
	 * getDocumentCategoriesByLangCode(java.lang.String)
	 */
	@Override
	public List<DocumentCategory> getDocumentCategoriesByLangCode(String langCode) {
		LOGGER.info("REGISTRATION-PACKET_CREATION-DOCUMENTCATEGORY", APPLICATION_NAME, APPLICATION_ID,
				"fetching the document categories by lang code");

		return documentCategoryDAO.getDocumentCategoriesByLangCode(langCode);
	}

}
