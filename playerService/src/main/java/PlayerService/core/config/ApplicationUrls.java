package PlayerService.core.config;


/**
 * Different constants used across the application
 *
 * @author Stefan Lazarevic
 */
public interface ApplicationUrls {

    String REST_API_V1_BASE_URI = "/api/v1";

    //core management
    String REST_API_V1_PLAYER = REST_API_V1_BASE_URI + "/player";


    //GameService
    String port = "8080";
    String BASE_URL = "http://localhost:";
    String API_VERSION = "/api/v1/";
    String GAME_SERVICE_ADDRESS = BASE_URL + port + API_VERSION;

}
