package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.CLIENT_CONNECTED;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.FLOOR_CEILING_DIST_RELATIVE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.ID;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.side;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.PLAYER_PERCENTAGE_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;

/**
 * Created by Qais on 11-Nov-17.
 */

public class ClientProgram extends Listener {
    private static final String TAG = "#ClientProgram# ";

    public void connected (Connection connection) {
        CLIENT_CONNECTED = true;
        Log.i(TAG, "Connected with " + connection.getRemoteAddressTCP().getHostString());
    }

    public void received (Connection connection, Object object) {

        if (object instanceof Network.RegistrationRequired) {

            Network.Register message = new Network.Register();
            int proposedID = ((Network.RegistrationRequired) object).id; // take proposed ID from server
            int proposedSide = ((Network.RegistrationRequired) object).side; // take proposed ID from server

            message.id =  proposedID;       // ID of myPlayer
            message.side = proposedSide;    // Side of myPlayer
            ID = proposedID;
            side = proposedSide;

            PointF new_point;

            // side was assigned to by server
            int count = 10; // lets say we can have 10 palyers placed on the screen
            if (side % 2 != 0) { // draw on left side
                new_point = new PointF(SCREEN_WIDTH/4, Constants.fixDist);
            } else {  // draw on right side
                new_point = new PointF(3*SCREEN_WIDTH / 4 , Constants.fixDist);
            }

            System.out.println("init: " + new_point.x + ", " + new_point.y);


            // side was assigned to by server
            /*if (side % 2 != 0) { // draw on left side
                new_point = new PointF(side * SCREEN_WIDTH/count, Constants.fixDist);
            } else {  // draw on right side
                new_point = new PointF(SCREEN_WIDTH - ((side) * SCREEN_WIDTH/count) , Constants.fixDist);
            }*/

            //message.x = new_point.x / SCREEN_WIDTH; // initial x pos.
            //message.y = new_point.y / SCREEN_HEIGHT; // initial y pos.
            message.x = new_point.x;
            message.y = new_point.y;
            message.name = Constants.NAME;
            KryoClient.getClient().sendTCP(message);
        }

        if (object instanceof Network.AddPlayer) {

            Network.AddPlayer msg = (Network.AddPlayer) object;

            if (GamePanel.players.containsKey(msg.id)) {
                Log.i(TAG, "AddPlayer failed, already in HashMap " + msg.id);
                return;
            }

            GamePanel.addPlayer(new Player(msg.id, msg.name, new PointF(msg.x, msg.y), msg.side));
            return;
        }

        if (object instanceof Network.UpdatePlayer) {

            Network.UpdatePlayer msg = (Network.UpdatePlayer) object;

            if (!GamePanel.players.containsKey(msg.id)) {
//                GamePanel.addPlayer(new Player(msg.id, new PointF(msg.x, msg.y)));
                return;
            }
            GamePanel.updatePlayer(msg);
            return;
        }

        if (object instanceof Network.Attack) {
            Network.Attack msg = (Network.Attack) object;
            GamePanel.attackPlayer(msg);
            return;
        }

        if (object instanceof Network.RemovePlayer) {
            Network.RemovePlayer msg = (Network.RemovePlayer) object;
            GamePanel.removePlayer(msg.id);
            return;
        }

        if (object instanceof Network.PlayAgain){
            Network.PlayAgain msg = (Network.PlayAgain) object;
            GamePanel.rematch(msg);
        }

    }

    public void disconnected (Connection connection) {
        Log.i(TAG, "Disconnected");
        CLIENT_CONNECTED = false;
    }

}
