package com.vaadin.demo;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class GroupIdHtmlConverter implements Converter<String, String> {

    private final String fallbackIconUrl = "/vaadin-maven-search-portlet/VAADIN/themes/maven-search/jar.png";

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
        String[] domainParts = value.split("\\.");
        String faviconUrl = "";
        if (domainParts.length >= 2) {
            faviconUrl = "http://" + domainParts[1] + "." + domainParts[0]
                    + "/favicon.ico";
        } else {
            faviconUrl = fallbackIconUrl;
        }
        return "<img src=\"" + faviconUrl
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
