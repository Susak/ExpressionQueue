package ctd.sokolov.patterns.server;

/**
 * Created by ruslan on 12/26/14.
 */
public class ServerBackEnd {


    public static int getResponse(String request) {
        int result;
        try {
            result = Integer.parseInt(request);
        } catch (NumberFormatException e) {
            result = evaluateOp(request);
        }
        return result;
    }

    private static int evaluateOp(String request) {
        String[] array = request.split("\\+|\\-|\\/|\\*");
        int l = Integer.parseInt(array[0]);
        int r = Integer.parseInt(array[1]);
        char op = getOp(request);
        switch (op) {
            case '+':
                return l + r;
            case '-':
                return l - r;
            case '/':
                return l / r;
            case '*':
                return l * r;
            default:
                throw new IllegalArgumentException("Unexpected symbol " + op);
        }
    }

    private static char getOp(String st) {
        for (char c : st.toCharArray()) {
            if (!Character.isDigit(c)) {
                return c;
            }
        }
        return 0;
    }
}
