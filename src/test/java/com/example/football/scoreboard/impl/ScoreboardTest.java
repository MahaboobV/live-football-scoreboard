package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.service.MatchStorage;
import com.example.football.scoreboard.model.Match;
import com.example.football.scoreboard.exception.MatchNotFoundException;
import com.example.football.scoreboard.exception.MatchUpdateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class ScoreboardTest {

    @Mock
    private MatchStorage matchStorage;

    @InjectMocks
    private Scoreboard scoreboard;


    @Test
    void testStartMatch() {
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        // Act
        scoreboard.startMatch(homeTeam, awayTeam);

        // Assert
        verify(matchStorage).saveMatch(argThat(match ->
                match.getHomeTeam().equals(homeTeam) &&
                match.getAwayTeam().equals(awayTeam) &&
                match.getHomeTeamScore() == 0 &&
                match.getAwayTeamScore() == 0 &&
                match.getStartTime().isAfter(now.minusSeconds(1)) &&
                match.getStartTime().isBefore(now.plusSeconds(1)) &&
                match.isLive()
        ));
    }

    @Test
    void testStartMatch_ReturnSavedMatch() {
        // Arrange
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        // Mock findMatch to return Null (no match is live between these two teams)
        when(matchStorage.findMatch(homeTeam, awayTeam)).thenReturn(null);

        // Mock getAllMatches to return empty list
        when(matchStorage.getAllMatches()).thenReturn(Collections.emptyList());

        // Act
        Match match = scoreboard.startMatch(homeTeam, awayTeam);

        // Assert
        assertNotNull(match);
        assertEquals("Team A", match.getHomeTeam());
        assertEquals("Team B", match.getAwayTeam());
        assertTrue(match.isLive());

    }
    @Test
    void testStartMatch_AlreadyLive() {

        String homeTeam = "Team A";
        String awayTeam = "Team B";

        Match match = new Match(homeTeam, awayTeam, 0 , 0, LocalDateTime.now());

        when(matchStorage.findMatch(homeTeam, awayTeam)).thenReturn(null).thenReturn(match);
        // Act
        Match firstMatch = scoreboard.startMatch(homeTeam, awayTeam);

        // Assert
        assertNotNull(firstMatch);

        // Act
        IllegalStateException stateException = assertThrows(IllegalStateException.class, () -> scoreboard.startMatch(homeTeam, awayTeam));

        // Assert the exception message
        assertEquals("A match between these two teams is already in progress.", stateException.getMessage());
    }

    @Test
    void testStartMatch_NullOrEmptyTeams() {
        // Act
        IllegalArgumentException nullException = assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch(null, "Team B"));

        // Assert the exception message
        assertEquals("Home and Away Teams must not be null or empty", nullException.getMessage());

        // Act
        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch("", "Team B"));

        // Assert the exception message
        assertEquals("Home and Away Teams must not be null or empty", emptyException.getMessage());

        // Act
        IllegalArgumentException spaceException = assertThrows(IllegalArgumentException.class, () -> scoreboard.startMatch("Team A", " "));

        // Assert the exception message
        assertEquals("Home and Away Teams must not be null or empty", spaceException.getMessage());
    }

    @Test
    void testStartMatch_OneOfTheTeamsMatchisLive() {

        // Arrange
        String homeTeam = "Team A";
        String awayTeam = "Team B";

        Match match = new Match(homeTeam, awayTeam, 0 , 0, LocalDateTime.now());
        match.setLive(true);

        // Mock findMatch to return Null (no match is live between these two teams)
        when(matchStorage.findMatch(homeTeam, awayTeam)).thenReturn(null);

        // Mock getAllMatches to return list of Match (simulating one match is ongoing with either of the team is part of)
        when(matchStorage.getAllMatches()).thenReturn(Collections.singletonList(match));


        // Act
        IllegalStateException stateException = assertThrows(IllegalStateException.class, () -> scoreboard.startMatch(homeTeam, awayTeam));

        // Assert the exception message
        assertEquals("Cannot start the match: a match involving either the home team " + homeTeam + " or the away team " + awayTeam + " is already in progress.", stateException.getMessage());
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
    void testGetMatchById_ValidId() {
        // Arrange
        String matchId = "match1";
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        Match match = new Match(homeTeam, awayTeam, 1 ,  1, now);

        when(matchStorage.findMatch((matchId))).thenReturn(match);

        // Act
        Match retrievedMatch = scoreboard.getMatch(matchId);

        // Assert
        assertEquals(homeTeam, retrievedMatch.getHomeTeam());
        assertEquals(awayTeam, retrievedMatch.getAwayTeam());
        assertEquals(1, retrievedMatch.getHomeTeamScore());
        assertEquals(1, retrievedMatch.getAwayTeamScore());
    }

    @Test
    void testGetMatchById_InvalidId() {
        // Arrange
        String matchId = "invalid1";

        when(matchStorage.findMatch((matchId))).thenReturn(null);

        // Act & Assert
        MatchNotFoundException exception = assertThrows(MatchNotFoundException.class, () -> scoreboard.getMatch(matchId));

        // Assert the exception message
        assertEquals("No match found with ID: invalid1", exception.getMessage());
    }

    @Test
    void testGetMatchById_NullOrEmptyId() {
        // Act & Assert
        IllegalArgumentException nullException = assertThrows(IllegalArgumentException.class, () -> scoreboard.getMatch(null));

        // Assert the exception message
        assertEquals("Match ID cannot be null or empty", nullException.getMessage());

        // Act & Assert
        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class, () -> scoreboard.getMatch(""));

        // Assert the exception message
        assertEquals("Match ID cannot be null or empty", emptyException.getMessage());

        // Act & Assert
        IllegalArgumentException spaceException = assertThrows(IllegalArgumentException.class, () -> scoreboard.getMatch(" "));

        // Assert the exception message
        assertEquals("Match ID cannot be null or empty", spaceException.getMessage());
    }

    @Test
    void testGetMatchByTeamNames_Valid() {
        // Arrange
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        Match match = new Match(homeTeam, awayTeam, 1 ,  1, now);

        when(matchStorage.findMatch(homeTeam, awayTeam)).thenReturn(match);

        // Act
        Match retrievedMatch = scoreboard.getMatch(homeTeam, awayTeam);

        // Assert
        assertEquals(homeTeam, retrievedMatch.getHomeTeam());
        assertEquals(awayTeam, retrievedMatch.getAwayTeam());
        assertEquals(1, retrievedMatch.getHomeTeamScore());
        assertEquals(1, retrievedMatch.getAwayTeamScore());
    }

    @Test
    void testGteMatchByTeamName_NullOrEmptyTeamNames() {
        // Act
        IllegalArgumentException nullException = assertThrows(IllegalArgumentException.class, () -> scoreboard.getMatch(null, "Team B"));

        // Assert the exception message
        assertEquals("Home and Away Teams must not be null or empty", nullException.getMessage());

        // Act
        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class, () -> scoreboard.getMatch("", "Team B"));

        // Assert the exception message
        assertEquals("Home and Away Teams must not be null or empty", emptyException.getMessage());

        // Act
        IllegalArgumentException spaceException = assertThrows(IllegalArgumentException.class, () -> scoreboard.getMatch("Team A", " "));

        // Assert the exception message
        assertEquals("Home and Away Teams must not be null or empty", spaceException.getMessage());
    }

    @Test
    void testUpdateMatchScore_ValidMatch() {
        // Arrange
        String matchId = "match1";
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        Match match = new Match(homeTeam, awayTeam, 0, 0, now);

        when(matchStorage.findMatch(matchId)).thenReturn(match);

        // Act
        scoreboard.updateMatchScore(matchId, 3, 2);

        // Assert
        assertEquals(3, match.getHomeTeamScore());
        assertEquals(2, match.getAwayTeamScore());
        verify(matchStorage).saveMatch(match);

    }

    @Test
    void testUpdateMatchScore_InvalidMatch() {

        // Arrange
        String matchId = "InvalidMatchId";
        when(matchStorage.findMatch(matchId)).thenReturn(null);

        // Act & Assert
        MatchNotFoundException exception = assertThrows(MatchNotFoundException.class, () -> scoreboard.updateMatchScore(matchId, 2, 1));
        assertEquals("No match found with ID: InvalidMatchId", exception.getMessage());
    }

    @Test
    void testUpdateMatchScore_NegativeScore() {

        // Arrange
        String matchId = "matchId1";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> scoreboard.updateMatchScore(matchId, 1, -1));
        assertEquals("Score cannot be negative", exception.getMessage());
    }

    @Test
    void testFinishMatch_ValidMatch() {
        // Arrange
        String matchId = "match1";
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        Match match = new Match(homeTeam, awayTeam, 0, 0, now);
        match.setLive(true);

        when(matchStorage.findMatch(matchId)).thenReturn(match);

        // Act
        scoreboard.finishMatch(matchId);

        // Assert
        assertFalse(match.isLive());
        verify(matchStorage).saveMatch(match); // Ensure the match is saved after finishing
    }

    @Test
    void testFinishMatch_InvalidMatch() {
        // Arrange
        String matchId = "invalidMatchId";
        when(matchStorage.findMatch(matchId)).thenReturn(null); //match not found

        // Act && Assert
        MatchNotFoundException exception = assertThrows(MatchNotFoundException.class, ()-> scoreboard.finishMatch(matchId));
        assertEquals("No match found with ID: invalidMatchId", exception.getMessage());
    }

    @Test
    void testFinishMatch_AlreadyFinsihedMatch() {
        // Arrange
        String matchId = "match1";
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        Match match = new Match(homeTeam, awayTeam, 2, 1, now);
        match.setLive(false); // The match is already finished
        when(matchStorage.findMatch(matchId)).thenReturn(match);

        // Act && Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, ()-> scoreboard.finishMatch(matchId));
        assertEquals("The match is already finished", exception.getMessage());
    }

    @Test
    void testFinishMatchById_NullOrEmptyId() {
        // Act & Assert
        IllegalArgumentException nullException = assertThrows(IllegalArgumentException.class, () -> scoreboard.finishMatch(null));

        // Assert the exception message
        assertEquals("Match ID cannot be null or empty", nullException.getMessage());

        // Act & Assert
        IllegalArgumentException emptyException = assertThrows(IllegalArgumentException.class, () -> scoreboard.finishMatch(""));

        // Assert the exception message
        assertEquals("Match ID cannot be null or empty", emptyException.getMessage());

        // Act & Assert
        IllegalArgumentException spaceException = assertThrows(IllegalArgumentException.class, () -> scoreboard.finishMatch(" "));

        // Assert the exception message
        assertEquals("Match ID cannot be null or empty", spaceException.getMessage());
    }

    @Test
    void testFinishMatchSaveFailure() {

        // Arrange
        String matchId = "match1";
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        Match liveMatch = new Match(homeTeam, awayTeam, 2, 1, now);
        liveMatch.setLive(true);

        when(matchStorage.findMatch(matchId)).thenReturn(liveMatch);
        doThrow(new RuntimeException("Database error")).when(matchStorage).saveMatch(liveMatch);

        // Act && Assert
        MatchUpdateException exception = assertThrows(MatchUpdateException.class, () -> scoreboard.finishMatch(matchId));

        assertEquals("Failed to update the match status", exception.getMessage());
    }

    @Test
    void testGetMatchSummary_EmptyScoreboard() {
        // Arrnage
        when(matchStorage.getAllMatches()).thenReturn(Collections.emptyList()); // Set as no live matches

        // Act
        List<String> matchSummary = scoreboard.getMatchSummary();

        // Assert
        assertTrue(matchSummary.isEmpty(), "Expected an empty list when there are no live matches");
    }

    @Test
    void testGetMatchSummary_SingleLiveMatch() {
        // Arrange
        String homeTeam = "Uruguay";
        String awayTeam = "Italy";
        LocalDateTime now = LocalDateTime.now();
        Match liveMatch = new Match(homeTeam, awayTeam, 2, 1, now);
        liveMatch.setLive(true);

        when(matchStorage.getAllMatches()).thenReturn(Collections.singletonList(liveMatch)); // only one live match

        // Act
        List<String> matchSummary = scoreboard.getMatchSummary();

        assertEquals(1, matchSummary.size());
        assertEquals("1. Uruguay 2 - Italy 1", matchSummary.get(0));
    }

    @Test
    void testGetMatchSummary_SortedByTotalScore() {
        // Arrange
        Match liveMatch3 = new Match("Uruguay", "Italy", 1, 1, LocalDateTime.now().minusMinutes(10));
        liveMatch3.setLive(true);

        Match liveMatch2 = new Match("Spain", "Brazil", 2, 3, LocalDateTime.now().minusMinutes(5));
        liveMatch2.setLive(true);

        Match liveMatch1 = new Match("Mexico", "Canada", 3, 4, LocalDateTime.now().minusMinutes(2));
        liveMatch1.setLive(true);

        when(matchStorage.getAllMatches()).thenReturn(List.of(liveMatch1, liveMatch2, liveMatch3));

        List<String> matchSummary = scoreboard.getMatchSummary();

        assertEquals(3, matchSummary.size());
        assertEquals("1. Mexico 3 - Canada 4", matchSummary.get(0)); //Match3 should come first (highest total score)
        assertEquals("2. Spain 2 - Brazil 3", matchSummary.get(1));
        assertEquals("3. Uruguay 1 - Italy 1", matchSummary.get(2));
    }

    @Test
    void testGetMatchSummary_SortedByStartTime() {
        // Arrange
        Match liveMatch3 = new Match("Uruguay", "Italy", 2, 2, LocalDateTime.now().minusMinutes(10));
        liveMatch3.setLive(true);

        Match liveMatch2 = new Match("Mexico", "Canada", 2, 2, LocalDateTime.now().minusMinutes(3));
        liveMatch2.setLive(true);

        Match liveMatch1 = new Match("Spain", "Brazil", 2, 2, LocalDateTime.now()); // recent match
        liveMatch1.setLive(true);

        when(matchStorage.getAllMatches()).thenReturn(List.of(liveMatch1, liveMatch2, liveMatch3));

        List<String> matchSummary = scoreboard.getMatchSummary();

        assertEquals(3, matchSummary.size());
        assertEquals("1. Spain 2 - Brazil 2", matchSummary.get(0)); //Match3 should come first (highest total score)
        assertEquals("2. Mexico 2 - Canada 2", matchSummary.get(1));
        assertEquals("3. Uruguay 2 - Italy 2", matchSummary.get(2));
    }
}
