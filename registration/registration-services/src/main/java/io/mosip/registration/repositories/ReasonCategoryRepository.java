package io.github.tf-govstack.registration.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.ReasonCategory;

/**
 * The Interface MasterSyncReasonCategoryRepository.
 *
 * @author Sreekar Chukka
 */
@Repository
public interface ReasonCategoryRepository extends BaseRepository<ReasonCategory, String> {

	List<ReasonCategory> findByIsActiveTrueAndLangCode(String langCode);

}
