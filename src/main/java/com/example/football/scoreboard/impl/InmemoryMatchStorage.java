package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

public class InmemoryMatchStorage implements MatchStorage {

    private final Map<String, Match> matchMap = new HashMap<>();

    @Override
    public void saveMatch(Match match) {
        matchMap.put(match.getMatchId(), match);
    }

    @Override
    public Match findMatch(String matchId) {
        Match match = matchMap.get(matchId);

        if(match ==  null) {
            throw new MatchNotFoundException("No match found with ID: "+matchId);
        }
        return match;
    }

    @Override
    public List<Match> getAllMatches() {
        return Collections.unmodifiableList(new ArrayList<>(matchMap.values()));
    }
}
