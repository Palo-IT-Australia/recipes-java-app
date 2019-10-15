package au.com.imed.portal.referrer.referrerportal.rest.crm.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	
	/**
	 * 
	 * @param word fuzzy search word. Length 3 or more chars.
	 * @return
	 */
	@GetMapping("/search")
	public List<CrmProfileEntity> searchCrms(@RequestParam("word") String word) {
		List<CrmProfileEntity> list = new ArrayList<CrmProfileEntity>();
		if(word != null && word.length() >= 3) {
			List<CrmPostcodeEntity> postcodes = postcodeRepository.findByPostcodeOrSuburbLike(word, "%" + word + "%");
				list = postcodes.stream()
					.map(ps -> profileRepository.findByName(ps.getName()))
					.flatMap(List::stream)
					.collect(Collectors.toList());
		}
		else
		{
			logger.info("Search word too short.");
		}
		return list;
	}
}
