package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class PdfReportService extends ReportService {

  @Override
  protected void addHeaderParams(HttpHeaders headers) {
    headers.add(HttpHeaders.ACCEPT, "application/pdf");
  }
}
