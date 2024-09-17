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
    public Match startMatch(String homeTeam, String awayTeam) {
        //Validate input
        if(homeTeam == null || homeTeam.trim().isEmpty() || awayTeam == null || awayTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Home and Away Teams must not be null or empty");
        }

        // Check the same combination match is already in progress
        if(matchStorage.findMatch(homeTeam, awayTeam) != null) {
            throw new IllegalStateException("A match between these two teams is already in progress.");
        }

        // check if one of the teams involved in already in progress match
        Optional<Match> matchOptional = matchStorage.getAllMatches().stream()
                .filter(match -> match.getHomeTeam().equals(homeTeam) ||  match.getAwayTeam().equals(homeTeam) ||
                        match.getHomeTeam().equals(awayTeam) ||  match.getAwayTeam().equals(awayTeam))
                .filter(Match::isLive)
                .findFirst();

        if(matchOptional.isPresent()) {
                throw new IllegalStateException("Cannot start the match: a match involving either the home team " + homeTeam + " or the away team " + awayTeam + " is already in progress.");
        }
        Match match = createNewMatch(homeTeam, awayTeam);
        match.setLive(true);
        matchStorage.saveMatch(match);
        return match;
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
    public Match getMatch(String homeTeam, String awayTeam) {
        //Validate input
        if(homeTeam == null || homeTeam.trim().isEmpty() || awayTeam == null || awayTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Home and Away Teams must not be null or empty");
        }
        return Optional.ofNullable(matchStorage.findMatch(homeTeam, awayTeam))
                .orElseThrow(() -> new MatchNotFoundException("No match found with Home Team: "+homeTeam +" Away Team: "+ awayTeam));
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
        // Retrieve and filter live matches
        List<Match> liveMatches = getLiveMatches();

        // Sort the matches by total score and start time
        List<Match> sortedMatches =  sortMatches(liveMatches);

        // format tthe sorted matches into desired summary format
        return formattedMacthes(sortedMatches);
    }

    private List<Match> getLiveMatches() {
        return matchStorage.getAllMatches().stream()
                .filter(Match::isLive).collect(Collectors.toList());
    }

    private List<Match> sortMatches(List<Match> matches) {
        return matches.stream()
                .filter(Match::isLive)
                .sorted(Comparator.comparingInt(Match::getTotalScore).reversed()
                        .thenComparing(Match::getStartTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private List<String> formattedMacthes(List<Match> matches) {
        return IntStream.range(0, matches.size())
                .mapToObj(i-> {
                    Match match = matches.get(i);
                    return (i+1) +". "+ match.getHomeTeam() +" "+ match.getHomeTeamScore()+
                            " - "+match.getAwayTeam() +" "+ match.getAwayTeamScore();

                }).collect(Collectors.toList());
    }


    private Match createNewMatch(String homeTeam, String awayTeam) {
        LocalDateTime now = LocalDateTime.now();
        return new Match(homeTeam, awayTeam, 0, 0, now);

    }
}
