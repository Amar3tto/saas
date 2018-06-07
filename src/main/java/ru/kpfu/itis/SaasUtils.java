package ru.kpfu.itis;

import ru.kpfu.itis.model.BillingUser;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class SaasUtils {

    public abstract BillingUser getCurrentUser(HttpServletRequest request);

    public static String extractDomain(HttpServletRequest request) {
        String domain = "";
        try {
            URL url = new URL(request.getRequestURL().toString());
            domain = url.getProtocol() + (request.isSecure() ? "s" : "") + "://" + url.getHost();
            if (url.getPort() != -1) {
                domain += ":" + url.getPort();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return domain;
    }

}
