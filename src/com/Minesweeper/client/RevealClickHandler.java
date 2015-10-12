package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by vcoulter in early October 2015.
 * Called "RevealClickHandler" since it's called by buttons that show the board --
 *  either the single-cell buttons or the Reveal button that ends the game.
 * Sample code from http://www.gwtproject.org/doc/latest/DevGuideUiHandlers.html
 *  added "extends Composite" in the declaration.
 */
public class RevealClickHandler implements ClickHandler {

    private Minefield minefield = null;
    private Grid associatedGrid = null;

    public void setMinefield(Minefield board) {
        minefield = board;
    }

    /**
     * setAssociatedGrid is only necessary for the Reveal button;
     * the ? buttons just get their parent.
     */
    public void setAssociatedGrid(Grid grid) {
        associatedGrid = grid;
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

        GWT.log("RevealClickHandler onclick, tabIndex = " + myTabIndex);

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
                    GWT.log("Game has been won!");
                }  // end if win

            } // end if not a mine

            // If it IS a mine, game over!
            else {
                GWT.log("Hit a mine!!");
                gridParent.setHTML(row, col, "<p class='exploded'>@</p>");
                // Do the same thing Reveal does at this point.
                reveal(gridParent);
            }
        } else {

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
