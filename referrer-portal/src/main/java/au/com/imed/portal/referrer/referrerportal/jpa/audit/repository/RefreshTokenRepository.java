package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>  {

    List<RefreshToken> findAllByUserIdAndValid(String userId, boolean valid);

    RefreshToken findByRefreshToken(String refreshToken);
}
