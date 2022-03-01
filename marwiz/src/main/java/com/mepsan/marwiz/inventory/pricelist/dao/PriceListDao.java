/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.01.2018 10:41:36
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class PriceListDao extends JdbcDaoSupport implements IPriceListDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PriceList> listofPriceList() {
        String sql = "SELECT\n"
                  + "     pl.id AS plid, \n"
                  + "     pl.name AS plname,\n"
                  + "     pl.code AS plcode,\n"
                  + "     pl.status_id AS plstatus_id,\n"
                  + "     sttd.name AS sttdname,\n"
                  + "     pl.is_default as plis_default,\n"
                  + "     pl.is_purchase as plis_purchase,\n"
                  + "     pl.c_time AS  plc_time , \n"
                  + "     pl.c_id AS plc_id, \n"
                  + "     usd.name as usdname,\n"
                  + "     usd.surname as usdsurname,\n"
                  + "     usd.username as usdusername\n"
                  + "FROM \n"
                  + "     inventory.pricelist pl \n"
                  + "INNER JOIN general.userdata usd ON(usd.id=pl.c_id)\n"
                  + "INNER JOIN system.status_dict sttd ON (sttd.status_id = pl.status_id AND sttd.language_id = ?)\n"
                  + "WHERE\n"
                  + "     pl.deleted = false AND pl.branch_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<PriceList> result = getJdbcTemplate().query(sql, param, new PriceListMapper());
        return result;
    }

    @Override
    public int create(PriceList obj) {
        String sql = "INSERT INTO inventory.pricelist (branch_id,name,code,status_id,is_default,is_purchase,c_id,u_id)\n"
                  + "SELECT ?,\n"
                  + " ?, \n"
                  + " ?,\n"
                  + " ?, \n"
                  + " ?, \n"
                  + " ?,\n"
                  + " ?,\n"
                  + " ? \n "
                  + "WHERE ((? = false) OR (? = true AND NOT EXISTS(SELECT id FROM inventory.pricelist WHERE deleted=false AND is_default=true AND branch_id=? AND is_purchase= ?))) Returning id; ";
        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(),
            obj.getCode(), obj.getStatus().getId(),
            obj.isIsDefault(), obj.isIsPurchase(), sessionBean.getUser().getId(), sessionBean.getUser().getId(), obj.isIsDefault(), obj.isIsDefault(),
            sessionBean.getUser().getLastBranch().getId(), obj.isIsPurchase()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            if (((SQLException) e.getCause()) == null) {//Varsa default değeri -1 döndürmek için 
                return -1;
            } else {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }

        }
    }

    @Override
    public int update(PriceList obj) {
        String sql = "UPDATE inventory.pricelist \n"
                  + "SET\n"
                  + "name=?, code=?, status_id=?, is_default=?, is_purchase=?, u_id=?, u_time=now() \n"
                  + "WHERE id=? and \n"
                  + "((? = false) OR (? = true AND NOT EXISTS(SELECT id FROM inventory.pricelist WHERE deleted=false AND is_default=true  AND branch_id=? AND is_purchase= ? AND id <> ? ))) Returning id; ";

        Object[] param = new Object[]{obj.getName(),
            obj.getCode(), obj.getStatus().getId(),
            obj.isIsDefault(), obj.isIsPurchase(), sessionBean.getUser().getId(), obj.getId(), obj.isIsDefault(), obj.isIsDefault(),
            sessionBean.getUser().getLastBranch().getId(), obj.isIsPurchase(), obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            if (((SQLException) e.getCause()) == null) {//Varsa default değeri -1 döndürmek için 
                return -1;
            } else {
                return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
            }
        }
    }

    @Override
    public int delete(PriceList priceList) {

        String sql = "UPDATE inventory.pricelist set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n"
                  + "UPDATE inventory.pricelistitem set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND pricelist_id=?\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), priceList.getId(), sessionBean.getUser().getId(), priceList.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    public PriceList findDefaultPriceList(boolean isPurchase, Branch branch) {

        String sql = "SELECT \n"
                  + "   pl.id AS plid\n"
                  + "FROM inventory.pricelist pl\n"
                  + "WHERE pl.deleted = FALSE\n"
                  + "   AND pl.status_id = 11 \n"
                  + "   AND pl.is_default = TRUE \n"
                  + "   AND pl.is_purchase = ? \n"
                  + "   AND pl.branch_id = ? ";

        Object[] param = new Object[]{isPurchase, branch.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, new PriceListMapper());
        } catch (DataAccessException e) {
            return new PriceList();
        }
    }
}
