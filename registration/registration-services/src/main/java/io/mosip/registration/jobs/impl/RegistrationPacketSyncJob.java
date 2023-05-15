package io.github.tf-govstack.registration.jobs.impl;

import java.sql.Timestamp;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.kernel.core.util.DateUtils;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.LoggerConstants;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.dto.ResponseDTO;
import io.github.tf-govstack.registration.exception.RegBaseUncheckedException;
import io.github.tf-govstack.registration.jobs.BaseJob;
import io.github.tf-govstack.registration.service.sync.PacketSynchService;

/**
 * The {@code RegistrationPacketSyncJob} is a job to sync the packet status
 * which extends {@code BaseJob}
 * 
 * <p>
 * This Job will be automatically triggered based on sync_frequency which has in
 * local DB.
 * </p>
 * 
 * <p>
 * If Sync_frequency = "0 0 11 * * ?" this job will be triggered everyday 11:00
 * AM, if it was missed on 11:00 AM, trigger on immediate application launch
 * </p>
 * 
 * @author SARAVANAKUMAR G
 * @since 1.0.0
 *
 */
@DisallowConcurrentExecution
@Component(value = "registrationPacketSyncJob")
public class RegistrationPacketSyncJob extends BaseJob {

	/**
	 * The RegPacketStatusServiceImpl
	 */

	@Autowired
	private PacketSynchService packetSynchService;


	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationPacketSyncJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.
	 * quartz.JobExecutionContext)
	 */
	@Async
	@Override
	public void executeInternal(JobExecutionContext context) {
		LOGGER.debug(LoggerConstants.REG_PACKET_SYNC_STATUS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal started");
		this.responseDTO = new ResponseDTO();

		try {
			this.jobId = loadContext(context);
			packetSynchService = applicationContext.getBean(PacketSynchService.class);

			// Execute Parent Job
			this.responseDTO = executeParentJob(jobId);

			// Execute Current Job
			if (responseDTO.getSuccessResponseDTO() != null) {
				this.responseDTO = packetSynchService.syncPacket(triggerPoint);

			}
			syncTransactionUpdate(responseDTO, triggerPoint, jobId, Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		} catch (RegBaseUncheckedException baseUncheckedException) {
			LOGGER.error(LoggerConstants.REG_PACKET_SYNC_STATUS_JOB, RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, baseUncheckedException.getMessage());
			throw baseUncheckedException;
		}

		LOGGER.debug(LoggerConstants.REG_PACKET_SYNC_STATUS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "job execute internal Ended");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.jobs.BaseJob#executeJob(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public ResponseDTO executeJob(String triggerPoint, String jobId) {
		LOGGER.debug(LoggerConstants.REG_PACKET_SYNC_STATUS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute Job started");

		// Execute Parent Job
		this.responseDTO = executeParentJob(jobId);

		// Execute Current Job
		if (responseDTO.getSuccessResponseDTO() != null) {
			this.responseDTO = packetSynchService.syncPacket(triggerPoint);
		}
		syncTransactionUpdate(responseDTO, triggerPoint, jobId, Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));

		LOGGER.debug(LoggerConstants.REG_PACKET_SYNC_STATUS_JOB, RegistrationConstants.APPLICATION_NAME,
				RegistrationConstants.APPLICATION_ID, "execute job ended");

		return responseDTO;
	}

}
