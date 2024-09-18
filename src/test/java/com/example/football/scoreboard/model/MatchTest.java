package com.example.football.scoreboard.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MatchTest {

    @Test
    void testMatchInitialization() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();

        // Act
        Match match = new Match("Team A", "Team B", 1, 2, startTime);

        // Assert
        assertNotNull(match.getMatchId(), "Match ID should not be null");
        assertEquals("Team A", match.getHomeTeam(), "Home team should be 'Team A'");
        assertEquals("Team B", match.getAwayTeam(), "Home team should be 'Team B'");
        assertEquals(1, match.getHomeTeamScore(), "Home team score should be 1");
        assertEquals(2, match.getAwayTeamScore(), "Home team score should be 2");
        assertEquals(startTime, match.getStartTime(), "Start time should match the provided time");
        assertFalse(match.isLive(), "Match should be not in live on initialization");
    }

    @Test
    void testSetHomeTeamScore() {
        // Arrange
        Match match = new Match("Team A", "Team B", 0, 0, LocalDateTime.now());

        // Act
        match.setHomeTeamScore(5);

        // Assert
        assertEquals(5, match.getHomeTeamScore());
    }

    @Test
    void testSetAwayTeamScore() {
        // Arrange
        Match match = new Match("Team A", "Team B", 0, 0, LocalDateTime.now());

        // Act
        match.setAwayTeamScore(4);

        // Assert
        assertEquals(4, match.getAwayTeamScore());
    }

    @Test
    void testSetLiveStatus() {
        // Arrange
        Match match = new Match("Team A", "Team B", 0, 0, LocalDateTime.now());

        // Act
        match.setLive(true);

        // Assert
        assertTrue(match.isLive());
    }

    @Test
    void testGetTotalScore() {
        // Arrange
        Match match = new Match("Team A", "Team B", 2, 4, LocalDateTime.now());

        // Act
        int totalScore = match.getTotalScore();

        // Assert
        assertEquals(6, totalScore);
    }

    @Test
    void testMatchEquality() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();

        Match match1 = new Match("Team A", "Team B", 2, 4, startTime);
        Match match2 = new Match("Team A", "Team B", 2, 4, startTime);

        // Act
        boolean areEqual = match1.equals(match2);

        // Assert
        assertFalse(areEqual, "Matches with different match IDs should not be equal");
    }

    @Test
    void testHasCode() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();

        Match match1 = new Match("Team A", "Team B", 2, 4, startTime);
        Match match2 = new Match("Team A", "Team B", 2, 4, startTime);

        // Act
        int hash1 = match1.hashCode();
        int hash2 = match2.hashCode();

        // Assert
        assertNotEquals(hash1, hash2, "HashCodes should differ for different match instances");
    }

    @Test
    void testUniqueMatchId() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.now();

        Match match1 = new Match("Team A", "Team B", 2, 4, startTime);
        Match match2 = new Match("Team A", "Team B", 2, 4, startTime);

        // Act
        String matchId1 = match1.getMatchId();
        String matchId2 = match2.getMatchId();

        // Assert
        assertNotEquals(matchId1, matchId2, "Match IDs should be unique for each instance");
    }
}
