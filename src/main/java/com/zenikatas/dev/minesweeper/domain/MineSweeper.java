package com.zenikatas.dev.minesweeper.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MineSweeper {
    private final Cell[][] grid;
    private boolean isMineUncovered = false;

    public MineSweeper(String[][] grid) {
        this.grid = new Cell[grid.length][grid[0].length];

        initializeGrid(grid);
    }

    public boolean isWon() {
        return !isMineUncovered &&
                allCells()
                        .filter(cell -> !cell.isAMine())
                        .allMatch(Cell::isUncovered);
    }

    public boolean isTerminated() {
        return isMineUncovered || isWon();
    }

    public Cell cellAt(int row, int col) {
        return grid[row][col];
    }

    public void uncoverCellAt(int row, int col) {
        if (cellAt(row, col).isAMine()) {
            isMineUncovered = true;
        } else {
            uncoverAdjacentCells(row, col);
        }
    }

    public int rowsLength() {
        return grid.length;
    }

    public int columnsLength() {
        return grid[0].length;
    }

    public int numberOfMine() {
        return (int) allMines()
                .count();
    }

    public void revealMines() {
        if (isTerminated()) {
            allMines()
                    .forEach(Cell::reveal);
        }
    }

    private void initializeGrid(String[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                this.grid[i][j] = new Cell(grid[i][j]);
            }
        }
    }

    private Stream<Cell> allCells() {
        return Arrays.stream(grid)
                .flatMap(Arrays::stream);
    }

    private void uncoverAdjacentCells(int row, int col) {
        if (isCellOnTheEdge(row, col)) {
            return;
        }
        if (cellAt(row, col).isUncovered()) {
            return;
        }

        cellAt(row, col).uncover(countNumberOfAdjacentMine(row, col));

        if (isAdjacentCellsContainAMine(row, col)) {
            return;
        }
        uncoverAdjacentCells(row - 1, col - 1);
        uncoverAdjacentCells(row - 1, col);
        uncoverAdjacentCells(row - 1, col + 1);
        uncoverAdjacentCells(row, col - 1);
        uncoverAdjacentCells(row, col + 1);
        uncoverAdjacentCells(row + 1, col - 1);
        uncoverAdjacentCells(row + 1, col);
        uncoverAdjacentCells(row + 1, col + 1);
    }

    private int countNumberOfAdjacentMine(int row, int col) {
        return (int) adjacentCells(row, col).stream()
                .filter(Cell::isAMine)
                .count();
    }

    private boolean isAdjacentCellsContainAMine(int row, int col) {
        return adjacentCells(row, col).stream()
                .anyMatch(Cell::isAMine);
    }

    private List<Cell> adjacentCells(int row, int col) {
        List<Cell> adjacentCells = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isCellOnTheEdge(row + i, col + j)) {
                    continue;
                }
                adjacentCells.add(cellAt(row + i, col + j));
            }
        }
        return adjacentCells;
    }

    private boolean isCellOnTheEdge(int row, int col) {
        return col < 0
                || row < 0
                || col >= columnsLength()
                || row >= rowsLength();
    }

    private Stream<Cell> allMines() {
        return allCells()
                .filter(Cell::isAMine);
    }
}
