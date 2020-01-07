package au.com.imed.portal.referrer.referrerportal.ahpra;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AhpraBotService {
	public AhpraDetails [] findByNumber(final String ahpra) {
		// TODO URL
		return new RestTemplate().getForObject("/datascrapper/ahpraDetail?ahpraNumber=" + ahpra, AhpraDetails[].class);
	}
	
//	public static void main(String args []) throws Exception {
//		ObjectMapper om = new ObjectMapper();
//		AhpraDetails [] l = om.readValue(new FileReader("c:\\temp\\ahpra.json"), AhpraDetails[].class);
//		for(AhpraDetails a : l)
//			System.out.println(a);
//	}
}
