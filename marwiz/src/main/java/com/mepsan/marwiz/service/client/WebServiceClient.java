/**
 *
 * @author Mehmet ERGÜLCÜ
 * @edit Emrullah YAKIŞAN
 * @date 18.07.2018 16:01:14
 */
package com.mepsan.marwiz.service.client;

import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WebServiceClient {

    //Aes şifresiz istek
    public String request(String endPoint, String username, String password, String data) {
        return requestMethod(endPoint, username, password, data, false, "text/xml");
    }

    //Aes şifreli istek
    public String requestAes(String endPoint, String username, String password, String data) {
        return requestMethod(endPoint, username, password, data, true, "text/xml");
    }

    //Json post işlemi için
    public String requestJson(String endPoint, String username, String password, String data) {
        return requestMethod(endPoint, username, password, data, false, "application/json");
    }

    public String requestMethod(String endPoint, String username, String password, String data, boolean isAes, String contentType) {

        AESEncryptor aes = new AESEncryptor();

        String result = null;
        HttpPost httpPost = new HttpPost(endPoint);
        try {
          
            HttpClient httpClient = createHttpClient_AcceptsUntrustedCerts();
            httpPost.addHeader("User-Agent", "Web Service Test Client");
            if (username != null) {
                httpPost.addHeader("username", username);
            }
            if (password != null) {
                httpPost.addHeader("password", password);
            }

            httpPost.setEntity(new StringEntity(data, contentType, "utf8"));

            /*int returnCode = httpClient.executeMethod(methodPost);*/
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int returnCode = httpResponse.getStatusLine().getStatusCode();
            if (returnCode != 200) {
                return "Http Status: " + returnCode;
            }
            String responseString = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

            if (contentType.equals("text/xml")) {// dönüş tipi xml ise

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(responseString));
                Document document = builder.parse(inputSource);

                NodeList nl = document.getElementsByTagName("TransactionResult");
                if (nl.getLength() > 0) {
                    result = nl.item(0).getTextContent();
                } else {
                    result = responseString;
                }
            } else if (contentType.equals("application/json")) {//json dönecek ise
                result = responseString;
            }

            if (isAes) {
                result = aes.decrypt(result);
            }

        } catch (Exception e) {
            result = e.getMessage();
            System.out.println("xmle1e=" + e.toString());

        } finally {
            try {
                httpPost.releaseConnection();
            } catch (Exception fe) {
                result = fe.getMessage();

                System.out.println("xmle1=" + fe.toString());
            }
        }
        return result;
    }

    public String requestGetMethod(String endPoint) {

        String result = null;
        HttpGet httpGet = new HttpGet(endPoint);
        try {
           
            HttpClient httpClient = createHttpClient_AcceptsUntrustedCerts();
            /* httpGet.addHeader("User-Agent", "Web Service Test Client");
            httpGet.addHeader("username", username);
            httpGet.addHeader("password", password);*/

 /*int returnCode = httpClient.executeMethod(methodPost);*/
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int returnCode = httpResponse.getStatusLine().getStatusCode();
            if (returnCode != 200) {
                result = "error";
            }
            result = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            System.out.println("xmle2e=" + e.toString());

            e.printStackTrace();
            result = e.getMessage();
            result = "error";
        } finally {
            try {
                httpGet.releaseConnection();
            } catch (Exception fe) {
                System.out.println("xmle2=" + fe.toString());

                fe.printStackTrace();
                result = fe.getMessage();
                result = "error";
            }

        }
        return result;
    }

    public static org.apache.http.client.HttpClient createHttpClient_AcceptsUntrustedCerts() throws Exception {
        HttpClientBuilder b = HttpClientBuilder.create();

        // setup a Trust Strategy that allows all certificates.
        //
        TrustStrategy trustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] xcs, String string) throws java.security.cert.CertificateException {
                return true;
            }
        };

        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, trustStrategy).build();
        b.setSslcontext(sslContext);
        // don't check Hostnames, either.
        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        // here's the special part:
        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
        //      -- and create a Registry, to register it.
        //
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // now, we create connection-manager using our Registry.
        //      -- allows multi-threaded use
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        b.setConnectionManager(connMgr);

        // finally, build the HttpClient;
        //      -- done!
        org.apache.http.client.HttpClient client = b.build();
        return client;
    }
}
