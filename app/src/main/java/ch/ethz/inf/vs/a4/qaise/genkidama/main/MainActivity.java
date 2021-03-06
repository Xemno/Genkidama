package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoServer;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.Scene;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.SceneManager;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;


public class MainActivity extends AppCompatActivity {

    public static Context context;
    //declaration for sound here
    private MediaPlayer backgroundsound;
    private Equalizer mEqualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // start up theme
        super.onCreate(savedInstanceState);

        context = this.getApplicationContext();

        backgroundsound = MediaPlayer.create(this,R.raw.loginmusic );
        backgroundsound.setLooping(true);
        backgroundsound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                backgroundsound.start();
            }
        });
        setupEqualizerFxAndUI(); //to equalize music that it works correctly

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



    /* **********************************
     *  Crete new Layout from scratch   *
     ********************************** */
    /* ----------------------------------------------------------------------------------------- */

        FrameLayout game = new FrameLayout(this);
        GamePanel gamePanel = new GamePanel(this, this);
        RelativeLayout startSceneUI = new RelativeLayout(this);
        RelativeLayout gameOverSceneUI = new RelativeLayout(this);
        RelativeLayout joinGameUI = new RelativeLayout(this);
        RelativeLayout createGameUI = new RelativeLayout(this);
        LinearLayout gamePlayeSceneUI = new LinearLayout(this);


        // Set the custom font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/font1980XX.ttf"); // custom font
        Typeface typefaceCDEF = Typeface.createFromAsset(getAssets(), "fonts/CDEF.ttf"); // custom font
        Typeface typefaceBandit = Typeface.createFromAsset(getAssets(), "fonts/Bandit.ttf"); // custom font

        float textSize = 25;
        float btnTextSize = 40;


        /* **********************************
         *      Start SCENE SCENE UI        *
         ********************************** */
        Button join_btn = new Button(this);
        Button create_btn = new Button(this);

        join_btn.setText(R.string.join);
        join_btn.setId(Constants.JOIN_BUTTON);
        join_btn.setTextSize(30);
        join_btn.setTextColor(Color.rgb(250,250,250));
        join_btn.setBackgroundResource(R.drawable.button);
        join_btn.setTypeface(typeface);

        create_btn.setText(R.string.create);
        create_btn.setId(Constants.CREATE_BUTTON);
        create_btn.setTextSize(30);
        create_btn.setTextColor(Color.rgb(250,250,250));
        create_btn.setBackgroundResource(R.drawable.button);
        create_btn.setTypeface(typeface);

        startSceneUI.setId(Constants.START_UI);
        startSceneUI.setVisibility(View.GONE);

        // Layout gameUI
        RelativeLayout.LayoutParams joinParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        joinParams.topMargin = SCREEN_HEIGHT/20 + SCREEN_WIDTH/16; // set to same as bottom of genkidamaLogo
        joinParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        joinParams.addRule(RelativeLayout.CENTER_VERTICAL);
        join_btn.setLayoutParams(joinParams);

        RelativeLayout.LayoutParams createParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        createParams.addRule(RelativeLayout.BELOW, Constants.JOIN_BUTTON);
        createParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        createParams.topMargin = 10;
        create_btn.setLayoutParams(createParams);

        //add buttons to gameUI
        startSceneUI.addView(join_btn);
        startSceneUI.addView(create_btn);


        /* **********************************
         *       Join Game SCENE UI         *
         ********************************** */

        TextView textV = new TextView(this);
        EditText username = new EditText(this);
        EditText ip = new EditText(this);
        EditText port = new EditText(this);
        Button join_game_btn = new Button(this);    // Button for logging in to the server
        Button start_game_btn = new Button(this);    // Button for entering a game if logged in with server
        Button btn_back_J = new Button(this);

        textV.setTypeface(typefaceBandit);
        username.setTypeface(typeface);
        ip.setTypeface(typeface);
        port.setTypeface(typeface);
        join_game_btn.setTypeface(typeface);
        start_game_btn.setTypeface(typeface);
        btn_back_J.setTypeface(typeface);

        textV.setHint("Here you can join a created game...");
//        textView.setBackgroundResource(R.drawable.);
        textV.setTextSize(20);
        textV.setTextColor(Color.DKGRAY);
        textV.setMovementMethod(ScrollingMovementMethod.getInstance());
        textV.setId(Constants.TEXT_V);

        RelativeLayout.LayoutParams tV_params = new RelativeLayout.LayoutParams(Constants.SCREEN_WIDTH/2, 3*Constants.SCREEN_HEIGHT/5);
        tV_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tV_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tV_params.rightMargin = 10;
        textV.setLayoutParams(tV_params);

        joinGameUI.setId(Constants.JOIN_GAME_UI);
        joinGameUI.setVisibility(View.GONE);


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

        join_game_btn.setText(R.string.join);
        join_game_btn.setTextColor(Color.rgb(250,250,250));
        join_game_btn.setBackgroundResource(R.drawable.button);
        join_game_btn.setTextSize(btnTextSize);
        join_game_btn.setId(Constants.LOGIN_BTN);

        start_game_btn.setText(R.string.start);
        start_game_btn.setTextColor(Color.rgb(250,250,250));
        start_game_btn.setBackgroundResource(R.drawable.button);
        start_game_btn.setTextSize(btnTextSize);
        start_game_btn.setId(Constants.START_BTN);
        start_game_btn.setEnabled(false);

        joinGameUI.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams user_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        user_params.topMargin = Constants.SCREEN_HEIGHT/4;
        user_params.leftMargin = 20;
        username.setLayoutParams(user_params);

        RelativeLayout.LayoutParams ip_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ip_params.addRule(RelativeLayout.BELOW, Constants.USERNAME_ID);
        ip_params.leftMargin = 20;
        ip.setLayoutParams(ip_params);

        RelativeLayout.LayoutParams port_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        port_params.addRule(RelativeLayout.BELOW, Constants.IP_ID);
        port_params.leftMargin = 20;
        port.setLayoutParams(port_params);

        RelativeLayout.LayoutParams enter_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        enter_params.leftMargin = 10;
        enter_params.bottomMargin = 10;
        enter_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        enter_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        join_game_btn.setLayoutParams(enter_params);

        RelativeLayout.LayoutParams start_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        start_params.rightMargin = 10;
        start_params.bottomMargin = 10;
        start_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        start_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        start_game_btn.setLayoutParams(start_params);

        btn_back_J.setText(R.string.back);
        btn_back_J.setTextColor(Color.rgb(250,250,250));
        btn_back_J.setBackgroundResource(R.drawable.button);
        btn_back_J.setTextSize(btnTextSize);
        btn_back_J.setId(Constants.BTN_BACK_J);

        RelativeLayout.LayoutParams btn_back_J_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_back_J_params.leftMargin = 10;
        btn_back_J_params.topMargin = 10;
        btn_back_J_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        btn_back_J_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btn_back_J.setLayoutParams(btn_back_J_params);

        //add button and edittext to loginUI
        joinGameUI.addView(textV);
        joinGameUI.addView(username);
        joinGameUI.addView(ip);
        joinGameUI.addView(port);
        joinGameUI.addView(join_game_btn);
        joinGameUI.addView(start_game_btn);
        joinGameUI.addView(btn_back_J);


        /* **********************************
         *       CREATE GAME SCENE UI       *
         ********************************** */
        createGameUI.setId(Constants.CREATE_GAME_UI);
        createGameUI.setVisibility(View.GONE);

        TextView textView = new TextView(this);
        EditText usrname = new EditText(this);
        EditText portET = new EditText(this);
        Button create_game_btn = new Button(this);    // Button for logging in to the server
        Button start_game_btn2 = new Button(this);    // Button for entering a game if logged in with server
        Button btn_back_C = new Button(this);

        usrname.setTypeface(typeface);
        portET.setTypeface(typeface);
        textView.setTypeface(typefaceBandit);
        start_game_btn2.setTypeface(typeface);
        btn_back_C.setTypeface(typeface);
        create_game_btn.setTypeface(typeface);


        textView.setHint("Waiting for some action...");
        textView.setTextSize(20);
        textView.setTextColor(Color.DKGRAY);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setId(Constants.TEXT_VIEW);

        RelativeLayout.LayoutParams textV_params = new RelativeLayout.LayoutParams(Constants.SCREEN_WIDTH/2, 3*Constants.SCREEN_HEIGHT/5);
        textV_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        textV_params.addRule(RelativeLayout.CENTER_IN_PARENT);
        textV_params.rightMargin = 10;
        textView.setLayoutParams(textV_params);

        usrname.setHint(R.string.hinte);
        usrname.setTextSize(textSize);
        usrname.setId(Constants.USERNAME2_ID);

        RelativeLayout.LayoutParams usr_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        usr_params.topMargin = Constants.SCREEN_HEIGHT/3;
        usr_params.leftMargin = 20;
        usrname.setLayoutParams(usr_params);

        portET.setHint(R.string.hintport);
        portET.setTextSize(textSize);
        portET.setId(Constants.PORT_ET);
        portET.setInputType(InputType.TYPE_CLASS_NUMBER);

        RelativeLayout.LayoutParams portET_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        portET_params.addRule(RelativeLayout.BELOW, Constants.USERNAME2_ID);
        portET_params.leftMargin = 20;
        portET.setLayoutParams(portET_params);

        create_game_btn.setText(R.string.create);
        create_game_btn.setTextColor(Color.rgb(250,250,250));
        create_game_btn.setBackgroundResource(R.drawable.button);
        create_game_btn.setTextSize(btnTextSize);
        create_game_btn.setId(Constants.CREATE2_BUTTON);

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.leftMargin = 10;
        params1.bottomMargin = 10;
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        create_game_btn.setLayoutParams(params1);

        start_game_btn2.setText(R.string.start);
        start_game_btn2.setTextColor(Color.rgb(250,250,250));
        start_game_btn2.setBackgroundResource(R.drawable.button);
        start_game_btn2.setTextSize(btnTextSize);
        start_game_btn2.setId(Constants.START2_BTN);
        start_game_btn2.setEnabled(false);

        RelativeLayout.LayoutParams start2_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        start2_params.rightMargin = 10;
        start2_params.bottomMargin = 10;
        start2_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        start2_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        start_game_btn2.setLayoutParams(start2_params);

        btn_back_C.setText(R.string.back);
        btn_back_C.setTextColor(Color.rgb(250,250,250));
        btn_back_C.setBackgroundResource(R.drawable.button);
        btn_back_C.setTextSize(btnTextSize);
        btn_back_C.setId(Constants.BTN_BACK_C);

        RelativeLayout.LayoutParams btn_back_C_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btn_back_C_params.leftMargin = 10;
        btn_back_C_params.topMargin = 10;
        btn_back_C_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        btn_back_C_params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btn_back_C.setLayoutParams(btn_back_C_params);


        createGameUI.addView(textView);
        createGameUI.addView(usrname);
        createGameUI.addView(portET);
        createGameUI.addView(create_game_btn);
        createGameUI.addView(start_game_btn2);
        createGameUI.addView(btn_back_C);


        /* **********************************
         *        GAMEPLAY SCENE UI         *
         ********************************** */

        Button att_btn = new Button(this);
        Button super_btn = new Button(this);

        att_btn.setText(R.string.att_string);
        att_btn.setAlpha(0.6f);
        att_btn.setTextSize(20);
        att_btn.setId(Constants.ATT_BTN);
        att_btn.setBackgroundResource(R.drawable.roundedbutton);
        att_btn.setTypeface(typeface);

        super_btn.setText(R.string.special);
        super_btn.setAlpha(0.6f);
        super_btn.setTextSize(20);
        super_btn.setId(Constants.SUPER_BTN);
        super_btn.setBackgroundResource(R.drawable.roundedbutton);
        super_btn.setTypeface(typeface);

        gamePlayeSceneUI.setId(Constants.GAME_PLAY_UI);
        gamePlayeSceneUI.setVisibility(View.GONE);

        // Layout gameUI
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(175,175);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constants.PLAYER_SIZE, Constants.PLAYER_SIZE);
        params.setMargins(12,8,12,8); // TODO: what does this do??

        super_btn.setLayoutParams(params);
        att_btn.setLayoutParams(params);

        gamePlayeSceneUI.setOrientation(LinearLayout.HORIZONTAL);
        gamePlayeSceneUI.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

        //add buttons to gameUI
        gamePlayeSceneUI.addView(super_btn);
        gamePlayeSceneUI.addView(att_btn);


        /* **********************************
         *       GAME OVER SCENE UI         *
         ********************************** */
        Button restartGame_btn = new Button(this);
        Button backToLogin_btn = new Button(this);
        TextView winner_msg = new TextView(this);

        winner_msg.setText("");
        winner_msg.setId(Constants.WINNER_MSG);
        winner_msg.setTypeface(typefaceBandit);
        winner_msg.setTextColor(Color.BLACK);
        winner_msg.setTextSize(btnTextSize);
        winner_msg.setGravity(Gravity.CENTER);


        RelativeLayout.LayoutParams winner_params = new RelativeLayout.LayoutParams(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT/6);
        winner_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        winner_msg.setLayoutParams(winner_params);

        restartGame_btn.setText(R.string.restartbuttonstring);
        restartGame_btn.setId(Constants.RESTARTGAME_BTN);
        restartGame_btn.setTextColor(Color.rgb(250,250,250));
        restartGame_btn.setTypeface(typeface);
        restartGame_btn.setTextSize(btnTextSize);

        backToLogin_btn.setText(R.string.backtologinbuttonstring);
        backToLogin_btn.setId(Constants.BACK_TO_LOGIN_BTN);
        backToLogin_btn.setTextColor(Color.rgb(250,250,250));
        backToLogin_btn.setTypeface(typeface);
        backToLogin_btn.setTextSize(btnTextSize);

        RelativeLayout.LayoutParams restart_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        restart_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        restart_params.addRule(RelativeLayout.BELOW, Constants.WINNER_MSG);
        restartGame_btn.setLayoutParams(restart_params);
        restartGame_btn.setBackgroundResource(R.drawable.button);

        RelativeLayout.LayoutParams login_params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        login_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        login_params.addRule(RelativeLayout.BELOW, Constants.RESTARTGAME_BTN);
        login_params.topMargin = 10;
        backToLogin_btn.setLayoutParams(login_params);
        backToLogin_btn.setBackgroundResource(R.drawable.button);



        //add buttons to gameOverUI
        gameOverSceneUI.addView(restartGame_btn);
        gameOverSceneUI.addView(backToLogin_btn);
        gameOverSceneUI.addView(winner_msg);
        gameOverSceneUI.setId(Constants.GAME_OVER_UI);
        gameOverSceneUI.setVisibility(View.GONE);
        gameOverSceneUI.setGravity(Gravity.CENTER);



        //add views to game
        game.addView(gamePanel);
        game.addView(createGameUI);
        game.addView(startSceneUI);
        game.addView(joinGameUI);
        game.addView(gameOverSceneUI);
        game.addView(gamePlayeSceneUI);

        setContentView(game);

    }

    //to equalize music

    private void setupEqualizerFxAndUI() {
        //create equalizer object and attach it with default priority 0
        mEqualizer = new Equalizer(0, backgroundsound.getAudioSessionId());
        mEqualizer.setEnabled(true);
        short bands = mEqualizer.getNumberOfBands();
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];

        for (short i = 0; i < bands; i++) {
            final short band = i;
            SeekBar bar = new SeekBar(this);
            bar.setMax(maxEQLevel - minEQLevel);
            bar.setProgress(mEqualizer.getBandLevel(band));
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    mEqualizer.setBandLevel(band, (short) (progress + minEQLevel));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        KryoClient.close();

        if (backgroundsound != null) {
            backgroundsound.pause();
//            backgroundsound.release();
//            backgroundsound = null;
        }


        // stop the server
        stopService(new Intent(this, KryoServer.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KryoClient.close();

        if (backgroundsound != null) {
            backgroundsound.stop();
            backgroundsound.release();
            backgroundsound = null;
        }

        if (GamePanel.attacksound != null) {
            GamePanel.attacksound.stop();
            GamePanel.attacksound.release();
            GamePanel.attacksound = null;
        }

        if (GamePanel.specialattacksound != null) {
            GamePanel.specialattacksound.stop();
            GamePanel.specialattacksound.release();
            GamePanel.specialattacksound = null;
        }

        // stop the server
        stopService(new Intent(this, KryoServer.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if ((KryoClient.getClient() == null) || !KryoClient.getClient().isConnected()) {
//            KryoClient.getInstance().connect();
//        }

        if (backgroundsound != null) {
            backgroundsound.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
