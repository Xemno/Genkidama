package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;


/**
 * Created by Qais on 10-Dec-17.
 */

public class KryoServer extends Service {

    public static final int port = 15555;
    private static int counter = 0; // ID counter for clients

    public static Server server;

    @Override
    public void onCreate() {
        super.onCreate();


        server = new Server() {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new StateConnection();
            }
        };

        Network.register(server);
        server.start();
        try {
            server.bind(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.addListener(new Listener.ThreadedListener(new ServerProgramm()));

        Toast.makeText(getApplication(), getIP(), Toast.LENGTH_LONG).show();

    }
/*
    public KryoServer() throws IOException {

    }*/

    public Server getServer()
    {
        return server;
    }


    public String getIP(){
        try {
            Enumeration<NetworkInterface> netInts = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface nI : Collections.list(netInts)){
                for (Enumeration<InetAddress> eia = nI.getInetAddresses(); eia.hasMoreElements();){
                    InetAddress ia = eia.nextElement();
                    if (!ia.isLoopbackAddress() && ia.getHostAddress().length() <= 16){

                        return ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException se) {
            se.printStackTrace();
        }
        return "failed to get host address...";
    }

    static class StateConnection extends Connection {
        StateObject state;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.close();
        }
        Toast.makeText(getApplication(), "Server stopped.", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
