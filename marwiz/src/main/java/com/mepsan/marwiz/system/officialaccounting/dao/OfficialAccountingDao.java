/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.officialaccounting.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author ali.kurt
 */
public class OfficialAccountingDao extends JdbcDaoSupport implements IOfficialAccountingDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<OfficalAccounting> listOfAccount(BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_account(?) ";
        Object[] param = new Object[]{selectedBranch.getBranch().getId()};
        try {
            return getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(1));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfficalAccounting> listOfStock(BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_stock(?) ";

        Object[] param = new Object[]{selectedBranch.getBranch().getId()};
        try {
            return getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(2));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfficalAccounting> listOfSafe(BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_safe(?) ";
        Object[] param = new Object[]{selectedBranch.getBranch().getId()};
        try {
            return getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(3));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfficalAccounting> listOfBank(BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_bankaccount(?) ";
        Object[] param = new Object[]{selectedBranch.getBranch().getId()};
        try {
            return getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(4));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfficalAccounting> listOfWarehouse(BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_warehouse(?) ";
        Object[] param = new Object[]{selectedBranch.getBranch().getId()};

        try {
            return getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(5));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }

    }

    @Override
    public List<OfficalAccounting> listOfAccountMovement(int type, boolean isRetail, Date begin, Date end, BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_accountreceipt(?,?,?,?,?)";
        Object[] param = new Object[]{selectedBranch.getBranch().getId(), type, isRetail, begin, end};

        try {
            return getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(6));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<OfficalAccounting> listOfStockReceipt(int type, boolean isRetail, Date begin, Date end, BranchSetting selectedBranch) {
        String sql = "SELECT * FROM integration.process_stockreceipt(?,?,?,?,?) ";
        Object[] param = new Object[]{selectedBranch.getBranch().getId(), type, isRetail, begin, end};
        try {
            return getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(7));
        } catch (DataAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Bu metot işlem tipine göre gerekli tablodaki issend ve response
     * alanlarını günceller
     *
     * @param officalAccounting
     * @param processType
     * @return
     */
    @Override
    public int update(OfficalAccounting officalAccounting, int processType) {

        String tableName = "";
        switch (processType) {
            case 1:
                tableName = "integration.account";
                break;
            case 2:
                tableName = "integration.stock";
                break;
            case 3:
                tableName = "integration.safe";
                break;
            case 4:
                tableName = "integration.bankaccount";
                break;
            case 5:
                tableName = "integration.warehouse";
                break;
            case 6:
            case 7:
            case 8:
                tableName = "integration.accountreceipt";
                break;
            case 9:
            case 10:
            case 11:
                tableName = "integration.stockreceipt";
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
                + "    response = ?\n"
                + "WHERE id = ? ";

        Object[] param = new Object[]{officalAccounting.isIsSend(), officalAccounting.getSendDate(), officalAccounting.getResponse(), officalAccounting.getId()};
        try {
            return getJdbcTemplate().update(sql, param);
        } catch (DataAccessException e) {
            return 0;
        }

    }

    /**
     * Bu metot ilgili tablolardaki gönderilmiş, silinmiş kayıtların sayılarını
     * döndürür.
     *
     * @param processType
     * @return
     */
    @Override
    public TotalCount getTotalCounts(int processType, String branchList) {
        String sql = "";
       

        switch (processType) {
            case 1://Cari kart
                sql = "SELECT\n"
                        + "    SUM(CASE WHEN t.deleted = FALSE THEN 1 ELSE 0 END) as notdeleted_count,\n"
                        + "    SUM(CASE WHEN t.deleted = TRUE THEN 1 ELSE 0 END) as deleted_count,\n"
                        + "    SUM(CASE WHEN t2.is_send = TRUE THEN 1 ELSE 0 END) as send_count,\n"
                        + "    SUM(CASE WHEN t2.is_send = FALSE THEN 1 ELSE 0 END) as notsend_count\n"
                        + "FROM general.account t\n"
                        + "LEFT JOIN integration.account t2 ON (t2.account_id = t.id)\n"
                        + "INNER JOIN general.account_branch_con abc ON (abc.account_id = t.id AND abc.deleted = FALSE AND abc.branch_id = t2.branch_id)\n"
                        + "        WHERE\n"
                        + "        	t2.branch_id IN (" + branchList + ") ";
                break;
            case 2://stok kart

                sql = "SELECT\n"
                        + "    SUM(CASE WHEN t.deleted = FALSE THEN 1 ELSE 0 END) as notdeleted_count,\n"
                        + "    SUM(CASE WHEN t.deleted = TRUE THEN 1 ELSE 0 END) as deleted_count,\n"
                        + "    SUM(CASE WHEN t2.is_send = TRUE THEN 1 ELSE 0 END) as send_count,\n"
                        + "    SUM(CASE WHEN t2.is_send = FALSE THEN 1 ELSE 0 END) as notsend_count\n"
                        + "FROM inventory.stock t\n"
                        + "LEFT JOIN integration.stock t2 ON (t2.stock_id = t.id)\n"
                        + "INNER JOIN inventory.stockinfo si ON (si.stock_id = t.id AND si.deleted = FALSE AND si.branch_id = t2.branch_id)\n"
                        + "WHERE \n"
                        + "        	t2.branch_id IN (" + branchList + ") ";
                      
                break;
            case 3://kasa kart
                sql = "SELECT\n"
                        + "	SUM(CASE WHEN deleted = FALSE THEN 1 ELSE 0 END) as notdeleted_count,\n"
                        + "    SUM(CASE WHEN deleted = TRUE THEN 1 ELSE 0 END) as deleted_count,\n"
                        + "    SUM(CASE WHEN is_send = TRUE THEN 1 ELSE 0 END) as send_count,\n"
                        + "    SUM(CASE WHEN is_send = FALSE THEN 1 ELSE 0 END) as notsend_count\n"
                        + "FROM finance.safe t\n"
                        + "LEFT JOIN integration.safe t2 ON (t2.safe_id = t.id)\n"
                        + " WHERE t.branch_id IN (" + branchList + ") ";

                break;
            case 4://banka kart
                sql = "SELECT\n"
                        + "	SUM(CASE WHEN t.deleted = FALSE THEN 1 ELSE 0 END) as notdeleted_count,\n"
                        + "    SUM(CASE WHEN t.deleted = TRUE THEN 1 ELSE 0 END) as deleted_count,\n"
                        + "    SUM(CASE WHEN t2.is_send = TRUE THEN 1 ELSE 0 END) as send_count,\n"
                        + "    SUM(CASE WHEN t2.is_send = FALSE THEN 1 ELSE 0 END) as notsend_count\n"
                        + "FROM finance.bankaccount t\n"
                        + "LEFT JOIN integration.bankaccount t2 ON (t2.bankaccount_id = t.id)\n"
                        + "INNER JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = t.id AND bbc.deleted=FALSE AND bbc.branch_id = t2.branch_id)\n"
                        + " WHERE t2.branch_id IN (" + branchList + ") ";

                break;
            case 5://depo kart
                sql = "SELECT\n"
                        + "	SUM(CASE WHEN deleted = FALSE THEN 1 ELSE 0 END) as notdeleted_count,\n"
                        + "    SUM(CASE WHEN deleted = TRUE THEN 1 ELSE 0 END) as deleted_count,\n"
                        + "    SUM(CASE WHEN is_send = TRUE THEN 1 ELSE 0 END) as send_count,\n"
                        + "    SUM(CASE WHEN is_send = FALSE THEN 1 ELSE 0 END) as notsend_count\n"
                        + "FROM inventory.warehouse t\n"
                        + "LEFT JOIN integration.warehouse t2 ON (t2.warehouse_id = t.id)\n"
                        + " WHERE t.branch_id IN (" + branchList + ") ";
                break;
            case 6://finansman belgeleri
                sql = "SELECT\n"
                        + "    0 as notdeleted_count,\n"
                        + "    0 as deleted_count,\n"
                        + "    SUM(CASE WHEN is_send = TRUE THEN 1 ELSE 0 END) as send_count,\n"
                        + "    SUM(CASE WHEN is_send = FALSE THEN 1 ELSE 0 END) as notsend_count\n"
                        + "FROM integration.accountreceipt t2 \n"
                        + "WHERE t2.type_id = 1 ";
                break;
            default:
                break;
        }
        List<TotalCount> list = getJdbcTemplate().query(sql, new TotalCountMapper());

        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return new TotalCount();
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
                + "   brns.erpintegration_id as brserpintegration_id,\n"
                + "   brns.is_erpuseshift AS brsis_erpuseshift\n"
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
