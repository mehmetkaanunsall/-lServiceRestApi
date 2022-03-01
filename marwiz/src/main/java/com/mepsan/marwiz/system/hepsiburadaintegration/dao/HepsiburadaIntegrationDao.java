/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.03.2021 03:45:04
 */
package com.mepsan.marwiz.system.hepsiburadaintegration.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchIntegration;
import com.mepsan.marwiz.general.model.inventory.ECommerceStock;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class HepsiburadaIntegrationDao extends JdbcDaoSupport implements IHepsiburadaIntegrationDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int updateListing(String updateSendData, String updateResult, String updateControlResult, Boolean isSuccess) {

        String sql = "SELECT * FROM integration.process_hepsiburada (?, ?, ?, ?, ?, ?);";

        Object[] param = new Object[]{2, updateSendData, updateResult, updateControlResult, sessionBean.getUser().getLastBranch().getId(), isSuccess};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<ECommerceStock> bringListing(String stockList, int first, int pageSize, boolean isBringListing, String where) {

        String sql = "SELECT * FROM inventory.findecommercestock (?, ?, ?, ?, ?, ?);";
        Object[] param = new Object[]{stockList, sessionBean.getUser().getLastBranch().getId(), String.valueOf(first), String.valueOf(pageSize), isBringListing, where};

        List<ECommerceStock> result = getJdbcTemplate().query(sql, param, new ECommerceStockMapper());
        return result;
    }

    @Override
    public int count(String where) {

        String sql = "SELECT \n"
                  + "COUNT(hb.id)\n"
                  + "    FROM  integration.hepsiburada hb\n"
                  + "    INNER JOIN inventory.stock stck ON(stck.id=hb.stock_id AND stck.deleted=FALSE AND stck.status_id = 3)\n"
                  + "    LEFT JOIN general.unit gunt ON(gunt.id=stck.unit_id)\n"
                  + "    INNER JOIN inventory.stockinfo si ON(si.stock_id = stck.id AND si.branch_id = ? AND si.deleted=FALSE)\n"
                  + "    LEFT JOIN inventory.pricelist pl ON (pl.branch_id=? AND pl.is_default=TRUE AND pl.is_purchase=FALSE AND pl.deleted=False)\n"
                  + "    LEFT JOIN inventory.pricelistitem pli ON (pli.stock_id=stck.id AND pli.pricelist_id=pl.id AND pli.deleted=False)\n"
                  + "    LEFT JOIN (SELECT\n"
                  + "                 txg.rate AS rate,\n"
                  + "                 stc.stock_id AS stock_id \n"
                  + "                 FROM inventory.stock_taxgroup_con stc\n"
                  + "                 INNER JOIN inventory.taxgroup txg  ON (txg.id=stc.taxgroup_id AND txg.deleted = false)\n"
                  + "                 WHERE stc.deleted = false\n"
                  + "                 AND txg.type_id = 10\n"
                  + "                 AND stc.is_purchase = FALSE) stg ON(stg.stock_id = stck.id)\n"
                  + "    WHERE hb.branch_id = ? AND si.is_passive = FALSE AND si.is_valid = TRUE\n"
                  + where;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public String findSendingHepsiburada() {//Tümünü gönder seçeneği

        String sql = "SELECT * FROM integration.findsendinghepsiburada(?);";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return String.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
