package com.example.football.scoreboard.service;

import com.example.football.scoreboard.model.Match;

import java.util.List;

public interface MatchStorage {
    
    void saveMatch(Match match);

    Match findMatch(String matchId);

    Match findMatch(String homeTeam, String awayTeam);

    List<Match> getAllMatches();
}
