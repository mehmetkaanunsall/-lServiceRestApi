/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.08.2020 02:42:26
 */
package com.mepsan.marwiz.general.printer.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.Printer;
import com.mepsan.marwiz.general.printer.dao.IPrinterDao;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class PrinterService implements IPrinterService {

    @Autowired
    private IPrinterDao printerDao;

    @Autowired
    private SessionBean sessionBean;

    public void setPrinterDao(IPrinterDao printerDao) {
        this.printerDao = printerDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public Printer listOfPrinterAccordingToType(int type, Branch branch) {
        return printerDao.listOfPrinterAccordingToType(type,branch);
    }

    @Override
    public List<Printer> listOfPrinter() {
        return printerDao.listOfPrinter();
    }

    @Override
    public int delete(Printer obj) {
        return printerDao.delete(obj);
    }

    @Override
    public int create(Printer obj) {
        return printerDao.create(obj);
    }

    @Override
    public int update(Printer obj) {
        return printerDao.update(obj);
    }

    @Override
    public String sendPrinterDevice(String json, Printer printer, int type) {
        String response = null;
        WebServiceClient webServiceClient = new WebServiceClient();
        if (type == 0) {//ETİKET
            System.out.println("---etiket" + "http://" + printer.getIpAddress() + ":" + printer.getPort() + "/printLabel");
            response = webServiceClient.requestJson("http://" + printer.getIpAddress() + ":" + printer.getPort() + "/printLabel", null, null, json);
        } else if (type == 1) {//FATURA
            System.out.println("----fatura" + "http://" + printer.getIpAddress() + ":" + printer.getPort() + "/printInvoice");
            response = webServiceClient.requestJson("http://" + printer.getIpAddress() + ":" + printer.getPort() + "/printInvoice", null, null, json);

        }
        JSONObject resJson = new JSONObject(response);

        String returnResponse = "";
        switch (resJson.getInt("errorcode")) {
            case 0:
                returnResponse = sessionBean.getLoc().getString("succesfuloperation");
                break;
            case 101:
                returnResponse = sessionBean.getLoc().getString("printerconnectionerror");
                break;
            case 102:
                returnResponse = sessionBean.getLoc().getString("printererror");
                break;
            case 103:
                returnResponse = sessionBean.getLoc().getString("coverofprinterisopen");
                break;
            case 104:
                returnResponse = sessionBean.getLoc().getString("jsoninvalid");
                break;
            case 105:
                returnResponse = sessionBean.getLoc().getString("undefinederror");
                break;
            default:
                returnResponse = sessionBean.getLoc().getString("error");
                break;
        }
        return returnResponse;
    }

}
