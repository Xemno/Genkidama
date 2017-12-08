package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
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
//TODO: HERE start openingmusic and when attack happens
    //TODO: start attacksound for special attack and normalattack for attack
    //TODO: Start them in the onclickmethods of the buttons
    //TODO: Add music for onbuttonclick (click) sound --> declare in which class?
    private static final String TAG = "#GamePlayScene#";

    private Activity activity;

    private PointF old_point, new_point;
    private boolean movingPlayer = false;

    float x_old, x_new;

    private boolean doOnce = true;

    private boolean sendOnce = true;

    private BaseFloor floor;

    Drawable background_image;

    private boolean btn_active = false;
    private boolean collision = false;  // TODO: not useful here, implement it in the player class
    private boolean new_game = false;

    Animation coinAnimation;



    public GamePlayScene(Activity activity) {
        this.activity = activity;

        floor = new BaseFloor();
        fixDist = floor.getFixHeight();

        old_point = new PointF(0,0);
        new_point = new PointF(0, fixDist); // TODO: added such that new_point is not null, must be changed though

        coinAnimation = new Animation(
                activity, R.drawable.coins,
                15, 16,
                8, 300, 300, true);
        coinAnimation.setFrameDuration(100);
        coinAnimation.scaleBitmap(5);

    }

    @Override
    public void update() {

        // initialize players new_point if assigned by server
        if (myPlayer() != null && new_point == null) {
            new_point = new PointF(myPlayer().new_point.x, myPlayer().new_point.y);
        }

        // send updated movement of our player to server
        // do the following only if the player has moved and only if player is initilaized!
        if ((myPlayer() != null) && (old_point.x != new_point.x || old_point.y != new_point.y) && KryoClient.getClient().isConnected()) {
            KryoClient.send(new_point); // sends the current point of myPlayer to server
            old_point.set(new_point.x, new_point.y);
        }

        // send the last point twice, such that we can detect that the palyer stays still,
        // and thus we can animate an idle_animation for our palyer
        if (sendOnce) {
            KryoClient.send(new_point);
            old_point.set(new_point.x, new_point.y);
            sendOnce = false;
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
                            if (players.size() > 1) {
                                for (Player enemy : players.values()) {
                                    if (myPlayer().id != enemy.id) myPlayer().attack(enemy);
                                }
                            }
                        }
                    });


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
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE); // BACKGROUND color



        // setting the background, this should actually already scale to any device (whole picture is on it)
        background_image = activity.getBaseContext().getResources().getDrawable(R.drawable.background_image);
        background_image.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT );
        background_image.draw(canvas);

        for (Player player : players.values()) { // draw all players
            player.draw(canvas);

//            Log.i(TAG, player.name);
//            Log.i(TAG, "ID:" + String.valueOf(player.id));
//            Log.i(TAG, "SIDE: " + String.valueOf(player.side));
        }

        coinAnimation.draw(canvas);

        floor.draw(canvas);

    }

    @Override
    public void terminate() {
        // TODO:
    }

    @Override
    public void receiveTouch(MotionEvent event) {
        //TODO: define what to do if touch event received, i.e., move players etc.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                movingPlayer = true;
                sendOnce = true;
//                x_old = event.getX();
                old_point.x = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                new_point.x = event.getX();
                float diff = new_point.x - old_point.x;

                if (movingPlayer && Math.abs(diff) > 5) { // only move our player if condition true
//                    new_point.x = (int) event.getX(); // only update y direction
                    new_point.x = old_point.x + (diff * 100/SCREEN_WIDTH);
//                    x_old = x_new;
                }
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                sendOnce = true;
//                new_point.x = (int) event.getX(); // TODO: newly added...
                break;
        }

    }


}
