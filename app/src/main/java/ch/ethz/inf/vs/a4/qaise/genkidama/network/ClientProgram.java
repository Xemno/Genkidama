package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import android.graphics.Point;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;

import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.FLOOR_CEILING_DIST_RELATIVE;
import static ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants.PLAYER_PERCENTAGE_HEIGHT;

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
            message.id =  proposedID;
            Constants.ID = proposedID;
            Log.i(TAG, "registering ID: " + proposedID );

//            Point new_point = new Point(Constants.SCREEN_WIDTH/2, 3* Constants.SCREEN_HEIGHT/4); // at which location the player rectangle is
            Point new_point = new Point(25,(int) (FLOOR_CEILING_DIST_RELATIVE - PLAYER_PERCENTAGE_HEIGHT/2)); // TODO: replace to PointF,a lso server-side
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

            Log.i(TAG, "AddPlayer ID: " + msg.id + "  Player in Map: " + GamePanel.players.values());

            GamePanel.addPlayer(new Player(msg.id, new Point(msg.x, msg.y)));
            return;
        }

        if (object instanceof Network.UpdatePlayer) {

            Network.UpdatePlayer msg = (Network.UpdatePlayer) object;

            if (!GamePanel.players.containsKey(msg.id)) {
                Log.i(TAG, "UpdatePlayer: Player with ID: " + msg.id  + " not contained ");
                GamePanel.addPlayer(new Player(msg.id, new Point(msg.x, msg.y)));
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
