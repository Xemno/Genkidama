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

    public PointF new_point, old_point;  // ###

    public HealthBar healthbar;
    public ChargeBar chargebar;

    // TODO: finish these animations, hitbox not yet right position
    public Animation animation;
    public Animation walk_right, walk_left;
    public Animation attack_left, attack_right;
    public Animation block_left, block_right;
    public Animation idle_left, idle_right;
    public Animation death_left, death_right;
    public Animation special_attack_left, special_attack_right;


    public boolean walkInX = false; // false = -x, true = +x

    public Player(int id, String name, PointF point, int side) {

        /* Initialize settings of this player */
        //|--------------------------------------------|//
        this.new_point = point;
        this.old_point = point;
        this.id = id;
        this.name = name;
        this.maxHealth = MAX_HEALTH;
        this.currentHealth = MAX_HEALTH;
        this.maxCharge = MAX_CHARGE;
        this.side = side; // Jela did this
        this.healthbar = new HealthBar(this);
        this.chargebar = new ChargeBar(this);
        int scaleFactor = 8*Constants.SCREEN_WIDTH/1920;
        float scaleHit = 8.f*Constants.SCREEN_WIDTH/1920;
        //|--------------------------------------------|//


        idle_right = new Animation(
                MainActivity.context,
                R.drawable.knight_idle_right,
                42, 42,
                4,
                point.x, point.y,
                scaleFactor, scaleHit, true);

        idle_left = new Animation(
                MainActivity.context,
                R.drawable.knight_idle_left,
                42, 42,
                4,
                point.x, point.y,
                scaleFactor, scaleHit, false);


        walk_right = new Animation(
                MainActivity.context,
                R.drawable.knight_walk_right_42x42,
                42, 42,
                8,
                point.x, point.y,
                scaleFactor, scaleHit, true);

        walk_left = new Animation(
                MainActivity.context,
                R.drawable.knight_walk_left_42x42,
                42, 42,
                8,
                point.x, point.y,
                scaleFactor, scaleHit, false);

        //Done Lara: frameWidth: 80
        attack_left=new Animation(MainActivity.context,
                R.drawable.knight_attack_left,
                80,42,
                10,point.x, point.y,
                scaleFactor, scaleHit, false);
        attack_left.setFrameDuration(28);

        attack_right=new Animation(MainActivity.context,
                R.drawable.knight_attack_right,
                80, 42,
                10, point.x, point.y,
                scaleFactor, scaleHit, true);
        attack_right.setFrameDuration(28);

        block_left=new Animation(MainActivity.context,
                R.drawable.knight_block_left,
                42,42,
                7,point.x,point.y,
                scaleFactor, scaleHit, false);
        block_left.setFrameDuration(40);

        block_right=new Animation(MainActivity.context,
                R.drawable.knight_block_right,
                42,42,
                7,point.x,point.y,
                scaleFactor, scaleHit, true);
        block_right.setFrameDuration(40);

//        death_left=new Animation(MainActivity.context,
//                R.drawable.knight_death_left,
//                42,42,
//                9, point.x, point.y,
//                scaleFactor, scaleHit, false);
//
//        death_right=new Animation(MainActivity.context,
//                R.drawable.knight_death_right,
//                42,42,
//                9, point.x, point.y,
//                scaleFactor, scaleHit);
//        //death_right.scaleBitmap(8);
//        death_right.forward = true;

        special_attack_left=new Animation(MainActivity.context,
                R.drawable.knight_attack_left_special,
                80,42,
                10,point.x, point.y,
                scaleFactor, scaleHit, false);
        special_attack_left.setFrameDuration(28);

        special_attack_right=new Animation(MainActivity.context,
                R.drawable.knight_attack_right_special,
                80, 42,
                10, point.x, point.y,
                scaleFactor, scaleHit, true);
        special_attack_right.setFrameDuration(28);



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
        if (this.collidesWith(enemy) && this.isCharged) { // TODO: really only attack if collision?
            KryoClient.specialAttack(enemy, SPECIAL_ATTACK_DMG);
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

    public void attackAnimation(){
        if(this.walkInX){
            attack_right.setWhereToDraw((new_point.x),(new_point.y));
            animation = attack_right;
            attack_right.setActivate(true);
        }else{
            attack_left.setWhereToDraw((new_point.x),(new_point.y));
            animation = attack_left;
            attack_left.setActivate(true);
        }
    }

    public void specialAttackAnimation(){
        if(this.walkInX){
            special_attack_right.setWhereToDraw((new_point.x),(new_point.y));
            animation = special_attack_right;
            special_attack_right.setActivate(true);
        }else{
            special_attack_left.setWhereToDraw((new_point.x),(new_point.y));
            animation = special_attack_left;
            special_attack_left.setActivate(true);
        }
    }

    public void blockAnimation(){
        if(this.walkInX){
            block_right.setWhereToDraw((new_point.x),(new_point.y));
            animation = block_right;
            block_right.setActivate(true);
        }else{
            block_left.setWhereToDraw((new_point.x),(new_point.y));
            animation = block_left;
            block_left.setActivate(true);
        }
    }

    public void idle_leftAnimation(){
        idle_left.setWhereToDraw((new_point.x) , (new_point.y));
        animation = idle_left;
    }

    public void idle_rightAnimation(){
        idle_right.setWhereToDraw((new_point.x) , (new_point.y));
        animation = idle_right;
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


        if(currentHealth == 0){
            isLoser = true;
        }

        old_point = new_point;

    }

    //not needed anywhere and doesn't make sense
    public void update(int damage) {
        this.currentHealth = this.currentHealth - damage;
    }

    public int getMaxHealth(){
        return maxHealth;
    }

    public int getCurrentHealth(){
        return currentHealth;
    }

    public void setCurrentHealth(int health){
        currentHealth = health;
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

    public HealthBar getHealthbar(){
        return healthbar;
    }

    public boolean collidesWith(Player enemy){
        if (enemy == null) return false;
        return RectF.intersects(animation.getHitbox(), enemy.animation.getHitbox());
    }
}
