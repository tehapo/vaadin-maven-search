package com.vaadin.demo.data;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MavenArtifact {

    private String id;
    private String g;
    private String a;
    private String latestVersion;
    private String repositoryId;
    private String p;
    private long timestamp;
    private int versionCount;
    private String[] text;
    private String[] ec;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getVersionCount() {
        return versionCount;
    }

    public void setVersionCount(int versionCount) {
        this.versionCount = versionCount;
    }

    public String[] getText() {
        return text;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public String[] getEc() {
        return ec;
    }

    public void setEc(String[] ec) {
        this.ec = ec;
    }

    @JsonIgnore
    public String getJavaDocUrl() {
        if (Arrays.asList(text).contains("-javadoc.jar")) {
            return "http://demo.vaadin.com/javadoc/" + g + "/" + a + "/"
                    + latestVersion + "/";
        } else {
            return null;
        }
    }

}
