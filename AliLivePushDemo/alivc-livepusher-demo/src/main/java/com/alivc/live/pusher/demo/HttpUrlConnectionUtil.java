package com.alivc.live.pusher.demo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by pengshuang on 28/02/2018.
 */

public class HttpUrlConnectionUtil {

    private static final int CONNECTION_TIMEOUT = 10000;
    public static String doHttpsPost(String serverURL, String xmlString) throws Exception {

        try {
            URL url = new URL(serverURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "text/xml");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setRequestProperty("Cache-Control", "no-cache");
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);

            urlConnection.connect();

            OutputStream os = null;
            os = new BufferedOutputStream(
                    urlConnection.getOutputStream());
            os.write(xmlString.toString().getBytes());
            os.flush();// writing your data which you post


            StringBuffer buffer = new StringBuffer();
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line + "\r\n");
            }
            // reading your response

            is.close();
            urlConnection.disconnect();// close your connection
            return buffer.toString();
        } catch (Exception e) {

        }
        return null;
    }
}
