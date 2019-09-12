package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
{ 
  "results": [
    {
      "accessible": true,
      "accessionNumber": "77.1234567",
      "date": "2016-08-16",
      "facility": "Box Hill Radiology",
      "status": "pending",
      "patient": {
        "name": "Barney Test",
        "dob": "1970-01-01",
        "id": "77.8765432",
        "uri": "/patient/1"
      },
      "description": "X-ray Chest",
      "procedures": [
        {
          "description": "string",
          "modality": "string",
          "procedureId": "string"
        }
      ],
      "referrer": {
        "name": "Test Referrer"
      },
      "reportUri": "/report/1",
      "uri": "/order/1"
    }
  ],
  "hidden": 0
} *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PatientOrder {
  private int hidden;
  private Order [] orders;
  
  public int getHidden() {
    return hidden;
  }
  public void setHidden(int hidden) {
    this.hidden = hidden;
  }
  @JsonProperty("results")
  public Order[] getOrders() {
    return orders;
  }
  @JsonProperty("orders")
  public void setOrders(Order[] orders) {
    this.orders = orders;
  }
  
}
