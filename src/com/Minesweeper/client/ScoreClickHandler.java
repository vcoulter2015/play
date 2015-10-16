package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
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
        GWT.log("Request for scores for '" + playerName + "'");

        String serverResults = DbAccessHandler.serverRequest(playerName, ServerAction.GetScore);
        GWT.log("Received from server: '" + serverResults + "'");

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
}
