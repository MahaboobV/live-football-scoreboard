package com.example.football.scoreboard;

import com.example.football.scoreboard.impl.InmemoryMatchStorage;
import com.example.football.scoreboard.impl.Scoreboard;
import com.example.football.scoreboard.model.InputWrapper;
import com.example.football.scoreboard.model.Match;

import java.util.Scanner;

public class LiveFootballScoreboardApp {

    private InputWrapper inputWrapper;

    private Scoreboard scoreboard;

    public LiveFootballScoreboardApp(InputWrapper inputWrapper, Scoreboard scoreboard) {
        this.inputWrapper = inputWrapper;
        this.scoreboard = scoreboard;
    }

    public void run() {
        boolean running = true;

        System.out.println("Welcome to the Live football Score App !");

        while(running) {
            // Display the menu
            displayMenu();

            System.out.println("Enter Your Choice :");
            int choice = inputWrapper.nextInt();
            inputWrapper.nextLine();

            switch (choice) {
                case 1 -> startMatch(inputWrapper, scoreboard);
                case 2 -> updateMatchScore(inputWrapper, scoreboard);
                case 3 -> finishMatch(inputWrapper, scoreboard);
                case 4 -> viewLiveMatchSummary(inputWrapper, scoreboard);
                case 5 -> {
                    running = false;
                    System.out.println("Exiting the application....");
                }
                default -> System.out.println("Invalid choice, please try again !");
            }
        }
    }

    public static void main(String[] args) {
        // Initialize the storage and scoreboard
        MatchStorage matchStorage = new InmemoryMatchStorage(); // In-memory storage for matches
        Scoreboard scoreboard = new Scoreboard(matchStorage); // Scoreboard that operates on the storage

        InputWrapper inputWrapper = new InputWrapper(new Scanner(System.in));
        LiveFootballScoreboardApp app = new LiveFootballScoreboardApp(inputWrapper, scoreboard);
        app.run();
    }

    private void displayMenu() {
        System.out.println("\n Select an option:");
        System.out.println("1. Start a match");
        System.out.println("2. Update match Score");
        System.out.println("3. Finish a match");
        System.out.println("4. View Live match summary");
        System.out.println("5. Exit");
    }
    private void startMatch(InputWrapper inputWrapper, Scoreboard scoreboard) {

        String homeTeam = getInput("Enter Home Team :", inputWrapper);
        String awayTeam = getInput("Enter Away Team :", inputWrapper);

        try {
            Match match = scoreboard.startMatch(homeTeam, awayTeam);
            displayMatchStartSuccess(homeTeam, awayTeam, match);

        }catch (IllegalArgumentException | IllegalStateException e) {
            displayErrorMessage(e);
        }
    }

    private String getInput(String propmt, InputWrapper inputWrapper) {
        System.out.println(propmt);
        return inputWrapper.nextLine();
    }


    private void displayMatchStartSuccess(String homeTeam, String awayTeam, Match match) {
        if(match != null) {
            System.out.println("Match started successfully : "+ homeTeam+ " vs "+awayTeam);
            System.out.println("Match Id : "+match.getMatchId());
        }
    }
    private void displayErrorMessage(RuntimeException e) {
        System.out.println("Error :" + e.getMessage());
    }


    private void updateMatchScore(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }

    private void finishMatch(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }

    private void viewLiveMatchSummary(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }

    private void updateScoreByMatchId(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }

    private void updateScoreByTeamNames(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }

    private void finishMatchByMatchId(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }

    private void finishMatchByTeamNames(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }
}
