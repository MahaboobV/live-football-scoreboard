package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
}
