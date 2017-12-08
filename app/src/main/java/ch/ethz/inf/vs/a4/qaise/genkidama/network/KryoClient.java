package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.GamePanel;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.MainActivity;


/**
 * Created by Qais on 11-Nov-17.
 */

// Class is Singleton pattern

public class KryoClient {
    // Sends and receives positions of the players
    private static final String TAG = "#KryoClient#";
    private static KryoClient instance = null;
    private static final int timeout = 5000;

    private static Client client;


    public static Client getClient() { // only return if instantiated, check it!
        return client;
    }

    public static KryoClient getInstance() {
        if (instance == null) {
            instance = new KryoClient();
        }
        return instance;
    }

    protected KryoClient() {

        client = new Client();
        Network.register(client); // register all the classes with this client
        client.start();
        client.addListener(new Listener.ThreadedListener(new ClientProgram()));


    }

    public static void send(PointF point) {
        if (client != null && client.isConnected()) {
            Network.MovePlayer message = new Network.MovePlayer();

            if (Constants.ID == 999) return; // if default ID, that is ID is not yet set by server
            message.id = Constants.ID;  // move our player
            // TODO: if float not necessary for packets, we can leave the rest but change network to int, and cast here from float to int
            message.x = point.x;
            message.y = point.y;
            client.sendTCP(message);
        }
    }

    public static void attack(Player enemy, int damage) {
        if (client != null && client.isConnected()) {
            Network.AttackPlayer message = new Network.AttackPlayer();

            if (Constants.ID == 999) return; // if default ID, that is ID is not yet set by server
            message.idA = GamePanel.myPlayer().id;  // move our player
            message.idE = enemy.id;
            // TODO: if float not necessary for packets, we can leave the rest but change network to int, and cast here from float to int
            message.damage = damage;
            client.sendTCP(message);
        }
    }

    private static void login() {
        Network.Login login = new Network.Login();
        login.name = Constants.NAME;
        login.id = Constants.ID;
        client.sendTCP(login);
        Log.i(TAG, "Login message sent");
    }

    private static class ConnectToServer extends AsyncTask<String, int[], String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                client.connect(timeout, Constants.SERVER_ADDRESS, Constants.PORT_NUMBER);
                Log.i(TAG, "Connection to Server: Succeeded");

                login(); // send login message to server

                return null;
            } catch (IOException e) {
                Log.i(TAG, "Error connecting: " + e.getMessage());
                return null;
            }
        }
    }

    private static class ReconnectToServer extends AsyncTask<String, int[], String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                // if once connected = true, then reconnect, otherwise connect
                client.reconnect(timeout);
                Log.i(TAG, "reconnection succeeded");

                login(); // send a login message upon reconnecting

                return null;
            } catch (IOException e) {
                Log.i(TAG, "Error reconnecting: " + e.getMessage());
                return null;
            }
        }
    }

    public void reconnect() {
        if (client == null) return;
        if (client.isConnected()) return;
        new ReconnectToServer().execute();
    }

    public void connect() {
        if (client == null) return;
        if (client.isConnected()) return;
        new ConnectToServer().execute();
    }

}
