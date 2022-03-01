/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 25.06.2018 08:11:50
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.general.common.CheckDeleteMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoicePayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class InvoicePaymentDao extends JdbcDaoSupport implements IInvoicePaymentDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(InvoicePayment obj, String json) {
        String sql = "SELECT r_return_id FROM finance.process_payment(?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?);";

        boolean isDirection = false;
        if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && obj.getInvoice().isIsDifferenceDirection()) { // Satınalma, Fiyat Farkı ve Fiyat Arttı İse
            isDirection = false;// Satınalma, Fiyat Farkı ve Fiyat Arttı İse
        } else if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && !obj.getInvoice().isIsDifferenceDirection()) { // Satınalma, Fiyat Farkı ve Fiyat Azaldı İse
            isDirection = true;// Satınalma, Fiyat Farkı ve Fiyat Azaldı İse
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && obj.getInvoice().isIsDifferenceDirection()) { // Satış, Fiyat Farkı ve Fiyat Arttı İse
            isDirection = true;// Satış, Fiyat Farkı ve Fiyat Arttı İse
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && !obj.getInvoice().isIsDifferenceDirection()) { // Satış, Fiyat Farkı ve Fiyat Azaldı İse
            isDirection = false;// Satış, Fiyat Farkı ve Fiyat Azaldı İse
        } else if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() != 27) { // Satınalma VE Normal Fatura İse
            isDirection = false;
        } else if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 27) { // Satınalma VE İade Fatura İse
            isDirection = true;
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() != 27) { // Satış VE Normal Fatura İse
            isDirection = true;
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 27) { // Satış VE İade Fatura İse
            isDirection = false;
        }

        Object[] param = new Object[]{
            0,
            null,
            obj.getInvoice().getSaleId(),
            obj.getInvoice().getId(),
            obj.getInvoice().isIsPurchase(),
            obj.getInvoice().getAccount().getId(),
            obj.getProcessDate(),
            isDirection,
            json,
            sessionBean.getUser().getId(),
            obj.getInvoice().getBranchSetting().getBranch().getId()};

        System.out.println("---***-" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(InvoicePayment obj, String json) {
        String sql = "SELECT r_return_id FROM finance.process_payment(?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?);";

        boolean isDirection = false;
        if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && obj.getInvoice().isIsDifferenceDirection()) { // Satınalma, Fiyat Farkı ve Fiyat Arttı İse
            isDirection = false;// Satınalma, Fiyat Farkı ve Fiyat Arttı İse
        } else if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && !obj.getInvoice().isIsDifferenceDirection()) { // Satınalma, Fiyat Farkı ve Fiyat Azaldı İse
            isDirection = true;// Satınalma, Fiyat Farkı ve Fiyat Azaldı İse
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && obj.getInvoice().isIsDifferenceDirection()) { // Satış, Fiyat Farkı ve Fiyat Arttı İse
            isDirection = true;// Satış, Fiyat Farkı ve Fiyat Arttı İse
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 26 && !obj.getInvoice().isIsDifferenceDirection()) { // Satış, Fiyat Farkı ve Fiyat Azaldı İse
            isDirection = false;// Satış, Fiyat Farkı ve Fiyat Azaldı İse
        } else if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() != 27) { // Satınalma VE Normal Fatura İse
            isDirection = false;
        } else if (obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 27) { // Satınalma VE İade Fatura İse
            isDirection = true;
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() != 27) { // Satış VE Normal Fatura İse
            isDirection = true;
        } else if (!obj.getInvoice().isIsPurchase() && obj.getInvoice().getType().getId() == 27) { // Satış VE İade Fatura İse
            isDirection = false;
        }

        Object[] param = new Object[]{
            1,
            obj.getFinancingDocument().getId(),
            obj.getInvoice().getSaleId(),
            obj.getInvoice().getId(),
            obj.getInvoice().isIsPurchase(),
            obj.getInvoice().getAccount().getId(),
            obj.getProcessDate(),
            isDirection,
            json,
            sessionBean.getUser().getId(),
            obj.getInvoice().getBranchSetting().getBranch().getId()};

        System.out.println("---***-" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<InvoicePayment> listOfPayments(Invoice invoice) {
        String sql = "SELECT\n"
                  + "    inp.id as inpid,\n"
                  + "    fdoc.id as fdocid,\n"
                  + "    COALESCE(fdoc.documentnumber,'') as fdocdocumentnumber,\n"
                  + "    fdoc.documentdate as fdocdocumentdate,\n"
                  + "    crd.id as crdid,\n"
                  + "    crd.duedate as crdduedate,\n"
                  + "    inp.price as inpprice,\n"
                  + "    inp.currency_id as inpcurrency_id,\n"
                  + "    crr.code as crrcode,\n"
                  + "    inp.exchangerate as inpexchangerate,\n"
                  + "    inp.type_id as inptype_id,\n"
                  + "    typd.name as typdname,\n"
                  + "    inp.is_direction as inpis_direction,\n"
                  + "    bam.bankaccount_id as bambankaccount_id,\n"
                  + "    sm.safe_id as smsafe_id,\n"
                  + "    chq.id as chqid,\n"
                  + "    chq.portfolionumber as chqportfolionumber,\n"
                  + "    chq.documentnumber as chqdocumentnumber,\n"
                  + "    chq.documentnumber_id as chqdocumentnumber_id,\n"
                  + "    chq.documentserial as chqdocumentserial,\n"
                  + "    chq.bankbranch_id as chqbankbranch_id,\n"
                  + "    chq.accountnumber as chqaccountnumber,\n"
                  + "    chq.ibannumber as chqibannumber,\n"
                  + "    chq.expirydate as chqexpirydate,\n"
                  + "    chq.status_id as chqstatus_id,\n"
                  + "    chq.paymentcity_id as chqpaymentcity_id,\n"
                  + "    chq.bill_collocationdate as chqbill_collocationdate,\n"
                  + "    chq.accountguarantor as chqaccountguarantor,\n"
                  + "    cty.country_id as ctycountry_id\n "
                  + "FROM finance.invoicepayment inp\n"
                  + "LEFT JOIN finance.financingdocument fdoc ON (fdoc.id = inp.financingdocument_id AND fdoc.deleted = FALSE)\n"
                  + "LEFT JOIN finance.bankaccountmovement bam ON (fdoc.id = bam.financingdocument_id AND bam.deleted = FALSE)\n"
                  + "LEFT JOIN finance.safemovement sm ON (fdoc.id = sm.financingdocument_id AND sm.deleted = FALSE)\n"
                  + "LEFT JOIN finance.credit crd ON (crd.id = inp.credit_id AND crd.deleted = FALSE)\n"
                  + "LEFT JOIN finance.chequebill chq ON (chq.id = inp.chequebill_id AND chq.deleted = FALSE)\n"
                  + "LEFT JOIN system.city cty ON (cty.id = chq.paymentcity_id)\n"
                  + "INNER JOIN system.currency crr ON (crr.id = inp.currency_id)\n"
                  + "INNER JOIN system.type_dict typd ON (typd.type_id = inp.type_id AND typd.language_id = ?)\n"
                  + "WHERE inp.deleted = FALSE "
                  + "AND inp.invoice_id = ?\n"
                  + "ORDER BY inp.id ";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), invoice.getId()};

        List<InvoicePayment> result = getJdbcTemplate().query(sql, param, new InvoicePaymentMapper());
        return result;
    }

    @Override
    public List<CheckDelete> testBeforeDelete(InvoicePayment invoicePayment) {
        String sql = "SELECT r_response, r_recordno, r_record_id FROM general.check_connection(?,?);";

        Object[] param = {7, invoicePayment.getId()};
        List<CheckDelete> result = getJdbcTemplate().query(sql, param, new CheckDeleteMapper());
        return result;
    }

    @Override
    public int delete(InvoicePayment invoicePayment) {
        String sql = "SELECT r_payment_id FROM finance.delete_payment_financingdocument (?, ?, ?);";

        Object[] param = new Object[]{4, invoicePayment.getId(), sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

}
