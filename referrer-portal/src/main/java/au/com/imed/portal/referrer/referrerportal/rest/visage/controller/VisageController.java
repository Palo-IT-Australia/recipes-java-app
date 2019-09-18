package au.com.imed.portal.referrer.referrerportal.rest.visage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.syslog.ReferrerEvent;
import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.common.util.StringConversionUtil;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.ReportFcmTokenEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.ReportNotificationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.ReportFcmTokenJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.ReportNotificationJPARepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.HospitalOrderSummary;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.HospitalUserPreferences;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Order;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Patient;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.PatientHistory;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.PatientOrder;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.RefreshedToken;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.ReportNotify;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.ReportNotifyRegister;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.SearchHospitalOrders;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.SearchOrders;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Tokens;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.UserPreferences;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.UsernamePassword;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountEmail;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AttachmentService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetOrderService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetPatientOrdersService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetPatientService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.PatientHistoryService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.PdfReportService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.ReportService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.SearchHospitalOrderSummaryService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.SearchOrdersService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.UserPreferencesService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.ViewImageService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.DicomService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;
import au.com.imed.portal.referrer.referrerportal.service.ReferrerPortalRestApiSyslog;

@RestController
@RequestMapping("/imedvisage/v1")
public class VisageController {
	private Logger logger = LoggerFactory.getLogger(VisageController.class);

	@Autowired
	private AuditService auditService;

	@Autowired
	private UserPreferencesService preferenceService;

	@Autowired
	private SearchOrdersService searchOrdersService;

	@Autowired
	private ReferrerPortalRestApiSyslog syslog;

	@Autowired
	private GetPatientService getPatientService;

	@Autowired
	private GetOrderService getOrderService;

	@Autowired
	private GetReferrerService getReferrerService;

	@Autowired
	private GetPatientOrdersService getPatientOrdersService;

	@Autowired
	private PatientHistoryService patientHistoryService;

	@Autowired
	private ViewImageService viewImageService;

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private DicomService dicomService;

	@Autowired
	private PortalAccountService portalAccountService;

	@Autowired
	private ReportFcmTokenJPARepository reportFcmTokenRepository;

	@Autowired
	private ReportNotificationJPARepository reportNotificationRepository;

	@Autowired
	@Qualifier("ViewHtmlReportService")
	private ReportService viewHtmlReportService;

	@Autowired
	private PdfReportService pdfReportService;

	@Autowired
	private ReferrerAccountService referrerAccountService;
	
  @Autowired
  private SearchHospitalOrderSummaryService hospitalSearchOrderService;
	
	@GetMapping("/ping")
	public String getPing() {
		return "V0.1 (Migrating)";
	}
	
	@GetMapping("/searchHospitalOrders")
  public ResponseEntity<List<HospitalOrderSummary>> searchHospitalOrders(@RequestParam Map<String, String> paramMap, @RequestHeader(value=PortalConstant.HEADER_AUTHENTICATION, required=false) String authentication) {
    String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
    if(rateLimit(userName))
    {
      if(preferenceService.isTermsAccepted(userName))
      {
        ResponseEntity<SearchHospitalOrders> entity = hospitalSearchOrderService.doRestGet(userName, paramMap, SearchHospitalOrders.class);
        List<HospitalOrderSummary> originalList = entity.getBody().getOrders();
        //      List<HospitalOrderSummary> filteredList = new ArrayList<>();
        //      for(HospitalOrderSummary summary : originalList) {
        //        Map<String, String> opmap = new HashMap<>(1);
        //        opmap.put("orderUri", summary.getUri());
        //        if(!summary.isAccessible()) {
        //          opmap.put("breakGlass", "true");
        //        }
        //        ResponseEntity<HospitalOrderDetails> orderEntity = hospitalOrderService.doRestGet(userName, opmap, HospitalOrderDetails.class);
        //        if(HttpStatus.OK.equals(orderEntity.getStatusCode())) {
        //          final String desc = orderEntity.getBody().getPriorityType().getDescription();
        //          System.out.println("Priority is " + desc + " for Acc# " + summary.getAccessionNumber());
        //          if("In-Patient".equalsIgnoreCase(desc)) {
        //            filteredList.add(summary);
        //          }
        //        }
        //      }
        //      return new ResponseEntity<>(filteredList, entity.getHeaders(), entity.getStatusCode());
        return new ResponseEntity<>(originalList, entity.getHeaders(), entity.getStatusCode());
      }
      else
      {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
      }
    }
    else
    {
      return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
    }
  }
	
  @GetMapping("/hospitalPreferences")
  public ResponseEntity<HospitalUserPreferences> getHospitalPreferences(@RequestHeader(value=PortalConstant.HEADER_AUTHENTICATION, required=false) String authentication) {
    HospitalUserPreferences pref = preferenceService.getHospitalPreferences(AuthenticationUtil.getAuthenticatedUserName(authentication));
    return new ResponseEntity<HospitalUserPreferences>(pref, pref != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }
  
  @PostMapping("/hospitalPreferences")
  public ResponseEntity<String> postHospitalPreferences(@RequestBody HospitalUserPreferences preferences, @RequestHeader(value=PortalConstant.HEADER_AUTHENTICATION, required=false) String authentication) {
    ResponseEntity<String> entity;
    try {
      preferenceService.updateHospitalPreferences(AuthenticationUtil.getAuthenticatedUserName(authentication), preferences);
      entity = new ResponseEntity<>(HttpStatus.OK);
    } catch(Exception ex) {
      entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return entity;
  }

	@GetMapping("/searchOrders")
	public ResponseEntity<List<Order>> searchOrders(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (rateLimit(userName)) {
			if (preferenceService.isTermsAccepted(userName)) {
				ResponseEntity<SearchOrders> entity = searchOrdersService.doRestGet(userName, paramMap,
						SearchOrders.class);
				List<Order> orders = entity.getStatusCode().equals(HttpStatus.OK)
						? getAccessibleOrders(entity.getBody().getOrders(), paramMap)
						: new ArrayList<Order>(0);
				syslog.log(ReferrerEvent.SEARCH_ORDERS, "/searchOrders", userName, paramMap);
				return new ResponseEntity<List<Order>>(orders, entity.getHeaders(), entity.getStatusCode());
			} else {
				return new ResponseEntity<List<Order>>(HttpStatus.METHOD_NOT_ALLOWED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
	}

	@RequestMapping("/patient")
	public ResponseEntity<Patient> getPatient(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		ResponseEntity<Patient> entity;
		if (rateLimit(userName)) {
			if (preferenceService.isTermsAccepted(userName)) {
				entity = getPatientService.doRestGet(userName, paramMap, Patient.class);
				if (HttpStatus.OK.equals(entity.getStatusCode())) {
					// Save history for this user
					patientHistoryService.addHistory(userName, paramMap.get("patientUri"), entity.getBody());
				}
				syslog.log(ReferrerEvent.PATIENT, "/patient", userName, paramMap, entity.getBody());
			} else {
				entity = new ResponseEntity<Patient>(HttpStatus.METHOD_NOT_ALLOWED);
			}
		} else {
			entity = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
		return entity;
	}

	@GetMapping("/order")
	public ResponseEntity<OrderDetails> getOrder(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		ResponseEntity<OrderDetails> entity;
		if (rateLimit(userName)) {
			if (preferenceService.isTermsAccepted(userName)) {
				entity = getOrderService.doRestGet(userName, paramMap, OrderDetails.class);
				// PatientOrder does not have patient DOB, get from details
				if (entity.getStatusCode().equals(HttpStatus.OK)) {
					OrderDetails orderDetails = entity.getBody();
					// Add dicom info
					orderDetails.setDicom(dicomService.findDicomList(orderDetails));
					// Add patient info
					String patientUri = orderDetails.getPatientUri();
					System.out.println("patientUri : " + patientUri);
					if (patientUri != null && patientUri.length() > 0) {
						Map<String, String> params = new HashMap<>(1);
						params.put("patientUri", patientUri);
						ResponseEntity<Patient> patientEntity = getPatientService.doRestGet(userName, params,
								Patient.class);
						if (patientEntity.getStatusCode().equals(HttpStatus.OK)) {
							String dob = patientEntity.getBody().getDateOfBirth();
							orderDetails.setPatientDob(dob);
							entity = new ResponseEntity<OrderDetails>(orderDetails, entity.getHeaders(), HttpStatus.OK);
							syslog.log(ReferrerEvent.ORDER, "/order", userName, paramMap, orderDetails);
						} else {
							entity = new ResponseEntity<OrderDetails>(entity.getHeaders(), entity.getStatusCode());
						}
					} else {
						System.out.println("No patient uri in order.");
						orderDetails.setPatientDob("");
						entity = new ResponseEntity<OrderDetails>(orderDetails, entity.getHeaders(), HttpStatus.OK);
					}
				} else {
					entity = new ResponseEntity<OrderDetails>(entity.getHeaders(), entity.getStatusCode());
				}
			} else {
				entity = new ResponseEntity<OrderDetails>(HttpStatus.METHOD_NOT_ALLOWED);
			}
		} else {
			entity = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}

		return entity;
	}

	@RequestMapping("/patientOrders")
	public ResponseEntity<PatientOrder> getPatientOrders(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		ResponseEntity<PatientOrder> entity;
		if (rateLimit(userName)) {
			if (preferenceService.isTermsAccepted(userName)) {
				entity = getPatientOrdersService.doRestGet(userName, paramMap, PatientOrder.class);
				// Filter out inaccessible ones if required
				final String showAll = paramMap.get("showAll");
				if (entity.getStatusCode().equals(HttpStatus.OK)) {
					if ("true".equalsIgnoreCase(showAll)) { // default is false
						entity = new ResponseEntity<PatientOrder>(entity.getBody(), entity.getHeaders(), HttpStatus.OK);
					} else {
						PatientOrder order = entity.getBody();
						entity = new ResponseEntity<PatientOrder>(getAccessiblePatientOrder(order), entity.getHeaders(),
								HttpStatus.OK);
					}
					syslog.log(ReferrerEvent.PATIENT_ORDERS, "/patientOrders", userName, paramMap, entity.getBody());
				} else {
					entity = new ResponseEntity<PatientOrder>(entity.getHeaders(), entity.getStatusCode());
				}
			} else {
				entity = new ResponseEntity<PatientOrder>(HttpStatus.METHOD_NOT_ALLOWED);
			}
		} else {
			entity = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
		return entity;
	}

	@GetMapping("/user")
	public ResponseEntity<Referrer> getReferrer(
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		Map<String, String> internalParams = new HashMap<String, String>(1);
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		internalParams.put(GetReferrerService.PARAM_CURRENT_USER_NAME, userName);
		ResponseEntity<Referrer> entity = getReferrerService.doRestGet(userName, internalParams, Referrer.class);
		if (HttpStatus.OK.equals(entity.getStatusCode())) {
			AccountDetail detail = portalAccountService.getReferrerAccountDetail(userName);
			if (detail != null) {
				Referrer ref = entity.getBody();
				ref.setEmail(detail.getEmail());
				ref.setName(detail.getName());
				ref.setMobile(detail.getMobile());
				System.out.println("/user overwriting with LDAP information");
				entity = new ResponseEntity<Referrer>(ref, HttpStatus.OK);
			}
		}
		return entity;
	}

	@GetMapping("/report")
	public ResponseEntity<String> viewHtmlReport(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);

		ResponseEntity<String> responceEntity = new ResponseEntity<String>(HttpStatus.METHOD_NOT_ALLOWED);
		if (rateLimit(userName)) {
			if (preferenceService.isTermsAccepted(userName)) {
				OrderDetails order = obtainOrderDetails(userName, paramMap);
				if (order != null) {
					paramMap.put("reportUri", order.getReportUri());
					ResponseEntity<byte[]> entity = viewHtmlReportService.doRestGet(userName, paramMap, byte[].class);
					String str = "";
					try {
						str = new String(entity.getBody(), "UTF-8");
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					syslog.log(ReferrerEvent.REPORT_VIEW, "/report", userName, paramMap, order);
					responceEntity = new ResponseEntity<String>(str, entity.getHeaders(), entity.getStatusCode());
				} else {
					responceEntity = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
				}
			}
		} else {
			responceEntity = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
		return responceEntity;
	}

	/**
	 * Pull notification with push flag to display toast on UI.
	 */
	@GetMapping("/reportNotify/recent")
	public ResponseEntity<List<ReportNotify>> reportNotifyRecent(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		ResponseEntity<List<ReportNotify>> responceEntity = new ResponseEntity<List<ReportNotify>>(
				HttpStatus.METHOD_NOT_ALLOWED);
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (preferenceService.isTermsAccepted(userName)) {
			List<ReportNotify> list = new ArrayList<>();
			if (preferenceService.isNotifyOn(userName)) {
				String fromMin = paramMap.get("fromMin");
				Date fromDate = StringConversionUtil
						.getDateByMinutesDiff(fromMin != null ? fromMin : StringConversionUtil.MINS_DEFAULT, true);
				List<ReportNotificationEntity> nots = reportNotificationRepository
						.findByUidAndMessageAtGreaterThanEqual(userName, fromDate);
				String pushMin = paramMap.get("pushMin");
				Date pushDate = StringConversionUtil
						.getDateByMinutesDiff(pushMin != null ? pushMin : StringConversionUtil.PUSH_MINS_DEFAULT, true);
				for (ReportNotificationEntity entity : nots) {
					ReportNotify rf = new ReportNotify();
					rf.setOrderUri(entity.getOrderUri());
					rf.setPatientUri(entity.getPatientUri());
					rf.setPatientDob(StringConversionUtil.toAusDate(entity.getPatientDob()));
					rf.setPatientName(entity.getPatientName());
					rf.setStudyDate(StringConversionUtil.toAusDate(entity.getOrderAt()));
					rf.setPatientId(entity.getPatientId());
					rf.setOrderPriority(entity.getOrderPriority());
					rf.setOrderPriorityType(entity.getOrderPriorityType());
					rf.setPush(entity.getMessageAt().after(pushDate));
					list.add(rf);
				}
				if (list.size() > 0) {
					System.out.println(
							"reportNotifyRecent() uid : " + userName + " have " + list.size() + " new reports.");
				}
			}
			responceEntity = new ResponseEntity<List<ReportNotify>>(list, HttpStatus.OK);
		}
		return responceEntity;
	}

	@RequestMapping("/reportPdf")
	public ResponseEntity<byte[]> pdfReport(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(HttpStatus.METHOD_NOT_ALLOWED);
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (rateLimit(userName)) {
			if (userName != null && preferenceService.isTermsAccepted(userName)) {
				OrderDetails order = obtainOrderDetails(userName, paramMap);
				if (order != null) {
					paramMap.put("reportUri", order.getReportUri());
					syslog.log(ReferrerEvent.REPORT_DOWNLOAD, "/reportPdf", userName, paramMap, order);
					entity = pdfReportService.doRestGet(userName, paramMap, byte[].class);
				} else {
					entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);
				}
			}
		} else {
			entity = new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
		return entity;
	}

	@RequestMapping("/attachment")
	public ResponseEntity<byte[]> getAttachment(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (rateLimit(userName)) {
			if (userName != null && preferenceService.isTermsAccepted(userName)) {
				syslog.log(ReferrerEvent.REFERRAL, "/attachment", userName, paramMap,
						obtainOrderDetails(userName, paramMap));
				return attachmentService.doRestGet(userName, paramMap, byte[].class);
			} else {
				return new ResponseEntity<byte[]>(HttpStatus.METHOD_NOT_ALLOWED);
			}
		} else {
			return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);
		}
	}

	@GetMapping("/canEmailView")
	public ResponseEntity<String> canEmailView(
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		// the same logic in /view
		boolean can = (userName != null && portalAccountService.canUserViewImage(userName));
		return new ResponseEntity<String>(can ? HttpStatus.OK : HttpStatus.METHOD_NOT_ALLOWED);
	}

	@GetMapping("/runUrlCheck")
	public String urlCheck() {
		return viewImageService.runUrlCheck();
	}

	@RequestMapping("/view")
	public ResponseEntity<String> viewImage(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		ResponseEntity<String> viewEntity;
		if (rateLimit(userName)) {
			if (userName != null && portalAccountService.canUserViewImage(userName)
					&& preferenceService.isTermsAccepted(userName)) {
				ResponseEntity<OrderDetails> entity = getOrderService.doRestGet(userName, paramMap, OrderDetails.class);
				if (HttpStatus.OK.equals(entity.getStatusCode())) {
					OrderDetails od = entity.getBody();
					// Add dicom info
					od.setDicom(dicomService.findDicomList(od));

					// Save history for hospital portlet
					Patient pat = new Patient();
					pat.setDateOfBirth(od.getPatient().getDob());
					pat.setPatientId(od.getPatient().getPatientId());
					pat.setFullName(od.getPatient().getFullName());
					patientHistoryService.addHistory(userName, od.getPatientUri(), pat);

					// Generate view url
					viewEntity = viewImageService.generateUrl(userName, paramMap, od);
					syslog.log(ReferrerEvent.IMAGE, "/view", userName, paramMap, od);
				} else {
					viewEntity = new ResponseEntity<String>(entity.getHeaders(), entity.getStatusCode());
				}
			} else {
				viewEntity = new ResponseEntity<String>(HttpStatus.METHOD_NOT_ALLOWED);
			}
		} else {
			viewEntity = new ResponseEntity<String>(HttpStatus.TOO_MANY_REQUESTS);
		}
		return viewEntity;
	}

	@RequestMapping("/viewIv")
	public ResponseEntity<String[]> viewImageIv(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		ResponseEntity<OrderDetails> entity = getOrderService.doRestGet(userName, paramMap, OrderDetails.class);
		ResponseEntity<String[]> viewEntity;
		if (HttpStatus.OK.equals(entity.getStatusCode())) {
			viewEntity = viewImageService.generateUrls(userName, paramMap, entity.getBody());
			syslog.log(ReferrerEvent.IMAGE, "/viewIv", userName, paramMap, entity.getBody());
		} else {
			viewEntity = new ResponseEntity<String[]>(PortalConstant.EMPTY_STRING_ARRAY, entity.getHeaders(),
					entity.getStatusCode());
		}
		return viewEntity;
	}

	@GetMapping("/viewIvEv")
	public ResponseEntity<String[]> viewImageIvEv(@RequestParam Map<String, String> paramMap,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		ResponseEntity<OrderDetails> entity = getOrderService.doRestGet(userName, paramMap, OrderDetails.class);
		ResponseEntity<String[]> viewEntity;
		if (HttpStatus.OK.equals(entity.getStatusCode())) {
			viewEntity = viewImageService.generateIvEvImageUrls(userName, paramMap, entity.getBody());
		} else {
			viewEntity = new ResponseEntity<String[]>(PortalConstant.EMPTY_STRING_ARRAY, entity.getHeaders(),
					entity.getStatusCode());
		}
		return viewEntity;
	}

	@RequestMapping("/history")
	public ResponseEntity<List<PatientHistory>> getPatientHistory(
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		return patientHistoryService.getHistories(AuthenticationUtil.getAuthenticatedUserName(authentication));
	}
	
	@GetMapping("/accepttandc")
	 public ResponseEntity<String> accepttandc(@RequestHeader(value=PortalConstant.HEADER_AUTHENTICATION, required=false) String authentication) {
	    String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
	    return preferenceService.updateTermsAndCondition(userName, PortalConstant.TERMS_AND_CONDITIONS_HIDE);
	 }

	@PostMapping(value = "/preferences")
	public ResponseEntity<String> postPreferences(@RequestBody UserPreferences preferences,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		syslog.log(ReferrerEvent.ACCEPT_TC, "/preferences", userName);
		return preferenceService.setPreferences(userName, preferences);
	}

	@GetMapping(value = "/preferences")
	public ResponseEntity<UserPreferences> getPreferences(
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		return preferenceService.getPreferences(AuthenticationUtil.getAuthenticatedUserName(authentication));
	}

	@GetMapping("/terms")
	public ResponseEntity<String> tandc() {
		return portalAccountService.getTermsAndConditions();
	}

	@PostMapping("/user/checkEmail")
	public ResponseEntity<String> checkEmailUniqueness(HttpServletResponse response,
			@RequestBody AccountEmail accountEmail,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		final String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (userName == null) {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		} else {
			try {
				boolean isUnique = portalAccountService.isEmailGloballyUnique(userName, accountEmail.getEmail());
				return new ResponseEntity<String>(isUnique ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
			} catch (Exception ex) {
				ex.printStackTrace();
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		}
	}

	@PostMapping("/user/details")
	public ResponseEntity<String> updateAccountDetails(HttpServletResponse response,
			@RequestBody AccountDetail accountDetail,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		final String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (userName == null) {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		} else {
			try {
				portalAccountService.updateReferrerAccountDetail(userName, accountDetail);
				syslog.log(ReferrerEvent.UPDATE_DETAILS, "/user/details", userName);
				return new ResponseEntity<String>(HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		}
	}

	@PostMapping("/user/password")
	public ResponseEntity<String> updatePassword(HttpServletResponse response,
			@RequestBody AccountPassword accountPassword,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		final String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (userName == null) {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		} else {
			try {
				portalAccountService.updateReferrerPassword(userName, accountPassword);
				syslog.log(ReferrerEvent.CHANGE_PSWD, "/user/password", userName);
				return new ResponseEntity<String>(HttpStatus.OK);
			} catch (Exception ex) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		}
	}

	@PostMapping("/reportNotify/register")
	public ResponseEntity<String> reportNotifyRegister(@RequestBody ReportNotifyRegister register,
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
		final String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
		if (userName == null) {
			return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		} else {
			try {
				List<ReportFcmTokenEntity> current = reportFcmTokenRepository.findByDeviceId(register.getDeviceId());
				ReportFcmTokenEntity ce;
				if (current.size() > 0) {
					// Should be one for update
					ce = current.get(0);
					ce.setToken(register.getToken());
					ce.setUid(userName);
				} else {
					ce = new ReportFcmTokenEntity();
					ce.setDeviceId(register.getDeviceId());
					ce.setUid(userName);
					ce.setToken(register.getToken());
				}
				return reportFcmTokenRepository.saveAndFlush(ce) != null ? new ResponseEntity<String>(HttpStatus.OK)
						: new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			} catch (Exception ex) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		}
	}

	/**
	 * Token methods
	 */
	@PostMapping("/token")
	public ResponseEntity<Tokens> createTokens(@RequestBody UsernamePassword usernamePassword) {
		ResponseEntity<Tokens> entity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		if (usernamePassword != null) { 
			boolean isAuthInPortal = referrerAccountService.checkPassword(usernamePassword.getUsername(), usernamePassword.getPassword());
			boolean isAuthInAd = new ImedActiveDirectoryLdapManager().checkPassword(usernamePassword.getUsername(), usernamePassword.getPassword());
			logger.info("/token auth LDAP {} , AD {}", isAuthInPortal, isAuthInAd);
			if(isAuthInAd || isAuthInPortal) {
				try {
					final String access = AuthenticationUtil.createAccessToken(usernamePassword.getUsername());
					final String refresh = AuthenticationUtil.createRefreshToken(usernamePassword.getUsername());
					Tokens tokens = new Tokens();
					tokens.setAccess(access);
					tokens.setRefresh(refresh);
					entity = new ResponseEntity<Tokens>(tokens, HttpStatus.OK);
				} catch (Exception ex) {
					ex.printStackTrace();
					entity = new ResponseEntity<Tokens>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			else
			{
				logger.warn("/token authorization error for " + usernamePassword.getUsername());
			}
		} else {
			logger.warn("createTokens(/token) No request body provided or username/password is wrong");
		}

		syslog.log(
				((HttpStatus.OK.equals(entity.getStatusCode())) ? ReferrerEvent.TOKEN_SUCCESS
						: ReferrerEvent.TOKEN_FAIL),
				"/token", usernamePassword != null ? usernamePassword.getUsername() : "");

		return entity;
	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<RefreshedToken> refreshToken(
			@RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION) String authentication) {
		ResponseEntity<RefreshedToken> entity = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		String username = null;
		if (authentication != null) {
			try {
				username = AuthenticationUtil.checkRefreshToken(authentication);
				if (username != null) {
					final String access = AuthenticationUtil.createAccessToken(username);
					RefreshedToken refreshedToken = new RefreshedToken();
					refreshedToken.setToken(access);
					entity = new ResponseEntity<RefreshedToken>(refreshedToken, HttpStatus.OK);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		syslog.log(((HttpStatus.OK.equals(entity.getStatusCode())) ? ReferrerEvent.TOKEN_REFRESH_SUCCESS
				: ReferrerEvent.TOKEN_REFRESH_FAIL), "/refreshToken", username != null ? username : "");
		return entity;
	}

	private boolean rateLimit(final String userName) {
		boolean valid = true;
		if (userName != null && userName.length() > 0) {
			if (!auditService.isUnderRateLimitRequest(userName)) {
				System.out.println("Too many requests from " + userName);
				valid = false;
				preferenceService.updateTermsAndCondition(userName, PortalConstant.TERMS_AND_CONDITIONS_SHOW);
				auditService.auditRateLimit(userName);
			}
		}
		return valid;
	}

	/**
	 * Unless enough search criteria provided, filter out orders with accessible =
	 * false
	 * 
	 * @param orders
	 * @param paramMap
	 * @return
	 */
	private List<Order> getAccessibleOrders(final List<Order> orders, final Map<String, String> paramMap) {
		if (isParameterProvided(paramMap)) {
			return orders;
		} else {
			List<Order> filtered = new ArrayList<>();
			for (Order order : orders) {
				if (order.isAccessible()) {
					filtered.add(order);
				} else {
					System.out.println(
							"Filtered out this inaccessible order as parameters not enough : " + order.getUri());
				}
			}
			return filtered;
		}
	}

	private boolean isParameterProvided(final Map<String, String> paramMap) {
		final boolean isName = paramMap.containsKey("search");
		final boolean isDob = paramMap.containsKey("dob");
		final boolean isId = paramMap.containsKey("patientId");
		return isId || (isName && isDob);
	}

	private PatientOrder getAccessiblePatientOrder(final PatientOrder patientOrder) {
		int hides = 0;
		List<Order> filtered = new ArrayList<Order>(16);
		for (Order order : patientOrder.getOrders()) {
			if (order.isAccessible()) {
				filtered.add(order);
			} else {
				hides++;
			}
		}
		PatientOrder newPatientOrder = new PatientOrder();
		newPatientOrder.setHidden(hides);
		newPatientOrder.setOrders(filtered.toArray(new Order[filtered.size()]));
		return newPatientOrder;
	}

	/**
	 * Utility method to obtain native order details to gain report uril or syslog
	 * data
	 * 
	 * @param userName
	 * @param paramMap
	 * @return null if any error
	 */
	private OrderDetails obtainOrderDetails(final String userName, final Map<String, String> paramMap) {
		OrderDetails order = null;
		if (userName != null && userName.length() > 0 && paramMap.containsKey("orderUri")) {
			ResponseEntity<OrderDetails> entity = getOrderService.doRestGet(userName, paramMap, OrderDetails.class);
			if (entity.getStatusCode().equals(HttpStatus.OK)) {
				order = entity.getBody();
			}
		} else {
			System.out.println("obtainOrderDetails() Not enough parameters provided.");
		}
		return order;
	}

}
