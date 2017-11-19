package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.engine.GameEngine;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.BaseFloor;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.ChargeBar;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.HealthBar;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;

import static java.security.AccessController.getContext;

/**
 * This is one specific scene. For us this might be the scene
 * where the game starts and the palyers can play.
 */

public class GamePlayScene /*extends Activity*/ implements Scene{

    //TODO: create the main game here
    //TODO: how to set SceneManager.ACTIVESCENE=0; here so that it is variable between this and the scene 1 for the gamover

    boolean is_healthbar_zero=false;
    private float x_old, x_new;
    //static final int MIN_DIST = 200;
    static final int MAX_HEALTH = 1000; //maybe put it into player class and don't give it as an argument
    static final int MAX_CHARGE = 200;

    private Player player1, player2; // player2 should be the enemy player
    private HealthBar healthbar1, healthbar2;
    private ChargeBar chargebar1, chargebar2;
    private Point playerPoint1, playerPoint2;

    static final int FLOOR_HEIGHT = 20; //TODO: replace it with value from Constants class
    private BaseFloor floor;
    Drawable d;

    private Activity activity;

    private boolean btn_active = false;
    private boolean collision = false;
    private boolean new_game = false;

    //TODO: make movement of player1 and player2 dependant on size of phone. Needs to be communicated with Server Messages

    public GamePlayScene(Activity activity) {
        this.activity = activity;

        /* Creating the ground on which the players move
        player1 = new Player(new Rect(0,0,200,200), Color.RED, MAX_HEALTH, MAX_HEALTH, MAX_CHARGE, 0);
        player1.setSide(0);
        healthbar1 = new HealthBar(player1);
        chargebar1 = new ChargeBar(player1);

        //create testplayer for test
        player2 = new Player(new Rect(200,200,400,400), Color.BLUE, MAX_HEALTH, 100, MAX_CHARGE, 0);
        player2.setSide(1);
        healthbar2 = new HealthBar(player2);
        chargebar2 = new ChargeBar(player2);

        playerPoint1 = new Point(Constants.SCREEN_WIDTH/4,Constants.SCREEN_HEIGHT - FLOOR_HEIGHT*Constants.SCREEN_HEIGHT/100 - player1.getRectangle().height()/2);
        player1.update(playerPoint1);

        //initialise enemy
        playerPoint2 = new Point(3*Constants.SCREEN_WIDTH/4, Constants.SCREEN_HEIGHT - FLOOR_HEIGHT*Constants.SCREEN_HEIGHT/100 - player2.getRectangle().height()/2);
        player2.update(playerPoint2);


        //initialise basefloor
        floor = new BaseFloor(FLOOR_HEIGHT);*/


       initializeGame();

    }

    private void initializeGame(){
          /* Creating the ground on which the players move */
        player1 = new Player(new Rect(0,0,200,200), Color.RED, MAX_HEALTH, MAX_HEALTH, MAX_CHARGE, 0);
        player1.setSide(0);
        healthbar1 = new HealthBar(player1);
        chargebar1 = new ChargeBar(player1);

        //create testplayer for test
        player2 = new Player(new Rect(200,200,400,400), Color.BLUE, MAX_HEALTH, 100, MAX_CHARGE, 0);
        player2.setSide(1);
        healthbar2 = new HealthBar(player2);
        chargebar2 = new ChargeBar(player2);

        playerPoint1 = new Point(Constants.SCREEN_WIDTH/4,Constants.SCREEN_HEIGHT - FLOOR_HEIGHT*Constants.SCREEN_HEIGHT/100 - player1.getRectangle().height()/2);
        player1.update(playerPoint1);

        //initialize enemy
        playerPoint2 = new Point(3*Constants.SCREEN_WIDTH/4, Constants.SCREEN_HEIGHT - FLOOR_HEIGHT*Constants.SCREEN_HEIGHT/100 - player2.getRectangle().height()/2);
        player2.update(playerPoint2);


        //initialize basefloor
        floor = new BaseFloor(FLOOR_HEIGHT);
    }


    @Override
    public void update() {
        if (new_game) {
            initializeGame();
            new_game = false;
        }
        collision = false;
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
                            if (collision){
                                player1.attack(player2, collision);
                                healthbar2.update();
                                chargebar1.update();
                            }
                        }
                    });
                }
            });

        }
        if (playerPoint1.x - player1.getRectangle().width()/2 < 0)
            playerPoint1.x = player1.getRectangle().width()/2;
        else if (playerPoint1.x + player1.getRectangle().width()/2 > Constants.SCREEN_WIDTH)
            playerPoint1.x = Constants.SCREEN_WIDTH - player1.getRectangle().width()/2;

        player1.update(playerPoint1);
        player2.update(playerPoint2);
        if (player1.playerCollide(player2)) {
            playerPoint1.x = playerPoint2.x - player2.getRectangle().width()/2 - player1.getRectangle().width()/2;
            player1.update(playerPoint1);
            collision = true;
        }

        //TODO ANJA: this method was thought for transition to gamoverscene


    }
    public void doSceneTransition(){
       // Transition mFadeTransition = new Fade();
        //mFadeTransition= TransitionInflater.from(this).inflateTransition(R.transition.transition);
        //TransitionManager.go((Scene)GameOverScene, mFadeTransition);


       // fade xmlns:android="http://schemas.android.com/apk/res/android" />
    }



    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE); // BACKGROUND color
        d = activity.getBaseContext().getResources().getDrawable(R.drawable.background_try);
        d.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        d.draw(canvas);

        floor.draw(canvas);
        healthbar1.draw(canvas);
        chargebar1.draw(canvas);
        player1.draw(canvas);
        healthbar2.draw(canvas);
        chargebar2.draw(canvas);
        player2.draw(canvas);
        //System.out.println(player2.getCurrentHealth());
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        canvas.drawText(String.valueOf(player2.getCurrentHealth()), Constants.SCREEN_HEIGHT/2, 500, paint);

        //draw the same to temp_canvas
        GameOverScene.test = Bitmap.createBitmap(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT, Bitmap.Config.RGB_565);
        Canvas temp_canvas = new Canvas(GameOverScene.test);

        temp_canvas.drawColor(Color.WHITE); // BACKGROUND color
        d = activity.getBaseContext().getResources().getDrawable(R.drawable.background_try);
        d.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        d.draw(temp_canvas);

        floor.draw(temp_canvas);
        healthbar1.draw(temp_canvas);
        chargebar1.draw(temp_canvas);
        player1.draw(temp_canvas);
        healthbar2.draw(temp_canvas);
        chargebar2.draw(temp_canvas);
        player2.draw(temp_canvas);
        temp_canvas.drawText(String.valueOf(player2.getCurrentHealth()), Constants.SCREEN_HEIGHT/2, 500, paint);

        //if current health is zero change scene
        if(healthbar1.currHealth==0 || healthbar2.currHealth==0) {
            //TransitionManager.go("GameOverScene");
            //set ACTIVE_SCENE=1 --> respectively
            terminate();
        }



    }

    @Override
    public void terminate() {
        //TODO: define what to do if this scene gets terminated
        /*Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
        att_btn.setVisibility(Button.GONE);
        Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
        super_btn.setVisibility(Button.GONE);*/
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout gameUI = (LinearLayout) activity.findViewById(Constants.GAME_UI);
                gameUI.setVisibility(View.GONE);
                btn_active = false;
            }
        });
        new_game = true;
        SceneManager.ACTIVE_SCENE = Constants.GAMEOVER_SCENE;

    }

    @Override
    public void receiveTouch(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                x_old = event.getX();

                return;
            case MotionEvent.ACTION_MOVE:
                    x_new = event.getX();
                    float diff = x_new - x_old;
                    if (Math.abs(diff) > 5) {
                        playerPoint1.set(playerPoint1.x + (int) diff, playerPoint1.y);
                        x_old = x_new;
                    } else {
                        // does not work like this, tap also not recognized
                        //player1.attack(player2, player1.playerCollide(player2));
                    }

                return;
            case MotionEvent.ACTION_UP:
                return;
        }
    }

    //is healthbar1= 0 or healthbasr2=0 --> then switch to gamoverscnene



}
