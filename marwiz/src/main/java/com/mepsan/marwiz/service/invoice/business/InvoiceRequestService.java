/**
 *
 * @author elif.mart
 */
package com.mepsan.marwiz.service.invoice.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.invoice.dao.IInvoiceRequestDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InvoiceRequestService implements IInvoiceRequestService {
    
    @Autowired
    public SessionBean sessionBean;
    
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    //Innova entegratör firması web servisinden carinin mükellef tipini sorgular
    @Override
    public List<Account> sendTaxpayerİnquiryRequest(Account account) {
        
        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        String result = null;
        List<Account> listResult;
        listResult = new ArrayList<>();
        
        try {
            
            String data = " <x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:pay1=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO\" xmlns:pay9=\"http://schemas.datacontract.org/2004/07/PayFlex.EFatura.ClientInterface.Contract.DTO.CustomerInquiry\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">\n"
                    + "    <x:Header/>\n"
                    + "    <x:Body>\n"
                    + "        <tem:CustomerInquiry>\n"
                    + "            <tem:request>\n"
                    + "                <pay1:Header>\n"
                    + "                    <pay1:InstitutionId>" + brSetting.geteInvoiceAccountCode() + "</pay1:InstitutionId>\n"
                    + "                    <pay1:OriginatorUserId>0</pay1:OriginatorUserId>\n"
                    + "                    <pay1:Password>" + brSetting.geteInvoicePassword() + "</pay1:Password>\n"
                    + "                    <pay1:Username>" + brSetting.geteInvoiceUserName() + "</pay1:Username>\n"
                    + "                </pay1:Header>\n"
                    + "                <pay9:IdentityNumbers>\n"
                    + "                    <arr:string>" + account.getTaxNo() + "</arr:string>\n"
                    + "                </pay9:IdentityNumbers>\n"
                    + "            </tem:request>\n"
                    + "        </tem:CustomerInquiry>\n"
                    + "    </x:Body>\n"
                    + "</x:Envelope>";
            
            
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(brSetting.getErpTimeout());
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(brSetting.getErpTimeout());
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {
                
                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IClientInterfaceService/CustomerInquiry");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                
                int returnCode = httpClient.executeMethod(methodPost);
                if (returnCode == 200) {
                    br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                    StringBuilder sb = new StringBuilder();
                    String readLine;
                    
                    while (((readLine = br.readLine()) != null)) {
                        sb.append(readLine);
                    }
                    
                    result = sb.toString();
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder;
                    builder = factory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                    Document document = builder.parse(inputSource);
                    result = document.getElementsByTagName("ResponseCode").item(0).getTextContent();
                    
                    NodeList returnList = document.getElementsByTagName("CustomerInquiryResult").item(0).getChildNodes().item(1).getChildNodes();
                    
                    for (int i = 0; i < returnList.getLength(); i++) {
                        Account acc = new Account();
                        Element node = (Element) returnList.item(i);
                        
                        if (node.getElementsByTagName("a:IsExists").item(0).getTextContent().equalsIgnoreCase("true")) {
                            
                            acc.setTaxpayertype_id(1);
                            acc.setEmail(node.getElementsByTagName("a:Alias").item(0).getTextContent());
                            acc.setTitle(node.getElementsByTagName("a:Name").item(0).getTextContent());
                            
                        } else {
                            
                            acc.setTaxpayertype_id(2);
                            
                        }
                        listResult.add(acc);
                    }
                }
                
            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                    }
                }
                
            }
            
        } catch (Exception ex) {
            
        }
        
        
        return listResult;
    }

    //Uyumsoft entegratör firması web servisinden carinin mükellef tipini sorgular
    @Override
    public Account requestTaxPayerİnquiryRequest(Account account) {
        
        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        String result = null;
        
        try {
            
            String data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
                    + "    <x:Header/>\n"
                    + "    <x:Body>\n"
                    + "        <tem:IsEInvoiceUser>\n"
                    + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                    + "            <tem:vknTckn>" + account.getTaxNo() + "</tem:vknTckn>\n"
                    + "        </tem:IsEInvoiceUser>\n"
                    + "    </x:Body>\n"
                    + "</x:Envelope>";
            
            
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(brSetting.getErpTimeout());
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(brSetting.getErpTimeout());
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {
                
                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/IsEInvoiceUser");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                
                int returnCode = httpClient.executeMethod(methodPost);
                
                br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;
                
                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }
                
                result = sb.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                Document document = builder.parse(inputSource);
                if (returnCode == 200) {
                    if (document.getElementsByTagName("IsEInvoiceUserResponse").item(0).getChildNodes().item(0).getAttributes().item(0).getTextContent().equalsIgnoreCase("true")) {
                        
                        if (document.getElementsByTagName("IsEInvoiceUserResponse").item(0).getChildNodes().item(0).getAttributes().item(1).getTextContent().equalsIgnoreCase("true")) {
                            
                            account.setTaxpayertype_id(1);
                            
                        } else {
                            
                            account.setTaxpayertype_id(2);
                        }
                        
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        
                    }
                } else if (returnCode == 500) {
                    
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("s:Fault").item(0).getChildNodes().item(1).getTextContent()));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    
                }
                
            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                    }
                }
                
            }
            
        } catch (Exception ex) {
            
        }
        return account;
    }
    //Uyumsoft entegratör firması web servisinden carinin vkn, tivari unvan vb. bilgilerini sorgular
    @Override
    public Account requestAccountInfo(Account acc) {
        BranchSetting brSetting = sessionBean.getUser().getLastBranchSetting();
        String result = null;
        
        try {
            
            String data = "<x:Envelope xmlns:x=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\">\n"
                    + "    <x:Header/>\n"
                    + "    <x:Body>\n"
                    + "        <tem:GetUserAliasses>\n"
                    + "            <tem:userInfo Username=\"" + brSetting.geteInvoiceUserName() + "\" Password=\"" + brSetting.geteInvoicePassword() + "\"></tem:userInfo>\n"
                    + "            <tem:vknTckn>"+acc.getTaxNo()+"</tem:vknTckn>\n"
                    + "        </tem:GetUserAliasses>\n"
                    + "    </x:Body>\n"
                    + "</x:Envelope>";
            
            
            HttpClient httpClient = new HttpClient();
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(brSetting.getErpTimeout());
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(brSetting.getErpTimeout());
            BufferedReader br = null;
            PostMethod methodPost = new PostMethod(brSetting.geteInvoiceUrl());
            try {
                
                byte[] encodedAuth = Base64.getEncoder().encode((brSetting.geteInvoiceUserName() + ":" + brSetting.geteInvoicePassword()).getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                methodPost.setRequestHeader("SOAPAction", "http://tempuri.org/IBasicIntegration/GetUserAliasses");
                methodPost.setRequestEntity(new StringRequestEntity(data, "text/xml", "utf-8"));
                
                int returnCode = httpClient.executeMethod(methodPost);
                
                br = new BufferedReader(new InputStreamReader(methodPost.getResponseBodyAsStream(), "utf-8"));
                StringBuilder sb = new StringBuilder();
                String readLine;
                
                while (((readLine = br.readLine()) != null)) {
                    sb.append(readLine);
                }
                
                result = sb.toString();
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                builder = factory.newDocumentBuilder();
                InputSource inputSource = new InputSource(new StringReader(sb.toString()));
                Document document = builder.parse(inputSource);
                if (returnCode == 200) {
                    if (document.getElementsByTagName("GetUserAliassesResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true") && document.getElementsByTagName("GetUserAliassesResult").item(0).getChildNodes().getLength() != 0) {
                        
                        acc.setTaxNo(document.getElementsByTagName("Definition").item(0).getAttributes().getNamedItem("Identifier").getTextContent());
                        acc.setTitle(document.getElementsByTagName("Definition").item(0).getAttributes().getNamedItem("Title").getTextContent());
                        acc.setEmail(document.getElementsByTagName("ReceiverboxAliases").item(0).getAttributes().getNamedItem("Alias").getTextContent());
                    
                    } else if(document.getElementsByTagName("GetUserAliassesResult").item(0).getAttributes().getNamedItem("IsSucceded").getTextContent().equalsIgnoreCase("true") && document.getElementsByTagName("GetUserAliassesResult").item(0).getChildNodes().getLength() == 0){
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("companyinformationforthenumberyouarequeryingcouldnotbefound")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        
                    }
                } else if (returnCode == 500) {
                    
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation") + " " + document.getElementsByTagName("s:Fault").item(0).getChildNodes().item(1).getTextContent()));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    
                }
                
            } catch (IOException | ParserConfigurationException | SAXException | DOMException | NumberFormatException e) {
                
            } finally {
                methodPost.releaseConnection();
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception fe) {
                    }
                }
                
            }
            
        } catch (Exception ex) {
            
        }
        return acc;
        
    }
}
