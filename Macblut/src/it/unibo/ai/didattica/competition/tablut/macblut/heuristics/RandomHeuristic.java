package it.unibo.ai.didattica.competition.tablut.macblut.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.Random;

public class RandomHeuristic implements Heuristic {
    private final Random r = new Random();
    @Override
    public double heuristic(State state, State.Turn turn) {
        return r.nextDouble();
    }
}
