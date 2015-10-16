package com.Minesweeper.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;

/**
 * Created by vcoulter in Oct 2015.
 * When testing the server code in the console terminal, when the server's running,
 *  check `ids status Proxy` to make sure the proxy is running to manage port 5700,
 *  and run the server, and then run commands like (-v is optional)
 *  echo '{"name":"Bob"}' |  http POST :5700/:eyedocmd/Minesweeper/getscore -v
 */
public class DbAccessClickHandler implements ClickHandler {

    // overloaded version
    public static String serverAction(String playerName, ServerAction action) {
        return serverAction(playerName, action, 0);
    }

    public static String serverAction(String playerName, ServerAction action, int score) {

        JSONObject dataContainer = new JSONObject();
        JSONString jsonName = new JSONString(playerName);
        JSONString jsonScore = new JSONString(Integer.toString(score));
        StringBuilder postUri = new
             StringBuilder("http://localhost:5700/:eyedocmd/Minesweeper/");
        // (See comment below about the username.)

        dataContainer.put("name", jsonName);
        switch (action) {
            case Win: dataContainer.put("score", jsonScore);
                postUri.append("win");
                break;
            case Loss: postUri.append("loss");
                break;
            case Draw: postUri.append("draw");
                break;
            case GetScore: postUri.append("getscore");
                break;
            // If the action isn't win, loss, draw, or get the score, then add a "hello" for testing.
            // "hello" does not access the database, just the service.
            default:
                postUri.append("hello");
        }

        GWT.log("serverAction data: " + dataContainer.toString());

        // JsonpRequestBuilder jsonprb = new JsonpRequestBuilder(); // but rb seems to do what I want
        RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, postUri.toString());
        /* N.B. Leaving the username out of the URI & then calling setUser down here did NOT work.
         * (However, I didn't try telling the rb to set a blank password or telling the rb
         * to setIncludeCredentials(true) -- perhaps that also would have made it work.)
         * rb.setUser("eyedocmd"); */
        rb.setHeader("content-type", "application/json");
        // rb.setRequestData(dataContainer.toString()); // This is also an argument to sendRequest.
        // TODO: figure out:
        // - How do I tell request to post what's in dataContainer?
        // - What other headers like the type of incoming data need set?

        GWT.log("To " + postUri + " - " + dataContainer);
        try {
            // TODO - if our action is getscore, the callback must be handled differently.
            Request req = rb.sendRequest(dataContainer.toString(), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    GWT.log("RequestCallback received response: " + response.getStatusCode()
                                    + " " + response.getStatusText());
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    GWT.log("RequestCallback got an error: " + exception);
                }
            });

            return req.toString();
        } catch (RequestException e) {
            GWT.log("Error on sendRequest: " + e);
        }
        // If we're here, then the return req.toString() didn't happen.
        return "Exception thrown on sending request; see GWT.log.";
    }

    public void onClick(ClickEvent event) {

    }
}
