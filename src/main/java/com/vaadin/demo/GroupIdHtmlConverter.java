package com.vaadin.demo;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.demo.servlet.FaviconServlet;

public class GroupIdHtmlConverter implements Converter<String, String> {

    private final String fallbackIconUrl = "/VAADIN/themes/maven-search/jar.png";
    private final String servletUrl = "/favicon";

    @Override
    public String convertToModel(String value,
            Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String convertToPresentation(String value,
            Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return "<img src=\"" + FaviconServlet.contextPath + servletUrl
                + "?groupId=" + value
                + "\" class=\"favicon\" onerror=\"this.src = '"
                + FaviconServlet.contextPath + fallbackIconUrl + "'\" /> "
                + value;
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
