package com.example.football.scoreboard;

import java.util.List;

public interface MatchOperations {

    void startMatch(String homeTeam, String awayTeam);

    Match getMatch(Match match);

    void updateMatchScore(int scoreA, int scoreB);

    void finishMatch();

    List<Match> getMatchSummary();
}
