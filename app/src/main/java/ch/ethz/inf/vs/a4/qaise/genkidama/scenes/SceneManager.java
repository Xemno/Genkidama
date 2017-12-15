package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Manager that manages our scenes.
 */

public class SceneManager {

    private ArrayList<Scene> scenes = new ArrayList<>();
    public static int ACTIVE_SCENE;

    public SceneManager(Activity activity) {
        scenes.add(new StartScene(activity));
        scenes.add(new JoinGameScene(activity));
        scenes.add(new CreateGameScene(activity));
        scenes.add(new GamePlayScene(activity));
        scenes.add(new GameOverScene(activity));
    }

    public void receiveTouch(MotionEvent event) {
        scenes.get(ACTIVE_SCENE).receiveTouch(event);
    }

    public void update() {
        scenes.get(ACTIVE_SCENE).update();
    }

    public void draw(Canvas canvas) {
        scenes.get(ACTIVE_SCENE).draw(canvas);
    }

    public void terminate(){
        scenes.get(ACTIVE_SCENE).terminate();
    }

}
