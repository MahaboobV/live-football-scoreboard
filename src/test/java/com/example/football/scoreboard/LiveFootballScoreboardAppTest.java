package com.example.football.scoreboard;

import com.example.football.scoreboard.exception.MatchNotFoundException;
import com.example.football.scoreboard.impl.Scoreboard;
import com.example.football.scoreboard.model.InputWrapper;
import com.example.football.scoreboard.model.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LiveFootballScoreboardAppTest {

    @Mock
    private InputWrapper mockInputWrapper;

    @Mock
    private Scoreboard mockScoreboard;


    private LiveFootballScoreboardApp liveFootballScoreboardApp;

    @BeforeEach
    void setUp() {
        liveFootballScoreboardApp = new LiveFootballScoreboardApp(mockInputWrapper, mockScoreboard);
    }

    @Test
    void testStartMatch() {

        Match match = new Match("Team A", "Team B", 0, 0 , LocalDateTime.now());
        // mock user input
        when(mockInputWrapper.nextInt()).thenReturn(1).thenReturn(5);
        when(mockInputWrapper.nextLine()).thenReturn("").thenReturn("Team A").thenReturn("Team B");
        when(mockScoreboard.startMatch("Team A", "Team B")).thenReturn(match);
        // Act
        liveFootballScoreboardApp.run();

        // Assert
        verify(mockScoreboard).startMatch("Team A", "Team B");

    }

    @Test
    void testUpdatetMatchScore() {
        Match match = new Match("Team A", "Team B", 0, 0 , LocalDateTime.now());

        // mock user input
        when(mockInputWrapper.nextInt()).thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(3).thenReturn(5);
        when(mockInputWrapper.nextLine()).thenReturn("").thenReturn("").thenReturn(match.getMatchId()).thenReturn("");
        doNothing().when(mockScoreboard).updateMatchScore(match.getMatchId(), 3,3);

        // Act
        liveFootballScoreboardApp.run();

        // Assert
        verify(mockScoreboard).updateMatchScore(match.getMatchId(), 3,3);

    }

    @Test
    void testUpdateMatchScoreByMatchId_Invalid() {
        // Arrange
        String matchId = "invalid-id";
        int homeTeamScore = 3;
        int awayTeamScore = 2;

        // Simulate user input
        when(mockInputWrapper.nextLine()).thenReturn("").thenReturn("").thenReturn(matchId).thenReturn(""); // Invalid Match ID
        when(mockInputWrapper.nextInt()).thenReturn(2).thenReturn(1).thenReturn(homeTeamScore).thenReturn(awayTeamScore).thenReturn(5);; // Scores

        // Simulate exception from the scoreboard
        doThrow(new MatchNotFoundException("Match not found")).when(mockScoreboard).updateMatchScore(matchId, homeTeamScore, awayTeamScore);

        // Act
        liveFootballScoreboardApp.run();

        // Assert
        verify(mockScoreboard).updateMatchScore(matchId, homeTeamScore, awayTeamScore); // Verify interaction

    }

    @Test
    void testUpdateMatchScoreByMatchId_IllegalArgumentException() {
        // Arrange
        String matchId = "12345";
        int homeTeamScore = 3;
        int awayTeamScore = 2;

        // Simulate user input
        when(mockInputWrapper.nextLine()).thenReturn("").thenReturn("").thenReturn(matchId).thenReturn(""); // Invalid Match ID
        when(mockInputWrapper.nextInt()).thenReturn(2).thenReturn(1).thenReturn(homeTeamScore).thenReturn(awayTeamScore).thenReturn(5); // Scores

        // Simulate exception from the scoreboard
        doThrow(new MatchNotFoundException("Match not found")).when(mockScoreboard).updateMatchScore(matchId, homeTeamScore, awayTeamScore);

        // Act
        liveFootballScoreboardApp.run();

        // Assert
        verify(mockScoreboard).updateMatchScore(matchId, homeTeamScore, awayTeamScore);

    }

    @Test
    void testUpdateMatchScoreByMatchId_InvalidOption() {
        // Arrange
        String matchId = "12345";
        int homeTeamScore = 3;
        int awayTeamScore = 2;

        // Simulate user input
        when(mockInputWrapper.nextLine()).thenReturn("").thenReturn("").thenReturn(matchId).thenReturn("");
        when(mockInputWrapper.nextInt()).thenReturn(3).thenReturn(5);

        // Act
        liveFootballScoreboardApp.run();

        // Assert
        verify(mockScoreboard, never()).updateMatchScore(matchId, homeTeamScore, awayTeamScore);

    }

}
