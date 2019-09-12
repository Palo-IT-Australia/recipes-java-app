package au.com.imed.portal.referrer.referrerportal.common.util;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.PbkdfKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.ldap.ABasicAccountService;

public class AuthenticationUtil extends ABasicAccountService {
	private static Logger logger = LoggerFactory.getLogger(AuthenticationUtil.class);

	private static final String AUTHWORD = "Bearer";
	private static final String ISSUER = "I-MED Radiology";
	private static final String AUDIENCE = "I-MED Referrers";
	private static final String SUBJECT_ACCESS = "Referrer portal Token ACCESS";
	private static final String SUBJECT_REFRESH = "Referrer portal Token REFRESH";
	private static final float EXPIRE_ACCESS = 20;
	private static final float EXPIRE_REFRESH = 60 * 24 * 7; // 7days
	private static final String WEBKEY_STRING = "eyJhbGciOiJQQkVTMi1IUzI1NitBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwicDJjIjo4MTkyLCJwMnMiOiJwTUYwd1NqZ01pX1ZYYnp2In0.GrfpV1L5V_tFNPdcgh09CsDl8eqpOF2Gj73hJsml8bLsjUxQqvHsPA.r8RsAT_oUxtvURgF6NLysw.l7n5nS_M6gGri1_cF5kti70FDEsJznx8Lgx4VoTMW5v0ZRxWRAaASvXFxnjYd-gwpS3CzCwLlk-w0eMTCd0vP201eq2bgo2vzZmD-n_HBCPWjSDxfjzPnsco0d2zvjysn-dkovVld_rY6CP0StBMp_editmLSbT6qltSg2_CYQgDVnCcR3Mw0K7LPoAre8SUp6JYqyYir8Le7751zTzM3XlgVNBCVsRfTJKBjqUwi4AGcQNc-q9sc9LCTMZq5wQinbUeFy0XLK-dKAe6ljDMIGFecwAxq6pkYM8PSu1cgy6zqXHdXn7_zjXGIvS3dU0dXAIzI1jIUWN7yphvaugPoEAl87OFK-20Xk8dIeJFylmqmg3ZmRld8XYLn615JG3ACuWwvYA4ZGZY9OoDzXsmifS9rwR1BscZtpgY1G-yCobzTZ7RJBFNzXxDG2PHLGaaQt_FWgcs5I4jF9wRvFsuddzHAwkDzs13Gdz5zd3abiIrviWzLFShwxo52mK75vnUKaubGuz2EMvwOhWxyuP0BGpR5Y8CNdyYVyCOVG0UGv8xrta9BPgfUo8yE6vdPYIkaMZ3TWAdXU-fOoy9914bcZDzDExx4g6M3PthQOvhTdlqHouEo2TeaQs015H5C6Snsrk2tNZEg0eMEpjP5upg2_qkn4s--ErX9S2Kt612hYUMzlxhHMB_e_elv7FBA1ygqeeyak0F4pAPoLNsIOm4t-jDotwRkEagqQe8FrEyHK17LCp0w2H2zau1cFuPVsXcl8pDCyYOQ1EwJBZpiO4kQ-a5KC0c-bynfq2V1Wf2_9efzHzN1xhhQGxGmBssnxsjN-7MLhylAm3rkUArqLEhE7Kb8Pr-Ja6ZIdlZwnSLkWY1a9x385Opm6-Dbc5k03IbkjvjLnZjIFOLSmQ9Ss3Mcg-CojXdUAUOJcgx59shs5_DyV-6b5xR_hvmF0c7W5GHxAxN7-ynzlJAtlrOOdA0iMN_d0RMpZu8g5xWVTDf-SbadI36aCLM681LYRZyQSjMo70IwC8i9xw2HEmIPyrO0wgIxEP88T_7_Fz89laTvnGZqxTSbFUjZtVAU2BDSu9E9s8EsTHFDv4bay5TOgJidmB0kb9lyt4AnLL8uypz4B1SYDshOWLXST-nf5Qqh6-osw-oe6Uvhi5qoyo5aVTpe4UXsLrES9wmoxMewqt8ztfLPfTzXO_ZoCUeyiYmvkHe5OHyty12WNyZ3oM5L_rcQezHWWHGJHZgwJE4Un3MQw537qT3n0RZ0UwIxWf1L6iBlmo5NsPxEBbPhZr84xexHiWTNaen0wcVq1VD2Ft9AiYx2euY9D7LF0VFpEFqDg_tzj4rzHW7B1FrZJKWrYo33PlR6Qs5tsmNRxK3NqmdxQ-x7kVNKJDlzdNoqNm9E1Hk33OzbIPxTKIjxK_fh6tu78_q2W96RSUXbzIpZAmfw0fH3PZSuyUhmFMhwTsdzNVrLAHgn73p5fIl6RJMWUHueumPe2T9hIU52kRlVHRS1Rwj6XaJnGmZbAET3Uvn_s_AiSJMC4JRyq6Zkn26jF_U6FnqYtBwK-4CgMfDiAJNwSELru46UOet7PqwCF_cHwFO4DivhFKik2psxNduYhewogrQRVvr9btwLEKdkRwoqzxpm48pa76RsOu9JBOEYKhsg9ZQsHSW-GFR5XQ9nSbmIeYnndF9Z2VkLe1npNGl2zb8l0u8VfeAJMQNH2nwhJwreljUFKFmMxRHcdDzBQmaX_-Y_J115orS5W7CvHjF2-1Cvvv0Q-hcThno_Z1YO2JXAdF2uQpY44Ud2_MAQJWCiOgsXcZR_t4iJ3nikHd0v4Rfs-tj_tffCxQvEsQpBAZN6-b4Iu-Fb2CaiaCM53GymRYf6jhtz5S_rMUwXn03QXbDjUMzyvusXZHga7kcZhLRea22i7mRFudiKqjHqICC1lkvjt40slJ7ibRJfnsDNOuX0h-S_W-7gC83lvgTTr7ec4sSq-AWRZfWns9zZJdkdwolDHzVavUaYqGthe22xmzTs9pJnq0_Ad589j5FTGpQigpwvYrngdRgokQnOZKcJW3eiGlKckFSudaT52yYlsLbykCDDt-DcNKmP-TfEcupv1h12P1NFzIYQhZC4yg_jWddSY0Xzgf4dc8N1tItdiS0tTcKlD9d7XftiZq5lgdScwE2j4ouJkZWL2-5Q8izcA.7YmCv6xZjCNCwmoZTpoo3Q";
	private static final String SECRET_KEY = "my1Med7ec3erk3y4restJWTTOKen";

	///
	/// Public methods to check/create token for client
	///
	public static String createAccessToken(final String username) throws Exception {
		return createToken(username, EXPIRE_ACCESS, SUBJECT_ACCESS);
	}

	public static String createRefreshToken(final String username) throws Exception {
		return createToken(username, EXPIRE_REFRESH, SUBJECT_REFRESH);
	}

	public static String checkAccessToken(final String authentication) throws Exception {
		return checkToken(getBearer(authentication), SUBJECT_ACCESS);
	}

	public static String checkRefreshToken(final String authentication) throws Exception {
		return checkToken(getBearer(authentication), SUBJECT_REFRESH);
	}

	/**
	 * Check with subject and return username bind to token if valid.
	 * 
	 * @param jwt
	 * @param subject
	 * @return null if any error or subject differs
	 * @throws Exception
	 */
	private static String checkToken(final String jwt, final String subject) throws Exception {
		String username = null;

		RsaJsonWebKey rsaJsonWebKey = getWebKey();

		JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime() // the JWT must have an expiration
																						// time
				.setAllowedClockSkewInSeconds(0) // allow some leeway in validating time based claims to account for
													// clock skew
				.setRequireSubject() // the JWT must have a subject claim
				.setExpectedIssuer(ISSUER) // whom the JWT needs to have been issued by
				.setExpectedAudience(AUDIENCE) // to whom the JWT is intended for
				.setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
				.setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
						new AlgorithmConstraints(ConstraintType.WHITELIST, // which is only RS256 here
								AlgorithmIdentifiers.RSA_USING_SHA256))
				.build(); // create the JwtConsumer instance

		try {
			JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
			logger.info("username : " + jwtClaims.getClaimValue("username"));
			logger.info("subject : " + jwtClaims.getSubject());
			if (subject.equals(jwtClaims.getSubject().toString())) {
				username = jwtClaims.getClaimValue("username").toString();
			} else {
				logger.warn("token error : subject is different");
			}
		} catch (InvalidJwtException e) {
			logger.warn("Invalid JWT! " + e);
			if (e.hasExpired()) {
				logger.warn("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
			}

			// Or maybe the audience was invalid
			if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) {
				logger.warn("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
			}
		}
		return username;
	}

	/**
	 * Generate both access and refresh. just expiry and subject differs.
	 * 
	 * @param username
	 * @param expiry
	 * @param subject
	 * @return
	 * @throws Exception
	 */
	private static String createToken(final String username, final float expiry, final String subject)
			throws Exception {
		RsaJsonWebKey rsaJsonWebKey = getWebKey();

		JwtClaims claims = new JwtClaims();
		claims.setIssuer(ISSUER); // who creates the token and signs it
		claims.setAudience(AUDIENCE); // to whom the token is intended to be sent
		claims.setExpirationTimeMinutesInTheFuture(expiry); // time when the token will expire (10 minutes from now)
		claims.setGeneratedJwtId(); // a unique identifier for the token
		claims.setIssuedAtToNow(); // when the token was issued/created (now)
		// claims.setNotBeforeMinutesInThePast(2); // time before which the token is not
		// yet valid (2 minutes ago)
		claims.setSubject(subject); // the subject/principal is whom the token is about
		// claims.setClaim("email","mail@example.com"); // additional claims/attributes
		// about the subject can be added
		claims.setClaim("username", username); // additional claims/attributes about the subject can be added
		// List<String> groups = Arrays.asList("group-one", "other-group",
		// "group-three");
		// claims.setStringListClaim("groups", groups); // multi-valued claims work too
		// and will end up as a JSON array

		JsonWebSignature jws = new JsonWebSignature();
		jws.setPayload(claims.toJson());
		jws.setKey(rsaJsonWebKey.getPrivateKey());
		jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
		jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

		String jwt = jws.getCompactSerialization();
		return jwt;
	}

	private static RsaJsonWebKey getWebKey() throws Exception {
		JsonWebEncryption decryptingJwe = new JsonWebEncryption();
		decryptingJwe.setCompactSerialization(WEBKEY_STRING);
		decryptingJwe.setKey(new PbkdfKey(SECRET_KEY));
		String payload = decryptingJwe.getPayload();
		PublicJsonWebKey publicJsonWebKey = PublicJsonWebKey.Factory.newPublicJwk(payload);
		return (RsaJsonWebKey) publicJsonWebKey;
	}

	private static String getBearer(final String authentication) {
		String token = null;
		if (authentication != null && authentication.trim().indexOf(AUTHWORD) == 0) {
			String bearer[] = authentication.trim().split("\\s+");
			if (bearer.length > 1) {
				return token = bearer[1];
			}
		}
		return token;
	}

	public static String getAuthenticatedUserName(final String auth) {
		String userName = null;

//		// JWT
//		if (auth != null && auth.length() > 0) {
//			try {
//				final String usernameInToken = AuthenticationUtil.checkAccessToken(auth);
//				if (usernameInToken != null) {
//					userName = CommonUtil.emailToUid(usernameInToken);
//					logger.info("getAuthenticatedUserName() " + PortalConstant.HEADER_AUTHENTICATION + " VALID with "
//							+ userName);
//				} else {
//					logger.info(
//							"getAuthenticatedUserName() " + PortalConstant.HEADER_AUTHENTICATION + " invalid token");
//				}
//			} catch (Exception ex) {
//				logger.info("getAuthenticatedUserName() " + PortalConstant.HEADER_AUTHENTICATION + " invalid");
//			}
//		}
//
//		if (userName == null) {
//			// Spring security for logged in user
//			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//			if (!(authentication instanceof AnonymousAuthenticationToken)) {
//				userName = authentication.getName();
//			}
//		}
//
//		logger.info("Authenticated user name : " + userName);

		return "alchau";
	}

}
