package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminrest/filetoaccount")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminFileToAccountRestController {
	private Logger logger = LoggerFactory.getLogger(AdminFileToAccountRestController.class);

	@GetMapping("/downloadresultfile")
	public void download(@RequestParam("name") String fname, HttpServletResponse response) {
		try{
			if(fname != null && (fname.indexOf("temp") >= 0 || fname.indexOf("Temp") >= 0) )
			{
				final String resultFilePath = fname;
				File f = new File(resultFilePath);
				byte [] filebytes = Files.readAllBytes(Paths.get(resultFilePath));
				if(filebytes != null) {
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "inline; filename=" + f.getName());

					OutputStream out = response.getOutputStream();
					out.write(filebytes);
					out.flush();
					out.close();
					f.delete();
					logger.info("Dowloaded and removed " + resultFilePath);
				}
			}
			else
			{
				logger.info("No TEMP file name provided. " + fname);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
