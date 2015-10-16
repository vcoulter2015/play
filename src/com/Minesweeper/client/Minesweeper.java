package com.Minesweeper.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Created by vcoulter on 10/8/15 as a training project for Google Web Toolkit.
 * Game is 10x10 -- that's hardcoded.
 * Another dependency: the gameboard works by tabindex (so that had better not change).
 */
public class Minesweeper implements EntryPoint {

    private VerticalPanel mainPanel = new VerticalPanel();
    private HorizontalPanel namePanel = new HorizontalPanel();
    private HorizontalPanel buttonPanel = new HorizontalPanel();
    private Grid scoreDisplayGrid = new Grid(2, 4);
    private Label usernameLabel = new Label("Enter your name: ");
    private TextBox usernameTextBox = new TextBox();
    private Label outcomeDisplay = new Label();
    private Grid minefieldGrid = new Grid(10, 10);
    private Button revealButton = new Button("Reveal");
    private Button restartButton = new Button("Restart");
    private Button scoreButton = new Button("View Scores");
    private RevealClickHandler revealClickHandler = new RevealClickHandler();
    private ScoreClickHandler scoreClickHandler = new ScoreClickHandler();

    // This is the entry point of the class.
    public void onModuleLoad() {

        // Set up the stuff above the gameboard.
        namePanel.add(usernameLabel);
        namePanel.add(usernameTextBox);

        // Create the object holding the array that's the actual minefield.
        Minefield minefield = new Minefield();

        // Build the gameboard.
        buildGrid(minefield);

        // Fix up the buttons & their horizontal panel.

        // Tab index is how we tell what buttons are clicked.
        // The cells in the 10x10 grid are tab indices 0-99,
        // so the tab index of the reveal button is the square of the dimension of the grid.
        revealButton.setTabIndex(100);
        RevealClickHandler.setMinefield(minefield);
        // Give the click handler a reference to other UI objects,
        // namely the grid and the textbox.
        RevealClickHandler.setOutcomeLabel(outcomeDisplay);
        RevealClickHandler.setMinefieldGrid(minefieldGrid);
        RevealClickHandler.setNameTextBox(usernameTextBox);
        revealButton.addClickHandler(revealClickHandler);

        // Click handler for Restart button using minefieldGrid & buildGrid().
        // BTW this is an example of an anonymous click handler.
        restartButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (!outcomeDisplay.getText().equals("")) {
                    // Clear any outcome.
                    outcomeDisplay.setText("");
                } else {  // the player clicked restart before the game was over
                    String playerName = RevealClickHandler.getNameFmt();
                    // Record that this was a draw.
                    // GWT.log(playerName + " clicked Restart before game over");
                    if (!playerName.equals("")) {
                        GWT.log("Restart button recording a draw.");
                        DbAccessHandler.serverRequest(playerName, ScoreAction.Draw);
                        ScoreClickHandler.updateScore(ScoreAction.Draw);
                    } // end if have a player name
                } // end else player clicked restart before game over

                // Generate another minefield.
                Minefield newMinefield = new Minefield();
                // Update the ? buttons with the new minefield.
                buildGrid(newMinefield);
                // Also need to tell the other button's click handler about the new minefield.
                RevealClickHandler.setMinefield(newMinefield);
            } // end onClick event
        });

        // Set up click handler for score button.
        ScoreClickHandler.setScoreDisplayGrid(scoreDisplayGrid);
        scoreButton.addClickHandler(scoreClickHandler);

        buttonPanel.add(revealButton);
        buttonPanel.add(restartButton);
        buttonPanel.add(scoreButton);

        // Set up the scores display.
        scoreDisplayGrid.setText(0, 0, "Wins");
        scoreDisplayGrid.setText(0, 1, "Losses");
        scoreDisplayGrid.setText(0, 2, "Draws");
        scoreDisplayGrid.setText(0, 3, "Score");
        scoreDisplayGrid.getRowFormatter().addStyleName(0, "scoreheader");
        // Don't display the scores until the user asks for them.
        scoreDisplayGrid.setVisible(false);

        // Fix up the main panel.
        mainPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

        mainPanel.add(namePanel);
        mainPanel.add(outcomeDisplay);
        mainPanel.add(minefieldGrid);
        mainPanel.add(buttonPanel);
        mainPanel.add(scoreDisplayGrid);

        // Associate the Main panel with the HTML host page.
        RootPanel.get("gameboard").add(mainPanel);

        // Give the name textbox the focus / cursor
        usernameTextBox.setFocus(true);

    }  // end onModuleLoad()

    /**
     * Assumptions:
     *  - Called when minefieldGrid already exists as a 10x10 grid (and yes, that size is hardcoded).
     */
    private void buildGrid(Minefield minefield) {

        // Delete the current contents of the grid.
        minefieldGrid.clear();

        for (int i=0; i < 10; i++)
            for (int j=0; j < 10; j++) {
                Button boardSpace = new Button("?");
                RevealClickHandler eClickHandler = new RevealClickHandler();
                // Now that the minefield is a class-level variable, the caller can set it.
                // eClickHandler.setMinefield(minefield);
                boardSpace.setTabIndex(i * 10 + j);
                boardSpace.addClickHandler(eClickHandler);
                minefieldGrid.setWidget(i, j, boardSpace);
            } // end for
    }
}
