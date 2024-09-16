package com.example.football.scoreboard;

import java.util.List;

public interface MatchStorage {
    
    void saveMatch(Match match);

    Match findMatch(String matchId);

    void removeMatch(Match match);

    List<Match> getAllMatches();
}
