package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

// same as Healthbar with renamed

public class ChargeBar implements GameObject {
    private Player player;                   // player associated with this charge bar
    private int currCharge, MaxCharge;       // charge data of the player

    private Rect rectCharge;                 // charge rectangle r=[0-100%]
    private Rect rectBorder;                 // missing charge rectangle 1-r

    // paint values
    // private final int COLOR_CHARGE = Color.rgb(218, 165, 32);                // color for charge r
    // private final int COLOR_BORDER = Color.rgb(255, 228, 181);                 // color for missing charge 1-r
    private final int COLOR_CHARGE = Color.WHITE;                // color for charge r
    private final int COLOR_BORDER = Color.rgb(121, 27, 25);                 // color for missing charge 1-r
    private final int ALPHA_CHARGE = 200;      // [0...255] // actual charge
    private final int ALPHA_BORDER = 200;      // [0...255] // missing charge

    // fixed position values, i.e. percentage of screen something should be
    /*private final int GAP_SIDE_PART = 16; // e.g. gap is one 16th of the screen
    private final int GAP_TOP_PART = 4;
    private final int BACKGROUND_WIDTH_PART = 4;
    private final int BACKGROUND_HEIGHT_PART = 24;
    private final int BORDER_SIZE = 6;*/
    private final int GAP_SIDE_PART = 20; // e.g. gap is one 16th of the screen
    private final int GAP_TOP_PART = 6;
    private final int BACKGROUND_WIDTH_PART = 4;
    private final int BACKGROUND_HEIGHT_PART = 28;
    private final int BORDER_SIZE = 5;


    // relative position values:
    private int gapSide, gapTop;                                // parameters for drawing: position and size
    private int backgroundWidth, backgroundHeight, chargeWidth;

    private int side;

    public ChargeBar(Player player) {
        // Get player data
        this.player = player;
        this.side = player.side;
        currCharge = player.getCurrentCharge();
        MaxCharge = player.getMaxCharge();
//        side = player.getSide();

        // Scaling size and position of the background part with final parameters above
        gapSide = Constants.SCREEN_WIDTH/GAP_SIDE_PART;        // where you want the charge bar
        gapTop = Constants.SCREEN_HEIGHT/GAP_TOP_PART;
        backgroundWidth = Constants.SCREEN_WIDTH/ BACKGROUND_WIDTH_PART;         // how large it should be
        backgroundHeight = Constants.SCREEN_HEIGHT/ BACKGROUND_HEIGHT_PART;

        // Health part has 2 pixel border and following width
        chargeWidth = currCharge * backgroundWidth /MaxCharge;

        makeRect();
    }

    //TODO: Change that...
    private void makeRect(){
        // Distinguish on which side the player starts and initialize symmetrically
        if(side == 1) {
            // display on left side
            rectBorder = new Rect(gapSide, gapTop,
                    gapSide + 2*BORDER_SIZE + backgroundWidth, gapTop + backgroundHeight + 2*BORDER_SIZE);
            rectCharge = new Rect(gapSide + BORDER_SIZE, gapTop + BORDER_SIZE,
                    gapSide + BORDER_SIZE + chargeWidth, gapTop + BORDER_SIZE + backgroundHeight);
        } else {
            // display on right side
            rectBorder = new Rect(Constants.SCREEN_WIDTH - gapSide - 2*BORDER_SIZE - backgroundWidth, gapTop,
                    Constants.SCREEN_WIDTH - gapSide, gapTop + backgroundHeight + 2*BORDER_SIZE);
            /*rectCharge = new Rect(Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - backgroundWidth, gapTop + BORDER_SIZE,
                    Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - backgroundWidth + chargeWidth, gapTop + BORDER_SIZE + backgroundHeight);*/
            rectCharge = new Rect (Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - chargeWidth, gapTop + BORDER_SIZE,
                    Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE, gapTop + BORDER_SIZE + backgroundHeight);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(COLOR_BORDER);
        paint.setAlpha(ALPHA_BORDER);
        canvas.drawRect(rectBorder, paint);

        paint.setColor(COLOR_CHARGE);
        paint.setAlpha(ALPHA_CHARGE);
        canvas.drawRect(rectCharge, paint);
    }

    @Override
    public void update() {
        currCharge = player.getCurrentCharge();
        chargeWidth = currCharge* backgroundWidth / MaxCharge;
        if (side == 1)
            rectCharge.right = gapSide + BORDER_SIZE + chargeWidth;
        else
            rectCharge.left = Constants.SCREEN_WIDTH - gapSide - BORDER_SIZE - chargeWidth;
    }
}
