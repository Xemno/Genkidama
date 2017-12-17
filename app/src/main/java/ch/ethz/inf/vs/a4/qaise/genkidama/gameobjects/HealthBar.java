package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

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

    private final int GAP_SIDE_PART = 20; // e.g. gap is one 16th of the screen
    private final int GAP_TOP_PART = 16;
    private final int BACKGROUND_WIDTH_PART = 3;
    private final int BACKGROUND_HEIGHT_PART = 20; //14
    private final int borderSize; // = 6;

    //TODO: set values in a nice looking way
    private final int GAP_TOP_PART_LOW  = 4;

    // relative position values:
    private int gapSide, gapTop, gapTopLow;                                // parameters for drawing: position and size
    private int backgroundWidth, backgroundHeight, healthWidth;

    private int side;


    // for the name:
    private int gapTopText, gapTopTextLow;
    final int fontSize = Constants.SCREEN_HEIGHT/24;
    Typeface font = Typeface.create("Arial", Typeface.BOLD);
    private String name;


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
        gapTopLow = Constants.SCREEN_HEIGHT/GAP_TOP_PART_LOW;
        borderSize = Constants.SCREEN_HEIGHT/180;

        backgroundWidth = Constants.SCREEN_WIDTH/ BACKGROUND_WIDTH_PART;         // how large it should be
        backgroundHeight = Constants.SCREEN_HEIGHT/BACKGROUND_HEIGHT_PART;

        // Health part has 2 pixel border and following width
        healthWidth = currHealth* backgroundWidth /MaxHealth;

        // for the name text
        gapTopText = Constants.SCREEN_HEIGHT/20;
        gapTopTextLow = 19*Constants.SCREEN_HEIGHT/80;    // /4;
        this.name = player.name;

        makeRect();
    }

    //TODO: Change that...work with ID instead of 'side'
    // TODO: But why?, ID could have distances bigger than 1, so modulus wouldn't work anymore
    private void makeRect(){
        // Distinguish on which side the player starts and initialize symmetrically

        switch (side) {
            case 1: // top left
                rectBorder = new Rect(gapSide, gapTop,
                        gapSide + 2* borderSize + backgroundWidth, gapTop + backgroundHeight + 2* borderSize);
                rectHealth = new Rect(gapSide + borderSize, gapTop + borderSize,
                        gapSide + borderSize + healthWidth, gapTop + borderSize + backgroundHeight);
                break;
            case 2: // top right
                rectBorder = new Rect(Constants.SCREEN_WIDTH - gapSide - 2* borderSize - backgroundWidth, gapTop,
                        Constants.SCREEN_WIDTH - gapSide, gapTop + backgroundHeight + 2* borderSize);
                rectHealth = new Rect (Constants.SCREEN_WIDTH - gapSide - borderSize - healthWidth, gapTop + borderSize,
                        Constants.SCREEN_WIDTH - gapSide - borderSize, gapTop + borderSize + backgroundHeight);
                break;
            case 3: // bottom left
                rectBorder = new Rect(gapSide, gapTopLow,
                        gapSide + 2* borderSize + backgroundWidth, gapTopLow + backgroundHeight + 2* borderSize);
                rectHealth = new Rect(gapSide + borderSize, gapTopLow + borderSize,
                        gapSide + borderSize + healthWidth, gapTopLow + borderSize + backgroundHeight);
                break;
            case 4: // bottom right
                rectBorder = new Rect(Constants.SCREEN_WIDTH - gapSide - 2* borderSize - backgroundWidth, gapTopLow,
                        Constants.SCREEN_WIDTH - gapSide, gapTopLow + backgroundHeight + 2* borderSize);
                rectHealth = new Rect (Constants.SCREEN_WIDTH - gapSide - borderSize - healthWidth, gapTopLow + borderSize,
                        Constants.SCREEN_WIDTH - gapSide - borderSize, gapTopLow + borderSize + backgroundHeight);
                break;
        }

        /*if(side == 1) {
            // display on left side
            rectBorder = new Rect(gapSide, gapTop,
                    gapSide + 2*borderSize + backgroundWidth, gapTop + backgroundHeight + 2*borderSize);
            rectHealth = new Rect(gapSide + borderSize, gapTop + borderSize,
                    gapSide + borderSize + healthWidth, gapTop + borderSize + backgroundHeight);
        } else  {
            // display on right side
            rectBorder = new Rect(Constants.SCREEN_WIDTH - gapSide - 2*borderSize - backgroundWidth, gapTop,
                    Constants.SCREEN_WIDTH - gapSide, gapTop + backgroundHeight + 2*borderSize);
            rectHealth = new Rect (Constants.SCREEN_WIDTH - gapSide - borderSize - healthWidth, gapTop + borderSize,
                    Constants.SCREEN_WIDTH - gapSide - borderSize, gapTop + borderSize + backgroundHeight);
        }*/
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTypeface(font);
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);

        switch (side) {
            case 1:
                canvas.drawText(name, gapSide, gapTopText, paint);
                break;
            case 2:
                canvas.drawText(name, Constants.SCREEN_WIDTH - gapSide - backgroundWidth, gapTopText, paint);
                break;
            case 3:
                canvas.drawText(name, gapSide, gapTopTextLow, paint);
                break;
            case 4:
                canvas.drawText(name, Constants.SCREEN_WIDTH - gapSide - backgroundWidth, gapTopTextLow, paint);
                break;
        }

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

        if(side == 1 || side == 3)
            rectHealth.right = gapSide + borderSize + healthWidth;
        else
            rectHealth.left = Constants.SCREEN_WIDTH - gapSide - borderSize - healthWidth;
    }
}

