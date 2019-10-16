package au.com.imed.portal.referrer.referrerportal.email;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerActivationEntity;
import au.com.imed.portal.referrer.referrerportal.model.ExternalPractice;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;

public class UserMessageUtil {
  public static final String ADMIN_USER_EMAIL = "Hidehiro.Uehara@i-med.com.au";

  public static String getPasswordChangedBody(String userid) {
      StringBuffer sb = new StringBuffer();

      sb.append("This email is to notify that your I-MED Online password for user id " + userid + " has been updated.\n");
      sb.append("If you did not request this change, please contact us on " + getSupportPhone() + " immediately.\n");
      sb.append(getSignature());

      return sb.toString();
  }

  public static String getAccountChangeBody(String userid) {
      StringBuffer sb = new StringBuffer();

      sb.append("The account details for user: " + userid + "\n");
      sb.append("have been updated. \n");
      sb.append("\n");
      sb.append("The user account willnow reflect the changes.");

      return sb.toString();
  }

  public static String getAccountLockedBody(String userid) {
      StringBuffer sb = new StringBuffer();

      sb.append("The account for user: " + userid + "\n");
      sb.append("has been locked. \n");
      sb.append("\n");
      sb.append("Please contact the help desk to unlock this account.");

      return sb.toString();
  }

  public static String getAccountUnLockedBody(String userid) {
      StringBuffer sb = new StringBuffer();

      sb.append("The account for user: " + userid + "\n");
      sb.append("has been unlocked. \n");
      sb.append("\n");
      sb.append("Please continue to use the account as required.");

      return sb.toString();
  }

  public static String getNewAccountCreatedBody(ExternalUser imedExternalUser) {
      StringBuffer sb = new StringBuffer();

      sb.append("A new account application for user: " + imedExternalUser.getUserid() + "\n");
      sb.append("has been created. \n");
      sb.append("\n");
      sb.append("First name: " + imedExternalUser.getFirstName() + "\n");
      sb.append("Last name: " + imedExternalUser.getLastName() + "\n");
      sb.append("Email: " + imedExternalUser.getEmail() + "\n");
      sb.append("AHPRA: " + imedExternalUser.getAhpraNumber() + "\n");
      sb.append("Phone: " + imedExternalUser.getPreferredPhone() + "\n");
      sb.append("Mobile: " + imedExternalUser.getMobile() + "\n");
      sb.append("Practices: \n");
      for (ExternalPractice practice:
           imedExternalUser.getPractices()) {
          sb.append("Practice Name: " + practice.getPracticeName() + "\n");
          sb.append("Provider Number: " + practice.getProviderNumber() + "\n");
          sb.append("Practice Address: " + practice.getPracticeAddress() + "\n");
          sb.append("Practice Phone: " + practice.getPracticePhone() + "\n");
          sb.append("Practice Fax: " + practice.getPracticeFax() + "\n\n");
      }
      sb.append(
          "Contact regarding Advanced InteleViewer: " + imedExternalUser.getContactAdvanced()
              + "\n");
      sb.append("\n");
      sb.append("Opt to go filmless: " + imedExternalUser.getFilmless() + "\n");
      sb.append("\n");
      sb.append("Please browse to Portal User Approval to action this request.");

      return sb.toString();
  }
  
  
  public static String getNotLoginPromptBody(final ReferrerActivationEntity entity) {
  	StringBuffer sb = new StringBuffer();

    sb.append("A new I-MED Online 2.0 account : " + entity.getUid() + " has not logged in yet.\n");
    sb.append("\n");
    sb.append("First name: " + entity.getFirstName() + "\n");
    sb.append("Last name: " + entity.getLastName() + "\n");
    sb.append("Email: " + entity.getEmail() + "\n");
    sb.append("AHPRA: " + entity.getAhpra() + "\n");
    sb.append("Mobile: " + entity.getMobile() + "\n");
    sb.append("Approved at: " + entity.getActivatedAt() + "\n");
    
    return sb.toString();
  }
  
  public static String getTandcPromptBody(final LdapUserDetails entity) {
  	StringBuffer sb = new StringBuffer();

    sb.append("A new I-MED Online 2.0 account : " + entity.getUid() + " has not accepted terms and conditions after logging in.\n");
    sb.append("\n");
    sb.append("First name: " + entity.getGivenName() + "\n");
    sb.append("Last name: " + entity.getSurname() + "\n");
    sb.append("Email: " + entity.getEmail() + "\n");
    sb.append("AHPRA: " + entity.getAhpra() + "\n");
    sb.append("Mobile: " + entity.getMobile() + "\n");
    
    return sb.toString();
  }

  public static String getNewAccountBody() {
      StringBuffer sb = new StringBuffer();

      sb.append("Your request for an I-MED Online account has been received. \n");
      sb.append("A member of our Referrer Support Team will contact you shortly to confirm the details.\n");
      sb.append("If you require immediate access please call " + getSupportPhone() + "\n");
      sb.append(getSignature());

      return sb.toString();
  }

    public static String getAccountMailIssueBody(String user) {
        StringBuffer sb = new StringBuffer();

        sb.append("There was an issue emailing user: " + user + "\n");
        sb.append("Please contact the user to correct their email address details.");

        return sb.toString();
    }

    public static String getRegistrationErrorBody(Exception e, String stage, ExternalUser user) {
        StringBuffer sb = new StringBuffer();

        sb.append("There was an issue while processing a new account registration: \n");
        sb.append("Stage: " + stage);
        sb.append("Form: \n");
        sb.append(user.toString() + "\n");
        sb.append("Exception: \n");
        sb.append(e.getMessage());

        return sb.toString();
    }

    public static String getResetPasswordBody(StageUser user, String password) {
        StringBuffer sb = new StringBuffer();

        sb.append("As requested, your I-MED Online password has been reset. Please log in with the following:\n\n");
        //sb.append("User id: " + user.getUid() + "\n");
        sb.append("Password: " + password + "\n\n");
        sb.append("Browse to https://i-medonline.com.au to login.\n");
        sb.append("If you did not request this change, please contact us on " + getSupportPhone() + " as soon as possible.\n");
        sb.append(getSignature());

        return sb.toString();
    }

    public static String getAccountApprovedBody(StageUser user) {
        StringBuffer sb = new StringBuffer();

        sb.append("Welcome to I-MED Online.\n");
        sb.append("We are pleased to advise your account has been approved - User id " + user.getUid() + "\n");
        sb.append("Please follow this link https://i-medonline.com.au to login or copy and paste it into your browser.\n");
        sb.append("You may like to save the Homepage to your favourites toolbar for easy reference.\n");
        sb.append("If you have any questions please call our Support line on " + getSupportPhone() + "\n");
        sb.append(getSignature());

        return sb.toString();
    }

    public static String getDetailsChangedBody(StageUser user) {
        StringBuffer sb = new StringBuffer();

        sb.append("The account details for user id " + user.getUid() + " have been updated as request.\n");
        sb.append("If you did not request this change, please contact us on " + getSupportPhone() + " as soon as possible.\n");
        sb.append(getSignature());

        return sb.toString();
    }

    public static String getDeclinedBody(String reason) {
        StringBuffer sb = new StringBuffer();

        sb.append("Your I-MED Online account application has been declined.\n");
        sb.append(reason != null && reason.length() > 0 ? "\nComment:\n" + reason : "");
        sb.append(getSignature());

        return sb.toString();
    }

    public static String getAccountMessage(String message) {
        StringBuffer sb = new StringBuffer();

        sb.append(message);
        sb.append(getSignature());

        return sb.toString();
    }

    private static String getSignature() {
        StringBuffer sb = new StringBuffer();
        sb.append("\n");
        sb.append("Regards\n");
        sb.append("I-MED Online Support Team");

        return sb.toString();
    }

    private static String getSupportPhone() {
        StringBuffer sb = new StringBuffer();
        sb.append("1300 147 852");
        return sb.toString();
    }
}
