package com.cortechx.kodah;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Shawn Grant on 10/3/2019.
 */

public class TCPClient {

    public void SendMessage(String content, String ip)
    {
        new Task().execute(content, ip);
    }

    static class Task extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... params) {
            String ipAddress = params[1];
            int portNumber = 1300;

            String modifiedSentence;
            Socket clientSocket;

            try
            {
                clientSocket = new Socket(ipAddress, portNumber);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                outToServer.writeBytes(params[0] + "\n");
                clientSocket.setSoTimeout(5000);

                //modifiedSentence = inFromServer.readLine();
                clientSocket.close();
                outToServer.close();
                inFromServer.close();
            }
            catch (Exception exc)
            {
                exc.printStackTrace();
            }
            return null;
        }

    }
}
