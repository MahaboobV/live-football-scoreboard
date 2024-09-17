package com.example.football.scoreboard;

import java.util.List;

public interface MatchStorage {
    
    void saveMatch(Match match);

    Match findMatch(String matchId);

    Match findMatch(String homeTeam, String awayTeam);

    List<Match> getAllMatches();
}
