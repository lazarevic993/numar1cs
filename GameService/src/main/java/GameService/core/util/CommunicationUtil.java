package GameService.core.util;

import static GameService.core.config.ApplicationUrls.GAME_SERVICE_ADDRESS;

public class CommunicationUtil {

    public static String createURLWithPort(String uri) {
        return GAME_SERVICE_ADDRESS + uri;
    }

}

