package com.example.football.scoreboard;

import java.util.List;

public interface MatchOperations {

    Match startMatch(String homeTeam, String awayTeam);

    Match getMatch(String matchId);

    void updateMatchScore(String matchId, int scoreA, int scoreB);

    void finishMatch(String matchId);

    List<String> getMatchSummary();
}
