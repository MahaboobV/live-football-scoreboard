package com.example.football.scoreboard.integration;

import com.example.football.scoreboard.service.MatchStorage;
import com.example.football.scoreboard.exception.MatchNotFoundException;
import com.example.football.scoreboard.impl.InmemoryMatchStorage;
import com.example.football.scoreboard.impl.Scoreboard;
import com.example.football.scoreboard.model.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void testStartMatch_ConcurrentStart() throws InterruptedException {

        // Arrange
        String homeTeam = "Team A";
        String awayTeam = "Team B";

        AtomicInteger exceptionCount = new AtomicInteger(0);
        AtomicReference<Match> matchRef= new AtomicReference<>();


        Runnable startMatchTask = () -> {
            try{
                matchRef.set(scoreboard.startMatch(homeTeam, awayTeam));
            }catch (Exception e){
                exceptionCount.incrementAndGet();
            }
        };

        // Creating two threads to simulate concurrent match starts

        Thread thread1 = new Thread(startMatchTask);
        Thread thread2 = new Thread(startMatchTask);

        //start both threads

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // Asserts

        assertNotNull(matchRef.get().getMatchId());
        assertEquals("Team A", matchRef.get().getHomeTeam());
        assertEquals("Team A", matchRef.get().getHomeTeam());
        assertTrue(matchRef.get().isLive());
        assertEquals(1, exceptionCount.get());
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
    void testUpdateMatch_ConcurrentUpdate() throws InterruptedException {

        // Arrange
        Match match = scoreboard.startMatch("Team A", "Team B");

        AtomicInteger exceptionCount = new AtomicInteger(0);


        Runnable startMatchTask = () -> {
            try{
                scoreboard.updateMatchScore(match.getMatchId(), 1, 2);
            }catch (Exception e){
                exceptionCount.incrementAndGet();
            }
        };

        // Creating two threads to simulate concurrent match starts

        Thread thread1 = new Thread(startMatchTask);
        Thread thread2 = new Thread(startMatchTask);

        //start both threads

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // Asserts

        assertEquals(1, match.getHomeTeamScore());
        assertEquals(2, match.getAwayTeamScore());
        assertEquals(1, exceptionCount.get());
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

