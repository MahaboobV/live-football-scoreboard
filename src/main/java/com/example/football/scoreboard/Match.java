package com.example.football.scoreboard;

import java.time.LocalDateTime;

public class Match {

    private final String homeTeam;

    private final String awayTeam;

    private int homeTeamScore;

    private int awayTeamScore;

    private final LocalDateTime startTime;

    private boolean isLive;

    private int totalScore;

    public Match(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeTeamScore = homeTeamScore;
        this.awayTeamScore = awayTeamScore;
        this.startTime = LocalDateTime.now();
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeTeamScore() {
        return homeTeamScore;
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
