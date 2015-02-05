package com.vaadin.demo;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class PomHtmlConverter implements Converter<String, String> {

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
        return "+";
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