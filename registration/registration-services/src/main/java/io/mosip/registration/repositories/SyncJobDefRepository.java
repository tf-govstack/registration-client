package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.SyncJobDef;

public interface SyncJobDefRepository extends BaseRepository<SyncJobDef, String> {


	/**
	 * fetches all jobs that is active
	 *
	 * @return the list of SyncControl
	 */
	
	List<SyncJobDef> findAllByIsActiveTrue();
}
