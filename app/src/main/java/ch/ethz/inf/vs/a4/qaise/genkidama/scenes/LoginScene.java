package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isIP;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isPort;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel.isValidString;

/**
 * Created by Qais on 26-Nov-17.
 */
public class LoginScene implements Scene {
    public static final String TAG = "##LoginScene## -> ";

    private Activity activity;
    private boolean btn_active = false;

    private EditText edit_username;
    private EditText ip_address;
    private EditText port_number;
    private Button login_btn, start_btn;

    private int top, right, left, bottom;

    private Animation coinAnimation;

    private Drawable background_image, genkidamaLogo;


    public LoginScene(Activity activity){
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
//                    RelativeLayout loginUI = (RelativeLayout) activity.findViewById(Constants.LOGIN_UI);
//                    loginUI.setVisibility(View.VISIBLE);
//                    loginUI.bringToFront();

                    login_btn = (Button) activity.findViewById(Constants.LOGIN_BTN);
                    start_btn = (Button) activity.findViewById(Constants.START_BTN);
                    edit_username = (EditText) activity.findViewById(Constants.USERNAME_ID);
                    ip_address = (EditText) activity.findViewById(Constants.IP_ID);
                    port_number = (EditText) activity.findViewById(Constants.PORT_ID);

                    btn_active = true;
                    login_btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            //TODO: needs to be changed
                            if(checkInputs()) {
                                KryoClient.getInstance().connect();
                                start_btn.setEnabled(true); // TODO: enable when connected to server...
                            }
                        }
                    });

                    start_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (GamePanel.myPlayer() != null /*&& players.size() > 1*/) { // if my player has been added by the server, terminate
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
    }

    @Override
    public void draw(Canvas canvas) {

        // Draw Background Picture
        background_image = activity.getBaseContext().getResources().getDrawable(R.drawable.rock_background);
        background_image.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        background_image.draw(canvas);

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
//                RelativeLayout loginUI = (RelativeLayout) activity.findViewById(Constants.LOGIN_UI);
//                loginUI.setVisibility(View.GONE);
                btn_active = false;
                SceneManager.ACTIVE_SCENE = Constants.GAMEPLAY_SCENE;
            }
        });
    }

    @Override
    public void receiveTouch(MotionEvent event) {}


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


}
