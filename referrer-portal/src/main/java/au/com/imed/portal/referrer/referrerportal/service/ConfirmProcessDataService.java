package au.com.imed.portal.referrer.referrerportal.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerPasswordResetEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerPasswordResetRepository;
import au.com.imed.portal.referrer.referrerportal.utils.SmsPasscodeHashUtil;
import au.com.imed.portal.referrer.referrerportal.utils.UrlCodeAes128Util;

@Service
public class ConfirmProcessDataService {
	private Logger logger = LoggerFactory.getLogger(ConfirmProcessDataService.class);

	@Autowired
	private ReferrerPasswordResetRepository resetRepository;
	
	//
	// Reset password
	//
	public ReferrerPasswordResetEntity savePasswordReset(final String uid, final String passcode) {
		ReferrerPasswordResetEntity entity = new ReferrerPasswordResetEntity();
    try
    {
      entity.setUid(uid);
      entity.setExpiredAt(UrlCodeAes128Util.getExpiryDate()); // 24 Hours valid
      entity.setFailures((byte) 0);

      String generatedSecuredPasswordHash = SmsPasscodeHashUtil.generateStorngPasswordHash(passcode);
      String [] places = generatedSecuredPasswordHash.split(":");
      entity.setPasscodeSalt(places[0]);
      entity.setPasscodeHash(places[1]);

      final String urlCode = SmsPasscodeHashUtil.randomString(32);
      entity.setUrlCode(urlCode);  
    }catch(Exception ex) {
      ex.printStackTrace();
      return null;
    }
    return resetRepository.save(entity);
  }
	
	public ReferrerPasswordResetEntity getReferrerPasswordResetEntityBySecret(final String secret) {
		final String urlcodedb = UrlCodeAes128Util.decrypt(secret.replaceAll(" ", "+"));
    List<ReferrerPasswordResetEntity> list = resetRepository.findByUrlCodeAndFailuresLessThanAndExpiredAtAfterAndActivatedAtIsNull(urlcodedb, (byte)3, new Date());
    logger.info("getReferrerPasswordResetEntityBySecret() valid secret ? " + list);
    return list.size() > 0 ? list.get(0) : null;
	}
	
	public ReferrerPasswordResetEntity setPasswordResetActive(ReferrerPasswordResetEntity entity) {
		entity.setActivatedAt(new Date());
		return resetRepository.save(entity);
	}
	
	public void incrementPasswordResetFailures(ReferrerPasswordResetEntity entity) {
		byte fs = entity.getFailures();
    entity.setFailures(++fs);
    resetRepository.save(entity);
	}
}
