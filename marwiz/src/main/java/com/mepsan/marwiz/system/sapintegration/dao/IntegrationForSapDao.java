/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapintegration.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.WarehouseReceipt;
import com.mepsan.marwiz.inventory.warehousereceipt.dao.WarehouseReceiptMapper;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import com.mepsan.marwiz.system.sapagreement.dao.SapAgreement;
import com.mepsan.marwiz.system.sapagreement.dao.SapAgreementMapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author elif.mart
 */
public class IntegrationForSapDao extends JdbcDaoSupport implements IIntegrationForSapDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<IntegrationForSap> listOfWarehouseReceipt(Date beginDate, Date endDate, Boolean send, BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_sap_warehousereceipt(?,?,?,?)";
        Object[] param = new Object[]{selectedBranch.getBranch().getId(), beginDate, endDate, sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().query(sql, param, new IntegrationForSapMapper(1));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<BranchSetting> findBranch() {
        String sql = "SELECT \n"
                + "   brn.id as brid,\n"
                + "   brn.name as brname,\n"
                + "   brns.is_centralintegration as brsis_centralintegration,\n"
                + "   brns.erpurl AS brserpurl,\n"
                + "   brns.erpusername AS brserpusername,\n"
                + "   brns.erppassword AS brserppassword,\n"
                + "   brns.erptimeout AS brserptimeout,\n"
                + "   brns.erpintegrationcode AS brserpintegrationcode,\n"
                + "   brns.erpintegration_id as brserpintegration_id\n"
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

    /**
     * Bu metot işlem tipine göre gerekli tablodaki issend ve response
     * alanlarını günceller
     *
     *
     */
    @Override
    public int update(IntegrationForSap sap, int processType) {

        String tableName = "";
        String columnName = "";
        String columnName1 = "";
        String columnisfirstsend = "";
        if ((sap.getEvent() == 1 || sap.getEvent() == 0) && sap.isIsSend() && (processType == 1 || processType == 2)) {
            columnisfirstsend = "is_firstsend = true,\n";
        }
        switch (processType) {
            case 1:
                tableName = "integration.sap_warehousereceipt";
                columnName = "receiptnumber";
                break;
            case 2:
                tableName = "integration.sap_purchaseinvoice";
                columnName = "invoicenumber";
                columnName1 = "is_sendwaybill = ? ,\n";
                break;
            case 3:
                tableName = "integration.sap_saleinvoice";
                columnName = "invoicenumber";
                break;
            default:
                break;
        }

        String sql = "UPDATE\n "
                + tableName + " \n"
                + "SET\n"
                + "    is_send = ?,\n"
                + "    senddate = ?,\n"
                + "    sendcount = COALESCE(sendcount,0) + 1,\n"
                + "    response = ?,\n"
                + "    " + columnName + " = ?,\n"
                + columnName1
                + "    sessionnumber = ?,\n"
                + columnisfirstsend
                + "    u_id = ?,\n"
                + "    u_time = now()\n"
                + "WHERE id = ? ";
        Object[] param = new Object[]{};
        if (processType == 2) {
            param = new Object[]{sap.isIsSend(), sap.getSendDate(), sap.getResponse(), sap.getSapDocumentNumber(), sap.isIsSendWaybill(), sap.getSapIDocNo(), sessionBean.getUser().getId(), sap.getId()};

        } else {
            param = new Object[]{sap.isIsSend(), sap.getSendDate(), sap.getResponse(), sap.getSapDocumentNumber(), sap.getSapIDocNo(), sessionBean.getUser().getId(), sap.getId()};

        }

        try {
            return getJdbcTemplate().update(sql, param);
        } catch (DataAccessException e) {
            System.out.println("-----catch---" + e.getMessage());
            return 0;
        }

    }

    @Override
    public List<IntegrationForSap> listOfSaleInvoices(Date beginDate, Date endDate, Boolean isRetail, BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_sap_saleinvoice(?,?,?,?,?)";
        Object[] param = new Object[]{selectedBranch.getBranch().getId(), isRetail, beginDate, endDate, sessionBean.getUser().getId()};
        try {
            return getJdbcTemplate().query(sql, param, new IntegrationForSapMapper(3));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<IntegrationForSap> listOfPurchaseInvoices(Date beginDate, Date endDate, int purchaseInvoiceType, BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_sap_purchaseinvoice(?,?,?,?,?)";
        Object[] param = new Object[]{selectedBranch.getBranch().getId(), purchaseInvoiceType, beginDate, endDate, sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().query(sql, param, new IntegrationForSapMapper(2));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<WarehouseReceipt> findWarehouseReceipt(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, BranchSetting selectedBranch) {
        String column = "";
        String join = "";

        if (sortField == null) {
            sortField = "whr.processdate";
            sortOrder = "desc";
        }
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranchSetting().getErpIntegrationId() == 1) {
            column = "swhr.is_send as swhris_send,\n";
            join = "left join integration.sap_warehousereceipt swhr on(swhr.object_id=whr.id and swhr.deleted=FALSE and swhr.branch_id=wh.branch_id)\n";
        }

        String sql = "select \n"
                + "whr.id as whrid,\n"
                + "whr.warehouse_id as whrwarehouse_id,\n"
                + "wh.name as whname,\n"
                + "whr.receiptnumber as whrreceiptnumber,\n"
                + "whr.is_direction as whris_direction,\n"
                + "whr.type_id as whrtype_id,\n"
                + "typd.name as typdname,\n"
                + column
                + "whr.processdate as whrprocessdate\n"
                + "from inventory.warehousereceipt whr\n"
                + "inner join inventory.warehouse wh on (whr.warehouse_id=wh.id and wh.deleted=false)\n"
                + "inner join system.type_dict typd on (typd.type_id=whr.type_id and typd.language_id=?)\n"
                + join
                + "where wh.branch_id=? and whr.deleted=false" + where + "\n"
                + "ORDER BY " + sortField + " " + sortOrder + "  \n"
                + " limit " + pageSize + " offset " + first;

        Object[] param = {sessionBean.getUser().getLanguage().getId(), selectedBranch.getBranch().getId()};
        List<WarehouseReceipt> result = getJdbcTemplate().query(sql, param, new WarehouseReceiptMapper());
        return result;
    }

    @Override
    public int openUpdate(List<IntegrationForSap> listOfSelectedSap, String sapIdList) {

        String sql = "UPDATE integration.sap_saleinvoice" + " \n"
                + "SET\n"
                + "    is_send = FALSE,\n"
                + "    senddate = NULL,\n"
                + "    response = NULL,\n"
                + "    invoicenumber = NULL,\n"
                + "    sessionnumber = NULL,\n"
                + "    u_id = ?,\n"
                + "    u_time = now()\n"
                + "WHERE id IN( " + sapIdList + ") ";

        Object[] param = {sessionBean.getUser().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int[] sendStatusUpdate(List<IntegrationForSap> listOfSelectedSap, int processType) {
        int result[] = {0};
        String tableName = "";
        String columnName = "";
        String columnName1 = "";
        String columnisfirstsend = "";

        switch (processType) {
            case 2:
                tableName = "integration.sap_purchaseinvoice";
                columnName1 = "is_sendwaybill = ? ,\n";
                columnisfirstsend = "is_firstsend = true,\n";
                break;
            case 3:
                tableName = "integration.sap_saleinvoice";
                break;
            default:
                break;
        }

        String sql = "UPDATE\n "
                + tableName + " \n"
                + "SET\n"
                + "    is_send = true,\n"
                + "    senddate = now(),\n"
                + "    sendcount = COALESCE(sendcount,0) + 1,\n"
                + "    response =  ' " + sessionBean.getLoc().getString("thedocumentwasnotsenttosapthesendingstatuswasupdatedmanually") + " ',\n"
                + "    invoicenumber = null,\n"
                + columnName1
                + columnisfirstsend
                + "    sessionnumber = null,\n"
                + "    u_id = ?,\n"
                + "    u_time = now()\n"
                + "WHERE id = ? ";
        try {
            result = getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {

                    IntegrationForSap sap = listOfSelectedSap.get(i);
                    if (processType == 2) {
                        ps.setBoolean(1, sap.getTypeId() == 59 ? true : false);
                        ps.setInt(2, sessionBean.getUser().getId());
                        ps.setInt(3, sap.getId());
                    } else if (processType == 3) {

                        ps.setInt(1, sessionBean.getUser().getId());
                        ps.setInt(2, sap.getId());
                    }

                }

                @Override
                public int getBatchSize() {

                    return listOfSelectedSap.size();
                }

            });
        } catch (Exception ex) {
            ex.printStackTrace();
            result[0] = -1;

        }
        return result;
    }
    
    @Override
    public List<IntegrationForSap> testResponse() {

        String sql = "SELECT \n"
                + "    sap.senddata as r_response\n"
                + "FROM integration.sap_agreement sap   \n"
                + "WHERE sap.deleted = FALSE AND sap.id =35";
        Object[] param = new Object[]{};
        return getJdbcTemplate().query(sql, param, new IntegrationForSapMapper(2));
    }

   
    

}
