/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.stockaccountreceipt.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.integration.OfficalAccounting;
import com.mepsan.marwiz.system.officialaccounting.dao.OfficialAccountingMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author ali.kurt
 */
public class StockAccountReceiptDao extends JdbcDaoSupport implements IStockAccountReceiptDao {

    @Override
    public List<OfficalAccounting> findNotSendedAllStockReceipt(BranchSetting bs, Date begin, Date end) {

        String sql = "SELECT * FROM integration.process_stockreceipt(?,?,?,?,?)";
        Object[] param = new Object[]{bs.getBranch().getId(), 1, true, begin, end};

        List<OfficalAccounting> accountings = new ArrayList<>();
        try {
            accountings = getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(7));
        } catch (Exception e) {
        }
        return accountings;
    }

    @Override
    public List<OfficalAccounting> findNotSendedAllAccountReceipt(BranchSetting bs, Date begin, Date end) {
        String sql = "SELECT * FROM integration.process_accountreceipt(?,?,?,?,?)";

        Object[] param = new Object[]{bs.getBranch().getId(), 1, true, begin, end};

        List<OfficalAccounting> accountings = new ArrayList<>();
        try {
            accountings = getJdbcTemplate().query(sql, param, new OfficialAccountingMapper(6));
        } catch (Exception e) {
        }
        return accountings;
    }

    @Override
    public int updateStockReceipt(OfficalAccounting officalAccounting) {
        String sql = "UPDATE\n"
                + " integration.stockreceipt\n "
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

    @Override
    public int updateAccountReceipt(OfficalAccounting officalAccounting) {
        String sql = "UPDATE\n"
                + " integration.accountreceipt\n "
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

    @Override
    public List<BranchSetting> listOfAllBranch() {
        String sql = "SELECT \n"
                + " br.id as brid,\n"
                + " brs.erpurl as brserpurl\n"
                + "FROM general.branch br \n"
                + "INNER JOIN general.branchsetting brs ON (brs.branch_id = br.id AND brs.deleted = FALSE)\n"
                + "WHERE br.deleted = FALSE\n"
                + "AND brs.erpintegration_id = 2\n";//Logo-Akbim

        List<BranchSetting> branchSettings = new ArrayList<>();
        try {
            branchSettings = getJdbcTemplate().query(sql, new OfficialBranchSettingMapper());
        } catch (Exception e) {
        }
        return branchSettings;
    }

}
