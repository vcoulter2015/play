package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Grid;

/**
 * Created by vcoulter in Oct 2015.
 */
public class ScoreClickHandler implements ClickHandler {

    private static Grid scoreDisplayGrid = null;

    /**
     * IMPORTANT: this must be called at setup so we know where to display the scores.
     * @param scoreGrid
     */
    public static void setScoreDisplayGrid(Grid scoreGrid) {
        scoreDisplayGrid = scoreGrid;
    }

    /**
     * Handles button click that queries the database for the scores.
     * @param event
     */
    public void onClick(ClickEvent event) {

        String playerName = RevealClickHandler.getNameFmt();
        if (playerName.equals(""))
            return;
        // If the player name is blank, do nothing.
        // If we're still here, send a request to the database.
        // GWT.log("Request for scores for '" + playerName + "'");

        DbAccessHandler.serverRequest(playerName, ScoreAction.GetScore);

        // The serverRequest call happens asynchronously & will call displayScores when it gets back.
    }

    /**
     * Purpose: displays the scores. Called by DbAccessHandler.serverRequest RequestCallback.
     * @param data - a string with a JSON object like:
     *   {"name":"playername","wins":a,"losses":b,"draws":c,"score":d}
     *   where a,b,c, & d are all integer #s.
     *   (If there was no such player in the database, these will all be 0's.)
     */
    public static void displayScores(String data) {

        JSONObject scoreData = JSONParser.parseStrict(data).isObject();

        if (scoreData != null) {

            // Columns of the score grid in order: wins, losses, draws, and score.
            scoreDisplayGrid.setText(1, 0, scoreData.get("wins").toString());
            scoreDisplayGrid.setText(1, 1, scoreData.get("losses").toString());
            scoreDisplayGrid.setText(1, 2, scoreData.get("draws").toString());
            scoreDisplayGrid.setText(1, 3, scoreData.get("score").toString());

            scoreDisplayGrid.setVisible(true);
        } else {
            GWT.log("Could not parse '" + data + "'");
        }

    }

    // Overload.
    public static void updateScore(ScoreAction outcome) {
        updateScore(outcome, 0);
    }

    /**
     * Only does anything if the player name is filled in and if the scores are visible.
     * @param outcome - win, loss, or draw (which column(s) to update)
     * @param score - optional - if a win, the score.
     */
    public static void updateScore(ScoreAction outcome, int score) {

        if (!RevealClickHandler.doClientUpdateScores ||
                    !scoreDisplayGrid.isVisible() ||
                    RevealClickHandler.getNameFmt().equals(""))
            return;

        String oldCount = "";
        String oldScore;
        int newCount;
        int newScore = 0;

        // Columns of the score grid in order: wins, losses, draws, and score.
        switch(outcome) {
            case Loss:
                oldCount = scoreDisplayGrid.getText(1, 1);
                break;
            case Win:
                oldCount = scoreDisplayGrid.getText(1, 0);
                oldScore = scoreDisplayGrid.getText(1, 3);
                newScore = Integer.parseInt(oldScore) + score;
                break;
            case Draw:
                oldCount = scoreDisplayGrid.getText(1, 2);
        } // end switch

        newCount = Integer.parseInt(oldCount) + 1;

        switch(outcome) {
            case Loss:
                scoreDisplayGrid.setText(1, 1, Integer.toString(newCount));
                break;
            case Win:
                scoreDisplayGrid.setText(1, 0, Integer.toString(newCount));
                scoreDisplayGrid.setText(1, 3, Integer.toString(newScore));
                break;
            case Draw:
                scoreDisplayGrid.setText(1, 2, Integer.toString(newCount));
        } // end switch

    }
}
