package com.Minesweeper.client;

/**
 * Created by vcoulter on 10/8/15.
 * Class probably ought to be protected.
 */
public class MineGenerator {

    /**
     *
     * @return - a 10x10 array of characters representing the gameboard.
     *  'X' indicates a mine, otherwise the character is a number 0-8 indicating
     *  the # of X's adjacent to that spot in the gameboard.
     */
    public static char[][] generate() {

        // char[][] gameboard = new char[10][10];
        // Do this the really simple and boring way:
        char[][] gameboard = {
                {'0', '1', '1', '1', '0', '0', '1', 'X', '1', '0'},
                {'0', '1', 'X', '2', '2', '2', '2', '1', '1', '0'},
                {'0', '2', '2', '4', 'X', 'X', '1', '1', '2', '2'},
                {'0', '2', 'X', '4', 'X', '3', '1', '2', 'X', 'X'},
                {'1', '3', 'X', '4', '2', '2', '0', '2', 'X', '3'},
                {'1', 'X', '2', '3', 'X', '3', '1', '2', '1', '1'},
                {'3', '3', '2', '2', 'X', '4', 'X', '2', '0', '0'},
                {'X', 'X', '2', '2', '2', '3', 'X', '2', '0', '0'},
                {'2', '2', '2', 'X', '1', '1', '2', '2', '2', '1'},
                {'0', '0', '1', '1', '1', '0', '1', 'X', '2', 'X'}
        };
        return gameboard;
    }
}
