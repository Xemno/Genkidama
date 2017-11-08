package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.HealthBar;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;

/**
 * This is one specific scene. For us this might be the scene
 * where the game starts and the palyers can play.
 */

public class GamePlayScene implements Scene {

    //TODO: create the main game here

    private Player player1, player2; // player2 should be the enemy player
    private HealthBar healthbar1, healthbar2;


    public GamePlayScene() {


        /* Creating the ground on which the players move */
        healthbar1 = new HealthBar();

    }

    @Override
    public void update() {
        //TODO: update player1 and palyer2 here and any further added game objects

    }

    @Override
    public void draw(Canvas canvas) {
        //TODO: draw player1 and palyer2 here and any further added game objects

        canvas.drawColor(Color.WHITE); // BACKGROUND color
        healthbar1.draw(canvas);


    }

    @Override
    public void terminate() {
        //TODO: define what to do if this scene gets terminated

    }

    @Override
    public void receiveTouch(MotionEvent event) {
        //TODO: define what to do if touch event received, i.e., move players etc.


    }
}
