package com.example.football.scoreboard;

import java.util.List;

public interface MatchStorage {
    
    void saveMatch(Match match);

    Match findMatch(String matchId);

    List<Match> getAllMatches();
}
