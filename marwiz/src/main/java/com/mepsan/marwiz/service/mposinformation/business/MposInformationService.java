/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.mposinformation.business;

import com.mepsan.marwiz.general.login.dao.ILoginDao;
import com.mepsan.marwiz.service.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author emrullah.yakisan
 */
public class MposInformationService implements IMposInformationService {

   

   

    @Override
    public String sendInformationLog(String jsonData, String username, String password, String licenceCode, String wsEndPoint) {

        String data
                = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <S:Body>\n"
                + "        <ns2:GetMPOSInfo xmlns:ns2=\"http://ws/\">\n"
                + "            <station>" + licenceCode + "</station>\n"
                + "            <info><![CDATA[" + jsonData + "]]></info>\n"
                + "        </ns2:GetMPOSInfo>\n"
                + "    </S:Body>\n"
                + "</S:Envelope>";
        return new WebServiceClient().request(wsEndPoint + "/WsIncome?xsd=1", username, password, data);
    }
}
