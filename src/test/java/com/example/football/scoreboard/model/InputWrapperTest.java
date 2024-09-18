package com.example.football.scoreboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InputWrapperTest {

    private InputWrapper inputWrapper;

    @BeforeEach
    public void setUp() {
        // Prepare input data for the Scanner
        String input = "42\nSelectOperations\n";

        // Use ByteArrayInputStream to simulate user input
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        inputWrapper = new InputWrapper(scanner);
    }
    @Test
    public void testNextInt() {
        // Act
        int result = inputWrapper.nextInt();

        // Assert
        assertEquals(42, result);
    }

    @Test
    public void testNextLine() {
        inputWrapper.nextInt(); // Consume the integer input
        inputWrapper.nextLine(); // Consume the leftover newline character

        String result = inputWrapper.nextLine();
        System.out.println("Actual output: [" + result + "]");

        // Assert
        assertEquals("SelectOperations", result);
    }
}
