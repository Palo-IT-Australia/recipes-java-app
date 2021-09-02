package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import au.com.imed.portal.referrer.referrerportal.common.util.BooleanToStringConverter;

@Entity
@Table(name = "REFERRER_EREFERRAL", catalog = "dbo")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElectronicReferralForm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", unique = true, nullable = false)
	private int id;

	@Column(name = "patient_name")
	String patientName;
	@Column(name = "patient_dob")
	String patientDob;
	@Column(name = "patient_gender")
	String patientGender;
	@Column(name = "patient_phone")
	String patientPhone;
	@Column(name = "patient_street")
	String patientStreet;
	@Column(name = "patient_suburb")
	String patientSuburb;
	@Column(name = "patient_postcode")
	String patientPostcode;
	@Column(name = "patient_state")
	String patientState;
	@Column(name = "patient_email")
	String patientEmail;
	@Convert(converter = BooleanToStringConverter.class)
	@Column(name = "patient_compensation")
	boolean patientCompensation;
	@Column(name = "exam_details")
	String examDetails;
	@Column(name = "clinical_details")
	String clinicalDetails;
	@Column(name = "patient_pregnant")
	String patientPregnant;
	@Column(name = "iv_contrast_details")
	String ivContrastDetails;
	@Column(name = "iv_contrast_allergy")
	String ivContrastAllergy;
	@Column(name = "iv_renal")
	String ivRenal;
	@Column(name = "iv_diabetes")
	String ivDiabetes;
	@Column(name = "iv_creatinine_level")
	String ivCreatinineLevel;
	@Column(name = "iv_creatinine_egfr")
	String ivCreatinineEgfr;
	@Column(name = "iv_creatinine_date")
	String ivCreatinineDate;
	@Column(name = "mri_metal")
	String mriMetal;
	@Column(name = "mri_pacemaker")
	String mriPacemaker;
	@Column(name = "mri_brainclip")
	String mriBrainClip;
	@Column(name = "mri_cochlear")
	String mriCochlear;
	@Column(name = "mri_coil")
	String mriCoil;
	@Column(name = "mri_ultrasound")
	String mriUltrasound;
	@Column(name = "mri_number")
	String mriNumber;
	@Column(name = "mri_date")
	String mriDate;
	@Column(name = "doctor_name")
	String doctorName;
	@Column(name = "doctor_provider_number")
	String doctorProviderNumber;
	@Column(name = "doctor_requester_number")
	String doctorRequesterNumber;
	@Column(name = "doctor_ahpra")
	String doctorAhpra;
	@Column(name = "doctor_phone")
	String doctorPhone;
	@Column(name = "doctor_email")
	String doctorEmail;
	@Column(name = "doctor_practice_name")
	String doctorPracticeName;
	@Column(name = "doctor_street")
	String doctorStreet;
	@Column(name = "doctor_suburb")
	String doctorSuburb;
	@Column(name = "doctor_state")
	String doctorState;
	@Column(name = "doctor_postcode")
	String doctorPostcode;
	@Column(name = "cc_doctor_name")
	String ccDoctorName;
	@Column(name = "cc_doctor_provider_number")
	String ccDoctorProviderNumber;
	@Column(name = "cc_doctor_requester_number")
	String ccDoctorRequesterNumber;
	@Column(name = "cc_doctor_email")
	String ccDoctorEmail;
	@Column(name = "cc_doctor_practice_name")
	String ccDoctorPracticeName;
	@Column(name = "cc_doctor_street")
	String ccDoctorStreet;
	@Column(name = "cc_doctor_suburb")
	String ccDoctorSuburb;
	@Column(name = "cc_doctor_state")
	String ccDoctorState;
	@Column(name = "cc_doctor_postcode")
	String ccDoctorPostcode;
	@Column(name = "signature_name")
	String signatureName;
	@Column(name = "signature_date")
	String signatureDate;
	@Convert(converter = BooleanToStringConverter.class)
	@Column(name = "signature_entitled")
	boolean signatureEntitled;
	@Column(name = "film_report")
	String filmReport;
	@Convert(converter = BooleanToStringConverter.class)
	@Column(name = "pad")
	boolean pad;
	@Convert(converter = BooleanToStringConverter.class)
	@Column(name = "copy_to_me")
	boolean copyToMe;
	@Convert(converter = BooleanToStringConverter.class)
	@Column(name = "urgent_result")
	boolean urgentResult;

	@Column(name = "submitted_time")
	Date submittedTime;
	
	@JsonIgnore
	@Column(name = "doctor_failures")
	private Integer doctorFailures = 0;

	@JsonIgnore
	@Column(name = "patient_failures")
	private Integer patientFailures = 0;

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientDob() {
		return patientDob;
	}

	public void setPatientDob(String patientDob) {
		this.patientDob = patientDob;
	}

	public String getPatientPhone() {
		return patientPhone;
	}

	public void setPatientPhone(String patientPhone) {
		this.patientPhone = patientPhone;
	}

	public String getPatientStreet() {
		return patientStreet;
	}

	public void setPatientStreet(String patientStreet) {
		this.patientStreet = patientStreet;
	}

	public String getPatientSuburb() {
		return patientSuburb;
	}

	public void setPatientSuburb(String patientSuburb) {
		this.patientSuburb = patientSuburb;
	}

	public String getPatientPostcode() {
		return patientPostcode;
	}

	public void setPatientPostcode(String patientPostcode) {
		this.patientPostcode = patientPostcode;
	}

	public String getPatientState() {
		return patientState;
	}

	public void setPatientState(String patientState) {
		this.patientState = patientState;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}

	public boolean isPatientCompensation() {
		return patientCompensation;
	}

	public void setPatientCompensation(boolean patientCompensation) {
		this.patientCompensation = patientCompensation;
	}

	public String getIvContrastDetails() {
		return ivContrastDetails;
	}

	public void setIvContrastDetails(String ivContrastDetails) {
		this.ivContrastDetails = ivContrastDetails;
	}

	public String getExamDetails() {
		return examDetails;
	}

	public void setExamDetails(String examDetails) {
		this.examDetails = examDetails;
	}

	public String getClinicalDetails() {
		return clinicalDetails;
	}

	public void setClinicalDetails(String clinicalDetails) {
		this.clinicalDetails = clinicalDetails;
	}

	public String getPatientPregnant() {
		return patientPregnant;
	}

	public void setPatientPregnant(String patientPregnant) {
		this.patientPregnant = patientPregnant;
	}

	public String getIvContrastAllergy() {
		return ivContrastAllergy;
	}

	public void setIvContrastAllergy(String ivContrastAllergy) {
		this.ivContrastAllergy = ivContrastAllergy;
	}

	public String getIvRenal() {
		return ivRenal;
	}

	public void setIvRenal(String ivRenal) {
		this.ivRenal = ivRenal;
	}

	public String getIvDiabetes() {
		return ivDiabetes;
	}

	public void setIvDiabetes(String ivDiabetes) {
		this.ivDiabetes = ivDiabetes;
	}

	public String getIvCreatinineLevel() {
		return ivCreatinineLevel;
	}

	public void setIvCreatinineLevel(String ivCreatinineLevel) {
		this.ivCreatinineLevel = ivCreatinineLevel;
	}

	public String getIvCreatinineEgfr() {
		return ivCreatinineEgfr;
	}

	public void setIvCreatinineEgfr(String ivCreatinineEgfr) {
		this.ivCreatinineEgfr = ivCreatinineEgfr;
	}

	public String getIvCreatinineDate() {
		return ivCreatinineDate;
	}

	public void setIvCreatinineDate(String ivCreatinineDate) {
		this.ivCreatinineDate = ivCreatinineDate;
	}

	public String getMriMetal() {
		return mriMetal;
	}

	public void setMriMetal(String mriMetal) {
		this.mriMetal = mriMetal;
	}

	public String getMriPacemaker() {
		return mriPacemaker;
	}

	public void setMriPacemaker(String mriPacemaker) {
		this.mriPacemaker = mriPacemaker;
	}

	public String getMriBrainClip() {
		return mriBrainClip;
	}

	public void setMriBrainClip(String mriBrainClip) {
		this.mriBrainClip = mriBrainClip;
	}

	public String getMriCochlear() {
		return mriCochlear;
	}

	public void setMriCochlear(String mriCochlear) {
		this.mriCochlear = mriCochlear;
	}

	public String getMriCoil() {
		return mriCoil;
	}

	public void setMriCoil(String mriCoil) {
		this.mriCoil = mriCoil;
	}

	public String getMriUltrasound() {
		return mriUltrasound;
	}

	public void setMriUltrasound(String mriUltrasound) {
		this.mriUltrasound = mriUltrasound;
	}

	public String getMriNumber() {
		return mriNumber;
	}

	public void setMriNumber(String mriNumber) {
		this.mriNumber = mriNumber;
	}

	public String getMriDate() {
		return mriDate;
	}

	public void setMriDate(String mriDate) {
		this.mriDate = mriDate;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getDoctorProviderNumber() {
		return doctorProviderNumber;
	}

	public void setDoctorProviderNumber(String doctorProviderNumber) {
		this.doctorProviderNumber = doctorProviderNumber;
	}

	public String getDoctorEmail() {
		return doctorEmail;
	}

	public void setDoctorEmail(String doctorEmail) {
		this.doctorEmail = doctorEmail;
	}

	public String getDoctorStreet() {
		return doctorStreet;
	}

	public void setDoctorStreet(String doctorStreet) {
		this.doctorStreet = doctorStreet;
	}

	public String getDoctorSuburb() {
		return doctorSuburb;
	}

	public void setDoctorSuburb(String doctorSuburb) {
		this.doctorSuburb = doctorSuburb;
	}

	public String getDoctorState() {
		return doctorState;
	}

	public void setDoctorState(String doctorState) {
		this.doctorState = doctorState;
	}

	public String getDoctorPostcode() {
		return doctorPostcode;
	}

	public void setDoctorPostcode(String doctorPostcode) {
		this.doctorPostcode = doctorPostcode;
	}

	public String getCcDoctorName() {
		return ccDoctorName;
	}

	public void setCcDoctorName(String ccDoctorName) {
		this.ccDoctorName = ccDoctorName;
	}

	public String getCcDoctorProviderNumber() {
		return ccDoctorProviderNumber;
	}

	public void setCcDoctorProviderNumber(String ccDoctorProviderNumber) {
		this.ccDoctorProviderNumber = ccDoctorProviderNumber;
	}

	public String getCcDoctorEmail() {
		return ccDoctorEmail;
	}

	public void setCcDoctorEmail(String ccDoctorEmail) {
		this.ccDoctorEmail = ccDoctorEmail;
	}

	public String getCcDoctorStreet() {
		return ccDoctorStreet;
	}

	public void setCcDoctorStreet(String ccDoctorStreet) {
		this.ccDoctorStreet = ccDoctorStreet;
	}

	public String getCcDoctorSuburb() {
		return ccDoctorSuburb;
	}

	public void setCcDoctorSuburb(String ccDoctorSuburb) {
		this.ccDoctorSuburb = ccDoctorSuburb;
	}

	public String getCcDoctorState() {
		return ccDoctorState;
	}

	public void setCcDoctorState(String ccDoctorState) {
		this.ccDoctorState = ccDoctorState;
	}

	public String getCcDoctorPostcode() {
		return ccDoctorPostcode;
	}

	public void setCcDoctorPostcode(String ccDoctorPostCode) {
		this.ccDoctorPostcode = ccDoctorPostCode;
	}

	public String getSignatureName() {
		return signatureName;
	}

	public void setSignatureName(String signatureName) {
		this.signatureName = signatureName;
	}

	public String getSignatureDate() {
		return signatureDate;
	}

	public void setSignatureDate(String signatureDate) {
		this.signatureDate = signatureDate;
	}

	public boolean isSignatureEntitled() {
		return signatureEntitled;
	}

	public void setSignatureEntitled(boolean signatureEntitled) {
		this.signatureEntitled = signatureEntitled;
	}

	public String getFilmReport() {
		return filmReport;
	}

	public void setFilmReport(String filmReport) {
		this.filmReport = filmReport;
	}

	public boolean isPad() {
		return pad;
	}

	public void setPad(boolean pad) {
		this.pad = pad;
	}

	public Date getSubmittedTime() {
		return submittedTime;
	}

	public void setSubmittedTime(Date submittedTime) {
		this.submittedTime = submittedTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPatientGender() {
		return patientGender;
	}

	public void setPatientGender(String patientGender) {
		this.patientGender = patientGender;
	}

	public String getDoctorPracticeName() {
		return doctorPracticeName;
	}

	public void setDoctorPracticeName(String doctorPracticeName) {
		this.doctorPracticeName = doctorPracticeName;
	}

	public String getCcDoctorPracticeName() {
		return ccDoctorPracticeName;
	}

	public void setCcDoctorPracticeName(String ccDoctorPracticeName) {
		this.ccDoctorPracticeName = ccDoctorPracticeName;
	}
	
	public String getDoctorAhpra() {
		return doctorAhpra;
	}

	public void setDoctorAhpra(String doctorAhpra) {
		this.doctorAhpra = doctorAhpra;
	}
	
	public String getDoctorRequesterNumber() {
		return doctorRequesterNumber;
	}

	public void setDoctorRequesterNumber(String doctorRequesterNumber) {
		this.doctorRequesterNumber = doctorRequesterNumber;
	}

	public String getCcDoctorRequesterNumber() {
		return ccDoctorRequesterNumber;
	}

	public void setCcDoctorRequesterNumber(String ccDoctorRequesterNumber) {
		this.ccDoctorRequesterNumber = ccDoctorRequesterNumber;
	}

	public String getDoctorPhone() {
		return doctorPhone;
	}

	public void setDoctorPhone(String doctorPhone) {
		this.doctorPhone = doctorPhone;
	}

	public boolean isCopyToMe() {
		return copyToMe;
	}

	public void setCopyToMe(boolean copyToMe) {
		this.copyToMe = copyToMe;
	}

	public boolean isUrgentResult() {
		return urgentResult;
	}

	public void setUrgentResult(boolean urgentResult) {
		this.urgentResult = urgentResult;
	}

	public Integer getDoctorFailures() {
		return doctorFailures == null ? 0 : doctorFailures;
	}

	public void setDoctorFailures(Integer doctorFailures) {
		this.doctorFailures = doctorFailures == null ? 0 : doctorFailures;
	}

	public Integer getPatientFailures() {
		return patientFailures == null ? 0 : patientFailures;
	}

	public void setPatientFailures(Integer patientFailures) {
		this.patientFailures = patientFailures == null ? 0 : patientFailures;
	}

	@Override
	public String toString() {
		return "ElectronicReferralForm [id=" + id + ", patientName=" + patientName + ", patientDob=" + patientDob
				+ ", patientGender=" + patientGender + ", patientPhone=" + patientPhone + ", patientStreet="
				+ patientStreet + ", patientSuburb=" + patientSuburb + ", patientPostcode=" + patientPostcode
				+ ", patientState=" + patientState + ", patientEmail=" + patientEmail + ", patientCompensation="
				+ patientCompensation + ", examDetails=" + examDetails + ", clinicalDetails=" + clinicalDetails
				+ ", patientPregnant=" + patientPregnant + ", ivContrastAllergy=" + ivContrastAllergy + ", ivRenal="
				+ ivRenal + ", ivDiabetes=" + ivDiabetes + ", ivCreatinineLevel=" + ivCreatinineLevel
				+ ", ivCreatinineEgfr=" + ivCreatinineEgfr + ", ivCreatinineDate=" + ivCreatinineDate + ", mriMetal="
				+ mriMetal + ", mriPacemaker=" + mriPacemaker + ", mriBrainClip=" + mriBrainClip + ", mriCochlear="
				+ mriCochlear + ", mriCoil=" + mriCoil + ", mriUltrasound=" + mriUltrasound + ", mriNumber=" + mriNumber
				+ ", mriDate=" + mriDate + ", doctorName=" + doctorName + ", doctorProviderNumber="
				+ doctorProviderNumber + ", doctorRequesterNumber=" + doctorRequesterNumber + ", doctorAhpra="
				+ doctorAhpra + ", doctorPhone=" + doctorPhone + ", doctorEmail=" + doctorEmail
				+ ", doctorPracticeName=" + doctorPracticeName + ", doctorStreet=" + doctorStreet + ", doctorSuburb="
				+ doctorSuburb + ", doctorState=" + doctorState + ", doctorPostcode=" + doctorPostcode
				+ ", ccDoctorName=" + ccDoctorName + ", ccDoctorProviderNumber=" + ccDoctorProviderNumber
				+ ", ccDoctorRequesterNumber=" + ccDoctorRequesterNumber + ", ccDoctorEmail=" + ccDoctorEmail
				+ ", ccDoctorPracticeName=" + ccDoctorPracticeName + ", ccDoctorStreet=" + ccDoctorStreet
				+ ", ccDoctorSuburb=" + ccDoctorSuburb + ", ccDoctorState=" + ccDoctorState + ", ccDoctorPostcode="
				+ ccDoctorPostcode + ", signatureName=" + signatureName + ", signatureDate=" + signatureDate
				+ ", signatureEntitled=" + signatureEntitled + ", filmReport=" + filmReport + ", pad=" + pad
				+ ", copyToMe=" + copyToMe + ", urgentResult=" + urgentResult + ", submittedTime=" + submittedTime
				+ "]";
	}

	@PrePersist
	protected void prePersist() {
		if (this.submittedTime == null)
			submittedTime = new Date();
	}

}
