package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchOperations;
import com.example.football.scoreboard.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;
import com.example.football.scoreboard.exception.MatchUpdateException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public void finishMatch(String matchId) {

        if(matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }

        Match match = Optional.ofNullable(matchStorage.findMatch(matchId))
                        .orElseThrow(() ->new MatchNotFoundException("No match found with ID: "+matchId));
        if(! match.isLive()) {
            throw new IllegalStateException("The match is already finished");
        }

        match.setLive(false); // Set the match as finished

        try {
            matchStorage.saveMatch(match); // Save the updated match state
        }catch (Exception e) {
            throw new MatchUpdateException("Failed to update the match status", e);
        }
    }

    @Override
    public List<String> getMatchSummary() {
        List<Match> liveMatches = matchStorage.getAllMatches();

        List<Match> sortedMatches =  liveMatches.stream()
                .filter(Match::isLive)
                .sorted(Comparator.comparingInt(Match::getTotalScore).reversed()
                        .thenComparing(Match::getStartTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return IntStream.range(0, sortedMatches.size())
                .mapToObj(i-> {
                    Match match = sortedMatches.get(i);
                    return (i+1) +". "+ match.getHomeTeam() +" "+ match.getHomeTeamScore()+
                            " - "+match.getAwayTeam() +" "+ match.getAwayTeamScore();

                }).collect(Collectors.toList());
    }

    private Match createNewMatch(String homeTeam, String awayTeam) {
        LocalDateTime now = LocalDateTime.now();
        return new Match(homeTeam, awayTeam, 0, 0, now);

    }
}
