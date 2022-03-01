/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 11:33:43
 */
package com.mepsan.marwiz.inventory.automationdevice.business;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import com.mepsan.marwiz.general.httpclient.business.HttpClientConnection;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.inventory.automationdevice.dao.IAutomationDeviceDao;
import java.awt.BorderLayout;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomationDeviceService implements IAutomationDeviceService {

    @Autowired
    private IAutomationDeviceDao automationDeviceDao;

    public void setAutomationDeviceDao(IAutomationDeviceDao automationDeviceDao) {
        this.automationDeviceDao = automationDeviceDao;
    }

    @Override
    public List<AutomationDevice> findAll(String where) {
        return automationDeviceDao.findAll(where);
    }

    @Override
    public int create(AutomationDevice obj) {
        return automationDeviceDao.create(obj);
    }

    @Override
    public int update(AutomationDevice obj) {
        return automationDeviceDao.update(obj);
    }

    @Override
    public int delete(AutomationDevice automationDevice) {
        return automationDeviceDao.delete(automationDevice);
    }

    @Override
    public boolean configureDetail(AutomationDevice automationDevice) {
        return sendConfigure(automationDeviceDao.configureDetail(automationDevice), automationDevice);
    }

    public boolean sendConfigure(String command, AutomationDevice automationDevice) {
        AESEncryptor aESEncryptor = new AESEncryptor();

        System.out.println("---json----" + command);
        HttpClientConnection connection = new HttpClientConnection(automationDevice.getIpadress(), String.valueOf(automationDevice.getPort()), aESEncryptor.encrypt(command), 10);
        JsonParser parser = new JsonParser();
        String resultMessage = connection.connect();

        System.out.println("****" + resultMessage);
        String commandDecrypt = aESEncryptor.decrypt(resultMessage);
        System.out.println("Result=" + commandDecrypt);
        if (commandDecrypt != null) {
            JsonObject json = parser.parse(commandDecrypt).getAsJsonObject();
            int result = json.get("Result").getAsInt();
            if (result > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean sendCommand(String command, AutomationDevice obj) {
        AESEncryptor aESEncryptor = new AESEncryptor();
        System.out.println("command=" + command);
        HttpClientConnection connection = new HttpClientConnection(obj.getIpadress(), String.valueOf(obj.getPort()), aESEncryptor.encrypt(command), 10);
        JsonParser parser = new JsonParser();
        String resultMessage = connection.connect();
        String commandDecrypt = aESEncryptor.decrypt(resultMessage);
        System.out.println("Result=" + commandDecrypt);
        if (commandDecrypt != null) {
            JsonObject json = parser.parse(commandDecrypt).getAsJsonObject();
            int result = json.get("Result").getAsInt();
            if (result > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int controlAutomationDevice() {
        return automationDeviceDao.controlAutomationDevice();
    }

}
