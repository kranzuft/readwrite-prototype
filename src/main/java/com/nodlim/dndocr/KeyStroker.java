package com.nodlim.dndocr;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class KeyStroker {
    void type(String str) {
        try {
            Robot robot = new Robot();

            TimeUnit.MILLISECONDS.sleep(500);
            int lines = 1;
            for (char ch : str.toCharArray()) {
                if (Character.isUpperCase(ch)) {
                    doShifted(robot, ch);
                } else if ("!@#$%^&*(){}_:+~<>?<>?|".contains(ch + "")) {
                    char modified = mapUpperSpecial(ch);
                    doShifted(robot, modified);
                } else if (ch == '\n') {
                    doUnshifted(robot, KeyEvent.VK_ENTER);
                    lines ++;
                } else if (ch == '"') {
                    doShifted(robot, KeyEvent.VK_QUOTE);
                } else if (ch == '\'') {
                    doUnshifted(robot, KeyEvent.VK_QUOTE);
                } else if (ch == '`') {
                    doUnshifted(robot, KeyEvent.VK_BACK_QUOTE);
                } else if (ch == '~') {
                    doShifted(robot, KeyEvent.VK_BACK_QUOTE);
                } else if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)) {
                    char upCh = Character.toUpperCase(ch);
                    doUnshifted(robot, upCh);
                } else if (",./;\\=`-[]".contains(ch + "")) {
                    doUnshifted(robot, ch);
                } else {
                    System.out.println("Not sure what to do with: " + ch);
                }
                if (lines % 5 == 0) {
                    TimeUnit.SECONDS.sleep(5);
                    lines = 1;
                }
            }
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void doUnshifted(Robot robot, int keyEvent) {
        robot.keyPress(keyEvent);
        robot.keyRelease(keyEvent);
    }

    private void doShifted(Robot robot, int keyEvent) {
        robot.keyPress(KeyEvent.VK_SHIFT);
        doUnshifted(robot, keyEvent);
        robot.keyRelease(KeyEvent.VK_SHIFT);
    }

    private char mapUpperSpecial(char ch) {
        switch (ch) {
            case '!' -> {
                return '1';
            }
            case '@' -> {
                return '2';
            }
            case '#' -> {
                return '3';
            }
            case '$' -> {
                return '4';
            }
            case '%' -> {
                return '5';
            }
            case '^' -> {
                return '6';
            }
            case '&' -> {
                return '7';
            }
            case '*' -> {
                return '8';
            }
            case '(' -> {
                return '9';
            }
            case ')' -> {
                return '0';
            }
            case '+' -> {
                return '=';
            }
            case ':' -> {
                return ';';
            }
            case '"' -> {
                return '\'';
            }
            case '~' -> {
                return '`';
            }
            case '_' -> {
                return '-';
            }
            case '<' -> {
                return ',';
            }
            case '>' -> {
                return '.';
            }
            case '?' -> {
                return '/';
            }
            case '|' -> {
                return '\\';
            }
            case '{' -> {
                return '[';
            }
            case '}' -> {
                return ']';
            }
            default -> System.out.println("Don't know how to handle: " + ch);
        }
        return ' ';
    }
}
