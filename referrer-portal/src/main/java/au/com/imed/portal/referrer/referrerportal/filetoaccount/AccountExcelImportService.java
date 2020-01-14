package au.com.imed.portal.referrer.referrerportal.filetoaccount;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.ExternalPractice;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;

@Service
public class AccountExcelImportService {
	private Logger logger = LoggerFactory.getLogger(AccountExcelImportService.class);
	
	@Autowired
	private ReferrerAccountService accountService;
	
	@Autowired
	private ReferrerCreateAccountService createAccountService;
	
	@Autowired
	private VisageCheckerService visageCheckerService;
	
	@Autowired
	private ReferrerProviderJpaRepository repository;
	
	public File importExcel(InputStream fis, final boolean dryrun) throws Exception {
		logger.info("dry run ? " + dryrun);
		
		File tempFile = File.createTempFile("filetoaccount-", ".csv");
		logger.info("importExcel temp file : " + tempFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		writer.write("uid,created,msg\n");
		
		Workbook workbook = new XSSFWorkbook(fis);

		// Referrers
		Sheet datatypeSheet = workbook.getSheetAt(0);
		Sheet providerSheet = workbook.getSheetAt(1);
		Iterator<Row> iterator = datatypeSheet.iterator();
		ImedActiveDirectoryLdapManager imedActiveDirectoryLdapManager = new ImedActiveDirectoryLdapManager();

		boolean first = true;
		while (iterator.hasNext()) {
			Row r = iterator.next();
			if(first) {
				first = false;
				continue;
			}
			
			boolean result = true;
			String msg = "";
			String csvrow = "";
			
			ExternalUser ref = mapToReferrer(r, providerSheet);
			logger.info("Referrer : " + ref);
			
			if(ref == null) {
					result = false;
					msg = "Invalid information";
					csvrow = ref == null ? "" : ref.getUserid() + ",\"" + result + "\",\"" + msg + "\"\n";
			}
			else
			{
				String originalUid = ref.getUserid();
				// WA only
				//ref.setUid("wa" + ref.getUid());
				
				// TODO use LdapAccountCheckerService(common with Create Account) instead of AD and findGlobalByAtr ?
				if(imedActiveDirectoryLdapManager.findByMail(ref.getEmail()).size() > 0) {
					result = false;
					msg = "email already in AD";
				}else if(imedActiveDirectoryLdapManager.findByUid(ref.getUserid()).size() > 0) {
					result = false;
					msg = "uid already in AD";					
				}else if(accountService.findGlobalByAtr("uid", ref.getUserid()).size() > 0){
					result = false;
					msg = "uid already in LDAP";
				}else if(accountService.findGlobalByAtr("AHPRA", ref.getAhpraNumber()).size() > 0) {
					result = false;
					msg = "AHPRA " + ref.getAhpraNumber() + " already in LDAP";
				}else if(accountService.findGlobalByAtr("mail", ref.getEmail()).size() > 0) {
					result = false;
					msg = "email address " + ref.getEmail() + " already in LDAP";
				}else if(visageCheckerService.isUsernameTaken(ref.getUserid())) {
					result = false;
					msg = "username already in Visage";
				}else if(visageCheckerService.isAhpraTaken(ref.getUserid())) {
					result = false;
					msg = "AHPRA " + ref.getAhpraNumber() + " already in Visage";
				}else if(poviderNumbersTakenInVisage(ref).size() > 0) {
					result = false;
					msg = "Provider number already in Visage";
				}else {
					try {
						// TODO maptoprovider then set to ref then use methods in createAccountService to convert to DB
						List<ReferrerProviderEntity> providers = getProvidersEntities(providerSheet, originalUid);
						if(providers.size() > 0) {
							logger.info("Saving providers " + providers);
							if(!dryrun) {
								//repository.saveAll(providers); 
							}
						}else {
							logger.info("No provider for " + originalUid);
						}
						logger.info("Creating : " + ref);
						if(!dryrun) {
							//TODO createAccountService.createPortalReferrerUser(ref, ref.getUserid());
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						result = false;
						msg = "Failed to create account";
					}
				}
				csvrow = originalUid + ",\"" + result + "\",\"" + msg + "\"\n";
			}
			writer.write(csvrow);
			writer.flush();
		}
		
		writer.close();
		workbook.close();
		
		return tempFile;
	}
	
	private List<String> poviderNumbersTakenInVisage(final ExternalUser externalUser) {
		List<String> takens = new ArrayList<>(0);
		for(ExternalPractice practice : externalUser.getPractices()) {
			if(visageCheckerService.isProviderNumberTaken(practice.getProviderNumber())) {
				logger.info("Provider number " + practice.getProviderNumber() + " is in Visage");
				takens.add(practice.getProviderNumber());
			}
		}
		return takens;
	}
	
	private String getStringValue(Row row, int num) {
		return row.getCell(num) != null ? row.getCell(num).getStringCellValue() : "";
	}
	
	private ExternalUser mapToReferrer(Row row, Sheet providerSheet) {
		ExternalUser ref = null;
		try {
			String uid = getStringValue(row, 0);
			String password = getStringValue(row, 1);
			String firstName = getStringValue(row, 2);
			String lastName = getStringValue(row, 3);
			String email = getStringValue(row, 4);
			String phone = getStringValue(row, 5);
			String mobile = getStringValue(row, 6);
			String ahpra = getStringValue(row, 7);
			//String address = getStringValue(row, 8);
			if(uid.length() > 0 && password.length() > 0) {
				ref = new ExternalUser();
				ref.setUserid(uid);
				ref.setPassword(password);
				ref.setFirstName(firstName);
				ref.setLastName(lastName);
				ref.setEmail(email);
				ref.setPreferredPhone(phone);
				ref.setMobile(mobile);
				ref.setAhpraNumber(ahpra);
				ref.setAccountType("NEW_IMPORT");
				
				ref.setPractices(getExternalPractices(providerSheet, uid));
			}else {
				logger.info("Skipping - No uid or password. Empty row.");
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return ref;
	}
	
	private ExternalPractice toExternalPractice(Row row) {
		ExternalPractice entity = new ExternalPractice();
		try {
			entity.setProviderNumber(getStringValue(row, 1));
			entity.setPracticeName(getStringValue(row, 2));
			entity.setPracticePhone(getStringValue(row, 3));
			entity.setPracticeFax(getStringValue(row, 4));
			entity.setPracticeStreet(getStringValue(row, 5));
			entity.setPracticeSuburb(getStringValue(row, 6));
			entity.setPracticeState(getStringValue(row, 7));
			entity.setPracticePostcode(getStringValue(row, 8));
			entity.setPracticeAddress(getStringValue(row, 5) + " " + getStringValue(row, 6) + " " + getStringValue(row, 7) + " " + getStringValue(row, 8));
		} catch(Exception ex) {
			entity = null;
			ex.printStackTrace();
		}
		
		return entity;
	}
	
	private List<ExternalPractice> getExternalPractices(Sheet sheet, final String uid) {
		List<ExternalPractice> list = new ArrayList<>(2);
		Iterator<Row> iterator = sheet.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			Row r = iterator.next();
			if(first) {
				first = false;
				continue;
			}
			
			if(uid.equals(r.getCell(0).getStringCellValue())) {
				list.add(toExternalPractice(r));
			}
		}
		return list;
	}
	
	private ReferrerProviderEntity toProvider(Row row) {
		ReferrerProviderEntity entity = new ReferrerProviderEntity();
		try {
			entity.setUsername(getStringValue(row, 0));
			entity.setProviderNumber(getStringValue(row, 1));
			entity.setPracticeName(getStringValue(row, 2));
			entity.setPracticePhone(getStringValue(row, 3));
			entity.setPracticeFax(getStringValue(row, 4));
			entity.setPracticeStreet(getStringValue(row, 5));
			entity.setPracticeSuburb(getStringValue(row, 6));
			entity.setPracticeState(getStringValue(row, 7));
			entity.setPracticePostcode(getStringValue(row, 8));
			entity.setPracticeAddress(getStringValue(row, 5) + " " + getStringValue(row, 6) + " " + getStringValue(row, 7) + " " + getStringValue(row, 8));
		} catch(Exception ex) {
			entity = null;
			ex.printStackTrace();
		}
		
		return entity;
	}
	
	private List<ReferrerProviderEntity> getProvidersEntities(Sheet sheet, final String uid) {
		List<ReferrerProviderEntity> list = new ArrayList<>(2);
		Iterator<Row> iterator = sheet.iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			Row r = iterator.next();
			if(first) {
				first = false;
				continue;
			}
			
			if(uid.equals(r.getCell(0).getStringCellValue())) {
				list.add(toProvider(r));
			}
		}
		return list;
	}
}
