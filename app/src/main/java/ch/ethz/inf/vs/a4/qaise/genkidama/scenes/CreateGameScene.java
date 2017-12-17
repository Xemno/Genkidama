package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
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

import java.util.HashSet;
import java.util.Set;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoServer;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isPort;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isValidString;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.myPlayer;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.players;

/**
 * Created by Qais on 10-Dec-17.
 */

public class CreateGameScene implements Scene{

    private static final String TAG = "#CreateGameScene#";

    private Activity activity;

    private EditText edit_username, edit_port;
    private Button create_btn, start_btn, back_btn;
    private TextView textView;

    private int nextScene;
//    private int top, right, left, bottom;
    private int playersSize = 0;

    private boolean btn_active = false;
    private static boolean serviceStarted = false;
    public static boolean clientConnect = false; // TODO: is set o staic
    public static boolean setEnabled = false; // TODO: is set o staic
    private static boolean loadAnimating = false;
    private static boolean backToStart = false;



    HashSet<String> names = new HashSet<>();


//    private Drawable genkidamaLogo;
    private Animation loadingAnimation;


    public CreateGameScene(Activity activity) {
        this.activity = activity;

        // Scale top of genkidamaLogo drawable
//        if ((SCREEN_HEIGHT/20 - SCREEN_WIDTH/16) <= 5) {
//            top = 5;
//        } else {
//            top = SCREEN_HEIGHT/20 - SCREEN_WIDTH/16;
//        }

        // Scale rest of genkidamaLogo drawable
//        right = SCREEN_WIDTH/2 + SCREEN_WIDTH/4;
//        left = SCREEN_WIDTH/2 - SCREEN_WIDTH/4;
//        bottom = SCREEN_HEIGHT/20 + SCREEN_WIDTH/16;

//        genkidamaLogo = activity.getBaseContext().getResources().getDrawable(R.drawable.genkidama_splash);
//        genkidamaLogo.setBounds(left, top, right, bottom);

        loadingAnimation = new Animation(
                activity, R.drawable.loading_32,
                32, 32,
                16,
                Constants.SCREEN_WIDTH - 32*4 - 50,
                50, 4, 4, false);
        loadingAnimation.setFrameDuration(50);
    }

    @Override
    public void update() {

        if (!btn_active){
            btn_active = true;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout createGameUI = (RelativeLayout) activity.findViewById(Constants.CREATE_GAME_UI);
                    createGameUI.setVisibility(View.VISIBLE);
                    createGameUI.bringToFront();

                    create_btn = (Button) activity.findViewById(Constants.CREATE2_BUTTON);
                    start_btn = (Button) activity.findViewById(Constants.START2_BTN);
                    back_btn = (Button) activity.findViewById(Constants.BTN_BACK_C);
                    edit_username = (EditText) activity.findViewById(Constants.USERNAME2_ID);
                    edit_port = (EditText) activity.findViewById(Constants.PORT_ET);

                    textView = (TextView) activity.findViewById(Constants.TEXT_VIEW);

                    create_btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            if (checkInputs() && !serviceStarted) {
                                activity.startService(new Intent(activity, KryoServer.class));
                                serviceStarted = true;

                            } else if(serviceStarted) {
                                Toast.makeText(activity.getApplication(), "already connected...", Toast.LENGTH_SHORT).show();
                            }
                        }

                    });

                    start_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (GamePanel.myPlayer() != null && players.size() > 1) { // if my player has been added by the server, terminate
                                nextScene = Constants.GAMEPLAY_SCENE;
                                KryoClient.startGame();
                                terminate();
                            } else {
                                Toast.makeText(activity.getApplication(), "PlayerSize: " + players.size() + "\nmyPlayer added : " + (myPlayer()!=null) , Toast.LENGTH_SHORT).show();
                                if (KryoClient.getClient().isConnected()) {
                                    Toast.makeText(activity.getApplication(), "already connected...", Toast.LENGTH_SHORT).show();
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

        if (Constants.serverStarted && !clientConnect) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    loadAnimating = true;
                    KryoClient.getInstance().connect();
                }
            }).start();
            clientConnect = true;
        }

        if (clientConnect && !setEnabled && KryoClient.getClient() != null && KryoClient.getClient().isConnected()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.append("Server Address: "
                            + Constants.SERVER_ADDRESS + "\n"
                            + "Port Number: "
                            + Constants.PORT_NUMBER + "\n"
                    );
                    start_btn.setEnabled(true);
                }
            });
            setEnabled = true;
        }

        if (players.size() != playersSize) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (players.size() < playersSize) {
                        playersSize--;
                        for (Player p : players.values()) {
                            if (names.contains(p.name)) {
                                names.remove(p.name);
                                textView.append(p.name + " left the game.\n");
                            }
                        }

                    } else if (players.size() > playersSize) {
                        playersSize++;
                        for (Player p : players.values()) {
                            if (!names.contains(p.name)) {
                                names.add(p.name);
                                textView.append(p.name + " joined the game.\n");
                            }
                        }
                    }

                    if (playersSize > 1) loadAnimating = false;

                    textView.append("Players [" + playersSize + "]: [ | ");
                    for (Player player : players.values()) {
                        names.add(player.name);
                        textView.append(player.name + " | ");
                    }
                    textView.append("]\n");

                }
            });
        }

        if (loadingAnimation == null) {
            loadingAnimation = new Animation(
                    activity, R.drawable.loading_32,
                    32, 32,
                    16,
                    Constants.SCREEN_WIDTH - 32*4 - 50,
                    50, 4, 4, false);
            loadingAnimation.setFrameDuration(50);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.rgb(255,215,0)); // BACKGROUND color gold

        // Draw Genkidama Text, centered and scales accordingly to the screen size
        StartScene.genkidamaLogo.draw(canvas);

        if (serviceStarted && loadAnimating && (loadingAnimation != null)) loadingAnimation.draw(canvas);
    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout createGameUI = (RelativeLayout) activity.findViewById(Constants.CREATE_GAME_UI);
                createGameUI.setVisibility(View.GONE);

                switch (createGameUI.getVisibility()) {
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
                loadAnimating = false;

                loadingAnimation.recycle();
                loadingAnimation = null;

                if (backToStart) {
                    serviceStarted = false;
                    clientConnect = false;
                    setEnabled = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            KryoClient.close(); // close client connection
                        }
                    }).start();
                    if (isMyServiceRunning(KryoServer.class, activity)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                activity.stopService(new Intent(activity, KryoServer.class)); // close server connection
                            }
                        }).start();
                    }
//                    backToStart = false;
                }
                backToStart = false;

                SceneManager.ACTIVE_SCENE = nextScene;
            }
        });
    }

    public static void reset(){
        serviceStarted = false;
        clientConnect = false;
        setEnabled = false;
        loadAnimating = false;
        backToStart = false; //not sure if needed -- TODO changed
    }

    @Override
    public void receiveTouch(MotionEvent event) {

    }

    private boolean checkInputs(){
        String name = edit_username.getText().toString();
        String port = edit_port.getText().toString();

        if (!isValidString(name)){
            Toast.makeText(activity.getApplication(), "Invalid Name! Only Characters allowed", Toast.LENGTH_SHORT).show();
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
        return true;
    }

    static boolean isMyServiceRunning(Class<?> serviceClass, Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
