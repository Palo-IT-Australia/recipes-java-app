package au.com.imed.portal.referrer.referrerportal.csv;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.UserPreferencesJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.UserPreferencesEntity;

@Service
public class PreferencesCsvService {
	@Autowired
	private UserPreferencesJPARepository repository;
	
	public File createCsv() throws Exception {
		File tempFile = File.createTempFile("pref-", "-csv");
    PrintWriter printWriter = new PrintWriter(tempFile);    
    printWriter.println("id,username,autoimg,notify");
    List<UserPreferencesEntity> list = repository.findByAutoimgOrNotify("VM", "YES");
    for(UserPreferencesEntity entity : list) {
      printWriter.print(String.format("\"%s\",\"%s\",\"%s\",\"%s\"\n",
      		entity.getId(),
      		entity.getUsername(),
      		entity.getAutoimg(),
      		entity.getNotify()));
    }
    printWriter.close();
    return tempFile;
	}
}
