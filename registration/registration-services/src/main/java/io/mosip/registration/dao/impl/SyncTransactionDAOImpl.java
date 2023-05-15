package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.dao.SyncTransactionDAO;
import io.github.tf-govstack.registration.entity.SyncTransaction;
import io.github.tf-govstack.registration.repositories.SyncTransactionRepository;

/**
 * implementation class of {@link SyncTransactionDAO}
 * 
 * @author Dinesh Ashokan
 *
 */
@Repository
public class SyncTransactionDAOImpl implements SyncTransactionDAO {

	/** Object for Logger. */
	private static final Logger LOGGER = AppConfig.getLogger(SyncTransactionDAOImpl.class);

	/**
	 * Autowired to sync transaction Repository
	 */
	@Autowired
	private SyncTransactionRepository syncTranscRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.dao.JobTransactionDAO#save(io.github.tf-govstack.registration.entity
	 * .SyncTransaction)
	 */
	@Override
	public SyncTransaction save(SyncTransaction syncTransaction) {

		LOGGER.info(RegistrationConstants.SYNC_TRANSACTION_DAO_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"saving sync details to database started");
		return syncTranscRepository.save(syncTransaction);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.SyncJobTransactionDAO#getAll()
	 */
	@Override
	public List<SyncTransaction> getAll() {
		LOGGER.info(RegistrationConstants.SYNC_TRANSACTION_DAO_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetch all sync details from database started");
		return syncTranscRepository.findAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.dao.SyncTransactionDAO#getSyncTransactions(java.sql.
	 * Timestamp, java.lang.String)
	 */
	@Override
	public List<SyncTransaction> getSyncTransactions(Timestamp req, String syncJobId) {
		LOGGER.info(RegistrationConstants.SYNC_TRANSACTION_DAO_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetch  sync details based on crDtime from database started");
		return syncTranscRepository.findByCrDtimeAfterAndSyncJobIdNotOrderByCrDtimeDesc(req, syncJobId);
	}

	/* (non-Javadoc)
	 * @see io.github.tf-govstack.registration.dao.SyncTransactionDAO#getAll(java.lang.String, java.sql.Timestamp, java.sql.Timestamp)
	 */
	@Override
	public List<SyncTransaction> getAll(String syncJobId, Timestamp previousFiredTime, Timestamp currentFiredTime) {
		LOGGER.info(RegistrationConstants.SYNC_TRANSACTION_DAO_LOGGER_TITLE, APPLICATION_NAME, APPLICATION_ID,
				"Fetch  sync details based on crDtime from database started");
		return syncTranscRepository.findBySyncJobIdAndCrDtimeBetween(syncJobId, previousFiredTime, currentFiredTime);

	}
}
