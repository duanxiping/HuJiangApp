package com.hujiang.hujiangapp.api;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HuJiangServer {
    private static final boolean isTesting = false;
    private static OkHttpClient defaultHttpClient = null;

    public static String baseUrl() {
        if (isTesting) {
            return "http://192.168.1.176:7878/";
        } else {
            return "https://rz.hj-tec.com/api/";
        }
    }

    public static ApiService createService() {
        OkHttpClient client = getDefaultHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }

    private static OkHttpClient getDefaultHttpClient() {
        if (defaultHttpClient == null) {
            OkHttpClient.Builder httpClientBuilder = getUnsafeOkHttpClientBuilder().addInterceptor(new MyInterceptor());
//            httpClientBuilder.writeTimeout(30, TimeUnit.SECONDS);
//            httpClientBuilder.readTimeout(30, TimeUnit.SECONDS);
            defaultHttpClient = httpClientBuilder.build();
        }
        return defaultHttpClient;
    }

    private static OkHttpClient.Builder getUnsafeOkHttpClientBuilder() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
