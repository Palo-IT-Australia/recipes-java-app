package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.SnapscanBooking;

@Service
public class ElectronicReferralSnapscanService {
	private Logger logger = LoggerFactory.getLogger(ElectronicReferralSnapscanService.class);

	@Value("${imed.ereferral.snapscan.rest.url}")
	private String SNAPSCAN_API_ROOT_URL;

	@Value("${imed.ereferral.snapscan.rest.token}")
	private String SNAPSCAN_API_TOKEN;
	
	public void postEreferral(ElectronicReferralForm electronicReferralForm) throws Exception {
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		
		HttpEntity<SnapscanBooking> request = new HttpEntity<>(toSnapscanBooking(electronicReferralForm), headers);
		ResponseEntity<JSONObject> entity = restTemplate.postForEntity(SNAPSCAN_API_ROOT_URL + "/booking_links?apiReferrer=imed&apiKey=" + SNAPSCAN_API_TOKEN, request, JSONObject.class);
		logger.info("postEreferral() api response {} {}", entity.getStatusCode(), entity.getBody());
		if(HttpStatus.OK.equals(entity.getStatusCode())) {
			logger.info("Snapscan ereferral api succeeded.");
		} else {
			throw new RuntimeException("Snapscan ereferral api returned error code : " + entity.getStatusCodeValue());
		}
	}
	
	private SnapscanBooking toSnapscanBooking(ElectronicReferralForm form) {
		SnapscanBooking book = new SnapscanBooking();
		book.setTitle("");
		book.setGender(form.getPatientGender());
		String firstName = form.getPatientName().split(" ")[0];		
		book.setFirstName(firstName);
		book.setLastName(form.getPatientName().replace(firstName, "").trim());
		book.setDateOfBirth(toOriginalDateFormat(form.getPatientDob()));
		book.setContactNumber(form.getPatientPhone());
		book.setAddress(form.getPatientStreet() + " " + form.getPatientSuburb() + " " + form.getPatientPostcode() + " " + form.getPatientState());
		book.setExamNotes(nonNull(form.getExamDetails()));
		book.setNotes(nonNull(form.getClinicalDetails()));
		book.setReferringDoctorName(form.getDoctorName());
		book.setReferringDoctorPhone(nonNull(form.getDoctorPhone()));
		book.setReferringDoctorEmail(nonNull(form.getDoctorEmail()));
		book.setReferringDoctorAddress(form.getDoctorStreet() + " " + form.getDoctorSuburb() + " " + form.getDoctorPostcode() + " " + form.getDoctorState());
		book.setReferringPracticeName(nonNull(form.getDoctorPracticeName()));
		book.setReferringDoctorNumber(nonNull(form.getDoctorProviderNumber()));
		book.setCopyTo(nonNull(form.getCcDoctorName()));
		logger.info("snapscan booking " + book);
		return book;
	}
	
	private String nonNull(String ori) {
		return ori != null ? ori : "";
	}
	
	private String toOriginalDateFormat(String ausDate) {
		String dt  = "";
		try {
			dt = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("dd/MM/yyyy").parse(ausDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dt;
	}
	
	@SuppressWarnings("unchecked")
	private String getToken() throws Exception {
		String token = null;
		JSONObject req = new JSONObject();
		req.put("token", SNAPSCAN_API_TOKEN);
		ResponseEntity<JSONObject> entity = new RestTemplate().postForEntity(SNAPSCAN_API_ROOT_URL + "/token", req, JSONObject.class);
		if(HttpStatus.OK.equals(entity.getStatusCode())) {
			logger.info("Snapscan token api succeeded.");
			token = (String) entity.getBody().get("token");
			logger.info("getToken() token = " + token); 
		} else {
			throw new RuntimeException("Snapscan token api returned error code : " + entity.getStatusCodeValue());
		}
		return token;
	}
	
//	public static void main(String args[]) throws Exception {
//		ElectronicReferralForm electronicReferralForm = new ElectronicReferralForm();
//		
//		new ElectronicReferralSnapscanService().postEreferral(electronicReferralForm);
//	}
}
