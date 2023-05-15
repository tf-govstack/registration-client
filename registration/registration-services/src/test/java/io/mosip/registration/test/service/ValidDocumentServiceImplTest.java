package io.github.tf-govstack.registration.test.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.github.tf-govstack.registration.dao.MasterSyncDao;
import io.github.tf-govstack.registration.dao.ValidDocumentDAO;
import io.github.tf-govstack.registration.entity.ApplicantValidDocument;
import io.github.tf-govstack.registration.entity.DocumentType;
import io.github.tf-govstack.registration.entity.id.ApplicantValidDocumentID;
import io.github.tf-govstack.registration.service.doc.category.impl.ValidDocumentServiceImpl;

public class ValidDocumentServiceImplTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@Mock
	private ValidDocumentDAO validDocumentDAO;

	@Mock
	private MasterSyncDao masterSyncDao;

	@InjectMocks
	private ValidDocumentServiceImpl validDocumentServiceImpl;

	@Test
	public void getDocumentCategoriesTest() {

		List<ApplicantValidDocument> validDocuments = new ArrayList<>();
		ApplicantValidDocument validDocument = new ApplicantValidDocument();
		ApplicantValidDocumentID validDocumentId=new ApplicantValidDocumentID();
		validDocumentId.setAppTypeCode("007");
		validDocumentId.setDocCatCode("POA");
		validDocumentId.setDocTypeCode("DocType");
		validDocument.setValidDocument(validDocumentId);

		validDocuments.add(validDocument);

		List<String> list = new ArrayList<>();
		list.add("DocType");

		List<DocumentType> documentTypes = new ArrayList<>();
		DocumentType documentType = new DocumentType();
		documentType.setCode("DocType");
		documentType.setLangCode("eng");
		documentType.setName("Passport");
		documentTypes.add(documentType);

		Mockito.when(validDocumentDAO.getValidDocuments("007", "POA")).thenReturn(validDocuments);
		Mockito.when(masterSyncDao.getDocumentTypes(list, "eng")).thenReturn(documentTypes);

		assertNotNull(validDocumentServiceImpl.getDocumentCategories("007", "POA", "eng"));
	}

}
