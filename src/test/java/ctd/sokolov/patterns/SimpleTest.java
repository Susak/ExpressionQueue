package ctd.sokolov.patterns;

import ctd.sokolov.patterns.expression.Parser;
import ctd.sokolov.patterns.server.Server;
import ctd.sokolov.patterns.utils.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Created by ruslan on 12/26/14.
 */
public class SimpleTest {

    @Before
    public void create() {
        TestUtils.createSession();
    }

    @Test
    public void tests() {
        Parser p = new Parser();
        String expression = "8/2/2*3+5*(1 +1)*4/2";
        int res = 26;
        assertEquals(p.evaluate(expression), res);
    }
}
