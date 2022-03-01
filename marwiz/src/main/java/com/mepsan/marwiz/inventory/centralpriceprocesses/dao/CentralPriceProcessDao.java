/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.06.2020 11:34:00
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CentralPriceProcessDao extends JdbcDaoSupport implements ICentralPriceProcessDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<CentralPriceProcess> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int branchStock, String branchID) {
        switch (branchStock) {
              case 0://şartlar mevcut zaten girmesin diye
                where = where + "";
                break;
            case 1://merkezi
                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                          + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                          + "AND  si1.is_valid  =TRUE AND si1.is_passive = FALSE)\n";
                break;
            case 2://merkezi olmayan
                where = where + " AND stck.is_otherbranch = TRUE ";
                break;
            default://hepsi
                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                          + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                          + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                          + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE AND si1.is_passive = FALSE ELSE stck.is_otherbranch = TRUE END)) \n";
                break;
        }

        String sql = "SELECT\n"
                  + "   ROW_NUMBER () OVER (ORDER BY stck.id DESC) AS id,\n"
                  + "   stck.id AS stckid,\n"
                  + "   stck.barcode AS stckbarcode,\n"
                  + "   stck.name AS stckname,\n"
                  + "   (SELECT crrd.name FROM system.currency_dict crrd WHERE crrd.deleted=FALSE AND crrd.currency_id = 1 AND crrd.language_id = ?) AS currencytag\n"
                  + "FROM inventory.stock stck\n"
                  + "WHERE stck.deleted=FALSE AND stck.status_id <> 4\n"
                  + where + "\n"
                  + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        List<CentralPriceProcess> result = getJdbcTemplate().query(sql, param, new CentralPriceProcessMapper());
        return result;
    }

    @Override
    public int count(String where, int branchStock, String branchID) {

        switch (branchStock) {
            case 0://şartlar mevcut zaten girmesin diye
                where = where + "";
                break;
            case 1://merkezi
                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                          + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                          + "AND  si1.is_valid  =TRUE AND si1.is_passive = FALSE)\n";
                break;
            case 2://merkezi olmayan
                where = where + " AND stck.is_otherbranch = TRUE ";
                break;
            default://hepsi
                where = where + " AND stck.id IN(SELECT si1.stock_id FROM inventory.stockinfo si1 \n"
                          + "INNER JOIN general.branchsetting brs ON(brs.branch_id = si1.branch_id AND brs.deleted=FALSE)\n"
                          + "where si1.deleted=FALSE AND si1.branch_id IN(" + branchID + ") AND si1.stock_id=stck.id \n"
                          + "AND (CASE WHEN brs.is_centralintegration =TRUE THEN si1.is_valid  =TRUE AND si1.is_passive = FALSE ELSE stck.is_otherbranch = TRUE END)) \n";
                break;
        }

        String sql = "SELECT\n"
                  + "   COUNT(stck.id) AS stckid\n"
                  + "FROM inventory.stock stck\n"
                  + "WHERE stck.deleted=FALSE AND stck.status_id <> 4  \n"
                  + where + "\n";
        Object[] param = new Object[]{};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public int save(String selectedCentralPrice, boolean isPurchase) {
        String sql = " SELECT r_responsecode FROM inventory.centeral_pricelist(?, ?, ?);";
        Object[] param = {isPurchase, selectedCentralPrice == null ? null : selectedCentralPrice.equals("") ? null : selectedCentralPrice, sessionBean.getUser().getId()};
        System.out.println("------------param" + Arrays.toString(param));
        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
