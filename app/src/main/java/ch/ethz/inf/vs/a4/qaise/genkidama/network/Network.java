package ch.ethz.inf.vs.a4.qaise.genkidama.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;


/**
 * Created by Qais on 11-Nov-17.
 */

public class Network {
    static public void register (EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Login.class);
        kryo.register(RegistrationRequired.class);
        kryo.register(Register.class);
        kryo.register(AddPlayer.class);
        kryo.register(UpdatePlayer.class);
        kryo.register(RemovePlayer.class);
        kryo.register(StateObject.class);
        kryo.register(MovePlayer.class);
    }

    public static class Login {
        public String name;
        public int id;
    }

    public static class RegistrationRequired {
        public int id; // propose an id for registration
    }

    public static class Register {
        public String name;
        public int id, x, y;
    }

    public static class AddPlayer {
        public int id, x ,y;
    }

    static public class RemovePlayer {
        public int id;
    }

    public static class MovePlayer {
        // For sending updates of our character
        public int id, x, y;
    }

    public static class Attack {
        // For Server use
        public int idA; // ID of the Attacker
        public int idE; // which player is being attacked
        public int damage;  // the damage caused

    }

    public static class AttackPlayer {
        // For Client use
        public int idA; // ID of the Attacker
        public int idE; // which player is being attacked
        public int damage;  // the damage caused
        public int charge;  // charge for idA from this damage

    }

    public static class UpdatePlayer {
        // For receiving updates of other characters
        public int id, x, y;
    }
}
