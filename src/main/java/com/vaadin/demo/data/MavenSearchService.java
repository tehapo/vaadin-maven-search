package com.vaadin.demo.data;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class MavenSearchService {

	private final Client client = ClientBuilder.newClient();
	private final WebTarget target = client
			.target("http://search.maven.org/solrsearch/select");

	public MavenSearchResponse search(String searchTerms) {
		return search(searchTerms, 0, 20);
	}

	public MavenSearchResponse search(String searchTerms, int start, int rows) {
		return target.queryParam("q", searchTerms).queryParam("start", start)
				.queryParam("rows", rows).request(MediaType.APPLICATION_JSON)
				.get(MavenSearchResponse.class);
	}

}
