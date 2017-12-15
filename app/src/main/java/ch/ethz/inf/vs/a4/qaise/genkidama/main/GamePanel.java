package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Random;

import ch.ethz.inf.vs.a4.qaise.genkidama.engine.GameEngine;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.Network;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.GameOverScene;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.GamePlayScene;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.SceneManager;

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

    private static HashMap<Integer, Integer> voteResults = new HashMap<>();
    // Voting on replay works the following:
    // 1 is rematch
    // 2 is no rematch

    public static int PLAYERCOUNT = 2;

    public static Player myPlayer () {
        if (Constants.ID == 999) return null;
        return players.get(Constants.ID);
    }
    /* ----------------------------------------------------------------------------------------- */


    /* ----------------------------------------------------------------------------------------- */
    /***** FUNCTIONS CALLED ONLY FROM SERVER *****/

    public static void addPlayer (Player player) {
        players.put(player.id, player);

        // TODO: test this, should be done always once
        if (player.id == Constants.ID) { // if my palyer is added, initialize the new_point in GamePlayerScene
            GamePlayScene.setNew_point(player.new_point);
            Log.i(TAG, "MYPLAYER with ID " + player.id + " is added to the game");
        }

//        if (GamePanel.manager.ACTIVE_SCENE == Constants.CREATE_GAME_SCENE) {
//            CreateGameScene.updateTextView(player.id, player.name);
//            try {
//                manager.getCurrentScene().upda
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
    }

    public static void attackPlayer(Network.Attack msg) {
        Player victim = players.get(msg.idE);    // the attacked player
        Player attacker = players.get(msg.idA);   // the attacking player
        int dmg = msg.damage;
        int health;

        //TODO: here music for attack
//        MediaPlayer attacksoundmusic= MediaPlayer.create(MainActivity.context, R.raw.attacksound);
//        attacksoundmusic.start();


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
            else {
                victim.setCurrentHealth(0);
                victim.isLoser = true;
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
            else {
                victim.setCurrentHealth(0);
                victim.isLoser = true;
            }
            attacker.chargebar.update();
            victim.healthbar.update();
        }
    }

    public static void removePlayer (int id) {
        players.remove(id);
    }

    public static void rematch(Network.PlayAgain msg){ //TODO fix bugs
        int id = msg.id;
        int ans = msg.answer;
        if (!voteResults.containsKey(id)) { //only add vote if client hasn't voted yet.
            voteResults.put(id, ans);
            int cnt = 0;
            for (Player player : players.values()) {
                if (voteResults.containsKey(player.id)) {
                    int res = voteResults.get(player.id);
                    if (res == 2) {
                        //TODO disconnect from Server
                        GameOverScene.setNextScene(Constants.START_SCENE);
                        GameOverScene.termination = true;
                        voteResults = new HashMap<>();
                        break;
                    } else if (res == 1) {
                        cnt++;
                        if (cnt == PLAYERCOUNT) {
                            GameOverScene.setNextScene(Constants.GAMEPLAY_SCENE);
                            GameOverScene.termination = true;
                            voteResults = new HashMap<>();
                        }
                    }
                }
            }
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
