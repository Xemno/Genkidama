package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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

        FrameLayout game = new FrameLayout(this);
        GamePanel gamePanel = new GamePanel(this, this);
        LinearLayout gameUI = new LinearLayout(this);
        Button att_btn = new Button(this);
        Button super_btn = new Button(this);


        att_btn.setText(R.string.att_string);
        att_btn.setId(Constants.ATT_BTN);
        att_btn.setBackgroundResource(R.drawable.roundedbutton);
        att_btn.setVisibility(Button.GONE);

        super_btn.setText(R.string.special);
        super_btn.setId(Constants.SUPER_BTN);
        super_btn.setBackgroundResource(R.drawable.roundedbutton);
        super_btn.setVisibility(Button.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(175,175);
        params.setMargins(12,8,12,8);

        super_btn.setLayoutParams(params);
        att_btn.setLayoutParams(params);

        gameUI.setOrientation(LinearLayout.HORIZONTAL);
        gameUI.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

        gameUI.addView(super_btn);
        gameUI.addView(att_btn);


        game.addView(gamePanel);
        game.addView(gameUI);

        setContentView(game);
        //new GamePanel(this);
    }
}
