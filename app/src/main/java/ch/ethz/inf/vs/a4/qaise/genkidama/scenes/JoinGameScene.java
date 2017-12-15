package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
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

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isIP;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isPort;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isValidString;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.myPlayer;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.players;

/**
 * Created by Qais on 13-Dec-17.
 */

public class JoinGameScene implements Scene {

    private Activity activity;

    private TextView textView;
    private EditText edit_username;
    private EditText ip_address;
    private EditText port_number;
    private Button join_btn, start_btn;

    private int nextScene;

    private boolean btn_active = false;
    private boolean setEnabled = false;
    private boolean connect = false;
    private boolean checkConnection = false;
    private boolean isConnected = false;

    private long lastTime = 0;
    private long timeout = 5000; // 5s timeout like in KryoClient

    Animation loadAnimation;



    public JoinGameScene(Activity activity) {
        this.activity = activity;

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
                                } else {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.append("...trying to find a connection...\n");
                                        }
                                    });
                                }

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        KryoClient.getInstance().connect();
                                        connect = true;
                                    }
                                }).start();

                                setEnabled = true;
                                checkConnection = true;
                            }

                        }
                    });

                    start_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // TODO: test this
                            if (GamePanel.myPlayer() != null && players.size() > 1) { // if my player has been added by the server, terminate
                                nextScene = Constants.GAMEPLAY_SCENE;
                                terminate();
                            } else {
                                Toast.makeText(activity.getApplication(), "PlayerSize: " + players.size() + "\n myPlayer added : " + (myPlayer()!=null) , Toast.LENGTH_LONG).show();
                                if (KryoClient.getClient().isConnected()) {
                                    Toast.makeText(activity.getApplication(), "already connected...", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(activity.getApplication(), "no connection", Toast.LENGTH_LONG).show();
                                }
                            }

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


    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.rgb(240,230,140)); // BACKGROUND color

        if (checkConnection) loadAnimation.draw(canvas);

    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout joinGameUI = (RelativeLayout) activity.findViewById(Constants.JOIN_GAME_UI);
                joinGameUI.setVisibility(View.GONE);
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
        String ip = ip_address.getText().toString();
        String port = port_number.getText().toString();

        if (!isValidString(name)){
            Toast.makeText(activity.getApplication(), "Invalid Name! Only Characters allowed", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!isIP(ip)){
            Toast.makeText(activity.getApplication(), "Invalid IP format!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!isPort(port) || Integer.parseInt(port) < 1024 || Integer.parseInt(port) > 65535) {
            Toast.makeText(activity.getApplication(), "Invalid Port or not in range of [1024, 65535].", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            Constants.PORT_NUMBER = Integer.parseInt(port);
        } catch (NumberFormatException nfe){
            Toast.makeText(activity.getApplication(), "Invalid Port Number!", Toast.LENGTH_LONG).show();
            return false;
        }
        Constants.NAME = name;
        Constants.SERVER_ADDRESS = ip;

        return true;
    }


}
