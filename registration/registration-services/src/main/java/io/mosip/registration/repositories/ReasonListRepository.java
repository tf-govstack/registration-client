package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.ReasonList;

/**
 * This class will handle the CRUD operation of reason list
 * 
 * @author Sreekar Chukka
 *
 */
public interface ReasonListRepository extends BaseRepository<ReasonList, String> {

	List<ReasonList> findByIsActiveTrueAndLangCodeAndReasonCategoryCodeIn(String langCode, List<String> resonCatog);

}
