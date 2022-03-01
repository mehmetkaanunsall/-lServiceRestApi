/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.07.2021 02:55:55
 */
package com.mepsan.marwiz.service.paro.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.service.paro.dao.ICallCampaignInfoDao;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class CallCampaignInfoService implements ICallCampaignInfoService {

    @Autowired
    ICallCampaignInfoDao callCampaignInfoDao;

    public void setCallCampaignInfoDao(ICallCampaignInfoDao callCampaignInfoDao) {
        this.callCampaignInfoDao = callCampaignInfoDao;
    }

    public void callWebServiceInformation(BranchSetting branchSetting) {
        if (branchSetting.getParoUrl() != null && !branchSetting.getParoUrl().isEmpty()) {
            WebServiceClient serviceClient = new WebServiceClient();
            String url = "";
            url = branchSetting.getParoUrl() + "/prjWebService/WsPodIslemleri?invoke=opetIstasyonVerisiSorgula&isyeriKod="
                      + branchSetting.getParoCenterAccountCode() + "&yetkiliKod=" + branchSetting.getParoCenterResponsibleCode() + "&uhkKod=" + branchSetting.getBranch().getLicenceCode()
                      + "&islemTip=1&param1=";

            String resultMessage = serviceClient.requestGetMethod(url);
            if (resultMessage != null) {
                if (!resultMessage.isEmpty()) {
                    try {
                        String resultString = "";
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder;

                        builder = factory.newDocumentBuilder();
                        InputSource inputSource = new InputSource(new StringReader(resultMessage));
                        Document document = builder.parse(inputSource);
                        resultString = document.getElementsByTagName("return").item(0).getTextContent();
                        DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder2;
                        String branchCode = "", pcID;
                        String responsibleCode = "";
                        String accountCode = "";
                        String pcIDString = "";
                        List<String> pcIDList = new ArrayList<>();
                        //Resonse İçerisinden Gelen Sonuç Çözümlenir.
                        builder2 = factory2.newDocumentBuilder();
                        InputSource inputSource2 = new InputSource(new StringReader(resultString));
                        Document document2;

                        document2 = builder2.parse(inputSource2);

                        NodeList nodeList = document2.getElementsByTagName("ISYERIPCID");
                        if (nodeList.getLength() > 0) {
                            for (int i = 0; i < nodeList.getLength(); i++) {
                                Node node = nodeList.item(i);
                                org.w3c.dom.Element element = null;
                                if (node.getNodeType() == Node.ELEMENT_NODE) {
                                    element = (org.w3c.dom.Element) node;

                                    //Tanı 1: UltraMarket  2: FullMarket  3: FullPaket 4:Opet
                                    //1- UltraMarket; 2- FullPaket; 3- FullMarket;
                                    int conceptType;
                                    if (branchSetting.getBranch().getConceptType() == 1) {
                                        conceptType = 1;
                                    } else if (branchSetting.getBranch().getConceptType() == 2) {
                                        conceptType = 3;
                                    } else {
                                        conceptType = 2;
                                    }
                                    if (Integer.parseInt(getTagValue("MARKAKODU", element)) == conceptType) {
                                        branchCode = getTagValue("SUBEKOD", element);
                                        responsibleCode = getTagValue("YETKILIKODU", element);
                                        accountCode = getTagValue("ISYERIKOD", element);
                                        branchSetting.setParoBranchCode(branchCode);
                                        branchSetting.setParoAccountCode(accountCode);
                                        branchSetting.setParoResponsibleCode(responsibleCode);
                                        pcID = getTagValue("PCID", element);
                                        pcIDList.add(pcID);
                                    }
                                }
                            }
                            for (String pc : pcIDList) {
                                pcIDString = pcIDString + "," + String.valueOf(pc);
                            }
                            if (!pcIDString.equals("")) {
                                pcIDString = pcIDString.substring(1, pcIDString.length());
                            }
                            if (!branchCode.equals("") && !branchCode.isEmpty() && !responsibleCode.equals("") && !responsibleCode.isEmpty()
                                      && !accountCode.equals("") && !accountCode.isEmpty()) {
                                callCampaignInfoDao.updateParoInformation(branchSetting, pcIDString);
                            }

                        }
                    } catch (SAXException ex) {
                        Logger.getLogger(CallCampaignInfoService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(CallCampaignInfoService.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParserConfigurationException ex) {
                        Logger.getLogger(CallCampaignInfoService.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private String getTagValue(String tag, org.w3c.dom.Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        if (node != null) {
            return node.getNodeValue();
        } else {
            return "";
        }
    }

    public void callBranchCampaignInfoForAllBranches() {
        List<BranchSetting> branchSettings = callCampaignInfoDao.findBranchSettingsForCampaignInfo();
        executeWebServiceInfo(branchSettings);
    }

    @Override
    public void executeWebServiceInfo(List<BranchSetting> branchSettings) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (BranchSetting branchSetting : branchSettings) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    callWebServiceInformation(branchSetting);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

    @Override
    public int updateParoInformation(BranchSetting obj, String pointOfSaleIntegrationList) {
        return callCampaignInfoDao.updateParoInformation(obj, pointOfSaleIntegrationList);
    }

}
