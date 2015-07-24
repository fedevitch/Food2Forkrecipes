package com.example.lyubomyr.food2forkrecipes;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Lyubomyr on 13.07.2015.
 */
public class RecipesQuery extends AsyncTask<String, Void, String> {
    public static String LOG_TAG = "log";
    private HttpURLConnection urlConnection;
    private BufferedReader reader;
    private String resultJson;
    private String requestURL;

    public RecipesQuery(){
        super.onPreExecute();
        urlConnection = null;
        reader = null;
        resultJson = "";
    }

    @Override
    protected String doInBackground(String... requestURL) {
        this.requestURL = requestURL[0];
        try {

            URL url = new URL(this.requestURL);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);
        Log.d(LOG_TAG, strJson);
        //stringJson = strJson;
    }
}
