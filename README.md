# play
Purpose: Practice using Git and GitHub.
Began as practice repository & became the repo for Minesweeper client.
Tested manual integration (i.e., not using IntelliJ) too.

# To run minesweeper (in development):
1. Make sure mysql is running.
2. Make sure the ids Proxy is running with `ids status Proxy`. If it's stopped, say `ids run Proxy`.
3. In the web service subdirectory, say `j8` then `./mk run`. (The former is a bash macro that gets that session onto JDK 1.8.)
4. In IntelliJ, run the client-side Minesweeper project (in this repo). Ignore what it says is the startup URL.
5. In a terminal window that's in the same directory as the html file, type `serve`.
6. Using MultiFirefox, start Firefox 24esr. Go to http://127.0.0.1:3000/MinesweeperVC.html?gwt.codesvr=127.0.0.1:9997 (Note that the 3000 is the same port that `serve` reported after it started running.)
7. Hint: don't type in your name until the game has successfully started (without blowing up on the first square).


