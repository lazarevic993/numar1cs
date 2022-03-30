package PlayerService.core.util;

import static PlayerService.core.config.ApplicationUrls.GAME_SERVICE_ADDRESS;

public class CommunicationUtil {

    public static String createURLWithPort(String uri) {
        return GAME_SERVICE_ADDRESS + uri;
    }

}

