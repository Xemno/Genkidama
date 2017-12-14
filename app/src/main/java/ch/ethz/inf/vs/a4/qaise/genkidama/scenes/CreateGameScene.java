package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoServer;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isValidString;

/**
 * Created by Qais on 10-Dec-17.
 */

public class CreateGameScene implements Scene{

    private static final String TAG = "#CreateGameScene#";

    private Activity activity;

    private EditText edit_username;
    private Button create_btn, start_btn;
    private TextView textView;

    private int nextScene;
    private int top, right, left, bottom;

    private boolean btn_active = false;
    private boolean serviceStarted = false;
    private boolean clientConnect = false;
    private boolean setEnabled = false;

    private Drawable genkidamaLogo;
    private Animation coinAnimation;


    public CreateGameScene(Activity activity) {
        this.activity = activity;

        // Scale top of genkidamaLogo drawable
        if ((SCREEN_HEIGHT/20 - SCREEN_WIDTH/16) <= 5) {
            top = 5;
        } else {
            top = SCREEN_HEIGHT/20 - SCREEN_WIDTH/16;
        }

        // Scale rest of genkidamaLogo drawable
        right = SCREEN_WIDTH/2 + SCREEN_WIDTH/4;
        left = SCREEN_WIDTH/2 - SCREEN_WIDTH/4;
        bottom = SCREEN_HEIGHT/20 + SCREEN_WIDTH/16;

        coinAnimation = new Animation(
                activity, R.drawable.coins,
                15, 16,
                8,
                Constants.SCREEN_WIDTH/4,
                Constants.SCREEN_HEIGHT/3 ,
                true, 4);
        coinAnimation.setFrameDuration(100);
    }

    @Override
    public void update() {

        if (!btn_active){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout createGameUI = (RelativeLayout) activity.findViewById(Constants.CREATE_GAME_UI);
                    createGameUI.setVisibility(View.VISIBLE);
                    createGameUI.bringToFront();

                    create_btn = (Button) activity.findViewById(Constants.CREATE2_BUTTON);
                    start_btn = (Button) activity.findViewById(Constants.START2_BTN);
                    edit_username = (EditText) activity.findViewById(Constants.USERNAME2_ID);
                    textView = (TextView) activity.findViewById(Constants.TEXT_VIEW);

                    btn_active = true;
                    create_btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {

                            if (checkInputs() && !serviceStarted) {
                                activity.startService(new Intent(activity, KryoServer.class));
                                serviceStarted = true;

                            } else if(serviceStarted) {
                                Toast.makeText(activity.getApplication(), "Service already started!", Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                    start_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (GamePanel.myPlayer() != null /*&& players.size() > 1*/) { // if my player has been added by the server, terminate
                                nextScene = Constants.GAMEPLAY_SCENE;
                                terminate();   // TODO: we want to wait for other players too
                            } else {
                                Toast.makeText(activity.getApplication(), "myPlayer not added", Toast.LENGTH_LONG).show();
                                if (KryoClient.getClient().isConnected()) {
//                                    KryoClient.login();
                                    //TODO: test to termiante() here, since already connected
                                    Toast.makeText(activity.getApplication(), "login message sent again", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(activity.getApplication(), "no connection", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    });
                }
            });
        }

        if (Constants.serverStarted && !clientConnect) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    KryoClient.getInstance().connect();
                }
            }).start();
            clientConnect = true;
        }

        if (clientConnect && !setEnabled && KryoClient.getClient() != null && KryoClient.getClient().isConnected()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.append("Server Address: ");
                    textView.setTextColor(Color.rgb(32,178,170));
                    textView.append("" + Constants.SERVER_ADDRESS + "\n");
//                    textView.setTextColor(Color.DKGRAY);
                    textView.append("Port Number: ");
//                    textView.setTextColor(Color.rgb(32,178,170));
                    textView.append("" + Constants.PORT_NUMBER + "\n");
//                    textView.setTextColor(Color.DKGRAY);
                    start_btn.setEnabled(true);
                }
            });
            setEnabled = true;
        }


    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.rgb(240,230,140)); // BACKGROUND color

        // Draw Genkidama Text, centered and scales accordingly to the screen size
        genkidamaLogo = activity.getBaseContext().getResources().getDrawable(R.drawable.genkidama_splash);
        genkidamaLogo.setBounds(left, top, right, bottom);
        genkidamaLogo.draw(canvas);

        coinAnimation.setWhereToDraw(left - 40, (bottom - top) - 30);
        coinAnimation.draw(canvas);
        coinAnimation.setWhereToDraw(right + 20, (bottom - top) - 30);
        coinAnimation.draw(canvas);
    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout startUI = (RelativeLayout) activity.findViewById(Constants.CREATE_GAME_UI);
                startUI.setVisibility(View.GONE);
                btn_active = false;
                SceneManager.ACTIVE_SCENE = nextScene;
            }
        });
    }

    @Override
    public void receiveTouch(MotionEvent event) {

    }

    private boolean checkInputs(){
        String name = edit_username.getText().toString();

        if (!isValidString(name)){
            Toast.makeText(activity.getApplication(), "Invalid Name! Only Characters allowed", Toast.LENGTH_LONG).show();
            return false;
        }
        Constants.NAME = name;
        return true;
    }

}
