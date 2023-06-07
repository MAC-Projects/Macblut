package it.unibo.ai.didattica.competition.tablut.macblut.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.macblut.Util;

public class DumbHeuristic implements Heuristic {
    @Override
    public double heuristic(State state, State.Turn turn) {
        return 0.5;
    }
}
