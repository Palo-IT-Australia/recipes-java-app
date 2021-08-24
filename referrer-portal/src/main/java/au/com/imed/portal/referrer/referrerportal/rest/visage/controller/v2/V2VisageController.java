package au.com.imed.portal.referrer.referrerportal.rest.visage.controller.v2;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.rest.visage.controller.VisageController;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.*;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountEmail;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${imed.api-v2.prefix}/imedvisage")
@PreAuthorize("isAuthenticated()")
public class V2VisageController  extends VisageController {

    @Override
    public ResponseEntity<List<Order>> searchOrders(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.searchOrders(paramMap, authentication);
    }

    @Override
    public String getPing() {
        return super.getPing();
    }

    @Override
    public ResponseEntity<QuickReport> quickReport(@RequestParam(PortalConstant.PARAM_IMED_SECIRITY) String imsec) {
        return super.quickReport(imsec);
    }

    @Override
    public ResponseEntity<byte[]> quickReportPdf(@RequestParam(PortalConstant.PARAM_IMED_SECIRITY) String imsec) {
        return super.quickReportPdf(imsec);
    }

    @Override
    public ResponseEntity<byte[]> quickAttachment(@RequestParam(PortalConstant.PARAM_IMED_SECIRITY) String imsec) {
        return super.quickAttachment(imsec);
    }

    @Override
    public ResponseEntity<List<HospitalOrderSummary>> searchHospitalOrders(@RequestParam Map<String, String> paramMap, String auth) {
        return super.searchHospitalOrders(paramMap, auth);
    }

    @Override
    public ResponseEntity<HospitalUserPreferences> getHospitalPreferences(String authentication) {
        return super.getHospitalPreferences(authentication);
    }

    @Override
    public ResponseEntity<String> postHospitalPreferences(@RequestBody HospitalUserPreferences preferences, String authentication) {
        return super.postHospitalPreferences(preferences, authentication);
    }

    @Override
    public ResponseEntity<JSONObject> isMyimedPatient(@RequestParam("patientId") String patientId, String authentication) {
        return super.isMyimedPatient(patientId, authentication);
    }

    @Override
    public ResponseEntity<Patient> getPatient(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.getPatient(paramMap, authentication);
    }

    @Override
    public ResponseEntity<OrderDetails> getOrder(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.getOrder(paramMap, authentication);
    }

    @Override
    public ResponseEntity<PatientOrder> getPatientOrders(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.getPatientOrders(paramMap, authentication);
    }

    @Override
    public ResponseEntity<Referrer> getReferrer(String authentication) {
        return super.getReferrer(authentication);
    }

    @Override
    public ResponseEntity<String> viewHtmlReport(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.viewHtmlReport(paramMap, authentication);
    }

    @Override
    public ResponseEntity<List<ReportNotify>> reportNotifyRecent(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.reportNotifyRecent(paramMap, authentication);
    }

    @Override
    public ResponseEntity<byte[]> pdfReport(@RequestParam Map<String, String> paramMap,
                                            @RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication,
                                            @RequestParam(value = PortalConstant.TOKEN, required = false) String token) {
        return super.pdfReport(paramMap, authentication, token);
    }

    @Override
    public ResponseEntity<byte[]> getAttachment(@RequestParam Map<String, String> paramMap,
                                                @RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication,
                                                @RequestParam(value = PortalConstant.TOKEN, required = false) String token) {
        return super.getAttachment(paramMap, authentication, token);
    }

    @Override
    public ResponseEntity<String> canEmailView(String authentication) {
        return super.canEmailView(authentication);
    }

    @Override
    public String urlCheck() {
        return super.urlCheck();
    }

    @Override
    public ResponseEntity<String> viewImage(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.viewImage(paramMap, authentication);
    }

    @Override
    public ResponseEntity<String[]> viewImageIv(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.viewImageIv(paramMap, authentication);
    }

    @Override
    public ResponseEntity<String[]> viewImageIvEv(@RequestParam Map<String, String> paramMap, String authentication) {
        return super.viewImageIvEv(paramMap, authentication);
    }

    @Override
    public ResponseEntity<List<PatientHistory>> getPatientHistory(String authentication) {
        return super.getPatientHistory(authentication);
    }

    @Override
    public ResponseEntity<String> accepttandc(String authentication) {
        return super.accepttandc(authentication);
    }

    @Override
    public ResponseEntity<String> postPreferences(@RequestBody UserPreferences preferences, String authentication) {
        return super.postPreferences(preferences, authentication);
    }

    @Override
    public ResponseEntity<UserPreferences> getPreferences(String authentication) {
        return super.getPreferences(authentication);
    }

    @Override
    public ResponseEntity<String> tandc() {
        return super.tandc();
    }

    @Override
    public ResponseEntity<JSONObject> getMobileWarning(String authentication) {
        return super.getMobileWarning(authentication);
    }

    @Override
    public ResponseEntity<String> checkEmailUniqueness(HttpServletResponse response, @RequestBody AccountEmail accountEmail, String authentication) {
        return super.checkEmailUniqueness(response, accountEmail, authentication);
    }

    @Override
    public ResponseEntity<String> updateAccountDetails(HttpServletResponse response, @RequestBody AccountDetail accountDetail, String authentication) {
        return super.updateAccountDetails(response, accountDetail, authentication);
    }

    @Override
    public ResponseEntity<String> updatePassword(HttpServletResponse response, @RequestBody AccountPassword accountPassword, String authentication) {
        return super.updatePassword(response, accountPassword, authentication);
    }

    @Override
    public ResponseEntity<String> reportNotifyRegister(@RequestBody ReportNotifyRegister register, String authentication) {
        return super.reportNotifyRegister(register, authentication);
    }

    @Override
    public ResponseEntity<Tokens> createTokens(@RequestBody UsernamePassword usernamePassword) {
        return super.createTokens(usernamePassword);
    }

    @Override
    public ResponseEntity<RefreshedToken> refreshToken(String authentication) {
        return super.refreshToken(authentication);
    }
}
