package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.HealthBar;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;

import static java.security.AccessController.getContext;

/**
 * This is one specific scene. For us this might be the scene
 * where the game starts and the palyers can play.
 */

public class GamePlayScene /*extends Activity*/ implements Scene{

    //TODO: create the main game here

    private float x_old, x_new;
    //static final int MIN_DIST = 200;
    static final int MAX_HEALTH = 1000; //maybe put it into player class and don't give it as an argument

    private Player player1, player2; // player2 should be the enemy player
    private HealthBar healthbar1, healthbar2;
    private Point playerPoint1, playerPoint2;
    static final int FLOOR_HEIGHT = 200; //TODO: replace it with value from Constants class


    private Activity activity;

    private boolean btn_active = false;
    private boolean collision = false;

    //TODO: make movement of player1 and player2 dependant on size of phone. Needs to be communicated with Server Messages


    public GamePlayScene(Activity activity) {
        this.activity = activity;

        /* Creating the ground on which the players move */
        healthbar1 = new HealthBar();
        player1 = new Player(new Rect(0,0,200,200), Color.RED, MAX_HEALTH, MAX_HEALTH);

        //create testplayer for test
        healthbar2 = new HealthBar();
        player2 = new Player(new Rect(200,200,400,400), Color.BLUE, MAX_HEALTH, MAX_HEALTH);

        playerPoint1 = new Point(Constants.SCREEN_WIDTH/4,Constants.SCREEN_HEIGHT - FLOOR_HEIGHT - player1.getRectangle().height()/2);
        player1.update(playerPoint1);

        //initialise enemy
        playerPoint2 = new Point(3*Constants.SCREEN_WIDTH/4, Constants.SCREEN_HEIGHT - FLOOR_HEIGHT - player2.getRectangle().height()/2);
        player2.update(playerPoint2);
    }


    @Override
    public void update() {
        collision = false;
        if (!btn_active) {
            Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
            att_btn.setVisibility(Button.VISIBLE);
            Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
            super_btn.setVisibility(Button.VISIBLE);
            btn_active = true;
            att_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (collision){
                        player1.attack(player2, collision);
                    }
                }
            });
        }
        if (playerPoint1.x - player1.getRectangle().width()/2 < 0)
            playerPoint1.x = player1.getRectangle().width()/2;
        else if (playerPoint1.x + player1.getRectangle().width()/2 > Constants.SCREEN_WIDTH)
            playerPoint1.x = Constants.SCREEN_WIDTH - player1.getRectangle().width()/2;

        player1.update(playerPoint1);
        player2.update(playerPoint2);
        if (player1.playerCollide(player2)) {
            playerPoint1.x = playerPoint2.x - player2.getRectangle().width()/2 - player1.getRectangle().width()/2;
            player1.update(playerPoint1);
            collision = true;
        }

    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE); // BACKGROUND color
        healthbar1.draw(canvas);
        player1.draw(canvas);
        healthbar2.draw(canvas);
        player2.draw(canvas);
        //System.out.println(player2.getCurrentHealth());
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        canvas.drawText(String.valueOf(player2.getCurrentHealth()), Constants.SCREEN_HEIGHT/2, 500, paint);

    }

    @Override
    public void terminate() {
        //TODO: define what to do if this scene gets terminated
        Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
        att_btn.setVisibility(Button.INVISIBLE);
        Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
        super_btn.setVisibility(Button.INVISIBLE);
        btn_active = false;

    }

    @Override
    public void receiveTouch(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                x_old = event.getX();

                return;
            case MotionEvent.ACTION_MOVE:
                    x_new = event.getX();
                    float diff = x_new - x_old;
                    if (Math.abs(diff) > 5) {
                        playerPoint1.set(playerPoint1.x + (int) diff, playerPoint1.y);
                        x_old = x_new;
                    } else {
                        // does not work like this, tap also not recognized
                        //player1.attack(player2, player1.playerCollide(player2));
                    }

                return;
            case MotionEvent.ACTION_UP:
                return;
        }


    }



}
