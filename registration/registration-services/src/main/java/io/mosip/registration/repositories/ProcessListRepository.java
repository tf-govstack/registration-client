package io.github.tf-govstack.registration.repositories;

import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.ProcessList;
import io.github.tf-govstack.registration.entity.id.IdAndLanguageCodeID;

/**
 * ProcessListRepository.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Repository
public interface ProcessListRepository extends BaseRepository<ProcessList, IdAndLanguageCodeID> {

}
