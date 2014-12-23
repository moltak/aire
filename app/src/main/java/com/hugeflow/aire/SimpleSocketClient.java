package com.hugeflow.aire;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SimpleSocketClient implements Runnable {
    private final String mServerIP;
    private final int mServerPort;
    private String mMsg;

    public SimpleSocketClient(String serverIP, int serverPort, String msg) {
        this.mServerIP = serverIP;
        this.mServerPort = serverPort;
        this.mMsg = msg;
    }

    @Override
    public void run() {
        try {
            InetAddress serverAddr = InetAddress.getByName(mServerIP);
            Log.d("TCP", "C: Connecting...");
            Socket socket = new Socket(serverAddr, mServerPort);

            Log.d("TCP", "C: Sending: '" + mMsg + "'");
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            PrintWriter out = new PrintWriter(new BufferedWriter(osw), true);
            out.print(mMsg);
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String return_msg = in.readLine();
            Log.d("TCP", "C: Server send to me this message -->" + return_msg);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
