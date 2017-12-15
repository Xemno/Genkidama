package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.engine.GameEngine;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.Network;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.CreateGameScene;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.GamePlayScene;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.SceneManager;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.CHARGE_AMOUNT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.MAX_CHARGE;


/**
 * This is the main entry of our game.
 * Here we update and draw every scene that we might want to
 * show on the screen.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "#GamePanel#";

    private GameEngine thread;
    private static SceneManager manager;

    public static MediaPlayer attacksound, specialattacksound;
    /*
     * This class sets up the game and the server updates the Players from this class
     */

    public GamePanel(Context context, Activity activity) {
        super(context);

        getHolder().addCallback(this);

        thread = new GameEngine(getHolder(), this);

        manager = new SceneManager(activity);


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

        if (player.id == Constants.ID) { // if my palyer is added, initialize the new_point in GamePlayerScene
            GamePlayScene.setNew_point(player.new_point);
            Log.i(TAG, "MYPLAYER " + player.name + " with ID " + player.id + " is added to the game");
        } else
            Log.i(TAG, "PLAYER " + player.name + " with ID " + player.id + " is added to the game");

    }

    public static void updatePlayer(Network.UpdatePlayer msg) {
        /* enemy players will be updated through the network here */
        Player player = players.get(msg.id);
        if (player == null) return;
        float x = msg.x * Constants.SCREEN_WIDTH;
        float y = msg.y * Constants.SCREEN_HEIGHT;
        if (x < 0.f)
            player.update(new PointF(0.f, y));
        else if (x > Constants.SCREEN_WIDTH)
            player.update(new PointF((float) Constants.SCREEN_WIDTH, y));
        else
            player.update(new PointF(x, y));

        Log.i(TAG, "PLAYER " + player.name + " with ID " + player.id + " updated " + "(" + player.new_point.x + ", " + player.new_point.y + ")");

    }

    public static void attackPlayer(Network.Attack msg) {
        Player victim = players.get(msg.idE);    // the attacked player
        Player attacker = players.get(msg.idA);   // the attacking player

        if (victim == null || attacker == null) return; // TODO: test this, newly added

        int dmg = msg.damage;
        int health;

        if (dmg >= 0) {
            // normal attack
            if (attacker.getCurrentCharge() >= MAX_CHARGE - CHARGE_AMOUNT) {
                attacker.setCurrentCharge(MAX_CHARGE);
                attacker.isCharged = true;
            } else {
                attacker.setCurrentCharge(attacker.getCurrentCharge() + CHARGE_AMOUNT);
            }

            health = victim.getCurrentHealth() - dmg;
            if (health > 0)
                victim.setCurrentHealth(health);
            else
                victim.setCurrentHealth(0);

            attacker.attackAnimation();
            victim.blockAnimation();

            if (attacksound == null)  {
                attacksound = MediaPlayer.create(MainActivity.context, R.raw.attacksound);
                attacksound.setLooping(false);
                attacksound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        attacksound.start();
                    }
                });
            } else {

                if (attacksound.isPlaying()) attacksound.pause();
                attacksound.seekTo(0);
                attacksound.start();
            }

            attacker.chargebar.update();
            victim.healthbar.update();}
        else { // special attack
            attacker.setCurrentCharge(0);
            attacker.isCharged = false;
            dmg = -dmg;
            health = victim.getCurrentHealth() - dmg;
            if (health > 0)
                victim.setCurrentHealth(health);
            else
                victim.setCurrentHealth(0);
            attacker.specialAttackAnimation();
            victim.blockAnimation();

            if (specialattacksound == null)  {
                specialattacksound = MediaPlayer.create(MainActivity.context, R.raw.specialattacksound);
                specialattacksound.setLooping(false);
                specialattacksound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        specialattacksound.start();
                    }
                });
            } else {
                if (specialattacksound.isPlaying()) specialattacksound.pause();
                specialattacksound.seekTo(0);
                specialattacksound.start();
            }


            attacker.chargebar.update();
            victim.healthbar.update();
        }
    }

    public static void removePlayer (int id) {
        players.remove(id);
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

    public static boolean isValidString(String str)
    {
        return str.matches("\\w+");
    }

    public static boolean isPort(String str)
    {
        return str.matches("\\d+");
    }

    public static boolean isIP(String str)
    {
        return str.matches("(\\d+\\.\\d+\\.\\d+\\.\\d+){1}");
    }

    /* ----------------------------------------------------------------------------------------- */

}
