package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.controller.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${imed.api-v2.prefix}/electronic-ereferral")
public class EreferralV2Controller {
    private Logger logger = LoggerFactory.getLogger(EreferralV2Controller.class);
}
