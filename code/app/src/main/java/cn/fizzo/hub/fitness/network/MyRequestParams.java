package cn.fizzo.hub.fitness.network;

import android.content.Context;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.http.app.DefaultParamsBuilder;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.fizzo.hub.fitness.LocalApp;

/**
 * Created by Raul.fan on 2018/1/23 0023.
 * Mail:raul.fan@139.com
 * QQ: 35686324
 */

public class MyRequestParams extends RequestParams {


    public MyRequestParams(Context context, String url) {
        super(url);

        //创建头信息
        this.setSslSocketFactory(trustAllSSlSocketFactory);
    }

    private static SSLSocketFactory trustAllSSlSocketFactory;

    public static SSLSocketFactory getTrustAllSSLSocketFactory() {
        if (trustAllSSlSocketFactory == null) {
            synchronized (DefaultParamsBuilder.class) {
                if (trustAllSSlSocketFactory == null) {

                    // 信任所有证书
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }};
                    try {
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        sslContext.init(null, trustAllCerts, null);
                        trustAllSSlSocketFactory = sslContext.getSocketFactory();
                    } catch (Throwable ex) {
                        LogUtil.e(ex.getMessage(), ex);
                    }
                }
            }
        }
        return trustAllSSlSocketFactory;
    }

}
