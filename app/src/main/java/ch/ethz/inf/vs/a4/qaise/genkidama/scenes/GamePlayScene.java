package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
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
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.BaseFloor;
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

    private BaseFloor floor;

    private Drawable layer1, layer2, layer3, layer4, layer5, layer6;

    private boolean btn_active = false;
    private boolean new_game = false;
    MediaPlayer attacksound= new MediaPlayer();
    MediaPlayer specialattacksound= new MediaPlayer();


//    private int frameLengthInMilliseconds = 50;
//    private long lastFrameChangeTime = 0;
//    private int dx = 0;
//    private boolean fromLeftToRight = true;

    public GamePlayScene(Activity activity) {
        this.activity = activity;

        floor = new BaseFloor();
        fixDist = floor.getFixHeight();

/*        if (myPlayer() != null) { // can be null at his point
            new_point = myPlayer().new_point;
        } else {
            new_point = new PointF(SCREEN_WIDTH/4, fixDist);
        }*/

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

                    LinearLayout gameUI = (LinearLayout) activity.findViewById(Constants.GAME_UI);
                    gameUI.setVisibility(View.VISIBLE);
                    Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
                    Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
                    btn_active = true;


                    att_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(attacksound!=null){attacksound.release();}

                            attacksound=MediaPlayer.create(MainActivity.context, R.raw.attacksound);
                            attacksound.start();
                            //start sound for attackbutton 
                            if (players.size() > 1) {
                                for (Player enemy : players.values()) {
                                    if (myPlayer().id != enemy.id) myPlayer().attack(enemy);
                                }
                            }

                        }

                    } );
                    attacksound.release();
                    attacksound=null;

                    


                    super_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(specialattacksound!=null){specialattacksound.release();}

                           specialattacksound=MediaPlayer.create(MainActivity.context, R.raw.specialattacksound);
                            specialattacksound.start();
                            if (players.size() > 1) {
                                for (Player enemy : players.values()) {
                                    if (myPlayer().id != enemy.id) myPlayer().specialAttack(enemy);
                                }
                            }

                        }
                    });
                    specialattacksound.release();
                    specialattacksound=null;


                }
            });
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE); // BACKGROUND color

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

        layer1 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer1);
        layer1.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer1.draw(canvas);

        layer2 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer2);
        layer2.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer2.draw(canvas);

        layer3 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer3);
        layer3.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer3.draw(canvas);

        layer4 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer4);
        layer4.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer4.draw(canvas);

        layer5 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer5);
        layer5.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer5.draw(canvas);

        // Draw players inbetween layer5 and layer6
        if (players.size() > 0) {
            for (Player player : players.values()) { // draw all players
                if (player != null) player.draw(canvas); // changed this to check for null object
            }

            for (Player player : players.values()) {
                if (player.isLoser){
//                    terminate(); // TODO: switch to gameoverscene
                }
            }

        } else {
            Log.i(TAG, "PLAYER SIZE is 0!");
        }

        layer6 = activity.getBaseContext().getResources().getDrawable(R.drawable.layer6);
        layer6.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        layer6.draw(canvas);




//        floor.draw(canvas);


    }

    @Override
    public void terminate() {
        // TODO:
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                movingPlayer = true;
                sendOnce = true;
                old_point.x = event.getX(); // not sure if right
                break;
            case MotionEvent.ACTION_MOVE:
                new_point.x = event.getX();
                float diff = new_point.x - old_point.x;
                if (movingPlayer && Math.abs(diff) > 5) { // only move our player if condition true
                    new_point.x = old_point.x + (diff * 100/SCREEN_WIDTH);
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
