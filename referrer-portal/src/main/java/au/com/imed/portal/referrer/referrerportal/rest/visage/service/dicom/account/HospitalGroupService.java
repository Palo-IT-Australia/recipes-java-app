package au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.AccountUid;


@Service
public class HospitalGroupService extends ABasicAccountService {
  private static final LdapQuery QUERY_MEMBER = query().where("cn").is("HospitalAccess").and("objectclass").is("groupOfUniqueNames");
  private static final String DN_PRE = "uid="; 
  private static final String DN_POST = ",ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au";

  public List<AccountUid> list() {
    List<AccountUid> list = new ArrayList<>();
    try
    {
      LdapTemplate template = getApplicationsLdapTemplate();
      List<List<String>> names = template.search(QUERY_MEMBER, new MemberContextMapper());
      for(String name : names.get(0)) {
        String uid = name.replace(DN_PRE, "").replace(DN_POST, "");
        AccountUid obj = new AccountUid();
        obj.setUid(uid);
        list.add(obj);
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    return list;
  }
  
  public boolean add(final String uid) {
    boolean isDone = false;
    if(uid != null && uid.length() > 0) {
      try {
        if(isReferrer(uid)) {
          LdapTemplate template = getApplicationsLdapTemplate();
          List<Name> groups = template.search(QUERY_MEMBER, new PersonContextMapper()); // Should be one
          List<List<String>> members = template.search(QUERY_MEMBER, new MemberContextMapper());
          if(members.get(0).contains(DN_PRE + uid + DN_POST)) {
            System.out.println("Skipping already member " + uid);
          }
          else {
            ModificationItem[] modItems = new ModificationItem[] {
                new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("uniquemember", DN_PRE + uid + DN_POST))
            };
            template.modifyAttributes(groups.get(0), modItems);
            System.out.println("Added hospital " + uid);
            isDone = true;
          }
        }else{
          System.out.println("Not referrer" + uid);
        }
      }catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    return isDone;
  }
  
  public boolean remove(final String uid) {
    boolean isDone = false;
    if(uid != null && uid.length() > 0) {
      try {
        if(isReferrer(uid)) {
          LdapTemplate template = getApplicationsLdapTemplate();
          List<Name> groups = template.search(QUERY_MEMBER, new PersonContextMapper()); // Should be one
          List<List<String>> members = template.search(QUERY_MEMBER, new MemberContextMapper());
          if(!members.get(0).contains(DN_PRE + uid + DN_POST)) {
            System.out.println("Skipping not yet member " + uid);
          }
          else {
            ModificationItem[] modItems = new ModificationItem[] {
                new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("uniquemember", DN_PRE + uid + DN_POST))
            };
            template.modifyAttributes(groups.get(0), modItems);
            System.out.println("Removed hospital " + uid);
            isDone = true;
          }
        }else{
          System.out.println("Not referrer" + uid);
        }
      }catch(Exception ex) {
        ex.printStackTrace();
      }
    }
    return isDone;
  }

  private boolean isReferrer(final String uid) {
    boolean is = false;
    try
    {
      if(uid != null && uid.length() > 0) {
        is = getReferrerLdapTemplate().search(query().where("uid").is(uid), new PersonContextMapper()).size() > 0;
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
    return is;
  }

  private static class MemberContextMapper extends AbstractContextMapper<List<String>> {
    public List<String> doMapFromContext(DirContextOperations context) {
      List<String> members = new ArrayList<>();
      try {
        NamingEnumeration<?> ne = context.getAttributes().get("uniquemember").getAll();
        while(ne.hasMore()) {
          String m = ne.next().toString();
          //System.out.println(m);
          members.add(m);
        }
      } catch (NamingException e) {
        e.printStackTrace();
      }
      return members;
    }
  }

  private static class PersonContextMapper extends AbstractContextMapper<Name> {
    public Name doMapFromContext(DirContextOperations context) {
      return context.getDn();
    }
  }
}
