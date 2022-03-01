/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2018 02:42:14
 */
package com.mepsan.marwiz.finance.customeragreements.dao;

import java.util.List;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CustomerAgreementsDao extends JdbcDaoSupport implements ICustomerAgreementsDao {

    String sql;

    @Override
    public List<CustomerAgreements> findAll(String where, boolean checkCreditControl, int creditType) {
        if (checkCreditControl == false) {

            sql = "SELECT \n"//cari bazında sorgu
                      + "    crdt.is_invoice AS isInvoiced,\n"
                      + "    crdt.account_id AS crdtaccount_id,\n"
                      + "    acc.name AS accname,\n"
                      + "    acc.title AS acctitle,\n"
                      + "    acc.is_employee AS accis_employee,\n"
                      + "    acc.address  AS accaddress,\n"
                      + "    acc.email AS accemail,\n"
                      + "    acc.phone AS accphone,\n"
                      + "    acc.dueday as accdueday,\n"
                      + "    COALESCE(SUM(crdt.money),0) AS crdtmoney,\n"
                      + "    crdt.currency_id AS crdtcurrency,\n"
                      + "    crdt.branch_id AS crdtbranch_id,\n"
                      + "    brs.is_centralintegration AS brsis_centralintegration,\n"
                      + "    brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                      + "    br.currency_id AS brcurrency_id,\n"
                      + "    br.is_agency AS bris_agency,\n"
                      + "    brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                      + "    cr.code as crcode\n" 
                    + "FROM finance.credit crdt \n"
                      + "	INNER JOIN general.account acc ON(acc.id = crdt.account_id)\n"
                      + "   INNER JOIN general.branchsetting brs ON (brs.branch_id = crdt.branch_id AND brs.deleted = FALSE)\n"
                      + "   INNER JOIN general.branch br ON (br.id = crdt.branch_id AND br.deleted = FALSE)\n"
                      + "   INNER JOIN system.currency cr ON(crdt.currency_id=cr.id)\n"  
                      + " AND crdt.deleted=False \n"
                      + "	AND crdt.is_cancel=FALSE\n"
                      + where + "\n"
                      + "GROUP BY crdt.account_id, acc.name, acc.title, acc.is_employee, acc.address, acc.email, acc.phone, br.is_agency,\n"
                      + "crdt.currency_id, acc.dueday, crdt.branch_id, brs.is_centralintegration, brs.is_invoicestocksalepricelist, br.currency_id,crdt.is_invoice,brs.is_unitpriceaffectedbydiscount,cr.code";
        } else {

            if (creditType == 1) //kredi bazında sorgu tip 1 - Market
            {
                sql = "SELECT\n"
                          + "ROW_NUMBER () OVER () AS rownumber,\n"
                          + "crdt.id as crdtid,\n"
                          + "crdt.processdate as crdtduedate,\n"
                          + "crdt.is_invoice AS isInvoiced,\n"
                          + "crdt.account_id AS crdtaccount_id,\n"
                          + "acc.name AS accname,\n"
                          + "acc.title AS acctitle,\n"
                          + "acc.is_employee AS accis_employee,\n"
                          + "acc.address  AS accaddress,\n"
                          + "acc.email AS accemail,\n"
                          + "acc.phone AS accphone,\n"
                          + "acc.dueday as accdueday,\n"
                          + "crdt.money AS crdtmoney,\n"
                          + "crdt.currency_id AS crdtcurrency,\n"
                          + "crdt.branch_id AS crdtbranch_id,\n"
                          + "brs.is_centralintegration AS brsis_centralintegration,\n"
                          + "brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                          + "br.currency_id AS brcurrency_id,\n"
                          + "br.is_agency AS bris_agency,\n"
                          + "brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                          + " cr.code as crcode\n"
                          + "FROM finance.credit crdt\n"
                          + "INNER JOIN general.account acc ON(acc.id = crdt.account_id)\n"
                          + "INNER JOIN general.branchsetting brs ON (brs.branch_id = crdt.branch_id AND brs.deleted = FALSE)\n"
                          + "INNER JOIN general.branch br ON (br.id = crdt.branch_id AND br.deleted = FALSE)\n"
                          + "INNER JOIN system.currency cr ON(crdt.currency_id=cr.id)\n"  
                          + where + "\n"
                          + "AND crdt.id NOT IN(select sh.credit_id  from automation.shiftsale sh WHERE sh.credit_id is not NULL and sh.deleted=false)\n"
                          + "AND crdt.deleted=False\n"
                          + "AND crdt.is_cancel=FALSE\n";
            }
            if (creditType == 2) {//kredi tipine göre sorgu tip 2 - Akaryakıt
                sql = "SELECT\n"
                          + "ROW_NUMBER () OVER () AS rownumber,\n"
                          + " crdt.id as crdtid,\n"
                          + " crdt.processdate as crdtduedate,\n"
                          + " crdt.is_invoice AS isInvoiced,\n"
                          + " crdt.account_id AS crdtaccount_id,\n"
                          + " acc.name AS accname,\n"
                          + " acc.title AS acctitle,\n"
                          + " acc.is_employee AS accis_employee,\n"
                          + " acc.address  AS accaddress,\n"
                          + " acc.email AS accemail,\n"
                          + " acc.phone AS accphone,\n"
                          + " acc.dueday as accdueday,\n"
                          + " crdt.money AS crdtmoney,\n"
                          + " crdt.currency_id AS crdtcurrency,\n"
                          + " crdt.branch_id AS crdtbranch_id,\n"
                          + " sh.plate AS shplate,\n"
                          + " stck.name AS stckname,\n"
                          + " sh.liter AS shsliter,\n"
                          + " sh.price AS shprice, \n"
                          + " un.id as unid,\n"
                          + " un.name as unname,\n"
                          + " un.sortname as unsort,\n"
                          + " un.unitrounding as unrounding,\n"
                          + " brs.is_centralintegration AS brsis_centralintegration,\n"
                          + " brs.is_invoicestocksalepricelist as brsis_invoicestocksalepricelist,\n"
                          + " br.currency_id AS brcurrency_id,\n"
                          + " br.is_agency AS bris_agency,\n"
                          + " brs.is_unitpriceaffectedbydiscount AS brsis_unitpriceaffectedbydiscount,\n"
                          + " cr.code as crcode\n"
                          + " FROM finance.credit crdt\n"
                          + " INNER JOIN general.account acc ON(acc.id = crdt.account_id)\n"
                          + " INNER JOIN general.branchsetting brs ON (brs.branch_id = crdt.branch_id AND brs.deleted = FALSE)\n"
                          + " INNER JOIN general.branch br ON (br.id = crdt.branch_id AND br.deleted = FALSE)\n"
                          + " INNER JOIN automation.shiftsale sh ON(sh.credit_id=crdt.id AND sh.deleted=FALSE)\n"
                          + " INNER JOIN inventory.stock stck ON(stck.id=sh.stock_id AND stck.deleted=FALSE)\n"
                          + " INNER JOIN general.unit un on(un.id=stck.unit_id)\n"
                          + " INNER JOIN system.currency cr ON(crdt.currency_id=cr.id)\n"  
                          + where + "\n"
                          + "AND crdt.deleted=False \n"
                          + "AND crdt.is_cancel=FALSE";

            }

        }

        List<CustomerAgreements> result = getJdbcTemplate().query(sql, new CustomerAgreementsMapper());
        return result;
    }

 

}
