package io.github.tf-govstack.registration.repositories;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.UserToken;

public interface UserTokenRepository extends BaseRepository<UserToken, String> {

    UserToken findByUsrIdAndUserDetailIsActiveTrue(String userId);

    UserToken findTopByTokenExpiryGreaterThanAndUserDetailIsActiveTrueOrderByTokenExpiryDesc(long currentTimeInSeconds);

    UserToken findTopByRtokenExpiryGreaterThanAndUserDetailIsActiveTrueOrderByRtokenExpiryDesc(long currentTimeInSeconds);

    void deleteByUsrId(String usrId);
}
