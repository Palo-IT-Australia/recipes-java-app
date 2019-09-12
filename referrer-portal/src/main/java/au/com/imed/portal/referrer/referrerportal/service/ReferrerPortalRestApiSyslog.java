package au.com.imed.portal.referrer.referrerportal.service;

import org.springframework.stereotype.Service;



import au.com.imed.portal.referrer.referrerportal.common.syslog.AReferrerPortalSyslog;
import au.com.imed.portal.referrer.referrerportal.common.syslog.ReferrereEventConsts;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Order;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Patient;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.PatientOrder;

@Service
public class ReferrerPortalRestApiSyslog extends AReferrerPortalSyslog 
{
  @Override
  protected String getValueFromObject(final String param, final Object object) {
    String val = "";
    if(object instanceof OrderDetails) {
      OrderDetails order = (OrderDetails) object;
      if(ReferrereEventConsts.PARAM_ACC_NUM.equals(param)) {
        val = order.getAccessionNumber();
      }
      else if(ReferrereEventConsts.PARAM_PATIENT_ID.equals(param)) {
        val = order.getPatient().getPatientId();
      }
    }
    else if(object instanceof Patient) {
      Patient patient = (Patient) object;
      if(ReferrereEventConsts.PARAM_PATIENT_ID.equals(param)) {
        val = patient.getPatientId();
      }        
    }
    else if(object instanceof PatientOrder) {
      PatientOrder po = (PatientOrder) object;
      if(ReferrereEventConsts.PARAM_PATIENT_ID.equals(param)) {
        Order [] orders = po.getOrders();
        val = orders.length > 0 ? orders[0].getPatient().getPatientId() : "";
      }
    }
    return val;
  }

  @Override
  protected Class<?> getLoggerClass() {
    return this.getClass();
  }
}
