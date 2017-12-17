package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoServer;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.Network;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.START_GAME;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isIP;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isPort;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isValidString;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.myPlayer;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.players;

/**
 * Created by Qais on 13-Dec-17.
 */

public class JoinGameScene implements Scene {

    private static final String TAG = "#JoinGameScene#";

    private Activity activity;

    private TextView textView;
    private EditText edit_username;
    private EditText ip_address;
    private EditText port_number;
    private Button join_btn, start_btn, back_btn;

    private int nextScene;

    private boolean btn_active = false;
    private static boolean setEnabled = false;
    private static boolean connect = false;
    private static boolean checkConnection = false;
    private static boolean isConnected = false;
    private static boolean backToStart = false;

    // TODO: make static variable startGame that is set from the Client by the server

    private long lastTime = 0;
    private long timeout = 5000; // 5s timeout like in KryoClient

    Animation loadAnimation;

//    private Drawable genkidamaLogo;
//    private int top, right, left, bottom;



    public JoinGameScene(Activity activity) {
        this.activity = activity;

//        if ((SCREEN_HEIGHT/20 - SCREEN_WIDTH/16) <= 5) {
//            top = 5;
//        } else {
//            top = SCREEN_HEIGHT/20 - SCREEN_WIDTH/16;
//        }
//
//        // Scale rest of genkidamaLogo drawable
//        right = SCREEN_WIDTH/2 + SCREEN_WIDTH/4;
//        left = SCREEN_WIDTH/2 - SCREEN_WIDTH/4;
//        bottom = SCREEN_HEIGHT/20 + SCREEN_WIDTH/16;

//        genkidamaLogo = activity.getBaseContext().getResources().getDrawable(R.drawable.genkidama_splash);
//        genkidamaLogo.setBounds(left, top, right, bottom);

        loadAnimation = new Animation(
                activity, R.drawable.color_pattern_clone_32,
                32, 32,
                23,
                Constants.SCREEN_WIDTH - 32*4 - 25,
                25,4, 4, false);
        loadAnimation.setFrameDuration(50);
    }


    @Override
    public void update() {

        if (!btn_active){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout loginUI = (RelativeLayout) activity.findViewById(Constants.JOIN_GAME_UI);
                    loginUI.setVisibility(View.VISIBLE);
                    loginUI.bringToFront();

                    join_btn = (Button) activity.findViewById(Constants.LOGIN_BTN);
                    start_btn = (Button) activity.findViewById(Constants.START_BTN);
                    back_btn = (Button) activity.findViewById(Constants.BTN_BACK_J);
                    edit_username = (EditText) activity.findViewById(Constants.USERNAME_ID);
                    ip_address = (EditText) activity.findViewById(Constants.IP_ID);
                    port_number = (EditText) activity.findViewById(Constants.PORT_ID);
                    textView = (TextView) activity.findViewById(Constants.TEXT_V);

                    btn_active = true;
                    join_btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {

                            if(checkInputs()) {
                                if (!checkConnection) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.append("Trying to connect to the server..\n");
                                            lastTime = System.currentTimeMillis();
                                        }
                                    });
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            KryoClient.getInstance().connect();
                                            connect = true;
                                        }
                                    }).start();
                                } else {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.append("...trying to find a connection...\n");
                                        }
                                    });
                                }

                                setEnabled = true;
                                checkConnection = true;
                            }

                        }
                    });

                    start_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // TODO: test this
                            if (GamePanel.myPlayer() != null && players.size() > 1 && START_GAME) { // if my player has been added by the server, terminate
                                nextScene = Constants.GAMEPLAY_SCENE;
                                terminate();
                            } else {
                                Toast.makeText(activity.getApplication(), "PlayerSize: " + players.size() + "\nmyPlayer added : " + (myPlayer()!=null) , Toast.LENGTH_SHORT).show();
                                if (KryoClient.getClient().isConnected()) {
                                    Toast.makeText(activity.getApplication(), "Group Leader can only start the game", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity.getApplication(), "no connection", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    });

                    back_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            nextScene = Constants.START_SCENE;
                            backToStart = true;
                            StartScene.backToStart = true;
                            terminate();
                        }
                    });
                }
            });
        }


        if (KryoClient.getClient() != null) {
            if (KryoClient.getClient().isConnected()) {
                isConnected = true;
                if (setEnabled) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            start_btn.setEnabled(true);
                        }
                    });
                    checkConnection = false;
                    setEnabled = false;
                    lastTime = 0;
                }

                if (connect) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.append("You are connected. You can join the game.\n"); // TODO: wait for atleast 2 players
                        }
                    });
                    connect = false;

                }

            } else {

                if (checkConnection) {
                    long time = System.currentTimeMillis();
                    if (time > lastTime + timeout ) {
                        lastTime = time;
                        checkConnection = false;
                        lastTime = 0;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.append("Failed to find a server...\n");
                            }
                        });
                    }
                }

                if (isConnected){ // if we are connected but server crashed or closed, then we dont want to join!
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            start_btn.setEnabled(false);
                            textView.append("Connection to server closed...\n");
                        }
                    });
                    checkConnection = false;
                    lastTime = 0;
                    isConnected = false;
                }

            }

        }

        if (START_GAME) {
            nextScene = Constants.GAMEPLAY_SCENE;
            terminate();
        }

        if (loadAnimation == null) {
            loadAnimation = new Animation(
                    activity, R.drawable.color_pattern_clone_32,
                    32, 32,
                    23,
                    Constants.SCREEN_WIDTH - 32*4 - 25,
                    25,4, 4, false);
            loadAnimation.setFrameDuration(50);
        }

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.rgb(238,232,170)); // BACKGROUND color pale golden rod

        // Draw Genkidama Text, centered and scales accordingly to the screen size
        StartScene.genkidamaLogo.draw(canvas);

        if (checkConnection && (loadAnimation != null)) loadAnimation.draw(canvas);

    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout joinGameUI = (RelativeLayout) activity.findViewById(Constants.JOIN_GAME_UI);
                joinGameUI.setVisibility(View.GONE);

                loadAnimation.recycle();
                loadAnimation = null;

                switch (joinGameUI.getVisibility()) {
                    case View.VISIBLE :
                        Log.e(TAG, "IN TERMINATE VIEW VISIBLE");
                        break;
                    case View.INVISIBLE :
                        Log.e(TAG, "IN TERMINATE VIEW INVISIBLE");
                        break;
                    case View.GONE :
                        Log.e(TAG, "IN TERMINATE VIEW GONE");
                        break;
                }

                btn_active = false;
                setEnabled = false;
                checkConnection = false;
                START_GAME = false;

                if (backToStart) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            KryoClient.close(); // close client connection
                        }
                    }).start();
                    isConnected = false;
                    connect = false;
//                    backToStart = false;
                }
                backToStart = false;

                SceneManager.ACTIVE_SCENE = nextScene;
            }
        });
    }

    public static void reset(){
        setEnabled = false;
        connect = false;
        checkConnection = false;
        isConnected = false;
        backToStart = false;
    }

    @Override
    public void receiveTouch(MotionEvent event) {

    }

    private boolean checkInputs(){
        String name = edit_username.getText().toString();
        String ip = ip_address.getText().toString();
        String port = port_number.getText().toString();

        if (!isValidString(name)){
            Toast.makeText(activity.getApplication(), "Invalid Name! Only Characters allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isIP(ip)){
            Toast.makeText(activity.getApplication(), "Invalid IP format!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isPort(port) || Integer.parseInt(port) < 1024 || Integer.parseInt(port) > 65535) {
            Toast.makeText(activity.getApplication(), "Invalid Port or not in range of [1024, 65535].", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Constants.PORT_NUMBER = Integer.parseInt(port);
        } catch (NumberFormatException nfe){
            Toast.makeText(activity.getApplication(), "Invalid Port Number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        Constants.NAME = name;
        Constants.SERVER_ADDRESS = ip;

        return true;
    }


}
