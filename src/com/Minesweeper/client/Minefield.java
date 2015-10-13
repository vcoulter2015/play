package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;
import java.util.Random;

/**
 * Created by vcoulter
 * Date: early October 2015
 */
public class Minefield {

    private char[][] gameboard;
    // Various GUI components depend on the size being 10, so that's not
    // completely arbitrary.
    private final int SIZE = 10;  // the length of one dimension
    private int mineCount;
    private int remainingSpaces;

    public Minefield() {

        gameboard = new char[SIZE][SIZE];

        // Generate the minefield.
        // First pass: Place the mines on the minefield.
        placeMines();
        // Second pass: figure out the # of mines adjacent to every space.
        setFieldCounts();

        // Initialize
        remainingSpaces = SIZE * SIZE;
    }

    /**
     * placeMines determines where the mines will be and how many of them there will be.
     * It acts on the class-level variable gameboard, whose contents will be
     * overwritten.
     */
    private void placeMines() {

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
        // In case of testing:
        // difficultyLevel = 90;

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
    private void setFieldCounts() {

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

    public char[][] getGameboard() {
        return gameboard;
    }

    public char getSpaceValue(int i, int j) {
        return gameboard[i][j];
    }

    public int getMineCount() {
        return mineCount;
    }

    public int getRemainingSpaces() {
        return remainingSpaces;
    }

    /**
     * This takes the place of a set method since, due to the nature of the game,
     * decrement is all remainingSpaces does.
     */
    public void decrementRemainingSpaces() {
        remainingSpaces--;
    }

    /**
     * Returns true if the # of remaining spaces has reached the # of mines.
     *  Note that this assumes the caller has used decrementRemainingSpaces correctly.
     *  If the caller has a bug, then this will reflect that.
     * @return - boolean - false if there are more remaining spots than mines.
     */
    public boolean isSweepFinished() {
        // Sanity check: this will return true if, somehow, remainingSpaces has been
        // decremented past the count of mines. (Cats have 9 lives, after all ...)
        return (mineCount >= remainingSpaces);
    }
}
