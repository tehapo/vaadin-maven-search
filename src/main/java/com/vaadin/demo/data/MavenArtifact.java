package com.vaadin.demo.data;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MavenArtifact {

    private static final String JAVADOC_TEMPLATE = "http://demo.vaadin.com/javadoc/%s/%s/%s/";
    private static final String POM_TEMPLATE = "<dependency>\n    <groupId>%s</groupId>\n    <artifactId>%s</artifactId>\n    <version>%s</version>\n</dependency>";

    private String id;

    @JsonProperty("g")
    private String groupId;

    @JsonProperty("a")
    private String artifactId;

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
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
            return String.format(JAVADOC_TEMPLATE, groupId, artifactId,
                    latestVersion);
        } else {
            return null;
        }
    }

    @JsonIgnore
    public String getPomSnippet() {
        return String.format(POM_TEMPLATE, groupId, artifactId, latestVersion);
    }

}
