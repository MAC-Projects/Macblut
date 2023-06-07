package it.unibo.ai.didattica.competition.tablut.macblut;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.macblut.heuristics.GoodBlackHeuristic;
import it.unibo.ai.didattica.competition.tablut.macblut.heuristics.GoodWhiteHeuristic;
import it.unibo.ai.didattica.competition.tablut.macblut.heuristics.Heuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MacblutGame implements Game<State, Action, State.Turn> {
    Heuristic whiteHeuristic = new GoodWhiteHeuristic();
    Heuristic blackHeuristic = new GoodBlackHeuristic();

    @Override
    public State getInitialState() {
        return new StateTablut();
    }

    @Override
    public State.Turn[] getPlayers() {
        return new State.Turn[]{ State.Turn.WHITE, State.Turn.BLACK };
    }

    @Override
    public State.Turn getPlayer(State state) {
        return state.getTurn();
    }

    @Override
    public List<Action> getActions(State state) {
        List<Action> result = new ArrayList<>();
        State.Pawn[][] board = state.getBoard();

        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if ((state.getTurn() == State.Turn.WHITE && (board[y][x] == State.Pawn.WHITE || board[y][x] == State.Pawn.KING)) ||
                        (state.getTurn() == State.Turn.BLACK && board[y][x] == State.Pawn.BLACK)) {
                    Util.getAvailableMoves(state, x, y, result);
                }
            }
        }

        Collections.shuffle(result);

        return result;
    }

    @Override
    public State getResult(State state, Action action) {
        return Util.move(state, action);
    }

    @Override
    public boolean isTerminal(State state) {
        return state.getNumberOf(State.Pawn.WHITE) == 0 || state.getNumberOf(State.Pawn.BLACK) == 0 ||
                state.getNumberOf(State.Pawn.KING) == 0 || Util.checkEscape(state.getBoard());
    }

    @Override
    public double getUtility(State state, State.Turn player) {
        State.Turn turn = state.getTurn();

        if (player == State.Turn.WHITE) {
            //return (turn == State.Turn.WHITE) ? whiteHeuristic.heuristic(state, player) : -blackHeuristic.heuristic(state, player);
            return whiteHeuristic.heuristic(state, player);
        } else {
            //return (turn == State.Turn.BLACK) ? blackHeuristic.heuristic(state, player) : -whiteHeuristic.heuristic(state, player);
            return blackHeuristic.heuristic(state, player);
        }
    }
}
