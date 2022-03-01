/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2021 03:08:12
 */
package com.mepsan.marwiz.system.hepsiburadaintegration.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.model.inventory.ECommerceStock;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.system.hepsiburadaintegration.dao.IHepsiburadaIntegrationDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HepsiburadaIntegrationService implements IHepsiburadaIntegrationService {

    @Autowired
    private IHepsiburadaIntegrationDao hepsiburadaIntegrationDao;

    @Autowired
    SessionBean sessionBean;

    private String updateSendData, updateResult, updateControlResult;
    int returnCode;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setHepsiburadaIntegrationDao(IHepsiburadaIntegrationDao hepsiburadaIntegrationDao) {
        this.hepsiburadaIntegrationDao = hepsiburadaIntegrationDao;
    }

//    @Override
//    public List<ECommerceStock> listingStock(int first, int pageSize, BranchIntegration branchIntegration) {
//        System.out.println("---------listStock");
//        String result = null;
//        List<ECommerceStock> listOfStocks = new ArrayList<>();
//        try {
//            String parameter = "?offset=" + first + "&limit=" + pageSize + "";
//            String url = branchIntegration.getHost1() + "/listings/merchantid/" + branchIntegration.getParameter1();
//            result = httpClientGetHepsiburada(url + parameter, branchIntegration);
//            System.out.println("------result" + result);
//            if (returnCode == 200) {
//                if (result != null && !result.equals("")) {
//
//                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                    DocumentBuilder builder;
//                    builder = factory.newDocumentBuilder();
//                    InputSource inputSource = new InputSource(new StringReader(result));
//                    Document doc = builder.parse(inputSource);
//
//                    doc.getDocumentElement().normalize();
//
//                    pagingCount = Integer.parseInt(doc.getElementsByTagName("TotalCount").item(0).getTextContent());
//                    listOfStocks = findStockName(result);
//                }
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SAXException ex) {
//            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ParserConfigurationException ex) {
//            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return listOfStocks;
//
//    }
    @Override
    public String listingStock(BranchIntegration branchIntegration) {
        String result = null;
        try {

        String url = branchIntegration.getHost1() + "/listings/merchantid/" + branchIntegration.getParameter1();
        result = httpClientGetHepsiburada(url, branchIntegration);

        } catch (IOException ex) {
            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public int count(String where) {
        return hepsiburadaIntegrationDao.count(where);
    }

    @Override
    public boolean updateListing(List<ECommerceStock> listOfCommerceStock, BranchIntegration branchIntegration, boolean isRemoveFromSale, boolean isSendAllStock, String tempSendData) {
        boolean isSuccess = false;
        try {
            updateResult = "";
            updateSendData = "";
            if (!isSendAllStock) {

                updateSendData = "<listings xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">";
                for (ECommerceStock obj : listOfCommerceStock) {
                    int availableStock = obj.getMarwizAvailableStock().intValue();
                    String price;
                    if (isRemoveFromSale) {//Satıştan kaldır
                        price = "0";
                    } else {
                        if (obj.getMarwizPrice().compareTo(BigDecimal.valueOf(0)) != -1) {
                            NumberFormat formatterUnit = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
                            DecimalFormatSymbols decimalFormatSymbolsUnit = ((DecimalFormat) formatterUnit).getDecimalFormatSymbols();
                            decimalFormatSymbolsUnit.setMonetaryDecimalSeparator(',');
                            //decimalFormatSymbolsUnit.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
                            decimalFormatSymbolsUnit.setCurrencySymbol("");
                            ((DecimalFormat) formatterUnit).setDecimalFormatSymbols(decimalFormatSymbolsUnit);
                            formatterUnit.setMaximumFractionDigits(2);
                            formatterUnit.setMinimumFractionDigits(2);
                            price = formatterUnit.format(obj.getMarwizPrice()).trim().replace(".", "");
                        } else {
                            price = "0";
                        }

                    }

                    if (availableStock < 0) {
                        availableStock = 0;
                    }

                    updateSendData += "<listing>";
                    updateSendData += "<HepsiburadaSku>" + obj.getHepsiburadaSku() + "</HepsiburadaSku>";
                    updateSendData += "<MerchantSku>" + obj.getMerchantSku() + "</MerchantSku>";
                    updateSendData += "<ProductName>" + obj.getStock().getName() + "</ProductName>";
                    updateSendData += "<Price>" + price + "</Price>";
                    updateSendData += "<AvailableStock>" + availableStock + "</AvailableStock>";
                    updateSendData += "<DispatchTime>" + "0" + "</DispatchTime>";
                    updateSendData += "<CargoCompany1>" + (obj.getCargoCompany1() == null ? "" : obj.getCargoCompany1()) + "</CargoCompany1>";
                    updateSendData += "<CargoCompany2>" + (obj.getCargoCompany2() == null ? "" : obj.getCargoCompany2()) + "</CargoCompany2>";
                    updateSendData += "<CargoCompany3>" + (obj.getCargoCompany3() == null ? "" : obj.getCargoCompany3()) + "</CargoCompany3>";
                    updateSendData += "</listing>";
                }
                updateSendData = updateSendData + "</listings>";
            } else {
                updateSendData = tempSendData;
            }

            if (updateSendData.equals("<listings/>")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("sendingdoesntoccurredbecausethereisnochangeinstocksinformation")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                String url = branchIntegration.getHost1() + "/listings/merchantid/" + branchIntegration.getParameter1() + "/inventory-uploads";
                updateResult = httpClientPostHepsiburada(url, updateSendData, branchIntegration);
                if (returnCode == 200) {
                    String idResult = "";
                    String message = "";
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(updateResult));
                    Document doc = builder.parse(inputSource);

                    doc.getDocumentElement().normalize();
                    NodeList nodeList = doc.getElementsByTagName("Id");
                    if (nodeList.getLength() > 0) {
                        idResult = doc.getElementsByTagName("Id").item(0).getTextContent();
                        if (idResult != null && !idResult.equals("")) {
                        } else {
                            NodeList nodeListError = doc.getElementsByTagName("Message");
                            if (nodeListError.getLength() > 0) {
                                message = doc.getElementsByTagName("Message").item(0).getTextContent();
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), message));
                                RequestContext.getCurrentInstance().update("grwProcessMessage");
                            }

                        }
                    } else {
                        NodeList nodeListError = doc.getElementsByTagName("Message");
                        if (nodeListError.getLength() > 0) {
                            message = doc.getElementsByTagName("Message").item(0).getTextContent();
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), message));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");
                        }
                    }
                    isSuccess = controlUpdateListing(idResult, branchIntegration);
                }
            }

        } catch (IOException | SAXException | ParserConfigurationException ex) {
            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isSuccess;

    }

    public boolean controlUpdateListing(String updateResultId, BranchIntegration branchIntegration) {
        boolean isSuccess = false;
        try {

            updateControlResult = "";
            String url = branchIntegration.getHost1() + "/listings/merchantid/" + branchIntegration.getParameter1() + "/inventory-uploads/id/";
            updateControlResult = httpClientGetHepsiburada(url + updateResultId, branchIntegration);

            if (returnCode == 200) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(updateControlResult));
                Document doc = builder.parse(inputSource);

                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("Errors");

                if (nodeList.getLength() > 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    //  System.out.println("-----Başarısız");
                } else {
                    isSuccess = true;
                    //  System.out.println("-----Başarılı");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
                hepsiburadaIntegrationDao.updateListing(updateSendData, updateResult, updateControlResult, isSuccess);
            }

        } catch (IOException | SAXException | ParserConfigurationException e) {
            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, e);
        }
        return isSuccess;
    }

    public String httpClientGetHepsiburada(String url, BranchIntegration branchIntegration) throws IOException {
        HttpURLConnection connection = null;
        returnCode = 0;
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
                returnCode = connection.getResponseCode();

                while ((line = rd.readLine()) != null) {
                    respo.append(line);
                    respo.append('\r');
                }
            rd.close();
            bringMessage();
            return respo.toString();
        } catch (java.net.SocketTimeoutException e) {
            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, e);
            return "";
        } catch (IOException e) {
            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, e);
            return "";
        } catch (Exception e) {
            Logger.getLogger(HepsiburadaIntegrationService.class.getName()).log(Level.SEVERE, null, e);
            return "";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public String httpClientPostHepsiburada(String url, String xml, BranchIntegration branchIntegration) throws IOException {
        HttpURLConnection connection = null;
        returnCode = 0;
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
            returnCode = connection.getResponseCode();

            String line;
            while ((line = rd.readLine()) != null) {
                respo.append(line);
                respo.append('\r');
            }
            rd.close();
            bringMessage();
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

    public void bringMessage() {
        switch (returnCode) {
            case 400:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("controlinurlparameter")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case 401:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("usernameorpasswordisincorrect")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case 404:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("urlisincorrect")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case 405:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("httpprotocolerror")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case 500:
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("contactintegrationteamwithticket")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
        }
    }

    @Override
    public List<ECommerceStock> bringListing(String stockList, int first, int pageSize, boolean isBringListing, String where) {
        return hepsiburadaIntegrationDao.bringListing(stockList, first, pageSize, isBringListing, where);
    }

    @Override
    public String createWhere(ECommerceStock obj) {
        String where = "";
        String stockList = "";
        for (Stock stock : obj.getStockList()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());
            where = where + " AND stck.id IN(" + stockList + ") ";
        }
        return where;
    }

    @Override
    public String findSendingHepsiburada() {
        return hepsiburadaIntegrationDao.findSendingHepsiburada();
    }

}
