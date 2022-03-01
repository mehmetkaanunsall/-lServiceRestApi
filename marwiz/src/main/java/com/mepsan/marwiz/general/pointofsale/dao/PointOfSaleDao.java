/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:19:26
 */
package com.mepsan.marwiz.general.pointofsale.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PointOfSaleDao extends JdbcDaoSupport implements IPointOfSaleDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PointOfSale> listOfPointOfSale() {
        String sql = "Select \n"
                  + "pos.id AS posid,\n"
                  + "pos.name AS posname,\n"
                  + "pos.code AS poscode,\n"
                  + "pos.brand AS posbrand,\n"
                  + "pos.model AS posmodel,\n"
                  + "pos.status_id AS posstatus_id,\n"
                  + "sttd.name AS sttdname,\n"
                  + "pos.serialnumber AS posserialnumber,\n"
                  + "pos.version AS posversion,\n"
                  + "pos.softwareversion AS possoftwareversion,\n"
                  + "pos.macaddress AS posmacaddress,\n"
                  + "pos.ipaddress AS posipaddress,\n"
                  + "pos.port AS posport,\n"
                  + "pos.cashregister_id AS poscashregister_id,\n"
                  + "cash.name AS cashname,\n"
                  + "pos.warehouse_id AS poswarehouse_id,\n"
                  + "pos.integrationcode AS posintegrationcode, \n"
                  + "iw.name AS iwname,\n"
                  + "pos.safe_id AS possafe_id,\n"
                  + "sf.currency_id AS sfcurrency_id,\n"
                  + "pos.localipaddress AS poslocalipaddress,\n"
                  + "pos.is_offline AS posis_offline,\n"
                  + "pos.stocktime AS posstocktime,\n"
                  + "pos.unittaxtime AS posunittaxtime,\n"
                  + "pos.usertime AS posusertime,\n"
                  + "pos.categorizationtime AS poscategorizationtime,\n"
                  + "pos.bankaccounttime AS posbankaccounttime,\n"
                  + "pos.pointofsaletime AS pospointofsaletime,\n"
                  + "pos.vendingmachinetime AS posvendingmachinetime,\n"
                  + "pos.washingintegrationcode AS poswashingintegrationcode\n"
                  + "FROM general.pointofsale pos \n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = pos.status_id AND sttd.language_id = ?)\n"
                  + "LEFT JOIN general.cashregister cash ON (cash.id = pos.cashregister_id AND cash.deleted=false)\n"
                  + "LEFT JOIN inventory.warehouse iw ON (iw.id = pos.warehouse_id AND iw.deleted=false)\n"
                  + "LEFT JOIN finance.safe sf ON (sf.id = pos.safe_id AND sf.deleted=false)\n"
                  + "WHERE pos.deleted=false AND pos.branch_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<PointOfSale> result = getJdbcTemplate().query(sql, param, new PointOfSaleMapper());
        return result;
    }

    @Override
    public int create(PointOfSale obj) {
        String sql = "INSERT INTO general.pointofsale\n"
                  + "(branch_id,name,code,brand,model,status_id,serialnumber,version,"
                  + "softwareversion,macaddress,ipaddress,port,cashregister_id,warehouse_id,safe_id,integrationcode,washingintegrationcode, localipaddress, "
                  + "is_offline, stocktime, unittaxtime, usertime, categorizationtime, bankaccounttime, pointofsaletime, vendingmachinetime, c_id,u_id) \n"
                  + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n"
                  + "RETURNING id ;";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.getCode(), obj.getBrand(), obj.getModel(),
            obj.getStatus().getId(), obj.getSerialNumber(), obj.getVersion(), obj.getSoftwareVersion(), obj.getMacAddress(), obj.getIpAddress(),
            obj.getPort(), obj.getCashRegister().getId() == 0 ? null : obj.getCashRegister().getId(), obj.getWareHouse().getId(), obj.getSafe().getId(), obj.getIntegrationCode(), obj.getWashingMachicneIntegrationCode(),
            obj.getLocalIpAddress(), obj.isIsOffline(), obj.getStockTime(), obj.getUnitTaxTime(), obj.getUserTime(), obj.getCategorizationTime(),
            obj.getBankAccountTime(), obj.getPointOfSaleTime(), obj.getVendingMachineTime(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(PointOfSale obj) {
        String sql = "UPDATE general.pointofsale "
                  + "SET "
                  + "name = ?, "
                  + "code = ? ,"
                  + "brand = ? ,"
                  + "model = ? ,"
                  + "status_id = ? ,"
                  + "serialnumber = ? ,"
                  + "version = ? ,"
                  + "softwareversion = ? ,"
                  + "macaddress = ? ,"
                  + "ipaddress = ? ,"
                  + "port = ? ,"
                  + "cashregister_id = ? ,"
                  + "warehouse_id = ? ,"
                  + "safe_id = ? ,"
                  + "integrationcode = ? ,"
                  + "washingintegrationcode = ?,"
                  + "localipaddress = ? ,"
                  + "is_offline = ? ,"
                  + "stocktime = ? ,"
                  + "unittaxtime = ? ,"
                  + "usertime = ? ,"
                  + "categorizationtime = ? ,"
                  + "bankaccounttime = ? ,"
                  + "pointofsaletime = ? ,"
                  + "vendingmachinetime = ? ,"
                  + "u_id= ? ,"
                  + "u_time= now() "
                  + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getName(), obj.getCode(), obj.getBrand(), obj.getModel(),
            obj.getStatus().getId(), obj.getSerialNumber(), obj.getVersion(), obj.getSoftwareVersion(), obj.getMacAddress(), obj.getIpAddress(),
            obj.getPort(), obj.getCashRegister().getId() == 0 ? null : obj.getCashRegister().getId(), obj.getWareHouse().getId(), obj.getSafe().getId(), obj.getIntegrationCode(), obj.getWashingMachicneIntegrationCode(),
            obj.getLocalIpAddress(), obj.isIsOffline(), obj.getStockTime(), obj.getUnitTaxTime(), obj.getUserTime(), obj.getCategorizationTime(),
            obj.getBankAccountTime(), obj.getPointOfSaleTime(), obj.getVendingMachineTime(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(PointOfSale obj) {

        String sql = "UPDATE general.pointofsale set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n"
                  + "UPDATE general.pointofsale_safe_con set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND pointofsale_id=?\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<PointOfSale> listIntegrationPointOfSale(Branch branch, String where) {
        String sql = "SELECT \n"
                  + "   pos.id AS posid,\n"
                  + "   pos.code AS poscode,\n"
                  + "   pos.name AS posname,\n"
                  + "   pos.macaddress AS posmacaddress,\n"
                  + "   pos.ipaddress AS posipaddress,\n"
                  + "   pos.integrationcode AS posintegrationcode\n"
                  + "FROM general.pointofsale pos\n"
                  + "   WHERE pos.deleted=FALSE AND pos.branch_id=? AND pos.status_id = 9\n"
                  + where + "\n"
                  + "ORDER BY pos.id DESC";

        Object[] param = new Object[]{branch.getId()};
        List<PointOfSale> result = getJdbcTemplate().query(sql, param, new PointOfSaleMapper());
        return result;

    }

    @Override
    public int updateIntegrationCode(PointOfSale obj) {
        String sql = "UPDATE general.pointofsale\n"
                  + "SET\n"
                  + "integrationcode = ?,\n"
                  + "u_id = ?,\n"
                  + "u_time = now()\n"
                  + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getIntegrationCode(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
