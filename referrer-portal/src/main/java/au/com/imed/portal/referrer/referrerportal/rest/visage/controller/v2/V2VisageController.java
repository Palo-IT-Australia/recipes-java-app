package au.com.imed.portal.referrer.referrerportal.rest.visage.controller.v2;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.syslog.ReferrerEvent;
import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.rest.visage.controller.VisageController;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Order;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.SearchOrders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${imed.api-v2.prefix}/imedvisage")
@PreAuthorize("isAuthenticated()")
public class V2VisageController  extends VisageController {

    @GetMapping("/searchOrders")
    public ResponseEntity<List<Order>> searchOrders(@RequestParam Map<String, String> paramMap,
                                                    @RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {

        return super.searchOrders(paramMap, authentication);
    }
}
