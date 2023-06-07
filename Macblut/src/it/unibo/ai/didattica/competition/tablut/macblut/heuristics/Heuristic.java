package it.unibo.ai.didattica.competition.tablut.macblut.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface Heuristic {
    double heuristic(State state, State.Turn turn);
}
