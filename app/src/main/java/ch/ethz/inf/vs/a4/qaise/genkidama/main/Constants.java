package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.app.Activity;

/**
 * Add global Constants here
 */

public class Constants {

//    public static Activity activity;
    public static String NAME = "default"; // TODO: overwrite with input name!
    public static String USERNAME;
    public static int PORT_NUMBER = 15555;
    public static String SERVER_ADDRESS = "192.168.0.103";
    public static int ID = 999;

    public static boolean serverStarted = false;
    public static boolean clientConnected = false;


    public static int side = 0;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

//    public static boolean CONNECTED = false;

    public static float PLAYER_PERCENTAGE_WIDTH = 10;
    public static float PLAYER_PERCENTAGE_HEIGHT; // defined via player_size
    public static int PLAYER_SIZE;
    // public static int FLOOR_CEILING_DIST_RELATIVE = 56500/700; // measured pixels from picture to get this ratio
    public static int FLOOR_CEILING_DIST_RELATIVE = 24000/255;
    public static int fixDist = FLOOR_CEILING_DIST_RELATIVE*SCREEN_HEIGHT/100;

    public static int JOIN_BUTTON = 1335;
    public static int CREATE_BUTTON = 1336;

    public static int ATT_BTN = 1337;
    public static int SUPER_BTN = 1338;

    //buttons for the Gameoverscene
    public static int RESTARTGAME_BTN = 1339;
    public static int BACK_TO_LOGIN_BTN = 1340;
    public static int USERNAME_ID = 1341;
    public static int IP_ID = 1342;
    public static int PORT_ID = 1343;
    public static int LOGIN_BTN = 1344;
    public static int START_BTN = 1345;
    public static int TEXT_VIEW = 1346;
    public static int USERNAME2_ID = 1347;
    public static int START2_BTN = 1348;
    public static int CREATE2_BUTTON = 1349;


    public static int GAME_PLAY_UI = 2000;
    public static int GAME_OVER_UI = 2001;
    public static int JOIN_GAME_UI = 2002;
    public static int START_UI = 2003;
    public static int CREATE_GAME_UI = 2004;

    public static int START_SCENE = 0;
    public static int Join_GAME_SCENE = 1;
    public static int CREATE_GAME_SCENE = 2;
    public static int GAMEPLAY_SCENE = 3;
    public static int GAMEOVER_SCENE = 4;

    public static int MAX_HEALTH = 1000;
    public static int MAX_CHARGE = 200;
    public static int CHARGE_AMOUNT = 10;

}
