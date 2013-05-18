package org.jenkinsci.plugins.kickfolio;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.kickfolio.model.KickfolioApp;
import org.jenkinsci.plugins.kickfolio.model.KickfolioAppObject;
import org.jenkinsci.plugins.kickfolio.model.KickfolioApps;
import org.jenkinsci.plugins.kickfolio.model.KickfolioVersion;
import org.jenkinsci.plugins.kickfolio.model.KickfolioVersionObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class KickfolioService {

    private final HttpHost httpHost = new HttpHost("kickfolio.com", 443,
            "https");
    private final HttpPost httpPost = new HttpPost("/api/apps");
    private final HttpPost httpPostVersions = new HttpPost("/api/versions");
    private final HttpGet httpGet = new HttpGet("/api/apps");
    private final String kickfolio_v1 = "application/vnd.kickfolio.v1";

    public KickfolioAppObject createApp(String appName, String apiKey) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        KickfolioAppObject theAppObject = new KickfolioAppObject();

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
            System.out.println("Request: " + gson.toJson(theApp));

            // Call Kickfolio REST API to create the new app
            HttpResponse response = httpClient.execute(httpHost, httpPost);
            String jsonKickfolioApp = handler.handleResponse(response);
            System.out.println("Response: " + jsonKickfolioApp);

            // Get JSON data from the HTTP Response
            KickfolioApp kickfolioApp = new Gson()
                    .fromJson(jsonKickfolioApp, KickfolioApp.class);
            theAppObject = kickfolioApp.getApp();

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
        return theAppObject;
    }

    public KickfolioAppObject findApp(String appName, String apiKey) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        KickfolioAppObject theApp = new KickfolioAppObject();

        try {
            // Kickfolio Authorization and Content-Type headers
            String kickfolioAuth = "Basic " + apiKey;
            httpGet.addHeader("Authorization", kickfolioAuth);
            httpGet.addHeader("Accept", kickfolio_v1);

            HttpResponse response = httpClient.execute(httpHost, httpGet);
            String jsonKickfolioApps = handler.handleResponse(response);
            System.out.println("Response: " + jsonKickfolioApps);

            KickfolioApps kickfolioApps = new Gson()
                    .fromJson(jsonKickfolioApps, KickfolioApps.class);
            List<KickfolioAppObject> list = Arrays.asList(kickfolioApps
                    .getApps());
            Iterator<KickfolioAppObject> iterator = list.iterator();

            boolean foundAppName = false;
            while ((iterator.hasNext()) && (!foundAppName)) {
                KickfolioAppObject thisApp = iterator.next();
                if (thisApp.getName().equals(appName)) {
                    theApp = thisApp;
                }
            }

        } catch (Exception e) {
            throw e;
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
        return theApp;
    }

    public KickfolioVersionObject addVersion(String appId, String urlUpload, String apiKey) throws Exception {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        KickfolioVersion newVersion = new KickfolioVersion();
        KickfolioVersionObject versionObj = new KickfolioVersionObject();

        try {
            // Construct {"version": ... } message body
            versionObj.setApp_id(appId);
            versionObj.setBundle_url(urlUpload);
            newVersion.setVersion(versionObj);
            System.out.println("Request: " + new Gson().toJson(newVersion));

            // Send new version REST call to Kickfolio
            httpPostVersions.addHeader("Authorization", "Basic " + apiKey);
            httpPostVersions.addHeader("Content-Type", "application/json");
            httpPostVersions.addHeader("Accept", kickfolio_v1);
            StringEntity reqBody = new StringEntity(
                    new Gson().toJson(newVersion),
                    ContentType.create("application/json", "UTF-8"));
            httpPostVersions.setEntity(reqBody);
            HttpResponse response = httpClient
                    .execute(httpHost, httpPostVersions);

            String jsonKickfolioVersion = handler.handleResponse(response);
            System.out.println("Response: " + jsonKickfolioVersion);
            newVersion = new Gson()
                    .fromJson(jsonKickfolioVersion, KickfolioVersion.class);

        } catch (Exception e) {
            e.getStackTrace();
            throw e;
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
        return newVersion.getVersion();
    }
}
