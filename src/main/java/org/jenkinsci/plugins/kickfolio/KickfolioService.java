package org.jenkinsci.plugins.kickfolio;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.kickfolio.model.KickfolioApp;
import org.jenkinsci.plugins.kickfolio.model.KickfolioAppObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KickfolioService {
    public String createApp(String appName, String apiKey) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpHost httpHost = new HttpHost("kickfolio.com", 443, "https");
        HttpPost httpPost = new HttpPost("/api/apps");
        ResponseHandler<String> handler = new BasicResponseHandler();
        String result = null;

        try {
            // Kickfolio Authorization and Content-Type headers
            String kickfolioAuth = "Basic " + apiKey;
            String kickfolio_v1 = "application/vnd.kickfolio.v1";

            httpPost.addHeader("Authorization", kickfolioAuth);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", kickfolio_v1);

            // Create Kickfolio App object
            KickfolioAppObject kickfolioAppObj = new KickfolioAppObject();
            kickfolioAppObj.setName(appName);

            // We want to exclude all non-annotated fields
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation().create();

            // Construct {"app": ... } message body
            KickfolioApp theApp = new KickfolioApp();
            theApp.setApp(kickfolioAppObj);
            StringEntity postBody = new StringEntity(gson.toJson(theApp),
                    ContentType.create("application/json", "UTF-8"));
            httpPost.setEntity(postBody);

            // Call Kickfolio REST API to create the new app
            HttpResponse response = httpClient.execute(httpHost, httpPost);
            String jsonKickfolioApp = handler.handleResponse(response);

            // Get JSON data from the HTTP Response
            KickfolioApp kickfolioApp = new Gson()
                    .fromJson(jsonKickfolioApp, KickfolioApp.class);
            result = kickfolioApp.getApp().getId();

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
        return result;
    }
}
