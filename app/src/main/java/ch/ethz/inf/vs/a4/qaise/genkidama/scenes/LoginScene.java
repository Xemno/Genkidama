package ch.ethz.inf.vs.a4.qaise.genkidama.scenes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.UUID;

import ch.ethz.inf.vs.a4.qaise.genkidama.main.Constants;


public class LoginScene implements Scene {
    private Activity activity;
    private boolean btn_active = false;

    public static final String TAG = "##LoginScene## -> ";

    private EditText edit_username;
    private EditText ip_address;
    private EditText portnumber;
    private Button enter_btn;

    public LoginScene(Activity activity){
        this.activity = activity;
    }

    @Override
    public void update() {
        if (!btn_active){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout loginUI = (RelativeLayout) activity.findViewById(Constants.LOGIN_UI);
                    loginUI.setVisibility(View.VISIBLE);
                    loginUI.bringToFront();
                    enter_btn = (Button) activity.findViewById(Constants.ENTER_BTN);
                    edit_username = (EditText) activity.findViewById(Constants.USERNAME_ID);
                    ip_address = (EditText) activity.findViewById(Constants.IP_ID);
                    portnumber = (EditText) activity.findViewById(Constants.PORT_ID);

                    btn_active = true;
                    enter_btn.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            if(testInputs())
                                terminate();
                        }
                    });

                }
            });
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
    }

    @Override
    public void terminate() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RelativeLayout loginUI = (RelativeLayout) activity.findViewById(Constants.LOGIN_UI);
                loginUI.setVisibility(View.GONE);
                btn_active = false;
                SceneManager.ACTIVE_SCENE = Constants.GAMEPLAY_SCENE;
            }
        });

    }

    @Override
    public void receiveTouch(MotionEvent event) {
        //do nothing
    }

    private boolean testInputs(){
        Constants.USERNAME = edit_username.getText().toString();
        Constants.IP_ADDRESS = ip_address.getText().toString();
        try{
            Constants.PORT_NUMBER = Integer.parseInt(portnumber.getText().toString());
        } catch (NumberFormatException nfe){
            System.out.println("You have to enter an integer as port number." + nfe);
            Toast.makeText(activity.getApplication(), "Please enter a port number!", Toast.LENGTH_LONG).show();
            return false;
        }

        /*
        Doesn't work like I want (Lara).
        Can someone maybe help?


        while(Constants.USERNAME == null || Constants.USERNAME.isEmpty() || Constants.IP_ADDRESS == null || Constants.IP_ADDRESS.isEmpty()){
            Constants.USERNAME = edit_username.getText().toString();
            if(Constants.USERNAME == null || Constants.USERNAME.isEmpty()){
                edit_username.setText("");
                edit_username.setHint("@string/hintUserNameWrong");
                edit_username.setHintTextColor(0xffff0000); //Makes the new hint red.
            }

            Constants.IP_ADDRESS = ip_address.getText().toString();
            if(Constants.IP_ADDRESS == null || Constants.IP_ADDRESS.isEmpty()){
                ip_address.setText("");
                ip_address.setHint("@string/hintIPwrong");
                ip_address.setHintTextColor(0xffff0000); //Makes the new hint red.
            }


            //Constants.PORT_NUMBER = 0;

            try {
                Constants.PORT_NUMBER = Integer.parseInt(portnumber.getText().toString());
            } catch(NumberFormatException nfe) {
                portnumber.setHint("@string/hintPortWrong");
                edit_username.setHintTextColor(0xffff0000); //Makes the new hint red.
            }


        }

        */

        //set port number and ip address --> save them
        //declare on top
        //et_name = (EditText) findViewById(R.id.edit_username);

        //String userName = et_name.getText().toString();
        //same for the IP and Port or how?
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //Log.i(TAG, "Network Info: " + networkInfo.toString());

        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(activity.getApplication(), "No internet connection!", Toast.LENGTH_LONG).show();
            return false;
        } else {  // connection to the internet is made

            //String userName = edit_username.getText().toString();
            String uuid = UUID.randomUUID().toString();
            //String portNumber=portnumber.getText().toString();
            //String ipaddress=ip_address.getText().toString();
            String user= Constants.USERNAME;
            int portNumber=Constants.PORT_NUMBER;
            String ipaddress=Constants.IP_ADDRESS;


            //safe port and IP
            //  Context context= getApplicationContext();
            // SharedPreferences sharedPreferences=context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
            //String SERVER_ADDRESS = sharedPreferences.getInt(SettingsActivity.KEY_IP, Constants.IP_ADDRESS);
            //int udpPort = sharedPreferences.getInt(SettingsActivity.KEY_PORT, Constants.PORT_NUMBER);
            // Constants.USERNAME= sharedPreferences.getString();
            //new Thread(new ClientThread(this, userName, uuid, MessageTypes.REGISTER, serverAddress, udpPort)).start();

        }
        return true;
    }
}
