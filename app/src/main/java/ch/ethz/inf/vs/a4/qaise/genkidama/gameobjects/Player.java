package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;

/**
 * Created by Qais on 04-Nov-17.
 */

//TODO: NOT FINISHED


//TODO: NOT FINISHED


public class Player implements GameObject {

    private final static int MIN_DMG = 10;
    private final static int MAX_DMG = 20;
    private final static int SPECIAL_ATTACK_DMG = 100;
    static final int MAX_HEALTH = 1000;
    static final int MAX_CHARGE = 200;


    public String name;
    public int id;
    public int side;


    private int maxHealth, maxCharge;
    private int currentHealth;
    private int currentCharge = 0;
    private final int CHARGE_AMOUNT = 5;
    public boolean isCharged = false;
    public boolean isLoser = false;



//    private Rect rectangle; // for collision detection
    private int rectWidth, rectHight;
    private int color;

    public PointF new_point, old_point;  // ###

    public HealthBar healthbar;
    public ChargeBar chargebar;

    // TODO: finish these animations, hitbox not yet right position
    Animation animation;
    Animation walk_right, walk_left;
    Animation attack_left, attack_right;
    Animation block_left, block_right;
    Animation idle_left, idle_right;
    Animation death_left, death_right;


    boolean walkInX = false; // false = -x, true = +x

    public Player(int id, PointF point, int side) {

        /* Initialize settings of this player */
        //|--------------------------------------------|//
        this.new_point = point;
        this.old_point = point;
        this.id = id;
        this.maxHealth = MAX_HEALTH;
        this.currentHealth = MAX_HEALTH;
        this.maxCharge = MAX_CHARGE;
        this.healthbar = new HealthBar(this); //TODO: uncomment for use
        this.chargebar = new ChargeBar(this); //TODO: uncomment for use
        this.side = side;
        //|--------------------------------------------|//

        if (side % 2 != 0) {
            walkInX = true;  // if on the left side, animate idle_right
        } // otherwise false anyways and animate idle_left


        /* Draw Player as a fixed Rectangle with random color */
        //|--------------------------------------------|//
//        color = Color.rgb(
//                GamePanel.getRandom(30, 255),
//                GamePanel.getRandom(30, 255),
//                GamePanel.getRandom(30, 255)
//        );

//        rectangle = new Rect(point.x, point.y, point.x + 42*8, point.y + 42*8);
//        rectWidth = rectangle.width()/2;
//        rectHight = rectangle.height()/2;
//        rectangle.set(  point.x - rectWidth,  point.y - rectHight,
//                point.x + rectWidth, point.y + rectHight);
//        //|--------------------------------------------|//


        // TODO: NOTE - In allen Sprite Sheets vom Knight sind die Füsse um 3 pixel vom Boden entfernt! Lösen....

        idle_right = new Animation(
                MainActivity.context,
                R.drawable.knight_idle_right,
                42, 42,
                4,
                point.x, point.y,
                true);
        idle_right.scaleBitmap(8);
        idle_right.forward = true;      // always true for right animations

        idle_left = new Animation(
                MainActivity.context,
                R.drawable.knight_idle_left,
                42, 42,
                4,
                point.x, point.y,
                true);
        idle_left.scaleBitmap(8);
        idle_left.forward = false;      // always false for left animations


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

       //declarations of other animations
        attack_left=new Animation(MainActivity.context,
                R.drawable.knight_attack_left,
                42,42,
                10,point.x, point.y,
                true);
        attack_left.scaleBitmap(8);
        attack_left.forward=false;

        attack_right=new Animation(MainActivity.context,
                R.drawable.knight_attack_right,
                42, 42,
                10, point.x, point.y,
                true);

        attack_right.scaleBitmap(8);

        block_left=new Animation(MainActivity.context,
                R.drawable.knight_block_left,
                42,42,
                7,point.x,point.y,
                true);
        block_left.scaleBitmap(8);
        block_left.forward=false;

        block_right=new Animation(MainActivity.context,
                R.drawable.knight_block_right,
                42,42,
                7,point.x,point.y,
                true);
        block_right.scaleBitmap(8);

        death_left=new Animation(MainActivity.context,
                R.drawable.knight_death_left,
                42,42,
                9, point.x, point.y,
                true);
        death_left.scaleBitmap(8);
        death_left.forward=false;

        death_right=new Animation(MainActivity.context,
                R.drawable.knight_death_right,
                42,42,
                9, point.x, point.y,
                true);
        death_right.scaleBitmap(8);


    }
    //Animation idle_right, idle_left;




    public void attack(Player enemy){
        if (this.collidesWith(enemy)) { // only attack if collision !!!
            // TODO
            //TODO: Music of normalattack -> where to declare the media player
            int damage = GamePanel.getRandom(MIN_DMG, MAX_DMG);
            if (currentCharge > maxCharge - CHARGE_AMOUNT) {
                currentCharge = maxCharge;
                isCharged = true;
            } else {
                currentCharge += CHARGE_AMOUNT;
            }

            int health = enemy.currentHealth - damage;
            if (health > 0) {
                KryoClient.attack(enemy, damage);
            }

        }
    }

    public void specialAttack(Player enemy) {
        if (this.collidesWith(enemy)) { // only attack if collision !!!
            // TODO
            //TODO: here music of attacksound
        }
    }

    @Override
    public void draw(Canvas canvas) {
//        Paint paint = new Paint(); // The Paint class holds the style and color information about how to draw geometries, text and bitmaps
//        paint.setColor(color);
//        paint.setAlpha(200);
//        canvas.drawRect(rectangle, paint);


        healthbar.draw(canvas); //TODO: uncomment for use
        chargebar.draw(canvas); //TODO: uncomment for use


        animation.draw(canvas);

    }

    @Override
    public void update() {
        // you can leave this empty if you don't need it
    }

    public void update(PointF point) {
        // (left, top, right, bottom)
        this.new_point = point;

//        rectangle.set(point.x - rectWidth, point.y - rectHight*2,
//                point.x + rectWidth, point.y);

        if (new_point.x < old_point.x) {
            walkInX = false;
            walk_left.setWhereToDraw((new_point.x) , (new_point.y)); // scale und frame dimension abziehen
            animation = walk_left;
        } else if (new_point.x > old_point.x) {
            walkInX = true;
            walk_right.setWhereToDraw((new_point.x) , (new_point.y)); // scale und frame dimension abziehen
            animation = walk_right;
        } else if (new_point.x == old_point.x) {
            if (walkInX){
                idle_right.setWhereToDraw((new_point.x) , (new_point.y));
                animation = idle_right;
            } else {
                idle_left.setWhereToDraw((new_point.x) , (new_point.y));
                animation = idle_left;
            }
        }


        if(currentHealth == 0){
            isLoser = true;
        }

        old_point = new_point;

    }

    public void update(int damage) {
        this.currentHealth = this.currentHealth - damage;
    }

    public void setColor (int color) {
        this.color = color;
    }

//    public Rect getRectangle() {
//        return rectangle; // for collision detection
//    }

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
        if (enemy == null) return false;
        return RectF.intersects( animation.getWhereToDraw(), enemy.animation.getWhereToDraw());
//        return Rect.intersects(rectangle, enemy.getRectangle()) || (rectangle.right >= enemy.getRectangle().left);

    }
}
