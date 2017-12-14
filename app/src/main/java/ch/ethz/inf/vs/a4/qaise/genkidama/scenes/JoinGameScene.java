package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;

/**
 * Created by Qais on 13-Dec-17.
 */

public class JoinGameScene implements Scene {

    private Activity activity;

    private EditText edit_username;
    private EditText ip_address;
    private EditText port_number;
    private Button join_btn, start_btn;

    private int nextScene;

    private boolean btn_active = false;
    private boolean setEnabled = false;



    public JoinGameScene(Activity activity) {
        this.activity = activity;
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

                    btn_active = true;
                    join_btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {

                            if(checkInputs()) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        KryoClient.getInstance().connect();
                                    }
                                }).start();
                                setEnabled = true;
                            } else {

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

        // TODO: test this
        if (setEnabled && KryoClient.getClient() != null && KryoClient.getClient().isConnected()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    start_btn.setEnabled(true);
                }
            });
            setEnabled = false;
        }

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.rgb(240,230,140)); // BACKGROUND color


    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout startUI = (RelativeLayout) activity.findViewById(Constants.START_UI);
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
        if (!isPort(port)) {
            Toast.makeText(activity.getApplication(), "Invalid Port! Only numbers allowed", Toast.LENGTH_LONG).show();
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
    public static boolean isValidString(String str)
    {
        return str.matches("\\w+");
    }

    public static boolean isPort(String str)
    {
        return str.matches("\\d+");
    }

    public static boolean isIP(String str)
    {
        return str.matches("(\\d+\\.\\d+\\.\\d+\\.\\d+){1}");
    }

}
