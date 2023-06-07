package it.unibo.ai.didattica.competition.tablut.macblut.heuristics;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.macblut.Util;

import java.lang.reflect.GenericDeclaration;

public class GoodBlackHeuristic implements Heuristic {
    private final int WHITE = 0;
    private final int BLACK = 1;

    public double heuristic(State state, State.Turn turn) {
        double result = 0;
        State.Pawn[][] board = state.getBoard();

        int[] kingPosition = Util.getKingPosition(board);

        if (Util.checkEscape(board)) return Double.NEGATIVE_INFINITY;
        if (kingPosition == null) return Double.POSITIVE_INFINITY;

        if (Util.kingIsEatable(state.getBoard())) {
            if (state.getTurn() == State.Turn.BLACK)
                return Double.POSITIVE_INFINITY;
            else
                result += 0;
        }

        int[] pawnAmounts = Util.getAmountOfPawns(board);
        int amountOfWhite = pawnAmounts[0];
        int amountOfBlack = pawnAmounts[1];

        int[] amountOfEatables = Util.getAmountOfEatables(board);

        if (state.getTurn() == State.Turn.BLACK && amountOfEatables[WHITE] > 0) {
            //result += 0 * (1 - (double) amountOfEatables[BLACK] / amountOfBlack);
            //result += 15 * ((double) amountOfEatables[WHITE] / amountOfWhite); // % Unsafe white pawns
            //if (amountOfEatables[WHITE] > 0) {
            result += 49 * (1 - (double) (amountOfWhite-1) / 8);
            //}
        } else {
            result += 50 * (1 - (double) amountOfWhite / 8); // % Remaining white pawns
            //result += 5 * (1 - (double) amountOfEatables[BLACK] / amountOfBlack); // % Safe black pawns
            //result += 0 * ((double) amountOfEatables[WHITE] / amountOfWhite); // % Unsafe white pawns
        }

        //result += 5 * (1 - (double) amountOfEatables[BLACK] / amountOfBlack); // % Safe black pawns
        if (pawnAmounts[WHITE] > 4)
            result += 3 * rhombusHeuristic(board); // % Rhombus pawns
        result += 50 * ((double) amountOfBlack / 16); // % Remaining black pawns
        //result += 30 * ((double) amountOfEatables[WHITE] / amountOfWhite); // % Unsafe white pawns
        //result += 50 * (1 - (double) amountOfWhite / 8); // % Remaining white pawns
        result += 3 * kingProximityHeuristic(amountOfBlack, board, kingPosition);

        return result;
    }

    public double rhombusHeuristic(State.Pawn[][] board) {
        int foundInRhombus = 0;
        for (int y = 0; y < board.length; y++) {
            for (int x = 0; x < board[y].length; x++) {
                if (isInRhombus(x, y, board) && board[y][x] == State.Pawn.BLACK) {
                    foundInRhombus++;
                }
            }
        }
        return (double) foundInRhombus / 8;
    }

    public boolean isInRhombus(int x, int y, State.Pawn[][] board) {
        return !Util.isCamp(x, y) &&
                ((x + y == 5) ||  // Top-left diagonal
                (x - y == 3) ||  // Top-right diagonal
                (y - x == 3) ||  // Bottom-left diagonal
                (x + y == 11));   // Bottom-right diagonal
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
        return 1 - (double) distanceSum / amountOfBlack / 8;
    }
}
