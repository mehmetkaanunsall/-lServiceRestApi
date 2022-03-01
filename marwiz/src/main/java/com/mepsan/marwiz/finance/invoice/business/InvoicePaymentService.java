/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 25.06.2018 08:34:28
 */
package com.mepsan.marwiz.finance.invoice.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.invoice.dao.IInvoicePaymentDao;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoicePayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class InvoicePaymentService implements IInvoicePaymentService {

    @Autowired
    private IInvoicePaymentDao invoicePaymentDao;

    public void setInvoicePaymentDao(IInvoicePaymentDao invoicePaymentDao) {
        this.invoicePaymentDao = invoicePaymentDao;
    }

    @Override
    public int create(InvoicePayment obj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", obj.getId());
        jsonObject.addProperty("typeid", obj.getType().getId());
        jsonObject.addProperty("safeid", obj.getSafe().getId());
        jsonObject.addProperty("bankaccountid", obj.getBankAccount().getId());
        jsonObject.addProperty("price", obj.getPrice());
        jsonObject.addProperty("currencyid", obj.getCurrency().getId());
        jsonObject.addProperty("exchangerate", obj.getExchangeRate());
        jsonObject.addProperty("documentnumber", obj.getFinancingDocument().getDocumentNumber());
        jsonObject.addProperty("description", obj.getFinancingDocument().getDescription());

        switch (obj.getType().getId()) {
            case 19://veresiye ise detay ekle
                JsonArray jDetail = new JsonArray();
                JsonObject jo = new JsonObject();
                jo.addProperty("money", obj.getPrice());
                jo.addProperty("duedate", sdf.format(obj.getProcessDate()));
                jo.addProperty("is_invoice", "TRUE");
                jDetail.add(jo);
                jsonObject.add("creditdetail", jDetail);
                break;
            case 66://çek ise detay ekle

                JsonObject jsonCheque = new JsonObject();
                jsonCheque.addProperty("is_cheque", Boolean.TRUE);
                jsonCheque.addProperty("portfolionumber", obj.getChequeBill().getPortfolioNumber());
                jsonCheque.addProperty("expirydate", sdf.format(obj.getChequeBill().getExpiryDate()));
                jsonCheque.addProperty("bankbranch_id", obj.getChequeBill().getBankBranch().getId());
                jsonCheque.addProperty("documentnumber_id", obj.getChequeBill().getDocumentNumber().getId() == 0 ? null : obj.getChequeBill().getDocumentNumber().getId());
                jsonCheque.addProperty("documentserial", obj.getChequeBill().getDocumentSerial());
                jsonCheque.addProperty("documentnumber", obj.getInvoice().isIsPurchase() ? String.valueOf(obj.getChequeBill().getDocumentNumber().getActualNumber()) : obj.getFinancingDocument().getDocumentNumber());
                jsonCheque.addProperty("accountnumber", obj.getChequeBill().getAccountNumber());
                jsonCheque.addProperty("ibannumber", obj.getChequeBill().getIbanNumber());
                jsonCheque.addProperty("status_id", 31);
                jsonCheque.addProperty("paymentcity_id", obj.getChequeBill().getPaymentCity().getId());
                jsonCheque.addProperty("accountguarantor", obj.getChequeBill().getAccountGuarantor());

                jsonObject.add("chequebilldetail", jsonCheque);
                jsonObject.addProperty("creditdetail", "");
                break;
            case 69: //senet ise detay ekle
                JsonObject jsonBill = new JsonObject();
                jsonBill.addProperty("is_cheque", Boolean.FALSE);
                jsonBill.addProperty("portfolionumber", obj.getChequeBill().getPortfolioNumber());
                jsonBill.addProperty("expirydate", sdf.format(obj.getChequeBill().getExpiryDate()));
                jsonBill.addProperty("documentnumber_id", obj.getChequeBill().getDocumentNumber().getId() == 0 ? null : obj.getChequeBill().getDocumentNumber().getId());
                jsonBill.addProperty("documentserial", obj.getChequeBill().getDocumentSerial());
                jsonBill.addProperty("documentnumber", !obj.getInvoice().isIsPurchase() ? obj.getFinancingDocument().getDocumentNumber() : String.valueOf(obj.getChequeBill().getDocumentNumber().getActualNumber()));
                jsonBill.addProperty("status_id", 31);
                jsonBill.addProperty("paymentcity_id", obj.getChequeBill().getPaymentCity().getId());
                jsonBill.addProperty("bill_collocationdate", sdf.format(obj.getChequeBill().getBillCollocationDate()));
                jsonBill.addProperty("accountguarantor", obj.getChequeBill().getAccountGuarantor());

                jsonObject.add("chequebilldetail", jsonBill);
                jsonObject.addProperty("creditdetail", "");
                break;
            default:
                jsonObject.addProperty("chequedetail", "");
                jsonObject.addProperty("creditdetail", "");
                break;
        }

        jsonArray.add(jsonObject);

        return invoicePaymentDao.create(obj, jsonArray.toString());
    }

    @Override
    public List<InvoicePayment> listOfPayments(Invoice invoice) {
        return invoicePaymentDao.listOfPayments(invoice);
    }

    @Override
    public int delete(InvoicePayment invoicePayment) {
        return invoicePaymentDao.delete(invoicePayment);
    }

    @Override
    public List<CheckDelete> testBeforeDelete(InvoicePayment invoicePayment) {
        return invoicePaymentDao.testBeforeDelete(invoicePayment);
    }

    @Override
    public int update(InvoicePayment obj) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", obj.getId());
        jsonObject.addProperty("typeid", obj.getType().getId());
        jsonObject.addProperty("safeid", obj.getSafe().getId());
        jsonObject.addProperty("bankaccountid", obj.getBankAccount().getId());
        jsonObject.addProperty("price", obj.getPrice());
        jsonObject.addProperty("currencyid", obj.getCurrency().getId());
        jsonObject.addProperty("exchangerate", obj.getExchangeRate());
        jsonObject.addProperty("documentnumber", obj.getFinancingDocument().getDocumentNumber());
        jsonObject.addProperty("description", obj.getFinancingDocument().getDescription());
        jsonObject.addProperty("chequedetail", "");
        jsonObject.addProperty("creditdetail", "");

        jsonArray.add(jsonObject);

        return invoicePaymentDao.update(obj, jsonArray.toString());
    }

}
