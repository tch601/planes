package BsmchFlights.Models;

import BsmchFlights.Services.AirportsService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

public class MyJWT {
    private static final Logger logger = LoggerFactory.getLogger(AirportsService.class);
    private static final Algorithm algorithm = Algorithm.HMAC256("flights");
    private static final JWTVerifier verifier = JWT.require(algorithm)
            .withIssuer("Flights")
            .build();

    public static String getUserJWTToken(User user) {
        String jwt = JWT.create()
                .withIssuer("Flights")
                .withSubject("User Details")
                .withClaim("user", user.getAsString())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000L * 24 * 31))
                .withIssuedAt(new Date())
                .withJWTId(UUID.randomUUID()
                        .toString())
                .sign(algorithm);
        logger.info("token " + jwt + " for user: " + user.getUsername() + " was created");

        return jwt;
    }

    public static User decodeUser(String jwtToken) throws JWTVerificationException {
        DecodedJWT decodedJWT = verifier.verify(jwtToken);

        return User.stringToUser(decodedJWT.getClaim("user").asString());
    }
}
