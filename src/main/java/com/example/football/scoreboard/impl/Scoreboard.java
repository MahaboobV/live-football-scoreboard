package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchOperations;
import com.example.football.scoreboard.MatchStorage;

import java.util.List;

public class Scoreboard implements MatchOperations {

    private final MatchStorage matchStorage;

    public Scoreboard(MatchStorage matchStorage) {
        this.matchStorage = matchStorage;
    }


    @Override
    public void startMatch(String homeTeam, String awayTeam) {
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
}
