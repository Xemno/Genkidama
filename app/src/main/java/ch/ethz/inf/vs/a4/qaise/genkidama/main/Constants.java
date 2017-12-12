package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.app.Activity;

/**
 * Add global Constants here
 */

public class Constants {

//    public static Activity activity;
    public static String NAME = "default"; // TODO: overwrite with input name!
    public static String USERNAME;
    public static int PORT_NUMBER = 15325;
    public static String SERVER_ADDRESS = "192.168.0.103";
    public static int ID = 999;

    public static int side = 0;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

//    public static boolean CONNECTED = false;

    public static float PLAYER_PERCENTAGE_WIDTH = 10;
    public static float PLAYER_PERCENTAGE_HEIGHT; // defined via player_size
    public static int PLAYER_SIZE;
    public static int FLOOR_CEILING_DIST_RELATIVE = 56500/700; // measured pixels from picture to get this ratio
    public static int fixDist = FLOOR_CEILING_DIST_RELATIVE*SCREEN_HEIGHT/100;

    public static int ATT_BTN = 1337;
    public static int SUPER_BTN = 1338;

    //buttons for the Gameoverscene
    public static int RESTARTGAME_BTN=1339;
    public static int BACK_TO_LOGIN_BTN = 1340;
    public static int USERNAME_ID = 1344;
    public static int IP_ID = 1345;
    public static int PORT_ID = 1346;
    public static int LOGIN_BTN = 1347;
    public static int START_BTN = 1348;
    public static int TEXT_VIEW = 1349;

    public static int GAME_UI = 1341;
    public static int GAMEOVER_UI = 1342;
    public static int LOGIN_UI = 1343;

    public static int LOGIN_SCENE = 0;
    public static int GAMEPLAY_SCENE = 1;
    public static int GAMEOVER_SCENE = 2;

    public static int MAX_HEALTH = 1000;
    public static int MAX_CHARGE = 200;
    public static int CHARGE_AMOUNT = 5;

}
