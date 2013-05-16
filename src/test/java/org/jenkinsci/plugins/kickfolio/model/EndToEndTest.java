package org.jenkinsci.plugins.kickfolio.model;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EndToEndTest {

    @Test
    public void test() {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            // Filepicker upload
            HttpHost httpHost = new HttpHost("www.filepicker.io", 443, "https");
            HttpPost httpPost = new HttpPost(
                    "/api/store/S3?key=AM5ozphAqSNKD6vhNfBivz");
            FileBody fileBody = new FileBody(new File(
                    "/Users/markprichard/Documents/Kickfolio/StockFish.zip"));

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("fileUpload", fileBody);
            httpPost.setEntity(reqEntity);

            HttpResponse response = null;
            ResponseHandler<String> handler = new BasicResponseHandler();

            response = httpClient.execute(httpHost, httpPost);
            String jsonFilepicker = handler.handleResponse(response);

            Filepicker filepicker = new Gson()
                    .fromJson(jsonFilepicker, Filepicker.class);

            // ulrFilepicker has the s3-backed download location
            String urlFilepicker = filepicker.getUrl();
            System.out.println("urlFilepicker: " + urlFilepicker);

            // Kickfolio Authorization and Content-Type headers
            String kickfolioAuth = "Basic UDFOZG9Gd21MdExIVjlwamtkamg=";
            String kickfolio_v1 = "application/vnd.kickfolio.v1";

            httpHost = new HttpHost("kickfolio.com", 443, "https");

            // Kickfolio POST to create new app
            httpPost = new HttpPost("/api/apps");
            httpPost.addHeader("Authorization", kickfolioAuth);
            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", kickfolio_v1);

            // Create Kickfolio App object
            KickfolioAppObject kickfolioAppObj = new KickfolioAppObject();
            kickfolioAppObj.setName("Stockfish");

            // We want to exclude all non-annotated fields
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation().create();

            // Construct {"app": ... } message body
            KickfolioApp theApp = new KickfolioApp();
            theApp.setApp(kickfolioAppObj);
            StringEntity postBody = new StringEntity(gson.toJson(theApp),
                    ContentType.create("application/json", "UTF-8"));
            httpPost.setEntity(postBody);
            System.out.println("Request: " + postBody);

            // Call Kickfolio REST API to create the new app
            response = httpClient.execute(httpHost, httpPost);
            String jsonKickfolioApp = handler.handleResponse(response);
            System.out.println("Response: " + jsonKickfolioApp);

            // Get JSON data from the HTTP Response
            KickfolioApp kickfolioApp = new Gson()
                    .fromJson(jsonKickfolioApp, KickfolioApp.class);

            // kickfolio_appid has the app_id we want
            String kickfolio_appid = kickfolioApp.getApp().getId();
            System.out.println("kickfolio_appid: " + kickfolio_appid);

            // Get all apps from Kickfolio REST API
            HttpGet httpGet = new HttpGet("/api/apps");
            httpGet.addHeader("Authorization", kickfolioAuth);
            httpGet.addHeader("Accept", kickfolio_v1);

            response = httpClient.execute(httpHost, httpGet);
            String jsonKickfolioApps = handler.handleResponse(response);
            System.out.println("Response: " + jsonKickfolioApps);

            KickfolioApps kickfolioApps = new Gson()
                    .fromJson(jsonKickfolioApps, KickfolioApps.class);
            List<KickfolioAppObject> list = Arrays.asList(kickfolioApps
                    .getApps());
            Iterator<KickfolioAppObject> iterator = list.iterator();
            while (iterator.hasNext()) {
                KickfolioAppObject app = iterator.next();
                System.out.println(app.getId());
                System.out.println(app.getName());
                System.out.println(app.getPublic_key());
            }

            // Add urlFilePicker as a new version to kickfolio_appid
            KickfolioVersionObject versionObj = new KickfolioVersionObject();
            versionObj.setApp_id(kickfolio_appid);
            versionObj.setBundle_url(urlFilepicker);

            // Construct {"version": ... } message body
            KickfolioVersion newVersion = new KickfolioVersion();
            newVersion.setVersion(versionObj);
            System.out.println("Request: " + new Gson().toJson(newVersion));

            // Send new version REST call to Kickfolio
            httpPost = new HttpPost("/api/versions");
            httpPost.addHeader("Authorization", kickfolioAuth);
            httpPost.addHeader("Accept", kickfolio_v1);
            StringEntity reqBody = new StringEntity(
                    new Gson().toJson(newVersion),
                    ContentType.create("application/json", "UTF-8"));
            httpPost.setEntity(reqBody);
            response = httpClient.execute(httpHost, httpPost);

            String jsonKickfolioVersion = handler.handleResponse(response);
            System.out.println("Response: " + jsonKickfolioVersion);

            newVersion = new Gson()
                    .fromJson(jsonKickfolioVersion, KickfolioVersion.class);
            System.out.println(newVersion.getVersion().getId());
            System.out.println(newVersion.getVersion().getApp_id());

        } catch (ClientProtocolException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
    }
}
