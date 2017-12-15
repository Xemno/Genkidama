package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;

/**
 * Created by Qais on 13-Dec-17.
 */

public class StartScene implements Scene {

    private Activity activity;
    private Button join_btn, create_btn;

    private boolean btn_active = false;
    private int nextScene;

    private int top, right, left, bottom;

    private Animation coinAnimation;

    private Drawable genkidamaLogo;


    public StartScene(Activity activity) {
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
                Constants.SCREEN_HEIGHT/3,5, 5, false);
        coinAnimation.setFrameDuration(65);

    }


    @Override
    public void update() {

        if (!btn_active){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout startUI = (RelativeLayout) activity.findViewById(Constants.START_UI);
                    startUI.setVisibility(View.VISIBLE);
                    startUI.bringToFront();

                    join_btn = (Button) activity.findViewById(Constants.JOIN_BUTTON);
                    create_btn = (Button) activity.findViewById(Constants.CREATE_BUTTON);

                    btn_active = true;

                    join_btn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textView = (TextView) activity.findViewById(Constants.TEXT_V);
                                    textView.setText("");
                                    textView.setHint("Waiting for some action...");
                                    nextScene = Constants.Join_GAME_SCENE;
                                    terminate();
                                }
                            });

                        }
                    });

                    create_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textView = (TextView) activity.findViewById(Constants.TEXT_VIEW);
                                    textView.setText("");
                                    textView.setHint("Waiting for some action...");
                                    nextScene = Constants.CREATE_GAME_SCENE;
                                    terminate();
                                }
                            });

                        }
                    });



                }
            });
        }

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.rgb(240, 230, 140)); // BACKGROUND color


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
                RelativeLayout startUI = (RelativeLayout) activity.findViewById(Constants.START_UI);
                startUI.setVisibility(View.GONE);
                btn_active = false;
                SceneManager.ACTIVE_SCENE = nextScene;
//                coinAnimation.recycle(); // TODO: TEST

            }
        });
    }

    @Override
    public void receiveTouch(MotionEvent event) {

    }
}
