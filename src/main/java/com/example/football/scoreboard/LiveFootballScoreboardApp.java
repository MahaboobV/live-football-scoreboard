package com.example.football.scoreboard;

import com.example.football.scoreboard.exception.MatchNotFoundException;
import com.example.football.scoreboard.impl.InmemoryMatchStorage;
import com.example.football.scoreboard.impl.Scoreboard;
import com.example.football.scoreboard.model.InputWrapper;
import com.example.football.scoreboard.model.Match;

import java.util.Objects;
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
        System.out.println("\n Select an option:");
        System.out.println("1. Using Match ID :");
        System.out.println("2. Using Team Names :");

        int selection = inputWrapper.nextInt();
        inputWrapper.nextLine();

        switch (selection) {
            case 1 -> updateScoreByMatchId(inputWrapper, scoreboard);
            case 2 -> updateScoreByTeamNames(inputWrapper, scoreboard);
            default -> System.out.println("Invalid choice, please choose the right option !");
        }
    }

    private void finishMatch(InputWrapper inputWrapper, Scoreboard scoreboard) {
        System.out.println("\n Select an option:");
        System.out.println("1. Using Match ID :");
        System.out.println("2. Using Team Names :");

        int selection = inputWrapper.nextInt();
        inputWrapper.nextLine();

        switch (selection) {
            case 1 -> finishMatchByMatchId(inputWrapper, scoreboard);
            case 2 -> finishMatchByTeamNames(inputWrapper, scoreboard);
            default -> System.out.println("Invalid choice, please choose the right option !");
        }
    }

    private void viewLiveMatchSummary(InputWrapper inputWrapper, Scoreboard scoreboard) {

    }

    private void updateScoreByMatchId(InputWrapper inputWrapper, Scoreboard scoreboard) {
        String matchId = getInput("Enter Match ID :", inputWrapper);

        Integer homeTeamScore = getInputInt("Enter Home Team Score:", inputWrapper);
        Integer awayTeamScore = getInputInt("Enter Away Team Score:", inputWrapper);

        // Validate scores
        if (! isValidScores(homeTeamScore, awayTeamScore)){
            return;
        }

        try {
            scoreboard.updateMatchScore(matchId, Objects.requireNonNull(homeTeamScore), Objects.requireNonNull(awayTeamScore));
            System.out.println("Score updated : Home Team " + homeTeamScore + " - Away Team " + awayTeamScore);
        } catch (IllegalArgumentException | MatchNotFoundException e) {
            System.out.println("Error :" + e.getMessage());
        }
    }

    private void updateScoreByTeamNames(InputWrapper inputWrapper, Scoreboard scoreboard) {
        String homeTeamName = getInput("Enter Home Team Name :", inputWrapper);
        String awayTeamName = getInput("Enter Away Team Name :", inputWrapper);

        // Validate team names
        if(! isValidTeamNames(homeTeamName, awayTeamName)){
            return;
        }

        Integer homeTeamScore = getInputInt("Enter Home Team Score:", inputWrapper);
        Integer awayTeamScore = getInputInt("Enter Away Team Score:", inputWrapper);

        // Validate scores
        if (! isValidScores(homeTeamScore, awayTeamScore)){
            return;
        }

        try {
            Match match = scoreboard.getMatch(homeTeamName, awayTeamName);
            scoreboard.updateMatchScore(match.getMatchId(), Objects.requireNonNull(homeTeamScore), Objects.requireNonNull(awayTeamScore));

            System.out.println("Score updated : Home Team " + homeTeamScore + " - Away Team " + awayTeamScore);
        } catch (IllegalArgumentException | MatchNotFoundException e) {
            System.out.println("Error :" + e.getMessage());
        }
    }

    private boolean isValidTeamNames(String homeTeamName, String awayTeamName) {
        if (homeTeamName.isEmpty() || awayTeamName.isEmpty()) {
            System.out.println("Error: Team names cannot be empty.");
            return false;
        }
        if (homeTeamName.equals(awayTeamName)) {
            System.out.println("Error: Home and Away Teams must be different.");
            return false;
        }
        return true;
    }

    private boolean isValidScores(Integer homeTeamScore, Integer awayTeamScore) {
        if (homeTeamScore == null || awayTeamScore == null) {
            System.out.println("Error: Invalid score input. Please enter numeric values.");
            return false;
        }
        if (homeTeamScore < 0 || awayTeamScore < 0) {
            System.out.println("Error: Scores cannot be negative.");
            return false;
        }
        return true;
    }

    private Integer getInputInt(String prompt, InputWrapper inputWrapper) {

        System.out.println(prompt);
        String input = inputWrapper.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null; // Return null to indicate invalid input
        }
    }

    private void finishMatchByMatchId(InputWrapper inputWrapper, Scoreboard scoreboard) {
        String matchId = getInput("Enter Match ID to finish the match:", inputWrapper);

        try {
            scoreboard.finishMatch(matchId);
            System.out.println("Match with Match ID: " + matchId + " has been finished");
        } catch (IllegalArgumentException | MatchNotFoundException e) {
            System.out.println("Error :" + e.getMessage());
        }
    }

    private void finishMatchByTeamNames(InputWrapper inputWrapper, Scoreboard scoreboard) {
        String homeTeamName = getInput("Enter Home Team Name :", inputWrapper);
        String awayTeamName = getInput("Enter Away Team Name :", inputWrapper);

        try {
            Match match = scoreboard.getMatch(homeTeamName, awayTeamName);
            scoreboard.finishMatch(match.getMatchId());
            System.out.println("Match with Match ID: " + match.getMatchId() + " has been finished");

        } catch (IllegalArgumentException | MatchNotFoundException e) {
            System.out.println("Error :" + e.getMessage());
        }
    }
}
