package org.jenkinsci.plugins.kickfolio.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Filepicker {
    private String url = null;
    private int filesize;
    private String type = null;
    private String key = null;
    private String filename = null;

    public Filepicker(String url, int filesize, String type, String key,
            String filename) {
        super();
        this.url = url;
        this.filesize = filesize;
        this.type = type;
        this.key = key;
        this.filename = filename;
    }

    public Filepicker() {
        super();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
