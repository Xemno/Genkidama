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
import android.widget.TextView;
import android.widget.Toast;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoServer;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.myPlayer;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.players;

import static ch.ethz.inf.vs.a4.qaise.genkidama.scenes.CreateGameScene.isMyServiceRunning;

/**
 * Created by Qais on 26-Nov-17.
 */

public class GameOverScene implements Scene {

    private Activity activity;
    private boolean btn_active=false;
    private static int nextScene;

    public boolean isWinner = false;
    public static boolean termination = false;
    private boolean waiting = false;
    private boolean firstcall = false;

    public static Canvas temp_canvas;
    public static Bitmap test;



    public GameOverScene (Activity activity){
        this.activity=activity;
        temp_canvas = new Canvas();

    }
    @Override
    public void update() {
        if (firstcall) {
            GamePanel.setPlayercount(GamePanel.players.size());
            firstcall = false;
            waiting = true;
        }

        if(!btn_active){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout gameOverUI = (RelativeLayout) activity.findViewById(Constants.GAME_OVER_UI);
                    gameOverUI.setVisibility(View.VISIBLE);
                    gameOverUI.bringToFront();
                    Button restartgame_btn = (Button) activity.findViewById(Constants.RESTARTGAME_BTN);
                    Button login_btn = (Button) activity.findViewById(Constants.BACK_TO_LOGIN_BTN);
                    TextView winner_msg = (TextView) activity.findViewById(Constants.WINNER_MSG);
                    if (!myPlayer().isLoser)
                        winner_msg.setText("You won!");
                    else {
                        for (Player player : players.values()) {
                            if (player.id != myPlayer().id && player.isWinner)
                                winner_msg.setText(player.id + " has won!");
                        }
                    }
                    btn_active = true;
                    restartgame_btn.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            KryoClient.playAgain(1);
                            nextScene = Constants.GAMEPLAY_SCENE;
                            waiting = true;


                        }
                    });
                    login_btn.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            //go back to login, startactivity main (there is our login)
                            KryoClient.playAgain(2);
                            nextScene = Constants.START_SCENE;
                            terminate();

                        }
                    });

                }
            });

        }
        if (waiting){ //TODO update timer, so people know how long to wait for next game
            long startTime = System.currentTimeMillis();
            long waitTime;
            while (!termination){
                waitTime = System.currentTimeMillis() - startTime;
                /*if (waitTime >= 15000) { //wait 15 seconds for all answers
                    nextScene = Constants.START_SCENE;
                    termination = true;
                }*/
            }
            terminate();
        }
    }

    public static void setNextScene(int scene){
        nextScene = scene;
    }

    @Override
    public void draw(Canvas canvas) {
        firstcall = true;
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAlpha(75);
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
                SceneManager.ACTIVE_SCENE = nextScene;
                btn_active = false;
                termination = false;
                waiting = false;
                firstcall = false;
                if (nextScene == Constants.START_SCENE){
                    if (KryoServer.server != null){
                        KryoServer.server.close();
                        activity.stopService(new Intent(activity, KryoServer.class));
                        CreateGameScene.reset();
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                KryoClient.close();
                            }
                        }).start();
                        JoinGameScene.reset();
                    }
                }
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


}
