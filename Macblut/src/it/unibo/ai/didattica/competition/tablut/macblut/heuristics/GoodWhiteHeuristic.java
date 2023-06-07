package it.unibo.ai.didattica.competition.tablut.macblut.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.macblut.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleToLongFunction;

public class GoodWhiteHeuristic implements Heuristic {
    private final int WHITE = 0;
    private final int BLACK = 1;

    public double heuristic(State state, State.Turn player) {
        double result = 0;
        State.Pawn[][] board = state.getBoard();

        int[] kingPosition = Util.getKingPosition(board);

        if (Util.checkEscape(board)) return Double.POSITIVE_INFINITY;
        if (kingPosition == null) return Double.NEGATIVE_INFINITY;

        if (Util.kingIsEatable(state.getBoard())) {
            if (state.getTurn() == State.Turn.WHITE)
                result -= 0;
            else
                return Double.NEGATIVE_INFINITY;
        }

        int[] pawnAmounts = Util.getAmountOfPawns(board);
        int amountOfWhite = pawnAmounts[0];
        int amountOfBlack = pawnAmounts[1];

        int[] amountOfEatables = Util.getAmountOfEatables(board);

        if (state.getTurn() == State.Turn.WHITE && amountOfEatables[BLACK] > 0) {
            //result += 0 * (1 - (double) amountOfEatables[WHITE] / amountOfWhite);
            //result += 5 * ((double) amountOfEatables[BLACK] / amountOfBlack);
            result += 49 * (1 - (double) (amountOfBlack-1) / 16);
        } else {
            //result += 15 * (1 - (double) amountOfEatables[WHITE] / amountOfWhite);
            //result += 0 * ((double) amountOfEatables[BLACK] / amountOfBlack);
            result += 50 * (1 - (double) amountOfBlack / 16);
        }

        result += getKingEscapeHeuristic(state, player, kingPosition);
        //result += (fractionOfEatenBlack > 0.3 ? 1 : 0) * getKingAwayFromCenterHeuristic(state, turn, kingPosition);
        //result += 10 * getNumberOfKingMovesHeuristic(state, kingPosition);
        //result += 5 * getKingProtectorHeuristic(board, kingPosition);
        //result += 5 * ((double) amountOfEatables[BLACK] / amountOfBlack);
        //result += 45 * (1 - (double) amountOfEatables[WHITE] / amountOfWhite);

        result += 50 * ((double) amountOfWhite / 8);
        result += 2.5 * (kingProximityHeuristic(amountOfBlack, board, kingPosition));

        if (amountOfBlack > 15)
            result += 1.5 * isKingInCastleHeuristic(kingPosition);

        return result;
    }

    private double getKingEscapeHeuristic(State state, State.Turn player, int[] kingPosition) {
        List<int[]> kingEscapes = Util.getPossibleKingEscapes(state.getBoard(), kingPosition[0], kingPosition[1]);

        if (kingEscapes.size() > 1)
            return 500; // Multiple escapes cannot all be blocked at once
        if (kingEscapes.size() == 1) {
            if (kingEscapes.get(0)[0] == 1) {
                if (state.getTurn() == State.Turn.WHITE)
                    return 500; // Blockable escape but it's our turn
                else
                    return 0; // Blockable escape
            } else {
                return 500; // Non-blockable escape
            }
        }
        return 0; // No escapes
    }

    private double getKingAwayFromCenterHeuristic(State state, State.Turn player, int[] kingPosition) {
        int distanceX = Math.abs(kingPosition[0] - 4);
        int distanceY = Math.abs(kingPosition[1] - 4);
        if (distanceX == 1 || distanceY == 1)
            return 1;
        else if (distanceX == 2 || distanceY == 2)
            return 3;
        else if (distanceX == 3 || distanceY == 3)
            return 1;
        return 0;
    }

    private double getNumberOfKingMovesHeuristic(State state, int[] kingPosition) {
        List<Action> moves = new ArrayList<>();
        Util.getAvailableMoves(state, kingPosition[0], kingPosition[1], moves);
        return (double) moves.size() / 10;
    }

    private double getKingProtectorHeuristic(State.Pawn[][] board, int[] kingPosition) {
        boolean foundHorizontal = false;
        boolean foundVertical = false;
        for (int dir = 0; dir < 4; dir++) {
            int cellX = kingPosition[0] + Util.getIncrementXFromDir(dir);
            int cellY = kingPosition[1] + Util.getIncrementYFromDir(dir);
            if (board[cellY][cellX] == State.Pawn.WHITE) {
                if (dir < 2)
                    foundHorizontal = true;
                else
                    foundVertical = true;
            }
        }
        return (foundHorizontal ? 0.5 : 0) + (foundVertical ? 0.5 : 0);
    }

    public double kingProximityHeuristic(int amountOfBlack, State.Pawn[][] board, int[] kingPosition) {
        int distanceSum = 0;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (board[y][x] == State.Pawn.BLACK) {
                    distanceSum += Util.getDistanceFromKing(x, y, kingPosition);
                }
            }
        }
        return (double) distanceSum / amountOfBlack / 8;
    }

    public double isKingInCastleHeuristic(int[] kingPosition) {
        return Util.isCastle(kingPosition[0], kingPosition[1]) ? 1.0 : 0.0;
    }
}
