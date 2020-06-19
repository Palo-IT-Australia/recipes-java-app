package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.util.CarestreamImageViewerUtil;
import au.com.imed.portal.referrer.referrerportal.common.util.InteleViewerUtil;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.DicomPacs;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;

@Service
public class ViewImageService {
	private Logger logger = LoggerFactory.getLogger(ViewImageService.class);
	
	private static final int INVALID_VIEWER = -1;
	private static final String EMPTY_STRING = "";

	public static final int VIEWER_EUNITY = 1;
	public static final int VIEWER_AGFA = 2;
	public static final int VIEWER_CARESTREAM = 3;
	public static final int VIEWER_ZED_LINK = 4;

	@Autowired
	private UrlAvailService urlAvailService;

	public String runUrlCheck() {
		return urlAvailService.getUrl(true);
	}

	public ResponseEntity<String> generateUrl(final String userName, final Map<String, String> paramMap,
			final OrderDetails order) {
		final String viewer = paramMap.get("viewer");
		final String studyInstanceUid = paramMap.get("suid");

		int viewerCode = INVALID_VIEWER;
		try {
			viewerCode = Integer.parseInt(viewer);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			viewerCode = INVALID_VIEWER;
		}
		ResponseEntity<String> entity;
		String url = EMPTY_STRING;
		if (userName != null) {
			if (viewerCode == VIEWER_CARESTREAM && order != null) {
				// Carestream has defect on mobile multi modality mode, use suid for mobile app
				if (studyInstanceUid != null && studyInstanceUid.length() > 0) {
					logger.info("Carestream /view per resutl mode using studyInstanceUid = " + studyInstanceUid);
					url = CarestreamImageViewerUtil.generateUrlByStudyInstanceUid(urlAvailService.getUrl(false),
							userName, studyInstanceUid, order.getPatient().getPatientId());
				} else {
					logger.info("Carestream /view multi modality mode using acc# = " + order.getAccessionNumber());
					url = CarestreamImageViewerUtil.generateUrl(urlAvailService.getUrl(false), userName,
							buildAccessionNumberString(order), order.getPatient().getPatientId());
				}
				entity = (url != null && url.length() > 0) ? ResponseEntity.ok(url)
						: new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				logger.info("generateUrl() viewer code incorrect or order could not be found.");
				entity = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			}
		} else {
			entity = new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
		}

		return entity;
	}

	private String buildAccessionNumberString(final OrderDetails orderDetails) {
		final String accessionNumber = orderDetails.getAccessionNumber();
		final DicomPacs[] dicoms = orderDetails.getDicom();
		String accstr = "";
		if (accessionNumber != null && accessionNumber.length() > 0) {
			accstr = accessionNumber;
		} else if (dicoms != null && dicoms.length > 0) {
			// Get available accession numbers from dicom
			for (int i = 0; i < dicoms.length; i++) {
				String pnum = dicoms[i].getAccessionNumber();
				if (pnum != null && pnum.length() > 0) {
					accstr += pnum;
					if (i < dicoms.length - 1) {
						accstr += "\\\\";
					}
				}
			}
		}
		logger.info("buildAccessionNumberString() " + accstr);
		return accstr;
	}

	public ResponseEntity<String[]> generateUrls(final String userName, final Map<String, String> paramMap,
			final OrderDetails order) {
		String accessionNumber = paramMap.get("accessionNumber");

		ResponseEntity<String[]> entity;
		if (userName != null) {
			String[] urls = InteleViewerUtil.generateUrls(userName, accessionNumber, order.getPatient().getPatientId());
			entity = ResponseEntity.ok(urls);
		} else {
			entity = new ResponseEntity<String[]>(HttpStatus.UNAUTHORIZED);
		}
		return entity;
	}

	public ResponseEntity<String[]> generateIvEvImageUrls(final String userName, final Map<String, String> paramMap, final OrderDetails order) {
    String accessionNumber = paramMap.get("accessionNumber"); // Not used until multi modality support
    final String mode = paramMap.get("mode");

    String ivmode = InteleViewerUtil.URL_EV;
    if("mobile".equalsIgnoreCase(mode)) {
      ivmode = InteleViewerUtil.URL_EV_MOBILE;
    }else if("rest".equalsIgnoreCase(mode)) {
      ivmode = InteleViewerUtil.URL_EV_REST;
    }
    
    String securedAccessionNumber = getAccessionNumberForSecuredIvEvUrl(order);
    logger.info("accessionNumber {}, securedAccessionNumber {}", accessionNumber, securedAccessionNumber);
    ResponseEntity<String[]> entity;
    if (userName != null && securedAccessionNumber != null) {
      String [] urls = InteleViewerUtil.generateUrls(userName, securedAccessionNumber, order.getPatient().getPatientId(), ivmode);
      entity = ResponseEntity.ok(urls);
    }    
    else if(accessionNumber == null) {
    	entity = new ResponseEntity<String[]>(HttpStatus.BAD_REQUEST);
    }
    else
    {
      entity = new ResponseEntity<String[]>(HttpStatus.UNAUTHORIZED);
    }
    return entity;
  }
	
	/**
	 * Currently IVEV cannot handle multi modality order separeted by more than one _1, _2... accession numbers.
	 * In this case, move up to patient level url
	 * @param orderDetails
	 * @return
	 */
	private String getAccessionNumberForSecuredIvEvUrl(final OrderDetails orderDetails) {
		final DicomPacs[] dicoms = orderDetails.getDicom();
		String accstr = null;
		if(dicoms != null) {
			logger.info("Dicom original length " + dicoms.length);
			List<String> distinctList = Arrays.asList(dicoms).stream().map(d -> d.getAccessionNumber()).distinct().collect(Collectors.toList());
			logger.info("Acc# distinct size " + distinctList.size());
			if(distinctList.size() > 0) {
				// If multiple accession# in DICOM, using patient level ("" as acc#)
				accstr = distinctList.size() == 1 ? distinctList.get(0) : "";
			}
		} else {
			logger.info("No DICOM");
		}
		logger.info("getAccessionNumberForSecuredIvEvUrl() returning " + accstr);
		return accstr;
	}

	private String[] getViewerAccessionNumbers(OrderDetails order) {
		String[] accessionNumbers;
		final String accNum = order.getAccessionNumber();
		if (accNum != null && accNum.length() > 0) {
			accessionNumbers = new String[] { accNum };
		} else {
			int size = order.getProcedures().length;
			accessionNumbers = new String[size];
			for (int i = 0; i < size; i++) {
				accessionNumbers[i] = order.getProcedures()[i].getProcedureId();
			}
		}
		return accessionNumbers;
	}

	private String getViewerAccessionNumberString(final OrderDetails order, final String delimiter) {
		String accessionNumbers;
		final String accNum = order.getAccessionNumber();
		if (accNum != null && accNum.length() > 0) {
			accessionNumbers = accNum;
		} else {
			int size = order.getProcedures().length;
			accessionNumbers = EMPTY_STRING;
			for (int i = 0; i < size; i++) {
				if (i != 0) {
					accessionNumbers += delimiter;
				}
				accessionNumbers += order.getProcedures()[i].getAccessionNumber();
			}
		}
		logger.info("getViewerAccessionNumberString() returning " + accessionNumbers);
		return accessionNumbers;
	}
}
