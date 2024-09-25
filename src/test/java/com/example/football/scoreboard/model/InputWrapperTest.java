package com.example.football.scoreboard.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testHasNextInt_Valid() {

        String input = "123";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        inputWrapper = new InputWrapper(scanner);

        assertTrue(inputWrapper.hasNextInt(), "Expected valid integer input");

        // Act
        int result = inputWrapper.nextInt();

        // Assert
        assertEquals(123, result);
    }

    @Test
    public void testHasNextInt_Invalid() {

        String input = "abcd";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        inputWrapper = new InputWrapper(scanner);

        // Act && Assert
        assertFalse(inputWrapper.hasNextInt(), "Expected invalid integer input");
    }

    @Test
    public void testNext() {

        String input = "1234";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        inputWrapper = new InputWrapper(scanner);

        // Act && Assert
        assertTrue(inputWrapper.hasNextInt());
        assertEquals(1234, inputWrapper.nextInt());
    }

    @Test
    public void testNext_ClearsInvalid() {

        String input = "abcd 1234";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        inputWrapper = new InputWrapper(scanner);

        // First input to be invalid
        if(!inputWrapper.hasNextInt()) {
            inputWrapper.next(); // clears the invalid input
        }

        // Act && Assert
        assertTrue(inputWrapper.hasNextInt()); // scanner should point to the next token
        assertEquals(1234, inputWrapper.nextInt());
    }
}
