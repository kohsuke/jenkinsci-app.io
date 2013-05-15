package org.jenkinsci.plugins.kickfolio.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class KickfolioAppVersion {

    @SuppressWarnings("unused")
    private class Version {
        private String app_id = null;
        private String bundle_url = null;

        public Version() {
            super();
        }

        public void setApp_id(String app_id) {
            this.app_id = app_id;
        }

        public void setBundle_url(String bundle_url) {
            this.bundle_url = bundle_url;
        }
    }

    private Version version = new Version();

    public KickfolioAppVersion(String app_id, String bundle_url) {
        super();
        this.version.setApp_id(app_id);
        this.version.setBundle_url(bundle_url);
    }
}
