package com.example.football.scoreboard.model;

import java.util.Scanner;

public class InputWrapper {

    private final Scanner scanner;

    public InputWrapper(Scanner scanner) {
        this.scanner = scanner;
    }

    public int nextInt() {
        return scanner.nextInt();
    }

    public String nextLine() {
        return scanner.nextLine();
    }

}
