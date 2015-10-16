package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Created by vcoulter in early October 2015.
 * Called "RevealClickHandler" since it's called by buttons that show the board --
 *  either the single-cell buttons or the Reveal button that ends the game.
 * Sample code from http://www.gwtproject.org/doc/latest/DevGuideUiHandlers.html
 *  added "extends Composite" in the declaration.
 */
public class RevealClickHandler implements ClickHandler {

    private static Minefield minefield = null;
    private static Grid associatedGrid = null;
    private static TextBox nameTextBox = null;
    private static Label outcomeLabel = null;

    public static void setMinefield (Minefield board) {
        minefield = board;
    }

    public static void setOutcomeLabel(Label outcomeDisplay) {
        outcomeLabel = outcomeDisplay;
    }

    public static void setNameTextBox(TextBox nameTextBox) {
        RevealClickHandler.nameTextBox = nameTextBox;
    }

    /**
     * setAssociatedGrid is only necessary for the Reveal button;
     * the ? buttons just get their parent.
     */
    public static void setAssociatedGrid(Grid grid) {
        associatedGrid = grid;
    }

    /**
     * Purpose: Checks if the character is allowed.
     * @param c - the character in question
     * @return - if c is an allowed character, return it
     *  (for a StringBuilder's convenience), otherwise return "".
     */
    private static String returnSanitaryValue(char c) {
        if (c == '.' || c == ',' || c == ' ')
            return Character.toString(c);
        else if (c >= 48 && c <= 57)    // 0-9 ok
            return Character.toString(c);
        else if (c >= 65 && c <= 90)    // A-Z ok
            return Character.toString(c);
        else if (c >= 97 && c <= 122)   // a-z ok
            return Character.toString(c);
        else
            return "";
    }

    /**
     * getNameFmt looks at the contents of the username textbox and sanitizes it.
     *   Then it returns its value in a case-insensitive, trimmed fashion.
     * (Side effect: it sets the contents of the username textbox to the result in order to give the user feedback.)
     *  It's static because other pieces of the game need it.
     * Assumptions: if no one first called setNameTextBox, an exception will be thrown.
     * @return - a string without leading or trailing spaces in which the first character
     *  is upper case and any following characters are lower case, or "" if there was
     *  no username entered.
     */
    public static String getNameFmt() {

        String nameTrim = nameTextBox.getText().trim();
        // What is the diff between the getValue() and getText() methods?
        String resultName = "";

        // Check on the length of what the user typed in:
        if (nameTrim.length() == 0)
            resultName = "";
        else {
            StringBuilder sanitaryName = new StringBuilder();

            for (int i = 0; i < nameTrim.length(); i++) {
                sanitaryName.append(returnSanitaryValue(nameTrim.charAt(i)));
            } // end for
            // Was anything left after that?
            if (sanitaryName.toString().equals(""))
                resultName = "";
            else {
                String intermediateResultName;
                // Make sure the name isn't longer than 35 characters (database constraint).
                // and redo trim since the sanitizing and substring may have left trailing spaces.
                if (sanitaryName.length() >= 35)
                    intermediateResultName = sanitaryName.substring(0, 35).trim();
                else
                    intermediateResultName = sanitaryName.toString().trim();

                if (intermediateResultName.length() <= 1)
                    resultName = intermediateResultName.toUpperCase();
                else
                    resultName = intermediateResultName.substring(0, 1).toUpperCase() +
                                         intermediateResultName.substring(1).toLowerCase();

            }

        } // end else name isn't blank
        nameTextBox.setText(resultName);
        return resultName;
    }

    /**
     *
     * @param event - reference to the button.
     * Which button is clicked is distinguished by the tabIndex of the button.
     */
    public void onClick(ClickEvent event) {
        // Trust that the code using this only attached it to the right sort of button.
        Button sender = (Button) event.getSource();
        int myTabIndex = sender.getTabIndex();
        String playerName = "";

        // GWT.log("RevealClickHandler onclick, tabIndex = " + myTabIndex);

        // Did a ? button in a cell get pressed or the Reveal button?
        if (myTabIndex < 100 && myTabIndex > -1) {

            Grid gridParent = (Grid) sender.getParent();
            int row = -1;
            int col = -1;
            char currentSpace;

            // Parse the tabIndex into row & column.
            col = myTabIndex % 10;
            row = (myTabIndex - col) / 10;

            // GWT.log("Cell at row " + row + " column " + col + " clicked.");

            // Ask the object what's at that square.
            currentSpace = minefield.getSpaceValue(row, col);

            // Remove the clicked button.
            gridParent.clearCell(row, col);
            // Register that we clicked another space with the minefield object.
            minefield.decrementRemainingSpaces();

            // If it's not a mine, then display the # instead of the button.
            if ('X' != currentSpace) {
                // Replace it with the # & color it.
                gridParent.setHTML(row, col, "<p class='adj" + currentSpace + "'>" + currentSpace + "</p>");

                // Is the game over?
                if (minefield.isSweepFinished()) {
                    // GWT.log("Game has been won!");
                    playerName = getNameFmt();
                    if (!playerName.equals("")) {
                        outcomeLabel.setText("You won, " + playerName + "! Click Restart for another game.");
                        // Record a win in the server-side database.
                        // GWT.log("Recording a win with score " + minefield.getMineCount());
                        DbAccessHandler.serverRequest(playerName, ServerAction.Win, minefield.getMineCount());
                    } // end if we need to record a win
                    else
                        outcomeLabel.setText("You won! Click Restart for another game.");
                }  // end if win

            } // end if not a mine

            // If it IS a mine, game over!
            else {
                //GWT.log("Hit a mine!!");
                gridParent.setHTML(row, col, "<p class='exploded'>@</p>");
                // Do the same thing Reveal does at this point.
                reveal(gridParent);

                // Give the player feedback and record a loss.
                playerName = getNameFmt();
                if (!playerName.equals("")) {
                    outcomeLabel.setText("You hit a mine, " + playerName + "! Click Restart for another game.");
                    // Record a loss in the server-side database.
                    // GWT.log("Recording a loss.");
                    DbAccessHandler.serverRequest(playerName, ServerAction.Loss);
                } // end if we need to record a loss
                else
                    outcomeLabel.setText("You hit a mine! Click Restart for another game.");

            }
        } else {  // the Reveal button was clicked.

            // N.B. If the user clicks this, either they didn't need to click it at all,
            // or the game is a draw.
            // Check the outcome label to see whether we need to record a draw or not.
            // (The Restart button does the same thing.)
            playerName = getNameFmt();
            if (outcomeLabel.getText().equals("") && !playerName.equals("")) {
                // BTW: if the player clicks Reveal with a blank name, then enters their name,
                // then clicks Reveal again, at that point this'll be recorded as a draw ...
                // but why would a player be doing that anyway?
                outcomeLabel.setText("Keep trying, " + playerName + "! Click Restart for another game.");
                // Record that this was a draw
                // GWT.log("Reveal button recording a draw.");
                DbAccessHandler.serverRequest(playerName, ServerAction.Draw);
            }

            // Get a pointer to the grid that's older sibling of our parent VerticalPanel.
            if (associatedGrid != null) {
                // GWT.log("Reveal button - using associatedGrid reference");
                reveal(associatedGrid);
            } else {
                // TODO - throw an exception or fix this code
                ComplexPanel parentPanel = (ComplexPanel) sender.getParent();
                Widget oldestSibling = parentPanel.getWidget(0);
                // Assuming the grid IS the oldest sibling ... which may not be correct in later versions
                reveal((Grid) oldestSibling);
            }

        }  // end if Reveal button

    }  // end onClick

    private void reveal(Grid gameboard) {

        char minefieldValue;

        for (int i=0; i < 10; i++)
            for (int j=0; j < 10; j++) {

                if (gameboard.getHTML(i, j).indexOf("button") > -1) {

                    gameboard.clearCell(i, j);
                    minefieldValue = minefield.getSpaceValue(i, j);
                    gameboard.setHTML(i, j, "<p class='adj" + minefieldValue + " untouched'>" +
                            minefieldValue + "</p>");

                }  // end if
            } // end for
    } // end reveal

}
