/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 18.07.2018 17:48:32
 */
package com.mepsan.marwiz.finance.salereturn.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SaleReturnDao extends JdbcDaoSupport implements ISaleReturnDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SaleReturnReport> findReceipt(String receiptNo, Date processDate, int branchId) {
        String sql = "SELECT \n"
                + "      rc.id AS rcidd ,\n"
                + "    rc.receiptno AS rcreceiptno,\n"
                + "    rc.processdate AS rcprocessdate,\n"
                + "    rc.is_return AS rcis_return , \n"
                + "    acc.id AS  accid, \n"
                + "    acc.name AS accname, \n"
                + "    sl.id AS  slid ,\n"
                + "    sl.currency_id AS slcurrency_id , \n"
                + "    sl.posmacaddress AS slposmacaddress, \n"
                + "    COALESCE(sl.totaldiscount,0) AS sltotaldiscount,\n"
                + "    COALESCE(sl.totalmoney,0) AS sltotalmoney,\n"
                + "    COALESCE(sl.totalprice,0) AS totalprice,\n"
                + "    COALESCE(sl.totaltax,0) AS sltotaltax,\n"
                + "    sli.stock_id AS  slistock_id,\n"
                + "    stck.name AS stckname,\n"
                + "    sli.processdate AS sliprocessdate,\n"
                + "    sli.unit_id AS sliunit_id,\n"
                + "    sli.unitprice AS sliunitprice,\n"
                + "    sli.recommendedprice AS slirecommendedprice,\n"
                + "    COALESCE(sli.quantity,0) AS sliquantity,\n"
                + "    COALESCE((sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)),0) AS slitotalpric,\n"
                + "    sli.taxrate AS slitaxrate,\n"
                + "    COALESCE(sli.totaltax,0) AS slitotaltax,\n"
                + "    COALESCE(sli.discountrate,0) AS  slidiscountrate,\n"
                + "    COALESCE(sli.discountprice,0) AS slidiscountprice,\n"
                + "    sli.currency_id AS slicurrency_id,\n"
                + "    sli.exchangerate AS sliexchangerate,\n"
                + "    COALESCE((sli.totalprice - ((sli.totalprice * COALESCE(sl.discountrate, 0))/100)),0) AS slitotalprice,\n"
                + "    COALESCE((sli.totalmoney - ((sli.totalmoney * COALESCE(sl.discountrate, 0))/100)),0) AS slitotalmoney,\n"
                + "    sli.is_managerdiscount AS sliis_managerdiscount,\n"
                + "    sli.manageruserdata_id AS slimanageruserdata_id,\n"
                + "    stck.unit_id AS stckunit_id,\n"
                + "    unt.name AS untname, \n"
                + "    unt.sortname AS untsortname,\n"
                + "    unt.unitrounding AS untunitrounding,\n"
                + "    us1.name AS us1name,\n"
                + "    us1.surname AS us1surname,\n"
                + "    sli.id AS sliid,\n"
                + "    sl.pointofsale_id AS slpointofsale_id,\n"
                + "    pos.safe_id AS possafe_id,\n"
                + "    sf.currency_id AS sfcurrency_id,\n"
                + "    pos.warehouse_id AS poswarehouse_id,\n"
                + "    pos.macaddress AS posmacaddress, \n"
                + "    ( SELECT wr.warehouse_id from  finance.receipt_warehousereceipt_con rwc\n"
                + "           INNER JOIN inventory.warehousereceipt wr ON(wr.id = rwc.warehousereceipt_id AND wr.deleted = FALSE) \n"
                + "           INNER JOIN inventory.warehousemovement wm ON(wm.warehousereceipt_id = wr.id AND wm.stock_id =stck.id AND wm.deleted = FALSE )\n"
                + "           WHERE rwc.receipt_id = rc.id ANd rwc.deleted = FALSE) AS  wrwarehouse_id, \n"
                + " (CASE WHEN ( SELECT COUNT(brcd.id) FROM log.barcode brcd WHERE brcd.marketsale_id = sl.id AND brcd.is_used = TRUE )<> 0 THEN TRUE ELSE FALSE END ) AS isusedstock \n"
                + "FROM \n"
                + "    finance.receipt rc \n"
                + "    INNER JOIN general.account acc ON(acc.id = rc.account_id AND acc.deleted = FALSE)\n"
                + "    INNER JOIN general.sale sl ON(sl.receipt_id = rc.id AND sl.deleted = FALSE)\n"
                + "    LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                + "    INNER JOIN general.saleitem sli ON(sli.sale_id = sl.id AND sli.deleted = FALSE)\n"
                + "    LEFT JOIN general.userdata us1 ON(us1.id=sli.manageruserdata_id)\n"
                + "    INNER JOIN inventory.stock stck ON(stck.id = sli.stock_id AND stck.deleted = FALSE AND stck.status_id =  3) \n"
                + "    LEFT JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.deleted = FALSE AND si.branch_id = ?) \n"
                + "    LEFT JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE )"
                + "    INNER JOIN general.pointofsale pos ON(pos.id = sl.pointofsale_id )\n"
                + "    INNER JOIN finance.safe sf ON(sf.id = pos.safe_id) \n"
                + "WHERE \n"
                + "    sl.is_return=False  AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND    rc.processdate between ?::TIMESTAMP AND  ?::TIMESTAMP + interval '1 hours'\n"
                + "    AND SUBSTR(rc.receiptno, LENGTH(rc.receiptno) - 4, 5)::INTEGER =  ?::INTEGER   AND rc.branch_id = ? \n"
                + "    AND rc.deleted = FALSE AND si.is_passive =FALSE \n"
                + "    ";

        Object[] param = {branchId, new Timestamp(processDate.getTime()), new Timestamp(processDate.getTime()), receiptNo.trim().toLowerCase().replaceAll("([a-zğüşöçİĞÜŞÖÇı])", "0"), branchId};

        List<SaleReturnReport> result = getJdbcTemplate().query(sql, param, new SaleReturnMapper());
        return result;
    }

    @Override
    public List<SaleReturnReport> findSalePayment(int saleId) {
        String sql = " \n"
                + "SELECT\n"
                + "    slp.type_id AS slptype_idd,\n"
                + "    typd.name AS typdname,\n"
                + "    COALESCE (SUM(slp.price),0) AS slpprice,  \n"
                + "    slp.exchangerate AS slpexchangerate, \n"
                + "    slp.currency_id AS slpcurrency_id,\n"
                + "    COALESCE (SUM(cr.money -cr.remainingmoney),0) AS totalcreditpaymentprice,\n"
                + "    ba.id AS baid,\n"
                + "    ba.name AS baname \n"
                + "FROM \n"
                + "	general.salepayment slp\n"
                + "    INNER JOIN system.type_dict typd ON (typd.type_id = slp.type_id AND typd.language_id =?)\n"
                + "     LEFT JOIN  finance.credit cr ON(cr.id = slp.credit_id AND cr.deleted = FALSE ) \n"
                + "    LEFT JOIN finance.financingdocument fd ON(fd.id = slp.financingdocument_id AND fd.deleted = FALSE)\n"
                + "    LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id = fd.id AND bam.deleted = FALSE)\n"
                + "    LEFT JOIN finance.bankaccount ba ON(ba.id = bam.bankaccount_id AND ba.deleted = FALSE) \n"
                + "WHERE \n"
                + "	slp.sale_id = ? AND slp.deleted=False\n"
                + "GROUP BY \n"
                + "	slp.type_id, typd.name, slp.currency_id,slp.exchangerate, ba.id , ba.name \n"
                + "            \n"
                + "            ";
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), saleId};
        List<SaleReturnReport> result = getJdbcTemplate().query(sql, param, new SaleReturnMapper());
        return result;
    }

    @Override
    public List<SaleReturnReport> findCreditPayment(int saleId) {
        String sql = "SELECT\n"
                + "    slp.credit_id AS slpcredit_idd,\n"
                + "    cr.money  AS crmoney ,\n"
                + "    cr.remainingmoney AS crremainingmoney, \n"
                + "    cr.is_paid AS  cris_paid, \n"
                + "    cr.duedate AS crduedate,\n"
                + "    crp.type_id AS crptype_id , \n"
                + "    typd.name AS typdname,\n"
                + "    COALESCE (crp.price,0) AS crpprice,  \n"
                + "    crp.currency_id AS crpcurrency_id, \n"
                + "    slp.currency_id AS slpcurrency_id,\n"
                + "    acc.id AS accid,\n"
                + "    acc.name AS accname,\n"
                + "    ba.id AS baid,\n"
                + "    ba.name AS baname \n"
                + "FROM \n"
                + "    general.salepayment slp\n"
                + "    INNER JOIN  finance.credit cr ON(cr.id = slp.credit_id AND cr.deleted = FALSE )\n"
                + "    LEFT JOIN  finance.creditpayment crp ON(crp.credit_id = cr.id AND crp.deleted = FALSE )\n"
                + "    INNER JOIN general.account acc ON(acc.id = cr.account_id AND acc.deleted = FALSE) \n"
                + "    LEFT JOIN  system.type_dict typd ON(typd.type_id = crp.type_id AND typd.language_id = ? ) \n"
                + "    LEFT JOIN finance.financingdocument fd ON(fd.id = crp.financingdocument_id AND fd.deleted = FALSE)\n"
                + "    LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id = fd.id AND bam.deleted = FALSE)\n"
                + "    LEFT JOIN finance.bankaccount ba ON(ba.id = bam.bankaccount_id AND ba.deleted = FALSE) \n"
                + "WHERE \n"
                + "  slp.sale_id =  ?  AND slp.deleted=False\n"
                + "ORDER BY crp.id ASC";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), saleId};
        List<SaleReturnReport> result = getJdbcTemplate().query(sql, param, new SaleReturnMapper());
        return result;
    }

    @Override
    public List<SaleReturnReport> findCreditPaymentDetail(int saleId) {
        String sql = "SELECT\n"
                + "    sum(crp.price)  AS crppricee,\n"
                + "    crp.exchangerate AS crpexchangerate, \n"
                + "    crp.type_id AS crptype_id , \n"
                + "    typd.name AS typdname,\n"
                + "    crp.currency_id AS crpcurrency_id, \n"
                + "    ba.id AS baid,\n"
                + "    ba.name AS baname \n"
                + "FROM \n"
                + "    general.salepayment slp\n"
                + "    INNER JOIN  finance.creditpayment crp ON(crp.credit_id = slp.credit_id AND crp.deleted = FALSE )\n"
                + "    LEFT JOIN  system.type_dict typd ON(typd.type_id = crp.type_id AND typd.language_id = ? ) \n"
                + "    LEFT JOIN finance.financingdocument fd ON(fd.id = crp.financingdocument_id AND fd.deleted = FALSE)\n"
                + "    LEFT JOIN finance.bankaccountmovement bam ON(bam.financingdocument_id = fd.id AND bam.deleted = FALSE)\n"
                + "    LEFT JOIN finance.bankaccount ba ON(ba.id = bam.bankaccount_id AND ba.deleted = FALSE) \n"
                + "WHERE \n"
                + "	slp.sale_id =  ?  AND slp.deleted=False \n"
                + "GROUP BY \n"
                + "   crp.exchangerate,crp.exchangerate,crp.type_id,\n"
                + "   typd.name,crp.currency_id,ba.id,ba.name  ";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), saleId};
        List<SaleReturnReport> result = getJdbcTemplate().query(sql, param, new SaleReturnMapper());
        return result;
    }

    @Override
    public int acceptReturn(SaleReturnReport obj, BranchSetting branchSetting, UserData userdata) {
        String sql = "SELECT r_receipt_id FROM finance.process_return_receipt(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{obj.getSales().getReceipt().getId(), obj.getSales().getId(), branchSetting.getBranch().getId(),
            obj.getSales().getAccount().getId(), sessionBean.getUser().getLastBranch().getCurrency().getId(), new Date(),
            sessionBean.getUser().getId(), sessionBean.getUser().getUsername(), branchSetting.isIsCentralIntegration(), userdata.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<SaleReturnReport> findSaleWithoutReceipt(String listStock, Date beginDate, Date endDate, int branchId) {
        String sql = "SELECT\n"
                + "    rc.id AS rcidd ,\n"
                + "    rc.receiptno AS rcreceiptno,\n"
                + "    rc.processdate AS rcprocessdate,\n"
                + "    rc.is_return AS rcis_return , \n"
                + "    acc.id AS  accid, \n"
                + "    acc.name AS accname, \n"
                + "    sl.id AS  slid ,\n"
                + "    sl.currency_id AS slcurrency_id , \n"
                + "    sl.totalmoney AS sltotalmoney,\n"
                + "    sl.totalprice AS sltotalprice,\n"
                + "    sl.totaltax AS sltotaltax,\n"
                + "    sl.totaldiscount AS sltotaldiscount,\n"
                + "    us1.name AS us1name,\n"
                + "    us1.surname AS us1surname,\n"
                + " (CASE WHEN ( SELECT COUNT(brcd.id) FROM log.barcode brcd WHERE brcd.marketsale_id = sl.id AND brcd.is_used = TRUE )<> 0 THEN TRUE ELSE FALSE END ) AS isusedstock \n"
                + "FROM \n"
                + "  finance.receipt rc \n"
                + "    INNER JOIN general.account acc ON(acc.id = rc.account_id AND acc.deleted = FALSE)\n"
                + "    INNER JOIN general.sale sl ON(sl.receipt_id = rc.id AND sl.deleted = FALSE ) \n"
                + "      LEFT JOIN general.sale sll ON(sll.returnparent_id = sl.id AND sll.deleted = False)\n"
                + "    INNER JOIN general.saleitem sli ON(sli.sale_id = sl.id AND sli.deleted = FALSE) \n "
                + "    LEFT JOIN general.userdata us1 ON(us1.id=sl.userdata_id)\n"
                + "WHERE \n"
                + "    sl.is_return=False  AND COALESCE(sll.id,0) = COALESCE(sll.returnparent_id,0) AND  rc.processdate between ? AND ? \n"
                + "    AND rc.branch_id = ?   AND rc.deleted = FALSE  " + listStock + "\n"
                + "    GROUP BY rc.id,rc.receiptno,rc.processdate,rc.is_return, \n"
                + "    acc.id,acc.name,sl.id,sl.currency_id ,sl.totalmoney,\n"
                + "    sl.totalprice,sl.totaltax,sl.totaldiscount,us1.name,us1.surname\n"
                + "    ORDER BY rc.processdate DESC ";

        // System.out.println("Sql = " + sql);
        Object[] param = {beginDate, endDate, branchId};
        List<SaleReturnReport> result = getJdbcTemplate().query(sql, param, new SaleReturnMapper());
        return result;
    }

    @Override
    public int checkSafeStatus(int saleId) {

        ResponseSalesReturn responseSalesReturn = new ResponseSalesReturn();
        String sql = "SELECT * FROM general.check_connection(?, ?);";
        //System.out.println("Sql = " + sql);
        Object[] param = {12, saleId};
        System.out.println(Arrays.toString(param));
        try {
            responseSalesReturn = getJdbcTemplate().queryForObject(sql, param, new ResponseSalesReturnMapper());
            System.out.println(responseSalesReturn.getSaleId());

            return responseSalesReturn.getSaleId();
        } catch (DataAccessException e) {
            return -1;
        }
    }

    //Yapılan iadenin paroya gönderilmesini sağlar.
    @Override
    public int createParoSales(int saleId) {
        String sql = "SELECT * FROM log.process_createorcancelparosales(?, ?, ?);";

        Object[] param = new Object[]{2, saleId, false};
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

   

    @Override
     public List<BranchSetting> findBranchSetting(){
      String sql = "SELECT \n"
                + "   brn.id as brid,\n"
                + "   brn.name as brname,\n"
                + "   brns.is_centralintegration as brsis_centralintegration,\n"
                + "   brns.parourl AS brsparourl,\n"
                + "   brns.paroaccountcode AS brsparoaccountcode,\n"
                + "   brns.parobranchcode AS brsparobranchcode,\n"
                + "   brns.paroresponsiblecode AS brsparoresponsiblecode\n"
                + "FROM general.userdata usr\n"
                + "INNER JOIN general.userdata_authorize_con usda ON(usr.id=usda.userdata_id AND usda.deleted=FALSE)\n"
                + "INNER JOIN general.authorize aut ON(aut.id=usda.authorize_id AND aut.deleted=FALSE)\n"
                + "INNER JOIN general.branch brn ON(brn.id=aut.branch_id AND brn.deleted=FALSE)\n"
                + "LEFT JOIN general.branchsetting brns ON(brns.branch_id=brn.id AND brns.deleted=FALSE)\n"
                + "WHERE usr.deleted=FALSE AND usr.id=?";

      
        Object[] param = new Object[]{sessionBean.getUser().getId()};
        List<BranchSetting> result = getJdbcTemplate().query(sql, param, new BranchSettingMapper());

        return result;
     
     }
    
}
