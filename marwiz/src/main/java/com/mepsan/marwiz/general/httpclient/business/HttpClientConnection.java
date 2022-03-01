/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 10.05.2017 19:05:16
 */
package com.mepsan.marwiz.general.httpclient.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpClientConnection {

    private HttpURLConnection connection = null;
    private HttpsURLConnection connectionSSL = null;
    private String ip;
    private String port;
    private String parameters;
    private int timeout;

    public HttpClientConnection(String ip, String port, String parameters, int timeoutSecond) {
        this.ip = ip;
        this.port = port;
        this.parameters = parameters;
        this.timeout = timeoutSecond;//saniye gelir
    }

    public HttpClientConnection(String ip, int timeoutSecond) {
        this.ip = ip;
        this.timeout = timeoutSecond;//saniye gelir
    }

    public String connect() {
        try {
            System.out.println("http://" + ip + ":" + port + "/" + parameters);
            URL url = new URL("http://" + ip + ":" + port + "/" + parameters);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(parameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setConnectTimeout(timeout * 1000);
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder respo = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
             while ((line = rd.readLine()) != null) {
                respo.append(line);
                respo.append('\r');
            }
            rd.close();
            return respo.toString();
        } catch (java.net.SocketTimeoutException e) {
            e.printStackTrace();
            return "error";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String connectWithSSLParameter(String parameter) {
        try {
            System.out.println("ip=" + ip);
            URL url = new URL(ip);
            connectionSSL = (HttpsURLConnection) url.openConnection();
            connectionSSL.setRequestMethod("GET");
            connectionSSL.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connectionSSL.setRequestProperty("Content-Length", Integer.toString(parameter.getBytes().length));
            connectionSSL.setRequestProperty("Content-Language", "en-US");
            connectionSSL.setUseCaches(false);
            connectionSSL.setDoOutput(true);
            connectionSSL.setConnectTimeout(timeout * 1000);
            InputStream is = connectionSSL.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder respo = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                respo.append(line);
                respo.append('\r');
            }
            rd.close();
            return respo.toString();
        } catch (java.net.SocketTimeoutException e) {
            System.out.println("e=" + e.getMessage());
            System.out.println("e=" + e.toString());

            return "error";
        } catch (IOException e) {
            System.out.println("e=" + e.getMessage());
            System.out.println("e=" + e.toString());

            return "error";
        } catch (Exception e) {
            System.out.println("e=" + e.getMessage());
            System.out.println("e=" + e.toString());

            return "error";
        } finally {
            if (connectionSSL != null) {
                connectionSSL.disconnect();
            }
        }
    }

}
