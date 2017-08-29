package anusha.test.xprivaxy.de.xprivacytest;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView imeiNumber;
    Button httpCallButton;
    TextView httpResponseHolder;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        initListeners();
        loadImei();
    }

    private void initListeners() {
        httpCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkConnected()){
                    Toast.makeText(getApplicationContext(), "Internet connected", Toast.LENGTH_SHORT).show();
                    //make http call
                    new OkHttpHandler().execute();
                }
            }
        });
    }

    private void init() {
        imeiNumber = (TextView) findViewById(R.id.imei_holder);
        httpCallButton = (Button) findViewById(R.id.http_call_button);
        httpResponseHolder = (TextView) findViewById(R.id.http_response_holder);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void loadImei() {

        //Check if there is permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            doPermissionGrantedStuffs();
        }
    }

    private void doPermissionGrantedStuffs() {
        // on permission granted, accessing the PHONE to get the device id
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        imeiNumber.setText(telephonyManager.getDeviceId());
    }

    private void requestReadPhoneStatePermission() {
            //Android system dialog box which asks for permission
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

    }

    //Based on user click of android system dialog box
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // user clicks allow
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                doPermissionGrantedStuffs();
            } else {
                // user clicks deny
                alertAlert(getString(R.string.permissions_not_granted_read_phone_state));
            }
        }
    }

    // dialog box showing that there is no permission
    private void alertAlert(String msg) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
                .show();
    }

    private class OkHttpHandler extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String responseString = "";
            Request request = new Request.Builder()
                    .url("http://publicobject.com/helloworld.txt")
                    .build();

            try{
                Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }
                responseString = response.body().string();
                System.out.println(responseString);
            } catch (Exception e){
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            httpResponseHolder.setText(s);
        }
    }
}
