package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.content.pm.ActivityInfo;
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

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game);

        // fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        getSupportActionBar().hide();
        // Create new GamePanel view
        //setContentView(new GamePanel(this));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //  landscape mode


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

        //initialize loginUI buttons and edittext
        username.setHint(R.string.hinte);
        username.setId(Constants.USERNAME_ID);

        port.setHint(R.string.hintport);
        port.setId(Constants.PORT_ID);
        port.setInputType(InputType.TYPE_CLASS_NUMBER);

        ip.setHint(R.string.hintip);
        ip.setId(Constants.IP_ID);

        enter_btn.setText(R.string.enterbutton);
        enter_btn.setId(Constants.ENTER_BTN);

        // Layout gameUI
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(175,175);
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
        //new GamePanel(this);
    }

    @Override
    public void onBackPressed() {

    }
}
