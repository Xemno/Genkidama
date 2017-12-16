package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.util.HashSet;

/**
 * Created by Qais on 10-Dec-17.
 */

public class ServerProgramm extends Listener {
    private static final String TAG = "#ClientProgram# ";

    private static int counter = 0; // ID counter for clients
    private static final int MAX_PLAYERS = 4;

    HashSet<StateObject> onlinePlayers = new HashSet<StateObject>();


    public void connected (Connection connection) {
    }

    public void received (Connection c, Object object) {


        // We know all connections for this server are actually StateConnection.
        KryoServer.StateConnection connection = (KryoServer.StateConnection)c;
        StateObject state = connection.state;

        /*** ---- LOGIN ---- ***/
        if(object instanceof Network.Login) {

            //Ignore if already logged in
            if (state != null) return;

            String name = ((Network.Login)object).name;
            int id = ((Network.Login)object).id;		// will be 999, that is a new player is logged in


            // reject if already logged in with same name or id
            for (StateObject other : onlinePlayers) {
                if (other.name.equals(name)) {
                    return;
                }
                if (other.id == id) {
                    return;
                }
            }

            counter++;
//            if (!(counter <= MAX_PLAYERS)) return; // TODO: add this
            Network.RegistrationRequired register = new Network.RegistrationRequired();
            register.id = counter;
            register.side = (onlinePlayers.size()) + 1; // First player has side 1, second side 2, etc...
            System.out.println("side: " + register.side);
            c.sendTCP(register);
        }


        /*** ---- REGISTER ---- ***/
        if(object instanceof Network.Register) {
            // ignore if already logged in
            if (state != null) return;

            Network.Register register = (Network.Register)object;

            // Reject if character already exists.
            // TODO: handle abnormal situations

            state = new StateObject();
            state.name = register.name;
            state.id = register.id;
            state.side = register.side;
            state.x = register.x;
            state.y = register.y;

            registerPlayerWithConnection(connection, state);

        }

        /*** ---- MOVE PLAYER ---- ***/
        if(object instanceof Network.MovePlayer) {

            if (state == null) {
                counter++;
                Network.RegistrationRequired register = new Network.RegistrationRequired();
                register.id = counter;

                c.sendTCP(register);
                return;
            }

            Network.MovePlayer msg = (Network.MovePlayer) object;
            Network.UpdatePlayer update = new Network.UpdatePlayer();
            update.id = msg.id;
            update.x = msg.x;
            update.y = msg.y;
            KryoServer.server.sendToAllTCP(update);

            return;
        }

        /*** ---- ATTACK PLAYER ---- ***/
        if(object instanceof Network.AttackPlayer) {

            if (state == null) {
                counter++;
                Network.RegistrationRequired register = new Network.RegistrationRequired();
                register.id = counter;

                c.sendTCP(register);
                return;
            }

            Network.AttackPlayer msg = (Network.AttackPlayer) object;
            Network.Attack attack = new Network.Attack();
            attack.idA = msg.idA;
            attack.idE = msg.idE;
            attack.damage = msg.damage;
            KryoServer.server.sendToAllTCP(attack);
        }

        /*** ---- PLAY AGAIN ---- ***/
        if (object instanceof Network.PlayAgain) {
            if (state == null) {
                //help
            }

            Network.PlayAgain msg = (Network.PlayAgain) object;

            Network.PlayAgain broadcast = new Network.PlayAgain();
            broadcast.id = msg.id;
            broadcast.answer = msg.answer;
            KryoServer.server.sendToAllTCP(broadcast);

            System.out.println(msg.id + " is voting for " + msg.answer);

        }

        /*** ---- START GAME ---- ***/
        if (object instanceof Network.StartGame) {
            if (state == null) {
                //help
            }
            Network.StartGame msg = new Network.StartGame();
            msg.info = 1;
            KryoServer.server.sendToAllTCP(msg);

        }


    }

    public void disconnected (Connection c) {

        KryoServer.StateConnection connection = (KryoServer.StateConnection)c;
        if (connection.state != null) {

            onlinePlayers.remove(connection.state);

            Network.RemovePlayer removePlayer = new Network.RemovePlayer();
            removePlayer.id = connection.state.id;
            KryoServer.server.sendToAllTCP(removePlayer);

        }
    }


    private void registerPlayerWithConnection (KryoServer.StateConnection c, StateObject state) {
        // logging in the player with State state to connection c

        c.state = state; // set the connections state to the given one

        // Add existing states of other already logged in players to the new logged in state connection
        for (StateObject other : onlinePlayers) {
            Network.AddPlayer addPlayer = new Network.AddPlayer();
            addPlayer.name = other.name;
            addPlayer.id = other.id;
            addPlayer.side = other.side;
            addPlayer.x = other.x;
            addPlayer.y = other.y;
            c.sendTCP(addPlayer);
        }

        onlinePlayers.add(state); // add to player list

        // Now add the logged in players state to all existing connections
        Network.AddPlayer addPlayer = new Network.AddPlayer();
        addPlayer.name = state.name;
        addPlayer.id = state.id;
        addPlayer.side = state.side;
        addPlayer.x = state.x;
        addPlayer.y = state.y;
        KryoServer.server.sendToAllTCP(addPlayer);

    }

}
