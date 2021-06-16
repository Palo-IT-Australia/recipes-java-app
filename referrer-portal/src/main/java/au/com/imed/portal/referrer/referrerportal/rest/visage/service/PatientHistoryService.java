package au.com.imed.portal.referrer.referrerportal.rest.visage.service;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.PatientHistoryEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.PatientHistoryJPARepository;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Patient;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.PatientHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PatientHistoryService {
  @Autowired
  private PatientHistoryJPARepository repository;

  public ResponseEntity<List<PatientHistory>> getHistories(final String username) {
    if(username != null) {
      List<PatientHistoryEntity> entities = repository.getHistories(username);
      return new ResponseEntity<>(convertList(entities), null, HttpStatus.OK);
    }
    else
    {
      return new ResponseEntity<>(new ArrayList<>(), null, HttpStatus.UNAUTHORIZED);
    }
  }

  public void addHistory(final String username, final String patientUri, final Patient patient) {
    System.out.println("patientUri = " + patientUri + ", username = " + username);
    List<PatientHistoryEntity> currentList = repository.findByUsernameAndPatientUri(username, patientUri);

    PatientHistoryEntity history;
    if(currentList.size() > 0) {
      history = currentList.get(0);
      System.out.println("This username already has patient history : " + history);
    }
    else {
      history = new PatientHistoryEntity();
    }

    history.setUsername(username);
    history.setPatientDob(patient.getDateOfBirth());
    history.setPatientId(patient.getPatientId());
    history.setPatientName(patient.getFullName());
    history.setPatientUri(patientUri);
    history.setModifiedAt(new Date());

    System.out.println("Adding/Updating history : " + history);
    repository.saveAndFlush(history);
  }

  private List<PatientHistory> convertList(final List<PatientHistoryEntity> entities) {
    List<PatientHistory> list = new ArrayList<>(20);
    for(PatientHistoryEntity entity : entities) {
      PatientHistory p = new PatientHistory();
      p.setName(entity.getPatientName());
      p.setId(entity.getPatientId());
      p.setUri(entity.getPatientUri());
      p.setDob(entity.getPatientDob());
      p.setId(entity.getPatientId());
      list.add(p);
    }
    return list;
  }
}
