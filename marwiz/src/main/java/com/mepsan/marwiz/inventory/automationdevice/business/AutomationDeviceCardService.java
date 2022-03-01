/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:58:08 PM
 */
package com.mepsan.marwiz.inventory.automationdevice.business;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import com.mepsan.marwiz.general.httpclient.business.HttpClientConnection;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceCard;
import com.mepsan.marwiz.inventory.automationdevice.dao.IAutomationDeviceCardDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomationDeviceCardService implements IAutomationDeviceCardService {

    @Autowired
    IAutomationDeviceCardDao automationDeviceCardDao;

    public void setAutomationDeviceCardDao(IAutomationDeviceCardDao automationDeviceCardDao) {
        this.automationDeviceCardDao = automationDeviceCardDao;
    }

    @Override
    public List<AutomationDeviceCard> listOfCard(AutomationDevice obj) {
        return automationDeviceCardDao.listOfCard(obj);
    }

    @Override
    public int delete(AutomationDeviceCard automationDeviceCard) {
        return automationDeviceCardDao.delete(automationDeviceCard);
    }

    @Override
    public int create(AutomationDeviceCard obj) {
        return automationDeviceCardDao.create(obj);
    }

    @Override
    public int update(AutomationDeviceCard obj) {
        return automationDeviceCardDao.update(obj);
    }

    @Override
    public AutomationDeviceCard sendCommand(String command, AutomationDevice obj) {
        AutomationDeviceCard card = new AutomationDeviceCard();
        AESEncryptor aESEncryptor = new AESEncryptor();
        System.out.println("command=" + command);
        String s=aESEncryptor.encrypt(command);
         
        HttpClientConnection connection = new HttpClientConnection(obj.getIpadress(), String.valueOf(obj.getPort()), s, 10);
        JsonParser parser = new JsonParser();
        String resultMessage = connection.connect();
        String commandDecrypt = aESEncryptor.decrypt(resultMessage);
        System.out.println("Result=" + commandDecrypt);
        if (commandDecrypt != null) {
            JsonObject json = parser.parse(commandDecrypt).getAsJsonObject();

            card.setResultId(json.get("Result").getAsInt());
            card.getType().setId(json.get("Type").getAsInt());
            card.setRfNo(json.get("CardNo").getAsString());

            return card;
        } else {
            return card;
        }
    }

}
