package ctd.sokolov.patterns.expression.analyzer;

import ctd.sokolov.patterns.Token;
import ctd.sokolov.patterns.TokenType;
import ctd.sokolov.patterns.exception.ParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * author: Ruslan Sokolov
 * date: 3/31/14
 */
public class LexicalAnalyzer {
    private InputStream in;
    private int curChar;
    private int prevChar;
    private int curPos;
    private Token curToken;

    public LexicalAnalyzer(InputStream in) {
        this.in = in;
        curPos = -1;
        nextChar();
    }

    private boolean isBlank() {
        return curChar == ' ' || curChar == '\t' || curChar == '\n' || curChar == '\r';
    }

    private void nextChar() {
        curPos++;
        prevChar = curChar;
        try {
            curChar = in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readNumber(boolean addMinus) {
        StringBuilder sb = new StringBuilder();
        if (addMinus) sb.append('-');
        do {
            sb.append((char) curChar);
            nextChar();
        } while (Character.isDigit((char) curChar));
        return sb.toString();
    }

    public void nextToken() throws ParseException {
        while (isBlank()) {
            nextChar();
        }
        switch (curChar) {
            case '(':
                curToken = new Token("(", TokenType.LPAREN);
                nextChar();
                break;
            case ')':
                curToken = new Token(")", TokenType.RPAREN);
                nextChar();
                break;
            case '+':
                curToken = new Token("+", TokenType.PLUS);
                nextChar();
                break;
            case '*':
                curToken = new Token("*", TokenType.MUL);
                nextChar();
                break;
            case '-':
                if (prevChar == 0 || prevChar == '(' || prevChar == '\n') {
                    nextChar();
                    if (Character.isDigit((char) curChar)) {
                        curToken = new Token(readNumber(true), TokenType.INTEGER);
                    } else if (curChar == '(') {
                        curToken = new Token("-", TokenType.UNARY_MINUS);
                    }
                } else {
                    curToken = new Token("-", TokenType.MINUS);
                    nextChar();
                }
                break;
            case '^':
                curToken = new Token("^", TokenType.POWER);
                nextChar();
                break;
            case '/':
                curToken = new Token("/", TokenType.DIV);
                nextChar();
                break;
            case -1:
                curToken = new Token("$", TokenType.END);
                break;
            default:
                if (Character.isDigit((char) curChar)) {
                    curToken = new Token(readNumber(false), TokenType.INTEGER);
                } else {
                    throw new ParseException("Illegal character " + (char) curChar + curPos);
                }
        }
    }

    public Token getToken() {
        return curToken;
    }
}

