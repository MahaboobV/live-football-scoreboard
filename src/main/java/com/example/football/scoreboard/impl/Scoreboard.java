package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchOperations;
import com.example.football.scoreboard.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Scoreboard implements MatchOperations {

    private final MatchStorage matchStorage;

    public Scoreboard(MatchStorage matchStorage) {
        this.matchStorage = matchStorage;
    }

    // Create a new match, sets it as live, and saves it to storage
    @Override
    public void startMatch(String homeTeam, String awayTeam) {
        Match match = createNewMatch(homeTeam, awayTeam);
        match.setLive(true);
        matchStorage.saveMatch(match);
    }

    @Override
    public Match getMatch(String matchId) {
        if (matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
        return Optional.ofNullable(matchStorage.findMatch(matchId))
                .orElseThrow(() -> new MatchNotFoundException("No match found with ID: "+matchId));
    }

    @Override
    public void updateMatchScore(String matchId, int homeTeamScore, int awayTeamScore) {

        if (homeTeamScore < 0 || awayTeamScore < 0) {
            throw new IllegalArgumentException("Score cannot be negative");
        }

        Match match = Optional.ofNullable(matchStorage.findMatch(matchId))
                       .orElseThrow(() -> new MatchNotFoundException("No match found with ID: "+matchId));

        match.setHomeTeamScore(homeTeamScore);
        match.setAwayTeamScore(awayTeamScore);

        matchStorage.saveMatch(match);
    }

    @Override
    public void finishMatch() {

    }

    @Override
    public List<Match> getMatchSummary() {
        return null;
    }

    private Match createNewMatch(String homeTeam, String awayTeam) {
        LocalDateTime now = LocalDateTime.now();
        return new Match(homeTeam, awayTeam, 0, 0, now);

    }
}
