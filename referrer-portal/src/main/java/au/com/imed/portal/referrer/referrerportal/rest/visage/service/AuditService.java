package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.RequestAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.RequestAuditJPARepository;

@Service
public class AuditService {
	private static final String[] HEADERS = new String[] { "Time", "User Name", "Command", "Parameters",
			"Break Glass" };

	@Autowired
	private RequestAuditJPARepository requestAuditJPARepository;

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
