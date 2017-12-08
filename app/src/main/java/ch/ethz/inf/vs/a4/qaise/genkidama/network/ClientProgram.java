package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.FLOOR_CEILING_DIST_RELATIVE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.ID;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.side;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.PLAYER_PERCENTAGE_HEIGHT;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.SCREEN_WIDTH;

/**
 * Created by Qais on 11-Nov-17.
 */

public class ClientProgram extends Listener {
    private static final String TAG = "#ClientProgram# ";

    public void connected (Connection connection) {
        Log.i(TAG, "Connected with " + connection.getRemoteAddressTCP().getHostString());
    }

    public void received (Connection connection, Object object) {

        if (object instanceof Network.RegistrationRequired) {

            Network.Register message = new Network.Register();
            int proposedID = ((Network.RegistrationRequired) object).id; // take proposed ID from server
            int proposedSide = ((Network.RegistrationRequired) object).side; // take proposed ID from server

            message.id =  proposedID;
            message.side = proposedSide;
            ID = proposedID;
            side = proposedSide;

            PointF new_point;

            // side was assigned to by server
            int count = 10; // lets say we can have 10 palyers placed on the screen
            if (side % 2 != 0) { // draw on left side
                new_point = new PointF(side * SCREEN_WIDTH/count, Constants.fixDist);
            } else {  // draw on right side
                new_point = new PointF(SCREEN_WIDTH - ((side) * SCREEN_WIDTH/count) , Constants.fixDist);
            }


            //TODO: x-direction (side) should be decided by server
            message.x = new_point.x; // initial x pos.
            message.y = new_point.y; // initial y pos.
            message.name = Constants.NAME;
            KryoClient.getClient().sendTCP(message);
        }

        if (object instanceof Network.AddPlayer) {

            Network.AddPlayer msg = (Network.AddPlayer) object;

            if (GamePanel.players.containsKey(msg.id)) {
                Log.i(TAG, "AddPlayer failed, already in HashMap " + msg.id);
                return;
            }

//
//            if (GamePanel.myPlayer() != null && GamePanel.myPlayer().id == msg.id) {
//                Constants.REGISTERED = true;
//            }

            GamePanel.addPlayer(new Player(msg.id, new PointF(msg.x, msg.y), msg.side));
            return;
        }

        if (object instanceof Network.UpdatePlayer) {

            Network.UpdatePlayer msg = (Network.UpdatePlayer) object;

//TODO: removed...maybe handle
//            if (!GamePanel.players.containsKey(msg.id)) {
//                GamePanel.addPlayer(new Player(msg.id, new PointF(msg.x, msg.y)));
//            }

            GamePanel.updatePlayer(msg);
            return;
        }

        if (object instanceof Network.Attack) {
            Network.Attack msg = (Network.Attack) object;
            GamePanel.attackPlayer(msg);
            return;
        }

        if (object instanceof Network.RemovePlayer) {
            // TODO: return if player not in list
            Network.RemovePlayer msg = (Network.RemovePlayer) object;
            GamePanel.removePlayer(msg.id);
            return;
        }

    }

    public void disconnected (Connection connection) {
        //TODO: remove own player from list or notify
        Log.i(TAG, "Disconnected");
    }

}
