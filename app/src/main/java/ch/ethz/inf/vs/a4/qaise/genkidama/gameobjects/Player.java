package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;


/**
 * Created by Qais on 04-Nov-17.
 */

//TODO: you might want to modify this class

public class Player implements GameObject {

    //    private Rect player;
    private String name;
    private int currentHealth, maxHealth, strength, skill, defence, range, movement;

    private Rect rectangle; // for collision detection
    private int color;

    private float posRatio; // used to calculate right position on different phones

    final static int MIN_DMG = 5;
    final static int MAX_DMG = 15;

    public Player(Rect rectangle, int color, int maxHealth, int currentHealth) {
        // TODO: create player and set invisible rectangle around for collision detection

       this.rectangle = rectangle;
       this.color = color;
       this.maxHealth = maxHealth;
       this.currentHealth = currentHealth;
    }

    // important to use the static method intersects instead of intersect. Else player rectangle gets smaller.
    public boolean playerCollide(Player enemy){
        return Rect.intersects(rectangle,enemy.getRectangle()) || (rectangle.right >= enemy.getRectangle().left);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint(); // The Paint class holds the style and color information about how to draw geometries, text and bitmaps
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
    }

    @Override
    public void update() {
        // you can leave this empty if you dont need it
    }

    public void update(Point point) {
        /* TODO: Update position
         *  update the position of the player and surround it with a rectangle
         *  for collision detection, i.e., rectangle moves with the player.
          */
        rectangle.set(point.x - rectangle.width()/2, point.y - rectangle.height()/2, point.x + rectangle.width()/2, point.y + rectangle.height()/2);
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

    public void setCurrentHealth(int health){
        currentHealth = health;
    }

    public void attack(Player enemy, boolean collision){

        if (collision) {
            int att_dmg = (int)(Math.random() * ((MAX_DMG - MIN_DMG) + 1)) +MIN_DMG; //calculates random value between MIN_DMG and MAX_DMG
            int health = enemy.currentHealth - att_dmg;
            if (health > 0) {
                enemy.setCurrentHealth(enemy.currentHealth - att_dmg);
            } else {
                enemy.setCurrentHealth(0);
            }
        }
        return;
    }



}
