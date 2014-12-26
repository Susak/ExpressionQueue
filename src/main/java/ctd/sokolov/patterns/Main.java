package ctd.sokolov.patterns;

import ctd.sokolov.patterns.expression.Parser;
import ctd.sokolov.patterns.server.Server;

/**
 * Created by ruslan on 12/26/14.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.out.println("Usage <expression>");
        }
        Server.run();
        System.out.println(new Parser().evaluate(args[0]));
        Server.stop();
        System.exit(0);
    }
}
