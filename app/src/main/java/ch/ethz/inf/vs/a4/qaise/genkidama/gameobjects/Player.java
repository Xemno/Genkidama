package ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.PLAYER_SIZE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;


//TODO: you might want to modify this class

public class Player implements GameObject {

    //    private Rect player;
    private String name;
    private int currentHealth, maxHealth;
    private int currentCharge, maxCharge;
    private final int CHARGE_AMOUNT = 5;
    public boolean isCharged = false;
    public boolean isLooser=false;

    private HealthBar healthbar;
    private ChargeBar chargebar;
    private int side;

    private int strength, skill, defence, range, movement;

    private Rect rectangle; // for collision detection
    private int color;

    private float posRatio; // used to calculate right position on different phones

    private final static int MIN_DMG = 10;
    private final static int MAX_DMG = 20;
    private final static int SPECIAL_ATTACK_DMG = 100;



    public Player(Rect rectangle, int color, int maxHealth, int currentHealth, int maxCharge, int currentCharge, int side) {
        // TODO: create player and set invisible rectangle around for collision detection

       this.rectangle = rectangle;
       this.color = color;
       this.maxHealth = maxHealth;
       this.currentHealth = currentHealth;
       this.maxCharge = maxCharge;
       this.currentCharge = currentCharge;
       this.side = side;
       this.healthbar = new HealthBar(this);
       this.chargebar = new ChargeBar(this);
    }

    // important to use the static method intersects instead of intersect. Else player rectangle gets smaller.
    public boolean playerCollide(Player enemy){
        int me_right = rectangle.right;
        int en_left = enemy.getRectangle().left;
        return Rect.intersects(rectangle,enemy.getRectangle()) || (rectangle.right >= enemy.getRectangle().left);
    }

    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint(); // The Paint class holds the style and color information about how to draw geometries, text and bitmaps
        paint.setColor(color);
        canvas.drawRect(rectangle, paint);
        healthbar.draw(canvas);
        chargebar.draw(canvas);
    }

    @Override
    public void update() {
        //TODO: setLoosermethod setzen
        setLoser();
        // you can leave this empty if you dont need it
    }

    public void update(PointF point) {  //TODO: change to give the percentage of the screen instead of fixed pos when called
        /* TODO: Update position
         *  update the position of the player and surround it with a rectangle
         *  for collision detection, i.e., rectangle moves with the player.
          */
        int halfSize = PLAYER_SIZE/2;
        // rectangle.set(point.x - halfSize, point.y - halfSize, point.x + halfSize, point.y + halfSize);

        // relative version:
        int fixX = (int) (SCREEN_WIDTH*point.x/100.0f);
        int fixY = (int) (SCREEN_HEIGHT*point.y/100.0f);
        rectangle.set(fixX - halfSize, fixY - halfSize, fixX + halfSize, fixY + halfSize);
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



    //public void setSide(int s) {side = s;}

    public int getSide() {return side;}


    public void setCurrentHealth(int health){
        currentHealth = health;
    }
    public void setLoser(){
        if(currentHealth==0){
            isLooser=true;
        }
    }

    public void attack(Player enemy, boolean collision){

        if (collision) {
            int att_dmg = (int)(Math.random() * ((MAX_DMG - MIN_DMG) + 1)) + MIN_DMG; //calculates random value between MIN_DMG and MAX_DMG
            if (currentCharge > maxCharge - CHARGE_AMOUNT) {
                currentCharge = maxCharge;
                isCharged = true;
            }
            else currentCharge += CHARGE_AMOUNT;
            int health = enemy.currentHealth - att_dmg;
            if (health > 0) {
                enemy.setCurrentHealth(enemy.currentHealth - att_dmg); // TODO: why not just pass health as argument?
            } else {
                enemy.setCurrentHealth(0);
            }
            enemy.healthbar.update();
            chargebar.update();
        }
        return;
    }

    public void specialAttack(Player enemy) {
        //TODO: Make it visible, do we need collision? Maybe make SA stronger when repeated
        int health = enemy.currentHealth - SPECIAL_ATTACK_DMG;
        if (health > 0) {
            enemy.setCurrentHealth(health);
        } else {
            enemy.setCurrentHealth(0);
        }
        currentCharge = 0;
        isCharged = false;
        enemy.healthbar.update();
        chargebar.update();
    }

    public HealthBar getHealthbar(){
        return healthbar;
    }
}
