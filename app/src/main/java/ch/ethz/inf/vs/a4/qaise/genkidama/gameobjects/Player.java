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
    private int currentHealth, maxHealth;
    private int currentCharge, maxCharge;
    private final int CHARGE_AMOUNT = 5;
    public boolean isLooser=false;

    private int strength, skill, defence, range, movement;

    private Rect rectangle; // for collision detection
    private int color;

    public HealthBar healthBar;
    public ChargeBar chargeBar;

    private float posRatio; // used to calculate right position on different phones

    final static int MIN_DMG = 10;
    final static int MAX_DMG = 20;

    public Player(Rect rectangle, int color, int maxHealth, int currentHealth, int maxCharge, int currentCharge) {
        // TODO: create player and set invisible rectangle around for collision detection

       this.rectangle = rectangle;
       this.color = color;
       this.maxHealth = maxHealth;
       this.currentHealth = currentHealth;
       this.maxCharge = maxCharge;
       this.currentCharge = currentCharge;


        healthBar = new HealthBar(this);
        chargeBar = new ChargeBar(this);
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
        healthBar.draw(canvas);
        chargeBar.draw(canvas);
    }

    @Override
    public void update() {
        //TODO: setLoosermethod setzen
        setLooser();
        // you can leave this empty if you dont need it

        healthBar.update();
        chargeBar.update();
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

    public int getMaxCharge() {
        return maxCharge;
    }

    public int getCurrentCharge() {
        return currentCharge;
    }

    // dummie method:
    private int side;
    public void setSide(int s) {side = s;}
    public int getSide() {return side;}


    public void setCurrentHealth(int health){
        currentHealth = health;
    }
    public void setLooser(){
        if(currentHealth==0){
            isLooser=true;
        }
    }

    public void attack(Player enemy, boolean collision){

        if (collision) {
            int att_dmg = (int)(Math.random() * ((MAX_DMG - MIN_DMG) + 1)) +MIN_DMG; //calculates random value between MIN_DMG and MAX_DMG
            if (currentCharge > maxCharge - CHARGE_AMOUNT) currentCharge = maxCharge;
            else currentCharge += CHARGE_AMOUNT;
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
