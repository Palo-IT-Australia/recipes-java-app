package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.rest.models.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${imed.api-v2.prefix}/practice")
@PreAuthorize("isAuthenticated()")
public class PracticeController {
    @PostMapping("/add")
    public ResponseEntity<ErrorResponse> addPractice () {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
