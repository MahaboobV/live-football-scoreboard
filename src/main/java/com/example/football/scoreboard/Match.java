package com.example.football.scoreboard;

import java.time.LocalDateTime;
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
}
