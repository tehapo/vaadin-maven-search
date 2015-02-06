package com.vaadin.demo;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class GroupIdHtmlConverter implements Converter<String, String> {

    private final String fallbackIconUrl = "/vaadin-maven-search-portlet/VAADIN/themes/maven-search/jar.png";
    private final String servletUrl = "/vaadin-maven-search-portlet/favicon";

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
        return "<img src=\"" + servletUrl + "?groupId=" + value
                + "\" class=\"favicon\" onerror=\"this.src = '"
                + fallbackIconUrl + "'\" /> " + value;
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
