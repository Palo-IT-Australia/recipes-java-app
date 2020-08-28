package au.com.imed.portal.referrer.referrerportal.audit;

import java.util.Date;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.util.Aes128StringEncodeUtil;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmAdminAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmAdminAuditJpaRepository;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;

@Service
public class CrmAdminAuditService {
	private Logger logger = LoggerFactory.getLogger(CrmAdminAuditService.class);

	@Autowired
	private CrmAdminAuditJpaRepository repository;
	
	public static final String COMMAND_CREATE = "create";
	public static final String COMMAND_APPROVE = "approve";
	public static final String COMMAND_INQUIRE = "inquire";
	public static final String COMMAND_RESET = "reset";

	public void auditReset(final String crm, final AccountDetail detail, final String temporalPassword) {
		final String encpwd = Aes128StringEncodeUtil.encrypt(temporalPassword);		
		CrmAdminAuditEntity entity = new CrmAdminAuditEntity();
		entity.setCommand(COMMAND_RESET);
		entity.setAuditAt(new Date());
		entity.setCrm(crm);
		entity.setReferrer(detail.getUid());
		entity.setPasswordEncoded(encpwd);
		entity.setEmail(detail.getEmail());
		entity.setMobile(detail.getMobile());
		repository.saveAndFlush(entity);
	}
	
	public void auditApprove(final String crm, final String uid, final String newuid) {
		CrmAdminAuditEntity entity = new CrmAdminAuditEntity();
		entity.setCommand(COMMAND_APPROVE);
		entity.setAuditAt(new Date());
		entity.setCrm(crm);
		entity.setReferrer(StringUtil.isBlank(newuid) ? uid : newuid);
		repository.saveAndFlush(entity);
	}
	
	public void auditCreate(final String crm, final ExternalUser imedExternalUser, int validationId) {
		logger.info("Audit Create referrer uid = " + imedExternalUser.getUserid() + ", pswd = " + imedExternalUser.getPassword());
		final String encpwd = Aes128StringEncodeUtil.encrypt(imedExternalUser.getPassword());		
		CrmAdminAuditEntity entity = new CrmAdminAuditEntity();
		entity.setCommand(COMMAND_CREATE);
		entity.setAuditAt(new Date());
		entity.setCrm(crm);
		entity.setReferrer(imedExternalUser.getUserid());
		entity.setValidationId(validationId);
		entity.setPasswordEncoded(encpwd);
		entity.setFirstName(imedExternalUser.getFirstName());
		entity.setLastName(imedExternalUser.getLastName());
		entity.setEmail(imedExternalUser.getEmail());
		entity.setAccountType(imedExternalUser.getAccountType());
		entity.setAhpra(imedExternalUser.getAhpraNumber());
		entity.setMobile(imedExternalUser.getMobile());
		entity.setPhone(imedExternalUser.getPreferredPhone());
		repository.saveAndFlush(entity);		
	}
	
	public void auditInquire(final String crm, final String provider, final String name) {
		CrmAdminAuditEntity entity = new CrmAdminAuditEntity();
		entity.setCommand(COMMAND_INQUIRE);
		entity.setAuditAt(new Date());
		entity.setCrm(crm);
		entity.setParameter("provider=" + provider + "&name=" + name);
		repository.saveAndFlush(entity);
	}
	
	public String getRawPasswordForCrmCreate(final String referrer) {
		String raw = null;
		List<CrmAdminAuditEntity> list = repository.findByCommandAndReferrerOrderByAuditAtDesc(COMMAND_CREATE, referrer);
		if(list.size() > 0) {
			raw = Aes128StringEncodeUtil.decrypt(list.get(0).getPasswordEncoded());
		} else {
			logger.info("getRawPasswordForCrmCreate() referrer uid not found " + referrer);
		}
		logger.info("getRawPasswordForCrmCreate {} {}", referrer, raw);
		return raw;
	}
	
	public String getRawPasswordForCrmReset(final String referrer) {
		String raw = null;
		List<CrmAdminAuditEntity> list = repository.findByCommandAndReferrerOrderByAuditAtDesc(COMMAND_RESET, referrer);
		if(list.size() > 0) {
			raw = Aes128StringEncodeUtil.decrypt(list.get(0).getPasswordEncoded());
		} else {
			logger.info("referrer uid not found " + referrer);
		}
		logger.info("getRawPasswordForCrmReset {} {}", referrer, raw);
		return raw;
	}
	
	public void switchReferrerUid(final String uid, final String newUid) {
		if(!StringUtil.isBlank(newUid)) {
			List<CrmAdminAuditEntity> list = repository.findByCommandAndReferrerOrderByAuditAtDesc(COMMAND_CREATE, uid);
			if(list.size() > 0) {
				CrmAdminAuditEntity entity = list.get(0);
				entity.setReferrer(newUid);
				repository.saveAndFlush(entity);
				logger.info("Updated referrer uid {} into {}", uid, newUid);
			} else {
				logger.info("Not created by crm " + uid);
			}
		} else {
			logger.info("No new uid");
		}
	}
	
	public boolean isAutoValidationCreatedByCrm(final String referrerId, final int validationTableId) {
		List<CrmAdminAuditEntity> list = repository.findByCommandAndReferrerAndValidationIdOrderByAuditAtDesc(COMMAND_CREATE, referrerId, validationTableId);
		logger.info("Found crm entry ? " + list.size());
		return list.size() > 0;
	}
}
