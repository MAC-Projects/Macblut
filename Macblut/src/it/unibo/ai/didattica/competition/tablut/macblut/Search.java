package it.unibo.ai.didattica.competition.tablut.macblut;

import aima.core.search.adversarial.Game;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class Search extends IterativeDeepeningAlphaBetaSearch<State, Action, State.Turn> {

    public Search(Game<State, Action, State.Turn> game, double utilMin, double utilMax, int time) {
        super(game, utilMin, utilMax, time);
        //setLogEnabled(true);
    }

    protected double eval(State state, State.Turn turn) {
        super.eval(state, turn);
        return this.game.getUtility(state, turn);
    }
}
