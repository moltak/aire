package com.hugeflow.aire;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
        sendUdpMessage();
    }

    private void sendTcpMessage() {
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

    private void sendUdpMessage() {
        try {
            // Retrieve the ServerName
            InetAddress serverAddr = InetAddress.getByName(mServerIP);

            Log.d("UDP", "C: Connecting...");
            /* Create new UDP-Socket */
            DatagramSocket socket = new DatagramSocket();

            /* Prepare some data to be sent. */
            byte[] buf = mMsg.getBytes();

            /* Create UDP-packet with
             * data & destination(url+port) */
            DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, mServerPort);
            Log.d("UDP", "C: Sending: '" + new String(buf) + "'");

            /* Send out the packet */
            socket.send(packet);
            Log.d("UDP", "C: Sent.");
            Log.d("UDP", "C: Done.");

//            socket.receive(packet);
            Log.d("UDP", "C: Received: '" + new String(packet.getData()) + "'");

        } catch (Exception e) {
            Log.e("UDP", "C: Error", e);
        }
    }
}
