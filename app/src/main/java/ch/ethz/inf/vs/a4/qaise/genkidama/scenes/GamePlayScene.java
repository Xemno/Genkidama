package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.CLIENT_CONNECTED;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.FLOOR_CEILING_DIST_RELATIVE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.ID;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.MAX_HEALTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.fixDist;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.side;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.myPlayer;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.players;


/**
 * This is one specific scene. For us this might be the scene
 * where the game starts and the palyers can play.
 */

public class GamePlayScene implements Scene {

    private static final String TAG = "#GamePlayScene#";

    private Activity activity;
    private static PointF new_point;
    private PointF old_point;
    private boolean movingPlayer = false;

    private int nextScene = 0;

    private boolean sendOnce = true;

    private Drawable layer1_5, layer6;

    private boolean btn_active = false;
    private boolean new_game = false;

    private float touch_old, touch_new; // used for movement detection. DO NOT confuse with old_point and new_point


    public GamePlayScene(Activity activity) {
        this.activity = activity;

        fixDist = FLOOR_CEILING_DIST_RELATIVE*SCREEN_HEIGHT/100;

        old_point = new PointF(0,0);
    }

    synchronized public void reset(){
        for (Player player : players.values()){
            player.setCurrentCharge(0);
            player.setCurrentHealth(MAX_HEALTH);
            player.chargebar.update();
            player.healthbar.update();
            player.isLoser = false;
            player.isWinner = false;
            //player.reset = true;
            if (myPlayer().id == player.id) {
                PointF new_point;
                int count = 4;
                if (side % 2 != 0) { // draw on left side
                    new_point = new PointF(side * SCREEN_WIDTH/count, Constants.fixDist);
                } else {  // draw on right side
                    new_point = new PointF(SCREEN_WIDTH - ((side) * SCREEN_WIDTH/count) , Constants.fixDist);
                }
                System.out.println("my pos: " + (new_point.x /SCREEN_WIDTH) + ", " + (new_point.y /SCREEN_HEIGHT ));
                KryoClient.send(new PointF(new_point.x / SCREEN_WIDTH, new_point.y/ SCREEN_HEIGHT));
                System.out.println(new_point.x +", " +new_point.y);
                player.setOld_point(new_point);
                GamePlayScene.setNew_point(new_point);
            }
        }


    }

    @Override
    public void update() {
        if(new_game){
            reset();
            new_game = false;
        }

        // In case something goes wrong with the assigning of the new_point
        if (myPlayer() != null && new_point == null) {
            new_point = new PointF(SCREEN_WIDTH/4, fixDist);
        }

        if (new_point != null && (myPlayer() != null)) {
            // send updated movement of our player to server
            // do the following only if the player has moved and only if player is initialized!
            if ((old_point.x != new_point.x || old_point.y != new_point.y)) {
                KryoClient.send(new PointF(new_point.x/SCREEN_WIDTH, new_point.y/SCREEN_HEIGHT)); // sends the current point of myPlayer to server
                old_point.set(new_point.x, new_point.y);
            }

            // send the last point twice, such that we can detect that the palyer stays still,
            // and thus we can animate an idle_animation for our palyer
            if (sendOnce) {
                KryoClient.send(new PointF(new_point.x/SCREEN_WIDTH, new_point.y/SCREEN_HEIGHT));
                old_point.set(new_point.x, new_point.y);
                sendOnce = false;
            }
        }
        if (myPlayer().isCharged){ //TODO enable and disable special button if not needed (MAYBE)
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        if (myPlayer().isLoser){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
                    Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
                    att_btn.setEnabled(false);
                    super_btn.setEnabled(false);
                }
            });
        }


        if (!btn_active) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    LinearLayout gameUI = (LinearLayout) activity.findViewById(Constants.GAME_PLAY_UI);
                    gameUI.setVisibility(View.VISIBLE);
                    final Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
                    Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
                    btn_active = true;


                    att_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //start sound for attackbutton 
                            if (players.size() > 1) {
                                for (Player enemy : players.values()) {
                                    if (myPlayer().id != enemy.id) myPlayer().attack(enemy);
                                }
                            }


                        }

                    } );


                    super_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (players.size() > 1) {
                                for (Player enemy : players.values()) {
                                    if (myPlayer().id != enemy.id) myPlayer().specialAttack(enemy);
                                }
                            }
                        }
                    });



                }
            });
        }

        if (!CLIENT_CONNECTED)  {
            nextScene = Constants.START_SCENE;
            terminate();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        layer1_5 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer1_5);
        layer1_5.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer1_5.draw(canvas);

        // Draw players inbetween layer5 and layer6
        if (players.size() > 0) {
            if (players.size() == 1){
                myPlayer().isWinner = true; // if all other players disconnected you must be the winner
            }
            ArrayList<Player> winnerlist = new ArrayList<>();
            for (Player player : players.values()) { // draw all players
                if (!player.isLoser)
                    winnerlist.add(player);

                if (player != null){ //only draw player if he's still in game
                    if (!player.isLoser) {
                        if (player.animation.isLastFrame()) {

                            player.animation.setActivate(false);
                            player.animation.setLastFrame(false);
                            if (player.walkInX) {
                                player.idle_rightAnimation();
                            } else {
                                player.idle_leftAnimation();
                            }
                        }

                        player.draw(canvas); // changed this to check for null object
                    } else {
                        player.healthbar.draw(canvas); //if player is dead only draw his health- and chargebar
                        player.chargebar.draw(canvas);
                    }
                }
            }
            if (winnerlist.size() == 1){
                winnerlist.get(0).isWinner = true;  //if list only contains one winner, then he must have won the game
            }
            for (Player player : players.values()) {
                if (player.isWinner){ //if a winner is found "save" the scene
                    GameOverScene.test = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Bitmap.Config.RGB_565);
                    Canvas temp_canvas = new Canvas(GameOverScene.test);

                    canvas.drawColor(Color.WHITE); // BACKGROUND color

                    layer1_5 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer1_5);
                    layer1_5.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
                    layer1_5.draw(temp_canvas);

                    for (Player pl : players.values()) { // draw all players. Cannot use players from above, because the loop stops when loser is found
                        if (pl != null && !pl.isLoser) pl.draw(temp_canvas); // changed this to check for null object
                        else if (pl.isLoser){
                            pl.chargebar.draw(temp_canvas);
                            pl.healthbar.draw(temp_canvas);
                        }
                    }

                    layer6 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer6);
                    layer6.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
                    layer6.draw(temp_canvas);

                    nextScene = Constants.GAMEOVER_SCENE;
                    terminate();
                }
            }

        } else {
            Log.i(TAG, "PLAYER SIZE is 0!");
        }

        layer6 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer6);
        layer6.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer6.draw(canvas);

    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (myPlayer().isLoser){ //reenables the buttons for next game if player lost
                    Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
                    Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
                    att_btn.setEnabled(true);
                    super_btn.setEnabled(true);
                }
                LinearLayout gameUI = (LinearLayout) activity.findViewById(Constants.GAME_PLAY_UI);
                gameUI.setVisibility(View.GONE);
                btn_active = false;

            }
        });
        new_game = true;
        SceneManager.ACTIVE_SCENE = nextScene;
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                movingPlayer = true;
                sendOnce = true;
                touch_old = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_new = event.getX();
                float diff = touch_new - touch_old;
                if (movingPlayer && Math.abs(diff) > 5) { // only move our player if condition true
                    new_point.x = old_point.x + (diff * (SCREEN_WIDTH/500.f)); //TODO adjust 500.f so the player movement is better
                    touch_old = touch_new;
                }
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                sendOnce = true;
                break;
        }

    }

    public static void setNew_point(PointF new_point) {
        GamePlayScene.new_point = new_point;
    }

}
