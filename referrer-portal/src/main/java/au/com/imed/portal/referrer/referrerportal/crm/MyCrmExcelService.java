package au.com.imed.portal.referrer.referrerportal.crm;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmPostcodeJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmProfileJpaRepository;

@Service
public class MyCrmExcelService {	
	private Logger logger = LoggerFactory.getLogger(MyCrmExcelService.class);
	
	@Autowired
	private CrmPostcodeJpaRepository postcodeRepository;
	@Autowired
	private CrmProfileJpaRepository profileRepository;
			
	public void saveData(InputStream fis) throws Exception {
		Workbook workbook = new XSSFWorkbook(fis);
		
		// Post code
		Sheet datatypeSheet = workbook.getSheet("SuburbPostCode");
		Iterator<Row> iterator = datatypeSheet.iterator();
		boolean first = true;
		while (iterator.hasNext()) {

			Row r = iterator.next();
			if(first) {
				first = false;
				continue;
			}
			
			String postcode = r.getCell(1).getStringCellValue();
			if(postcode != null & postcode.length() > 0) {
				try {
					CrmPostcodeEntity entity;
					List<CrmPostcodeEntity> list = postcodeRepository.findByPostcode(postcode);
					entity = list.size() > 0 ? list.get(0) : new CrmPostcodeEntity(); 
					entity.setName(r.getCell(6).getStringCellValue());	
					entity.setSuburb(r.getCell(2).getStringCellValue());
					entity.setBu(r.getCell(8).getStringCellValue());
					entity.setPostcode(postcode);
					//logger.info("Saving postcode " + postcode);
					postcodeRepository.saveAndFlush(entity);
				} catch(Exception ex) {
					ex.printStackTrace();
					logger.info("Skipping this postcode");
				}
			}
		}
    
    // CRM
    datatypeSheet = workbook.getSheet("CRM");
		iterator = datatypeSheet.iterator();
		first = true;
		while (iterator.hasNext()) {

			Row r = iterator.next();
			if(first) {
				first = false;
				continue;
			}
			
			Cell regcel = r.getCell(5);
			if(regcel != null && regcel.toString() != null && !regcel.toString().isBlank()) {
				String region = r.getCell(5).getStringCellValue();
				String name = r.getCell(0).getStringCellValue();
				List<CrmProfileEntity> list = profileRepository.findByName(name);
				CrmProfileEntity entity = list.size() > 0 ? list.get(0) : new CrmProfileEntity();
				entity.setEmail(r.getCell(2).getStringCellValue());
				entity.setName(name);
				entity.setPhone(r.getCell(1).getStringCellValue());
				entity.setRegion(region);
				//logger.info("Saving CRM " + name);
				profileRepository.saveAndFlush(entity);
			}
		}
		
	}
}
