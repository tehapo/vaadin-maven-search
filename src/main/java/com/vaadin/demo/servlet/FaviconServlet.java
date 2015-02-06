package com.vaadin.demo.servlet;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * Servlet that caches requests to favicons and keeps an internal blacklist of
 * URLs not supported.
 */
@SuppressWarnings("serial")
public class FaviconServlet extends HttpServlet {

    private static final List<String> domainBlacklist = new CopyOnWriteArrayList<String>();
    private static Cache<String, byte[]> faviconCache;
    private static OkHttpClient client = new OkHttpClient();

    static {
        faviconCache = CacheBuilder.newBuilder().maximumSize(100)
                .expireAfterWrite(30, TimeUnit.MINUTES).build();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String groupId = req.getParameter("groupId");
        if (groupId == null || groupId.length() == 0) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            final String[] domainParts = groupId.split("\\.");
            final String domain = (domainParts.length >= 2 ? domainParts[1]
                    + "." + domainParts[0] : null);

            if (domain != null && !domainBlacklist.contains(domain)) {
                try {
                    byte[] bytes = faviconCache.get(domain,
                            new Callable<byte[]>() {

                                @Override
                                public byte[] call() throws Exception {
                                    // Actual HTTP request.
                                    return getFaviconBytes(domain);
                                }

                            });
                    resp.getOutputStream().write(bytes);
                } catch (Exception e) {
                    // Blacklist the domain to avoid subsequent calls.
                    domainBlacklist.add(domain);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    private byte[] getFaviconBytes(final String domain) throws IOException {
        final String url = "http://" + domain + "/favicon.ico";
        Request request = new Request.Builder().url(url).build();
        return client.newCall(request).execute().body().bytes();
    }
}
