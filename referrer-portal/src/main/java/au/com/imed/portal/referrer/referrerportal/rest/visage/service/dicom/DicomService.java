package au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom;

import java.util.ArrayList;
import java.util.List;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.weasis.dicom.op.CFind;
import org.weasis.dicom.param.DicomNode;
import org.weasis.dicom.param.DicomParam;
import org.weasis.dicom.param.DicomState;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.DicomPacs;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Order;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Procedure;

@Service
public class DicomService {
	private Logger logger = LoggerFactory.getLogger(DicomService.class);

  private static final DicomNode CALLING = new DicomNode("IMEDDMZIWS03");
  private static final DicomNode CALLED = new DicomNode("IMEDPDNSWMDB01", "10.20.160.50", 5000);
  private static final DicomParam SIUID = new DicomParam(Tag.StudyInstanceUID);
  private static final DicomParam MODALITY_IN_STUDY = new DicomParam(Tag.ModalitiesInStudy);
  private static final DicomParam STUDY_DISC = new DicomParam(Tag.StudyDescription);

  public String findStudyInstanceUid(final String accessionNumber, final String procedure) {
    String uid = null;
    try {
      DicomParam[] params = { new DicomParam(Tag.AccessionNumber, accessionNumber), SIUID};
      DicomState state = CFind.process(CALLING, CALLED, params);

      if(state.getStatus() == 0) {
        List<Attributes> items = state.getDicomRSP();
        int proc = Integer.parseInt(procedure);
        if(items.size() > proc) {
          uid = items.get(proc).getString(Tag.StudyInstanceUID);
        }
        else if(items.size() > 0) {
          // falling back to 0
          uid = items.get(0).getString(Tag.StudyInstanceUID);          
        }
      }
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
    logger.info("findStudyInstanceUid() Study instance uid : " + uid);
    return uid;
  }  
  
  public DicomPacs [] findDicomList(final Order orderSummary) {
  	return findDicomList(orderSummary.getAccessionNumber(), orderSummary.getProcedures());
  }
  
  public DicomPacs [] findDicomList(final OrderDetails orderDetails) {
  	return findDicomList(orderDetails.getAccessionNumber(), orderDetails.getProcedures());
  }
  	
  private DicomPacs [] findDicomList(final String accessionNumber, final Procedure [] procedures) {
  	List<DicomPacs> list = new ArrayList<>();
  	
    if(accessionNumber != null && accessionNumber.length() > 0) {
      list = this.findDicomList(accessionNumber);
    }
    else if(procedures != null && procedures.length > 0) {
      for(Procedure proc : procedures) {
        list.addAll(this.findDicomList(proc.getAccessionNumber()));
      }
    }
    
    logger.info("findDicomList() final " + list);
    return list.toArray(new DicomPacs[list.size()]);
  }
  
  private List<DicomPacs> findDicomList(final String accessionNumber) {
    List<DicomPacs> list = new ArrayList<>();

    if(accessionNumber == null || accessionNumber.length() == 0) {
      logger.info("findDicomList() accessionNumber is empty, no dicom search run.");
    }
    else
    {
      DicomParam[] params = { new DicomParam(Tag.AccessionNumber, accessionNumber), SIUID, MODALITY_IN_STUDY, STUDY_DISC};
      DicomState state = CFind.process(CALLING, CALLED, params);

      if(state.getStatus() == 0) {
        List<Attributes> items = state.getDicomRSP();
        for(Attributes item : items) {
          String siuid = item.getString(Tag.StudyInstanceUID);
          String modins = item.getString(Tag.ModalitiesInStudy);
          String sdisc = item.getString(Tag.StudyDescription);
          DicomPacs dicom = new DicomPacs();
          dicom.setAccessionNumber(accessionNumber);
          dicom.setStudyInstanceUID(siuid != null ? siuid : "");
          dicom.setModalitiesInStudy(modins != null ? modins : "");
          dicom.setStudyDescription(sdisc != null ? sdisc : "");
          list.add(dicom);
        }
      }
      else {
        logger.info("findDicomList() No dicom/pacs result for accessionNumber : " + accessionNumber + " due to " + state.getMessage());
      }
    }

    return list;
  }
}
