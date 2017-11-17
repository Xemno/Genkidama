package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;

public class SceneTransitionActivity extends AppCompatActivity {

    GameOverScene gameOverScene;
    GamePlayScene gamePlayScene;
    Transition transitionMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene_transition);

        transitionMgr = TransitionInflater.from(this).inflateTransition(R.transition.transition);

    }

    public void goToGameOverScene(View view){
        //TransitionManager.go(gameOverScene, transitionMgr);
    }

    public void goToGamePlayScene(View view){
        //TransitionManager.go(GamePlayScene, transitionMgr);
    }
}
