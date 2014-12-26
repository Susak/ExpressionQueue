package ctd.sokolov.patterns.utils;

import ctd.sokolov.patterns.server.Server;

/**
 * Created by ruslan on 12/26/14.
 */
public class TestUtils {

    public static void createSession() {
        try {
            Server.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stopSession() {
        Server.stop();
    }
}
