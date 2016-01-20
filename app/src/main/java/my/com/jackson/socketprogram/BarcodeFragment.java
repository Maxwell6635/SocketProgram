package my.com.jackson.socketprogram;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class BarcodeFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String serverIpAddress = "172.16.132.248";
    private String messages = "";
    private String contantsString;
    private boolean connected = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
//                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        contantsString = rawResult.getText();
        if (contantsString.equalsIgnoreCase("0")) {
            Toast.makeText(getContext(), "Problem to get the barcode number", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), contantsString, Toast.LENGTH_LONG).show();
            try {
                Toast.makeText(getContext() ,"Connect",Toast.LENGTH_SHORT).show();
                udp_send(contantsString);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    private void udp_send(String messages) throws IOException
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

