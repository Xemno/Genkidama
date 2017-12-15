package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;
import ch.ethz.inf.vs.a4.qaise.genkidama.gameobjects.Player;
import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;

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
            message.x = point.x;
            message.y = point.y;
            client.sendTCP(message);
        }
    }

    public static void attack(Player enemy, int damage) {
        if (client != null && client.isConnected()) {
            new AttackMessage().execute(enemy.id, damage);
        }
    }

    public static void specialAttack(Player enemy, int damage) {
        if (client != null && client.isConnected())
            new AttackMessage().execute(enemy.id, -damage);
    }

    public static void login() {
        Network.Login login = new Network.Login();
        login.name = Constants.NAME;
        login.id = Constants.ID;
        client.sendTCP(login);
        Log.i(TAG, "Login message sent");
    }

    public static void playAgain(int ans) {
        new PlayAgainMessage().execute(ans);
    }

    private static class AttackMessage extends AsyncTask<Integer, int[], String> {
        @Override
        protected String doInBackground(Integer... arg0) {
            try {
                Network.AttackPlayer message = new Network.AttackPlayer();

                if (Constants.ID == 999) return null; // if default ID, that is ID is not yet set by server
                message.idA = Constants.ID;  // move our player
                message.idE = arg0[0];
                message.damage = arg0[1];
                client.sendTCP(message);
                return null;

            } catch (Exception e) {
                Log.i(TAG, "Error connecting: " + e.getMessage());
                return null;
            }
        }
    }

    private static class PlayAgainMessage extends AsyncTask<Integer, int[], String> {
        @Override
        protected String doInBackground(Integer... arg0) {
            try {
                Network.PlayAgain message = new Network.PlayAgain();

                if (Constants.ID == 999) return null; // if default ID, that is ID is not yet set by server
                message.id = Constants.ID;  // move our player
                message.answer = arg0[0];
                client.sendTCP(message);
                return null;

            } catch (Exception e) {
                Log.i(TAG, "Error connecting: " + e.getMessage());
                return null;
            }
        }
    }

    private static class ConnectToServer extends AsyncTask<String, int[], String> {
//        AsyncTask<Void, Void, Void>

//        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//            //this method will be running on UI thread
//            pdLoading.setMessage("\tLoading...");
//            pdLoading.show();
//        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                client.connect(timeout, Constants.SERVER_ADDRESS, Constants.PORT_NUMBER);
                Constants.clientConnected = true;
                Log.i(TAG, "Connection to Server: Succeeded");
                login(); // send login message to server

                return null;
            } catch (IOException e) {
                Log.i(TAG, "Error connecting: " + e.getMessage());
                return null;
            }
        }

//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//            //this method will be running on UI thread
//
//            pdLoading.dismiss();
//        }
    }


    public void connect() {
        if (client == null) return;
        if (client.isConnected()) return;
        new ConnectToServer().execute();
    }

    public static void close(){
        if (client != null) client.close();
        if (instance != null) instance = null;
    }
}
