package au.com.imed.portal.referrer.referrerportal.common.util;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwk.JsonWebKey;
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
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationUtil {
    private static Logger logger = LoggerFactory.getLogger(AuthenticationUtil.class);

    private static final String AUTHWORD = "Bearer";
    private static final String ISSUER = "I-MED Radiology";
    private static final String AUDIENCE = "Referrer Portal Client";
    private static final String SUBJECT_ACCESS = "I-MED Token ACCESS";
    private static final String SUBJECT_REFRESH = "I-MED Token REFRESH";
    private static final float EXPIRE_ACCESS = 20;
    private static final float EXPIRE_REFRESH = 60 * 24 * 7; // 7days
    private static final String WEBKEY_STRING = "eyJhbGciOiJQQkVTMi1IUzI1NitBMTI4S1ciLCJlbmMiOiJBMTI4Q0JDLUhTMjU2IiwicDJjIjo4MTkyLCJwMnMiOiIzRkNyanliYTBwOXZvQXNUIn0.fXlwgtkn_s3aHcU6gdsxbO8g3UiFbDQUvfyN_w4Tokhi7cUQ_rPjvw.KoP4zXOJaqNwv8Tw7foqpQ.M3TOzEVn6STorZRKjB9ey0ku6tqeLLyG-hnCRAwy6ArZraI1V45bjkflYWo168hAf6VQOihWQhZTSS4TdcYm0fGPIOwrKYEerqlr94Pqs3ekJNsLjgP0gLHPwxreeWfndP8h6HN9aKh39YnKg80TgGqN06eIZCpK0i65XwY_BrKTjtkUm4h-iUgtzawGe9JD2O6RXb3ZfxlT28EDm4NtLUoaTPohsrTzAQn_bOCrxQbsY9v2q_wVrBZeyCnbY9BY6uny9xqUi5B2qruAhHag9vBV5sG5W8DBvwXeDVIQYb5jQazjHn1haN92QXKVd7UyXLcM1DxDlZhrd66rWVPnDcv5Zpp8uzPGXVLA3uQtN0r-Ujg_v5nmOZxBQA2X38WoJYNi9lTa8So7Xl23A9iagoP9hJ4tLCuD8Z-PitcZdZKlqA5QtQWpwC5LkyF0bfGC-rZrHRt7vCf4LPAshQZ_CHHDrhXZgqzWvSkob8FE7XxPWh0UcZQ9XvtReOaM_w4Va-WundCWM3-LVWbRtq87cCfLIAvrwppHinYVfR28CXlai53BngHCT7TzdpSxXRd-1XjlBWMn5ks-N3ppmQCwIH-m3sFpkFiy2KlhLvNYE8RCLTGwLz9tvgVyMldd4cW5IcaaLymO04Yy46627xyqhL30fFHHfa-u1-Pa71AHt-Wi5jGRi0Mfa90l6aMNFrwW_z-Ck4u4Aahpf7pDzUezNnc8DEUMI6ln1vKVDs7JJf19Mp5eEa-hvwH_AoJxokurXXnhobcVF8Gk4IfikbJzoVdbSEsJKUZAXG3dOzHALCXA-qa-QHzXWpO5CSMICUcAM0lRrSqdXsDqVC5d8v41jh8GM00xI3bn7MRvAAH9Ja7k3z33k87FTsNDM7Y1_lRrKBcjxPt4_twaGXL2hIQ4VLP8CmOMmxN5WCmU0hhhSIUHO3olpC7ugAMvGnfcGPinjhV_1jg89czVvYvECfq-vCoWc3r8atFCu6zPTzaz-qxI4GKIosb7ufcL34TrOnX3IXaZrsfEprpLV7ArB-0ZOcCU9K3RcLDqqYgauABJN9rc7b4Tpu90jSEvujitSjRCLmmj0Dk85VuN9nHe0eU1toMTv72VauMbbGDlmj7D2C_PzujbeoOqrWnuPAKDfBwGjhfzAD7fVy8tgQZT-hBkcD8a3AM3l6t4wDZMbaXaDacCc_xlQsfvAHZU3w600LZtPwDpOSlm8RFdfOi5FHwOtYQsQvnrDQF84RI2oK1EQ5ZQHsHDY1glrHjb4gqbvyPcA9jA1J4VscqHyT151x5rjXAAmQGtETvAh0EMF_papCtlz-5KPr8t3PwTySmroU0srEYfY2pXbgiQOexR1r8JS5A7N97VipSazZmex89-LHpo3IM31NMq8fJQ_Yi-uFxSZ55MlZnJrI1xQ4qqShWkij7hVdodWLwMPhQYA7GwOr8ytbuUt2NUFyRWG_htnog_YZn2kZE35eS6LYyvd8a6wXedf981OvK1ehUQuy99I3v8SHNV4B4z7OFKjsgzYAJVXAMAUjwnb4sgwF1LblFgCDp-qC5ccgTEc2nrZfGfavvktq381mrZBZTkeDVcSMr_thrDy3-J_bJFwtRF98KLwHMWxI8Cyi8UP4Wlcoo8gIMdRB2Vz4QDpn0u1n5CYQWtuBORrJXUjHL0qtzGqP0wwJog9uP0bHwherVL-xSw-Yi95J4rfMSJGpz4spvuKYuMq78gQczi6o94bbbDWvCHjoxnYpEyyGSrm3KLmWlXgAn8k5kb7HcEf4w7--B4G73xJitz2g_fJcLiWJAeNrcgZYPXS5lA0rUZkWNUdBxh_FBgRyl53ryGvF-Epv4uR8MleGT-MmZQqBpWTqAKpXNAe-LB-YFZsG7durJ-SEoPuV3nSJc_oTq9cDf7hK7XywlVcAv2SPOBEAUMwoHLzWBuCvF2-4Db8AcBhkjCimCQDFifS6OjMsZ8ta7Mt99f21YpBwyY-Cka2xOd7gq58H9g3eRa9wOqP9phmMSUDi4qtk6zrM_ICjEB9JHLZx9BMFkuvgjsqwk3S5sBJTTD4YqN5NrKoRZruT0eNQoffzHbZan6UqgRWGeemXiemL_v-DXBg9qkjJ-SrnU5FbqHRqCgrg5BLsGKeiMotEvHhYM6uojfNb7V-fDnIS4pLbNFXW9pW1Hh8veHoLG7h2VVx40vPA.erlYmPKgVsSmUdRJp9rpAA";

    /**
     * usage example
     */
//  public static void main(String[] args) throws Exception {
//    final String username = "hellouser";
//    String auth = AUTHWORD + " " + createAccessToken(username);
//    System.out.println(checkAccessToken(auth));
//    auth = AUTHWORD + " " + createRefreshToken(username);
//    System.out.println(checkRefreshToken(auth));
//  }

    ///
    /// Public methods to check/create token for client
    ///
    public static String createAccessToken(final String username) throws Exception {
        return createToken(username, null, EXPIRE_ACCESS, SUBJECT_ACCESS);
    }

    ///
    /// Public methods to check/create token for client
    ///
    public static String createAccessToken(final String username, List<String> groups) throws Exception {
        return createToken(username, groups, EXPIRE_ACCESS, SUBJECT_ACCESS);
    }


    public static String createRefreshToken(final String username) throws Exception {
        return createToken(username, null, EXPIRE_REFRESH, SUBJECT_REFRESH);
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

        var jwtConsumer = getJwtConsumer(rsaJsonWebKey);

        try {
            //  Validate the JWT and process it to the Claims
            //JwtClaims jwtClaims = jwtConsumer.processToClaims(new String(Base64.decodeBase64(jwt)));
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
            System.out.println("JWT validation succeeded! \n" + jwtClaims);
            System.out.println("username : " + jwtClaims.getClaimValue("username"));
            System.out.println("subject : " + jwtClaims.getSubject());
            if (subject.equals(jwtClaims.getSubject().toString())) {
                System.out.println("subject is correct");
                username = jwtClaims.getClaimValue("username").toString();
            } else {
                System.out.println("token error : subject is different");
            }
        } catch (InvalidJwtException e) {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            System.out.println("Invalid JWT! " + e);

            // Programmatic access to (some) specific reasons for JWT invalidity is also possible
            // should you want different error handling behavior for certain conditions.

            // Whether or not the JWT has expired being one common reason for invalidity
            if (e.hasExpired()) {
                System.out.println("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
            }

            // Or maybe the audience was invalid
            if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID)) {
                System.out.println("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
            }
        }
        return username;
    }

    private static JwtConsumer getJwtConsumer(RsaJsonWebKey rsaJsonWebKey) {
        // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
        // be used to validate and process the JWT.
        // The specific validation requirements for a JWT are context dependent, however,
        // it typically advisable to require a (reasonable) expiration time, a trusted issuer, and
        // and audience that identifies your system as the intended recipient.
        // If the JWT is encrypted too, you need only provide a decryption key or
        // decryption key resolver to the builder.
        return new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(0) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer(ISSUER) // whom the JWT needs to have been issued by
                .setExpectedAudience(AUDIENCE) // to whom the JWT is intended for
                .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
                .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                        new AlgorithmConstraints(ConstraintType.WHITELIST, // which is only RS256 here
                                AlgorithmIdentifiers.RSA_USING_SHA256))
                .build(); // create the JwtConsumer instance
    }

    /**
     * Generate both access and refresh. just expiry and subject differs.
     *
     * @param username
     * @param claims
     * @param expiry
     * @param subject
     * @return
     * @throws Exception
     */
    private static String createToken(final String username, List<String> groups, final float expiry, final String subject) throws Exception {
        System.out.println("createToken() " + subject);
        RsaJsonWebKey rsaJsonWebKey = getWebKey();

        JwtClaims claims = new JwtClaims();
        claims.setIssuer(ISSUER);  // who creates the token and signs it
        claims.setAudience(AUDIENCE); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(expiry); // time when the token will expire (10 minutes from now)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        //claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
        claims.setSubject(subject); // the subject/principal is whom the token is about
        //claims.setClaim("email","mail@example.com"); // additional claims/attributes about the subject can be added
        claims.setClaim("username", username); // additional claims/attributes about the subject can be added

        claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array

        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the private key
        jws.setKey(rsaJsonWebKey.getPrivateKey());

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        String jwt = jws.getCompactSerialization();
        System.out.println("JWT original : \n" + jwt);
        System.out.println("original len " + jwt.length());
        //jwt = Base64.encodeBase64String(jwt.getBytes());
        //System.out.println("base64 len " + jwt.length());

        // Now you can do something with the JWT. Like send it to some other party
        // over the clouds and through the interwebs.
        //System.out.println("JWT base64 : \n" + jwt);
        return jwt;
    }

    private static final String SECRET_KEY = "S3cretK3y0FImedReferre5To9en";

    /**
     * Call only one time to save as real key
     */
//  private static String createJwkString() throws Exception {
//    RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
//    rsaJsonWebKey.setKeyId("I-MED Radiology");
//    System.out.println(rsaJsonWebKey.toJson());
//    String jwkjson = rsaJsonWebKey.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE);
//    JsonWebEncryption encryptingJwe = new JsonWebEncryption();
//    encryptingJwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.PBES2_HS256_A128KW);
//    encryptingJwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
//    encryptingJwe.setKey(new PbkdfKey(SECRET_KEY));
//    encryptingJwe.setPayload(jwkjson);
//    String jweEncryptedJwk = encryptingJwe.getCompactSerialization();
//    System.out.println("Key to save : \n" + jweEncryptedJwk);
//    return jweEncryptedJwk;
//  }
    private static RsaJsonWebKey getWebKey() throws Exception {
        JsonWebEncryption decryptingJwe = new JsonWebEncryption();
        decryptingJwe.setCompactSerialization(WEBKEY_STRING);
        decryptingJwe.setKey(new PbkdfKey(SECRET_KEY));
        String payload = decryptingJwe.getPayload();
        PublicJsonWebKey publicJsonWebKey = PublicJsonWebKey.Factory.newPublicJwk(payload);
        // share the public part with whomever/whatever needs to verify the signatures
        System.out.println(publicJsonWebKey.getClass());
        System.out.println(publicJsonWebKey.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY));
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
        System.out.println("getBearer() " + token);
        return token;
    }

    public static String getAuthenticatedUserName(final String auth) {
        String userName = null;

        // JWT
        if (auth != null && auth.length() > 0) {
            try {
                final String usernameInToken = AuthenticationUtil.checkAccessToken(auth);
                if (usernameInToken != null) {

                    userName = usernameInToken;

                    logger.info("getAuthenticatedUserName() " + PortalConstant.HEADER_AUTHENTICATION + " VALID with "
                            + userName);
                } else {
                    logger.info(
                            "getAuthenticatedUserName() " + PortalConstant.HEADER_AUTHENTICATION + " invalid token");
                }
            } catch (Exception ex) {
                logger.info("getAuthenticatedUserName() " + PortalConstant.HEADER_AUTHENTICATION + " invalid");
            }
        }

        if (userName == null) {
            // Spring security for logged in user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication instanceof AnonymousAuthenticationToken)) {
                userName = authentication.getName();
            }
        }

        logger.info("Authenticated user name : " + userName);

        return userName;
    }

    public static Collection<? extends GrantedAuthority> getAuthorities(String authenticationHeader) throws Exception {
        Object roles = getJwtConsumer(getWebKey()).processToClaims(getBearer(authenticationHeader)).getClaimValue("groups");
        if (roles instanceof List) {
            return ((List<String>) roles).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return new HashSet<>(1);
    }
}
