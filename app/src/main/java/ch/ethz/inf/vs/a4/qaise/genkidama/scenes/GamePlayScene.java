package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.BaseFloor;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.FLOOR_CEILING_DIST_RELATIVE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.ID;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.players;


/**
 * This is one specific scene. For us this might be the scene
 * where the game starts and the palyers can play.
 */

public class GamePlayScene implements Scene {

    private static final String TAG = "#GamePlayScene#";

    private Activity activity;

    private Point old_point, new_point;
    private float x_old, x_new;
    private PointF point; // Point of our player
    private boolean movingPlayer = false;

    private boolean doOnce = true;

    private BaseFloor floor;

    Drawable background_image;

    private boolean btn_active = false;
    private boolean collision = false;  // TODO: not useful here, implement it in the player class
    private boolean new_game = false;

    Animation coinAnimation;



    public GamePlayScene(Activity activity) {
        this.activity = activity;

        floor = new BaseFloor();


//TODO  new_point = new Point(SCREEN_WIDTH/2, floor.getFixHeight()); // at which location the player rectangle is
//        new_point = new Point(SCREEN_WIDTH/2, 3* SCREEN_HEIGHT/4); // at which location the player rectangle is
//        old_point = new Point(0,0);

        /* Set the player on the right side on the screen */
        //TODO> this is for choosing the side of the player, but doesnt work yet
        if (ID == 999){ // should actually never happen
            int var = GamePanel.getRandom(1,9);
            new_point = new Point(var*SCREEN_WIDTH/10, floor.getFixHeight());

        } else { // side was assigned to by server
            int count = 10; // lets say we can have 10 palyers placed on the screen
            if (ID % 2 == 0) { // draw on left side
                new_point = new Point(ID * SCREEN_WIDTH/count, floor.getFixHeight());
            } else {  // draw on right side
                new_point = new Point(SCREEN_WIDTH - ((ID - 1) * SCREEN_WIDTH/count) , floor.getFixHeight());
            }
        }

//        new_point = new Point(0,0);
        old_point = new Point(0,0);


        coinAnimation = new Animation(
                activity, R.drawable.coins,
                15, 16,
                8, 300, 300, true);
        coinAnimation.setFrameDuration(100);
        coinAnimation.scaleBitmap(5);


    }

    @Override
    public void update() {

//        if (doOnce && REGISTERED) {
//            Log.i(TAG, "------Code done once---------");
//            Log.i(TAG, "------Code done once : " + ID);
//
//            if (ID == 999){ // should actually never happen
//                int var = GamePanel.getRandom(1,9);
//                new_point = new Point(var*SCREEN_WIDTH/10, floor.getFixHeight());
//
//            } else { // side was assigned to by server
//                int count = 10; // lets say we can have 10 palyers placed on the screen
//                if (ID % 2 == 0) { // draw on left side
//                    new_point = new Point(ID * SCREEN_WIDTH/count, floor.getFixHeight());
//                } else {  // draw on right side
//                    new_point = new Point(SCREEN_WIDTH - ((ID - 1) * SCREEN_WIDTH/count) , floor.getFixHeight());
//                }
//            }
//            doOnce = false;
//        }

        // send updated movement of our player to server
        // do the following only if the player has moved!
        // TODO: maybe also check for beginning if our player is in HastSet
        if ((old_point.x != new_point.x || old_point.y != new_point.y) && KryoClient.getClient().isConnected() /* && (myPlayer() != null)*/) {
            KryoClient.send(new_point); // sends the point of this player to server
            old_point.set(new_point.x, new_point.y);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE); // BACKGROUND color

        // setting the background, this should actually already scale to any device (whole picture is on it)
        background_image = activity.getBaseContext().getResources().getDrawable(R.drawable.background_image);
        background_image.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        background_image.draw(canvas);

        for (Player player : players.values()) { // draw all players
            player.draw(canvas);
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
                break;
            case MotionEvent.ACTION_MOVE:
                if (movingPlayer) { // only move our player if condition true
//                    new_point.set((int) event.getX(), (int) event.getY()); // update point on touch
//                    new_point.set((int) event.getX(), floor.getFixHeight()); // update point on touch
                    new_point.x = (int) event.getX(); // only update y direction
//                    if (!doOnce && REGISTERED) {
//                        new_point.x = (int) event.getX(); // only update y direction
//                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                break;
        }

    }


}
