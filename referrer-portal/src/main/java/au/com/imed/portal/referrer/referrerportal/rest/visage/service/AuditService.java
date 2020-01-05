package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.RequestAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.RequestAuditJPARepository;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.DicomPacs;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

@Service
public class AuditService {
	private Logger logger = LoggerFactory.getLogger(AuditService.class);

	private static final String[] HEADERS = new String[] { "Time", "User Name", "Command", "Parameters", "Break Glass" };
	private static final String BREAK_GLASS = "breakGlass";
	
	@Autowired
	private RequestAuditJPARepository requestAuditJPARepository;
	
	public void doAudit(final String command, final String username, final Map<String, String> params) {
  	doAudit(command, username, params, null);
  }
	
	public void doAudit(final String command, final String username, final Map<String, String> params, OrderDetails order) {
		try {			
			RequestAuditEntity entity = new RequestAuditEntity();
			entity.setIpAddress(getRemoteIpAddress());
			entity.setAuditAt(new Date());
			entity.setBreakGlass(params.containsKey(BREAK_GLASS) ? params.get(BREAK_GLASS) : "false");
			entity.setCommand(command);
			entity.setUsername(username);
			entity.setParameters(buildParamString(params));
			if(order != null) {
				entity.setAccessionNum(getAccessionNumberString(order));
				entity.setPatientId(order.getPatient().getPatientId());
			}
			requestAuditJPARepository.save(entity);    
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private String getAccessionNumberString(final OrderDetails orderDetails) {
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
						accstr += ",";
					}
				}
			}
		}
		return accstr;
	}

  private String buildParamString(Map<String, String> params) {
    StringBuffer sb = new StringBuffer();
    if(params != null) {
      Set<String> keyset = params.keySet();
      for(String key : keyset) {
      	if(key.startsWith("_")) {
      		continue;
      	}
        sb.append(key);
        sb.append("=");
        sb.append(params.get(key));
        sb.append("&");
      }
    }
    String ps = sb.toString();
    if(ps.endsWith("&")) {
      ps = ps.substring(0, ps.length() - 1);
    }
    if(ps.length() >= 300) {
    	ps = ps.substring(0, 300);
    }
    return ps;
  }
  
  private static final String[] IP_HEADER_CANDIDATES = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
  };
  
	private String getRemoteIpAddress() {
		String ip = "";
		try {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			for (String header: IP_HEADER_CANDIDATES) {
				String ipList = request.getHeader(header);
				if (ipList != null && ipList.length() > 0 && !"unknown".equalsIgnoreCase(ipList)) {
					String ipadr = ipList.split(",")[0];
					logger.info("Header IP address: " + header + " = " + ipadr);
					if(ipadr.length() > 0) {
						ip = ipadr;
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		logger.info("Remote IP address: " + ip);
		return ip;
	}

	public void getAuditExcel(final String userName, HttpServletResponse response, final Map<String, String> paramMap) {
		if (userName == null) {
			return;
		}

		final String startDate = paramMap.get("startDate");
		final String endDate = paramMap.get("endDate");
		if (startDate != null && endDate != null) {
			List<RequestAuditEntity> list = requestAuditJPARepository.getBetween(startDate, endDate);
			if (list.size() > 0) {
				try {
					response.setContentType("application/vnd.ms-excel");
					response.setHeader("Content-Disposition", "attachment; filename=Audit.xls");

					WritableWorkbook workbook = Workbook.createWorkbook(response.getOutputStream());
					WritableSheet sheet = workbook.createSheet("Audit", 0);

					WritableCellFormat cellFormat = new WritableCellFormat();
					cellFormat.setBackground(Colour.ORANGE);
					int idx = 0;
					for (String header : HEADERS) {
						sheet.addCell(new Label(idx++, 0, header, cellFormat));
					}

					int row = 1;
					for (RequestAuditEntity entity : list) {
						sheet.addCell(new Label(0, row, entity.getAuditAt().toString()));
						sheet.addCell(new Label(1, row, entity.getUsername()));
						sheet.addCell(new Label(2, row, entity.getCommand()));
						sheet.addCell(new Label(3, row, entity.getParameters()));
						sheet.addCell(new Label(4, row, entity.getBreakGlass()));
						row++;
					}

					workbook.write();
					workbook.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
	}

	private static final long LIMIT_RATE_PAST_SEC = 10 * 1000;
	private static final long LIMIT_RATE_NUM = 36;

	public boolean isUnderRateLimitRequest(final String username) {
		Date from = new Date();
		from.setTime(from.getTime() - LIMIT_RATE_PAST_SEC);
		return requestAuditJPARepository.countByUsernameAndAuditAtGreaterThan(username, from) <= LIMIT_RATE_NUM;
	}

	public void auditRateLimit(final String userName) {
		RequestAuditEntity entity = new RequestAuditEntity();
		entity.setUsername(userName);
		entity.setCommand("rateLimit");
		entity.setBreakGlass("false");
		entity.setParameters("");
		entity.setAuditAt(new Date());
		requestAuditJPARepository.saveAndFlush(entity);
	}
	
	public List<RequestAuditEntity> findByUsernameAndCommand(final String username, final String command) {
		return requestAuditJPARepository.findByUsernameAndCommand(username, command);
	}
}
