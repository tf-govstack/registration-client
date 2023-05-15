package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.exception.ExceptionUtils;
import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.dao.MasterSyncDao;
import io.github.tf-govstack.registration.entity.BiometricAttribute;
import io.github.tf-govstack.registration.entity.DocumentCategory;
import io.github.tf-govstack.registration.entity.DocumentType;
import io.github.tf-govstack.registration.entity.Language;
import io.github.tf-govstack.registration.entity.Location;
import io.github.tf-govstack.registration.entity.LocationHierarchy;
import io.github.tf-govstack.registration.entity.ReasonCategory;
import io.github.tf-govstack.registration.entity.ReasonList;
import io.github.tf-govstack.registration.entity.SyncControl;
import io.github.tf-govstack.registration.entity.SyncJobDef;
import io.github.tf-govstack.registration.exception.RegBaseUncheckedException;
import io.github.tf-govstack.registration.repositories.BiometricAttributeRepository;
import io.github.tf-govstack.registration.repositories.BlocklistedWordsRepository;
import io.github.tf-govstack.registration.repositories.DocumentCategoryRepository;
import io.github.tf-govstack.registration.repositories.DocumentTypeRepository;
import io.github.tf-govstack.registration.repositories.LanguageRepository;
import io.github.tf-govstack.registration.repositories.LocationHierarchyRepository;
import io.github.tf-govstack.registration.repositories.LocationRepository;
import io.github.tf-govstack.registration.repositories.ReasonCategoryRepository;
import io.github.tf-govstack.registration.repositories.ReasonListRepository;
import io.github.tf-govstack.registration.repositories.SyncJobControlRepository;
import io.github.tf-govstack.registration.repositories.SyncJobDefRepository;

/**
 * The implementation class of {@link MasterSyncDao}
 * 
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
@Repository
@Transactional
public class MasterSyncDaoImpl implements MasterSyncDao {

	/** Object for Sync Status Repository. */
	@Autowired
	private SyncJobControlRepository syncStatusRepository;

	/** Object for Sync Biometric Attribute Repository. */
	@Autowired
	private BiometricAttributeRepository biometricAttributeRepository;

	/** Object for Sync Blocklisted Words Repository. */
	@Autowired
	private BlocklistedWordsRepository blocklistedWordsRepository;

	/** Object for Sync Document Category Repository. */
	@Autowired
	private DocumentCategoryRepository documentCategoryRepository;

	/** Object for Sync Document Type Repository. */
	@Autowired
	private DocumentTypeRepository documentTypeRepository;

	/** Object for Sync Location Repository. */
	@Autowired
	private LocationRepository locationRepository;

	/** Object for Sync Reason Category Repository. */
	@Autowired
	private ReasonCategoryRepository reasonCategoryRepository;

	/** Object for Sync Reason List Repository. */
	@Autowired
	private ReasonListRepository reasonListRepository;

	/** Object for Sync language Repository. */
	@Autowired
	private LanguageRepository languageRepository;

	/** Object for Sync screen auth Repository. */
	@Autowired
	private SyncJobDefRepository syncJobDefRepository;

	@Autowired
	private LocationHierarchyRepository locationHierarchyRepository;

	/**
	 * logger for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(MasterSyncDaoImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.MasterSyncDao#getMasterSyncStatus()
	 */
	@Override
	public SyncControl syncJobDetails(String synccontrol) {

		SyncControl syncControlResonse = null;

		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"DAO findByID method started");

		try {
			// find the user
			syncControlResonse = syncStatusRepository.findBySyncJobId(synccontrol);

		} catch (RuntimeException runtimeException) {
			LOGGER.error(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
					runtimeException.getMessage() + ExceptionUtils.getStackTrace(runtimeException));
			throw new RegBaseUncheckedException(RegistrationConstants.MASTER_SYNC_JOD_DETAILS,
					runtimeException.getMessage());
		}

		LOGGER.info(RegistrationConstants.MASTER_SYNC_JOD_DETAILS, APPLICATION_NAME, APPLICATION_ID,
				"DAO findByID method ended");

		return syncControlResonse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.dao.MasterSyncDao#findLocationByLangCode(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<Location> findLocationByLangCode(int hierarchyLevel, String langCode) {
		return locationRepository.findByIsActiveTrueAndHierarchyLevelAndLangCode(hierarchyLevel, langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.dao.MasterSyncDao#findLocationByParentLocCode(java.lang
	 * .String)
	 */
	@Override
	public List<Location> findLocationByParentLocCode(String parentLocCode, String langCode) {
		return locationRepository.findByIsActiveTrueAndParentLocCodeAndLangCode(parentLocCode, langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.MasterSyncDao#getAllReasonCatogery()
	 */
	@Override
	public List<ReasonCategory> getAllReasonCatogery(String langCode) {
		return reasonCategoryRepository.findByIsActiveTrueAndLangCode(langCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.MasterSyncDao#getReasonList(java.util.List)
	 */
	@Override
	public List<ReasonList> getReasonList(String langCode, List<String> reasonCat) {
		return reasonListRepository.findByIsActiveTrueAndLangCodeAndReasonCategoryCodeIn(langCode, reasonCat);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.dao.MasterSyncDao#getBlockListedWords(java.lang.String)
	 */
	@Override
	public List<String> getBlockListedWords() {
		return blocklistedWordsRepository.findAllActiveBlockListedWords();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.MasterSyncDao#getDocumentCategories(java.lang.
	 * String)
	 */
	@Override
	public List<DocumentType> getDocumentTypes(List<String> docCode, String langCode) {
		return documentTypeRepository.findByIsActiveTrueAndLangCodeAndCodeIn(langCode, docCode);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.MasterSyncDao#getDocumentCategories(java.lang.
	 * String)
	 */
	@Override
	public DocumentType getDocumentType(String docCode, String langCode) {
		return documentTypeRepository.findByIsActiveTrueAndLangCodeAndCode(langCode, docCode);
	}


	public List<SyncJobDef> getSyncJobs() {
		return syncJobDefRepository.findAllByIsActiveTrue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.MasterSyncDao#getBiometricType(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<BiometricAttribute> getBiometricType(String langCode, List<String> biometricType) {
		return biometricAttributeRepository.findByLangCodeAndBiometricTypeCodeIn(langCode, biometricType);
	}

	public List<Language> getActiveLanguages() {

		return languageRepository.findAllByIsActiveTrue();
	}

	public List<DocumentCategory> getDocumentCategory() {
		return documentCategoryRepository.findAllByIsActiveTrue();
	}

	public List<Location> getLocationDetails() {
		return locationRepository.findAllByIsActiveTrue();
	}

	public List<Location> getLocationDetails(String langCode) {
		return locationRepository.findByIsActiveTrueAndLangCode(langCode);
	}

	public List<Location> getLocationDetails(String hierarchyName, String langCode) {
		return locationRepository.findByIsActiveTrueAndHierarchyNameAndLangCode(hierarchyName, langCode);
	}

	public Location getLocation(String code, String langCode) {
		return locationRepository.findByCodeAndLangCode(code, langCode);
	}

	@Override
	public List<LocationHierarchy> getAllLocationHierarchy(String langCode) {
		return locationHierarchyRepository.findAllByIsActiveTrueAndLangCode(langCode);
	}
	
	@Override
	public Long getLocationHierarchyCount() {
		return locationHierarchyRepository.count();
	}
}
