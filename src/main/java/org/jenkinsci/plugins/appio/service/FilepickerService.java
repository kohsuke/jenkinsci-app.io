package org.jenkinsci.plugins.appio.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.appio.model.Filepicker;

import com.google.gson.Gson;

public class FilepickerService implements Serializable {
    private static final long serialVersionUID = 1L;

    private Logger logger = null;
    private String apiKey = null;
    
    public FilepickerService(String apiKey) {
		super();
		this.apiKey = apiKey;
	}

	public void setLogger(Logger logger) {
        this.logger = logger;
    }

    static interface Logger {
        void logDebug(String message);
    }

    private void logDebug(String message) {
        if (logger != null) {
            logger.logDebug(message);
        } else {
            System.out.println(message);
        }
    }

    public String getUploadURL(String filePath) throws Exception {
        Filepicker filepicker = null;
        DefaultHttpClient httpClient = new DefaultHttpClient();

        try {
            // Filepicker upload
            HttpHost httpHost = new HttpHost("www.filepicker.io", 443, "https");
            HttpPost httpPost = new HttpPost("/api/store/S3?key=" + apiKey);
            FileBody fileBody = new FileBody(new File(filePath));

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("fileUpload", fileBody);
            httpPost.setEntity(reqEntity);

            ResponseHandler<String> handler = new BasicResponseHandler();
            HttpResponse response = httpClient.execute(httpHost, httpPost);
            String filepickerResponse = handler.handleResponse(response);
            logDebug("FilepickerService.getUploadURL() Response: "
                    + filepickerResponse);

            filepicker = new Gson()
                    .fromJson(filepickerResponse, Filepicker.class);

        } catch (ClientProtocolException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }

        return filepicker.getUrl();
    }
}
