package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.engine.GameEngine;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GameActivity;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;

/**
 * Created by anja on 16.11.2017.
 */

public class GameOverScene implements Scene {

    //TODO: idee: musik generell und böse musik bei gameover --> möglich?
    //medaillen für gewinner --> up to 3 rounds
    //button für restart game --> ACTIVE SCENE=0
    //oder button zu login page --> startACtivity mainactivity
    //constructor
    //TODO: how to set this activescene ?




   // SceneManager.ACTIVE_SCENE=1;

    private Activity activity;
    private boolean btn_active=false;
    private int nextScene;

    Drawable d;
    public boolean isWinner=false;
    Player player1;

    public static Canvas temp_canvas;
    public static Bitmap test;



    public GameOverScene (Activity activity){
        this.activity=activity;
        //player1 = new Player(new Rect(0,0,200,200), Color.RED, 100, 500, 300, 0);
        temp_canvas = new Canvas();

    }
    @Override
    public void update() {
       // SceneManager.ACTIVE_SCENE=1;
        //TODO activescene set
        //if on button click then change to other activity
        if(!btn_active){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout gameOverUI = (RelativeLayout) activity.findViewById(Constants.GAMEOVER_UI);
                    gameOverUI.setVisibility(View.VISIBLE);
                    gameOverUI.bringToFront();
                    Button restartgame_btn = (Button) activity.findViewById(Constants.RESTARTGAME_BTN);
                    Button login_btn = (Button) activity.findViewById(Constants.BACK_TO_LOGIN_BTN);
                    btn_active = true;
                    restartgame_btn.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            nextScene = Constants.GAMEPLAY_SCENE;
                            terminate();
                        }
                    });
                    login_btn.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            //go back to login, startactivity main (there is our login)
                            //startActivity(new Intent(this, MainActivity.class));
                            nextScene = Constants.LOGIN_SCENE;
                            terminate();

                        }
                    });
                }
            });

        }

    }


    @Override
    public void draw(Canvas canvas) {
        //canvas.drawColor(Color.WHITE);
        //canvas.drawColor(Color.WHITE); // BACKGROUND color
        //d = activity.getBaseContext().getResources().getDrawable(R.drawable.background_try);
        //d.setBounds(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        //d.draw(canvas);
        //canvas.drawColor(Color.WHITE);

        Rect screen = new Rect(0,0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        canvas.drawBitmap(test, null, screen, new Paint());


    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout gameOverUI = (RelativeLayout) activity.findViewById(Constants.GAMEOVER_UI);
                gameOverUI.setVisibility(View.GONE);
                btn_active = false;
                SceneManager.ACTIVE_SCENE = nextScene;
            }
        });




    }

    @Override
    public void receiveTouch(MotionEvent event) {
        //TODO: I think nothing todo here? we dont touch each other
        //only have to implement the onclick button methods
        //change background, do medals, maybe music or something like that

    }
    public void setWinnerMedals(){
        //if player.isWinner --> then player.setWinnerMedals();
        //TODO create a picture which gives the winner a medal
    }
    public void setLooserpage(){
        if (player1.isLooser) {
            //if player.isLooser--> then player.setLooserpage();
            //TODO: create a page where the loser gets displayed something like GAME OVER, want to restart?
        }

    }


}
