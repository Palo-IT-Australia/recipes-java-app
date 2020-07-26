package au.com.imed.portal.referrer.referrerportal.ldap;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.VisageRequestAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.VisageRequestAuditJPARepository;

@Service
public class LdapCsvCreationService extends ABasicAccountService {
	
	@Autowired
	private VisageRequestAuditJPARepository auditRepository;
	
	public File createCsv(final boolean isAudit) throws Exception {
		LdapTemplate ldapTemplate = getReferrerLdapTemplate();

    AndFilter filter = new AndFilter();
    filter.and(new EqualsFilter("objectclass", "Person"));
    
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningObjFlag(true);
    searchControls.setReturningAttributes(new String [] {"uid", "givenName", "sn", "mail", "ahpra", "createTimeStamp", "BusinessUnit", "employeeType", "homePhone", "mobile", "physicalDeliveryOfficeName"});
    
    final String filterstr = filter.encode();
    List<String> list = ldapTemplate.search("", filterstr, searchControls, new AttributesMapper<String>() {
      @Override
      public String mapFromAttributes(Attributes attrs) throws NamingException {
        String dtstr = "";
        try
        {
          String ori = attrs.get("createTimeStamp").get(0).toString().split("/.")[0];
          dtstr = new SimpleDateFormat("dd/MM/yyyy").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(ori));
        }catch(Exception ex){
          ex.printStackTrace();
        }
        if(isAudit) {
        	// New line below after last access
          return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
              attrs.get("uid") != null && attrs.get("uid").get(0) != null ? attrs.get("uid").get(0).toString() : "",
              attrs.get("givenName") != null && attrs.get("givenName").get(0) != null ? attrs.get("givenName").get(0).toString() : "",
              attrs.get("sn") != null && attrs.get("sn").get(0) != null ? attrs.get("sn").get(0).toString() : "",
              attrs.get("mail") != null && attrs.get("mail").get(0) != null ? attrs.get("mail").get(0).toString() : "",
              attrs.get("ahpra") != null && attrs.get("ahpra").get(0) != null ? attrs.get("ahpra").get(0).toString() : "",
              attrs.get("BusinessUnit") != null && attrs.get("BusinessUnit").get(0) != null ? attrs.get("BusinessUnit").get(0).toString() : "",
              attrs.get("employeeType") != null && attrs.get("employeeType").get(0) != null ? attrs.get("employeeType").get(0).toString() : "",
              attrs.get("homePhone") != null && attrs.get("homePhone").get(0) != null ? attrs.get("homePhone").get(0).toString() : "",
              attrs.get("mobile") != null && attrs.get("mobile").get(0) != null ? attrs.get("mobile").get(0).toString() : "",
              attrs.get("physicalDeliveryOfficeName") != null && attrs.get("physicalDeliveryOfficeName").get(0) != null ? attrs.get("physicalDeliveryOfficeName").get(0).toString() : "",
              dtstr);
        }else{
          return String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
              attrs.get("uid") != null && attrs.get("uid").get(0) != null ? attrs.get("uid").get(0).toString() : "",
              attrs.get("givenName") != null && attrs.get("givenName").get(0) != null ? attrs.get("givenName").get(0).toString() : "",
              attrs.get("sn") != null && attrs.get("sn").get(0) != null ? attrs.get("sn").get(0).toString() : "",
              attrs.get("mail") != null && attrs.get("mail").get(0) != null ? attrs.get("mail").get(0).toString() : "",
              attrs.get("ahpra") != null && attrs.get("ahpra").get(0) != null ? attrs.get("ahpra").get(0).toString() : "",
              dtstr);          
        }
      }});

    File tempFile = File.createTempFile("ldap-", "-csv");
    PrintWriter printWriter = new PrintWriter(tempFile);
    if(isAudit) {
      printWriter.println("username,firstname,lastname,email,AHPRA#,BusinessUnit,AccountType,phone,mobile,address,creation time,Last Access");
    }else{
      printWriter.println("username,firstname,lastname,email,AHPRA#,creation time");      
    }
    for(String l : list) {
    	if(isAudit) {
    		printWriter.print(l + "," + getLastAccess(l) + "\n");
    	} else {
    		printWriter.print(l);    		
    	}
    }
    printWriter.close();
    return tempFile;
	}
	
	private String getLastAccess(final String csvLine) {
		String lastAccess = "";
		try {
			String username = csvLine.split(",")[0].replaceAll("\"", "");
			System.out.println("username " + username);
			
			if(!StringUtil.isBlank(username)) {
				VisageRequestAuditEntity audit = auditRepository.findFirstByUsernameOrderByAuditAtDesc(username);
				System.out.println(audit);
				if(audit != null) {
					Date accori = audit.getAuditAt();
					lastAccess = accori != null ? new SimpleDateFormat("dd/MM/yyyy HH:mm").format(accori) : "";
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lastAccess;
	}
}
