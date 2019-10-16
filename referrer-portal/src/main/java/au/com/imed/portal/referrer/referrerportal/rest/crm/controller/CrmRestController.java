package au.com.imed.portal.referrer.referrerportal.rest.crm.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.crm.MyCrmImageService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmPostcodeJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmProfileJpaRepository;

@RestController
@RequestMapping("/crmrest")
public class CrmRestController {
	private Logger logger = LoggerFactory.getLogger(CrmRestController.class);

	@Autowired
	private CrmProfileJpaRepository profileRepository;
	
	@Autowired
	private CrmPostcodeJpaRepository postcodeRepository;
	
	@Autowired
	private MyCrmImageService imageService;
	
	/**
	 * 
	 * @param word fuzzy search word. Length 3 or more chars.
	 * @return
	 */
	@GetMapping("/search")
	public Set<CrmProfileEntity> searchCrms(@RequestParam("word") String word) {
		Set<CrmProfileEntity> list = new HashSet<CrmProfileEntity>();
		if(word != null && word.length() >= 3) {
			List<CrmPostcodeEntity> postcodes = postcodeRepository.findByPostcodeOrSuburbLike(word, word + "%");
			for(CrmPostcodeEntity pc : postcodes) {
				List<CrmProfileEntity> profiles = profileRepository.findByName(pc.getName());
				for(CrmProfileEntity profile : profiles) {
					profile.setPostcode(pc.getPostcode());
					list.add(profile);
				}
			}
		}
		else
		{
			logger.info("Search word too short.");
		}
		return list;
	}
	
	@GetMapping("/image")
	public ResponseEntity<byte[]> getImage(@RequestParam("id") int id, HttpServletResponse response) {
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			CrmProfileEntity entity = profileRepository.findById(id).orElse(null);
			if (entity != null) {
				byte[] filebytes = imageService.getImage(id);
				if (filebytes != null) {
					responseEntity = new ResponseEntity<>(filebytes, HttpStatus.OK);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return responseEntity;
	}
}
