/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.03.2021 12:05:29
 */
package com.mepsan.marwiz.service.hepsiburada.business;

import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.service.hepsiburada.dao.IHepsiburadaDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HepsiburadaService implements IHepsiburadaService {

    @Autowired
    private IHepsiburadaDao hepsiburadaDao;

    private String updateSendData, updateResult, updateControlResult;

    public void setHepsiburadaDao(IHepsiburadaDao hepsiburadaDao) {
        this.hepsiburadaDao = hepsiburadaDao;
    }

    @Override
    public void listHepsiburada(BranchIntegration branchIntegration) {
        try {
            String result = null;
            String url = "";
            url = branchIntegration.getHost1() + "/listings/merchantid/" + branchIntegration.getParameter1();
            result = httpClientGetHepsiburada(url, branchIntegration);
            hepsiburadaDao.processHepsiburada(result, branchIntegration, 1, "", "", false);

            updateListing(branchIntegration);
        } catch (IOException ex) {
            Logger.getLogger(HepsiburadaService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void updateListing(BranchIntegration branchIntegration) {

        try {
            updateSendData = hepsiburadaDao.findSendingHepsiburada(branchIntegration);///Değişmesi gereken ürünler geldi!!!
            updateResult = "";

            if (updateSendData.equals("<listings/>")) {

            } else {
                String url = "";
                url = branchIntegration.getHost1() + "/listings/merchantid/" + branchIntegration.getParameter1() + "/inventory-uploads";
                updateResult = httpClientPostHepsiburada(url, updateSendData, branchIntegration);
                String idResult = "";
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(updateSendData));
                Document doc;
                doc = builder.parse(inputSource);

                doc.getDocumentElement().normalize();
                NodeList nodeList1 = doc.getElementsByTagName("listing");
                if (nodeList1.getLength() > 0) {

                    DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder1;
                    builder1 = factory1.newDocumentBuilder();
                    InputSource inputSource1 = new InputSource(new StringReader(updateResult));
                    Document doc1;
                    doc1 = builder1.parse(inputSource1);

                    doc1.getDocumentElement().normalize();
                    NodeList nodeList = doc1.getElementsByTagName("Id");
                    if (nodeList.getLength() > 0) {
                        idResult = doc1.getElementsByTagName("Id").item(0).getTextContent();

                    }
                    controlUpdateListing(idResult, branchIntegration);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(HepsiburadaService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(HepsiburadaService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(HepsiburadaService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void controlUpdateListing(String updateResultId, BranchIntegration branchIntegration) {

        try {
            updateControlResult = "";
            boolean isSuccess = false;
            String updateControlResult = "";
            String url = "";
            url = branchIntegration.getHost1() + "/listings/merchantid/" + branchIntegration.getParameter1() + "/inventory-uploads/id/";
            updateControlResult = httpClientGetHepsiburada(url + updateResultId, branchIntegration);

            if (updateControlResult != null && !updateControlResult.equals("")) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(updateControlResult));
                Document doc;
                doc = builder.parse(inputSource);

                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("Errors");
                if (nodeList.getLength() == 0) {
                    isSuccess = true;
                }
            }

            hepsiburadaDao.processHepsiburada(updateSendData, branchIntegration, 2, updateResult, updateControlResult, isSuccess);

        } catch (ParserConfigurationException ex) {
            Logger.getLogger(HepsiburadaService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(HepsiburadaService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HepsiburadaService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void listHepsiburadaAsync() {
        List<BranchIntegration> branchIntegrations = hepsiburadaDao.findBranchIntegration();
        executeListHepsiburada(branchIntegrations);
    }

    @Override
    public void executeListHepsiburada(List<BranchIntegration> branchIntegrations) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (BranchIntegration br : branchIntegrations) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    listHepsiburada(br);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    public String httpClientGetHepsiburada(String url, BranchIntegration branchIntegration) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL urls = new URL(url);
            connection = (HttpURLConnection) urls.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");

            byte[] encodedAuth = Base64.getEncoder().encode((branchIntegration.getUsername1() + ":" + branchIntegration.getPassword1()).getBytes("UTF-8"));
            String authHeader = "Basic " + new String(encodedAuth);
            connection.setRequestProperty("Authorization", authHeader);
            connection.setDoOutput(true);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(branchIntegration.getTimeout1() * 1000);

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder respo = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                respo.append(line);
                respo.append('\r');
            }
            rd.close();
            return respo.toString();
        } catch (java.net.SocketTimeoutException e) {
            return "";
        } catch (IOException e) {
            return "";
        } catch (Exception e) {
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String httpClientPostHepsiburada(String url, String xml, BranchIntegration branchIntegration) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL urls = new URL(url);
            connection = (HttpURLConnection) urls.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");

            byte[] encodedAuth = Base64.getEncoder().encode((branchIntegration.getUsername1() + ":" + branchIntegration.getPassword1()).getBytes("UTF-8"));
            String authHeader = "Basic " + new String(encodedAuth);
            connection.setRequestProperty("Authorization", authHeader);
            connection.setDoOutput(true);
            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(branchIntegration.getTimeout1() * 1000);

            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] b = xml.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder respo = new StringBuilder();

            String line;
            while ((line = rd.readLine()) != null) {
                respo.append(line);
                respo.append('\r');
            }
            rd.close();
            return respo.toString();
        } catch (java.net.SocketTimeoutException e) {
            return "";
        } catch (IOException e) {
            return "";
        } catch (Exception e) {
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
}
