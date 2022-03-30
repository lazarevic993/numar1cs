package PlayerService.core.util;

import static PlayerService.core.config.ApplicationUrls.ADDRESS;

public class CommunicationUtil {

    public static String createURLWithPort(String uri) {
        return ADDRESS+ uri;
    }

}

