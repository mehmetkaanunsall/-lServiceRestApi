/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.12.2019 01:52:35
 */
package com.mepsan.marwiz.inventory.taxdepartment.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
       
public class TaxDepartmentDao extends JdbcDaoSupport implements ITaxDepartmentDao {             

    @Autowired         
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
     
    @Override
    public List<TaxDepartment> listOfTaxDepartment() {                 
        String sql = "SELECT \n"
                + "	 txd.id AS txdid,\n"
                + "    txd.departmentno AS txddepartmentno,\n"
                + "    txd.name AS txdname,\n"
                + "    txd.taxgroup_id AS txdtaxgroup_id,\n"
                + "    txg.name AS txgname,\n"
                + "    txg.c_time as txgc_time,\n"
                + "    usd.id as usdid,\n"
                + "    usd.name as usdname,\n"
                + "    usd.surname as usdsurname,\n"
                + "    usd.username as usdusername \n"
                + "FROM inventory.taxdepartment txd\n"
                + "	INNER JOIN inventory.taxgroup txg ON(txg.id = txd.taxgroup_id AND txg.deleted=FALSE)\n"
                + "     LEFT JOIN general.userdata usd ON(usd.id=txd.c_id)\n"
                + "WHERE txd.deleted=FALSE AND txd.branch_id = ? \n"
                + "ORDER BY txd.departmentno";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<TaxDepartment> result = getJdbcTemplate().query(sql, param, new TaxDepartmentMapper());
        return result;
    }

    @Override
    public int create(TaxDepartment obj) {
        String sql = "INSERT INTO inventory.taxdepartment\n"
                + "(departmentno, name, taxgroup_id, c_id, u_id, branch_id) \n"
                + "VALUES(?, ?, ?, ?, ?, ?) \n"
                + "RETURNING id ;";
        Object[] param = new Object[]{obj.getDepartmentNo(), obj.getName(), obj.getTaxGroup().getId(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(TaxDepartment obj) {
        String sql = "UPDATE inventory.taxdepartment \n"
                + "SET "
                + "departmentno = ?, "
                + "name = ? ,"
                + "taxgroup_id = ? ,"
                + "u_id= ? ,"
                + "u_time= now() "
                + "WHERE id = ? AND deleted = false";
        Object[] param = new Object[]{obj.getDepartmentNo(), obj.getName(),
            obj.getTaxGroup().getId(),
            sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(TaxDepartment obj) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT taxdepartment_id FROM inventory.stockinfo WHERE taxdepartment_id=? AND deleted=False) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(TaxDepartment obj) {
        String sql = "UPDATE inventory.taxdepartment set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int changeStockTaxDepartment() {
        String sql = "UPDATE \n"
                + "	inventory.stockinfo ist\n"
                + "SET\n"
                + "    taxdepartment_id = kk.taxdepartment_id,\n"
                + "    u_id  = ?,\n"
                + "    u_time = now()\n"
                + "FROM\n"
                + "(SELECT * FROM\n"
                + "		(SELECT\n"
                + "                (SELECT \n"
                + "                        gtd.id \n"
                + "                    FROM \n"
                + "                      	inventory.taxdepartment gtd \n"
                + "                    WHERE \n"
                + "                      	gtd.deleted = FALSE \n"
                + "                      	AND gtd.taxgroup_id = istc.taxgroup_id \n"
                + "                      	AND gtd.branch_id = ?\n"
                + "                    ORDER BY gtd.id ASC LIMIT 1\n"
                + "                ) AS taxdepartment_id,\n"
                + "                istc.taxgroup_id, \n"
                + "                stck.id AS stock_id,\n"
                + "                COALESCE(tx2.rate,-1)  AS rate2,\n"
                + "                COALESCE(tx.rate ,-1)  AS rate\n"
                + "     		FROM\n"
                + "               inventory.stock stck\n"
                + "               INNER JOIN 	inventory.stock_taxgroup_con istc ON (istc.stock_id = stck.id AND istc.is_purchase = FALSE AND istc.deleted = FALSE)\n"
                + "               INNER JOIN 	inventory.taxgroup tx ON (tx.id = istc.taxgroup_id AND tx.type_id = 10  AND tx.deleted = FALSE)\n"
                + "               LEFT JOIN 	inventory.stockinfo si ON (si.stock_id=stck.id AND si.deleted=False AND si.branch_id = ?)\n"
                + "               LEFT JOIN 	inventory.taxdepartment itdd ON (itdd.id = si.taxdepartment_id and itdd.deleted = FALSE)\n"
                + "               LEFT JOIN 	inventory.taxgroup tx2 ON (tx2.id = itdd.taxgroup_id AND tx2.type_id = 10  AND tx2.deleted = FALSE)\n"
                + "     		WHERE\n"
                + "               stck.deleted = FALSE \n"
                + "               AND CASE WHEN ? = TRUE THEN si.is_valid = TRUE ELSE stck.is_otherbranch = TRUE END ) ıı\n"
                + "	WHERE \n"
                + "    	ıı.rate <> ıı.rate2\n"
                + ")kk\n"
                + "WHERE \n"
                + "	ist.stock_id = kk.stock_id\n"
                + "    AND ist.branch_id = ?";
        Object[] param = new Object[]{sessionBean.getUser().getId(), sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getLastBranch().getId(),
            sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration(), sessionBean.getUser().getLastBranch().getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    /**
     * Seçilen Ürünün vergi grubuna göre merkezi entegrasyonu olmayan şubelerde
     * departmanını günceller.
     *
     * @param stock
     * @return
     */
    @Override
    public int changeTaxDepartmentToStockInNonCentralBranch(Stock stock) {
        String sql = " UPDATE\n"
                + "	inventory.stockinfo ist\n"
                + "SET\n"
                + "    taxdepartment_id = kk.taxdepartment_id,\n"
                + "    u_id  = ?,\n"
                + "    u_time = NOW()\n"
                + "FROM\n"
                + "(\n"
                + "	SELECT \n"
                + "    	(SELECT \n"
                + "         	gtd.id \n"
                + "         FROM inventory.taxdepartment gtd \n"
                + "         WHERE gtd.deleted = FALSE \n"
                + "               AND gtd.taxgroup_id = istc.taxgroup_id \n"
                + "               AND gtd.branch_id = si.branch_id\n"
                + "         ORDER BY gtd.id ASC LIMIT 1\n"
                + "         ) AS taxdepartment_id,\n"
                + "         istc.taxgroup_id, \n"
                + "         stck.id AS stock_id,\n"
                + "         si.branch_id AS branch_id\n"
                + "    FROM\n"
                + "         inventory.stockinfo si\n"
                + "    INNER JOIN inventory.stock stck ON(stck.id = si.stock_id AND stck.deleted=FALSE)\n"
                + "    INNER JOIN inventory.stock_taxgroup_con istc ON (istc.stock_id = stck.id AND istc.is_purchase = FALSE AND istc.deleted = FALSE)\n"
                + "    INNER JOIN inventory.taxgroup tx ON (tx.id = istc.taxgroup_id AND tx.type_id = 10  AND tx.deleted = FALSE)\n"
                + "    INNER JOIN general.branchsetting brs ON(brs.branch_id = si.branch_id AND brs.deleted=FALSE)\n"
                + "    WHERE si.deleted = FALSE\n"
                + "          AND stck.id = ? AND brs.is_centralintegration = FALSE\n"
                + ")kk\n"
                + "WHERE ist.stock_id = kk.stock_id\n"
                + "      AND ist.branch_id = kk.branch_id";

        Object[] param = new Object[]{sessionBean.getUser().getId(), stock.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
