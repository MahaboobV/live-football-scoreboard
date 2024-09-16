package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchOperations;
import com.example.football.scoreboard.MatchStorage;

import java.time.LocalDateTime;
import java.util.List;

public class Scoreboard implements MatchOperations {

    private final MatchStorage matchStorage;

    public Scoreboard(MatchStorage matchStorage) {
        this.matchStorage = matchStorage;
    }


    @Override
    public void startMatch(String homeTeam, String awayTeam) {
        Match match = createNewMatch(homeTeam, awayTeam);
        match.setLive(true); // Setting match status as Live
        matchStorage.saveMatch(match); // Save the match to the storage
    }

    @Override
    public Match getMatch(Match match) {
        return null;
    }

    @Override
    public void updateMatchScore(int scoreA, int scoreB) {

    }

    @Override
    public void finishMatch() {

    }

    @Override
    public List<Match> getMatchSummary() {
        return null;
    }

    private Match createNewMatch(String homeTeam, String awayTeam) {
        LocalDateTime now = LocalDateTime.now(); // Sett startTime as current time
        return new Match(homeTeam, awayTeam, 0, 0, now); // Create a new match with 0-0 score

    }
}
