package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;
import com.example.football.scoreboard.exception.MatchUpdateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        liveMatch.setLive(true); // The match is already finished

        when(matchStorage.findMatch(matchId)).thenReturn(liveMatch);
        doThrow(new RuntimeException("Database error")).when(matchStorage).saveMatch(liveMatch);

        // Act && Assert
        MatchUpdateException exception = assertThrows(MatchUpdateException.class, () -> scoreboard.finishMatch(matchId));

        assertEquals("Failed to update the match status", exception.getMessage());
    }
}
