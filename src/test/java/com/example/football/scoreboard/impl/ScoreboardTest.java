package com.example.football.scoreboard.impl;

import com.example.football.scoreboard.MatchStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;


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

        // Act
        scoreboard.startMatch(homeTeam, awayTeam);

        // Assert
        verify(matchStorage).saveMatch(argThat(match ->
                match.getHomeTeam().equals(homeTeam) &&
                        match.getAwayTeam().equals(awayTeam) &&
                        match.getHomeTeamScore() == 0 &&
                        match.getAwayTeamScore() == 0));

    }
}
