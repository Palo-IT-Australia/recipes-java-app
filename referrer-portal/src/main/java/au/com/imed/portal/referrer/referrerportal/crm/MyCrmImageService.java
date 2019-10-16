package au.com.imed.portal.referrer.referrerportal.crm;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmProfileJpaRepository;

@Service
public class MyCrmImageService {
	private final static String PNG = "data:image/png;base64,";
	
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
				entity.setImgstr(PNG + bsf);
				logger.info("Saving Image name " + name);
				profileRepository.saveAndFlush(entity);
			}
			else
			{
				logger.info("No matching profile " + name);
			}
		}
	}
	
	public byte [] getImage(final int id) {
		byte [] imgbin = null;
		CrmProfileEntity entity = profileRepository.findById(id).orElse(null);
		if(entity != null) {
			String imgstr = entity.getImgstr();
			if(imgstr != null) {
				imgbin = DatatypeConverter.parseBase64Binary(imgstr.replace(PNG, ""));
			}else {
				imgbin = DatatypeConverter.parseBase64Binary("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");
			}
		}
		return imgbin;
	}
}
