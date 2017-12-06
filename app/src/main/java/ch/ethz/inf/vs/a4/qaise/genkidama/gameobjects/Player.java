package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;


/**
 * Created by Qais on 04-Nov-17.
 */

//TODO: NOT FINISHED


public class Player implements GameObject {

    private final static int MIN_DMG = 10;
    private final static int MAX_DMG = 20;
    private final static int SPECIAL_ATTACK_DMG = 100;
    static final int MAX_HEALTH = 1000;
    static final int MAX_CHARGE = 200;


    public String name;
    public int id;

    private int maxHealth, maxCharge;
    private int currentHealth;
    private int currentCharge = 0;
    private final int CHARGE_AMOUNT = 5;
    public boolean isCharged = false;
    public boolean isLooser = false;


    private Rect rectangle; // for collision detection
    private int rectWidth, rectHight;
    private int color;

    public Point new_point, old_point;

    private HealthBar healthbar;
    private ChargeBar chargebar;

    Animation walk_right, walk_left; // TODO: finish these animations, hitbox not yet right position
    // TODO: add the other animations too

    boolean walkInX = false; // false = -x, true = +x

    public Player(int id, Point point) {

        /* Initialize settings of this player */
        //|--------------------------------------------|//
        this.new_point = point;
        this.old_point = point;
        this.id = id;
        this.maxHealth = MAX_HEALTH;
        this.currentHealth = MAX_HEALTH;
        this.maxCharge = MAX_CHARGE;
//        this.healthbar = new HealthBar(this); //TODO: uncomment for use
//        this.chargebar = new ChargeBar(this); //TODO: uncomment for use
        //|--------------------------------------------|//


        /* Draw Player as a fixed Rectangle with random color */
        //|--------------------------------------------|//
        color = Color.rgb(
                GamePanel.getRandom(30, 255),
                GamePanel.getRandom(30, 255),
                GamePanel.getRandom(30, 255)
        );
        rectangle = new Rect(point.x, point.y, point.x + 42*8, point.y + 42*8);
        rectWidth = rectangle.width()/2;
        rectHight = rectangle.height()/2;
        rectangle.set(  point.x - rectWidth,  point.y - rectHight,
                point.x + rectWidth, point.y + rectHight);
        //|--------------------------------------------|//


        walk_right = new Animation(
                MainActivity.context,
                R.drawable.knight_walk_right_42x42,
                42, 42,
                8,
                point.x, point.y,
                true);
        walk_right.scaleBitmap(8);
        walk_right.forward = true;

        walk_left = new Animation(
                MainActivity.context,
                R.drawable.knight_walk_left_42x42,
                42, 42,
                8,
                point.x, point.y,
                true);
        walk_left.scaleBitmap(8);
        walk_left.forward = false;

    }


    public void attack(Player enemy){
        if (this.collidesWith(enemy)) { // only attack if collision !!!
            // TODO
        }
    }

    public void specialAttack(Player enemy) {
        if (this.collidesWith(enemy)) { // only attack if collision !!!
            // TODO
        }
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint(); // The Paint class holds the style and color information about how to draw geometries, text and bitmaps
        paint.setColor(color);
        paint.setAlpha(200);
        canvas.drawRect(rectangle, paint);
//        healthbar.draw(canvas); //TODO: uncomment for use
//        chargebar.draw(canvas); //TODO: uncomment for use



        if (walkInX) {
            walk_right.draw(canvas);
        } else {
            walk_left.draw(canvas);
        }
//        walk_right.draw(canvas);

    }

    @Override
    public void update() {
        // you can leave this empty if you don't need it
    }

    public void update(Point point) {
        // (left, top, right, bottom)
        this.new_point = point;

        rectangle.set(  point.x - rectWidth,  point.y - rectHight,
                point.x + rectWidth, point.y + rectHight);

        if (new_point.x < old_point.x) {
            walkInX = false;
            walk_left.setWhereToDraw((float)(new_point.x - 42*8) , (float) (new_point.y - 42*8)); // scale und frame dimension abziehen
        } else {
            walkInX = true;
            walk_right.setWhereToDraw((float)(new_point.x - 42*8) , (float) (new_point.y - 42*8)); // scale und frame dimension abziehen
        }



        if(currentHealth == 0){
            isLooser = true;
        }

        old_point = new_point;

    }

    public void update(int damage) {
        this.currentHealth = this.currentHealth - damage;
    }

    public void setColor (int color) {
        this.color = color;
    }

    public Rect getRectangle() {
        return rectangle; // for collision detection
    }

    public int getMaxHealth(){
        return maxHealth;
    }

    public int getCurrentHealth(){
        return currentHealth;
    }

    public int getMaxCharge() {
        return maxCharge;
    }

    public int getCurrentCharge() {
        return currentCharge;
    }

    public void setCurrentHealth(int health){
        currentHealth = health;
    }

    public HealthBar getHealthbar(){
        return healthbar;
    }

    public boolean collidesWith(Player enemy){
        return Rect.intersects(rectangle, enemy.getRectangle()) || (rectangle.right >= enemy.getRectangle().left);
    }
}
