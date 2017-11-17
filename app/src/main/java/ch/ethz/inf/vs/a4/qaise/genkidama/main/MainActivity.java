package ch.ethz.inf.vs.a4.qaise.genkidama.main;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

import ch.ethz.inf.vs.a4.qaise.genkidama.R;
import ch.ethz.inf.vs.a4.qaise.genkidama.scenes.GamePlayScene;


public class MainActivity extends AppCompatActivity {
    private EditText edit_username;
    private EditText ip_address;
    private EditText portnumber;
    Button enterbutton;
    public static final String PREFERENCES="ch.ethz.inf.vs.a4.qaise.genkidama.main.PREFERENCES_FILE_KEY";
    public static final String KEY_IP="ch.ethz.inf.vs.a4.qaise.genkidama.main.IP_KEY";
    public static final String KEY_PORT="ch.ethz.inf.vs.a4.qaise.genkidama.main.PORT_KEY";

    SharedPreferences sharedPreferences;
    public static final String TAG = "##MainActivity## -> ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        // Set Fullscreen:
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get rid of the toolbar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // use this to get rid of app title. FEATURE_NO_TITLE didn't work properly somehow.
        getSupportActionBar().hide();


        // Get Screen Dimensions
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //  landscape mode


        // metrics = new DisplayMetrics();
        // DisplayMetrics metrics = new DisplayMetrics();
        Constants.SCREEN_WIDTH = metrics.widthPixels;
        Constants.SCREEN_HEIGHT = metrics.heightPixels;

        //initialize buttons and editexts
        edit_username=(EditText)findViewById(R.id.et_username);
        ip_address=(EditText)findViewById(R.id.et_ip);
        portnumber=(EditText)findViewById(R.id.et_port);
        enterbutton=(Button)findViewById(R.id.namebutton);

        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        enterbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Constants.USERNAME = edit_username.getText().toString();
                if(Constants.USERNAME == null || Constants.USERNAME.isEmpty()){
                    //Textview as hint in red. ("You have to enter at least one letter as username.");
                }

                Constants.IP_ADDRESS = ip_address.getText().toString();


                //Constants.PORT_NUMBER = 0;
                try {
                    Constants.PORT_NUMBER = Integer.parseInt(portnumber.getText().toString());
                } catch(NumberFormatException nfe) {
                    System.out.println("You have to enter an integer as port number." + nfe);
                }
                //client.isConnected
            }
        });



        //Context context = getApplicationContext();
        //SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE);




      setContentView(R.layout.activity_main);
    }


    public void onClickLogin(View view) {
        //set port number and ip address --> save them
        //declare on top
        //et_name = (EditText) findViewById(R.id.edit_username);

        //String userName = et_name.getText().toString();
        //same for the IP and Port or how?
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        Log.i(TAG, "Network Info: " + networkInfo.toString());

        if (networkInfo == null || !networkInfo.isConnected()) {
            Toast.makeText(getApplication(), "No internet connection!", Toast.LENGTH_LONG).show();
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



            startActivity(new Intent(this, GameActivity.class));
        }

        }


    }
