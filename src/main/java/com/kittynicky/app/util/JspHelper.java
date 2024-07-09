package com.kittynicky.app.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JspHelper {
    private static final String JSP_PAGE_FORMAT = "/WEB-INF/jsp/%s.jsp";
    private static final String JSP_ERROR_FORMAT = "/WEB-INF/error/%s.jsp";

    public static String getPath(String jspName) {
        return String.format(JSP_PAGE_FORMAT, jspName);
    }

}
