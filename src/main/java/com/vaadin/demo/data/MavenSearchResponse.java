package com.vaadin.demo.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "status", "spellcheck" })
public class MavenSearchResponse {

    private ResponseHeader responseHeader;
    private Response response;

    @JsonIgnore
    public List<MavenArtifact> getArtifacts() {
        return getResponse().docs;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public static class Response {
        private int numFound;
        private int start;
        private List<MavenArtifact> docs;

        public int getNumFound() {
            return numFound;
        }

        public void setNumFound(int numFound) {
            this.numFound = numFound;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public List<MavenArtifact> getDocs() {
            return docs;
        }

        public void setDocs(List<MavenArtifact> docs) {
            this.docs = docs;
        }
    }

    @JsonIgnoreProperties({ "QTime", "params", "sort" })
    public static class ResponseHeader {
        private int status;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
