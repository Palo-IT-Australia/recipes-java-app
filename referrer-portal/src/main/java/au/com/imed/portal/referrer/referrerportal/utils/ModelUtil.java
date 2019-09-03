package au.com.imed.portal.referrer.referrerportal.utils;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.imed.portal.referrer.referrerportal.model.ExternalPractice;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;

public class ModelUtil {
  private final static Pattern SPECIAL = Pattern.compile ("[!#$%&*+=|<>?{}\\[\\]~]");

	private static Logger logger = LoggerFactory.getLogger(ModelUtil.class);

	public static boolean sanitizeModel(Object obj) {
    return sanitizeModel(obj, false);
  }
  
  public static boolean sanitizeModel(Object obj, boolean checkSpecialCharsExceptPassword) {
    boolean allValid = true;
    if(obj != null) {
      for(Field f : obj.getClass().getDeclaredFields()) {
        if(f.getType().isAssignableFrom(String.class)) {
          f.setAccessible(true);
          try
          {
            String val = (String)f.get(obj);
            if(val != null) {
              // If first invalid variable, set flag false
              if(allValid && !Jsoup.isValid(val, Whitelist.none())) {
                logger.info("sanitizeModel() detected invalid : " + val);
                allValid = false;
              }
              else if(allValid && checkSpecialCharsExceptPassword && !f.getName().toLowerCase().contains("password") && SPECIAL.matcher(val).find()) {
              	logger.info("sanitizeModel() detected special chars : " + val);
                allValid = false;
              }
              // Replace variables. note isValid and clean results differ e.g. spaces closing html tags.
              f.set(obj, Jsoup.clean(val, Whitelist.none()));
            }
          }
          catch(Exception ex) 
          {
            ex.printStackTrace();
          }
        }
      }
    }
    return allValid;
  }
  
  public static boolean sanitizeExternalUserModel(ExternalUser imedExternalUser) {
    boolean allValid = ModelUtil.sanitizeModel(imedExternalUser);
    for(ExternalPractice practice : imedExternalUser.getPractices()) {
      if(!ModelUtil.sanitizeModel(practice)) {
        allValid = false;
      }
    }
    return allValid;
  }
}
