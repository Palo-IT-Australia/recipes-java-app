package au.com.imed.portal.referrer.referrerportal.rest.clinic.controller;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentSummaryEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.model.Appointment;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository.ClinicContentRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository.ClinicContentSummaryRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.service.AppointmentService;
import au.com.imed.portal.referrer.referrerportal.model.AccountPatientLdap;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;
import au.com.imed.portal.referrer.referrerportal.utils.ModelUtil;

@RestController
@RequestMapping("/rest")
public class ClinicRestController {

	@Autowired
	private ClinicContentRepository clinicRepository;

	@Autowired
	private ClinicContentSummaryRepository clinicSummaryRepository;

	@Autowired
	private PortalAccountService accountService;

	@Autowired
	private AppointmentService appointmentService;

	@GetMapping("/image")
	public ResponseEntity<byte[]> getClinicImage(@RequestParam("id") int id, HttpServletResponse response) {
		ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			ClinicContentEntity entity = clinicRepository.findById(id).orElse(null);
			if (entity != null) {
				byte[] filebytes = entity.getImgbin();
				if (filebytes != null) {
					responseEntity = new ResponseEntity<>(filebytes, HttpStatus.OK);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return responseEntity;
	}

	@GetMapping("/infopdf")
	public void getInformationPdf(@RequestParam("id") int id, HttpServletResponse response) {
		try {
			ClinicContentEntity entity = clinicRepository.findById(id).orElse(null);
			if (entity != null) {
				byte[] filebytes = entity.getInfofilebin();
				if (filebytes != null) {
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "inline; filename=" + entity.getInfofilename());

					OutputStream out = response.getOutputStream();
					out.write(filebytes);
					out.flush();
					out.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@GetMapping("/find")
	public ResponseEntity<List<ClinicContentSummaryEntity>> findClinics(@RequestParam("lat") double lat,
			@RequestParam("lon") double lon, @RequestParam(value = "mod", required = false) String modality) {
		List<ClinicContentSummaryEntity> clinics;
		if (modality != null && modality.length() > 0) {
			clinics = clinicSummaryRepository.findByProceduresContaining(modality);
		} else {
			clinics = clinicSummaryRepository.findAll();
		}

		List<ClinicContentSummaryEntity> filtered = clinics.stream()
				.filter(c -> assignDistance(c, lat, lon) < PortalConstant.FINDER_RADIUS).sorted((c1, c2) -> {
					if (c1.getDistance() > c2.getDistance())
						return 1;
					if (c1.getDistance() == c2.getDistance())
						return 0;
					else
						return -1;
				}).limit(PortalConstant.MAX_CLINICS).collect(Collectors.toList());

		return new ResponseEntity<>(filtered, HttpStatus.OK);
	}

	@GetMapping("/one")
	public ResponseEntity<ClinicContentEntity> getClinic(@RequestParam("id") int id) {
		ClinicContentEntity entity = clinicRepository.findById(id).orElse(null);
		return new ResponseEntity<ClinicContentEntity>(entity, entity != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	}

	/**
	 * This API supposed to return current user details to pre-populate patient
	 * information in the appointment page but here the current user is referrer
	 * returning not implemented
	 * 
	 * @param authentication
	 * @return
	 */
	@GetMapping("/userldap")
	public ResponseEntity<AccountPatientLdap> getUserLdap(
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		return (new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED));
	}

	/**
	 * This API does the same thing as appointment endpoint. But this is mainly for
	 * third party people to use in their app (like Creative Factory for UX dev)
	 * 
	 * @param appointment
	 * @param referral
	 * @param authentication
	 * @return
	 */
	@PostMapping("/booking")
	public ResponseEntity<String> postBooking(@RequestPart Appointment appointment,
			@RequestPart("referral") @Nullable MultipartFile referral,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		if (AuthenticationUtil.getAuthenticatedUserName(authentication) != null) {
			return makeAppointment(appointment, referral);
		} else {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/appointment")
	public ResponseEntity<String> postAppointment(@RequestPart("appointment") Appointment appointment,
			@RequestPart("referral") @Nullable MultipartFile referral) {
		return makeAppointment(appointment, referral);
	}

	private double assignDistance(ClinicContentSummaryEntity clinic, double lat2, double lng2) {
		double lat1 = Double.parseDouble(clinic.getLat());
		double lng1 = Double.parseDouble(clinic.getLon());
		double earthRadius = 6371.00; // Earth radius in km
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);

		clinic.setDistance(dist);
		return dist;
	}

	private ResponseEntity<String> makeAppointment(Appointment appointment, MultipartFile referral) {
		if (ModelUtil.sanitizeModel(appointment) && appointmentService.validAppointment(appointment)) {
			try {
				appointmentService.sendEmail(appointment, referral);
				return new ResponseEntity<String>(HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

}
