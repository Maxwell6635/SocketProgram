package my.com.jackson.socketprogram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.net.*;

import android.app.Activity;
import android.os.*;
import android.util.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;


public class MainActivity extends Activity {

    private EditText serverIp;
    private Button connectPhones , btnScanBarCode;
    private String serverIpAddress = "192.168.0.10";
    private String messages = "";
    private String contantsString;
    private boolean connected = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverIp = (EditText) findViewById(R.id.server_ip);
        connectPhones = (Button) findViewById(R.id.connect_phones);
        btnScanBarCode = (Button) findViewById(R.id.scan_button);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btnScanBarCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.initiateScan();



            }
        });

        connectPhones.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!connected) {
                    messages = serverIp.getText().toString();
                    if (!messages.equals("")) {
                        try {
                            Toast.makeText(getApplicationContext() ,"Connect",Toast.LENGTH_SHORT).show();
                            udp_send(messages);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {

            // handle scan result
            contantsString =  scanResult.getContents()==null?"0":scanResult.getContents();
            if (contantsString.equalsIgnoreCase("0")) {
                Toast.makeText(this, "Problem to get the  contant Number", Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(this, contantsString, Toast.LENGTH_LONG).show();

            }

        }
        else{
            Toast.makeText(this, "Problem to secan the barcode.", Toast.LENGTH_LONG).show();
        }
    }

    private void udp_send(String  messages) throws IOException
    {
        String messageStr= messages;
        int server_port = 10000;
        DatagramSocket s = new DatagramSocket();
        InetAddress local = InetAddress.getByName(serverIpAddress);
        int msg_length=messageStr.length();
        byte[] message = messageStr.getBytes();
        DatagramPacket p = new DatagramPacket(message, msg_length,local,server_port);
        s.send(p);
    }

    public class ClientThread implements Runnable {
        public void run() {
            try {
                InetAddress serverAddr =
                        InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket(serverAddr, 10000);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out =
                                new PrintWriter(
                                        new BufferedWriter(
                                                new OutputStreamWriter(
                                                        socket.getOutputStream())),
                                        true);

                        // where you issue the commands
                        out.println("Hey Server!");
                        Log.d("ClientActivity", "C: Sent.");
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }

    ;


}