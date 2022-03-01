package com.mepsan.marwiz.general.httpclient.business;

/**
 *
 * @author Samet Dağ
 * @date 08.11.2018
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Screen01Connection {

    private HttpURLConnection connection = null;

    String ipAddress;

    public Screen01Connection(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String connect() {

        try {
            URL url = new URL(ipAddress);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setRequestProperty("charset", "windows-1254");
            connection.setReadTimeout(2000);
            if (connection != null) {
                if (connection.getInputStream() != null) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "ISO-8859-9"));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();
                } else {
                    return "error";
                }
            } else {
                return "error";
            }
        } catch (java.net.SocketTimeoutException e) {
            return "timeout";
        } catch (java.io.IOException e) {
            return "error";
        } catch (Exception e) {
            return "error";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
