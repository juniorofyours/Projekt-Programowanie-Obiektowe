package org.example;


/**
 * Klasa używana do wypisywania tekstu w danym wybranym kolorze w konsoli
 */
public class ConsoleColors {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    public static void printlnRed(String text) {
        System.out.println(RED + text + RESET);
    }

    public static void printlnGreen(String text) {
        System.out.println(GREEN + text + RESET);
    }

    public static void printlnYellow(String text) {
        System.out.println(YELLOW + text + RESET);
    }

    public static void printlnBlue(String text) {
        System.out.println(BLUE + text + RESET);
    }

}