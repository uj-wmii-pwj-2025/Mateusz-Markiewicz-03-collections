package uj.wmii.pwj.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleshipGeneratorImpl implements BattleshipGenerator {

    private final Random random = new Random();

    @Override
    public String generateMap() {
        char[][] board = new char[10][10];
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                board[i][j] = '.';

        int[] shipSizes = {1, 2, 3, 4};

        for (int size : shipSizes) {
            int count = 5 - size;

            for (int i = 0; i < count; i++) {
                boolean placed = false;
                int attempts = 0;
                while (!placed && attempts < 1000) {
                    if (size == 1) {
                        placed = placeSingleShip(board);
                    } else {
                        placed = placeShipWithShape(board, size);
                    }
                    attempts++;
                }
                if (!placed) {
                    throw new RuntimeException("Failed to place all ships on the board.");
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) sb.append(board[i][j]);
        }
        return sb.toString();
    }

    private boolean placeSingleShip(char[][] board) {
        int row = random.nextInt(10);
        int col = random.nextInt(10);

        if (canPlaceCell(board, row, col)) {
            board[row][col] = '#';
            return true;
        }
        return false;
    }

    private boolean placeShipWithShape(char[][] board, int size) {
        int startRow = random.nextInt(10);
        int startCol = random.nextInt(10);

        if (!canPlaceCell(board, startRow, startCol)) {
            return false;
        }

        List<int[]> shipCells = new ArrayList<>();
        shipCells.add(new int[]{startRow, startCol});

        for (int segment = 1; segment < size; segment++) {
            List<int[]> possibleNextCells = getPossibleNextCells(board, shipCells);

            if (possibleNextCells.isEmpty()) {
                return false;
            }

            int[] nextCell = possibleNextCells.get(random.nextInt(possibleNextCells.size()));
            shipCells.add(nextCell);
        }

        for (int[] cell : shipCells) {
            board[cell[0]][cell[1]] = '#';
        }

        return true;
    }

    private List<int[]> getPossibleNextCells(char[][] board, List<int[]> currentShip) {
        List<int[]> possibleCells = new ArrayList<>();
        int[] lastCell = currentShip.getLast();

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int newRow = lastCell[0] + dir[0];
            int newCol = lastCell[1] + dir[1];

            if (isValidPosition(newRow, newCol) && canPlaceCell(board, newRow, newCol) && !isCellInShip(newRow, newCol, currentShip)) {
                possibleCells.add(new int[]{newRow, newCol});
            }
        }

        return possibleCells;
    }

    private boolean isCellInShip(int row, int col, List<int[]> ship) {
        for (int[] cell : ship) {
            if (cell[0] == row && cell[1] == col) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceCell(char[][] board, int row, int col) {
        if (!isValidPosition(row, col)) return false;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (isValidPosition(r, c) && board[r][c] == '#') {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }
}