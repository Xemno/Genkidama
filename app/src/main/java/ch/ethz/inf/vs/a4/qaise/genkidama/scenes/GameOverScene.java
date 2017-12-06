package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

/**
 * Created by Qais on 26-Nov-17.
 */

public class GameOverScene implements Scene {

    private Activity activity;
    private boolean btn_active=false;
    private int nextScene;

    public boolean isWinner = false;

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

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAlpha(50);
        Rect screen = new Rect(0,0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        canvas.drawBitmap(test, null, screen, paint);

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
//        if (player1.isLoser) {
//            //if player.isLoser--> then player.setLooserpage();
//            //TODO: create a page where the loser gets displayed something like GAME OVER, want to restart?
//        }

    }

}
