package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
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

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;


public class MainActivity extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // start up theme
        super.onCreate(savedInstanceState);

        context = this.getApplicationContext();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //  landscape mode
        getSupportActionBar().hide();

        // Get Screen Dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //  landscape mode


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
        Button restart_game_btn = new Button(this);
        Button login_btn = new Button(this);

        // loginUI textviews and buttons
        EditText username = new EditText(this);
        EditText ip = new EditText(this);
        EditText port = new EditText(this);
        Button enter_btn = new Button(this);

        // Set the custom font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/font1980XX.ttf"); // custom font
        username.setTypeface(typeface);
        ip.setTypeface(typeface);
        port.setTypeface(typeface);
        enter_btn.setTypeface(typeface);


        //set IDs for different layouts and make them gone
        gameUI.setId(Constants.GAME_UI);
        gameOverUI.setId(Constants.GAMEOVER_UI);
        loginUI.setId(Constants.LOGIN_UI);

        gameUI.setVisibility(View.GONE);
        gameOverUI.setVisibility(View.GONE);
        loginUI.setVisibility(View.GONE);

        // initialize buttons for gameUI
        att_btn.setText(R.string.att_string);
        att_btn.setId(Constants.ATT_BTN);
        att_btn.setBackgroundResource(R.drawable.roundedbutton);

        super_btn.setText(R.string.special);
        super_btn.setId(Constants.SUPER_BTN);
        super_btn.setBackgroundResource(R.drawable.roundedbutton);

        // initialize buttons for gameOverUI
        restart_game_btn.setText(R.string.restartbuttonstring);
        restart_game_btn.setId(Constants.RESTARTGAME_BTN);

        login_btn.setText(R.string.backtologinbuttonstring);
        login_btn.setId(Constants.BACK_TO_LOGIN_BTN);

        float textSize = username.getTextSize() * 0.7f;
        //initialize loginUI buttons and edittext
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

        enter_btn.setText(R.string.enterbutton);
        enter_btn.setTextSize(textSize);
        enter_btn.setId(Constants.ENTER_BTN);

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
        restart_game_btn.setLayoutParams(restart_params);

        RelativeLayout.LayoutParams login_params = new RelativeLayout.LayoutParams(400, 200);
        login_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        login_params.addRule(RelativeLayout.BELOW, Constants.RESTARTGAME_BTN);
        login_btn.setLayoutParams(login_params);

        // Layout loginUI
        loginUI.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams user_params = new RelativeLayout.LayoutParams(600, 200);
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

        RelativeLayout.LayoutParams enter_params = new RelativeLayout.LayoutParams(600, 200);
        enter_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        enter_params.addRule(RelativeLayout.BELOW, Constants.PORT_ID);
        enter_btn.setLayoutParams(enter_params);



        //add buttons to gameUI
        gameUI.addView(super_btn);
        gameUI.addView(att_btn);

        //add buttons to gameOverUI
        gameOverUI.addView(restart_game_btn);
        gameOverUI.addView(login_btn);

        //add button and edittext to loginUI
        loginUI.addView(username);
        loginUI.addView(ip);
        loginUI.addView(port);
        loginUI.addView(enter_btn);

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
