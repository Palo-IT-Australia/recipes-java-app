package au.com.imed.portal.referrer.referrerportal.ahpra;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AhpraBotService {
	public AhpraDetails [] findByNumber(final String ahpra) {
		// TODO real 
		//return new RestTemplate().getForObject("/datascrapper/ahpraDetail?ahpraNumber=" + ahpra, AhpraDetails[].class);
		
		String aaa = "[{\"Profession\":\"Health Practitioner\",\"Name\":\" Ms Michele Ann Johnson\",\"Personal Details\":{\"Sex\":\"Female\",\"Languages (in addition to English)\":\"\",\"Qualifications\":\"Bachelor of Applied Science (Physiotherapy), Curtin University, Australia, 1988\"},\"Principal Place of Practice\":{\"Suburb\":\"DUNCRAIG\",\"State\":\"WA\",\"Postcode\":\"6023\",\"Country\":\"Australia\"},\"Registration Details\":{\"Profession\":\"Physiotherapist\",\"Registration number\":\"PHY0001540395\",\"Date of first Registration in Profession\":\"23/01/1989\",\"Registration status\":\"Registered\",\"Registration expiry date\":\"Under the National Law, registrants are able to practise while their renewal application is being processed. Practitioners also remain registered for one month after their registration expiry date. If the practitioner's name appears on the Register, they are registered and can practise (excepting practitioners with a Registration Type of 'non-practising' or those with a condition which stops them from practising, or where their registration is suspended).\",\"Conditions\":\"None\",\"Undertakings\":\"None\",\"Reprimands\":\"None\"},\"Registration Type - General\":{\"Registration expiry date\":\"30/11/2020\",\"Endorsements\":\"None\",\"Notations - General\":\"None\",\"Registration Requirements\":\"None\"}}]";
		ObjectMapper om = new ObjectMapper();
		try {
			return om.readValue(aaa, AhpraDetails[].class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new AhpraDetails[0];
		}
	}
	
//	public static void main(String args []) throws Exception {
//		ObjectMapper om = new ObjectMapper();
//		AhpraDetails [] l = om.readValue(new FileReader("c:\\temp\\ahpra.json"), AhpraDetails[].class);
//		for(AhpraDetails a : l)
//			System.out.println(a);
//	}
}
