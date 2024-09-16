package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchStorage;

import java.util.ArrayList;
import java.util.List;

public class InmemoryMatchStorage implements MatchStorage {

    private final List<Match> matches = new ArrayList<>();

    @Override
    public void saveMatch(Match match) {

    }

    @Override
    public Match findMatch(String matchId) {
        return null;
    }

    @Override
    public void removeMatch(Match match) {

    }

    @Override
    public List<Match> getAllMatches() {
        return null;
    }
}
