package com.example.football.scoreboard.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Match {
    private final String matchId;

    private final String homeTeam;

    private final String awayTeam;

    private int homeTeamScore;

    private int awayTeamScore;

    private final LocalDateTime startTime;

    private boolean isLive;

    private int totalScore;

    public Match(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore, LocalDateTime startTime) {
        this.matchId = UUID.randomUUID().toString();
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.startTime = startTime;
        this.isLive = false; // Default value
    }

    public String getMatchId() {
        return matchId;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setHomeTeamScore(int homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setAwayTeamScore(int awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public int getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isLive() {
        return isLive;
    }


    public int getTotalScore() {
        return homeTeamScore + awayTeamScore;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return homeTeamScore == match.homeTeamScore &&
                awayTeamScore == match.awayTeamScore &&
                isLive == match.isLive &&
                totalScore == match.totalScore &&
                Objects.equals(matchId, match.matchId) &&
                Objects.equals(homeTeam, match.homeTeam) &&
                Objects.equals(awayTeam, match.awayTeam) &&
                Objects.equals(startTime,match.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, homeTeam, awayTeam, homeTeamScore, awayTeamScore, startTime, isLive, totalScore);
    }
}
