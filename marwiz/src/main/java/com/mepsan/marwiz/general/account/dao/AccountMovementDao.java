/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   29.01.2018 01:25:38
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountMovementDao extends JdbcDaoSupport implements IAccountMovementDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<AccountMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId) {
        String sql = "";
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        Object[] params;
        String whereTermDate = "";
        String whereType = "";

        if (financingTypeId > 0) {
            whereType = " AND fdoc.type_id = " + financingTypeId + " \n";
        }

        if (sortField == null) {
            sortField = "accm.movementdate";
            sortOrder = " DESC ";
        } else if (sortField.equals("financingDocument.documentDate")) {
            sortField = " (CASE WHEN fdoc.id > 0 THEN fdoc.documentdate\n"
                      + "	WHEN inv.id > 0 THEN inv.invoicedate\n"
                      + "       WHEN rcp.id > 0 THEN rcp.processdate"
                      + "       ELSE accm.movementdate END) ";

        } else if (sortField.equals("financingDocument.documentNumber")) {
            sortField = " (CASE WHEN fdoc.id > 0 THEN fdoc.documentnumber\n"
                      + "	WHEN inv.id > 0 THEN inv.documentnumber\n"
                      + "       WHEN cqb.id > 0 THEN cqb.portfolionumber\n"
                      + "       WHEN rcp.id > 0 THEN rcp.receiptno ELSE null END) ";

        } else if (sortField.equals("dateCreated")) {
            sortField = " (CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                      + "       WHEN inv.id>0 THEN inv.c_time\n"
                      + "	WHEN cqb.id>0 THEN cqb.c_time\n"
                      + "       WHEN rcp.id>0 THEN rcp.c_time END) ";

        } else if (sortField.equals("userCreated.fullName")) {
            sortField = " concat(usr.name,usr.surname) ";

        } else if (sortField.equals("dateUpdated")) {
            sortField = " (CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                      + "       WHEN inv.id>0 THEN inv.u_time\n"
                      + "	WHEN cqb.id>0 THEN cqb.u_time\n"
                      + "       WHEN rcp.id>0 THEN rcp.u_time END) ";

        } else if (sortField.equals("userUpdated.fullName")) {
            sortField = " concat(usr1.name,usr1.surname) ";

        } else if (sortField.equals("financingDocument.financingType.tag")) {
            sortField = " (CASE WHEN fdoc.id>0 THEN fdoc.type_id\n"
                      + "       WHEN inv.id>0 THEN (CASE WHEN inv.is_purchase THEN 0 ELSE 1 END)\n"
                      + "	WHEN cqb.id>0 THEN cqb.id\n"
                      + "       WHEN rcp.id>0 THEN rcp.id END) ";
        } else if (sortField.equals("isDirection")) {//Borç
            sortField = " (CASE WHEN accm.is_direction  = FALSE THEN accm.price * accm.exchangerate ELSE NULL END) ";
        } else if (sortField.equals("exchangeRate")) {//Alacak
            sortField = " (CASE WHEN accm.is_direction  = TRUE THEN accm.price * accm.exchangerate ELSE NULL END) ";
        } else if (sortField.equals("invoice.dueDate")) {
            sortField = "inv.duedate";

        }

        if (termDate != null) {

            switch (termDateOpType) {
                case 1:
                    whereTermDate = "AND inv.duedate < '" + format.format(termDate) + "'";
                    break;
                case 2:
                    whereTermDate = "AND inv.duedate = '" + format.format(termDate) + "'";
                    break;
                case 3:
                    whereTermDate = "AND inv.duedate > '" + format.format(termDate) + "'";
                    break;
                default:
                    whereTermDate = "";
                    break;
            }

        }

        switch (opType) {
            case 1:    //sadece gelen
                sql = "SELECT\n"
                          + "fdoc.id as fdocid,  \n"
                          + "fdoc.documentnumber as fdocdocumentnumber,  \n"
                          + "fdoc.description as fdocdescription,   \n"
                          + "fdoc.type_id as fdoctype_id,\n"
                          + "fdoc.documentdate as fdocdocumentdate,\n"
                          + "typd.name AS typdname,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                          + "      WHEN inv.id>0 THEN inv.c_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_time END) AS fdocc_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                          + "      WHEN inv.id>0 THEN inv.u_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_time END) AS fdocu_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "      WHEN inv.id>0 THEN inv.c_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_id END) AS fdocc_id,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "      WHEN inv.id>0 THEN inv.u_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_id END) AS fdocu_id,\n"
                          + "usr.name as usrname,\n"
                          + "usr.surname as usrsurname,\n"
                          + "usr1.name as usr1name,\n"
                          + "usr1.surname as usr1surname,\n"
                          + "inv.id as invid,\n"
                          + "inv.documentnumber as invdocumentnumber,\n"
                          + "inv.invoicedate as invinvoicedate,\n"
                          + "inv.is_purchase AS invis_purchase,\n"
                          + "accm.id as accmid,\n"
                          + "accm.price as accmprice, \n"
                          + "accm.is_direction as accmis_direction,  \n"
                          + "COALESCE(accm.exchangerate,1) as accmexchangerate,  \n"
                          + "accm.movementdate as accmmovementdate,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "accm.chequebill_id AS accmchequebill_id,\n"
                          + "cqb.portfolionumber AS cqbportfolionumber,\n"
                          + "accm.receipt_id AS accmreceipt_id,\n"
                          + "rcp.receiptno AS rcpreceiptno,\n"
                          + "rcp.processdate AS rcpprocessdate,\n"
                          + "crr.code as crrcode,\n"
                          + "crr.sign as crrsign, \n"
                          + "(SELECT COALESCE (SUM (S.price), 0)  FROM (  \n"
                          + "SELECT (accm2.price*COALESCE(accm2.exchangerate,1))  as price\n"
                          + "FROM  general.accountmovement AS accm2 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm2.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm2.account_id = acc.id  AND accm2.deleted=false AND accm2.is_direction=true AND accm2.branch_id = accm.branch_id \n"
                          + whereType + "\n"
                          + "AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "ORDER BY accm2.movementdate DESC, accm2.id DESC) S \n"
                          + ")+SUM(accm.price*COALESCE(accm.exchangerate,1)) \n"
                          + "OVER(ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 0 ELSE 1 END,\n"
                          + sortField + " ,accm.id \n"
                          + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS accbalance,\n"
                          + "shp.shift_id AS shpshift_id,\n"
                          + "stf.stocktaking_id AS stfstocktaking_id, \n"
                          + "inv.duedate AS invduedate, \n"
                          + "br.name AS brname\n"
                          + "FROM    \n"
                          + "general.accountmovement accm  \n"
                          + "INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False) \n"
                          + "LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE)\n"
                          + "LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                          + "LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + "LEFT JOIN general.branch br ON(br.id = accm.branch_id AND br.deleted=FALSE)\n"
                          + "WHERE  accm.is_direction=true AND accm.deleted=false AND accm.branch_id IN( " + branchList + ")\n"
                          + "AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + "AND accm.account_id=? \n"
                          + where + whereTermDate + "\n"
                          + whereType + "\n"
                          + "ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 1 ELSE 0 END,\n"
                          + sortField + " " + sortOrder + ", accm.id " + sortOrder + " \n"
                          + " limit " + pageSize + " offset " + first;
                params = new Object[]{sessionBean.getUser().getLanguage().getId(), account.getId()};
                break;
            case 2:
                sql = "SELECT\n"
                          + "fdoc.id as fdocid,  \n"
                          + "fdoc.documentnumber as fdocdocumentnumber,  \n"
                          + "fdoc.description as fdocdescription,   \n"
                          + "fdoc.type_id as fdoctype_id,\n"
                          + "fdoc.documentdate as fdocdocumentdate,\n"
                          + "typd.name AS typdname,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                          + "      WHEN inv.id>0 THEN inv.c_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_time END) AS fdocc_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                          + "      WHEN inv.id>0 THEN inv.u_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_time END) AS fdocu_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "      WHEN inv.id>0 THEN inv.c_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_id END) AS fdocc_id,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "      WHEN inv.id>0 THEN inv.u_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_id END) AS fdocu_id,\n"
                          + "usr.name as usrname,\n"
                          + "usr.surname as usrsurname,\n"
                          + "usr1.name as usr1name,\n"
                          + "usr1.surname as usr1surname,\n"
                          + "inv.id as invid,\n"
                          + "inv.documentnumber as invdocumentnumber,\n"
                          + "inv.invoicedate as invinvoicedate,\n"
                          + "inv.is_purchase AS invis_purchase,\n"
                          + "accm.id as accmid,\n"
                          + "accm.price as accmprice, \n"
                          + "accm.is_direction as accmis_direction,  \n"
                          + "COALESCE(accm.exchangerate,1) as accmexchangerate,  \n"
                          + "accm.movementdate as accmmovementdate,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "accm.chequebill_id AS accmchequebill_id,\n"
                          + "cqb.portfolionumber AS cqbportfolionumber,\n"
                          + "accm.receipt_id AS accmreceipt_id,\n"
                          + "rcp.receiptno AS rcpreceiptno,\n"
                          + "rcp.processdate AS rcpprocessdate,\n"
                          + "crr.code as crrcode,\n"
                          + "crr.sign as crrsign, \n"
                          + "(SELECT COALESCE (SUM (S.price), 0)  FROM (  \n"
                          + "SELECT -(accm2.price*COALESCE(accm2.exchangerate,1)) as price\n"
                          + "FROM  general.accountmovement AS accm2 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm2.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm2.account_id = acc.id AND accm2.is_direction=false AND accm2.deleted=false AND accm2.branch_id = accm.branch_id \n"
                          + whereType + "\n"
                          + "AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "ORDER BY accm2.movementdate DESC, accm2.id DESC) S \n"
                          + ")+SUM(-(accm.price*COALESCE(accm.exchangerate,1))) \n"
                          + "OVER(ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 0 ELSE 1 END,\n"
                          + "" + sortField + " ,accm.id \n"
                          + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS accbalance,\n"
                          + "shp.shift_id AS shpshift_id,\n"
                          + "stf.stocktaking_id AS stfstocktaking_id, \n"
                          + "inv.duedate AS invduedate, \n"
                          + "br.name AS brname\n"
                          + "FROM    \n"
                          + "general.accountmovement accm  \n"
                          + "INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE) \n"
                          + "LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                          + "LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + "LEFT JOIN general.branch br ON(br.id = accm.branch_id AND br.deleted=FALSE)\n"
                          + "WHERE  accm.is_direction=false AND accm.deleted=false AND accm.branch_id IN( " + branchList + " )\n"
                          + "AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + "AND accm.account_id=?\n"
                          + where + whereTermDate + "\n"
                          + whereType + "\n"
                          + "ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 1 ELSE 0 END,\n"
                          + sortField + " " + sortOrder + ", accm.id " + sortOrder + " \n"
                          + " limit " + pageSize + " offset " + first;
                params = new Object[]{sessionBean.getUser().getLanguage().getId(), account.getId()};
                break;
            default:
                sql = "SELECT \n"
                          + "fdoc.id as fdocid, \n"
                          + "fdoc.documentnumber as fdocdocumentnumber,  \n"
                          + "fdoc.description as fdocdescription,   \n"
                          + "fdoc.type_id as fdoctype_id,\n"
                          + "fdoc.documentdate as fdocdocumentdate,\n"
                          + "typd.name AS typdname,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                          + "      WHEN inv.id>0 THEN inv.c_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_time END) AS fdocc_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                          + "      WHEN inv.id>0 THEN inv.u_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_time END) AS fdocu_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "      WHEN inv.id>0 THEN inv.c_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_id END) AS fdocc_id,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "      WHEN inv.id>0 THEN inv.u_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_id END) AS fdocu_id,\n"
                          + "usr.name as usrname,\n"
                          + "usr.surname as usrsurname,\n"
                          + "usr1.name as usr1name,\n"
                          + "usr1.surname as usr1surname,\n"
                          + "inv.id as invid,\n"
                          + "inv.documentnumber as invdocumentnumber,\n"
                          + "inv.invoicedate as invinvoicedate,\n"
                          + "inv.is_purchase AS invis_purchase,\n"
                          + "accm.id as accmid,\n"
                          + "accm.price as accmprice, \n"
                          + "accm.is_direction as accmis_direction,  \n"
                          + "COALESCE(accm.exchangerate,1) as accmexchangerate,  \n"
                          + "accm.movementdate as accmmovementdate,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "accm.chequebill_id AS accmchequebill_id,\n"
                          + "cqb.portfolionumber AS cqbportfolionumber,\n"
                          + "accm.receipt_id AS accmreceipt_id,\n"
                          + "rcp.receiptno AS rcpreceiptno,\n"
                          + "rcp.processdate AS rcpprocessdate,\n"
                          + "crr.code as crrcode,\n"
                          + "crr.sign as crrsign, \n"
                          + "(SELECT COALESCE (SUM (S.price), 0)  FROM (  \n"
                          + "SELECT CASE accm2.is_direction WHEN true THEN (accm2.price*COALESCE(accm2.exchangerate,1))  \n"
                          + "ELSE -(accm2.price*COALESCE(accm2.exchangerate,1))  END   as price\n"
                          + "FROM  general.accountmovement AS accm2 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm2.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm2.account_id = acc.id  AND accm2.deleted=false AND accm2.branch_id = accm.branch_id \n"
                          + whereType + "\n"
                          + "AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "ORDER BY accm2.movementdate DESC, accm2.id DESC) S \n"
                          + ")+SUM(CASE WHEN accm.is_direction=true THEN  (accm.price*COALESCE(accm.exchangerate,1))\n"
                          + "ELSE -(accm.price*COALESCE(accm.exchangerate,1)) END ) \n"
                          + "OVER(ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 0 ELSE 1 END,\n"
                          + sortField + " ,accm.id \n"
                          + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS accbalance,\n"
                          + "shp.shift_id AS shpshift_id,\n"
                          + "stf.stocktaking_id AS stfstocktaking_id, \n"
                          + "inv.duedate AS invduedate, \n"
                          + "br.name AS brname\n"
                          + "FROM    \n"
                          + "general.accountmovement accm  \n"
                          + "INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE)\n"
                          + "LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                          + "LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + "LEFT JOIN general.branch br ON(br.id = accm.branch_id AND br.deleted=FALSE)\n"
                          + "WHERE    \n"
                          + "accm.account_id=? AND accm.deleted=false AND accm.branch_id IN( " + branchList + " ) AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + where + whereTermDate + "\n"
                          + whereType + "\n"
                          + "ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 1 ELSE 0 END,\n"
                          + sortField + " " + sortOrder + ", accm.id " + sortOrder + " \n"
                          + " limit " + pageSize + " offset " + first;
                
                params = new Object[]{sessionBean.getUser().getLanguage().getId(), account.getId()};
                break;
        }
        return getJdbcTemplate().query(sql, params, new AccountMovementMapper());
    }

    @Override
    public AccountMovement count(String where, Account account, int opType, Date beginDate, Date endDate, Date termDate, int termDateOpType, String branchList, int financingTypeId) {
        String sql;
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        Object[] params;
        String whereTermDate = "";

        String whereType = "";

        if (financingTypeId > 0) {
            whereType = " AND fdoc.type_id = " + financingTypeId + " \n";
        }

        if (termDate != null) {

            switch (termDateOpType) {
                case 1:
                    whereTermDate = "AND inv.duedate < '" + format.format(termDate) + "'";
                    break;
                case 2:
                    whereTermDate = "AND inv.duedate = '" + format.format(termDate) + "'";
                    break;
                case 3:
                    whereTermDate = "AND inv.duedate > '" + format.format(termDate) + "'";
                    break;
                default:
                    whereTermDate = "";
                    break;
            }

        }

        switch (opType) {
            case 1:
                sql = " SELECT \n"
                          + " COUNT(accm.id) AS accmid,\n"
                          + " (SELECT \n"
                          + "    COALESCE (SUM (S.price), 0) \n"
                          + "    FROM (  \n"
                          + "          SELECT (accm2.price*COALESCE(accm2.exchangerate,1))  as price\n"
                          + "          FROM  general.accountmovement AS accm2 \n"
                          + "          WHERE accm2.account_id = ?  AND accm2.deleted=false AND accm2.is_direction=true AND accm2.branch_id IN( " + branchList + " )\n"
                          + "          AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "          ORDER BY accm2.movementdate DESC, accm2.id DESC \n"
                          + "     ) S \n"
                          + " ) AS transferringbalance, \n"
                          + " (SELECT \n"
                          + "     COALESCE(SUM(accm1.price*COALESCE(accm1.exchangerate,1)),0)\n"
                          + "     FROM\n"
                          + "     general.accountmovement accm1 \n"
                          + "      LEFT JOIN finance.invoice inv ON (inv.id = accm1.invoice_id AND inv.deleted = False )\n"
                          + "      LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm1.financingdocument_id AND fdoc.deleted=False)\n"
                          + "     WHERE accm1.is_direction=true AND accm1.deleted=false AND accm1.branch_id IN( " + branchList + " )\n"
                          + whereType + "\n"
                          + "     AND accm1.account_id=? AND \n"
                          + "     accm1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' " + whereTermDate + "\n"
                          + " ) AS sumincoming,\n"
                          + " 0 AS sumoutcoming\n"
                          + " FROM \n"
                          + "    general.accountmovement accm \n"
                          + "    INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "    LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "    LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "    LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "    LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE) \n"
                          + "    LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "    LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "    LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "    LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "    LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                          + "    LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "    LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + " WHERE \n"
                          + "	accm.account_id=? AND accm.is_direction=true AND accm.deleted=false AND accm.branch_id IN( " + branchList + ")\n"
                          + whereType + "\n"
                          + " AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'" + where + whereTermDate; //where
                params = new Object[]{account.getId(), account.getId(), sessionBean.getUser().getLanguage().getId(), account.getId()};
                break;
            case 2:
                sql = "SELECT \n"
                          + " COUNT(accm.id) AS accmid,\n"
                          + " (SELECT \n"
                          + "    COALESCE (SUM (S.price), 0) \n"
                          + "    FROM (  \n"
                          + "          SELECT (-accm2.price*COALESCE(accm2.exchangerate,1))  as price\n"
                          + "          FROM  general.accountmovement AS accm2 \n"
                          + "          WHERE accm2.account_id = ?  AND accm2.deleted=false AND accm2.is_direction=false AND accm2.branch_id IN( " + branchList + " )\n"
                          + "          AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "          ORDER BY accm2.movementdate DESC, accm2.id DESC \n"
                          + "     ) S \n"
                          + " ) AS transferringbalance, \n"
                          + " 0 AS sumincoming,\n"
                          + " (SELECT \n"
                          + "     COALESCE(SUM(accm1.price*COALESCE(accm1.exchangerate,1)),0)\n"
                          + "     FROM\n"
                          + "     general.accountmovement accm1 \n"
                          + "      LEFT JOIN finance.invoice inv ON (inv.id = accm1.invoice_id AND inv.deleted = False )\n"
                          + "      LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm1.financingdocument_id AND fdoc.deleted=False)\n"
                          + "     WHERE accm1.is_direction=false AND accm1.deleted=false AND accm1.branch_id IN( " + branchList + " ) \n"
                          + whereType + "\n"
                          + "     AND accm1.account_id=? AND \n"
                          + "     accm1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' " + whereTermDate + "\n"
                          + " ) AS sumoutcoming\n"
                          + " FROM \n"
                          + "    general.accountmovement accm \n"
                          + "    INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "    LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "    LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "    LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "    LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "    LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE) \n"
                          + "    LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "    LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "    LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "    LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                          + "    LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "    LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + " WHERE \n"
                          + "	accm.account_id=? AND accm.is_direction=false AND accm.deleted=false AND accm.branch_id IN( " + branchList + " )\n"
                          + whereType + "\n"
                          + " AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'" + where + whereTermDate; //where
                params = new Object[]{account.getId(), account.getId(), sessionBean.getUser().getLanguage().getId(), account.getId()};
                break;
            default:
                sql = "SELECT \n"
                          + " COUNT(accm.id) AS accmid,\n"
                          + "(SELECT \n"
                          + "    COALESCE (SUM (S.price), 0) \n"
                          + "    FROM (  \n"
                          + "         SELECT CASE accm2.is_direction WHEN true THEN (accm2.price*COALESCE(accm2.exchangerate,1))  \n"
                          + "         ELSE -(accm2.price*COALESCE(accm2.exchangerate,1))  END   as price\n"
                          + "         FROM  general.accountmovement AS accm2 \n"
                          + "         WHERE accm2.account_id = ?  AND accm2.deleted=false AND accm2.branch_id IN( " + branchList + " )\n"
                          + "         AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "         ORDER BY accm2.movementdate DESC, accm2.id DESC \n"
                          + "         ) S \n"
                          + ") AS transferringbalance, \n"
                          + " (SELECT \n"
                          + "     COALESCE(SUM(accm1.price*COALESCE(accm1.exchangerate,1)),0)\n"
                          + "     FROM\n"
                          + "     general.accountmovement accm1 \n"
                          + "      LEFT JOIN finance.invoice inv ON (inv.id = accm1.invoice_id AND inv.deleted = False )\n"
                          + "      LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm1.financingdocument_id AND fdoc.deleted=False)\n"
                          + "     WHERE accm1.is_direction=true AND accm1.deleted=false AND accm1.branch_id IN( " + branchList + " )\n"
                          + whereType + "\n"
                          + "     AND accm1.account_id=? AND \n"
                          + "     accm1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' " + whereTermDate + "\n"
                          + " ) AS sumincoming,\n"
                          + " (SELECT \n"
                          + "     COALESCE(SUM(accm1.price*COALESCE(accm1.exchangerate,1)),0)\n"
                          + "     FROM\n"
                          + "     general.accountmovement accm1 \n"
                          + "      LEFT JOIN finance.invoice inv ON (inv.id = accm1.invoice_id AND inv.deleted = False )\n"
                          + "      LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm1.financingdocument_id AND fdoc.deleted=False)\n"
                          + "     WHERE accm1.is_direction=false AND accm1.deleted=false AND accm1.branch_id IN( " + branchList + " )\n"
                          + whereType + "\n"
                          + "     AND accm1.account_id=? AND \n"
                          + "     accm1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "' " + whereTermDate + " \n"
                          + " ) AS sumoutcoming\n"
                          + " FROM \n"
                          + "    general.accountmovement accm \n"
                          + "    INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "    LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "    LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "    LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "    LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "    LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE) \n"
                          + "    LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "    LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "    LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "    LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = ?)\n"
                          + "    LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "    LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + " WHERE \n"
                          + "	accm.account_id=? AND accm.deleted=false AND accm.branch_id IN( " + branchList + " )\n"
                          + whereType + "\n"
                          + " AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'" + where + whereTermDate; //where
                params = new Object[]{account.getId(), account.getId(), account.getId(), sessionBean.getUser().getLanguage().getId(), account.getId()};
                break;
        }

        List<AccountMovement> result = getJdbcTemplate().query(sql, params, new AccountMovementMapper());
        if (!result.isEmpty()) {
            return result.get(0);
        } else {
            return new AccountMovement();
        }
    }

    @Override
    public String exportData(String where, Account account, int opType, Date beginDate, Date endDate, String sortField, String sortOrder, Date termDate, int termDateOpType, String branchList, int financingTypeId) {
        String sql;
        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String whereTermDate = "";

        String whereType = "";

        if (financingTypeId > 0) {
            whereType = " AND fdoc.type_id = " + financingTypeId + " \n";
        }

        if (sortField == null) {
            sortField = "accm.movementdate";
            sortOrder = " DESC ";
        } else if (sortField.equals("financingDocument.documentDate")) {
            sortField = " (CASE WHEN fdoc.id > 0 THEN fdoc.documentdate\n"
                      + "	WHEN inv.id > 0 THEN inv.invoicedate\n"
                      + "       WHEN rcp.id > 0 THEN rcp.processdate"
                      + "       ELSE accm.movementdate END) ";

        } else if (sortField.equals("financingDocument.documentNumber")) {
            sortField = " (CASE WHEN fdoc.id > 0 THEN fdoc.documentnumber\n"
                      + "	WHEN inv.id > 0 THEN inv.documentnumber\n"
                      + "       WHEN cqb.id > 0 THEN cqb.portfolionumber\n"
                      + "       WHEN rcp.id > 0 THEN rcp.receiptno ELSE null END) ";

        } else if (sortField.equals("dateCreated")) {
            sortField = " (CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                      + "       WHEN inv.id>0 THEN inv.c_time\n"
                      + "	WHEN cqb.id>0 THEN cqb.c_time\n"
                      + "       WHEN rcp.id>0 THEN rcp.c_time END) ";

        } else if (sortField.equals("userCreated.fullName")) {
            sortField = " concat(usr.name,usr.surname) ";

        } else if (sortField.equals("dateUpdated")) {
            sortField = " (CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                      + "       WHEN inv.id>0 THEN inv.u_time\n"
                      + "	WHEN cqb.id>0 THEN cqb.u_time\n"
                      + "       WHEN rcp.id>0 THEN rcp.u_time END) ";

        } else if (sortField.equals("userUpdated.fullName")) {
            sortField = " concat(usr1.name,usr1.surname) ";

        } else if (sortField.equals("financingDocument.financingType.tag")) {
            sortField = " (CASE WHEN fdoc.id>0 THEN fdoc.type_id\n"
                      + "       WHEN inv.id>0 THEN (CASE WHEN inv.is_purchase THEN 0 ELSE 1 END)\n"
                      + "	WHEN cqb.id>0 THEN cqb.id\n"
                      + "       WHEN rcp.id>0 THEN rcp.id END) ";
        } else if (sortField.equals("isDirection")) {//Borç
            sortField = " (CASE WHEN accm.is_direction  = FALSE THEN accm.price * accm.exchangerate ELSE NULL END) ";
        } else if (sortField.equals("exchangeRate")) {//Alacak
            sortField = " (CASE WHEN accm.is_direction  = TRUE THEN accm.price * accm.exchangerate ELSE NULL END) ";
        } else if (sortField.equals("invoice.dueDate")) {
            sortField = "inv.duedate";

        }

        if (termDate != null) {

            switch (termDateOpType) {
                case 1:
                    whereTermDate = "AND inv.duedate < '" + format.format(termDate) + "'";
                    break;
                case 2:
                    whereTermDate = "AND inv.duedate = '" + format.format(termDate) + "'";
                    break;
                case 3:
                    whereTermDate = "AND inv.duedate > '" + format.format(termDate) + "'";
                    break;
                default:
                    whereTermDate = "";
                    break;
            }

        }

        switch (opType) {
            case 1:
                //sadece gelen
                sql = "SELECT\n"
                          + "fdoc.id as fdocid,  \n"
                          + "fdoc.documentnumber as fdocdocumentnumber,  \n"
                          + "fdoc.description as fdocdescription,   \n"
                          + "fdoc.type_id as fdoctype_id,\n"
                          + "fdoc.documentdate as fdocdocumentdate,\n"
                          + "typd.name AS typdname,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                          + "      WHEN inv.id>0 THEN inv.c_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_time END) AS fdocc_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                          + "      WHEN inv.id>0 THEN inv.u_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_time END) AS fdocu_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "      WHEN inv.id>0 THEN inv.c_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_id END) AS fdocc_id,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "      WHEN inv.id>0 THEN inv.u_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_id END) AS fdocu_id,\n"
                          + "usr.name as usrname,\n"
                          + "usr.surname as usrsurname,\n"
                          + "usr1.name as usr1name,\n"
                          + "usr1.surname as usr1surname,\n"
                          + "inv.id as invid,\n"
                          + "inv.documentnumber as invdocumentnumber,\n"
                          + "inv.invoicedate as invinvoicedate,\n"
                          + "inv.is_purchase AS invis_purchase,\n"
                          + "accm.id as accmid,\n"
                          + "accm.price as accmprice, \n"
                          + "accm.is_direction as accmis_direction,  \n"
                          + "COALESCE(accm.exchangerate,1) as accmexchangerate,  \n"
                          + "accm.movementdate as accmmovementdate,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "accm.chequebill_id AS accmchequebill_id,\n"
                          + "cqb.portfolionumber AS cqbportfolionumber,\n"
                          + "accm.receipt_id AS accmreceipt_id,\n"
                          + "rcp.receiptno AS rcpreceiptno,\n"
                          + "rcp.processdate AS rcpprocessdate,\n"
                          + "crr.code as crrcode,\n"
                          + "crr.sign as crrsign, \n"
                          + "(SELECT COALESCE (SUM (S.price), 0)  FROM (  \n"
                          + "SELECT (accm2.price*COALESCE(accm2.exchangerate,1))  as price\n"
                          + "FROM  general.accountmovement AS accm2 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm2.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm2.account_id = acc.id AND accm2.is_direction=true AND accm2.deleted=false AND accm2.branch_id = accm.branch_id \n"
                          + whereType + "\n"
                          + "AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "ORDER BY accm2.movementdate DESC, accm2.id DESC) S \n"
                          + ")+SUM(accm.price*COALESCE(accm.exchangerate,1)) \n"
                          + "OVER(ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 0 ELSE 1 END,\n"
                          + sortField + " ,accm.id \n"
                          + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS accbalance,\n"
                          + "shp.shift_id AS shpshift_id,\n"
                          + "stf.stocktaking_id AS stfstocktaking_id, \n"
                          + "inv.duedate AS invduedate, \n"
                          + "br.name AS brname\n"
                          + "FROM    \n"
                          + "general.accountmovement accm  \n"
                          + "INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE) \n"
                          + "LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + ")\n"
                          + "LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + "LEFT JOIN general.branch br ON(br.id = accm.branch_id AND br.deleted=FALSE)\n"
                          + "WHERE  accm.is_direction=true AND accm.deleted=false AND accm.branch_id IN( " + branchList + " )\n"
                          + "AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + "AND accm.account_id=" + account.getId() + "\n"
                          + where + whereTermDate + "\n"
                          + whereType + "\n"
                          + "ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 1 ELSE 0 END,\n"
                          + sortField + " " + sortOrder + ", accm.id " + sortOrder + " \n";
                break;
            //sadece giden
            case 2:
                sql = "SELECT\n"
                          + "fdoc.id as fdocid,  \n"
                          + "fdoc.documentnumber as fdocdocumentnumber,  \n"
                          + "fdoc.description as fdocdescription,   \n"
                          + "fdoc.type_id as fdoctype_id,\n"
                          + "fdoc.documentdate as fdocdocumentdate,\n"
                          + "typd.name AS typdname,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                          + "      WHEN inv.id>0 THEN inv.c_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_time END) AS fdocc_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                          + "      WHEN inv.id>0 THEN inv.u_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_time END) AS fdocu_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "      WHEN inv.id>0 THEN inv.c_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_id END) AS fdocc_id,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "      WHEN inv.id>0 THEN inv.u_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_id END) AS fdocu_id,\n"
                          + "usr.name as usrname,\n"
                          + "usr.surname as usrsurname,\n"
                          + "usr1.name as usr1name,\n"
                          + "usr1.surname as usr1surname,\n"
                          + "inv.id as invid,\n"
                          + "inv.documentnumber as invdocumentnumber,\n"
                          + "inv.invoicedate as invinvoicedate,\n"
                          + "inv.is_purchase AS invis_purchase,\n"
                          + "accm.id as accmid,\n"
                          + "accm.price as accmprice, \n"
                          + "accm.is_direction as accmis_direction,  \n"
                          + "COALESCE(accm.exchangerate,1) as accmexchangerate,  \n"
                          + "accm.movementdate as accmmovementdate,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "accm.chequebill_id AS accmchequebill_id,\n"
                          + "cqb.portfolionumber AS cqbportfolionumber,\n"
                          + "accm.receipt_id AS accmreceipt_id,\n"
                          + "rcp.receiptno AS rcpreceiptno,\n"
                          + "rcp.processdate AS rcpprocessdate,\n"
                          + "crr.code as crrcode,\n"
                          + "crr.sign as crrsign, \n"
                          + "(SELECT COALESCE (SUM (S.price), 0)  FROM (  \n"
                          + "SELECT -(accm2.price*COALESCE(accm2.exchangerate,1)) as price\n"
                          + "FROM  general.accountmovement AS accm2 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm2.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm2.account_id = acc.id AND accm2.is_direction=false AND accm2.deleted=false AND accm2.branch_id =accm.branch_id\n"
                          + whereType + "\n"
                          + "AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "ORDER BY accm2.movementdate DESC, accm2.id DESC) S \n"
                          + ")+SUM(-(accm.price*COALESCE(accm.exchangerate,1))) \n"
                          + "OVER(ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 0 ELSE 1 END,\n"
                          + sortField + " ,accm.id \n"
                          + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS accbalance,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "acc.balance AS balance,\n"
                          + "(SELECT COALESCE(SUM(accm1.price*COALESCE(accm1.exchangerate,1)),0)\n"
                          + "FROM\n"
                          + "general.accountmovement accm1 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm1.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm1.is_direction=false AND accm1.deleted=false AND accm1.branch_id =accm.branch_id\n"
                          + whereType + "\n"
                          + "AND accm1.account_id=" + account.getId() + " AND accm1.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "') AS sumoutcoming,\n"
                          + "shp.shift_id AS shpshift_id,\n"
                          + "stf.stocktaking_id AS stfstocktaking_id, \n"
                          + "inv.duedate AS invduedate,\n"
                          + "br.name AS brname\n"
                          + "FROM    \n"
                          + "general.accountmovement accm  \n"
                          + "INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE) \n"
                          + "LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + ")\n"
                          + "LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + "LEFT JOIN general.branch br ON(br.id = accm.branch_id AND br.deleted=FALSE)\n"
                          + "WHERE  accm.is_direction=false AND accm.deleted=false AND accm.branch_id IN( " + branchList + " )\n"
                          + "AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + "AND accm.account_id=" + account.getId() + "\n"
                          + where + whereTermDate + "\n"
                          + whereType + "\n"
                          + "ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 1 ELSE 0 END,\n"
                          + sortField + " " + sortOrder + ", accm.id " + sortOrder + " \n";
                break;
            default:
                //hepsi
                sql = "SELECT \n"
                          + "fdoc.id as fdocid, \n"
                          + "fdoc.documentnumber as fdocdocumentnumber,  \n"
                          + "fdoc.description as fdocdescription,   \n"
                          + "fdoc.type_id as fdoctype_id,\n"
                          + "fdoc.documentdate as fdocdocumentdate,\n"
                          + "typd.name AS typdname,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_time\n"
                          + "      WHEN inv.id>0 THEN inv.c_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_time END) AS fdocc_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_time\n"
                          + "      WHEN inv.id>0 THEN inv.u_time\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_time\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_time END) AS fdocu_time,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "      WHEN inv.id>0 THEN inv.c_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.c_id END) AS fdocc_id,\n"
                          + "(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "      WHEN inv.id>0 THEN inv.u_id\n"
                          + "	   WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      WHEN rcp.id>0 THEN rcp.u_id END) AS fdocu_id,\n"
                          + "usr.name as usrname,\n"
                          + "usr.surname as usrsurname,\n"
                          + "usr1.name as usr1name,\n"
                          + "usr1.surname as usr1surname,\n"
                          + "inv.id as invid,\n"
                          + "inv.documentnumber as invdocumentnumber,\n"
                          + "inv.invoicedate as invinvoicedate,\n"
                          + "inv.is_purchase AS invis_purchase,\n"
                          + "accm.id as accmid,\n"
                          + "accm.price as accmprice, \n"
                          + "accm.is_direction as accmis_direction,  \n"
                          + "COALESCE(accm.exchangerate,1) as accmexchangerate,  \n"
                          + "accm.movementdate as accmmovementdate,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "accm.chequebill_id AS accmchequebill_id,\n"
                          + "cqb.portfolionumber AS cqbportfolionumber,\n"
                          + "accm.receipt_id AS accmreceipt_id,\n"
                          + "rcp.receiptno AS rcpreceiptno,\n"
                          + "rcp.processdate AS rcpprocessdate,\n"
                          + "crr.code as crrcode,\n"
                          + "crr.sign as crrsign, \n"
                          + "(SELECT COALESCE (SUM (S.price), 0)  FROM (  \n"
                          + "SELECT CASE accm2.is_direction WHEN true THEN (accm2.price*COALESCE(accm2.exchangerate,1))  \n"
                          + "ELSE -(accm2.price*COALESCE(accm2.exchangerate,1))  END   as price\n"
                          + "FROM  general.accountmovement AS accm2 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm2.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm2.account_id = acc.id  AND accm2.deleted=false AND accm2.branch_id =accm.branch_id\n"
                          + whereType + "\n"
                          + "AND accm2.movementdate < '" + format.format(beginDate) + "' \n"
                          + "ORDER BY accm2.movementdate DESC, accm2.id DESC) S \n"
                          + ")+SUM(CASE WHEN accm.is_direction=true THEN  (accm.price*COALESCE(accm.exchangerate,1))\n"
                          + "ELSE -(accm.price*COALESCE(accm.exchangerate,1)) END ) \n"
                          + "OVER(ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 0 ELSE 1 END,\n"
                          + sortField + " ,accm.id \n"
                          + "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS accbalance,\n"
                          + "accm.currency_id as accmcurrency_id,\n"
                          + "acc.balance AS balance,\n"
                          + "(SELECT \n"
                          + "COALESCE(SUM(accm3.price*COALESCE(accm3.exchangerate,1)),0)\n"
                          + "FROM\n"
                          + "general.accountmovement accm3 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm3.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm3.is_direction=true AND accm3.deleted=false AND accm3.branch_id =accm.branch_id\n"
                          + whereType + "\n"
                          + "AND accm3.account_id=" + account.getId() + " AND accm3.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + ") AS sumincoming,\n"
                          + "(SELECT COALESCE(SUM(accm4.price*COALESCE(accm4.exchangerate,1)),0)\n"
                          + "FROM\n"
                          + "general.accountmovement accm4 \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm4.financingdocument_id AND fdoc.deleted=False)\n"
                          + "WHERE accm4.is_direction=false AND accm4.deleted=false AND accm4.branch_id =accm.branch_id\n"
                          + whereType + "\n"
                          + "AND accm4.account_id=" + account.getId() + " AND accm4.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + ") AS sumoutcoming   ,\n"
                          + "shp.shift_id AS shpshift_id,\n"
                          + "stf.stocktaking_id AS stfstocktaking_id ,\n"
                          + "inv.duedate AS invduedate, \n"
                          + "br.name AS brname\n"
                          + "FROM    \n"
                          + "general.accountmovement accm  \n"
                          + "INNER JOIN general.account acc ON(acc.id=accm.account_id AND acc.deleted = False)   \n"
                          + "LEFT JOIN finance.financingdocument fdoc  ON (fdoc.id = accm.financingdocument_id AND fdoc.deleted = False)\n"
                          + "LEFT JOIN finance.invoice inv ON (inv.id = accm.invoice_id AND inv.deleted = False)\n"
                          + "LEFT JOIN general.shiftpayment_financingdocument_con shpcon ON(shpcon.financingdocument_id = fdoc.id AND shpcon.deleted=FALSE)\n"
                          + "LEFT JOIN general.shiftpayment shp ON(shp.id = shpcon.shiftpayment_id AND shp.deleted = FALSE)\n"
                          + "LEFT JOIN inventory.stocktaking_financingdocument_con stf ON(stf.financingdocument_id = fdoc.id AND stf.deleted =FALSE) \n"
                          + "LEFT JOIN finance.chequebill cqb ON (cqb.id = accm.chequebill_id AND cqb.deleted = False)\n"
                          + "LEFT JOIN finance.receipt rcp ON (rcp.id = accm.receipt_id AND rcp.deleted = False)\n"
                          + "LEFT JOIN system.currency crr ON (crr.id = accm.currency_id) \n"
                          + "LEFT JOIN system.type_dict typd ON (typd.type_id = fdoc.type_id AND typd.language_id = " + sessionBean.getUser().getLanguage().getId() + ")\n"
                          + "LEFT JOIN general.userdata usr ON (usr.id=(CASE WHEN fdoc.id>0 THEN fdoc.c_id\n"
                          + "                                                WHEN inv.id>0 THEN inv.c_id\n"
                          + "						     WHEN cqb.id>0 THEN cqb.c_id\n"
                          + "      					     WHEN rcp.id>0 THEN rcp.c_id ELSE 0 END))\n"
                          + "LEFT JOIN general.userdata usr1 ON (usr1.id=(CASE WHEN fdoc.id>0 THEN fdoc.u_id\n"
                          + "	  					       WHEN inv.id>0 THEN inv.u_id\n"
                          + "						       WHEN cqb.id>0 THEN cqb.u_id\n"
                          + "      					       WHEN rcp.id>0 THEN rcp.u_id ELSE 0 END))\n"
                          + "LEFT JOIN general.branch br ON(br.id = accm.branch_id AND br.deleted=FALSE)\n"
                          + "WHERE    \n"
                          + "accm.account_id=" + account.getId() + " AND accm.deleted=false AND accm.branch_id IN( " + branchList + " ) " + " AND accm.movementdate BETWEEN '" + format.format(beginDate) + "' AND '" + format.format(endDate) + "'\n"
                          + where + whereTermDate + "\n"
                          + whereType + "\n"
                          + "ORDER BY CASE WHEN fdoc.id IS NULL AND inv.id IS NULL AND cqb.id IS NULL AND rcp.id IS NULL THEN 1 ELSE 0 END,\n"
                          + sortField + " " + sortOrder + ", accm.id " + sortOrder + " \n";
                break;
        }

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

    @Override
    public int updatePrice(AccountMovement accountMovement) {
        String sql = "UPDATE general.accountmovement "
                  + "SET "
                  + "price = ?, "
                  + "is_direction = ?, "
                  + "u_id= ? ,"
                  + "u_time= now() "
                  + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{accountMovement.getPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? accountMovement.getPrice() : accountMovement.getPrice().multiply(BigDecimal.valueOf(-1)),
            accountMovement.getPrice().compareTo(BigDecimal.valueOf(0)) == 1 ? true : false,
            sessionBean.getUser().getId(), accountMovement.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
