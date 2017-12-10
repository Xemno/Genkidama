package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.animation.Animation;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;
import ch.ethz.inf.vs.a4.qaise.genkidama.network.KryoClient;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.MAX_CHARGE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.MAX_HEALTH;


/**
 * Created by Qais on 04-Nov-17.
 */

//TODO: NOT FINISHED


//TODO: NOT FINISHED


public class Player implements GameObject {

    private final static int MIN_DMG = 10;
    private final static int MAX_DMG = 20;
    private final static int SPECIAL_ATTACK_DMG = 100;



    public String name;
    public int id;
    public int side;


    private int maxHealth, maxCharge;
    private int currentHealth;
    private int currentCharge = 0;
    public boolean isCharged = false;
    public boolean isLoser = false;



//    private Rect rectangle; // for collision detection
    private int rectWidth, rectHight;
    private int color;

    public PointF new_point, old_point;  // ###

    public HealthBar healthbar;
    public ChargeBar chargebar;

    //Done LARA: variables
    boolean block;
    boolean attack;
    boolean special_attack;
    boolean is_dead;
    //Done LARA

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
        this.side = side; // Jela did this
        this.healthbar = new HealthBar(this);
        this.chargebar = new ChargeBar(this);
        //|--------------------------------------------|//


        idle_right = new Animation(
                MainActivity.context,
                R.drawable.knight_idle_right,
                42, 42,
                4,
                point.x, point.y,
                true, 8);
//        idle_right.scaleBitmap(8);
        idle_right.forward = true;      // always true for right animations

        idle_left = new Animation(
                MainActivity.context,
                R.drawable.knight_idle_left,
                42, 42,
                4,
                point.x, point.y,
                true, 8);
//        idle_left.scaleBitmap(8);
        idle_left.forward = false;      // always false for left animations


        walk_right = new Animation(
                MainActivity.context,
                R.drawable.knight_walk_right_42x42,
                42, 42,
                8,
                point.x, point.y,
                true, 8);
//        walk_right.scaleBitmap(8);
        walk_right.forward = true;

        walk_left = new Animation(
                MainActivity.context,
                R.drawable.knight_walk_left_42x42,
                42, 42,
                8,
                point.x, point.y,
                true, 8);
//        walk_left.scaleBitmap(8);
        walk_left.forward = false;

/*
        //Done Lara: frameWidth: 80
        attack_left=new Animation(MainActivity.context,
                R.drawable.knight_attack_left,
                80,42,
                10,point.x, point.y,
                true);
        attack_left.scaleBitmap(8);
        attack_left.forward=false;

        attack_right=new Animation(MainActivity.context,
                R.drawable.knight_attack_right,
                80, 42,
                10, point.x, point.y,
                true);

        attack_right.scaleBitmap(8);
        //Done Lara: frameWidth: 80

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
*/



        if (side % 2 != 0) {
            walkInX = true;  // if on the left side, animate idle_right
            animation = idle_right;
        } // otherwise false anyways and animate idle_left

    }

    public void attack(Player enemy){
        if (this.collidesWith(enemy)) { // only attack if collision !!!
            int damage = GamePanel.getRandom(MIN_DMG, MAX_DMG);
            KryoClient.attack(enemy, damage);
        }
    }

    public void specialAttack(Player enemy) {
        if (this.collidesWith(enemy)) { // only attack if collision !!!
            // TODO
        }
    }

    @Override
    public void draw(Canvas canvas) {

        healthbar.draw(canvas);
        chargebar.draw(canvas);
        animation.draw(canvas);

    }

    @Override
    public void update() {
        // you can leave this empty if you don't need it
    }

    public void update(PointF point) {
        // (left, top, right, bottom)
        this.new_point = point;

        // Qais: added old version again
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

        //TODO Lara attack, special_attack, block, is_dead put into code and look that only one can be true at each time.
        //TODO LARA: Animation ( Qais: please revise, the following does not work..)
//        if (new_point.x < old_point.x) {
//            walkInX = false;
//        } else if (new_point.x > old_point.x) {
//            walkInX = true;
//        }
//
//        if (!attack && !special_attack &&!block && !is_dead){
//            if (!walkInX) {
//                walk_left.setWhereToDraw((new_point.x) , (new_point.y)); // scale und frame dimension abziehen
//                animation = walk_left;
//            } else if (walkInX) {
//                walk_right.setWhereToDraw((new_point.x) , (new_point.y)); // scale und frame dimension abziehen
//                animation = walk_right;
//            } else if (new_point.x == old_point.x) {
//                if (walkInX){
//                    idle_right.setWhereToDraw((new_point.x) , (new_point.y));
//                    animation = idle_right;
//                } else {
//                    idle_left.setWhereToDraw((new_point.x) , (new_point.y));
//                    animation = idle_left;
//                }
//            }
//        }else if(attack && !special_attack && !block && !is_dead && walkInX){
//            attack_right.setWhereToDraw((new_point.x),(new_point.y));
//            animation = attack_right;
//        }else if(!attack && special_attack && !block && !is_dead && walkInX){
//            attack_right.setWhereToDraw((new_point.x),(new_point.y));
//            animation = attack_right;
//        }else if(!attack && !special_attack && block && !is_dead && walkInX){
//            block_left.setWhereToDraw((new_point.x),(new_point.y));
//            animation = block_left;
//        }else if(!attack && !special_attack && !block && is_dead && walkInX){
//            death_left.setWhereToDraw((new_point.x),(new_point.y));
//            animation = death_left;
//        }else if(attack && !special_attack && !block && !is_dead && !walkInX){
//            attack_left.setWhereToDraw((new_point.x),(new_point.y));
//            animation = attack_left;
//        }else if(!attack && special_attack && !block && !is_dead && !walkInX){
//            attack_left.setWhereToDraw((new_point.x),(new_point.y));
//            animation = attack_left;
//        }else if(!attack && !special_attack && block && !is_dead && !walkInX){
//            block_right.setWhereToDraw((new_point.x),(new_point.y));
//            animation = block_right;
//        }else if(!attack && !special_attack &&! block && is_dead && !walkInX){
//            death_right.setWhereToDraw((new_point.x),(new_point.y));
//            animation = death_right;
//        }



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

    public void setCurrentCharge(int charge){
        currentCharge = charge;
    }

    public void setCurrentHealth(int health){
        currentHealth = health;
    }

    public HealthBar getHealthbar(){
        return healthbar;
    }

    public boolean collidesWith(Player enemy){
        if (enemy == null) return false;
        return RectF.intersects(animation.getWhereToDraw(), enemy.animation.getWhereToDraw());
    }
}
