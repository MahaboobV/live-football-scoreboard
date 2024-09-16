package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.Match;
import com.example.football.scoreboard.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InmemoryMatchStorageTest {

    private MatchStorage matchStorage;

    @BeforeEach
    void setUp() {
        matchStorage = new InmemoryMatchStorage();
    }

    @Test
    void testGetAllMatches_Empty() {
        // Act
        List<Match> matches = matchStorage.getAllMatches();

        assertTrue(matches.isEmpty(), "Expected no matches to be returned");
    }

    @Test
    void testFindMatch_NotFound() {
        // Arrange
        String matchId = UUID.randomUUID().toString();

        // Act
        MatchNotFoundException exception = assertThrows(MatchNotFoundException.class, () -> matchStorage.findMatch(matchId));

        // Assert
        assertEquals("No match found with ID: "+matchId , exception.getMessage());
    }

    @Test
    void testSaveAndFindMatch() {
        // Arrange
        String homeTeam = "Team A";
        String awayTeam = "Team B";
        LocalDateTime now = LocalDateTime.now();

        Match match = new Match(homeTeam, awayTeam, 0, 0, now);

        // Act
        matchStorage.saveMatch(match);

        Match foundMatch = matchStorage.findMatch(match.getMatchId());

        // Assert
        assertNotNull(foundMatch);
        assertEquals(match.getMatchId(), foundMatch.getMatchId());
        assertEquals(match.getHomeTeam(), foundMatch.getHomeTeam());
        assertEquals(match.getAwayTeam(), foundMatch.getAwayTeam());
        assertEquals(match.getTotalScore(), foundMatch.getTotalScore());
    }

    @Test
    void testGetAllMatches() {
        // Arrange
        Match match1 = new Match("Home Team A", "Away Team A", 0, 0, LocalDateTime.now());

        Match match2 = new Match("Home Team B", "Away Team B", 0, 0, LocalDateTime.now());

        // Act
        matchStorage.saveMatch(match1);
        matchStorage.saveMatch(match2);

        List<Match> matches = matchStorage.getAllMatches();

        // Assert
        assertEquals(2 , matches.size());

        assertTrue(matches.contains(match1));
        assertTrue(matches.contains(match2));

    }

    @Test
    void testUpdateExistingMatch() {
        // Arrange
        Match match = new Match("Home Team A", "Away Team A", 0, 0, LocalDateTime.now());

        //Act
        matchStorage.saveMatch(match);

        match.setHomeTeamScore(1);
        match.setAwayTeamScore(3);

        matchStorage.saveMatch(match);

        Match updatedMatch = matchStorage.findMatch(match.getMatchId());

        // Assert
        assertNotNull(updatedMatch);
        assertEquals(1, updatedMatch.getHomeTeamScore());
        assertEquals(3, updatedMatch.getAwayTeamScore());
    }

}