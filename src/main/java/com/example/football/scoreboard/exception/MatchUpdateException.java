package com.example.football.scoreboard.exception;

public class MatchUpdateException extends RuntimeException{
    public MatchUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
