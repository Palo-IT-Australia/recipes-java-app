package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.imed.portal.referrer.referrerportal.rest.consts.OrderStatusConst;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails.Report.Dictation;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails.Report.Signature;

/**
 *
 * Visage
{
   "accessionNumber": "70.54546",
   "attachments":    [
            {
         "attachmentDateTime": "2007-05-04 10:31:29",
         "attachmentType": "US Worksheet",
         "contents": [         {
            "pageNumber": 0,
            "uri": "/attachment/21028490/0"
         }],
         "mimeType": "image/jpeg"
      },
            {
         "attachmentDateTime": "2007-05-04 08:57:02",
         "attachmentType": "Request",
         "contents": [         {
            "pageNumber": 0,
            "uri": "/attachment/21028489/0"
         }],
         "mimeType": "image/jpeg"
      }
   ],
   "dateOfService": "2007-05-04",
   "events": [],
   "facility": "Broadmeadows Medical Imaging [TRAINING]",
   "highestStatus": "COMPLETE",
   "patient":    {
      "fullName": "UNAL, Mr Zuhdu",
      "patientId": "70.DGH280Z",
      "uri": "/patient/104317249"
   },
   "priority":    {
      "code": "",
      "description": ""
   },
   "priorityType":    {
      "code": "",
      "description": ""
   },
   "procedureDescription": "USOUND ABDO/URINARY TRACT (OA085)",
   "procedures": [   {
      "appointment": "2007-05-04 08:56:00",
      "description": "USOUND ABDO/URINARY TRACT (OA085)",
      "modality": "US",
      "procedureId": "70.54546_1",
      "status": "COMPLETE",
      "uri": "/procedure/10213781"
   }],
   "referrer":    {
      "uri": "/referrer/78907",
      "fullName": "KURNAZ, Dr Selim",
      "practiceName": "",
      "providerNumber": "240501JX",
      "speciality": "GP-General",
      "phone1": "0393091119",
      "phone2": "",
      "mobile": "",
      "email": "",
      "fax": "0394299544",
      "address":       {
         "line1": "Avicenna Medical Centre",
         "line2": "182 Blair Street",
         "line3": "",
         "city": "DALLAS",
         "state": "VIC",
         "postcode": "3047",
         "country": ""
      },
      "businessUnit": "MIA Victoria"
   },
   "report":    {
      "dictations": [],
      "signatures": [      {
         "signer": "NEW, Dr Kim",
         "proxyOrOverreadFor": "",
         "type": "STANDARD"
      }],
      "uri": "/diagnosticReport/16274102"
   },
   "uri": "/order/519156424"
} *

  Rest
  {
  "accessionNumber": "77.1234567",
  "attachments": [
    {
      "dateTime": "string",
      "type": "string",
      "pages": [
        {
          "pageNumber": 0,
          "uri": "string"
        }
      ]
    }
  ],
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
  "reportingDrs": [
    "Dr Good Radiologist"
  ],
  "signingDrs": [
    "Dr Good Radiologist"
  ],
  "reportUri": "/report/1"
}
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class OrderDetails {
  private String accessionNumber;
  private Attachment [] attachments;
  private String dateOfService;
  private String facility;
  private String highestStatus;
  
  private Patient patient;
  
  private String procedureDescription;
  private Procedure [] procedures;
  
  private Referrer referrer;
  private Report report;
  
  private DicomPacs[] dicom = new DicomPacs[0];  
  
  public String getAccessionNumber() {
    return accessionNumber;
  }

  public void setAccessionNumber(String accessionNumber) {
    this.accessionNumber = accessionNumber;
  }

  public Attachment[] getAttachments() {
    return attachments;
  }

  public void setAttachments(Attachment[] attachments) {
    this.attachments = attachments;
  }

  @JsonProperty("date")
  public String getDateOfService() {
    return dateOfService;
  }

  @JsonProperty("dateOfService")
  public void setDateOfService(String dateOfService) {
    this.dateOfService = dateOfService;
  }

  public String getFacility() {
    return facility;
  }

  public void setFacility(String facility) {
    this.facility = facility;
  }

  @JsonIgnore
  public String getHighestStatus() {
    return highestStatus;
  }

  @JsonProperty("status")
  public String getStatus() {
    return OrderStatusConst.GROUP_STATUS_MAP.get(this.highestStatus);
  }

  @JsonProperty("highestStatus")
  public void setHighestStatus(String highestStatus) {
    this.highestStatus = highestStatus;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }

  @JsonProperty("description")
  public String getProcedureDescription() {
    return procedureDescription;
  }

  @JsonProperty("procedureDescription")
  public void setProcedureDescription(String procedureDescription) {
    this.procedureDescription = procedureDescription;
  }

  public Procedure[] getProcedures() {
    return procedures;
  }

  public void setProcedures(Procedure[] procedures) {
    this.procedures = procedures;
  }

  public Referrer getReferrer() {
    return referrer;
  }

  public void setReferrer(Referrer referrer) {
    this.referrer = referrer;
  }

  @JsonIgnore
  public Report getReport() {
    return report;
  }

  @JsonProperty("report")
  public void setReport(Report report) {
    this.report = report;
  }
  
  @JsonProperty("reportUri")
  public String getReportUri() {
    return this.getReport().getUri();
  }

  @JsonProperty("reportingDrs")
  public String [] getReportDrs() {
    Dictation [] dictations = this.getReport().getDictations();
    String [] drs = new String [dictations.length];
    for(int i = 0; i < dictations.length; i++) {
      drs[i] = dictations[i].getDoctor();
    }
    return drs;
  }

  @JsonProperty("signingDrs")
  public String [] getSigningDrs() {
    Signature [] signatures = this.getReport().getSignatures();
    String [] sigs = new String [signatures.length];
    for(int i = 0; i < signatures.length; i++) {
      sigs[i] = signatures[i].getSigner();
    }
    return sigs;
  }

  @JsonIgnore
  public void setPatientDob(String dob) {
    this.patient.setDob(dob);
  }

  @JsonIgnore
  public String getPatientUri() {
    return this.patient.getUri();
  }

  @JsonIgnoreProperties(ignoreUnknown=true)
  public static class Attachment {
    private String attachmentDateTime;
    private String attachmentType;
    private Content [] contents;
    
    @JsonProperty("dateTime")
    public String getAttachmentDateTime() {
      return attachmentDateTime;
    }

    @JsonProperty("attachmentDateTime")
    public void setAttachmentDateTime(String attachmentDateTime) {
      this.attachmentDateTime = attachmentDateTime;
    }

    @JsonProperty("type")
    public String getAttachmentType() {
      return attachmentType;
    }

    @JsonProperty("attachmentType")
    public void setAttachmentType(String attachmentType) {
      // TODO delte after Visage bug fixed IRP-148
      this.attachmentType = "".equals(attachmentType) ? "Request" : attachmentType;
    }

    @JsonProperty("pages")
    public Content[] getContents() {
      return contents;
    }

    @JsonProperty("contents")
    public void setContents(Content[] contents) {
      this.contents = contents;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Content {
      private int pageNumber;
      private String uri;
      
      public int getPageNumber() {
        return pageNumber;
      }
      public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
      }
      public String getUri() {
        return uri;
      }
      public void setUri(String uri) {
        this.uri = uri;
      }      
    }
    
  }
  
  @JsonIgnoreProperties(ignoreUnknown=true)
  public static class Patient {
    private String fullName;
    private String patientId;
    private String uri;
    private String dob = "";
    
    @JsonProperty("name")
    public String getFullName() {
      return fullName;
    }
    @JsonProperty("fullName")
    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
    @JsonProperty("id")
    public String getPatientId() {
      return patientId;
    }
    @JsonProperty("patientId")
    public void setPatientId(String patientId) {
      this.patientId = patientId;
    }
    public String getUri() {
      return uri;
    }
    public void setUri(String uri) {
      this.uri = uri;
    }
    public String getDob() {
      return dob;
    }
    public void setDob(String dob) {
      this.dob = dob;
    }
  }
  
//  @JsonIgnoreProperties(ignoreUnknown=true)
//  public static class Procedure {
//    private String description;
//    private String modality;
//    private String procedureId;
//    private String accessionNumber;
//    public String getDescription() {
//      return description;
//    }
//    public void setDescription(String description) {
//      this.description = description;
//    }
//    public String getModality() {
//      return modality;
//    }
//    public void setModality(String modality) {
//      this.modality = modality;
//    }
//    public String getProcedureId() {
//      return procedureId;
//    }
//    public void setProcedureId(String procedureId) {
//      this.procedureId = procedureId;
//    }
//    public String getAccessionNumber() {
//      return accessionNumber;
//    }
//    public void setAccessionNumber(String accessionNumber) {
//      this.accessionNumber = accessionNumber;
//    }
//    
//  }
  
  @JsonIgnoreProperties(ignoreUnknown=true)
  static class Referrer {
    private String fullName;

    @JsonProperty("name")
    public String getFullName() {
      return fullName;
    }

    @JsonProperty("fullName")
    public void setFullName(String fullName) {
      this.fullName = fullName;
    }
    
  }
  
  public static class Report {
    private String uri;
    private Dictation [] dictations;
    private Signature [] signatures;
    
    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }

    public Dictation[] getDictations() {
      return dictations;
    }

    public void setDictations(Dictation[] dictations) {
      this.dictations = dictations;
    }

    public Signature[] getSignatures() {
      return signatures;
    }

    public void setSignatures(Signature[] signatures) {
      this.signatures = signatures;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    static class Dictation {
      private String doctor;

      public String getDoctor() {
        return doctor;
      }

      public void setDoctor(String doctor) {
        this.doctor = doctor;
      }
      
    }
    
    @JsonIgnoreProperties(ignoreUnknown=true)
    static class Signature {
      private String signer;

      public String getSigner() {
        return signer;
      }

      public void setSigner(String signer) {
        this.signer = signer;
      }
      
    }
  }

  public DicomPacs[] getDicom() {
    return dicom;
  }

  public void setDicom(DicomPacs[] dicom) {
    this.dicom = dicom;
  }  
}
