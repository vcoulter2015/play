package com.Minesweeper.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Created by vcoulter on 10/8/15 as a training project for Google Web Toolkit.
 * Game is 10x10 -- that's hardcoded.
 * Another dependency: the gameboard works by tabindex (so that had better not change)
 * and the grid & buttons relate to each other knowing that children of the VerticalPanel are,
 * in this order: Grid, Reveal button, Restart Button - so sometimes they relate to each other
 * as child 0, child 1, etc.
 * Has yet to keep track of whether the game is over (i.e., when user reveals every safe square).
 */
public class Minesweeper implements EntryPoint {

    private VerticalPanel mainPanel = new VerticalPanel();
    private Grid minefieldGrid = new Grid(10, 10);
    private Button revealButton = new Button("Reveal");
    private Button restartButton = new Button("Restart");
    private RevealClickHandler revealClickHandler = new RevealClickHandler();

    // This is the entry point of the class.
    public void onModuleLoad() {

        // Generate the array that's the actual minefield.
        // Possible enhancement: Make a minefield object that wraps the 2D char array.
        char[][] minefield = MineGenerator.generate();


        // Build the gameboard.
        buildGrid(minefield);

        // Tab index is how we tell what buttons are clicked.
        // The cells in the 10x10 grid are tab indices 0-99,
        // so the tab index of the reveal button is the square of the dimension of the grid.
        revealButton.setTabIndex(100);
        revealClickHandler.setMinefield(minefield);
        revealButton.addClickHandler(revealClickHandler);

        // Click handler for Restart button using minefieldGrid & buildGrid().
        // BTW this is an example of an anonymous click handler.
        restartButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // Generate another minefield.
                char[][] newMinefield = MineGenerator.generate();
                // Update the ? buttons with the new minefield.
                buildGrid(newMinefield);
                // Also need to tell the Reveal button's click handler about the new minefield.
                revealClickHandler.setMinefield(newMinefield);
            } // end onClick event
        });

        mainPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

        mainPanel.add(minefieldGrid);
        mainPanel.add(revealButton);
        mainPanel.add(restartButton);

        // Associate the Main panel with the HTML host page.
        RootPanel.get("gameboard").add(mainPanel);

    }  // end onModuleLoad()

    /**
     * Assumptions:
     *  - Called when minefieldGrid already exists as a 10x10 grid (and yes, that size is hardcoded).
     */
    private void buildGrid(char[][] minefield) {

        // Delete the current contents of the grid.
        minefieldGrid.clear();

        for (int i=0; i < 10; i++)
            for (int j=0; j < 10; j++) {
                Button boardSpace = new Button("?");
                RevealClickHandler eClickHandler = new RevealClickHandler();
                eClickHandler.setMinefield(minefield);
                boardSpace.setTabIndex(i * 10 + j);
                boardSpace.addClickHandler(eClickHandler);
                minefieldGrid.setWidget(i, j, boardSpace);
            } // end for
    }
}
