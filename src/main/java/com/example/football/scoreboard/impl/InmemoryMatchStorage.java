package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.service.MatchStorage;
import com.example.football.scoreboard.model.Match;
import com.example.football.scoreboard.exception.MatchNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InmemoryMatchStorage implements MatchStorage {

    private final Map<String, Match> matchMap = new HashMap<>();

    @Override
    public void saveMatch(Match match) {
        if(match == null || match.getMatchId() == null) {
            throw new IllegalArgumentException("Match or Match Id cannot be null");
        }
        matchMap.put(match.getMatchId(), match);
    }

    @Override
    public Match findMatch(String matchId) {
        if(matchId == null || matchId.trim().isEmpty()) {
            throw new IllegalArgumentException("Match ID cannot be null or empty");
        }
        Match match = matchMap.get(matchId);

        if(match ==  null) {
            throw new MatchNotFoundException("No match found with ID: "+matchId);
        }
        return match;
    }

    @Override
    public Match findMatch(String homeTeam, String awayTeam) {
        return matchMap.values().stream()
                .filter(match -> match.getHomeTeam().equals(homeTeam) &&
                        match.getAwayTeam().equals(awayTeam) &&
                        match.isLive())
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Match> getAllMatches() {
        return List.copyOf(matchMap.values());
    }
}
