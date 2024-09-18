package com.example.football.scoreboard.integration;

import com.example.football.scoreboard.service.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;
import com.example.football.scoreboard.impl.InmemoryMatchStorage;
import com.example.football.scoreboard.impl.Scoreboard;
import com.example.football.scoreboard.model.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LiveFootballScoreboardIntegrationTest {

    private Scoreboard scoreboard;

    @BeforeEach
    void setUp() {
        // Arrange
        MatchStorage matchStorage = new InmemoryMatchStorage();
        scoreboard = new Scoreboard(matchStorage);
    }

    @Test
    void testStartMatchSuccessfully() {
        // Act
        Match match = scoreboard.startMatch("Team A", "Team B");

        // Assert
        assertNotNull(match, "Match should be created successfully.");
        assertEquals("Team A", match.getHomeTeam());
        assertEquals("Team B", match.getAwayTeam());
    }

    @Test
    void testStartMatchWithDuplicateTeams() {
        // Arrange
        scoreboard.startMatch("Team A", "Team B");

        // Act
        IllegalStateException stateException = assertThrows(IllegalStateException.class ,
                ()-> scoreboard.startMatch("Team A", "Team B"));

        // Assert
        assertEquals("A match between these two teams is already in progress.", stateException.getMessage());
    }

    @Test
    void testStartMatchInvalidTeamNames() {
        // Act
        IllegalArgumentException argumentException = assertThrows(IllegalArgumentException.class ,
                ()-> scoreboard.startMatch("", "Team B"));

        // Assert
        assertEquals("Home and Away Teams must not be null or empty", argumentException.getMessage());
    }

    @Test
    void testStartMatchWithSameTeamNamess() {

        // Act
        IllegalArgumentException argumentException = assertThrows(IllegalArgumentException.class ,
                ()-> scoreboard.startMatch("Team A", "Team A"));

        // Assert
        assertEquals("Home and Away Teams must be different.", argumentException.getMessage());
    }

    @Test
    void testUpdateMatchScoreByMatchId() {
        // Arrange
        Match match = scoreboard.startMatch("Team A", "Team B");

        scoreboard.updateMatchScore(match.getMatchId(), 1, 2);

        // Assert
        assertEquals(1, match.getHomeTeamScore(), "Home team score should be updated");
        assertEquals(2, match.getAwayTeamScore(), "Away team score should be updated");
    }

    @Test
    void testUpdateMatchScoreByInvalidMatchId() {
        String invalidMatchId = "invalidId";
        // Act
        MatchNotFoundException matchNotFoundException = assertThrows(MatchNotFoundException.class ,
                ()-> scoreboard.updateMatchScore(invalidMatchId, 1, 2));

        // Assert
        assertEquals("No match found with ID: "+invalidMatchId, matchNotFoundException.getMessage());
    }

    @Test
    void testFinishMatchSuccessfully() {

        // Arrange
        Match match = scoreboard.startMatch("Team A", "Team B");

        // Act
        scoreboard.finishMatch(match.getMatchId());

        // Assert
        assertFalse(scoreboard.getMatch(match.getMatchId()).isLive(), "Match should not be live");

    }

    @Test
    void testFinishMatchWithInvalidId() {
        // Arrange
        String invalidMatchId = "invalidId";

        // Act
        MatchNotFoundException matchNotFoundException = assertThrows(MatchNotFoundException.class ,
                ()-> scoreboard.finishMatch(invalidMatchId));

        // Assert
        assertEquals("No match found with ID: "+invalidMatchId, matchNotFoundException.getMessage());
    }

    @Test
    void testViewMatchSummaryWithNoLiveMatches() {
        // Act
        List<String> summary = scoreboard.getMatchSummary();

        // Assert
        assertTrue(summary.isEmpty(), "There should not be any live matches");
    }

    @Test
    void testViewMatchSummaryWithLiveeMatches() {
        // Arrange
        Match match1 = scoreboard.startMatch("Team A", "Team B");

        Match match2 = scoreboard.startMatch("Team C", "Team D");

        scoreboard.updateMatchScore(match1.getMatchId(), 1, 1);
        scoreboard.updateMatchScore(match2.getMatchId(), 3, 2);

        // Act
        List<String> summary = scoreboard.getMatchSummary();

        // Assert
        assertEquals(2, summary.size(), "There should be 2 live matches in the summary.");
        assertTrue(summary.get(0).contains("Team C"), "Match summary should be sorted total score and start time.");
    }

}

