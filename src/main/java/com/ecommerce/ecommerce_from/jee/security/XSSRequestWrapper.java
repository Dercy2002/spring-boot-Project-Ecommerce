package com.ecommerce.ecommerce_from.jee.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

    public XSSRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return sanitize(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) return null;
        
        String[] sanitizedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            sanitizedValues[i] = sanitize(values[i]);
        }
        return sanitizedValues;
    }



    private String sanitize(String value) {
    if (value == null) return null;

    // Échapper les caractères spéciaux sans expression régulière complexe
    StringBuilder sanitized = new StringBuilder();

    for (char c : value.toCharArray()) {
        switch (c) {
            case '<' -> sanitized.append("&lt;");
            case '>' -> sanitized.append("&gt;");
            case '"' -> sanitized.append("&quot;");
            case '\'' -> sanitized.append("&#x27;");
            case '/' -> sanitized.append("&#x2F;");
            case '&' -> sanitized.append("&amp;");
            default -> sanitized.append(c);
        }
    }

    return sanitized.toString();
}


}
