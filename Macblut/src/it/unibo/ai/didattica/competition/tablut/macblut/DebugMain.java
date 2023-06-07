package it.unibo.ai.didattica.competition.tablut.macblut;

import it.unibo.ai.didattica.competition.tablut.domain.*;

public class DebugMain {
    public static void main(String[] args) {
        MacblutGame aimaGame = new MacblutGame();
        Search search = new Search(aimaGame, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 5);

        State state = new StateTablut();

        State.Pawn[][] board = new State.Pawn[][]{
                new State.Pawn[]{ State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.BLACK, State.Pawn.BLACK, State.Pawn.BLACK, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY},
                new State.Pawn[]{ State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.BLACK, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY},
                new State.Pawn[]{ State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.WHITE, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY},
                new State.Pawn[]{ State.Pawn.BLACK, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.WHITE, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.BLACK},
                new State.Pawn[]{ State.Pawn.BLACK, State.Pawn.BLACK, State.Pawn.WHITE, State.Pawn.WHITE, State.Pawn.THRONE, State.Pawn.WHITE, State.Pawn.WHITE, State.Pawn.BLACK, State.Pawn.BLACK},
                new State.Pawn[]{ State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.BLACK, State.Pawn.KING, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.BLACK},
                new State.Pawn[]{ State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.WHITE, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY},
                new State.Pawn[]{ State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY},
                new State.Pawn[]{ State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.BLACK, State.Pawn.BLACK, State.Pawn.BLACK, State.Pawn.EMPTY, State.Pawn.EMPTY, State.Pawn.EMPTY},
        };

        state.setBoard(board);
        state.setTurn(State.Turn.WHITE);

        Action action = search.makeDecision(state);

        System.out.println("CHOSEN ACTION: " + action);
    }
}
