package io.github.tf-govstack.registration.jobs.impl;

import java.util.Map;
import java.util.WeakHashMap;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.LoggerConstants;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.entity.SyncJobDef;
import io.github.tf-govstack.registration.jobs.JobManager;

/**
 * This job is the implementation of {@link JobManager}
 * 
 * <p>
 * This will be automatically triggered based on sync_frequency which has in
 * local DB.
 * </p>
 * 
 * <p>
 * If Sync_frequency = "0 0 11 * * ?" this job will be triggered everyday 11:00
 * AM, if it was missed on 11:00 AM, trigger on immediate application launch.
 * </p>
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */
@Component
public class JobManagerImpl implements JobManager {

	/**
	 * LOGGER for logging
	 */
	private static final Logger LOGGER = AppConfig.getLogger(JobManagerImpl.class);

	public synchronized String getJobId(JobExecutionContext context) {

		return getJobId(context.getJobDetail());
	}

	public synchronized String getJobId(JobDetail jobDetail) {

		return jobDetail.getKey().getName();
	}

	@Override
	public synchronized String getJobId(Trigger trigger) {
		return getJobId((JobDetail) trigger.getJobDataMap().get(RegistrationConstants.JOB_DETAIL));
	}

	

}
