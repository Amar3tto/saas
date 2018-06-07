package ru.kpfu.itis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.base.codec.binary.Base64;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.kpfu.itis.config.PaymentConfig;
import ru.kpfu.itis.dto.PaypalTokenDto;
import ru.kpfu.itis.dto.PaypalUserDto;

@Service
public class SaasPaypalAPIService {

    private final Logger LOGGER = LoggerFactory.getLogger(SaasPaypalAPIService.class);

    @Autowired
    private PaymentConfig paymentConfig;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private ObjectMapper objectMapper;

    public String getUserEmail(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        String uriString = UriComponentsBuilder
                .fromUriString(paymentConfig.getPaypalApiUrl() + "/v1/identity/openidconnect/userinfo")
                .queryParam("schema", "openid")
                .build().toUriString();
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(15000)
                .setSocketTimeout(15000)
                .setConnectionRequestTimeout(15000)
                .build();
        HttpGet httpGet = new HttpGet(uriString);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("Authorization", "Bearer " + accessToken);
        try (CloseableHttpResponse execute = httpClient.execute(httpGet)) {
            if (execute.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("Error. Reason: " + execute.getStatusLine().getReasonPhrase());
            }
            PaypalUserDto userDto = objectMapper.readValue(execute.getEntity().getContent(), PaypalUserDto.class);
            return userDto.getEmail();
        } catch (Exception e) {
            LOGGER.error("Paypal auth failed", e);
        }
        return null;
    }

    public String getAccessToken(String code) {
        String uriString = UriComponentsBuilder
                .fromUriString(paymentConfig.getPaypalApiUrl() + "/v1/identity/openidconnect/tokenservice")
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .build().toUriString();

        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectTimeout(15000)
                .setSocketTimeout(15000)
                .setConnectionRequestTimeout(15000)
                .build();
        HttpPost httpPost = new HttpPost(uriString);
        httpPost.setConfig(requestConfig);
        String clientId = paymentConfig.getPayPalClientId();
        String clientSecret = paymentConfig.getPayPalClientSecret();
        String authKey = Base64.encodeBase64String((clientId + ":" + clientSecret).getBytes());
        httpPost.setHeader("Authorization", "Basic " + authKey);
        try (CloseableHttpResponse execute = httpClient.execute(httpPost)) {
            if (execute.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("Error. Reason: " + execute.getStatusLine().getReasonPhrase());
            }
            PaypalTokenDto tokenDto = objectMapper.readValue(execute.getEntity().getContent(), PaypalTokenDto.class);
            return tokenDto.getAccessToken();
        } catch (Exception e) {
            LOGGER.error("Paypal auth failed", e);
        }
        return null;
    }
}
