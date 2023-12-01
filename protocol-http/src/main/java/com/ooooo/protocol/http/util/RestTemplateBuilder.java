package com.ooooo.protocol.http.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * 2022/3/8 14:10
 * @since 1.0.0
 */
@Slf4j
public class RestTemplateBuilder {

    private int readTimeout = 60 * 1000 * 3;

    private int connectTimeout = 10 * 1000;

    private boolean isHttps;

    private InputStream cert;

    private String tlsVersion = "TLSv1.2";

    public static RestTemplateBuilder builder() {
        return new RestTemplateBuilder();
    }

    public RestTemplateBuilder readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RestTemplateBuilder connectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public RestTemplateBuilder cert(InputStream cert) {
        this.cert = cert;
        return this;
    }

    public RestTemplateBuilder isHttps(boolean isHttps) {
        this.isHttps = isHttps;
        return this;
    }

    public RestTemplateBuilder tlsVersion(String tlsVersion) {
        this.tlsVersion = tlsVersion;
        return this;
    }

    public RestTemplate build() {
        OkHttpClient client;
        if (isHttps) {
            Builder builder = new Builder().connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS));

            SSLSocketClient sslSocketClient = new SSLSocketClient(cert, tlsVersion);
            builder.sslSocketFactory(sslSocketClient.getSslSocketFactory(), sslSocketClient.getX509TrustManager());
            builder.hostnameVerifier(sslSocketClient.getHostnameVerifier());

            client = builder.build();
        } else {
            client = new Builder().build();
        }

        OkHttp3ClientHttpRequestFactory requestFactory = new OkHttp3ClientHttpRequestFactory(client);
        requestFactory.setReadTimeout(this.readTimeout);
        requestFactory.setConnectTimeout(this.connectTimeout);
        RestTemplate template = new RestTemplate(requestFactory);

        // message converter
        for (HttpMessageConverter<?> messageConverter : template.getMessageConverters()) {
            if (messageConverter instanceof StringHttpMessageConverter) {
                // utf8
                ((StringHttpMessageConverter) messageConverter).setDefaultCharset(StandardCharsets.UTF_8);
            } else if (messageConverter instanceof MappingJackson2HttpMessageConverter) {
                // jackson, 过滤null值
                ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) messageConverter).getObjectMapper();
                objectMapper.setSerializationInclusion(Include.NON_NULL);
            }
        }

        // 可以接受其他类型的状态码， 例如400
        template.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        });
        return template;
    }

    private static class SSLSocketClient {

        @Getter
        private SSLSocketFactory sslSocketFactory;

        @Getter
        private X509TrustManager x509TrustManager;

        @Getter
        private HostnameVerifier hostnameVerifier;

        @SneakyThrows
        public SSLSocketClient(InputStream cert, String tlsVersion) {
            x509TrustManager = createTrustManager(cert);
            sslSocketFactory = createSslSocketFactory(tlsVersion);
            hostnameVerifier = createHostnameVerifier();
        }

        private SSLSocketFactory createSslSocketFactory(String tlsVersion) throws NoSuchAlgorithmException, KeyManagementException {
            SSLContext sslContext = SSLContext.getInstance(tlsVersion);
            sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
            return sslContext.getSocketFactory();
        }

        private HostnameVerifier createHostnameVerifier() {
            return (s, sslSession) -> true;
        }

        @SneakyThrows
        private KeyStore createKeyStore(InputStream cert) {
            if (cert == null) {
                return null;
            }

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(cert);
            if (certificates.isEmpty()) {
                throw new IllegalArgumentException("expected non-empty set of trusted certificates");
            }

            // Put the certificates a key store.
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (Certificate certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificate);
            }

            return keyStore;
        }

        private X509TrustManager createTrustManager(InputStream cert) {
            KeyStore keyStore = createKeyStore(cert);
            if (keyStore == null) {
                return trustAllCertificates();
            }

            X509TrustManager trustManager = null;
            try {
                // Use it to build an X509 trust manager.
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }

                trustManager = (X509TrustManager) trustManagers[0];
            } catch (Throwable t) {
                log.error("trustManagerForCertificates", t);
            }
            return trustManager;
        }

        private X509TrustManager trustAllCertificates() {
            return new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
        }
    }
}
