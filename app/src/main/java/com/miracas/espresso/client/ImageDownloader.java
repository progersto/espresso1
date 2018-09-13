package com.miracas.espresso.client;


import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.design.IOnRequestCompleted;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDownloader extends AsyncTask<Void, Void, String> {

    private File directory;
    private IOnRequestCompleted context;
    private String imageUrl;
    private String fileName;
    private String message;
    private File output;

    public ImageDownloader(IOnRequestCompleted context, String url, String message, File directory) {
        super();
        this.imageUrl = url;
        this.fileName = "miracas_share" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        this.context = context;
        this.message = message;
        this.directory = directory;
    }

    @Override
    protected void onPostExecute(String response) {
        Response response1 = new Response();
        response1.message = message;
        response1.fileName = fileName;
        response1.output = output;
        context.onRequestComplete(ClientCodes.IMAGE_DOWNLOADER, response1);
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            output = new File(directory, fileName);

            FileOutputStream fileOutput = new FileOutputStream(output);
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }

            fileOutput.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class Response {
        public String message;
        public String fileName;
        public File output;
    }

}
