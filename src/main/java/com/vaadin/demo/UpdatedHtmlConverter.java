package com.vaadin.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

public class UpdatedHtmlConverter implements Converter<String, Long> {

    private static final long RECENTLY_UPDATED_MILLIS = 1000L * 60 * 60 * 24
            * 30;

    @Override
    public Long convertToModel(String value, Class<? extends Long> targetType,
            Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String convertToPresentation(Long value,
            Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        Date date = new Date(value);
        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        if (recentlyUpdated(date)) {
            return dateStr + "<span class=\"badge\">New</span>";
        } else {
            return dateStr;
        }
    }

    private boolean recentlyUpdated(Date date) {
        return date != null
                && date.after(new Date(System.currentTimeMillis()
                        - RECENTLY_UPDATED_MILLIS));
    }

    @Override
    public Class<Long> getModelType() {
        return Long.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
