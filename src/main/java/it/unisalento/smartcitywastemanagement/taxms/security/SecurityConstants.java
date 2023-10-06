package it.unisalento.smartcitywastemanagement.taxms.security;

import java.util.List;

public class SecurityConstants {
    public static final String JWT_SECRET = "seedseedseedseedseedseedseedseedseedseedseed"; // sarà utilizzato per l'algoritmo di firma

    public static final String THIS_MICROSERVICE ="http://taxService:8085";

    public static final String ISSUER ="http://taxService:8085";


    public static final List<String> AUDIENCE = List.of("http://disposalManagementService:8083");

    public static final String SUBJECT = "taxMS";

    public static final String ROLE = "MICROSERVICE-COMMUNICATION";

}
