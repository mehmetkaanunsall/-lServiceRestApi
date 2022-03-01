/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:28:09 PM
 */
package com.mepsan.marwiz.general.contractarticles.dao;

import com.mepsan.marwiz.general.brand.dao.BrandMapper;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.ContractArticles;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ContractArticlesDao extends JdbcDaoSupport implements IContractArticlesDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<ContractArticles> findAll() {
        String sql = "SELECT\n"
                  + "  ca.id AS caid,\n"
                  + "  ca.stock_id as castock_id,\n"
                  + "  stck.name as stckname,\n"
                  + "  ca.articletype as caarticletype,\n"
                  + "  COALESCE(ca.warehousecost,0) as cawarehousecost,\n"
                  + "  COALESCE(ca.branchprofitrate,0) as cabranchprofitrate,\n"
                  + "  COALESCE(ca.rate1,0) as carate1,\n"
                  + "  COALESCE(ca.volume1,0) as cavolume1,\n"
                  + "  COALESCE(ca.rate2,0) as carate2,\n"
                  + "  COALESCE(ca.volume2,0) as cavolume2,\n"
                  + "  COALESCE(ca.rate3,0) as carate3,\n"
                  + "  COALESCE(ca.volume3,0) as cavolume3,\n"
                  + "  COALESCE(ca.rate4,0) as carate4,\n"
                  + "  COALESCE(ca.volume4,0) as cavolume4,\n"
                  + "  COALESCE(ca.rate5,0) as carate5,\n"
                  + "  COALESCE(ca.volume5,0) as cavolume5\n"
                  + "FROM automation.contractarticles ca \n"
                  + "INNER JOIN inventory.stock stck ON(stck.id=ca.stock_id AND stck.deleted=FALSE)\n"
                  + "WHERE ca.deleted=FALSE AND ca.branch_id = ? ";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new ContractArticlesMapper());
    }

    @Override
    public int testBeforeDelete(ContractArticles obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int delete(ContractArticles obj) {
        String sql = "UPDATE automation.contractarticles set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int create(ContractArticles obj) {
        String sql = "INSERT INTO \n"
                  + "  automation.contractarticles\n"
                  + "(\n"
                  + "  branch_id,\n"
                  + "  stock_id,\n"
                  + "  articletype,\n"
                  + "  warehousecost,\n"
                  + "  branchprofitrate,\n"
                  + "  rate1,\n"
                  + "  volume1,\n"
                  + "  rate2,\n"
                  + "  volume2,\n"
                  + "  rate3,\n"
                  + "  volume3,\n"
                  + "  rate4,\n"
                  + "  volume4,\n"
                  + "  rate5,\n"
                  + "  volume5,\n"
                  + "  c_id,\n"
                  + "  u_id\n"
                  + ")\n"
                  + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)RETURNING id ;";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getStock().getId(), obj.getArticltType(), obj.getWarehouseCost(), obj.getBranchProfitRate(), obj.getRate1(), obj.getVolume1(),
            obj.getRate2(), obj.getVolume2(), obj.getRate3(), obj.getVolume3(), obj.getRate4(), obj.getVolume4(), obj.getRate5(), obj.getVolume5(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(ContractArticles obj) {
        String sql = "UPDATE \n"
                  + "  automation.contractarticles \n"
                  + "SET \n"
                  + "  stock_id = ?,\n"
                  + "  articletype = ?,\n"
                  + "  warehousecost = ?,\n"
                  + "  branchprofitrate = ?,\n"
                  + "  rate1 = ?,\n"
                  + "  volume1 = ?,\n"
                  + "  rate2 = ?,\n"
                  + "  volume2 = ?,\n"
                  + "  rate3 = ?,\n"
                  + "  volume3 = ?,\n"
                  + "  rate4 = ?,\n"
                  + "  volume4 = ?,\n"
                  + "  rate5 = ?,\n"
                  + "  volume5 = ?,\n"
                  + "  u_id = ?,\n"
                  + "  u_time = now()\n"
                  + "WHERE \n"
                  + "  id = ?;";

        Object[] param = new Object[]{obj.getStock().getId(), obj.getArticltType(), obj.getWarehouseCost(), obj.getBranchProfitRate(), obj.getRate1(), obj.getVolume1(),
            obj.getRate2(), obj.getVolume2(), obj.getRate3(), obj.getVolume3(), obj.getRate4(), obj.getVolume4(), obj.getRate5(), obj.getVolume5(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int stockControl(ContractArticles obj) {
        String sql = "SELECT CASE WHEN EXISTS (SELECT stock_id FROM automation.contractarticles WHERE stock_id=? AND deleted=False) THEN 1 ELSE 0 END";

        Object[] param = new Object[]{obj.getStock().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public ContractArticles findStockArticles(int stockId, Branch branch) {
        String sql = "SELECT \n"
                  + "ca.id as caid,\n"
                  + "ca.stock_id as castock_id,\n"
                  + "ca.articletype as caarticletype,\n"
                  + "COALESCE(ca.branchprofitrate,0) as cabranchprofitrate,\n"
                  + " COALESCE(ca.warehousecost,0) as cawarehousecost,\n"
                  + " COALESCE(ca.rate1,0) as carate1,\n"
                  + " COALESCE(ca.volume1,0) as cavolume1,\n"
                  + " COALESCE(ca.rate2,0) as carate2,\n"
                  + " COALESCE(ca.volume2,0) as cavolume2,\n"
                  + " COALESCE(ca.rate3,0) as carate3,\n"
                  + " COALESCE(ca.volume3,0) as cavolume3,\n"
                  + " COALESCE(ca.rate4,0) as carate4,\n"
                  + " COALESCE(ca.volume4,0) as cavolume4,\n"
                  + " COALESCE(ca.rate5,0) as carate5,\n"
                  + " COALESCE(ca.volume5,0) as cavolume5\n"
                  + "FROM automation.contractarticles ca \n"
                  + "WHERE ca.deleted=FALSE AND ca.stock_id=? AND ca.branch_id = ? LIMIT 1";

        Object[] param = new Object[]{stockId, branch.getId()};
        try {
            return getJdbcTemplate().queryForObject(sql, param, new ContractArticlesMapper());
        } catch (Exception e) {
            return new ContractArticles();
        }
    }

}
