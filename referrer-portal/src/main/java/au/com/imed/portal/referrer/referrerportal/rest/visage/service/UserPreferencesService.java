package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.HospitalPreferencesJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.UserPreferencesJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.HospitalPreferencesEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.UserPreferencesEntity;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.HospitalUserPreferences;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.UserPreferences;

@Service
public class UserPreferencesService {
	public static final String HELP_SHOW = "show";
	public static final String HELP_HIDE = "hide";
	public static final String NOTIFY_YES = "YES";
	public static final String NOTIFY_NO = "NO";
	public static final String AUTOIMG_OFF = "OFF";
	public static final String AUTOIMG_VUE_MOTION = "VM";
	public static final String AUTOIMG_INTELE_VIEWER = "IV";

	@Autowired
	private UserPreferencesJPARepository repository;

	@Autowired
	private HospitalPreferencesJPARepository hospitalRepository;

	public ResponseEntity<UserPreferences> getPreferences(final String userName) {
		if (userName != null) {
			UserPreferencesEntity entity = getPreferenceEntity(userName);
			UserPreferences pref = new UserPreferences();
			pref.setHelp(entity == null ? HELP_SHOW : entity.getHelp());
			pref.setAutoimg(entity == null || entity.getAutoimg() == null ? AUTOIMG_OFF : entity.getAutoimg());
			pref.setNotify(entity == null || entity.getNotify() == null ? NOTIFY_NO : entity.getNotify());
			return new ResponseEntity<UserPreferences>(pref, HttpStatus.OK);
		} else {
			return new ResponseEntity<UserPreferences>(HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * Utility to check if the given user has accepted t&c
	 * 
	 * @param userName
	 * @return
	 */
	public boolean isTermsAccepted(final String userName) {
		boolean isAccepted = false;
		ResponseEntity<UserPreferences> entity = this.getPreferences(userName);
		if (HttpStatus.OK.equals(entity.getStatusCode())) {
			isAccepted = HELP_HIDE.equalsIgnoreCase(entity.getBody().getHelp());
		}
		return isAccepted;
	}

	public boolean isNotifyOn(final String userName) {
		boolean ison = false;
		ResponseEntity<UserPreferences> entity = this.getPreferences(userName);
		if (HttpStatus.OK.equals(entity.getStatusCode())) {
			ison = NOTIFY_YES.equalsIgnoreCase(entity.getBody().getNotify());
		}
		return ison;
	}

	public ResponseEntity<String> setPreferences(final String userName, final UserPreferences preferences) {
		if (userName != null) {
			System.out.println("setPreferences() help will be " + preferences.getHelp());
			UserPreferencesEntity entity = getPreferenceEntity(userName);
			if (entity == null) {
				entity = new UserPreferencesEntity();
			}
			entity.setUsername(userName);
			entity.setHelp(preferences.getHelp());
			entity.setQuery(""); // not used yet
			entity.setNotify(preferences.getNotify());
			entity.setAutoimg(preferences.getAutoimg());
			repository.saveAndFlush(entity);
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	public ResponseEntity<String> updateTermsAndCondition(final String userName, String termsAndConditionStatus) {
		if (userName != null) {
			UserPreferencesEntity entity = getPreferenceEntity(userName);
			if (entity != null) {
				entity.setUsername(userName);
				entity.setHelp(termsAndConditionStatus);
				repository.saveAndFlush(entity);
			}
			return new ResponseEntity<String>(HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}
	}

	private UserPreferencesEntity getPreferenceEntity(final String userName) {
		List<UserPreferencesEntity> list = repository.findByUsername(userName);
		return list.size() > 0 ? list.get(0) : null;
	}

	public HospitalUserPreferences getHospitalPreferences(final String userName) {
		HospitalUserPreferences pref = null;
		if (userName != null) {
			List<HospitalPreferencesEntity> list = hospitalRepository.findByUsername(userName);
			if (list.size() > 0) {
				pref = new HospitalUserPreferences();
				pref.setHospitalUri(list.get(0).getHospitalUri());
				pref.setHospitalStatus(list.get(0).getHospitalStatus());
			}
		}
		return pref;
	}

	public void updateHospitalPreferences(final String userName, final HospitalUserPreferences preferences)
			throws Exception {
		HospitalPreferencesEntity entity = hospitalRepository.findByUsername(userName).get(0);
		// Update only set something
		if (preferences.getHospitalUri() != null) {
			entity.setHospitalUri(preferences.getHospitalUri());
		}
		if (preferences.getHospitalStatus() != null) {
			entity.setHospitalStatus(preferences.getHospitalStatus());
		}
		hospitalRepository.saveAndFlush(entity);
	}
}
