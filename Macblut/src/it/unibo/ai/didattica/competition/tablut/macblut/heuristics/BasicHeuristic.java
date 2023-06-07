package it.unibo.ai.didattica.competition.tablut.macblut.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.macblut.Util;

public class BasicHeuristic implements Heuristic {

    public double heuristic(State state, State.Turn turn) {
        if (Util.checkEscape(state.getBoard())) return turn == State.Turn.WHITE ? 1.0 : 0;
        if (state.getNumberOf(State.Pawn.KING) == 0) return turn == State.Turn.BLACK ? 1.0 : 0;

        return 0.5 * defensiveHeuristic(state, turn) + 0.5 * aggressiveHeuristic(state, turn);
    }

    private double defensiveHeuristic(State state, State.Turn turn) {
        State.Pawn pawnType = turn == State.Turn.BLACK ? State.Pawn.BLACK : State.Pawn.WHITE;
        double totalPawns = turn == State.Turn.BLACK ? 16 : 8;
        return state.getNumberOf(pawnType) / totalPawns;
    }

    private double aggressiveHeuristic(State state, State.Turn turn) {
        State.Pawn pawnType = turn == State.Turn.BLACK ? State.Pawn.WHITE : State.Pawn.BLACK;
        double totalPawns = turn == State.Turn.BLACK ? 8 : 16;
        return 1 - state.getNumberOf(pawnType) /  totalPawns;
    }
}
