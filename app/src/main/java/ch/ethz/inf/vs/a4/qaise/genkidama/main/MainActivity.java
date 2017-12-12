package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;


public class MainActivity extends AppCompatActivity {

    public static Context context;
    //declaration for sound here
   private MediaPlayer backgroundsound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // start up theme
        super.onCreate(savedInstanceState);

        context = this.getApplicationContext();

//        backgroundsound = MediaPlayer.create(this,R.raw.loginmusic );
//        backgroundsound.setLooping(true);
//        backgroundsound.start();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //  landscape mode
        getSupportActionBar().hide();

        // Get Screen Dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Constants.SCREEN_WIDTH = metrics.widthPixels;
        Constants.SCREEN_HEIGHT = metrics.heightPixels;
        Constants.PLAYER_SIZE = (int)(Constants.SCREEN_WIDTH*Constants.PLAYER_PERCENTAGE_WIDTH/100);
        Constants.PLAYER_PERCENTAGE_HEIGHT = 100*Constants.PLAYER_SIZE/Constants.SCREEN_HEIGHT;


        // create new layout from scratch
        FrameLayout game = new FrameLayout(this);
        GamePanel gamePanel = new GamePanel(this, this);
        LinearLayout gameUI = new LinearLayout(this);
        RelativeLayout gameOverUI = new RelativeLayout(this);
        RelativeLayout loginUI = new RelativeLayout(this);

        // gameUI buttons
        Button att_btn = new Button(this);
        Button super_btn = new Button(this);

        // gameOverUI buttons
        Button restartGame_btn = new Button(this);
        Button backToLogin_btn = new Button(this);

        // loginUI textviews and buttons
//        TextView textView = new TextView(this); // TODO: added this
        EditText username = new EditText(this);
        EditText ip = new EditText(this);
        EditText port = new EditText(this);
        Button login_btn = new Button(this);    // Button for logging in to the server
        Button enter_btn = new Button(this);    // Button for entering a game if logged in with server

        // Set the custom font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/font1980XX.ttf"); // custom font
//        textView.setTypeface(typeface);
        username.setTypeface(typeface);
        ip.setTypeface(typeface);
        port.setTypeface(typeface);
        login_btn.setTypeface(typeface);
        enter_btn.setTypeface(typeface);
        float textSize = 20;

        //set IDs for different layouts and make them gone
        gameUI.setId(Constants.GAME_UI);
        gameOverUI.setId(Constants.GAMEOVER_UI);
        loginUI.setId(Constants.LOGIN_UI);

        gameUI.setVisibility(View.GONE);
        gameOverUI.setVisibility(View.GONE);
        loginUI.setVisibility(View.GONE);


        // initialize buttons for gameUI
        att_btn.setText(R.string.att_string);
        att_btn.setAlpha(0.6f);
        att_btn.setId(Constants.ATT_BTN);
        att_btn.setBackgroundResource(R.drawable.roundedbutton);

        super_btn.setText(R.string.special);
        super_btn.setAlpha(0.6f);
        super_btn.setId(Constants.SUPER_BTN);
        super_btn.setBackgroundResource(R.drawable.roundedbutton);

        // initialize buttons for gameOverUI
        restartGame_btn.setText(R.string.restartbuttonstring);
        restartGame_btn.setId(Constants.RESTARTGAME_BTN);

        backToLogin_btn.setText(R.string.backtologinbuttonstring);
        backToLogin_btn.setId(Constants.BACK_TO_LOGIN_BTN);


        //initialize loginUI buttons, edittext and textview
//        textView.setHint("");
//        textView.setBackgroundResource(R.drawable.);
//        textView.setTextSize(15);
//        textView.setTextColor(Color.DKGRAY);
//        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
//        textView.setId(Constants.TEXT_VIEW);

        username.setHint(R.string.hinte);
        username.setTextSize(textSize);
        username.setId(Constants.USERNAME_ID);

        port.setHint(R.string.hintport);
        port.setTextSize(textSize);
        port.setId(Constants.PORT_ID);
        port.setInputType(InputType.TYPE_CLASS_NUMBER);

        ip.setHint(R.string.hintip);
        ip.setTextSize(textSize);
        ip.setId(Constants.IP_ID);

        login_btn.setText(R.string.login_button);
        login_btn.setTextColor(Color.rgb(250,250,250));
        login_btn.setBackgroundResource(R.drawable.button);
        login_btn.setTextSize(40);
        login_btn.setId(Constants.LOGIN_BTN);

        enter_btn.setText(R.string.enter_button);
        enter_btn.setTextColor(Color.rgb(250,250,250));
        enter_btn.setBackgroundResource(R.drawable.button);
        enter_btn.setTextSize(40);
        enter_btn.setId(Constants.START_BTN);
        enter_btn.setEnabled(false);
//        enter_btn.setClickable(false);
//        enter_btn.setFocusableInTouchMode(false);
//        enter_btn.setFocusable(false);

        // Layout gameUI
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(175,175);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE);
        params.setMargins(12,8,12,8);

        super_btn.setLayoutParams(params);
        att_btn.setLayoutParams(params);

        gameUI.setOrientation(LinearLayout.HORIZONTAL);
        gameUI.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

        // Layout gameOverUI
        gameOverUI.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams restart_params = new RelativeLayout.LayoutParams(400, 200);
        restart_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        restartGame_btn.setLayoutParams(restart_params);

        RelativeLayout.LayoutParams login_params = new RelativeLayout.LayoutParams(400, 200);
        login_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        login_params.addRule(RelativeLayout.BELOW, Constants.RESTARTGAME_BTN);
        backToLogin_btn.setLayoutParams(login_params);

        // Layout loginUI
        loginUI.setGravity(Gravity.CENTER);


/*        //TODO: username, ip and port EditTexts are not yet aligned rightfully, have no idea why not...
        RelativeLayout.LayoutParams user_params = new RelativeLayout.LayoutParams(600, 200);
        user_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        user_params.addRule(RelativeLayout.ALI);
//        user_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        username.setLayoutParams(user_params);

        RelativeLayout.LayoutParams ip_params = new RelativeLayout.LayoutParams(600, 200);
        ip_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        ip_params.addRule(RelativeLayout.CENTER_VERTICAL);
        ip_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        ip_params.addRule(RelativeLayout.BELOW, Constants.USERNAME_ID);
        ip.setLayoutParams(ip_params);

        RelativeLayout.LayoutParams port_params = new RelativeLayout.LayoutParams(600, 200);
        port_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        port_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        port_params.addRule(RelativeLayout.BELOW, Constants.IP_ID);
        port.setLayoutParams(port_params);*/

        // TODO: works so far
        RelativeLayout.LayoutParams user_params = new RelativeLayout.LayoutParams(600, 200);
        user_params.topMargin = SCREEN_HEIGHT/20 + SCREEN_WIDTH/16; // set to same as bottom of genkidamaLogo
        user_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        username.setLayoutParams(user_params);

        RelativeLayout.LayoutParams ip_params = new RelativeLayout.LayoutParams(600, 200);
        ip_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ip_params.addRule(RelativeLayout.BELOW, Constants.USERNAME_ID);
        ip.setLayoutParams(ip_params);

        RelativeLayout.LayoutParams port_params = new RelativeLayout.LayoutParams(600, 200);
        port_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        port_params.addRule(RelativeLayout.BELOW, Constants.IP_ID);
        port.setLayoutParams(port_params);



//        RelativeLayout.LayoutParams textV_params = new RelativeLayout.LayoutParams(Constants.SCREEN_WIDTH/3, 3*Constants.SCREEN_HEIGHT/4); // TODO added
//        textV_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        textV_params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        textView.setLayoutParams(textV_params);

        RelativeLayout.LayoutParams enter_params = new RelativeLayout.LayoutParams(Constants.SCREEN_WIDTH/5, Constants.SCREEN_HEIGHT/6);
        enter_params.leftMargin = 10;
        enter_params.bottomMargin = 10;
        enter_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        enter_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        login_btn.setLayoutParams(enter_params);

        RelativeLayout.LayoutParams start_params = new RelativeLayout.LayoutParams(Constants.SCREEN_WIDTH/5, Constants.SCREEN_HEIGHT/6);
        start_params.rightMargin = 10;
        start_params.bottomMargin = 10;
        start_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        start_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        enter_btn.setLayoutParams(start_params);


        //add buttons to gameUI
        gameUI.addView(super_btn);
        gameUI.addView(att_btn);

        //add buttons to gameOverUI
        gameOverUI.addView(restartGame_btn);
        gameOverUI.addView(backToLogin_btn);

        //add button and edittext to loginUI
        loginUI.addView(username);
        loginUI.addView(ip);
        loginUI.addView(port);
        loginUI.addView(login_btn);
        loginUI.addView(enter_btn);
//        loginUI.addView(textView);

        //add views to game
        game.addView(gamePanel);
        game.addView(loginUI);
        game.addView(gameOverUI);
        game.addView(gameUI);

        setContentView(game);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (KryoClient.getClient() != null) KryoClient.getClient().close();

/*        if (backgroundsound != null) {
            backgroundsound.reset();
            backgroundsound.release();
            backgroundsound = null;
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (KryoClient.getClient() == null) {

        } else if (!KryoClient.getClient().isConnected()) {
            return;
        } else { // TODO: handle this
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (KryoClient.getClient() == null) {
            // TODO: handle
        } else {
        }

        // TODO: connection might remain, so maybe we want to notify server for handling this
    }

}
