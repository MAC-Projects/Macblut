package it.unibo.ai.didattica.competition.tablut.macblut;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static boolean areValidIndices(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < 9;
    }

    public static boolean isSolid(int originX, int originY, int x, int y, State.Pawn[][] board) {
        State.Pawn pawn = board[y][x];
        return pawn == State.Pawn.WHITE || pawn == State.Pawn.BLACK || pawn == State.Pawn.KING ||
                (isCamp(x, y) && (!isCamp(originX, originY) || Math.abs(x - originX + y - originY) > 3)) ||
                isCastle(x, y);
    }

    public static boolean isCapturer(int capturedX, int capturedY, int x, int y, State.Pawn[][] board) {
        return ((board[capturedY][capturedX] == State.Pawn.WHITE || board[capturedY][capturedX] == State.Pawn.KING) && board[y][x] == State.Pawn.BLACK) ||
                (board[capturedY][capturedX] == State.Pawn.BLACK && (board[y][x] == State.Pawn.WHITE || board[y][x] == State.Pawn.KING)) ||
                (isCamp(x, y) && !isCamp(capturedX, capturedY)) || isCastle(x, y);
    }

    public static boolean isCaptureAgainstWall(int capturedX, int capturedY, int wallX, int wallY) {
        return (isCamp(wallX, wallY) && !isCamp(capturedX, capturedY)) || isCastle(wallX, wallY);
    }

    public static boolean isCamp(int x, int y) {
        return (x >= 3 && x <= 5 && (y == 0 || y == 8)) || (y >= 3 && y <= 5 && (x == 0 || x == 8)) ||
                (x == 4 && (y == 1 || y == 7)) || (y == 4 && (x == 1 || x == 7));
    }

    public static boolean isCastle(int x, int y) {
        return x == 4 && y == 4;
    }

    public static boolean isInCenter(int x, int y) {
        return (x == 4 && y >= 3 && y <= 5) || (y == 4 && x >= 3 && x <= 5);
    }

    public static boolean isBlackOrCastle(int x, int y, State.Pawn[][] board) {
        return isCastle(x, y) || board[y][x] == State.Pawn.BLACK;
    }

    public static int[] getKingPosition(State.Pawn[][] board) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (board[y][x] == State.Pawn.KING) {
                    return new int[]{ x, y };
                }
            }
        }
        return null;
    }

    /**
     * Moves a pawn to a specific cell according to an action and resolves any resulting captures
     * @param state The current game state
     * @param action The action to perform
     * @return The new game state after the move
     */
    public static State move(State state, Action action) {
        int x1 = action.getColumnFrom();
        int y1 = action.getRowFrom();
        int x2 = action.getColumnTo();
        int y2 = action.getRowTo();

        State.Pawn[][] oldBoard = state.getBoard();
        State.Pawn[][] board = new State.Pawn[oldBoard.length][];
        for (int i = 0; i < oldBoard.length; i++) {
            board[i] = oldBoard[i].clone();
        }

        State newState = new StateTablut();
        newState.setTurn(state.getTurn() == State.Turn.WHITE ? State.Turn.BLACK : State.Turn.WHITE);

        State.Pawn ownColor = board[y1][x1];
        State.Pawn otherColor = ownColor == State.Pawn.BLACK ? State.Pawn.WHITE : State.Pawn.BLACK;

        // First we move the pawn
        board[y2][x2] = board[y1][x1];
        board[y1][x1] = State.Pawn.EMPTY;

        // Then we check for captures
        for (int dir = 0; dir < 4; dir++) {
            // 0 right, 1 left
            // 2 down, 3 up
            int incrementX = dir == 0 ? 1 : dir == 1 ? -1 : 0;
            int incrementY = dir == 2 ? 1 : dir == 3 ? -1 : 0;

            int capturedX = x2 + incrementX;
            int capturedY = y2 + incrementY;

            if (areValidIndices(capturedX, capturedY)) {
                int otherCapturerX = x2 + 2 * incrementX;
                int otherCapturerY = y2 + 2 * incrementY;

                // Capture of ordinary pawn
                if (areValidIndices(otherCapturerX, otherCapturerY) &&
                        board[capturedY][capturedX] == otherColor) {

                    if (board[otherCapturerY][otherCapturerX] == ownColor || // Capture with pawn on the other side
                            isCaptureAgainstWall(capturedX, capturedY, otherCapturerX, otherCapturerY)) { // Capture against wall
                        board[capturedY][capturedX] = State.Pawn.EMPTY;
                    }
                }

                // Capture of king
                if (board[capturedY][capturedX] == State.Pawn.KING && ownColor == State.Pawn.BLACK) {

                    if (isInCenter(capturedX, capturedY)) {
                        // The king is in the center, capturing requires special conditions
                        if (isBlackOrCastle(capturedX + 1, capturedY, board) &&
                                isBlackOrCastle(capturedX, capturedY + 1, board) &&
                                isBlackOrCastle(capturedX - 1, capturedY, board) &&
                                isBlackOrCastle(capturedX, capturedY - 1, board)) {
                            board[capturedY][capturedX] = State.Pawn.EMPTY;
                        }
                    } else {
                        // The king is not in the center, capturing works like with any other pawn
                        if (board[y2 + 2 * incrementY][x2 + 2 * incrementX] == ownColor || // Capture with pawn on the other side
                                isCaptureAgainstWall(capturedX, capturedY, x2 + 2 * incrementX, y2 + 2 * incrementY)) { // Capture against wall
                            board[capturedY][capturedX] = State.Pawn.EMPTY;
                        }
                    }
                }
            }
        }

        newState.setBoard(board);

        return newState;
    }

    public static void getAvailableMoves(State state, int x, int y, List<Action> actions) {
        State.Pawn[][] board = state.getBoard();
        for (int dir = 0; dir < 4; dir++) {
            int increment = dir % 2 == 0 ? +1 : -1;
            int[] sx = new int[1];
            int[] sy = new int[1];
            int[] stepVariable = dir < 2 ? sx : sy;
            for (stepVariable[0] = increment; x + sx[0] >= 0 && y + sy[0] >= 0 &&
                    x + sx[0] < 9 && y + sy[0] < 9 &&
                    !Util.isSolid(x, y, x + sx[0], y + sy[0], board); stepVariable[0] += increment) {
                try {
                    actions.add(new Action(state.getBox(y, x), state.getBox(y + sy[0], x + sx[0]), state.getTurn()));
                } catch (IOException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        }
    }

    public static boolean pawnIsCapturable(int x, int y, State.Pawn[][] board) {
        if (board[y][x] == State.Pawn.KING && isInCenter(x, y))
            return kingInCenterIsCapturable(x, y, board);
        else
            return regularPawnIsCapturable(x, y, board);
    }

    public static boolean regularPawnIsCapturable(int x, int y, State.Pawn[][] board) {
        State.Pawn attackedPawn = board[y][x];
        // State.Pawn attackerPawn = getOppositePawn(attackedPawn);

        for (int dir = 0; dir < 4; dir++) {
            int incrementX = getIncrementXFromDir(dir);
            int incrementY = getIncrementYFromDir(dir);

            int attackerX = x + incrementX;
            int attackerY = y + incrementY;

            if (!areValidIndices(attackerX, attackerY))
                continue;

            if (isCapturer(x, y, attackerX, attackerY, board)) {
                int originX = x - incrementX;
                int originY = y - incrementY;
                if (!areValidIndices(originX, originY) || board[originY][originX] != State.Pawn.EMPTY)
                    continue;

                if (findCapturerFromOrigin(attackedPawn, originX, originY, board))
                    return true;
            }
        }
        return false;
    }

    public static boolean kingInCenterIsCapturable(int x, int y, State.Pawn[][] board) {
        int emptyX = -1, emptyY = -1;
        for (int dir = 0; dir < 4; dir++) {
            int cellX = x + getIncrementXFromDir(dir);
            int cellY = y + getIncrementYFromDir(dir);

            if (board[cellY][cellX] == State.Pawn.WHITE)
                return false; // The king is not capturable, as there is a white pawn next to it.
            if (board[cellY][cellX] == State.Pawn.EMPTY && !isCastle(cellX, cellY)) {
                if (emptyX == -1) {
                    emptyX = cellX;
                    emptyY = cellY;
                } else {
                    return false;   // The king is not capturable, as there is more than one empty space next to it.
                }
            }
        }
        return findCapturerFromOrigin(State.Pawn.KING, emptyX, emptyY, board);
    }

    public static boolean findCapturerFromOrigin(State.Pawn capturedPawn, int originX, int originY, State.Pawn[][] board) {
        outerFor:
        for (int dir = 0; dir < 4; dir++) {
            int incrementX = getIncrementXFromDir(dir);
            int incrementY = getIncrementYFromDir(dir);

            int x, y;

            for (y = originY + incrementY, x = originX + incrementX;; y += incrementY, x += incrementX) {
                if (!areValidIndices(x, y))
                    continue outerFor; // No pawns found in this direction, stop searching in this dir altogether.

                if (((capturedPawn == State.Pawn.WHITE || capturedPawn == State.Pawn.KING) && board[y][x] == State.Pawn.BLACK) ||
                        (capturedPawn == State.Pawn.BLACK && (board[y][x] == State.Pawn.WHITE || board[y][x] == State.Pawn.KING)))
                    return true; // Capturer pawn found in this direction, return.

                if (isCamp(x, y)) {
                    if (!isCamp(x + incrementX, y + incrementY))
                        continue outerFor; // No pawns found inside the camp, so there's no pawns found in this direction.
                    continue; // If a camp is found before a capturer pawn, continue searching in the camp.
                }

                if (isSolid(originX, originY, x, y, board))
                    continue outerFor; // Solid found which is not enemy, stop searching in this dir altogether.
            }
        }
        return false;
    }

    public static boolean isEscape(int x, int y) {
        return ((y == 0 || y == 8) && (x == 1 || x == 2 || x == 6 || x == 7)) ||
                ((x == 0 || x == 8) && (y == 1 || y == 2 || y == 6 || y == 7));
    }

    private static final int[][] escapeCells = new int[][]{
            new int[]{ 1, 0 }, new int[]{ 2, 0 }, new int[]{ 6, 0 }, new int[]{ 7, 0 },
            new int[]{ 1, 8 }, new int[]{ 2, 8 }, new int[]{ 6, 8 }, new int[]{ 7, 8 },
    };

    public static boolean checkEscape(State.Pawn[][] board) {
        for (int[] coords : escapeCells) {
            if (board[coords[0]][coords[1]] == State.Pawn.KING || board[coords[1]][coords[0]] == State.Pawn.KING) {
                return true;
            }
        }

        return false;
    }

    public static boolean kingIsEatable(State.Pawn[][] board) {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (board[y][x] == State.Pawn.KING)
                    return Util.pawnIsCapturable(x, y, board);
            }
        }
        return true;
    }

    public static List<int[]> getPossibleKingEscapes(State.Pawn[][] board, int kingX, int kingY) {
        List<int[]> result = new ArrayList<>();
        outerFor:
        for (int dir = 0; dir < 4; dir++) {
            int incrementX = getIncrementXFromDir(dir);
            int incrementY = getIncrementYFromDir(dir);

            boolean isBlockable = false;

            for (int x = kingX + incrementX, y = kingY + incrementY;; x += incrementX, y += incrementY) {
                if (!areValidIndices(x, y)) // End of board found: stop exploring in this dir, go to the next.
                    continue outerFor;
                if (isSolid(kingX, kingY, x, y, board))
                    continue outerFor; // Solid object found: stop exploring in this dir, go to the next.
                if (findCapturerFromOrigin(State.Pawn.KING, x, y, board))
                    isBlockable = true;

                if (isEscape(x, y)) {
                    result.add(new int[]{ isBlockable ? 1 : 0, x, y });
                    break;
                }
            }
        }
        return result;
    }

    public static State.Pawn getOppositePawn(State.Pawn pawn) {
        // TODO: Usare questo metodo nei posti in cui il tipo di pedone opposto è calcolato manualmente
        if (pawn == State.Pawn.WHITE || pawn == State.Pawn.KING) {
            return State.Pawn.BLACK;
        } else if (pawn == State.Pawn.BLACK) {
            return State.Pawn.WHITE;
        } else throw new IllegalArgumentException("Invalid opposite pawn request; only black or white pawns have an opposite");
    }

    public static int getIncrementXFromDir(int dir) {
        switch (dir) {
            case 0: return 1;
            case 1: return -1;
            default: return 0;
        }
    }

    public static int getIncrementYFromDir(int dir) {
        switch (dir) {
            case 2: return 1;
            case 3: return -1;
            default: return 0;
        }
    }

    public static int[] getAmountOfPawns(State.Pawn[][] board) {
        int[] result = new int[]{ 0, 0 };
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (board[y][x] == State.Pawn.WHITE)
                    result[0]++;
                else if (board[y][x] == State.Pawn.BLACK)
                    result[1]++;
            }
        }
        return result;
    }

    public static int[] getAmountOfEatables(State.Pawn[][] board)  {
        int eatableWhites = 0, eatableBlacks = 0;
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (board[y][x] == State.Pawn.WHITE) {
                    if (Util.regularPawnIsCapturable(x, y, board)) {
                        eatableWhites++;
                    }
                } else if (board[y][x] == State.Pawn.BLACK) {
                    if (Util.regularPawnIsCapturable(x, y, board)) {
                        eatableBlacks++;
                    }
                }
            }
        }
        return new int[]{ eatableWhites, eatableBlacks };
    }

    public static int getDistanceFromKing(int x, int y, int[] kingPosition) {
        return (Math.abs(x - kingPosition[0]) + Math.abs(y - kingPosition[1])) / 2;
    }

    public static boolean isInTopRightSmallQuadrant(int x, int y) {
        return (x == 6 && (y == 0 || y == 1)) ||
                (x == 7 && (y == 0 || y == 1 || y == 2)) ||
                (x == 8 && (y == 1 || y == 2));
    }

    public static boolean isInBottomRightSmallQuadrant(int x, int y) {
        return (x == 6 && (y == 7 || y == 8)) ||
                (x == 7 && (y == 6 || y == 7 || y == 8)) ||
                (x == 8 && (y == 6 || y == 7));
    }


}

