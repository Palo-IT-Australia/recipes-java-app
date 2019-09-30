package au.com.imed.portal.referrer.referrerportal.rest.editor.controller;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.RadiologistEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository.ClinicContentRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository.RadiologistRepository;
import au.com.imed.portal.referrer.referrerportal.rest.editor.model.ClinicContentSaveModel;
import au.com.imed.portal.referrer.referrerportal.rest.editor.model.RadiologistSaveModel;
import au.com.imed.portal.referrer.referrerportal.rest.editor.model.SimpleResultModel;

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
  
  @GetMapping("/getRadiologistImage")
  public void getRadiologistImage(HttpServletResponse response, @RequestParam("id") int id) 
  {
    try {
      byte [] imgbytes = radiologistRepository.getOne(id).getImgbin();
      if(imgbytes != null) {
        response.setContentType("image/png");
        OutputStream out = response.getOutputStream();
        out.write(imgbytes);
        out.flush();
        out.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @RequestMapping("/putRadiologist")
  public ResponseEntity<SimpleResultModel> putRadiologist(@RequestBody RadiologistEntity entity) 
  {
    System.out.println("putRadiologist() entity id = " + entity.getId());
    RadiologistEntity current = radiologistRepository.getOne(entity.getId());
    entity.setImgbin(current.getImgbin());
    radiologistRepository.saveAndFlush(entity);
    System.out.println("Updated " + entity.getName());

    return new ResponseEntity<SimpleResultModel>(new SimpleResultModel("Updated " + entity.getName(), "sucess"), HttpStatus.OK);
  }
  
  @RequestMapping("/saveRadiologist")
  public ResponseEntity<SimpleResultModel> saveRadiologist(@RequestBody RadiologistSaveModel model) 
  {
    String imgstr = model.getImgstr();
    RadiologistEntity entity = model.getRadiologist();
    int id = entity.getId();
    if(id > 0) {
      // update
      if(imgstr != null && imgstr.length() > 0) {
        entity.setImgbin(generateImageBytes(imgstr));        
      }
      else {
        RadiologistEntity current = radiologistRepository.getOne(entity.getId());
        entity.setImgbin(current.getImgbin()); 
      }
    } else {
      // new
      if(imgstr != null && imgstr.length() > 0) {
        entity.setImgbin(generateImageBytes(imgstr));
      }
    }
    radiologistRepository.saveAndFlush(entity);

    return new ResponseEntity<SimpleResultModel>(new SimpleResultModel("Saved " + entity.getName(), "sucess"), HttpStatus.OK);
  }
  
  @RequestMapping("/deleteRadiologist")
  public ResponseEntity<SimpleResultModel> deleteRadiologist(@RequestParam(value="id") int id) 
  {
    radiologistRepository.deleteById(id);

    return new ResponseEntity<SimpleResultModel>(new SimpleResultModel("Deleted", "sucess"), HttpStatus.OK);
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
	
	@Transactional
  @GetMapping("/getClinicImage")
  public void getClinicImage(HttpServletResponse response, @RequestParam(value="id") int id) 
  {
    try {
      byte [] imgbytes = clinicContentRepository.getOne(id).getImgbin();  
      if(imgbytes != null) {
          response.setContentType("image/png");
          OutputStream out = response.getOutputStream();
          out.write(imgbytes);
          out.flush();
          out.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @GetMapping("/getClinicInfofile")
  public void getClinicInfofile(HttpServletResponse response, @RequestParam(value="id") int id) {
    try{
      ClinicContentEntity entity = clinicContentRepository.getOne(id);
      byte [] filebytes = entity.getInfofilebin();
      if(filebytes != null) {
        response.setContentType("application/octet-stream");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + entity.getInfofilename());
        
        OutputStream out = response.getOutputStream();
        out.write(filebytes);
        out.flush();
        out.close();
      }
    }catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  
  @RequestMapping("/putClinic")
  public ResponseEntity<SimpleResultModel> putClinic(@RequestBody ClinicContentEntity entity) 
  {
    System.out.println("putClinic() entity id = " + entity.getId());
    ClinicContentEntity current = clinicContentRepository.getOne(entity.getId());
    entity.setImgbin(current.getImgbin());
    entity.setInfofilebin(current.getInfofilebin());
    entity.setInfofilename(current.getInfofilename());
    clinicContentRepository.saveAndFlush(entity);
    System.out.println("Updated " + entity.getName());

    return new ResponseEntity<SimpleResultModel>(new SimpleResultModel("Updated " + entity.getName(), "sucess"), HttpStatus.OK);
  }
  
  @RequestMapping("/saveClinic")
  public ResponseEntity<SimpleResultModel> saveClinic(@RequestBody ClinicContentSaveModel model) 
  {
    String imgstr = model.getImgstr();
    String infofilestr = model.getInfofilestr();
    ClinicContentEntity entity = model.getClinic();
    int id = entity.getId();
    if(id > 0) {
      // update
      ClinicContentEntity current = null;
      if(imgstr != null && imgstr.length() > 0) {
        entity.setImgbin(generateImageBytes(imgstr));        
      }
      else {
        current = clinicContentRepository.getOne(entity.getId());
        entity.setImgbin(current.getImgbin()); 
      }
      
      if(infofilestr != null && infofilestr.length() > 0) {
        entity.setInfofilebin(generateImageBytes(infofilestr));
      }
      else {
        if(current == null) { 
          current = clinicContentRepository.getOne(entity.getId());
        }
        entity.setInfofilebin(current.getInfofilebin()); 
        entity.setInfofilename(current.getInfofilename());
      }
    } else {
      // new
      if(imgstr != null && imgstr.length() > 0) {
        entity.setImgbin(generateImageBytes(imgstr));
      }
      if(infofilestr != null && infofilestr.length() > 0) {
        entity.setInfofilebin(generateImageBytes(infofilestr));
      }
    }
    clinicContentRepository.saveAndFlush(entity);

    return new ResponseEntity<SimpleResultModel>(new SimpleResultModel("Saved " + entity.getName(), "sucess"), HttpStatus.OK);
  }
  
  @RequestMapping("/deleteClinic")
  public ResponseEntity<SimpleResultModel> deleteClinic(@RequestParam(value="id") int id) 
  {
    clinicContentRepository.deleteById(id);

    return new ResponseEntity<SimpleResultModel>(new SimpleResultModel("Deleted", "sucess"), HttpStatus.OK);
  }
  
  //
  // Utils
  //  
  private byte [] generateImageBytes(String base) {
    return Base64.decodeBase64(base.split(",")[1]);
  }
}
