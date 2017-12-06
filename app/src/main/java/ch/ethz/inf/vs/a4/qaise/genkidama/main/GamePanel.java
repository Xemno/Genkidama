package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Random;

import ch.ethz.inf.vs.a4.qaise.genkidama.engine.GameEngine;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.Network;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.SceneManager;


/**
 * This is the main entry of our game.
 * Here we update and draw every scene that we might want to
 * show on the screen.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "#GamePanel#";


    private GameEngine thread;

    private SceneManager manager;

    /* TODO: instantiate player here, add it to hashmap and access it then from every scene
     *  So we can bind our network in this class and do not have to worry to send packets in
     *  all scenes.
     */

    public GamePanel(Context context, Activity activity) {
        super(context);

        getHolder().addCallback(this);

        thread = new GameEngine(getHolder(), this);

        manager = new SceneManager(activity);

        // TODO: initialize network
//        new KryoClient();

        setFocusable(true);
    }

    @Override
    //This is called immediately after the surface is first created.
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // as soon as the surface is created do the following
        thread = new GameEngine(getHolder(), this); // start game engine
        thread.setRunning(true);
        thread.start();
    }

    @Override
    //This is called immediately after any structural changes (format or size) have been made to the surface.
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        // leave empty
    }

    @Override
    //This is called immediately before a surface is being destroyed.
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while(retry) {      // Might not be a good solution to do this!
            try {  // try to finish that thread
                thread.setRunning(false);
                thread.join();  // wait for thread to be finished
            } catch (Exception e) {e.printStackTrace();}
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {  // Manages our touch inputs
        manager.receiveTouch(event); // any touch event should be received by every scene
        return true; // detect any touch
    }

    public void update() { // updates our game frame by frame
        // gets called from MainThread, so everything we need to update comes here
        manager.update();
    }

    @Override
    public void draw(Canvas canvas) { // draws everything in our game
        super.draw(canvas);

        manager.draw(canvas);
    }


    /* **********************************
     *                                  *
     *   Following is for the NETWORK.  *
     *                                  *
     *                                  *
     ********************************** */

    /* ----------------------------------------------------------------------------------------- */
    /***** SETUP *****/
    public static HashMap<Integer, Player> players = new HashMap();

    public static Player myPlayer () {
        if (Constants.ID == 999) return null;
        return players.get(Constants.ID);
    }
    /* ----------------------------------------------------------------------------------------- */


    /* ----------------------------------------------------------------------------------------- */
    /***** FUNCTIONS CALLED ONLY FROM SERVER *****/

    public static void addPlayer (Player player) {
        players.put(player.id, player);
        Log.i(TAG, "Players: EntrySet -> " + players.entrySet());
    }

    public static void updatePlayer(Network.UpdatePlayer msg) {
        /* enemy players will be updated through the network here */
        Player player = players.get(msg.id);
//        if (player == null) return;
        player.update(new Point(msg.x, msg.y));
    }

    public static void attackPlayer(Network.Attack msg) {
        Player attackedPlayer = players.get(msg.idE);    // the attacked player
        Player attackingPlayer = players.get(msg.idA);   // the attacking player

        // TODO: what to do when player gets attacked
        // TODO: update health of attackedPlayer according to damage taken
        // TODO: update charge of attackingPlayer according to damage dealt

    }

    public static void removePlayer (int id) {
        players.remove(id);
        if (players.containsKey(id)) {
            Log.i(TAG, "ERROR: failed to remove player");
        }
    }
    /* ----------------------------------------------------------------------------------------- */


    /* **********************************
     *                                  *
     *   Arbitrary Functions.           *
     *                                  *
     *                                  *
     ********************************** */
    /* ----------------------------------------------------------------------------------------- */
    //This gives you a random number in between low (inclusive) and high (exclusive)
    public static int getRandom(int low, int high) {
        Random r = new Random();
        return r.nextInt(high - low) + low;
    }

    /* ----------------------------------------------------------------------------------------- */

}
