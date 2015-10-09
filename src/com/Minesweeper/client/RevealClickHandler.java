package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by vcoulter on 10/8/15.
 * Called "RevealClickHandler" since it's called by buttons that show the board --
 *  either the single-cell buttons or the Reveal button that ends the game.
 * Sample code from http://www.gwtproject.org/doc/latest/DevGuideUiHandlers.html
 *  added "extends Composite" in the declaration, but without indicating
 *  what package "Composite" was from.
 */
public class RevealClickHandler implements ClickHandler {

    private char[][] minefield = null;

    public void setMinefield(char[][] board) {
        minefield = board;
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

            GWT.log("Cell at row " + row + " column " + col + " clicked.");

            // Ask the game what's at that square.
            currentSpace = minefield[row][col];

            // Remove the clicked button.
            gridParent.clearCell(row, col);

            // If it's not a mine, then display the # instead of the button.
            if ('X' != currentSpace) {
                // Replace it with the # & color it.
                gridParent.setHTML(row, col, "<p class='adj" + currentSpace + "'>" + currentSpace + "</p>");

            } // end if not a mine

            // If it IS a mine, game over!
            else {
                GWT.log("Hit a mine!!");
                gridParent.setHTML(row, col, "<p class='exploded'>@</p>");
                // Do the same thing Reveal does at this point.
                reveal(gridParent);
            }
        } else {

            GWT.log("Reveal button");
            // Get a pointer to the grid that's older sibling of our parent VerticalPanel.
            ComplexPanel parentPanel = (ComplexPanel) sender.getParent();
            Widget oldestSibling = parentPanel.getWidget(0);
            // Assuming the grid IS the oldest sibling ...
            reveal( (Grid) oldestSibling );

        }  // end if Reveal button

    }  // end onClick

    private void reveal(Grid gameboard) {

        for (int i=0; i < 10; i++)
            for (int j=0; j < 10; j++) {

                if (gameboard.getHTML(i, j).indexOf("button") > -1) {

                    gameboard.clearCell(i, j);

                    gameboard.setHTML(i, j, "<p class='adj" + minefield[i][j] + " untouched'>" +
                            minefield[i][j] + "</p>");

                }  // end if
            } // end for
    } // end reveal

}
