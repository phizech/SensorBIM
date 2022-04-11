package com.example.sensorBIM.services;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class RestService {

    private BufferedReader br;

    public JSONObject getRequest(String url, String token) {
        try {
            url = url.replace("//", "/");
            URL object = new URL("http://" + url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setConnectTimeout(3000);
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Token " + token);

            String res = getResult(con);
            return stringToJson(res);
        } catch (Exception e) {
            return null;
        }
    }

    public HttpResponse<String> postRequest(String url, String token, String payload) throws JSONException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("content-type", "application/json")
                .header("Authorization", "Token " + token)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String v = response.body();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    private String getResult(HttpURLConnection con) throws IOException {
        StringBuilder sb = new StringBuilder();
        int HttpResult = con.getResponseCode();
        if (HttpResult == HttpURLConnection.HTTP_OK) {
            br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            closeStream();
            return sb.toString();
        } else {
            return null;
        }
    }

    public void closeStream() throws IOException {
        if (br != null) {
            br.close();
        }
    }

    public JSONObject stringToJson(String result) {
        try {
            result = result.replace("NaN", "\"NaN\"");
            return new JSONObject(result);
        } catch (JSONException err) {
            return null;
        }
    }
}
