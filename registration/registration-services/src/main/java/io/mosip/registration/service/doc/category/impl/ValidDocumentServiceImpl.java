
package io.github.tf-govstack.registration.service.doc.category.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tf-govstack.registration.dao.MasterSyncDao;
import io.github.tf-govstack.registration.dao.ValidDocumentDAO;
import io.github.tf-govstack.registration.dto.mastersync.DocumentCategoryDto;
import io.github.tf-govstack.registration.entity.ApplicantValidDocument;
import io.github.tf-govstack.registration.entity.DocumentType;
import io.github.tf-govstack.registration.service.doc.category.ValidDocumentService;

/**
 * Implementation for {@link ValidDocumentService} 
 * 
 * @author balamurugan.ramamoorthy
 *
 * @since 1.0.0
 */
@Service
public class ValidDocumentServiceImpl implements ValidDocumentService {

	@Autowired
	private ValidDocumentDAO validDocumentDAO;

	@Autowired
	private MasterSyncDao masterSyncDao;

	@Override
	public List<DocumentCategoryDto> getDocumentCategories(String applicantType, String docCode, String langCode) {

		List<ApplicantValidDocument> masterValidDocuments = validDocumentDAO.getValidDocuments(applicantType, docCode);

		List<String> validDocuments = new ArrayList<>();
		masterValidDocuments.forEach(docs -> {
			validDocuments.add(docs.getValidDocument().getDocTypeCode());
		});

		List<DocumentCategoryDto> documentsDTO = new ArrayList<>();
		List<DocumentType> masterDocuments = masterSyncDao.getDocumentTypes(validDocuments, langCode);

		masterDocuments.forEach(document -> {

			DocumentCategoryDto documents = new DocumentCategoryDto();
			documents.setDescription(document.getDescription());
			documents.setLangCode(document.getLangCode());
			documents.setName(document.getName());
			documents.setCode(document.getCode());
			documentsDTO.add(documents);

		});

		return documentsDTO;
	}
}