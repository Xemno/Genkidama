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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoServer;

/**
 * Created by Qais on 26-Nov-17.
 */

public class GameOverScene implements Scene {

    private Activity activity;
    private boolean btn_active=false;
    private static int nextScene;

    public boolean isWinner = false;
    public static boolean termination = false;

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
                    RelativeLayout gameOverUI = (RelativeLayout) activity.findViewById(Constants.GAME_OVER_UI);
                    gameOverUI.setVisibility(View.VISIBLE);
                    gameOverUI.bringToFront();
                    Button restartgame_btn = (Button) activity.findViewById(Constants.RESTARTGAME_BTN);
                    Button login_btn = (Button) activity.findViewById(Constants.BACK_TO_LOGIN_BTN);
                    btn_active = true;
                    restartgame_btn.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            //nextScene = Constants.GAMEPLAY_SCENE;
                            KryoClient.playAgain(1);
                            long startTime = System.currentTimeMillis();
                            long waitTime;
                            while (!termination){
                                waitTime = System.currentTimeMillis() - startTime;
                                if (waitTime >= 15000) {
                                    nextScene = Constants.START_SCENE;
                                    termination = true;
                                    if (KryoServer.server != null){
                                        KryoServer.server.close();
                                        activity.stopService(new Intent(activity, KryoServer.class));
                                        Toast.makeText(activity.getApplication(), "Server stopped.", Toast.LENGTH_LONG).show();
                                    } else {
                                        KryoClient.close();
                                    }
                                }
                            //ugly loop to wait for termination
                                // TODO:
                                // Qais to Rahel: I think while loop waiting in a UI thread is not a good idea...
                                // you dont need to wait in this while loop, start the the same code with a
                                // boolean variable in the update() outside of the UI thead, which is called every time anyways,
                                // where then you can check for the waitTime, better then doing it here!!
                            }
                            terminate();
                        }
                    });
                    login_btn.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            //go back to login, startactivity main (there is our login)
                            //startActivity(new Intent(this, MainActivity.class));
                            nextScene = Constants.START_SCENE;
                            if (KryoServer.server != null){
                                KryoServer.server.close();
                                activity.stopService(new Intent(activity, KryoServer.class));
                                Toast.makeText(activity.getApplication(), "Server stopped.", Toast.LENGTH_LONG).show();
                            } else {
                                KryoClient.close();
                            }
                            terminate();

                        }
                    });
                }
            });

        }

        // TODO: do your countdown here rahel

    }

    public static void setNextScene(int scene){
        nextScene = scene;
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
                RelativeLayout gameOverUI = (RelativeLayout) activity.findViewById(Constants.GAME_OVER_UI);
                gameOverUI.setVisibility(View.GONE);
                btn_active = false;
                SceneManager.ACTIVE_SCENE = nextScene;
                if (nextScene == Constants.START_SCENE){
                    if (KryoServer.server != null){
                        KryoServer.server.close();
                        activity.stopService(new Intent(activity, KryoServer.class));
                    } else {
                        KryoClient.close();
                    }
                }
                termination = false;
                /*if (nextScene == Constants.LOGIN_SCENE) {
                    Toast.makeText(activity.getApplication(), "Your opponent didn't want to play anymore", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity.getApplication(), "Restarting game...", Toast.LENGTH_SHORT).show();
                }*/
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
        //if myPlayer().isWinner --> then player.setWinnerMedals();
        //TODO create a picture which gives the winner a medal
    }
    public void setLooserpage(){
//        if (myPlayer.isLoser) {
//            //if player.isLoser--> then player.setLooserpage();
//            //TODO: create a page where the loser gets displayed something like GAME OVER, want to restart?
//        }

    }

}
