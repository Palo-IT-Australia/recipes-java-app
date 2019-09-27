package au.com.imed.portal.referrer.referrerportal.rest.editor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.RadiologistEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository.ClinicContentRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository.RadiologistRepository;

@RestController
@RequestMapping("/editorrest/dbmanager")
public class EditorDbManagerController {
	@Autowired
	private ClinicContentRepository clinicContentRepository;
	
	@Autowired
	RadiologistRepository radiologistRepository;

	//
  // Radiologist DB
  //
  @GetMapping("getRadiologistList")
  public ResponseEntity<List<RadiologistEntity>> getRadiologistList() 
  {
    List<RadiologistEntity> list = radiologistRepository.findAll();
    return new ResponseEntity<List<RadiologistEntity>>(list, HttpStatus.OK);
  }
  
	//
	//  Clinic DB
	//
	@GetMapping("/getClinicList")
	public ResponseEntity<List<ClinicContentEntity>> getClinicList() 
	{
		List<ClinicContentEntity> list = clinicContentRepository.findAll();
		return new ResponseEntity<List<ClinicContentEntity>>(list, HttpStatus.OK);
	}
}
