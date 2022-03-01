/**
 *
 * @author elif.mart
 */
package com.mepsan.marwiz.system.einvoiceintegration.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Exchange;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.log.IncomingEInvoice;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IIncomingEInvoicesDao;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IncomingInvoicesItem;
import com.mepsan.marwiz.system.einvoiceintegration.dao.EInvoice;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import sun.misc.BASE64Decoder;

public class IncomingEInvoicesService implements IIncomingEInvoicesService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IIncomingEInvoicesDao incomingEInvoicesDao;

    List<IncomingInvoicesItem> listInvoiceItem = new ArrayList<>();
    List<Currency> listCurrency = new ArrayList<>();
    List<IncomingEInvoice> listOfGetInvoices = new ArrayList<>();
    List<IncomingEInvoice> tempListOfGetInvoices = new ArrayList<>();
    List<EInvoice> listInvoice = new ArrayList<>();
    boolean isDiscount = true;
    int tempRequestNumber;
    boolean isError = false;
    String approvalMessage = "";

    public String getApprovalMessage() {
        return approvalMessage;
    }

    public void setApprovalMessage(String approvalMessage) {
        this.approvalMessage = approvalMessage;
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }

    public int getTempRequestNumber() {
        return tempRequestNumber;
    }

    public void setTempRequestNumber(int tempRequestNumber) {
        this.tempRequestNumber = tempRequestNumber;
    }

    public List<Currency> getListCurrency() {
        return listCurrency;
    }

    public void setListCurrency(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
    }

    public boolean isIsDiscount() {
        return isDiscount;
    }

    public void setIsDiscount(boolean isDiscount) {
        this.isDiscount = isDiscount;
    }

    public List<IncomingInvoicesItem> getListInvoiceItem() {
        return listInvoiceItem;
    }

    public void setListInvoiceItem(List<IncomingInvoicesItem> listInvoiceItem) {
        this.listInvoiceItem = listInvoiceItem;
    }

    public void setIncomingEInvoicesDao(IIncomingEInvoicesDao incomingEInvoicesDao) {
        this.incomingEInvoicesDao = incomingEInvoicesDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<IncomingEInvoice> getTempListOfGetInvoices() {
        return tempListOfGetInvoices;
    }

    public void setTempListOfGetInvoices(List<IncomingEInvoice> tempListOfGetInvoices) {
        this.tempListOfGetInvoices = tempListOfGetInvoices;
    }

    public List<EInvoice> getListInvoice() {
        return listInvoice;
    }

    public void setListInvoice(List<EInvoice> listInvoice) {
        this.listInvoice = listInvoice;
    }

    @Override
    public String createWhere(List<Account> accountList, String invoiceNo, int operationType, int dateFilterType, Date beginDate, Date endDate) {
        String accountTaxNoList = "";
        String where = "";

        if (!accountList.isEmpty()) {
            for (Account acc : accountList) {
                accountTaxNoList = accountTaxNoList + ",'" + acc.getTaxNo() + "'";
                if (acc.getId() == 0) {
                    accountTaxNoList = "";
                    break;
                }

            }
        }

        if (!accountTaxNoList.isEmpty()) {
            accountTaxNoList = accountTaxNoList.substring(1, accountTaxNoList.length());
        }

        if (!accountTaxNoList.isEmpty()) {
            where = where + " AND lgei.gibtaxno IN ( " + accountTaxNoList + " ) ";
        }

        if (invoiceNo != null && !invoiceNo.isEmpty()) {
            where = where + " AND lgei.gibinvoice = '" + invoiceNo + "' ";
        }

        if (operationType == 3) {
            where = where + " AND lgei.is_archive = TRUE \n";
        } else {
            where = where + " AND lgei.is_archive = FALSE \n";
        }
        if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {

            if (dateFilterType == 1) {
                where = where + " AND lgei.gibdate BETWEEN '" + beginDate + "'" + "AND '" + endDate + "' \n";
            } else {
                where = where + " AND lgei.invoicedate BETWEEN '" + beginDate + "'" + "AND '" + endDate + "' \n";

            }

        }

        return where;
    }

    @Override
    public String jsonArrayInvoiceItems(List<IncomingInvoicesItem> list) {

        JsonArray jsonArray = new JsonArray();
        for (IncomingInvoicesItem obj : list) {
            //kdv hariç ise kdv'li birim fiyat hesapla
            if (!obj.isIsTaxIncluded()
                    && obj.getTaxRate() != null
                    && obj.getTaxRate().doubleValue() > 0
                    && obj.getUnitPrice() != null
                    && obj.getUnitPrice().doubleValue() > 0) {

                BigDecimal x = BigDecimal.ONE.add(obj.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                obj.setUnitPrice(obj.getUnitPrice().multiply(x));

            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("is_service", obj.isIsService());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getUnit().getId());
            jsonObject.addProperty("unitprice", obj.getUnitPrice());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("totalprice", obj.getTotalPrice() == null ? 0 : obj.getTotalPrice());
            jsonObject.addProperty("taxrate", obj.getTaxRate() == null ? 0 : obj.getTaxRate());
            jsonObject.addProperty("totaltax", obj.getTotalTax() == null ? 0 : obj.getTotalTax());
            jsonObject.addProperty("is_discountrate", obj.isIsDiscountRate());
            jsonObject.addProperty("discountrate", obj.getDiscountRate() == null ? 0 : obj.getDiscountRate());
            jsonObject.addProperty("discountprice", obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice());
            jsonObject.addProperty("is_discountrate2", false);
            jsonObject.addProperty("discountrate2", 0);
            jsonObject.addProperty("discountprice2", 0);
            jsonObject.addProperty("currency_id", obj.getCurrency().getId());
            jsonObject.addProperty("exchangerate", obj.getExchangeRate());
            jsonObject.addProperty("totalmoney", obj.getTotalMoney() == null ? 0 : obj.getTotalMoney());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("stockcount", obj.getStockCount());
            jsonObject.addProperty("waybillitem_id", obj.getWaybillItemIds());//irsaliyeden aktarıldı ise
            jsonObject.addProperty("managerUserDataid", (obj.getDiscountPrice() == null || obj.getDiscountPrice().doubleValue() == 0) ? null : sessionBean.getUser().getId());
            jsonObject.addProperty("isManagerDiscount", (obj.getDiscountPrice() != null && obj.getDiscountPrice().doubleValue() > 0));
            jsonObject.addProperty("recommendedprice", obj.getStock().getStockInfo().getRecommendedPrice() == null ? BigDecimal.ZERO : obj.getStock().getStockInfo().getRecommendedPrice());
            jsonObject.addProperty("warehouse_id", obj.getInvoice().isIsFuel() ? obj.getWarehouse().getId() : obj.getInvoice().getWarehouse().getId());
            if (obj.getUnitPrice() == BigDecimal.ZERO) {
                jsonObject.addProperty("is_free", true);
            }
            jsonObject.addProperty("is_free", false);
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public String jsonArrayWaybillItems(List<IncomingInvoicesItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (IncomingInvoicesItem obj : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getUnit().getId());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("stockcount", obj.getStockCount());
            jsonObject.addProperty("warehouse_id", obj.getInvoice().isIsFuel() ? obj.getWarehouse().getId() : obj.getInvoice().getWarehouse().getId());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    //Innova web servisinden tek responseda dönen faturaları ayırarak log tablosuna kaydeder
    @Override
    public void listOfIncomingInvoice() {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String data = " ";
        int requestNumber = 0;
        if (isError) {
            requestNumber = tempRequestNumber;
        } else {
            requestNumber = incomingEInvoicesDao.updateRequestNumber();
        }

        data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:pay1=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO\" xmlns:pay6=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.GetInvoices\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:GetInvoices>\n"
                + "            <tem:request>\n"
                + "                <pay1:Header>\n"
                + "                    <pay1:InstitutionId>" + brSetting.geteInvoiceAccountCode() + "</pay1:InstitutionId>\n"
                + "                    <pay1:OriginatorUserId>0</pay1:OriginatorUserId>\n"
                + "                    <pay1:Password>" + brSetting.geteInvoicePassword() + "</pay1:Password>\n"
                + "                    <pay1:Username>" + brSetting.geteInvoiceUserName() + "</pay1:Username>\n"
                + "                </pay1:Header>\n"
                + "                <pay6:AcceptanceDateTime>" + sdf.format(date) + "</pay6:AcceptanceDateTime>\n"
                + "                <pay6:InstitutionSourceCode>0</pay6:InstitutionSourceCode>\n"
                + "                <pay6:MaxItemsToReturn>50</pay6:MaxItemsToReturn>\n"
                + "                <pay6:RequestNumber>" + requestNumber + "</pay6:RequestNumber>\n"
                + "                <pay6:ViewContentType>4</pay6:ViewContentType>\n" // Fatura detayının hangi formatta görüntüleneceği 4:PDF
                + "            </tem:request>\n"
                + "        </tem:GetInvoices>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";
        try {
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {
//                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
//                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IClientInterfaceService/GetInvoices");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                int returnCode = httpClient.executeMethod(methodPost);
                System.out.println("----return code--" + returnCode);
                if (returnCode == 200) {
                    br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String readLine;

                    while (((readLine = br.readLine()) != null)) {
                        sb.append(readLine);
                    }
                    String result = sb.toString();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                    Document document = builder.parse(inputSource);
                    System.out.println("----data----" + data);
                    System.out.println("----result----" + result);
                    result = result.replace("'", " ");
                    result = result.replace("&", " ");
                    NodeList returnHeader = document.getElementsByTagName("GetInvoicesResult").item(0).getChildNodes().item(0).getChildNodes();
                    if (document.getElementsByTagName("ResponseCode").item(0).getTextContent().equalsIgnoreCase("0000") && document.getElementsByTagName("Status").item(0).getTextContent().equalsIgnoreCase("0")) {
                        NodeList returnList = document.getElementsByTagName("GetInvoicesResult").item(0).getChildNodes().item(1).getChildNodes();

                        List<IncomingEInvoice> listInvoice = new ArrayList<>();
                        List<IncomingEInvoice> listNew = new ArrayList<>();
                        listInvoice = incomingEInvoicesDao.getInvoicesData(result); // İnvoiceleri ayırdık
                        for (int i = 0; i < listInvoice.size(); i++) {
                            IncomingEInvoice iei = new IncomingEInvoice();
                            String strData = listInvoice.get(i).getGetData();//
                            iei.setGetData(strData);
                            iei.setIsSuccess(false);
                            iei.setResponseCode("0000");
                            iei.setResponseDescription(null);
                            iei.setProcessDate(date);
                            iei.setRequestId(requestNumber);
                            iei = updateIncomingInvoice(iei);
                            listNew.add(iei);

                        }

                        incomingEInvoicesDao.create(listNew);

                        isError = false;
                    } else if (document.getElementsByTagName("ResponseCode").item(0).getTextContent().equalsIgnoreCase("0001")) {
                        isError = true;
                        tempRequestNumber = requestNumber;

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("systemfailure")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    } else {
                        System.out.println("------responsecode-----" + document.getElementsByTagName("ResponseCode").item(0).getTextContent());
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("Message").item(0).getTextContent()));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }

                } else {
                    System.out.println("----returncode----" + returnCode);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                }
            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("------catch--listOfIncomingInvoice--11--" + e.getMessage());
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("-------catch---listOfIncomingInvoice-22-" + fe.getMessage());
                    }
                }

            }

        } catch (Exception ex) {
            System.out.println("-------catch--listOfIncomingInvoice--33-" + ex.getMessage());
        }

    }

    //Log tablosundaki xml datadan fatura ve item bilgilerini okur (Innova)
    @Override
    public List<EInvoice> listGetInvoices(int first, int pageSize, String sortField, String sortOrder, java.util.Map<String, Object> filters, Date beginDate, Date endDate) {
        BranchSetting brSetting = new BranchSetting();
        boolean isTaxNo = false;
        String pricingCurrencyCode = "";
        BigDecimal itemExchangeRate = new BigDecimal(BigInteger.ONE);

        listOfGetInvoices.clear();
        listInvoice.clear();
        listInvoiceItem.clear();
        listCurrency = sessionBean.getCurrencies();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        listOfGetInvoices = incomingEInvoicesDao.findall(first, pageSize, sortField, sortOrder, filters, "", beginDate, endDate, true);

        try {

            for (int i = 0; i < listOfGetInvoices.size(); i++) {

                listOfGetInvoices.get(i).getGetData();

                EInvoice inv = new EInvoice();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = null;

                builder = factory.newDocumentBuilder();

                InputSource inputSource = new InputSource(new StringReader(listOfGetInvoices.get(i).getGetData()));

                Document document = null;
                document = builder.parse(inputSource);

                document.getDocumentElement().normalize();

                Element root = null;
                root = document.getDocumentElement();

                NodeList returnHeader1 = root.getChildNodes();

                for (int a = 1; a < returnHeader1.getLength(); a = a + 2) {

                    Element node1 = (Element) returnHeader1.item(a);

                    if (node1.getNodeName().equalsIgnoreCase("a:Body")) {

                        NodeList itemList = node1.getChildNodes();
                        for (int k = 1; k < itemList.getLength(); k = k + 2) {

                            Element bodyChield = (Element) itemList.item(k);

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:AccountingSupplierParty")) {
                                if (bodyChield.getChildNodes().getLength() != 0) {
                                    NodeList accountingSupplierPartyChields = bodyChield.getChildNodes();
                                    for (int z = 1; z < accountingSupplierPartyChields.getLength(); z = z + 2) {
                                        Element accountingSupplierPartyChield = (Element) accountingSupplierPartyChields.item(z);

                                        if (accountingSupplierPartyChield.getNodeName().equalsIgnoreCase("b:Party")) {
                                            if (accountingSupplierPartyChield.getChildNodes().getLength() != 0) {
                                                NodeList partyChields = accountingSupplierPartyChield.getChildNodes();
                                                for (int y = 1; y < partyChields.getLength(); y = y + 2) {
                                                    Element partyChield = (Element) partyChields.item(y);

                                                    if (partyChield.getNodeName().equalsIgnoreCase("b:PartyName") && partyChield.getTextContent() != null) {
                                                        inv.getAccount().setTitle(partyChield.getTextContent());
                                                    }
                                                    if (partyChield.getNodeName().equalsIgnoreCase("b:PartyIdentifications")) {
                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList partyIdentificationsChields = partyChield.getChildNodes();
                                                            for (int j = 1; j < partyIdentificationsChields.getLength(); j = j + 2) {

                                                                Element partyIdentificationsChield = (Element) partyIdentificationsChields.item(j);
                                                                NodeList chieldsList = partyIdentificationsChield.getChildNodes();

                                                                for (int l = 1; l < chieldsList.getLength(); l = l + 2) {
                                                                    Element node6 = (Element) chieldsList.item(l);
                                                                    if (isTaxNo && node6.getNodeName().equalsIgnoreCase("b:Value") && node6.getTextContent() != null) {
                                                                        inv.getAccount().setTaxNo(node6.getTextContent());
                                                                        isTaxNo = false;
                                                                    }

                                                                    if (node6.getTextContent().equalsIgnoreCase("VKN")) {
                                                                        isTaxNo = true;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:AllowanceCharge")) {
                                if (bodyChield.getChildNodes().getLength() != 0) {

                                    NodeList allowanceChargesChields = bodyChield.getChildNodes();

                                    for (int z = 1; z < allowanceChargesChields.getLength(); z = z + 2) {

                                        Element allowanceChargesChield = (Element) allowanceChargesChields.item(z);
                                        if (allowanceChargesChield.getChildNodes().getLength() != 0) {
                                            NodeList allowanceChields = allowanceChargesChield.getChildNodes();

                                            for (int y = 1; y < allowanceChields.getLength(); y = y + 2) {

                                                Element allowanceChield = (Element) allowanceChields.item(y);

                                                if (allowanceChield.getNodeName().equalsIgnoreCase("b:Amount")) {

                                                    if (allowanceChield.getChildNodes().getLength() != 0) {
                                                        NodeList amountChields = allowanceChield.getChildNodes();
                                                        for (int x = 1; x < amountChields.getLength(); x = x + 2) {
                                                            Element amountChield = (Element) amountChields.item(x);

                                                            if (amountChield.getNodeName().equalsIgnoreCase("b:Value") && amountChield.getTextContent() != null) {

                                                                String amount = amountChield.getTextContent();
                                                                BigDecimal amountValue = new BigDecimal(amount);
                                                                inv.setDiscountPrice(amountValue != null ? amountValue : BigDecimal.ZERO);
                                                            }
                                                        }
                                                    }
                                                }

                                                if (allowanceChield.getNodeName().equalsIgnoreCase("b:MultiplierFactorNumeric") && allowanceChield.getTextContent() != null) {

                                                    String rate = allowanceChield.getTextContent();
                                                    BigDecimal rateValue = new BigDecimal(rate);
                                                    inv.setDiscountRate(rateValue != null ? rateValue : BigDecimal.ZERO);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:DocumentCurrencyCode")) {
                                if (bodyChield.getTextContent() != null) {
                                    for (Currency crr : listCurrency) {
                                        if (bodyChield.getTextContent().equalsIgnoreCase(crr.getInternationalCode())) {
                                            inv.setCurrency(crr);
                                        }
                                    }
                                }
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:ID") && bodyChield.getTextContent() != null) {
                                inv.setDocumentNumber(bodyChield.getTextContent());
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:InvoiceLine")) {
                                if (bodyChield.getChildNodes().getLength() != 0) {
                                    NodeList listInvoiceLine = bodyChield.getChildNodes();
                                    for (int j = 1; j < listInvoiceLine.getLength(); j = j + 2) {
                                        Element node17 = (Element) listInvoiceLine.item(j);
                                        IncomingInvoicesItem invI = new IncomingInvoicesItem();
                                        if (node17.getNodeName().equalsIgnoreCase("b:InvoiceLine")) {
                                            if (node17.getChildNodes().getLength() != 0) {
                                                NodeList invoiceLineChields = node17.getChildNodes();
                                                for (int m = 1; m < invoiceLineChields.getLength(); m = m + 2) {//INVOİCELİNE CHİELDS 
                                                    Element invoiceLineChield = (Element) invoiceLineChields.item(m);

                                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("b:AllowanceCharge")) {
                                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {

                                                            NodeList allowanceChargeChields = invoiceLineChield.getChildNodes();

                                                            for (int s = 1; s < allowanceChargeChields.getLength(); s = s + 2) { //AllowanceCharge

                                                                Element allowanceChargeChield = (Element) allowanceChargeChields.item(s);
                                                                if (allowanceChargeChield.getNodeName().equalsIgnoreCase("b:Amount")) {
                                                                    if (allowanceChargeChield.getChildNodes().getLength() != 0) {
                                                                        NodeList amountChields = allowanceChargeChield.getChildNodes();

                                                                        for (int t = 1; t < amountChields.getLength(); t = t + 2) {
                                                                            Element amountChield = (Element) amountChields.item(t);
                                                                            if (amountChield.getNodeName().equalsIgnoreCase("b:Value") && amountChield.getTextContent() != null) {

                                                                                String valueAmount = amountChield.getTextContent();
                                                                                BigDecimal bigDecimal = new BigDecimal(valueAmount);

                                                                                invI.setDiscountPrice(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                                            }
                                                                        }
                                                                    }
                                                                }

                                                                if (allowanceChargeChield.getNodeName().equalsIgnoreCase("b:MultiplierFactorNumeric") && allowanceChargeChield.getTextContent() != null) {
                                                                    invI.setIsDiscountRate(true);
                                                                    String valueDiscountRate = allowanceChargeChield.getTextContent();
                                                                    BigDecimal bigDecimal = new BigDecimal(valueDiscountRate);
                                                                    invI.setDiscountRate(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("b:InvoicedQuantityUnitCode") && invoiceLineChield.getTextContent() != null) {

                                                        invI.getUnit().setSortName(invoiceLineChield.getTextContent());
                                                        invI.setOldUnitName(invoiceLineChield.getTextContent());

                                                    }
                                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("b:InvoicedQuantityValue") && invoiceLineChield.getTextContent() != null) {

                                                        String valuequantity = invoiceLineChield.getTextContent();
                                                        BigDecimal bigDecimal = new BigDecimal(valuequantity);
                                                        invI.setQuantity(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);

                                                    }
                                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("b:Item")) {
                                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                            NodeList itemChields = invoiceLineChield.getChildNodes();
                                                            for (int y = 1; y < itemChields.getLength(); y = y + 2) {
                                                                Element itemChield = (Element) itemChields.item(y);
                                                                if (itemChield.getNodeName().equalsIgnoreCase("b:Name") && itemChield.getTextContent() != null) {
                                                                    invI.getStock().setName(itemChield.getTextContent());
                                                                    invI.setOldStockName(itemChield.getTextContent());
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("b:LineExtensionAmount")) {
                                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                            NodeList lineExtensionAmountChields = invoiceLineChield.getChildNodes();
                                                            for (int y = 1; y < lineExtensionAmountChields.getLength(); y = y + 2) {
                                                                Element lineExtensionAmountChield = (Element) lineExtensionAmountChields.item(y);

                                                                if (lineExtensionAmountChield.getNodeName().equalsIgnoreCase("b:Value") && lineExtensionAmountChield.getTextContent() != null) {
                                                                    String valueLİneEx = lineExtensionAmountChield.getTextContent();
                                                                    BigDecimal bigDecimal = new BigDecimal(valueLİneEx);
                                                                    invI.setTotalMoney(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("b:Price")) {
                                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                            NodeList priceChields = invoiceLineChield.getChildNodes();
                                                            for (int y = 1; y < priceChields.getLength(); y = y + 2) {
                                                                Element priceChield = (Element) priceChields.item(y);
                                                                if (priceChield.getNodeName().equalsIgnoreCase("b:Value") && priceChield.getTextContent() != null) {

                                                                    String valueUnitPrice = priceChield.getTextContent();
                                                                    BigDecimal bigDecimal = new BigDecimal(valueUnitPrice);
                                                                    invI.setUnitPrice(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                                }
                                                                if (priceChield.getNodeName().equalsIgnoreCase("b:CurrencyCode") && priceChield.getTextContent() != null) {
                                                                    for (Currency crr : listCurrency) {
                                                                        if (priceChield.getTextContent().equalsIgnoreCase(crr.getInternationalCode())) {
                                                                            invI.setCurrency(crr);
                                                                        }
                                                                    }
                                                                    if (invI.getCurrency().getId() == 0) {
                                                                        for (Currency crr : listCurrency) {
                                                                            if (crr.getId() == 1) {

                                                                                invI.setCurrency(crr);
                                                                            }
                                                                        }
                                                                    }

                                                                }
                                                            }

                                                        }
                                                    }
                                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("b:TaxTotal")) {
                                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                            NodeList taxTotalChields = invoiceLineChield.getChildNodes();
                                                            for (int z = 1; z < taxTotalChields.getLength(); z = z + 2) {

                                                                Element taxTotalChield = (Element) taxTotalChields.item(z);

                                                                if (taxTotalChield.getNodeName().equalsIgnoreCase("b:TaxSubtotal")) {
                                                                    if (taxTotalChield.getChildNodes().getLength() != 0) {
                                                                        NodeList taxSubtotalChields = taxTotalChield.getChildNodes();
                                                                        for (int d = 1; d < taxSubtotalChields.getLength(); d = d + 2) {

                                                                            Element taxSubtotalChield = (Element) taxSubtotalChields.item(d);

                                                                            if (taxSubtotalChield.getNodeName().equalsIgnoreCase("b:TaxSubtotal")) {
                                                                                if (taxSubtotalChield.getChildNodes().getLength() != 0) {
                                                                                    NodeList subtotalChields = taxSubtotalChield.getChildNodes();

                                                                                    for (int y = 1; y < subtotalChields.getLength(); y = y + 2) {
                                                                                        Element subtotalChield = (Element) subtotalChields.item(y);
                                                                                        if (subtotalChield.getNodeName().equalsIgnoreCase("b:Percent") && subtotalChield.getTextContent() != null) {
                                                                                            String valueTaxRate = subtotalChield.getTextContent();
                                                                                            BigDecimal bigDecimal = new BigDecimal(valueTaxRate);
                                                                                            invI.setTaxRate(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                                                        }

                                                                                        if (subtotalChield.getNodeName().equalsIgnoreCase("b:TaxAmount")) {
                                                                                            if (subtotalChield.getChildNodes().getLength() != 0) {
                                                                                                NodeList taxAmountChields = subtotalChield.getChildNodes();
                                                                                                for (int l = 1; l < taxAmountChields.getLength(); l = l + 2) {
                                                                                                    Element taxAmountChield = (Element) taxAmountChields.item(l);

                                                                                                    if (taxAmountChield.getNodeName().equalsIgnoreCase("b:Value") && taxAmountChield.getTextContent() != null) {
                                                                                                        String valueTotalTax = taxAmountChield.getTextContent();
                                                                                                        BigDecimal bigDecimal = new BigDecimal(valueTotalTax);
                                                                                                        invI.setTotalTax(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                if (invI.getTotalMoney() == null) {

                                                    invI.setTotalMoney(BigDecimal.ZERO);

                                                }
                                                if (invI.getDiscountPrice() == null) {

                                                    invI.setDiscountPrice(BigDecimal.ZERO);

                                                }

                                                if (invI.getTotalPrice() == null) {

                                                    invI.setTotalPrice(BigDecimal.ZERO);

                                                }

                                                invI.setTotalPrice(invI.getTotalMoney().subtract(invI.getDiscountPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? invI.getDiscountPrice() : BigDecimal.ZERO));

                                                if (invI.getTotalTax() != null) {

                                                    invI.setTotalMoney((invI.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? invI.getTotalPrice() : BigDecimal.ZERO).add(invI.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? invI.getTotalTax() : BigDecimal.ZERO));
                                                }
                                                invI.setExchangeRate(BigDecimal.ONE);
                                                invI.getInvoice().setId(listOfGetInvoices.get(i).getId());
                                                listInvoiceItem.add(invI);
                                                for (int y = 0; y < listInvoiceItem.size(); y++) {
                                                    listInvoiceItem.get(y).setId(y + 1);
                                                    listInvoiceItem.get(y).getStock().setId(0);
                                                    listInvoiceItem.get(y).getUnit().setId(0);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:InvoiceTypeCode") && bodyChield.getTextContent() != null) {

                                switch (Integer.parseInt(bodyChield.getTextContent())) {
                                    case 1://Satış
                                        inv.getType().setId(59);
                                        inv.getType().setTag("İrsaliye Fatura");
                                        break;

                                    case 2:// İade
                                        inv.getType().setId(27);
                                        inv.getType().setTag("İade");
                                        break;

                                    case 3://Tevkifat
                                        inv.getType().setTag("Tevkifat Faturası");
                                        break;

                                    case 4://İstisna
                                        inv.getType().setTag("İstisna Faturası");
                                        break;

                                    case 5://İhraç Kayıtlı Fatura
                                        inv.getType().setTag("İhraç Kayıtlı Fatura");
                                        break;

                                    case 6://Özel Matrah 
                                        inv.getType().setTag("Özel Matrah Faturası");
                                        break;

                                    case 7://SGK
                                        inv.getType().setTag("SGK Faturası");
                                        break;
                                    default:

                                        break;
                                }
                            }
                            if (bodyChield.getNodeName().equalsIgnoreCase("b:IssueDateTime") && bodyChield.getTextContent() != null) {

                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                Date formattedDate4 = inputFormat.parse(bodyChield.getTextContent());
                                String newDate;
                                newDate = sdf1.format(formattedDate4);
                                Date dateNew1 = sdf1.parse(newDate);
                                String formattedDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(dateNew1);
                                Timestamp timestamp = new Timestamp(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(formattedDate).getTime());

                                inv.setInvoiceDate(dateNew1);
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:LegalMonetaryTotal")) {
                                if (bodyChield.getChildNodes().getLength() != 0) {
                                    NodeList legalMonetaryTotalChields = bodyChield.getChildNodes();
                                    for (int b = 1; b < legalMonetaryTotalChields.getLength(); b = b + 2) {
                                        Element legalMonetaryTotalChield = (Element) legalMonetaryTotalChields.item(b);

                                        if (legalMonetaryTotalChield.getNodeName().equalsIgnoreCase("b:TaxInclusiveAmount")) {
                                            if (legalMonetaryTotalChield.getChildNodes().getLength() != 0) {
                                                NodeList taxInclusiveAmountChields = legalMonetaryTotalChield.getChildNodes();
                                                for (int y = 1; y < taxInclusiveAmountChields.getLength(); y = y + 2) {
                                                    Element taxInclusiveAmountChield = (Element) taxInclusiveAmountChields.item(y);

                                                    if (taxInclusiveAmountChield.getNodeName().equalsIgnoreCase("b:Value") && taxInclusiveAmountChield.getTextContent() != null) {
                                                        String valueTotal = taxInclusiveAmountChield.getTextContent();
                                                        BigDecimal bigDecimalTotal = new BigDecimal(valueTotal);
                                                        inv.setTotalMoney(bigDecimalTotal != null ? bigDecimalTotal : BigDecimal.ZERO);
                                                    }
                                                }
                                            }
                                        }

                                        if (legalMonetaryTotalChield.getNodeName().equalsIgnoreCase("b:PayableRoundingAmount")) {

                                            if (legalMonetaryTotalChield.getChildNodes().getLength() != 0) {

                                                NodeList payableRoundingAmountChields = legalMonetaryTotalChield.getChildNodes();
                                                for (int y = 1; y < payableRoundingAmountChields.getLength(); y = y + 2) {
                                                    Element payableRoundingAmountChield = (Element) payableRoundingAmountChields.item(y);

                                                    if (payableRoundingAmountChield.getNodeName().equalsIgnoreCase("b:Value") && payableRoundingAmountChield.getTextContent() != null) {
                                                        String valueRoundingAmount = payableRoundingAmountChield.getTextContent();
                                                        BigDecimal bigDecimal = new BigDecimal(valueRoundingAmount);
                                                        inv.setRoundingPrice(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                    }
                                                }
                                            }
                                        }
                                        if (legalMonetaryTotalChield.getNodeName().equalsIgnoreCase("b:LineExtensionAmount")) {

                                            if (legalMonetaryTotalChield.getChildNodes().getLength() != 0) {

                                                NodeList lineExtensionAmountChields = legalMonetaryTotalChield.getChildNodes();
                                                for (int y = 1; y < lineExtensionAmountChields.getLength(); y = y + 2) {
                                                    Element lineExtensionAmountChield = (Element) lineExtensionAmountChields.item(y);

                                                    if (lineExtensionAmountChield.getNodeName().equalsIgnoreCase("b:Value") && lineExtensionAmountChield.getTextContent() != null) {
                                                        String valueLine = lineExtensionAmountChield.getTextContent();
                                                        BigDecimal bigDecimal = new BigDecimal(valueLine);
                                                        inv.setTotalPrice(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                    }
                                                }
                                            }
                                        }

//                                        if (legalMonetaryTotalChield.getNodeName() == "b:AllowanceTotalAmount") {
//
//                                            NodeList totalDiscountList = legalMonetaryTotalChield.getChildNodes();
//
//                                            for (int j = 1; j < totalDiscountList.getLength(); j = j + 2) {
//                                                Element nodeDiscount = (Element) totalDiscountList.item(j);
//
//                                                if (nodeDiscount.getNodeName() == "b:Value") {
//
//                                                    String valueDiscountAmount = nodeDiscount.getTextContent();
//                                                    BigDecimal bigDecimal = new BigDecimal(valueDiscountAmount);
//
//                                                    inv.setTotalDiscount(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
//
//                                                }
//
//                                            }
//
////                                            if (node7.getElementsByTagName("b:Value").item(0).getTextContent() != null) {
////                                                System.out.println("------CONTROL------");
////
////                                                String valueDiscountAmount = node7.getElementsByTagName("b:Value").item(0).getTextContent();
////                                                BigDecimal bigDecimal = new BigDecimal(valueDiscountAmount);
////
////                                                inv.setTotalDiscount(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
////                                            }
//                                        }
                                    }
                                }
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:Notes")) {
                                if (bodyChield.getChildNodes().getLength() != 0) {
                                    NodeList notesChields = bodyChield.getChildNodes();
                                    for (int x = 1; x < notesChields.getLength(); x = x + 2) {
                                        Element notesChield = (Element) notesChields.item(x);
                                        if (notesChield.getNodeName().equalsIgnoreCase("c:string") && notesChield.getTextContent() != null) {
                                            inv.setDescription(notesChield.getTextContent());
                                        }
                                    }
                                }
                            }
                            if (bodyChield.getNodeName().equalsIgnoreCase("b:ProfileID") && bodyChield.getTextContent() != null) {
                                inv.setInvoiceScenarioId(Integer.valueOf(bodyChield.getTextContent()));
                                if (Integer.valueOf(bodyChield.getTextContent()) == 2) {
                                    if (listOfGetInvoices.get(i).getApprovalStatusId() == 0) {
                                        listOfGetInvoices.get(i).setApprovalStatusId(1);
                                        incomingEInvoicesDao.update(listOfGetInvoices.get(i));
                                    }
                                } else {
                                    if (listOfGetInvoices.get(i).getApprovalStatusId() != 0) {
                                        listOfGetInvoices.get(i).setApprovalStatusId(0);
                                        incomingEInvoicesDao.update(listOfGetInvoices.get(i));
                                    }
                                }
                            }

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:TaxTotal")) {
                                if (bodyChield.getChildNodes().getLength() != 0) {

                                    NodeList invoiceTaxTotalChields = bodyChield.getChildNodes();
                                    for (int b = 1; b < invoiceTaxTotalChields.getLength(); b = b + 2) {
                                        Element invoiceTaxTotalChield = (Element) invoiceTaxTotalChields.item(b);
                                        if (invoiceTaxTotalChield.getNodeName().equalsIgnoreCase("b:TaxTotal")) {
                                            if (invoiceTaxTotalChield.getChildNodes().getLength() != 0) {
                                                NodeList taxTotalChields = invoiceTaxTotalChield.getChildNodes();
                                                for (int y = 1; y < taxTotalChields.getLength(); y = y + 2) {
                                                    Element taxTotalChield = (Element) taxTotalChields.item(y);
                                                    if (taxTotalChield.getNodeName().equalsIgnoreCase("b:TaxAmount")) {
                                                        if (taxTotalChield.getChildNodes().getLength() != 0) {
                                                            NodeList taxAmountChields = taxTotalChield.getChildNodes();
                                                            for (int z = 1; z < taxAmountChields.getLength(); z = z + 2) {
                                                                Element taxAmountChield = (Element) taxAmountChields.item(z);
                                                                if (taxAmountChield.getNodeName().equalsIgnoreCase("b:Value") && taxAmountChield.getTextContent() != null) {

                                                                    String valueTaxSubTotal = taxAmountChield.getTextContent();
                                                                    BigDecimal taxSub = new BigDecimal(valueTaxSubTotal);
                                                                    inv.setTotalTax(taxSub != null ? taxSub : BigDecimal.ZERO);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (bodyChield.getNodeName() == "b:PricingCurrencyCode") {
                                if (bodyChield.getTextContent() != null) {
                                    pricingCurrencyCode = bodyChield.getTextContent();
                                }
                            }

                            if (bodyChield.getNodeName() == "b:PricingExchangeRate") {

                                if (bodyChield.getNodeValue() != null) {
                                    NodeList pricingChields = bodyChield.getChildNodes();

                                    for (int j = 1; j < pricingChields.getLength(); j = j + 2) {
                                        Element nodePricing = (Element) pricingChields.item(j);
                                        if (nodePricing.getNodeName() == "b:CalculationRate") {
                                            if (nodePricing.getTextContent() != null) {
                                                String exchangeRate = nodePricing.getTextContent();
                                                itemExchangeRate = new BigDecimal(exchangeRate);
                                                itemExchangeRate = itemExchangeRate != null ? itemExchangeRate : BigDecimal.ZERO;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

//                    if (node1.getNodeName() == "a:ViewContent") {
//
//                        String encodedBytes = node1.getTextContent();
//                        try {
//                            BASE64Decoder decoder = new BASE64Decoder();
//                            byte[] decodedBytes;
//                            FileOutputStream fop;
//                            decodedBytes = new BASE64Decoder().decodeBuffer(encodedBytes);
//                            File file = new File("C:\\Users\\elif.mart/NewFile.pdf");
//                            fop = new FileOutputStream(file);
//
//                            fop.write(decodedBytes);
//
//                            fop.flush();
//                            fop.close();
//                            System.out.println("Created");
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
                    if (node1.getNodeName() == "a:Header") {

                        NodeList headerItemList = node1.getChildNodes();
                        for (int j = 1; j < headerItemList.getLength(); j = j + 2) {
                            Element nodeHeader = (Element) headerItemList.item(j);

                            if (nodeHeader.getNodeName().equalsIgnoreCase("b:ReferenceNumber") && nodeHeader.getTextContent() != null) {
                                inv.setReferenceNumber(nodeHeader.getTextContent());
                            }

                            if (nodeHeader.getNodeName().equalsIgnoreCase("b:ReceiveDateTime") && nodeHeader.getTextContent() != null) {

                                SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                Date formattedDate4 = inputFormat.parse(nodeHeader.getTextContent());
                                String newDate;
                                newDate = sdf1.format(formattedDate4);
                                Date dateNew1 = sdf1.parse(newDate);
                                inv.setReceivedDate(dateNew1);

                            }
                        }
                    }
                }

                if (pricingCurrencyCode.equalsIgnoreCase(inv.getCurrency().getInternationalCode()) || pricingCurrencyCode.isEmpty()) {
                    for (int j = 0; j < listInvoiceItem.size(); j++) {

                        if (listOfGetInvoices.get(i).getId() == listInvoiceItem.get(j).getInvoice().getId()) {
                            listInvoiceItem.get(j).setExchangeRate(BigDecimal.ONE);
                            listInvoiceItem.get(j).setCurrency(inv.getCurrency());
                        }
                    }
                    inv.setExchangeRate(BigDecimal.ONE);

                } else {
                    for (int j = 0; j < listInvoiceItem.size(); j++) {
                        listInvoiceItem.get(j).setExchangeRate(itemExchangeRate);
                        for (Currency crr : listCurrency) {
                            if (crr.getInternationalCode().equalsIgnoreCase(pricingCurrencyCode)) {
                                listInvoiceItem.get(j).setCurrency(crr);
                            } else {
                                listInvoiceItem.get(j).getCurrency().setInternationalCode(pricingCurrencyCode);
                            }
                        }
                    }
                    Currency currency = inv.getCurrency();
                    Currency resCurrency = brSetting.getBranch().getCurrency();
                    Exchange exchange = new Exchange();
                    exchange = incomingEInvoicesDao.bringExchangeRate(currency, resCurrency);
                    inv.setExchangeRate(exchange.getBuying() != null ? exchange.getBuying() : BigDecimal.ONE);
                }

                inv.setId(listOfGetInvoices.get(i).getId());
                inv.getAccount().setId(0);
                inv.setApprovalStatusId(listOfGetInvoices.get(i).getApprovalStatusId());
                listInvoice.add(inv);
            }
        } catch (Exception e) {
            System.out.println("--------catch----listGetInvoices--" + e.getMessage());
        }

        return listInvoice;
    }

    @Override
    public List<IncomingInvoicesItem> listOfİtem() {
        return listInvoiceItem;
    }

    @Override
    public int update(IncomingEInvoice obj) {
        return incomingEInvoicesDao.update(obj);
    }

    //Ticari fatura için onay/ret yanıtı gönderir(Innova)
    @Override
    public int sendApproval(EInvoice obj) {
        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        int replyState = 0;
        int approvalResult = 0;
        if (obj.getApprovalStatusId() == 2) {

            replyState = 1;

        } else if (obj.getApprovalStatusId() == 3) {

            replyState = 2;

        }

        String data = " ";

        data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:pay1=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO\" xmlns:pay8=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.ReplyInvoices\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:ReplyInvoices>\n"
                + "            <tem:request>\n"
                + "                <pay1:Header>\n"
                + "                    <pay1:InstitutionId>" + brSetting.geteInvoiceAccountCode() + "</pay1:InstitutionId>\n"
                + "                    <pay1:OriginatorUserId>0</pay1:OriginatorUserId>\n"
                + "                    <pay1:Password>" + brSetting.geteInvoicePassword() + "</pay1:Password>\n"
                + "                    <pay1:Username>" + brSetting.geteInvoiceUserName() + "</pay1:Username>\n"
                + "                </pay1:Header>\n"
                + "                <pay8:AcceptanceDateTime>" + sdf.format(date) + "</pay8:AcceptanceDateTime>\n"
                + "                <pay8:Items>\n"
                + "                    <pay8:ReplyInvoiceItem>\n"
                + "                        <pay8:ReferenceNumber>" + obj.getReferenceNumber() + "</pay8:ReferenceNumber>\n"
                + "                        <pay8:ReplyDescription>" + obj.getApprovalDescription() + "</pay8:ReplyDescription>\n"
                + "                        <pay8:ReplyState>" + replyState + "</pay8:ReplyState>\n"
                + "                    </pay8:ReplyInvoiceItem>\n"
                + "                </pay8:Items>\n"
                + "            </tem:request>\n"
                + "        </tem:ReplyInvoices>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";

        try {

            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IClientInterfaceService/ReplyInvoices");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                int returnCode = httpClient.executeMethod(methodPost);

                if (returnCode == 200) {

                    br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String readLine;

                    while (((readLine = br.readLine()) != null)) {
                        sb.append(readLine);
                    }
                    String result = sb.toString();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(sb.toString()));

                    Document document = builder.parse(inputSource);
                    NodeList returnHeader = document.getElementsByTagName("ReplyInvoicesResult").item(0).getChildNodes();
                    if (document.getElementsByTagName("ResponseCode").item(0).getTextContent().equalsIgnoreCase("0000") && document.getElementsByTagName("Status").item(0).getTextContent().equalsIgnoreCase("0")) {
                        NodeList returnList = document.getElementsByTagName("ReplyInvoicesResult").item(0).getChildNodes().item(1).getChildNodes();

                        for (int i = 0; i < returnList.getLength(); i++) {
                            Element node = (Element) returnList.item(i);

                            if (node.getElementsByTagName("a:ResponseCode").item(0).getTextContent().equalsIgnoreCase("0000") && node.getElementsByTagName("a:ResponseStatus").item(0).getTextContent().equalsIgnoreCase("0")) {

                                approvalResult = 1;
                            } else {

                                if (node.getElementsByTagName("a:ResponseDescription").item(0).getTextContent().contains("Kabul Edildi")) {
                                    approvalResult = 2;//Fatura daha önce onaylanmış

                                } else if (node.getElementsByTagName("a:ResponseDescription").item(0).getTextContent().contains("Reddedildi")) {
                                    approvalResult = 3; //Fatura daha önce reddedilmiş
                                    approvalMessage = node.getElementsByTagName("a:ResponseDescription").item(0).getTextContent();
                                } else {
                                    approvalResult = -1;
                                    approvalMessage = node.getElementsByTagName("a:ResponseDescription").item(0).getTextContent();
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("-----returncode---" + returnCode);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("------catch--sendApproval-11-" + e.getMessage());
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("-------catch---sendApproval--22-" + fe.getMessage());
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("-------catch---sendApproval--33--" + ex.getMessage());
        }
        return approvalResult;

    }

    @Override
    public List<IncomingInvoicesItem> bringItemList(Invoice obj) {
        List<IncomingInvoicesItem> eInvoiceItemList = new ArrayList<>();
        IncomingEInvoice iei = new IncomingEInvoice();
        iei = incomingEInvoicesDao.bringEInvoiceItem(obj);
        listCurrency = sessionBean.getCurrencies();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(new StringReader(iei.getGetData()));

            Document document = null;
            document = builder.parse(inputSource);

            document.getDocumentElement().normalize();
            Element root = null;
            root = document.getDocumentElement();
            NodeList returnHeader1 = root.getChildNodes();

            for (int a = 1; a < returnHeader1.getLength(); a = a + 2) {
                Element node1 = (Element) returnHeader1.item(a);
                if (node1.getNodeName().equalsIgnoreCase("a:Body")) {
                    NodeList itemList = node1.getChildNodes();
                    for (int k = 1; k < itemList.getLength(); k = k + 2) {

                        Element node2 = (Element) itemList.item(k);

                        if (node2.getNodeName().equalsIgnoreCase("b:InvoiceLine")) {
                            NodeList itemList6 = node2.getChildNodes();
                            for (int j = 1; j < itemList6.getLength(); j = j + 2) {
                                Element node17 = (Element) itemList6.item(j);
                                IncomingInvoicesItem iItem = new IncomingInvoicesItem();

                                if (node17.getNodeName().equalsIgnoreCase("b:InvoiceLine")) {
                                    NodeList itemList15 = node17.getChildNodes();
                                    for (int m = 1; m < itemList15.getLength(); m = m + 2) {//INVOİCELİNE CHİELDS 
                                        Element node8 = (Element) itemList15.item(m);
                                        if (node8.getNodeName().equalsIgnoreCase("b:InvoicedQuantityValue") && node8.getTextContent() != null) {

                                            String valuequantity = node8.getTextContent();
                                            BigDecimal bigDecimal = new BigDecimal(valuequantity);
                                            iItem.setQuantity(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);

                                        }

                                        if (node8.getNodeName().equalsIgnoreCase("b:InvoicedQuantityUnitCode") && node8.getTextContent() != null) {

                                            iItem.getUnit().setName(node8.getTextContent());
                                            iItem.setOldUnitName(node8.getTextContent());

                                        }

                                        if (node8.getNodeName().equalsIgnoreCase("b:LineExtensionAmount") && node8.getTextContent() != null) {

                                            if (node8.getElementsByTagName("b:Value").item(0).getTextContent() != null) {
                                                String valueLİneEx = node8.getElementsByTagName("b:Value").item(0).getTextContent();
                                                BigDecimal bigDecimal = new BigDecimal(valueLİneEx);
                                                iItem.setTotalMoney(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                            }

                                            if (node8.getElementsByTagName("b:CurrencyCode").item(0).getTextContent() != null) {

                                                for (Currency crr : listCurrency) {
                                                    if (node8.getElementsByTagName("b:CurrencyCode").item(0).getTextContent().equalsIgnoreCase(crr.getInternationalCode())) {
                                                        iItem.setCurrency(crr);
                                                    }
                                                }
                                            }
                                        }

                                        if (node8.getNodeName().equalsIgnoreCase("b:AllowanceCharges")) {

                                            NodeList itemList7 = node8.getChildNodes();

                                            for (int s = 1; s < itemList7.getLength(); s = s + 2) { //AllowanceCharge

                                                Element node9 = (Element) itemList7.item(s);
                                                NodeList itemList8 = node9.getChildNodes();
                                                for (int h = 1; h < itemList8.getLength(); h = h + 2) {//AllowanceCharge Chields

                                                    Element node10 = (Element) itemList8.item(h);
                                                    if (node10.getNodeName().equalsIgnoreCase("b:MultiplierFactorNumeric")) {

                                                        if (node10.getTextContent() != null) {

                                                            iItem.setIsDiscountRate(true);
                                                            String valueDiscountRate = node10.getTextContent();
                                                            BigDecimal bigDecimal = new BigDecimal(valueDiscountRate);
                                                            iItem.setDiscountRate(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);

                                                        }
                                                    }
                                                    if (node10.getNodeName().equalsIgnoreCase("b:Amount")) {

                                                        if (node10.getElementsByTagName("b:Value").item(0).getTextContent() != null) {

                                                            String valueAmount = node10.getElementsByTagName("b:Value").item(0).getTextContent();
                                                            BigDecimal bigDecimal = new BigDecimal(valueAmount);

                                                            iItem.setDiscountPrice(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (node8.getNodeName().equalsIgnoreCase("b:Item")) {

                                            NodeList itemList10 = node8.getChildNodes();
                                            for (int y = 1; y < itemList10.getLength(); y = y + 2) {
                                                Element node12 = (Element) itemList10.item(y);
                                                if (node12.getNodeName().equalsIgnoreCase("b:Name")) {
                                                    iItem.getStock().setName(node12.getTextContent());
                                                    iItem.setOldStockName(node12.getTextContent());
                                                }
                                            }
                                        }

                                        if (node8.getNodeName().equalsIgnoreCase("b:Price")) {

                                            NodeList itemList11 = node8.getChildNodes();
                                            for (int y = 1; y < itemList11.getLength(); y = y + 2) {
                                                Element node13 = (Element) itemList11.item(y);
                                                if (node13.getNodeName().equalsIgnoreCase("b:Value")) {
                                                    String valueUnitPrice = node13.getTextContent();
                                                    BigDecimal bigDecimal = new BigDecimal(valueUnitPrice);
                                                    iItem.setUnitPrice(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);
                                                }
                                            }
                                        }

                                        if (node8.getNodeName().equalsIgnoreCase("b:TaxTotal")) {

                                            NodeList itemListTaxTotal = node8.getChildNodes();

                                            for (int z = 1; z < itemListTaxTotal.getLength(); z = z + 2) {

                                                Element node14 = (Element) itemListTaxTotal.item(z);

                                                if (node14.getNodeName().equalsIgnoreCase("b:TaxSubtotal")) {

                                                    NodeList itemList13 = node14.getChildNodes();
                                                    for (int d = 1; d < itemList13.getLength(); d = d + 2) {

                                                        Element node15 = (Element) itemList13.item(d);
                                                        NodeList taxSubTotalChield = node15.getChildNodes();

                                                        for (int y = 1; y < taxSubTotalChield.getLength(); y = y + 2) {

                                                            Element nodeSubTotalChield = (Element) taxSubTotalChield.item(y);
                                                            if (nodeSubTotalChield.getNodeName().equalsIgnoreCase("b:Percent")) {

                                                                if (nodeSubTotalChield.getTextContent() != null) {

                                                                    String valueTaxRate = nodeSubTotalChield.getTextContent();
                                                                    BigDecimal bigDecimal = new BigDecimal(valueTaxRate);
                                                                    iItem.setTaxRate(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);

                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                if (node14.getNodeName().equalsIgnoreCase("b:TaxAmount")) {

                                                    if (node14.getNodeValue() != null) {

                                                        NodeList itemListTax = node14.getChildNodes();

                                                        for (int l = 1; l < itemListTax.getLength(); l = l + 2) {
                                                            Element nodeTax = (Element) itemListTax.item(l);

                                                            if (nodeTax.getNodeName().equalsIgnoreCase("b:Value")) {
                                                                if (nodeTax.getNodeValue() != null) {

                                                                    String valueTotalTax = nodeTax.getTextContent();
                                                                    BigDecimal bigDecimal = new BigDecimal(valueTotalTax);
                                                                    iItem.setTotalTax(bigDecimal != null ? bigDecimal : BigDecimal.ZERO);

                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (iItem.getTotalMoney() == null) {

                                    iItem.setTotalMoney(BigDecimal.ZERO);

                                }
                                if (iItem.getDiscountPrice() == null) {

                                    iItem.setDiscountPrice(BigDecimal.ZERO);

                                }

                                if (iItem.getTotalPrice() == null) {

                                    iItem.setTotalPrice(BigDecimal.ZERO);

                                }

                                iItem.setTotalPrice(iItem.getTotalMoney().subtract(iItem.getDiscountPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getDiscountPrice() : BigDecimal.ZERO));

                                if (iItem.getTotalTax() != null) {

                                    iItem.setTotalMoney((iItem.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalPrice() : BigDecimal.ZERO).add(iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalTax() : BigDecimal.ZERO));
                                }

                                iItem.setExchangeRate(BigDecimal.ONE);
                                eInvoiceItemList.add(iItem);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("-----catch----bringItemList-" + e.getMessage());
        }
        return eInvoiceItemList;
    }

    @Override
    public int createInvoice(EInvoice obj, String invoiceItems, String waybillItems, Integer ieInvoiceId, Integer ieInvoiceApprovalStatusId, String ieInvoiceApprovalDescription) {

        return incomingEInvoicesDao.createInvoice(obj, invoiceItems, waybillItems, ieInvoiceId, ieInvoiceApprovalStatusId, ieInvoiceApprovalDescription);

    }

    @Override
    public List<IncomingInvoicesItem> calculater(List<IncomingInvoicesItem> list, EInvoice obj) {

        List<IncomingInvoicesItem> listItems;
        listItems = new ArrayList<>();
        if (!list.isEmpty()) {
            listItems.addAll(list);

        }

        for (int i = 0; i < list.size(); i++) {

            //Fatura bazında tutar iskontosu girildi ise önce oran bul sonra uygula
            if (!obj.isIsDiscountRate() && obj.getDiscountPrice() != null && obj.getDiscountPrice().doubleValue() > 0) {
                //fatura bazında tutar iskontosu varsa yeniden oran hesaplayıp iskontosunu bul
                BigDecimal tempTotalPrice = obj.getTotalPrice();

                //hiç ürün yoksa iskontoyu ekleme
                if (tempTotalPrice.compareTo(BigDecimal.ZERO) != 0) {
                    tempTotalPrice = tempTotalPrice.add(obj.getDiscountPrice());
                }

//                //yeni ürün ekleniyorsa onun tutarınıda ekle.
//                if (invoiceItem.getId() == 0) {
//                    tempTotalPrice = tempTotalPrice.add(invoiceItem.getTotalPrice());
//                }
                BigDecimal rate = new BigDecimal(100).multiply(obj.getDiscountPrice()).divide(tempTotalPrice, 4, RoundingMode.HALF_EVEN);
                BigDecimal disc = new BigDecimal(BigInteger.ONE).subtract(rate.divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                list.get(i).setTotalPrice(list.get(i).getTotalPrice().multiply(disc));
            }

            //Fatura bazında oran iskontosu girildi ise
            if (obj.isIsDiscountRate() && obj.getDiscountRate() != null && obj.getDiscountRate().doubleValue() > 0) {

                BigDecimal disc = new BigDecimal(BigInteger.ONE).subtract(obj.getDiscountRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                list.get(i).setTotalPrice(list.get(i).getTotalPrice().multiply(disc));
            }

        }

        return listItems;

    }

    @Override
    public IncomingInvoicesItem calculaterItem(IncomingInvoicesItem invoiceItem) {
        BigDecimal up = null;

        //miktar veya birim fiyat yoksa hesaplama yapılamaz!
        if (invoiceItem.getQuantity() == null || invoiceItem.getUnitPrice() == null || invoiceItem.getQuantity().doubleValue() == 0 || invoiceItem.getUnitPrice().doubleValue() == 0) {

            invoiceItem.setTotalPrice(BigDecimal.ZERO);
            invoiceItem.setTotalMoney(BigDecimal.ZERO);
            invoiceItem.setDiscountPrice(BigDecimal.ZERO);
            invoiceItem.setTotalTax(BigDecimal.ZERO);
            return invoiceItem;
        }

        if (!invoiceItem.isIsNotCalcTotalPrice()) { // total price yeniden hesaplar
            //vergi dahil ise vergi haric birim fiyata göre topam vergi çıkar
            //System.out.println("-invoiceItem.isIsTaxIncluded()---" + invoiceItem.isIsTaxIncluded());
            if (invoiceItem.isIsTaxIncluded()) {
                if (invoiceItem.getTaxRate() != null) {
                    BigDecimal x = BigDecimal.ONE.add(invoiceItem.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
                    up = invoiceItem.getUnitPrice().divide(x, 4, RoundingMode.HALF_EVEN);
                } else {
                    up = invoiceItem.getUnitPrice();
                }

            } else {
                up = invoiceItem.getUnitPrice();
            }
            //System.out.println("up"+up);

            //vergisiz birim fiyat ve miktarı çarpıp vergisiz tutarı hesapladık
            if (invoiceItem.getQuantity() != null && up != null) {
                invoiceItem.setTotalPrice(invoiceItem.getQuantity().multiply(up));
            } else {
                invoiceItem.setTotalPrice(BigDecimal.ZERO);
            }

            //System.out.println("invoiceItem.getTotalPrice"+invoiceItem.getTotalPrice());
            //iskonto tutarını hesapladık.
            //   System.out.println("CALCULATER---invoiceItem.isIsDiscountRate()---" + invoiceItem.isIsDiscountRate());
            if (!invoiceItem.isIsDiscountRate()) {//tutar girildi oran hesapla
                if (invoiceItem.getDiscountPrice() != null && invoiceItem.getDiscountPrice().doubleValue() > 0) {
                    invoiceItem.setDiscountRate((invoiceItem.getDiscountPrice().divide(invoiceItem.getTotalPrice(), 4, RoundingMode.HALF_EVEN)).multiply(new BigDecimal(100)));
                } else {
                    invoiceItem.setDiscountPrice(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate(BigDecimal.ZERO);
                }
            } else if (invoiceItem.isIsDiscountRate()) {//oran girildi tutar hesapla
                if (invoiceItem.getDiscountRate() != null && invoiceItem.getDiscountRate().doubleValue() > 0) {
                    invoiceItem.setDiscountPrice((invoiceItem.getTotalPrice().multiply(invoiceItem.getDiscountRate())).movePointLeft(2));
                } else {
                    invoiceItem.setDiscountPrice(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate(BigDecimal.ZERO);
                }
            } else {
                invoiceItem.setDiscountPrice(BigDecimal.ZERO);
                invoiceItem.setDiscountRate(BigDecimal.ZERO);
            }

            invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().subtract(invoiceItem.getDiscountPrice() != null ? invoiceItem.getDiscountPrice() : BigDecimal.valueOf(0)));

            //2.iskonto hesabı
            if (!invoiceItem.isIsDiscountRate2()) {//tutar girildi oran hesapla
                if (invoiceItem.getDiscountPrice2() != null && invoiceItem.getDiscountPrice2().doubleValue() > 0) {
                    invoiceItem.setDiscountRate2((invoiceItem.getDiscountPrice2().divide(invoiceItem.getTotalPrice(), 4, RoundingMode.HALF_EVEN)).multiply(new BigDecimal(100)));
                } else {
                    invoiceItem.setDiscountPrice2(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate2(BigDecimal.ZERO);
                }
            } else if (invoiceItem.isIsDiscountRate2()) {//oran girildi tutar hesapla
                if (invoiceItem.getDiscountRate2() != null && invoiceItem.getDiscountRate2().doubleValue() > 0) {
                    invoiceItem.setDiscountPrice2((invoiceItem.getTotalPrice().multiply(invoiceItem.getDiscountRate2())).movePointLeft(2));
                } else {
                    invoiceItem.setDiscountPrice2(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate2(BigDecimal.ZERO);
                }
            } else {
                invoiceItem.setDiscountPrice2(BigDecimal.ZERO);
                invoiceItem.setDiscountRate2(BigDecimal.ZERO);
            }
            //   System.out.println("-Ürün--iskonto oranu ===" + invoiceItem.getDiscountRate());
            // System.out.println("-ÜRün-iskonto tutarı===" + invoiceItem.getDiscountPrice());
            //iskontoyu düştük
            invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().subtract(invoiceItem.getDiscountPrice2()));

            //System.out.println("--Ürün İskontosu Düştükten Sonra--" + invoiceItem.getTotalPrice());
            //Fatura bazında tutar iskontosu girildi ise önce oran bul sonra uygula
            if (!invoiceItem.getInvoice().isIsDiscountRate() && invoiceItem.getInvoice().getDiscountPrice() != null && invoiceItem.getInvoice().getDiscountPrice().doubleValue() > 0) {
                //fatura bazında tutar iskontosu varsa yeniden oran hesaplayıp iskontosunu bul
                BigDecimal tempTotalPrice = invoiceItem.getInvoice().getTotalPrice();

                //hiç ürün yoksa iskontoyu ekleme
                if (tempTotalPrice.compareTo(BigDecimal.ZERO) != 0) {
                    tempTotalPrice = tempTotalPrice.add(invoiceItem.getInvoice().getDiscountPrice());
                }

                //yeni ürün ekleniyorsa onun tutarınıda ekle.
                if (invoiceItem.getId() == 0) {
                    tempTotalPrice = tempTotalPrice.add(invoiceItem.getTotalPrice());
                }

                BigDecimal rate = BigDecimal.valueOf(0);
                BigDecimal disc = BigDecimal.valueOf(0);
                if (tempTotalPrice != null && tempTotalPrice.compareTo(BigDecimal.valueOf(0)) != 0) {
                    rate = new BigDecimal(100).multiply(invoiceItem.getInvoice().getDiscountPrice()).divide(tempTotalPrice, 8, RoundingMode.HALF_EVEN);
                    disc = new BigDecimal(BigInteger.ONE).subtract(rate.divide(new BigDecimal(100), 8, RoundingMode.HALF_EVEN));
                }

                invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().multiply(disc));
            }

            //Fatura bazında oran iskontosu girildi ise
            if (invoiceItem.getInvoice().isIsDiscountRate() && invoiceItem.getInvoice().getDiscountRate() != null && invoiceItem.getInvoice().getDiscountRate().doubleValue() > 0) {

                BigDecimal disc = new BigDecimal(BigInteger.ONE).subtract(invoiceItem.getInvoice().getDiscountRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().multiply(disc));
            }
        }
        //  System.out.println("*Fstura bazında iskonto düşüldükte sonra---" + invoiceItem.getTotalPrice());
        // vergi oranını hesapladık.
        if (invoiceItem.getTaxRate() != null) {
            if (invoiceItem.getTaxRate().doubleValue() > 0) {
                invoiceItem.setTotalTax(invoiceItem.getTotalPrice().multiply(invoiceItem.getTaxRate()).divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
            } else {
                invoiceItem.setTotalTax(BigDecimal.ZERO);
            }
        } else {
            invoiceItem.setTotalTax(BigDecimal.ZERO);
        }

        // System.out.println("invoiceItem.getTotalTax"+invoiceItem.getTotalTax());
        invoiceItem.setTotalMoney(invoiceItem.getTotalPrice().add(invoiceItem.getTotalTax()));

        //System.out.println("invoiceItem.getTotalMoney"+invoiceItem.getTotalMoney());
//        calculateProfit(invoiceItem);
        return invoiceItem;
    }

    //UyumSoft web servisinden tek responseda dönen faturaları ayırarak log tablosuna kaydeder
    @Override
    public List<EInvoice> uListOfIncomingInvoice(Date beginDate, Date endDate) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String data = " ";
        List<IncomingEInvoice> listNew = new ArrayList<>();
        List<IncomingEInvoice> listCreated = new ArrayList<>();
        data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:GetInboxInvoices>\n"
                + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                + "            <tem:query  PageSize=\"10\" SetTaken=\"true\" OnlyNewestInvoices=\"true\">\n"
                + "                <tem:ExecutionStartDate>" + sdf.format(beginDate) + "</tem:ExecutionStartDate>\n"
                + "                <tem:ExecutionEndDate>" + sdf.format(endDate) + "</tem:ExecutionEndDate>\n"
                + "            </tem:query>\n"
                + "        </tem:GetInboxInvoices>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";
        try {

            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/GetInboxInvoices");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                int returnCode = httpClient.executeMethod(methodPost);
                System.out.println("data------" + data);
                if (returnCode == 200) {

                    br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String readLine;

                    while (((readLine = br.readLine()) != null)) {
                        sb.append(readLine);
                    }
                    String result = sb.toString();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                    Document document = builder.parse(inputSource);
                    System.out.println("----result----" + result);

                    result = result.replace("'", " ");
                    result = result.replace("&", " ");
//                  NodeList returnHeader = document.getElementsByTagName("GetInboxInvoicesResult").item(0).getChildNodes().item(0).getChildNodes();

                    if (document.getElementsByTagName("GetInboxInvoicesResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true")) {

                        if (document.getElementsByTagName("Value").item(0).getChildNodes().getLength() != 0) {
                            List<IncomingEInvoice> listInvoice = new ArrayList<>();

                            listInvoice = incomingEInvoicesDao.getInvoicesData(result); // İnvoice'leri ayırdık

                            for (int i = 0; i < listInvoice.size(); i++) {

                                IncomingEInvoice iei = new IncomingEInvoice();
                                iei.setId(i + 1);
                                String strData = listInvoice.get(i).getGetData();
                                iei.setGetData(strData);
                                iei.setIsSuccess(false);
                                iei.setResponseCode("true");
                                iei.setResponseDescription(null);
                                iei.setProcessDate(date);
                                iei.setApprovalStatusId(0);
                                iei.setApprovalDescription(null);
                                iei = updateIncomingInvoice(iei);
                                listNew.add(iei);

                            }
                            List<Integer> resultCreate;
                            resultCreate = incomingEInvoicesDao.create(listNew);

                            if (!resultCreate.isEmpty()) {
                                String ids = "";
                                for (int i = 0; i < resultCreate.size(); i++) {

                                    ids = ids + "," + resultCreate.get(i);
                                }

                                if (!ids.isEmpty()) {
                                    ids = ids.substring(1, ids.length());
                                    listCreated = incomingEInvoicesDao.findGIBIncomingInvoices(ids);

                                }
                            }

                        } else {

                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("neweinvoicenotfoundininbox")));
                            RequestContext.getCurrentInstance().update("grwProcessMessage");

                        }
                    } else {

                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");

                    }
                } else {
                    System.out.println("----returncode----uListOfIncomingInvoice" + returnCode);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");

                }
            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("-------catch-1---uListOfIncomingInvoice--" + e.getMessage());
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("-----catch-2--uListOfIncomingInvoice-" + fe.getMessage());
                    }
                }

            }

        } catch (Exception ex) {
            System.out.println("-----catch-3-uListOfIncomingInvoice--" + ex.getMessage());
        }
        return uListGetInvoices(listCreated);
    }

    public IncomingEInvoice updateIncomingInvoice(IncomingEInvoice obj) { // Web servisten alınan fatura xmli içerisinden tarih ve fatura numarası bilgilerini alır

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String invoiceDate = "";
        String invoiceTime = "";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;

            builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(new StringReader(obj.getGetData()));

            Document document = null;
            document = builder.parse(inputSource);

            document.getDocumentElement().normalize();

            Element root = null;
            root = document.getDocumentElement();

            NodeList returnHeader = root.getChildNodes();

            if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 2) {

                for (int j = 1; j < returnHeader.getLength(); j = j + 2) {

                    Element nodeChield = (Element) returnHeader.item(j);
                    if (nodeChield.getNodeName().equalsIgnoreCase("Invoice")) {
                        NodeList invoiceChields = nodeChield.getChildNodes();

                        for (int k = 1; k < invoiceChields.getLength(); k = k + 2) {
                            Element invoiceChield = (Element) invoiceChields.item(k);

                            if (invoiceChield.getNodeName().equalsIgnoreCase("ID")) {
                                if (invoiceChield.getTextContent() != null) {
                                    obj.setGibInvoice(invoiceChield.getTextContent());
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("IssueDate")) {
                                if (invoiceChield.getTextContent() != null) {
                                    invoiceDate = invoiceChield.getTextContent();
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("IssueTime")) {
                                if (invoiceChield.getTextContent() != null) {
                                    invoiceTime = invoiceChield.getTextContent();
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("AccountingSupplierParty")) {
                                String accountName = "";

                                if (invoiceChield.getChildNodes().getLength() != 0) {
                                    NodeList accountingChields = invoiceChield.getChildNodes();
                                    for (int y = 1; y < accountingChields.getLength(); y = y + 2) {
                                        Element accountingChield = (Element) accountingChields.item(y);
                                        if (accountingChield.getNodeName().equalsIgnoreCase("Party")) {
                                            if (accountingChield.getChildNodes().getLength() != 0) {
                                                NodeList partyChields = accountingChield.getChildNodes();
                                                for (int z = 1; z < partyChields.getLength(); z = z + 2) {
                                                    Element partyChield = (Element) partyChields.item(z);
                                                    if (partyChield.getNodeName().equalsIgnoreCase("PartyIdentification")) {
                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList partyIdentificationChields = partyChield.getChildNodes();
                                                            for (int t = 1; t < partyIdentificationChields.getLength(); t = t + 2) {

                                                                Element partyIdentificationChield = (Element) partyIdentificationChields.item(t);
                                                                if (partyIdentificationChield.getNodeName().equalsIgnoreCase("ID")) {

                                                                    if (partyIdentificationChield.getAttributes().getNamedItem("schemeID").getTextContent() != null && partyIdentificationChield.getTextContent() != null) {

                                                                        if (partyIdentificationChield.getAttributes().getNamedItem("schemeID").getTextContent().equalsIgnoreCase("VKN") || partyIdentificationChield.getAttributes().getNamedItem("schemeID").getTextContent().equalsIgnoreCase("TCKN")) {
                                                                            obj.setGibTaxNo(partyIdentificationChield.getTextContent());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (partyChield.getNodeName().equalsIgnoreCase("PartyName")) {
                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList partyNameChields = partyChield.getChildNodes();
                                                            for (int a = 1; a < partyNameChields.getLength(); a = a + 2) {
                                                                Element partyNameChield = (Element) partyNameChields.item(a);
                                                                if (partyNameChield.getNodeName().equalsIgnoreCase("Name") && partyNameChield.getTextContent() != null) {
                                                                    obj.setGibAccountName(partyNameChield.getTextContent());
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (partyChield.getNodeName().equalsIgnoreCase("Person")) {

                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList personChields = partyChield.getChildNodes();
                                                            for (int a = 1; a < personChields.getLength(); a = a + 2) {
                                                                Element personChield = (Element) personChields.item(a);
                                                                if (personChield.getNodeName().equalsIgnoreCase("FirstName") && personChield.getTextContent() != null) {
                                                                    accountName = accountName + personChield.getTextContent();
                                                                }

                                                                if (personChield.getNodeName().equalsIgnoreCase("FamilyName") && personChield.getTextContent() != null) {
                                                                    accountName = accountName + personChield.getTextContent();
                                                                }

                                                                if (personChield.getNodeName().equalsIgnoreCase("Title") && personChield.getTextContent() != null) {
                                                                    obj.setGibAccountName(personChield.getTextContent());
                                                                }
                                                                if (!accountName.isEmpty()) {
                                                                    obj.setGibAccountName(accountName);
                                                                }

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                    if (nodeChield.getNodeName().equalsIgnoreCase("CreateDateUtc")) {
                        if (nodeChield.getTextContent() != null) {
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                            Date formattedDate4 = inputFormat.parse(nodeChield.getTextContent());
                            String newDate;
                            newDate = sdf1.format(formattedDate4);
                            Date dateNew1 = sdf1.parse(newDate);
                            obj.setGibDate(dateNew1);
                        }
                    }

                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date formattedDate4 = sdf1.parse(invoiceDate);
                    if (!invoiceTime.isEmpty()) {
                        invoiceDate = sdf1.format(formattedDate4) + " " + invoiceTime;
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date time = sdf2.parse(invoiceDate);
                        obj.setInvoiceDate(time);
                    } else {
                        invoiceDate = sdf1.format(formattedDate4);
                        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                        Date time = sdf2.parse(invoiceDate);
                        obj.setInvoiceDate(time);
                    }

                }
            } else if (sessionBean.getUser().getLastBranchSetting().geteInvoiceIntegrationTypeId() == 1) {

                for (int a = 1; a < returnHeader.getLength(); a = a + 2) {

                    Element node1 = (Element) returnHeader.item(a);

                    if (node1.getNodeName() == "a:Body") {

                        NodeList itemList = node1.getChildNodes();
                        for (int k = 1; k < itemList.getLength(); k = k + 2) {

                            Element bodyChield = (Element) itemList.item(k);

                            if (bodyChield.getNodeName().equalsIgnoreCase("b:ID") && bodyChield.getTextContent() != null) {
                                obj.setGibInvoice(bodyChield.getTextContent());
                            }

                            if (node1.getNodeName() == "a:Header") {

                                NodeList headerItemList = node1.getChildNodes();
                                for (int j = 1; j < headerItemList.getLength(); j = j + 2) {
                                    Element nodeHeader = (Element) headerItemList.item(j);

                                    if (nodeHeader.getNodeName() == "b:ReceiveDateTime") {

                                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                                        Date formattedDate4 = inputFormat.parse(nodeHeader.getTextContent());
                                        String newDate;
                                        newDate = sdf1.format(formattedDate4);
                                        Date dateNew1 = sdf1.parse(newDate);
                                        obj.setGibDate(dateNew1);

                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("----catch--updateIncomingInvoice-" + e.getMessage());
        }

        return obj;
    }

    @Override
    public List<EInvoice> findInMarwizEInvoices(int first, int pageSize, String sortField, String sortOrder, java.util.Map<String, Object> filters, Date beginDate, Date endDate, String where, boolean isLazy) {
        List<IncomingEInvoice> resultList = new ArrayList<>();
        resultList = incomingEInvoicesDao.findall(first, pageSize, sortField, sortOrder, filters, where, beginDate, endDate, isLazy);
        return uListGetInvoices(resultList);

    }

    //Log tablosundaki xml datadan fatura ve item bilgilerini okur (Uyumsoft)
    @Override
    public List<EInvoice> uListGetInvoices(List<IncomingEInvoice> listEInvoices) {

        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        boolean isDiscount = false;
        String pricingCurrencyCode = "";
        BigDecimal itemExchangeRate = new BigDecimal(BigInteger.ONE);

        listOfGetInvoices.clear();
        tempListOfGetInvoices.clear();
        listInvoice.clear();
        listInvoiceItem.clear();
        listCurrency = sessionBean.getCurrencies();

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            for (int i = 0; i < listEInvoices.size(); i++) {

                String invoiceDate = "";
                String invoiceTime = "";
                String invoiceDescription = "";
                listEInvoices.get(i).getGetData();

                EInvoice inv = new EInvoice();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = null;

                builder = factory.newDocumentBuilder();

                InputSource inputSource = new InputSource(new StringReader(listEInvoices.get(i).getGetData()));

                Document document = null;
                document = builder.parse(inputSource);

                document.getDocumentElement().normalize();

                Element root = null;
                root = document.getDocumentElement();

                NodeList returnHeader = root.getChildNodes();

                for (int j = 1; j < returnHeader.getLength(); j = j + 2) {

                    Element nodeChield = (Element) returnHeader.item(j);
                    if (nodeChield.getNodeName().equalsIgnoreCase("Invoice")) {
                        NodeList invoiceChields = nodeChield.getChildNodes();

                        for (int k = 1; k < invoiceChields.getLength(); k = k + 2) {
                            Element invoiceChield = (Element) invoiceChields.item(k);
                            if (invoiceChield.getNodeName().equalsIgnoreCase("ProfileID")) {
                                if (invoiceChield.getTextContent().equalsIgnoreCase("TEMELFATURA")) {
                                    inv.setInvoiceScenarioId(1);
                                    if (listEInvoices.get(i).getApprovalStatusId() != 0) {
                                        listEInvoices.get(i).setApprovalStatusId(0);
                                        incomingEInvoicesDao.update(listEInvoices.get(i));
                                    }

                                } else if (invoiceChield.getTextContent().equalsIgnoreCase("TICARIFATURA")) {
                                    inv.setInvoiceScenarioId(2);
                                    if (listEInvoices.get(i).getApprovalStatusId() == 0) {
                                        listEInvoices.get(i).setApprovalStatusId(1);
                                        incomingEInvoicesDao.update(listEInvoices.get(i));
                                    }
                                }
                            }
                            if (invoiceChield.getNodeName().equalsIgnoreCase("ID")) {
                                if (invoiceChield.getTextContent() != null) {
                                    inv.setDocumentNumber(invoiceChield.getTextContent());
                                }
                            }
                            if (invoiceChield.getNodeName().equalsIgnoreCase("UUID")) {
                                if (invoiceChield.getTextContent() != null) {
                                    inv.setDocumentSerial(invoiceChield.getTextContent());
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("IssueDate")) {
                                if (invoiceChield.getTextContent() != null) {
                                    invoiceDate = invoiceChield.getTextContent();
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("IssueTime")) {
                                if (invoiceChield.getTextContent() != null) {
                                    invoiceTime = invoiceChield.getTextContent();
                                }
                            }
                            if (invoiceChield.getNodeName().equalsIgnoreCase("InvoiceTypeCode")) {
                                if (invoiceChield.getTextContent() != null) {

                                    switch (invoiceChield.getTextContent()) {
                                        case "SATIS"://Satış
                                            inv.getType().setId(59);
                                            inv.getType().setTag("İrsaliye Fatura");
                                            break;

                                        case "IADE":// İade
                                            inv.getType().setId(27);
                                            inv.getType().setTag("İade");
                                            break;

                                        case "TEVKIFAT"://Tevkifat
                                            inv.getType().setTag("Tevkifat Faturası");
                                            break;

                                        case "ISTISNA"://İstisna
                                            inv.getType().setId(59);
                                            inv.getType().setTag("İstisna Faturası");
                                            break;

                                        case "IHRACKAYITLI"://İhraç Kayıtlı Fatura
                                            inv.getType().setTag("İhraç Kayıtlı Fatura");
                                            break;

                                        case "OZELMATRAH"://Özel Matrah 
                                            inv.getType().setId(59);
                                            inv.getType().setTag("Özel Matrah Faturası");
                                            break;

                                        default:

                                            break;
                                    }
                                }
                            }
                            if (invoiceChield.getNodeName().equalsIgnoreCase("Note")) {
                                if (invoiceChield.getTextContent() != null) {
                                    invoiceDescription = invoiceDescription + invoiceChield.getTextContent();
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("DocumentCurrencyCode")) {
                                if (invoiceChield.getTextContent() != null) {
                                    for (Currency crr : listCurrency) {
                                        if (crr.getInternationalCode().equalsIgnoreCase(invoiceChield.getTextContent())) {
                                            inv.setCurrency(crr);
                                        }
                                    }
                                    if (inv.getCurrency().getId() == 0) {
                                        for (Currency crr : listCurrency) {
                                            if (crr.getId() == 1) {
                                                inv.setCurrency(crr);
                                            }
                                        }
                                    }
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("PricingCurrencyCode")) {
                                if (invoiceChield.getTextContent() != null) {
                                    pricingCurrencyCode = invoiceChield.getTextContent();
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("AccountingSupplierParty")) {
                                String accountName = "";

                                if (invoiceChield.getChildNodes().getLength() != 0) {
                                    NodeList accountingChields = invoiceChield.getChildNodes();
                                    for (int y = 1; y < accountingChields.getLength(); y = y + 2) {
                                        Element accountingChield = (Element) accountingChields.item(y);
                                        if (accountingChield.getNodeName().equalsIgnoreCase("Party")) {
                                            if (accountingChield.getChildNodes().getLength() != 0) {
                                                NodeList partyChields = accountingChield.getChildNodes();
                                                for (int z = 1; z < partyChields.getLength(); z = z + 2) {
                                                    Element partyChield = (Element) partyChields.item(z);
                                                    if (partyChield.getNodeName().equalsIgnoreCase("PartyIdentification")) {
                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList partyIdentificationChields = partyChield.getChildNodes();
                                                            for (int t = 1; t < partyIdentificationChields.getLength(); t = t + 2) {

                                                                Element partyIdentificationChield = (Element) partyIdentificationChields.item(t);
                                                                if (partyIdentificationChield.getNodeName().equalsIgnoreCase("ID")) {

                                                                    if (partyIdentificationChield.getAttributes().getNamedItem("schemeID").getTextContent() != null && partyIdentificationChield.getTextContent() != null) {

                                                                        if (partyIdentificationChield.getAttributes().getNamedItem("schemeID").getTextContent().equalsIgnoreCase("VKN") || partyIdentificationChield.getAttributes().getNamedItem("schemeID").getTextContent().equalsIgnoreCase("TCKN")) {
                                                                            inv.getAccount().setTaxNo(partyIdentificationChield.getTextContent());
                                                                            inv.setOldTaxNo(partyIdentificationChield.getTextContent());
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (partyChield.getNodeName().equalsIgnoreCase("PartyName")) {
                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList partyNameChields = partyChield.getChildNodes();
                                                            for (int a = 1; a < partyNameChields.getLength(); a = a + 2) {
                                                                Element partyNameChield = (Element) partyNameChields.item(a);
                                                                if (partyNameChield.getNodeName().equalsIgnoreCase("Name") && partyNameChield.getTextContent() != null) {
                                                                    inv.getAccount().setTitle(partyNameChield.getTextContent());
                                                                    inv.setOldAccountName(partyNameChield.getTextContent());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (partyChield.getNodeName().equalsIgnoreCase("Contact")) {
                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList contactChields = partyChield.getChildNodes();
                                                            for (int a = 1; a < contactChields.getLength(); a = a + 2) {
                                                                Element contactChield = (Element) contactChields.item(a);
                                                                if (contactChield.getNodeName().equalsIgnoreCase("Telephone")) {
                                                                    if (contactChield.getTextContent() != null) {
                                                                        inv.getAccount().setPhone(contactChield.getTextContent());
                                                                    }
                                                                }

                                                                if (contactChield.getNodeName().equalsIgnoreCase("ElectronicMail")) {
                                                                    if (contactChield.getTextContent() != null) {
                                                                        inv.getAccount().setEmail(contactChield.getTextContent());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (partyChield.getNodeName().equalsIgnoreCase("Person")) {

                                                        if (partyChield.getChildNodes().getLength() != 0) {
                                                            NodeList personChields = partyChield.getChildNodes();
                                                            for (int a = 1; a < personChields.getLength(); a = a + 2) {
                                                                Element personChield = (Element) personChields.item(a);
                                                                if (personChield.getNodeName().equalsIgnoreCase("FirstName") && personChield.getTextContent() != null) {
                                                                    accountName = accountName + personChield.getTextContent();
                                                                }

                                                                if (personChield.getNodeName().equalsIgnoreCase("FamilyName") && personChield.getTextContent() != null) {
                                                                    accountName = accountName + personChield.getTextContent();
                                                                }

                                                                if (personChield.getNodeName().equalsIgnoreCase("Title") && personChield.getTextContent() != null) {
                                                                    inv.getAccount().setTitle(personChield.getTextContent());
                                                                }

                                                                inv.getAccount().setName(accountName);
                                                                inv.setOldAccountName(accountName);

                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("AllowanceCharge")) {

                                if (invoiceChield.getChildNodes().getLength() != 0) {
                                    NodeList allowanceChargeChields = invoiceChield.getChildNodes();
                                    for (int a = 1; a < allowanceChargeChields.getLength(); a = a + 2) {
                                        Element allowanceChargeChield = (Element) allowanceChargeChields.item(a);

                                        if (allowanceChargeChield.getNodeName().equalsIgnoreCase("ChargeIndicator") && allowanceChargeChield.getTextContent() != null) {
                                            if (allowanceChargeChield.getTextContent().equalsIgnoreCase("false")) {
                                                isDiscount = true;
                                                inv.setIsDiscountRate(false);
                                            }
                                        }

                                        if (allowanceChargeChield.getNodeName().equalsIgnoreCase("MultiplierFactorNumeric") && allowanceChargeChield.getTextContent() != null) {
                                            String dscRate = allowanceChargeChield.getTextContent();
                                            BigDecimal discountRate = new BigDecimal(dscRate);
                                            inv.setDiscountRate(discountRate != null ? discountRate : BigDecimal.ZERO);
                                        }

                                        if (allowanceChargeChield.getNodeName().equalsIgnoreCase("Amount") && allowanceChargeChield.getTextContent() != null) {
                                            String dscAmount = allowanceChargeChield.getTextContent();
                                            BigDecimal discountAmount = new BigDecimal(dscAmount);
                                            inv.setDiscountPrice(discountAmount != null ? discountAmount : BigDecimal.ZERO);
                                        }
                                    }
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("PricingExchangeRate")) {
                                if (invoiceChield.getChildNodes().getLength() != 0) {
                                    NodeList pricingExchangeRateChields = invoiceChield.getChildNodes();
                                    for (int a = 1; a < pricingExchangeRateChields.getLength(); a = a + 2) {
                                        Element pricingExchangeRateChield = (Element) pricingExchangeRateChields.item(a);
                                        if (pricingExchangeRateChield.getNodeName().equalsIgnoreCase("CalculationRate") && pricingExchangeRateChield.getTextContent() != null) {
                                            String exchangeRate = pricingExchangeRateChield.getTextContent();
                                            itemExchangeRate = new BigDecimal(exchangeRate);
                                            itemExchangeRate = itemExchangeRate != null ? itemExchangeRate : BigDecimal.ZERO;

                                        }
                                    }
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("TaxTotal")) {
                                if (invoiceChield.getChildNodes().getLength() != 0) {
                                    NodeList taxTotalChields = invoiceChield.getChildNodes();
                                    for (int v = 1; v < taxTotalChields.getLength(); v = v + 2) {
                                        Element taxTotalChield = (Element) taxTotalChields.item(v);
                                        if (taxTotalChield.getNodeName().equalsIgnoreCase("TaxAmount")) {
                                            if (taxTotalChield.getTextContent() != null) {
                                                String taxAmount = taxTotalChield.getTextContent();
                                                BigDecimal totalTax = new BigDecimal(taxAmount);
                                                inv.setTotalTax(totalTax != null ? totalTax : BigDecimal.ZERO);
                                            }
                                        }
                                    }
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("LegalMonetaryTotal")) {
                                if (invoiceChield.getChildNodes().getLength() != 0) {
                                    NodeList legalChields = invoiceChield.getChildNodes();
                                    for (int x = 1; x < legalChields.getLength(); x = x + 2) {
                                        Element legalChield = (Element) legalChields.item(x);
                                        if (legalChield.getNodeName().equalsIgnoreCase("TaxExclusiveAmount")) {
                                            if (legalChield.getTextContent() != null) {
                                                String ttlPrice = legalChield.getTextContent();
                                                BigDecimal totalPrice = new BigDecimal(ttlPrice);
                                                inv.setTotalPrice(totalPrice != null ? totalPrice : BigDecimal.ZERO);
                                            }
                                        }
                                        if (legalChield.getNodeName().equalsIgnoreCase("TaxInclusiveAmount")) {
                                            if (legalChield.getTextContent() != null) {
                                                String ttlMoney = legalChield.getTextContent();
                                                BigDecimal totalMoney = new BigDecimal(ttlMoney);
                                                inv.setTotalMoney(totalMoney != null ? totalMoney : BigDecimal.ZERO);
                                            }
                                        }
                                        if (legalChield.getNodeName().equalsIgnoreCase("PayableRoundingAmount")) {
                                            if (legalChield.getTextContent() != null) {
                                                String rndngPrice = legalChield.getTextContent();
                                                BigDecimal roundingPrice = new BigDecimal(rndngPrice);
                                                inv.setRoundingPrice(roundingPrice != null ? roundingPrice : BigDecimal.ZERO);
                                            }
                                        }
                                    }
                                }
                            }

                            if (invoiceChield.getNodeName().equalsIgnoreCase("InvoiceLine")) {
                                if (invoiceChield.getChildNodes().getLength() != 0) {
                                    IncomingInvoicesItem iItem = new IncomingInvoicesItem();

                                    NodeList invoiceLineChields = invoiceChield.getChildNodes();
                                    for (int y = 1; y < invoiceLineChields.getLength(); y = y + 2) {
                                        Element invoiceLineChield = (Element) invoiceLineChields.item(y);
                                        if (invoiceLineChield.getNodeName().equalsIgnoreCase("InvoicedQuantity")) {
                                            if (invoiceLineChield.getTextContent() != null) {
                                                if (invoiceLineChield.getAttributes().getNamedItem("unitCode").getTextContent() != null) {
                                                    iItem.getUnit().setSortName(invoiceLineChield.getAttributes().getNamedItem("unitCode").getTextContent());
                                                    iItem.getUnit().setId(0);
                                                    iItem.setOldUnitName(invoiceLineChield.getAttributes().getNamedItem("unitCode").getTextContent());
                                                }

                                                String quantity = invoiceLineChield.getTextContent();
                                                BigDecimal stockQuantity = new BigDecimal(quantity);
                                                iItem.setQuantity(stockQuantity != null ? stockQuantity : BigDecimal.ZERO);

                                            }
                                        }
                                        if (invoiceLineChield.getNodeName().equalsIgnoreCase("LineExtensionAmount")) {

                                            if (invoiceLineChield.getTextContent() != null) {
                                                String stckTtlPrice = invoiceLineChield.getTextContent();
                                                BigDecimal stockTotalPrice = new BigDecimal(stckTtlPrice);
                                                iItem.setTotalPrice(stockTotalPrice != null ? stockTotalPrice : BigDecimal.ZERO);
                                            }
                                        }

                                        if (invoiceLineChield.getNodeName().equalsIgnoreCase("AllowanceCharge")) {
                                            if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                NodeList allowanceChargeChields = invoiceLineChield.getChildNodes();
                                                for (int a = 1; a < allowanceChargeChields.getLength(); a = a + 2) {
                                                    Element allowanceChargeChield = (Element) allowanceChargeChields.item(a);

                                                    if (allowanceChargeChield.getNodeName().equalsIgnoreCase("ChargeIndicator") && allowanceChargeChield.getTextContent() != null) {
                                                        if (allowanceChargeChield.getTextContent().equalsIgnoreCase("false")) {
                                                            iItem.setIsDiscountRate(false);
                                                        }
                                                    }

                                                    if (allowanceChargeChield.getNodeName().equalsIgnoreCase("MultiplierFactorNumeric") && allowanceChargeChield.getTextContent() != null) {
                                                        String stckDscRate = allowanceChargeChield.getTextContent();
                                                        BigDecimal stockDiscountRate = new BigDecimal(stckDscRate);
                                                        iItem.setDiscountRate(stockDiscountRate != null ? stockDiscountRate : BigDecimal.ZERO);
                                                    }

                                                    if (allowanceChargeChield.getNodeName().equalsIgnoreCase("Amount") && allowanceChargeChield.getTextContent() != null) {
                                                        String stckDscPrice = allowanceChargeChield.getTextContent();
                                                        BigDecimal stockDiscountPrice = new BigDecimal(stckDscPrice);
                                                        iItem.setDiscountPrice(stockDiscountPrice != null ? stockDiscountPrice : BigDecimal.ZERO);
                                                    }
                                                }
                                            }
                                        }

                                        if (invoiceLineChield.getNodeName().equalsIgnoreCase("TaxTotal")) {
                                            if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                NodeList stckTaxTotalChields = invoiceLineChield.getChildNodes();
                                                for (int z = 1; z < stckTaxTotalChields.getLength(); z = z + 2) {
                                                    Element stckTaxTotalChield = (Element) stckTaxTotalChields.item(z);
                                                    if (stckTaxTotalChield.getNodeName().equalsIgnoreCase("TaxSubtotal")) {
                                                        if (stckTaxTotalChield.getChildNodes().getLength() != 0) {
                                                            NodeList stckTaxSubtotalChields = stckTaxTotalChield.getChildNodes();
                                                            for (int a = 1; a < stckTaxSubtotalChields.getLength(); a = a + 2) {
                                                                Element stckTaxSubtotalChield = (Element) stckTaxSubtotalChields.item(a);
                                                                if (stckTaxSubtotalChield.getNodeName().equalsIgnoreCase("TaxAmount") && stckTaxSubtotalChield.getTextContent() != null) {
                                                                    String stckTaxTotal = stckTaxSubtotalChield.getTextContent();
                                                                    BigDecimal stockTaxTotal = new BigDecimal(stckTaxTotal);
                                                                    iItem.setTotalTax(stockTaxTotal != null ? stockTaxTotal : BigDecimal.ZERO);
                                                                }

                                                                if (stckTaxSubtotalChield.getNodeName().equalsIgnoreCase("Percent") && stckTaxSubtotalChield.getTextContent() != null) {
                                                                    String stckTaxRate = stckTaxSubtotalChield.getTextContent();
                                                                    BigDecimal stockTaxRate = new BigDecimal(stckTaxRate);
                                                                    iItem.setTaxRate(stockTaxRate != null ? stockTaxRate : BigDecimal.ZERO);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (invoiceLineChield.getNodeName().equalsIgnoreCase("Item")) {
                                            if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                NodeList itemChields = invoiceLineChield.getChildNodes();
                                                for (int a = 1; a < itemChields.getLength(); a = a + 2) {
                                                    Element itemChield = (Element) itemChields.item(a);

                                                    if (itemChield.getNodeName().equalsIgnoreCase("Name") && itemChield.getTextContent() != null) {
                                                        iItem.getStock().setName(itemChield.getTextContent());
                                                        iItem.setOldStockName(itemChield.getTextContent());
                                                    } else if (itemChield.getNodeName().equalsIgnoreCase("SellersItemIdentification") && itemChield.getTextContent() != null) {
                                                        if (itemChield.getChildNodes().getLength() != 0) {

                                                            NodeList itemChieldsSeller = itemChield.getChildNodes();

                                                            for (int b = 1; b < itemChieldsSeller.getLength(); b = b + 2) {

                                                                Element itemChieldSeller = (Element) itemChieldsSeller.item(b);

                                                                if (itemChieldSeller.getNodeName().equalsIgnoreCase("ID") && itemChieldSeller.getTextContent() != null) {
                                                                    iItem.getStock().getStockInfo().seteInvoiceIntegrationCode(itemChieldSeller.getTextContent());
                                                                    iItem.setOldStockEntegrationCode(itemChieldSeller.getTextContent());
                                                                }

                                                            }
                                                        }

                                                    }

                                                }
                                            }
                                        }

                                        if (invoiceLineChield.getNodeName().equalsIgnoreCase("Price")) {
                                            if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                                NodeList priceChields = invoiceLineChield.getChildNodes();
                                                for (int a = 1; a < priceChields.getLength(); a = a + 2) {
                                                    Element priceChield = (Element) priceChields.item(a);

                                                    if (priceChield.getNodeName().equalsIgnoreCase("PriceAmount") && priceChield.getTextContent() != null) {
                                                        String unitPrice = priceChield.getTextContent();
                                                        BigDecimal stockUnitPrice = new BigDecimal(unitPrice);
                                                        iItem.setUnitPrice(stockUnitPrice != null ? stockUnitPrice : BigDecimal.ZERO);
                                                        if (priceChield.getAttributes().getNamedItem("currencyID").getTextContent() != null) {
                                                            for (Currency crr : listCurrency) {
                                                                if (crr.getInternationalCode().equalsIgnoreCase(priceChield.getAttributes().getNamedItem("currencyID").getTextContent())) {
                                                                    iItem.setCurrency(crr);
                                                                }
                                                            }
                                                        }

                                                        if (iItem.getCurrency().getId() == 0) {
                                                            for (Currency crr : listCurrency) {
                                                                if (crr.getId() == 1) {
                                                                    iItem.setCurrency(crr);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (iItem.getTotalMoney() == null) {

                                        iItem.setTotalMoney(BigDecimal.ZERO);

                                    }

                                    if (iItem.getDiscountPrice() == null) {

                                        iItem.setDiscountPrice(BigDecimal.ZERO);

                                    }

                                    if (iItem.getTotalPrice() == null) {

                                        iItem.setTotalPrice(BigDecimal.ZERO);

                                    }

                                    if (iItem.getTotalTax() == null) {

                                        iItem.setTotalTax(BigDecimal.ZERO);
                                    }

                                  if (iItem.getTotalTax() != null) {

                                        iItem.setTotalMoney((iItem.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalPrice() : BigDecimal.ZERO).add(iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalTax() : BigDecimal.ZERO));
                                    }

                                    iItem.setExchangeRate(BigDecimal.ONE);
                                    iItem.getInvoice().setId(listEInvoices.get(i).getId());
                                    listInvoiceItem.add(iItem);
                                    for (int y = 0; y < listInvoiceItem.size(); y++) {
                                        listInvoiceItem.get(y).setId(y + 1);
                                        listInvoiceItem.get(y).getStock().setId(0);
                                        listInvoiceItem.get(y).getUnit().setId(0);

                                    }
                                }
                            }
                        }
                    }
                    if (nodeChield.getNodeName().equalsIgnoreCase("CreateDateUtc")) {
                        if (nodeChield.getTextContent() != null) {
                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                            Date formattedDate4 = inputFormat.parse(nodeChield.getTextContent());
                            String newDate;
                            newDate = sdf1.format(formattedDate4);
                            Date dateNew1 = sdf1.parse(newDate);
                            inv.setReceivedDate(dateNew1);
                        }
                    }
                }

                if (!pricingCurrencyCode.isEmpty()) {
                    if (pricingCurrencyCode.equalsIgnoreCase(inv.getCurrency().getInternationalCode())) {
                        for (int j = 0; j < listInvoiceItem.size(); j++) {
                            if (listEInvoices.get(i).getId() == listInvoiceItem.get(j).getInvoice().getId()) {
                                listInvoiceItem.get(j).setExchangeRate(BigDecimal.ONE);
                                listInvoiceItem.get(j).setCurrency(inv.getCurrency());
                            }
                        }
                        inv.setExchangeRate(BigDecimal.ONE);

                    } else {

                        for (int j = 0; j < listInvoiceItem.size(); j++) {
                            if (listInvoiceItem.get(j).getInvoice().getId() == listEInvoices.get(i).getId()) {
                                listInvoiceItem.get(j).setExchangeRate(itemExchangeRate);
                                for (Currency crr : listCurrency) {
                                    if (crr.getInternationalCode().equalsIgnoreCase(pricingCurrencyCode)) {
                                        listInvoiceItem.get(j).setCurrency(crr);
                                    } else {

                                        for (Currency crr1 : listCurrency) {

                                            if (crr1.getId() == 1) {
                                                listInvoiceItem.get(j).setCurrency(crr1);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Currency currency = inv.getCurrency();
                        Currency resCurrency = brSetting.getBranch().getCurrency();
                        Exchange exchange = new Exchange();
                        exchange = incomingEInvoicesDao.bringExchangeRate(currency, resCurrency);
                        inv.setExchangeRate(exchange.getBuying() != null ? exchange.getBuying() : BigDecimal.ONE);
                    }
                }

                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                Date formattedDate4 = sdf1.parse(invoiceDate);
                if (!invoiceTime.isEmpty()) {
                    invoiceDate = sdf1.format(formattedDate4) + " " + invoiceTime;
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date time = sdf2.parse(invoiceDate);
                    inv.setInvoiceDate(time);
                } else {
                    invoiceDate = sdf1.format(formattedDate4);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    Date time = sdf2.parse(invoiceDate);
                    inv.setInvoiceDate(time);
                }
                inv.setId(listEInvoices.get(i).getId());
                inv.getAccount().setId(0);
                inv.setApprovalStatusId(listEInvoices.get(i).getApprovalStatusId());
                inv.setDescription(invoiceDescription);
                listInvoice.add(inv);

            }
        } catch (Exception e) {
        }
        return listInvoice;

    }

    @Override
    public List<IncomingInvoicesItem> uBringItemList(Invoice obj
    ) {

        List<IncomingInvoicesItem> eInvoiceItemList = new ArrayList<>();
        IncomingEInvoice iei = new IncomingEInvoice();
        iei = incomingEInvoicesDao.bringEInvoiceItem(obj);
        listCurrency = sessionBean.getCurrencies();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(iei.getGetData()));
            Document document = null;
            document = builder.parse(inputSource);

            document.getDocumentElement().normalize();
            Element root = null;
            root = document.getDocumentElement();
            NodeList returnHeader1 = root.getChildNodes();

            for (int j = 1; j < returnHeader1.getLength(); j = j + 2) {
                Element nodeChield = (Element) returnHeader1.item(j);
                if (nodeChield.getNodeName().equalsIgnoreCase("Invoice")) {
                    NodeList invoiceChields = nodeChield.getChildNodes();

                    for (int k = 1; k < invoiceChields.getLength(); k = k + 2) {

                        Element invoiceChield = (Element) invoiceChields.item(k);

                        if (invoiceChield.getNodeName().equalsIgnoreCase("InvoiceLine")) {
                            if (invoiceChield.getChildNodes().getLength() != 0) {
                                IncomingInvoicesItem iItem = new IncomingInvoicesItem();
                                NodeList invoiceLineChields = invoiceChield.getChildNodes();
                                for (int y = 1; y < invoiceLineChields.getLength(); y = y + 2) {

                                    Element invoiceLineChield = (Element) invoiceLineChields.item(y);
                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("InvoicedQuantity")) {
                                        if (invoiceLineChield.getTextContent() != null) {
                                            if (invoiceLineChield.getAttributes().getNamedItem("unitCode").getTextContent() != null) {
                                                iItem.getUnit().setSortName(invoiceLineChield.getAttributes().getNamedItem("unitCode").getTextContent());
                                                iItem.getUnit().setId(0);
                                                iItem.setOldUnitName(invoiceLineChield.getAttributes().getNamedItem("unitCode").getTextContent());
                                            }

                                            String quantity = invoiceLineChield.getTextContent();
                                            BigDecimal stockQuantity = new BigDecimal(quantity);
                                            iItem.setQuantity(stockQuantity != null ? stockQuantity : BigDecimal.ZERO);

                                        }
                                    }
                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("LineExtensionAmount")) {

                                        if (invoiceLineChield.getTextContent() != null) {
                                            String stckTtlPrice = invoiceLineChield.getTextContent();
                                            BigDecimal stockTotalPrice = new BigDecimal(stckTtlPrice);
                                            iItem.setTotalPrice(stockTotalPrice != null ? stockTotalPrice : BigDecimal.ZERO);
                                        }
                                    }

                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("AllowanceCharge")) {

                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                            NodeList allowanceChargeChields = invoiceLineChield.getChildNodes();
                                            for (int a = 1; a < allowanceChargeChields.getLength(); a = a + 2) {
                                                Element allowanceChargeChield = (Element) allowanceChargeChields.item(a);

                                                if (allowanceChargeChield.getNodeName().equalsIgnoreCase("ChargeIndicator") && allowanceChargeChield.getTextContent() != null) {
                                                    if (allowanceChargeChield.getTextContent().equalsIgnoreCase("false")) {
                                                        iItem.setIsDiscountRate(false);
                                                    }
                                                }

                                                if (allowanceChargeChield.getNodeName().equalsIgnoreCase("MultiplierFactorNumeric") && allowanceChargeChield.getTextContent() != null) {
                                                    String stckDscRate = allowanceChargeChield.getTextContent();
                                                    BigDecimal stockDiscountRate = new BigDecimal(stckDscRate);
                                                    iItem.setDiscountRate(stockDiscountRate != null ? stockDiscountRate : BigDecimal.ZERO);
                                                }

                                                if (allowanceChargeChield.getNodeName().equalsIgnoreCase("Amount") && allowanceChargeChield.getTextContent() != null) {
                                                    String stckDscPrice = allowanceChargeChield.getTextContent();
                                                    BigDecimal stockDiscountPrice = new BigDecimal(stckDscPrice);
                                                    iItem.setDiscountPrice(stockDiscountPrice != null ? stockDiscountPrice : BigDecimal.ZERO);
                                                }
                                            }
                                        }
                                    }

                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("TaxTotal")) {

                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                            NodeList stckTaxTotalChields = invoiceLineChield.getChildNodes();
                                            for (int z = 1; z < stckTaxTotalChields.getLength(); z = z + 2) {
                                                Element stckTaxTotalChield = (Element) stckTaxTotalChields.item(z);
                                                if (stckTaxTotalChield.getNodeName().equalsIgnoreCase("TaxSubtotal")) {
                                                    if (stckTaxTotalChield.getChildNodes().getLength() != 0) {
                                                        NodeList stckTaxSubtotalChields = stckTaxTotalChield.getChildNodes();
                                                        for (int a = 1; a < stckTaxSubtotalChields.getLength(); a = a + 2) {
                                                            Element stckTaxSubtotalChield = (Element) stckTaxSubtotalChields.item(a);
                                                            if (stckTaxSubtotalChield.getNodeName().equalsIgnoreCase("TaxAmount") && stckTaxSubtotalChield.getTextContent() != null) {
                                                                String stckTaxTotal = stckTaxSubtotalChield.getTextContent();
                                                                BigDecimal stockTaxTotal = new BigDecimal(stckTaxTotal);
                                                                iItem.setTotalTax(stockTaxTotal != null ? stockTaxTotal : BigDecimal.ZERO);
                                                            }

                                                            if (stckTaxSubtotalChield.getNodeName().equalsIgnoreCase("Percent") && stckTaxSubtotalChield.getTextContent() != null) {
                                                                String stckTaxRate = stckTaxSubtotalChield.getTextContent();
                                                                BigDecimal stockTaxRate = new BigDecimal(stckTaxRate);
                                                                iItem.setTaxRate(stockTaxRate != null ? stockTaxRate : BigDecimal.ZERO);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("Item")) {

                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                            NodeList itemChields = invoiceLineChield.getChildNodes();
                                            for (int a = 1; a < itemChields.getLength(); a = a + 2) {
                                                Element itemChield = (Element) itemChields.item(a);
                                                if (itemChield.getNodeName().equalsIgnoreCase("Name") && itemChield.getTextContent() != null) {
                                                    iItem.getStock().setName(itemChield.getTextContent());
                                                    iItem.setOldStockName(itemChield.getTextContent());
                                                }
                                            }
                                        }
                                    }

                                    if (invoiceLineChield.getNodeName().equalsIgnoreCase("Price")) {

                                        if (invoiceLineChield.getChildNodes().getLength() != 0) {
                                            NodeList priceChields = invoiceLineChield.getChildNodes();
                                            for (int a = 1; a < priceChields.getLength(); a = a + 2) {
                                                Element priceChield = (Element) priceChields.item(a);

                                                if (priceChield.getNodeName().equalsIgnoreCase("PriceAmount") && priceChield.getTextContent() != null) {
                                                    String unitPrice = priceChield.getTextContent();
                                                    BigDecimal stockUnitPrice = new BigDecimal(unitPrice);
                                                    iItem.setUnitPrice(stockUnitPrice != null ? stockUnitPrice : BigDecimal.ZERO);
                                                    if (priceChield.getAttributes().getNamedItem("currencyID").getTextContent() != null) {
                                                        for (Currency crr : listCurrency) {
                                                            if (crr.getInternationalCode().equalsIgnoreCase(priceChield.getAttributes().getNamedItem("currencyID").getTextContent())) {
                                                                iItem.setCurrency(crr);
                                                            }
                                                        }
                                                    }

                                                    if (iItem.getCurrency().getId() == 0) {
                                                        for (Currency crr : listCurrency) {
                                                            if (crr.getId() == 1) {
                                                                iItem.setCurrency(crr);
                                                            }
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }

                                if (iItem.getTotalMoney() == null) {

                                    iItem.setTotalMoney(BigDecimal.ZERO);

                                }

                                if (iItem.getDiscountPrice() == null) {

                                    iItem.setDiscountPrice(BigDecimal.ZERO);

                                }

                                if (iItem.getTotalPrice() == null) {

                                    iItem.setTotalPrice(BigDecimal.ZERO);

                                }

                                if (iItem.getTotalTax() == null) {

                                    iItem.setTotalTax(BigDecimal.ZERO);
                                }

                            
                                if (iItem.getTotalTax() != null) {

                                    iItem.setTotalMoney((iItem.getTotalPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalPrice() : BigDecimal.ZERO).add(iItem.getTotalTax().compareTo(BigDecimal.valueOf(0)) == 1 ? iItem.getTotalTax() : BigDecimal.ZERO));
                                }

                                iItem.setExchangeRate(BigDecimal.ONE);

                                eInvoiceItemList.add(iItem);
                                for (int y = 0; y < eInvoiceItemList.size(); y++) {
                                    eInvoiceItemList.get(y).setId(y + 1);
                                    eInvoiceItemList.get(y).getStock().setId(0);
                                    eInvoiceItemList.get(y).getUnit().setId(0);

                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("-------catch---uBringItemList--" + e.getMessage());
        }
        return eInvoiceItemList;
    }

    //(Uyumsoft)Ticari faturalar için onay/ret yanıtı gönderir
    @Override
    public int sendUApproval(EInvoice obj, String eInvoiceUUID
    ) {
        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        String replyState = "";
        int approvalResult = 0;
        if (obj.getApprovalStatusId() == 2) {//ONAY

            replyState = "Approved";

        } else if (obj.getApprovalStatusId() == 3) {//RET

            replyState = "Declined";

        }

        String data = " ";

        data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
                + "    <x:Header/>\n"
                + "    <x:Body>\n"
                + "        <tem:SendDocumentResponse>\n"
                + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                + "            <tem:responses>\n"
                + "                <tem:DocumentResponseInfo>\n"
                + "                    <tem:LineResponses>\n"
                + "                        <tem:LineResponseInfo>\n"
                + "                            <tem:LineNumber>0</tem:LineNumber>\n"
                + "                            <tem:Description>?</tem:Description>\n"
                + "                        </tem:LineResponseInfo>\n"
                + "                    </tem:LineResponses>\n"
                + "                    <tem:InvoiceId>" + eInvoiceUUID + "</tem:InvoiceId>\n"
                + "                    <tem:ResponseStatus>" + replyState + "</tem:ResponseStatus>\n";
        if (obj.getApprovalStatusId() == 3) {
            data = data + " \n"
                    + "                  <tem:Reason>" + obj.getApprovalDescription() + "</tem:Reason>\n";
        }
        data = data + "\n"
                + "                </tem:DocumentResponseInfo>\n"
                + "            </tem:responses>\n"
                + "        </tem:SendDocumentResponse>\n"
                + "    </x:Body>\n"
                + "</x:Envelope>";
        try {

            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(20000);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(20000);
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {

                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/SendDocumentResponse");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                int returnCode = httpClient.executeMethod(methodPost);
                System.out.println("----data------" + data);

                if (returnCode == 200) {

                    br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String readLine;

                    while (((readLine = br.readLine()) != null)) {
                        sb.append(readLine);
                    }
                    String result = sb.toString();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                    Document document = builder.parse(inputSource);
                    System.out.println("----result----" + result);
                    NodeList returnHeader = document.getElementsByTagName("SendDocumentResponseResponse").item(0).getChildNodes();
                    if (document.getElementsByTagName("SendDocumentResponseResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true") && document.getElementsByTagName("SendDocumentResponseResult").item(0).getAttributes().getNamedItem("Value").getTextContent().equalsIgnoreCase("true")) {
                        approvalResult = 1;

                    } else if (document.getElementsByTagName("SendDocumentResponseResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("false") && document.getElementsByTagName("SendDocumentResponseResult").item(0).getAttributes().getNamedItem("Value").getTextContent().equalsIgnoreCase("false")) {
                        approvalMessage = document.getElementsByTagName("SendDocumentResponseResult").item(0).getAttributes().getNamedItem("Message").getTextContent();
                        approvalResult = 0;
                    }
                } else {
                    System.out.println("-----returncode-----" + returnCode);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    approvalResult = -1;
                }

            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                System.out.println("-------catch--1--sendUApproval-" + e.getMessage());
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                        System.out.println("-------catch-2-sendUApproval----" + fe.getMessage());
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("--------catch-3-sendUApproval----" + ex.getMessage());
        }
        return approvalResult;

    }

    @Override
    public String approvalMessage() {
        return approvalMessage;
    }

    @Override
    public List<Unit> bringUnit(Stock stock
    ) {
        return incomingEInvoicesDao.bringUnit(stock);
    }

    @Override
    public int create(IncomingEInvoice obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<IncomingEInvoice> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return incomingEInvoicesDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where, Date beginDate, Date endDate) {
        return incomingEInvoicesDao.count(where, beginDate, endDate);
    }

    @Override
    public int count(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem) {
        return incomingEInvoicesDao.findFuelStockWarehouse(invoiceItem);

    }

    @Override
    public List<Stock> listStock(String stockEInvoiceIntegrationCodeList, EInvoice selectedObject) {
        return incomingEInvoicesDao.listStock(stockEInvoiceIntegrationCodeList, selectedObject);
    }

    @Override
    public int updateStockIntegrationCode(IncomingInvoicesItem obj, String stockInfoIds) {
        return incomingEInvoicesDao.updateStockIntegrationCode(obj, stockInfoIds);
    }

    @Override
    public List<Stock> findStockInfo(String stockEInvoiceIntegrationCode) {
        return incomingEInvoicesDao.findStockInfo(stockEInvoiceIntegrationCode);
    }

    @Override
    public int updateArchive(String ids, int updateType) {

        return incomingEInvoicesDao.updateArchive(ids, updateType);

    }

}
