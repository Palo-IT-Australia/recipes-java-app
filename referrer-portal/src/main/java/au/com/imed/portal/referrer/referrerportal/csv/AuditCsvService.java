package au.com.imed.portal.referrer.referrerportal.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.VisageRequestAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.VisageRequestAuditJPARepository;

@Service
public class AuditCsvService {
	@Autowired
	private VisageRequestAuditJPARepository repository;
	
	public File createCsv() throws Exception {
		final String from = LocalDate.now().minusDays(7).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00";
		final String to = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59";

		File tempFile = File.createTempFile("audit-", "-csv");
    PrintWriter printWriter = new PrintWriter(tempFile);
    printWriter.println("Time,User Name,Command,Parameters,Break Glass,Accession Num,Patient Id,IP address");
    List<VisageRequestAuditEntity> list = repository.getBetween(from, to);
    for(VisageRequestAuditEntity entity : list) {
      printWriter.print(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
      		entity.getAuditAt().toString(),
      		entity.getUsername(),
      		entity.getCommand(),
      		entity.getParameters(),
      		entity.getBreakGlass(),
      		entity.getAccessionNum(),
      		entity.getPatientId(),
      		entity.getIpAddress()));
    }
    printWriter.close();
    return tempFile;
	}
	
	public File createCsvZip() throws Exception {
		File csvFile = createCsv();
		File zipFile = File.createTempFile("auditcsvzip", "-zip");
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
			FileInputStream fis = new FileInputStream(csvFile);

			ZipEntry zipEntry = new ZipEntry("audit.csv");
			zos.putNextEntry(zipEntry);

			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}
			zos.closeEntry();
			zos.close();
			fis.close();
		} finally {
			csvFile.delete();
		}
		return zipFile;
	}
}
