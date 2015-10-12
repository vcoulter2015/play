package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;

import java.util.Random;

/**
 * Created by vcoulter on 10/8/15
 * to be part of the Minesweeper project for learning Google Web Toolkit.
 */
public class MineGenerator {

    private static char[][] gameboard;
    private static int mineCount;

    /**
     *
     * @return - a 10x10 array of characters representing the gameboard.
     *  'X' indicates a mine, otherwise the character is a number 0-8 indicating
     *  the # of X's adjacent to that spot in the gameboard.
     */
    public static char[][] generate() {

        gameboard = new char[10][10];

        // First pass: Place the mines on the minefield.
        placeMines();

        // Second pass: figure out the # of mines adjacent to every space.
        setFieldCounts();

        // If I want to do this the really simple and boring way:
        /*
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
        }; */
        return gameboard;
    }

    /**
     * placeMines determines where the mines will be and how many of them there will be.
     * It acts on the class-level variable gameboard, whose contents will be
     * overwritten.
     */
    private static void placeMines() {

        Random rnd = new Random();
        int rndNumber = 0;
        int difficultyLevel = 0;

        // Set the difficulty level, that is, the # to compare mod 100 to.
        /* BTW, modulus, like regular division, keeps the sign according to the operands,
            so its result is negative half the time unless I use absolute value.
         *
         * The smaller difficultyLevel is, the more difficult (higher % mines) the field is.
            A 72 can be pretty difficult in that it's hard to get started.
            65 and sometimes 70 can be too challenging.
            An 82 can be interesting without being too easy (tho' sometimes is too easy).
            87 (or larger) is too easy.
         */
        difficultyLevel = 71 + Math.abs(rnd.nextInt()) % 9;

        mineCount = 0;

        for (int i = 0; i < gameboard.length; i++)
            for (int j = 0; j < gameboard[i].length; j++) {
                rndNumber = Math.abs(rnd.nextInt());
                // GWT.log("nextInt: " + rndNumber);
                // This condition is the key to controlling how difficult the minefield is.
                if (rndNumber % 100 >= difficultyLevel) {
                    gameboard[i][j] = 'X';
                    mineCount++;
                } else
                    gameboard[i][j] = '0';
            } // end for
        GWT.log("Difficulty level: " + difficultyLevel + "; # of mines: " + mineCount);
    } // end placeMines

    /**
     * setFieldCounts goes thru the gameboard (the class-level variable) and
     * stores how many mines are adjacent to each space that isn't a mine.
     * ASSUMPTION: when this is called, the gameboard should only contain two
     * characters: '0' and 'X'. If this is incorrect, then the counts will be incorrect.
     */
    private static void setFieldCounts() {

        // Loop part 1: Go thru every space in the minefield.
        for (int i = 0; i < gameboard.length; i++)
            for (int j = 0; j < gameboard[i].length; j++) {

                if ('X' == gameboard[i][j]) {

                    // Loop part 2: Increment spaces adjacent to mines.
                    for (int y = i - 1; y <= i + 1; y++) {

                        // Does this square really exist?
                        if (y < 0 || y >= gameboard.length)
                            continue;

                        for (int x = j - 1; x <= j + 1; x++) {

                            // Does this square really exist?
                            if (x < 0 || x >= gameboard[i].length)
                                continue;

                            if (gameboard[y][x] != 'X')
                                gameboard[y][x]++;

                        } // end innermost for
                    } // end for x
                } // end if current square is a mine
            } // end loop thru every space
    } // end setFieldCounts
}
