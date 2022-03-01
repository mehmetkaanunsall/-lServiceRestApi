/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:55:27 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.automat.washingmachicne.dao.IWashingMachicneDao;
import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import com.mepsan.marwiz.general.httpclient.business.HttpClientConnection;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import java.text.Normalizer;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class WashingMachicneService implements IWashingMachicneService {

    @Autowired
    IWashingMachicneDao washingMachicneDao;

    public void setWashingMachicneDao(IWashingMachicneDao washingMachicneDao) {
        this.washingMachicneDao = washingMachicneDao;
    }

    @Override
    public List<WashingMachicne> findAll(String where) {
        return washingMachicneDao.findAll(where);
    }

    @Override
    public int create(WashingMachicne obj) {
        return washingMachicneDao.create(obj);
    }

    @Override
    public int update(WashingMachicne obj) {
        return washingMachicneDao.update(obj);
    }

    @Override
    public int delete(WashingMachicne obj) {
        return washingMachicneDao.delete(obj);
    }

    @Override
    public int testBeforeDelete(WashingMachicne obj) {
        return washingMachicneDao.testBeforeDelete(obj);
    }

    public List<WashingMachicne> selectWashinMachine(String where) {
        return washingMachicneDao.selectWashinMachine(where);
    }

    @Override
    public boolean sendConfiguration(String command, WashingMachicne obj) {
        String sendConfiguration = washingMachicneDao.sendConfiguration(obj);
        JsonParser parser = new JsonParser();

        String jsonObj = Normalizer    //türkçe karakterler en formatına çevrildi.
                .normalize(sendConfiguration, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");

        System.out.println("---json----" + jsonObj);

        AESEncryptor aESEncryptor = new AESEncryptor();
        HttpClientConnection connection = new HttpClientConnection(obj.getIpAddress(), obj.getPort(), aESEncryptor.encrypt(jsonObj), 10);
        String resultMessage = connection.connect();
        String commandDecrypt = aESEncryptor.decrypt(resultMessage);
        System.out.println("Result=" + commandDecrypt);
        if (commandDecrypt != null) {
            if (commandDecrypt.isEmpty()) {
                return false;
            } else {
                JsonObject json = parser.parse(commandDecrypt).getAsJsonObject();
                int result = json.get("Result").getAsInt();
                if (result > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean configureDetail(WashingMachicne obj) {
        return sendConfiguration(washingMachicneDao.sendConfiguration(obj), obj);
    }

}
