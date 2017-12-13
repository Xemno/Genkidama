package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;



    /*  What we do:
    *   We have two Rects: rectHealth and rectBorder.
    *   rectBorder is the black "background" rectangle, which will also indicate how much health is missing
    *   rectHealth is the green health border, which indicates how much health is still left
    *
    *   A third rectMissing can be added quickly
    * */

public class HealthBar implements GameObject {
    private Player player;                   // player associated with this healthbar
    public int currHealth, MaxHealth;       // health data of the player

    private Rect rectHealth;                 // health rectangle r=[0-100%]
    private Rect rectBorder;                 // missing health rectangle 1-r

    // paint values
    private int colorHealth;                 // color for health r

    private final int COLOR_BORDER = Color.BLACK;                 // color for missing health 1-r
    private final int ALPHA_HEALTH = 200;      // [0...255] // actual health
    private final int ALPHA_BORDER = 200;      // [0...255] // missing health

    // fixed position values, i.e. percentage of screen something should be
    /*private final int GAP_SIDE_PART = 16; // e.g. gap is one 16th of the screen
    private final int GAP_TOP_PART = 8;
    private final int BACKGROUND_WIDTH_PART = 3;
    private final int BACKGROUND_HEIGHT_PART = 12;
    private final int BORDER_SIZE = 10;*/
    private final int GAP_SIDE_PART = 20; // e.g. gap is one 16th of the screen
    private final int GAP_TOP_PART = 16;
    private final int BACKGROUND_WIDTH_PART = 3;
    private final int BACKGROUND_HEIGHT_PART = 14;
    private final int BORDER_SIZE = 6;

    // relative position values:
    private int gapSide, gapTop;                                // parameters for drawing: position and size
    private int backgroundWidth, backgroundHeight, healthWidth;

    private int side;


    public HealthBar(Player player) {
        // Get player data
        this.player = player;
        this.side = player.side;
        currHealth = player.getCurrentHealth();
        MaxHealth = player.getMaxHealth();

//        side = player.getSide();

        colorHealth = Color.rgb(51, 204, 51);

        // Scaling size and position of the background part with final parameters above
        gapSide = Constants.SCREEN_WIDTH/GAP_SIDE_PART;        // where you want the healthbar
        gapTop = Constants.SCREEN_HEIGHT/GAP_TOP_PART;
        backgroundWidth = Constants.SCREEN_WIDTH/ BACKGROUND_WIDTH_PART;         // how large it should be
        backgroundHeight = Constants.SCREEN_HEIGHT/BACKGROUND_HEIGHT_PART;

        // Health part has 2 pixel border and following width
        healthWidth = currHealth* backgroundWidth /MaxHealth;

        makeRect();
    }

    //TODO: Change that...work with ID instead of 'side'
    private void makeRect(){
        // Distinguish on which side the player starts and initialize symmetrically
        if(side == 1) {
            // display on left side
            rectBorder = new Rect(gapSide, gapTop,
                    gapSide + 2*BORDER_SIZE + backgroundWidth, gapTop + backgroundHeight + 2*BORDER_SIZE);
            rectHealth = new Rect(gapSide + BORDER_SIZE, gapTop + BORDER_SIZE,
                    gapSide + BORDER_SIZE + healthWidth, gapTop + BORDER_SIZE + backgroundHeight);
        } else /*if(side == 2)*/ {
            // display on right side
            rectBorder = new Rect(Constants.SCREEN_WIDTH - gapSide - 2*BORDER_SIZE - backgroundWidth, gapTop,
                    Constants.SCREEN_WIDTH - gapSide, gapTop + backgroundHeight + 2*BORDER_SIZE);
            /*rectHealth = new Rect(Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - backgroundWidth, gapTop + BORDER_SIZE,
                    Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - backgroundWidth + healthWidth, gapTop + BORDER_SIZE + backgroundHeight);*/
            rectHealth = new Rect (Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - healthWidth, gapTop + BORDER_SIZE,
                    Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE, gapTop + BORDER_SIZE + backgroundHeight);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(COLOR_BORDER);
        paint.setAlpha(ALPHA_BORDER);
        canvas.drawRect(rectBorder, paint);

        paint.setColor(colorHealth);
        paint.setAlpha(ALPHA_HEALTH);
        canvas.drawRect(rectHealth, paint);
    }

    @Override
    public void update() {
        currHealth = player.getCurrentHealth();
        if (currHealth > MaxHealth/2) colorHealth = Color.rgb(51, 204, 51);
        else if (currHealth > 3*MaxHealth/10) colorHealth = Color.rgb(204, 204, 51);
        else if (currHealth > 15*MaxHealth/100) colorHealth = Color.rgb(204, 128, 51);
        else if (currHealth > 8*MaxHealth/100) colorHealth = Color.rgb(204, 51, 51);
        else colorHealth = Color.RED;

        healthWidth = currHealth* backgroundWidth /MaxHealth;

        if(side == 1)
            rectHealth.right = gapSide + BORDER_SIZE + healthWidth;
        else
            rectHealth.left = Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - healthWidth;
    }
}

