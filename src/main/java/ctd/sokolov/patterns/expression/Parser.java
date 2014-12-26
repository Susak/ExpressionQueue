package ctd.sokolov.patterns.expression;

import ctd.sokolov.patterns.expression.analyzer.LexicalAnalyzer;
import ctd.sokolov.patterns.Token;
import ctd.sokolov.patterns.TokenType;
import ctd.sokolov.patterns.client.Client;
import ctd.sokolov.patterns.exception.ParseException;
import ctd.sokolov.patterns.server.Server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;

/**
 * author: Ruslan Sokolov
 * date: 4/1/14
 */
public class Parser {
    private LexicalAnalyzer lex;
    private LinkedList<Token> operations = new LinkedList<>();
    private LinkedList<Integer> numbers = new LinkedList<>();

    private void polishNotation() throws ParseException {
        for (Token token = lex.getToken(); token.getType() != TokenType.END; lex.nextToken(), token = lex.getToken()) {
            if (token.getType() == TokenType.LPAREN) {
                operations.add(token);
            } else if (token.getType() == TokenType.RPAREN) {
                while (operations.getLast().getType() != TokenType.LPAREN) {
                    evaluate(operations.removeLast());
                }
                operations.removeLast();
            }
            else if (operation(token)) {
                while (!operations.isEmpty() && priority(operations.getLast()) >= priority(token)) {
                    evaluate(operations.removeLast());
                }
                operations.add(token);
            } else {
                numbers.add(evaluateString(token.getValue()));
            }
        }
    }

    private boolean operation(Token token) {
        switch (token.getType()) {
            case PLUS:
            case MINUS:
            case DIV:
            case MUL:
            case UNARY_MINUS:
                return true;
            default:
                return false;
        }
    }

    private int priority(Token token) {
        if (token.getType() == TokenType.UNARY_MINUS) {
            return 4;
        }
        switch (token.getType()) {
            case PLUS:
            case MINUS:
                return 1;
            case MUL:
            case DIV:
                return 2;
            default:
                return -1;
        }
    }

    private void evaluate(Token token) {
        if (token.getType() == TokenType.UNARY_MINUS) {
            String request = "-" + numbers.removeLast();
            numbers.add(evaluateString(request));
            return;
        }
        int r = numbers.removeLast();
        int l = numbers.removeLast();
        String request = l + token.getValue() + r;
        numbers.add(evaluateString(request));
    }

    private int getResult() {
        while (!operations.isEmpty()) {
            evaluate(operations.removeLast());
        }
        return numbers.getFirst();
    }

    public int evaluate(String expression) throws ParseException {
        InputStream in = new ByteArrayInputStream(expression.getBytes());
        lex = new LexicalAnalyzer(in);
        lex.nextToken();
        polishNotation();
        int result = getResult();
        if(lex.getToken().getType().equals(TokenType.END)) {
            return result;
        } else {
            throw new ParseException("Expression is not correct");
        }
    }

    private int evaluateString(String st) {
        int call = 0;
        try {
            call = Client.call(st);
        } catch (Exception e) {
            System.out.println("Error occurred while sent request");
            System.exit(1);
        }
        return call;
    }
}

