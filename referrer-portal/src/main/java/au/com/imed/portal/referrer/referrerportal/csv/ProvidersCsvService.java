package au.com.imed.portal.referrer.referrerportal.csv;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.util.StringConversionUtil;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;

@Service
public class ProvidersCsvService {
	@Autowired
	private ReferrerProviderJpaRepository repository;
	
	public File createCsv() throws Exception {
		File tempFile = File.createTempFile("provider-", "-csv");
    PrintWriter printWriter = new PrintWriter(tempFile);    
    printWriter.println("id,username,provider_number,practice_name,practice_phone,practice_fax,practice_address,practice_street,practice_suburb,practice_state,practice_postcode");
    List<ReferrerProviderEntity> list = repository.findAll();
    for(ReferrerProviderEntity entity : list) {
      printWriter.print(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
      		entity.getId(),
      		StringConversionUtil.nonQuote(entity.getUsername()),
      		StringConversionUtil.nonQuote(entity.getProviderNumber()),
   				StringConversionUtil.nonQuote(entity.getPracticeName()),
   				StringConversionUtil.nonQuote(entity.getPracticePhone()),
   				StringConversionUtil.nonQuote(entity.getPracticeFax()),
   				StringConversionUtil.nonQuote(entity.getPracticeAddress()),
   				StringConversionUtil.nonQuote(entity.getPracticeStreet()),
   				StringConversionUtil.nonQuote(entity.getPracticeSuburb()),
   				StringConversionUtil.nonQuote(entity.getPracticeState()),
   				StringConversionUtil.nonQuote(entity.getPracticePostcode())
   		));
    }
    printWriter.close();
    return tempFile;
	}
}
