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

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.FLOOR_CEILING_DIST_RELATIVE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.ID;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.fixDist;
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

    private boolean sendOnce = true;

    private Drawable layer1_5, layer6;

    private boolean btn_active = false;
    private boolean new_game = false;

    MediaPlayer attacksound;
    MediaPlayer specialattacksound;

    private float touch_old, touch_new; // used for movement detection. DO NOT confuse with old_point and new_point


    public GamePlayScene(Activity activity) {
        this.activity = activity;

        fixDist = FLOOR_CEILING_DIST_RELATIVE*SCREEN_HEIGHT/100;

        old_point = new PointF(0,0);
    }

    @Override
    public void update() {

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
                            // TODO: why not moving this to constructor
//                            attacksound = MediaPlayer.create(MainActivity.context, R.raw.attacksound);


                            //start sound for attackbutton 
                            if (players.size() > 1) {
                                for (Player enemy : players.values()) {
                                    if (myPlayer().id != enemy.id) myPlayer().attack(enemy);
                                }
                            }

                        }

                    } );
                    // TODO: why not moving this to terminate() ? this is released often times..
//                    attacksound.release();
//                    attacksound = null;

                    


                    super_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

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
    }

    @Override
    public void draw(Canvas canvas) {
//        canvas.drawColor(Color.WHITE); // BACKGROUND color

//        long time = System.currentTimeMillis();
//
//        if ( time > lastFrameChangeTime + frameLengthInMilliseconds) {
//            lastFrameChangeTime = time;
//            if (fromLeftToRight) {
//                dx += 2;
//            } else {
//                dx -= 2;
//            }
//
//            if (SCREEN_WIDTH/5 <= dx) {
//                fromLeftToRight = false;
//            } if (dx <= -SCREEN_WIDTH/5) {
//                fromLeftToRight = true;
//            }
//        }

        layer1_5 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer1_5);
        layer1_5.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer1_5.draw(canvas);

        // Draw players inbetween layer5 and layer6
        if (players.size() > 0) {
            for (Player player : players.values()) { // draw all players
                if (player != null){
                    if(player.attack_right.isLastFrame() && player.walkInX){
                        player.idle_rightAnimation();
                        player.attack_right.setActivate(false);
                        player.attack_right.setLastFrame(false);
                    }else if(player.attack_right.isLastFrame() && !player.walkInX){
                        player.idle_leftAnimation();
                        player.attack_right.setActivate(false);
                        player.attack_right.setLastFrame(false);
                    }else if(player.attack_left.isLastFrame() && player.walkInX){
                        player.idle_rightAnimation();
                        player.attack_left.setActivate(false);
                        player.attack_left.setLastFrame(false);
                    }else if(player.attack_left.isLastFrame() && !player.walkInX) {
                        player.idle_leftAnimation();
                        player.attack_left.setActivate(false);
                        player.attack_left.setLastFrame(false);
                    }else if(player.special_attack_right.isLastFrame() && player.walkInX){
                        player.idle_rightAnimation();
                        player.special_attack_right.setActivate(false);
                        player.special_attack_right.setLastFrame(false);
                    }else if(player.special_attack_right.isLastFrame() && !player.walkInX){
                        player.idle_leftAnimation();
                        player.special_attack_right.setActivate(false);
                        player.special_attack_right.setLastFrame(false);
                    }else if(player.special_attack_left.isLastFrame() && player.walkInX){
                        player.idle_rightAnimation();
                        player.special_attack_left.setActivate(false);
                        player.special_attack_left.setLastFrame(false);
                    }else if(player.special_attack_left.isLastFrame() && !player.walkInX) {
                        player.idle_leftAnimation();
                        player.special_attack_left.setActivate(false);
                        player.special_attack_left.setLastFrame(false);
                    }

                    if(player.block_right.isLastFrame() && player.walkInX){
                        player.idle_rightAnimation();
                        player.block_right.setActivate(false);
                        player.block_right.setLastFrame(false);
                    }else if(player.block_right.isLastFrame() && !player.walkInX){
                        player.idle_leftAnimation();
                        player.block_right.setActivate(false);
                        player.block_right.setLastFrame(false);
                    }else if(player.block_left.isLastFrame() && player.walkInX){
                        player.idle_rightAnimation();
                        player.block_left.setActivate(false);
                        player.block_left.setLastFrame(false);
                    }else if(player.block_left.isLastFrame() && !player.walkInX) {
                        player.idle_leftAnimation();
                        player.block_left.setActivate(false);
                        player.block_left.setLastFrame(false);
                    }

                    player.draw(canvas); // changed this to check for null object
                }
            }

            for (Player player : players.values()) {
                if (player.isLoser){
                    GameOverScene.test = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Bitmap.Config.RGB_565);
                    Canvas temp_canvas = new Canvas(GameOverScene.test);

                    canvas.drawColor(Color.WHITE); // BACKGROUND color

                    layer1_5 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer1_5);
                    layer1_5.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
                    layer1_5.draw(temp_canvas);

                    for (Player pl : players.values()) { // draw all players. Cannot use players from above, because the loop stops when loser is found
                        if (pl != null) pl.draw(temp_canvas); // changed this to check for null object
                    }

                    layer6 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer6);
                    layer6.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
                    layer6.draw(canvas);

                    terminate(); // TODO: switch to gameoverscene
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
                LinearLayout gameUI = (LinearLayout) activity.findViewById(Constants.GAME_PLAY_UI);
                gameUI.setVisibility(View.GONE);
                btn_active = false;
                specialattacksound.release();
                attacksound.release();
                specialattacksound = null;
                attacksound = null;
            }
        });
        new_game = true;
        SceneManager.ACTIVE_SCENE = Constants.GAMEOVER_SCENE;
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
