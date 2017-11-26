package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.BaseFloor;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.FLOOR_CEILING_DIST_RELATIVE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.PLAYER_PERCENTAGE_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.PLAYER_PERCENTAGE_WIDTH;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.PLAYER_SIZE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;

/**
 * This is one specific scene. For us this might be the scene
 * where the game starts and the palyers can play.
 */

public class GamePlayScene /*extends Activity*/ implements Scene{

    boolean is_healthbar_zero=false;
    private float x_old, x_new;
    static final int MAX_HEALTH = 1000; //maybe put it into player class and don't give it as an argument
    static final int MAX_CHARGE = 200;

    private Player player1, player2; // player2 should be the enemy player
    private PointF playerPoint1, playerPoint2;

    private int fixDist;
    private BaseFloor floor;
    Drawable d;

    private Activity activity;

    private boolean btn_active = false;
    private boolean collision = false;
    private boolean new_game = false;

    //TODO: make movement of player1 and player2 dependant on size of phone. Needs to be communicated with Server Messages

    public GamePlayScene(Activity activity) {
        this.activity = activity;
        initializeGame();
    }

    private void initializeGame(){

        // initialize rects first scaling with respect to width and calculate fixed floor
        Rect rect1 = new Rect(0, 0, PLAYER_SIZE, PLAYER_SIZE);
        Rect rect2 = new Rect(0, 0, PLAYER_SIZE, PLAYER_SIZE);
        fixDist = FLOOR_CEILING_DIST_RELATIVE*SCREEN_HEIGHT/100;

        player1 = new Player(rect1, Color.RED, MAX_HEALTH, MAX_HEALTH, MAX_CHARGE, 0, 0);

        //create testplayer for test
        player2 = new Player(rect2, Color.BLUE, MAX_HEALTH, MAX_HEALTH, MAX_CHARGE, 0, 1);

        //TODO: Change this to be done by server according to side and move setSide() & Bars
        // playerPoint1 = new Point(Constants.SCREEN_WIDTH/4,Constants.SCREEN_HEIGHT - FLOOR_CEILING_DIST*Constants.SCREEN_HEIGHT/100 - player1.getRectangle().height()/2);
        playerPoint1 = new PointF(25,FLOOR_CEILING_DIST_RELATIVE - PLAYER_PERCENTAGE_HEIGHT/2);
        player1.update(playerPoint1);

        //initialize enemy
        // playerPoint2 = new Point(3*Constants.SCREEN_WIDTH/4, Constants.SCREEN_HEIGHT - FLOOR_CEILING_DIST*Constants.SCREEN_HEIGHT/100 - player2.getRectangle().height()/2);
        playerPoint2 = new PointF(75, FLOOR_CEILING_DIST_RELATIVE - PLAYER_PERCENTAGE_HEIGHT/2);
        player2.update(playerPoint2);

        //initialize basefloor
        floor = new BaseFloor(fixDist);
    }


    @Override
    public void update() {
        if (new_game) {
            initializeGame();
            new_game = false;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
                super_btn.setEnabled(player1.isCharged);
            }
        });
        collision = false;
        if (!btn_active) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout gameUI = (LinearLayout) activity.findViewById(Constants.GAME_UI);
                    gameUI.setVisibility(View.VISIBLE);
                    Button att_btn = (Button) activity.findViewById(Constants.ATT_BTN);
                    Button super_btn = (Button) activity.findViewById(Constants.SUPER_BTN);
                    super_btn.setEnabled(false);
                    btn_active = true;
                    att_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            player1.attack(player2, collision);
                        }
                    });

                    // trying to do special attack
                    super_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (player1.isCharged){
                                player1.specialAttack(player2);
                            }
                        }
                    });
                }
            });

        }

        if (playerPoint1.x - PLAYER_PERCENTAGE_WIDTH /2 < 0)
            playerPoint1.x = PLAYER_PERCENTAGE_WIDTH /2;
        else if (playerPoint1.x + PLAYER_PERCENTAGE_WIDTH /2 > 100)
            playerPoint1.x = 100 - PLAYER_PERCENTAGE_WIDTH /2;

        player1.update(playerPoint1);
        player2.update(playerPoint2);


        //TODO: they never really touch, mini gap, and I don't get why
        if (player1.playerCollide(player2)) {
            playerPoint1.x = playerPoint2.x - PLAYER_PERCENTAGE_WIDTH + 0.1f;
            player1.update(playerPoint1);
            collision = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE); // BACKGROUND color

        // setting the background, this should actually already scale to any device (whole picture is on it)
        d = activity.getBaseContext().getResources().getDrawable(R.drawable.background_try);
        d.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        d.draw(canvas);

        floor.draw(canvas);
        player1.draw(canvas);
        player2.draw(canvas);

        //draw the same to temp_canvas
        GameOverScene.test = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.RGB_565);
        Canvas temp_canvas = new Canvas(GameOverScene.test);

        temp_canvas.drawColor(Color.WHITE); // BACKGROUND color
        d = activity.getBaseContext().getResources().getDrawable(R.drawable.background_try);
        d.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        d.draw(temp_canvas);

        floor.draw(temp_canvas);
        player1.draw(temp_canvas);
        player2.draw(temp_canvas);

        //if current health is zero change scene
        if(player1.getHealthbar().currHealth==0 || player2.getHealthbar().currHealth==0) {
            terminate();
        }
    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout gameUI = (LinearLayout) activity.findViewById(Constants.GAME_UI);
                gameUI.setVisibility(View.GONE);
                btn_active = false;
            }
        });
        new_game = true;
        SceneManager.ACTIVE_SCENE = Constants.GAMEOVER_SCENE;
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
                        // TODO: scale this movement to work 1:1 on a 1200-1500 pixel screen width
                        playerPoint1.set(playerPoint1.x +  (diff*100/SCREEN_WIDTH), playerPoint1.y);
                        //playerPoint1.set(playerPoint1.x + (int) (diff*SCREEN_WIDTH/30000), playerPoint1.y);
                        x_old = x_new;
                    }

                return;
            case MotionEvent.ACTION_UP:
                return;
        }
    }




}
