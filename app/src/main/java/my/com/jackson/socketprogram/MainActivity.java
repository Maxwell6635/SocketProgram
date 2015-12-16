package my.com.jackson.socketprogram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MainActivity extends Activity {

    private EditText serverIp;
    private Button connectPhones , btnScanBarCode;
    private String serverIpAddress = "192.168.0.10";
    private String messages = "";
    private String contantsString;
    private boolean connected = false;


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
                Toast.makeText(this, "Problem to get the barcode number", Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(this, contantsString, Toast.LENGTH_LONG).show();
                serverIp.setText(contantsString);
                try {
                    Toast.makeText(getApplicationContext() ,"Connect",Toast.LENGTH_SHORT).show();
                    udp_send(contantsString);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }
        else{
            Toast.makeText(this, "Problem to scan the barcode.", Toast.LENGTH_LONG).show();
        }
    }

    public void launchBarcodeActivity(View v) {
        Intent intent = new Intent(this, BarcodeActivity.class);
        startActivity(intent);
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

}
