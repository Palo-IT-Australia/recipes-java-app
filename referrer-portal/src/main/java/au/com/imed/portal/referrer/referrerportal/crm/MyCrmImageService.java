package au.com.imed.portal.referrer.referrerportal.crm;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmProfileJpaRepository;

@Service
public class MyCrmImageService {
	@Autowired
	private CrmProfileJpaRepository profileRepository;
			
	private Logger logger = LoggerFactory.getLogger(MyCrmImageService.class);
	
	public void saveImages(final List<MultipartFile> profiles) throws IOException {
		for(MultipartFile file : profiles) {
			String fname = file.getOriginalFilename();
			logger.info("file name: " + fname);
			String name = fname.split("\\.")[0];
			List<CrmProfileEntity> list = profileRepository.findByName(name);
			if(list.size() > 0) {
				CrmProfileEntity entity = list.get(0);
				String bsf = new String(Base64.getEncoder().encode(file.getBytes()), "UTF-8");
				entity.setImgstr(bsf);
				logger.info("Saving Image name " + name);
				profileRepository.saveAndFlush(entity);
			}
			else
			{
				logger.info("No matching profile " + name);
			}
		}
	}
	
	public String getImageString(final String name) {
		List<CrmProfileEntity> list = profileRepository.findByName(name);
		if(list.size() > 0) {
			return "data:image/png;base64," + list.get(0).getImgstr();
		}
		else
		{
			return "";
		}
	}
}
