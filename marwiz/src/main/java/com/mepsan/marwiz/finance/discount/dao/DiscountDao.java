/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 14:57:10
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DiscountDao extends JdbcDaoSupport implements IDiscountDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public int create(Discount obj) {
        String sql = "INSERT INTO \n"
                  + "  finance.discount\n"
                  + "(\n"
                  + "  name,\n"
                  + "  begindate,\n"
                  + "  enddate,\n"
                  + "  status_id,\n"
                  + "  description,\n"
                  + "  is_allcustomer,\n"
                  + "  is_invoice,\n"
                  + "  is_allbranch, \n"
                  + "  is_retailcustomer,\n"
                  + "  c_id,\n"
                  + "  u_id\n"
                  + ")\n"
                  + "VALUES ( ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ? ,?) RETURNING id;\n";

        Object[] param = new Object[]{obj.getName(), obj.getBeginDate(), obj.getEndDate(), obj.getStatus().getId(), obj.getDescription(), obj.isIsAllCustomer(), obj.isIsInvoice(), obj.isIsAllBranch(), obj.isIsRetailCustomer(), sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Discount obj) {
        String sql = "UPDATE \n"
                  + "  finance.discount \n"
                  + "SET \n"
                  + "  name = ?,\n"
                  + "  begindate = ?,\n"
                  + "  enddate = ?,\n"
                  + "  status_id = ?,\n"
                  + "  description = ?,\n"
                  + "  is_allcustomer = ?,\n"
                  + "  is_invoice = ?,\n"
                  + "  is_allbranch = ? ,\n"
                  + "  is_retailcustomer = ? , \n"
                  + "  u_id = ?,\n"
                  + "  u_time = now()\n"
                  + "WHERE \n"
                  + "  id = ?; \n";

        Object[] param = new Object[]{obj.getName(), obj.getBeginDate(), obj.getEndDate(), obj.getStatus().getId(), obj.getDescription(), obj.isIsAllCustomer(), obj.isIsInvoice(), obj.isIsAllBranch(), obj.isIsRetailCustomer(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int testBeforeDelete(Discount discount) {
        String sql = "SELECT CASE WHEN EXISTS (\n"
                  + "              SELECT discount_id FROM finance.discountitem WHERE discount_id=? AND deleted=False) THEN 1 \n"
                  + "                                  WHEN EXISTS (SELECT discount_id FROM finance.discount_branch_con WHERE discount_id=? AND deleted=False) THEN 1 \n"
                  + "                                  WHEN EXISTS (SELECT discount_id FROM finance.discount_account_con WHERE discount_id=? AND deleted=False) THEN 1 ELSE 0 END\n";

        Object[] param = new Object[]{discount.getId(), discount.getId(), discount.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Discount obj) {
        String sql = "UPDATE finance.discount SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public List<Discount> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        if (sortField == null) {
            sortField = "dsc.id";
            sortOrder = "desc";
        }

        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND dsc.centercampaign_id IS NULL  ";
        }

        String sql = "SELECT \n"
                  + "    dsc.id AS dscid,\n"
                  + "    dsc.name AS dscname,\n"
                  + "    dsc.begindate AS dscbegindate,\n"
                  + "    dsc.enddate AS dscenddate, \n"
                  + "    dsc.status_id AS dscstatus_id,\n"
                  + "    sttd.name AS sttdname,\n"
                  + "    dsc.description AS dscdescription,\n"
                  + "    dsc.is_allcustomer AS dscis_allcustomer, \n"
                  + "    dsc.is_invoice AS dscis_invoice, \n"
                  + "    dsc.is_allbranch AS dscis_allbranch , \n"
                  + "    dsc.is_retailcustomer AS dscis_retailcustomer , \n"
                  + "    dsc.centercampaign_id AS dsccentercampaign_id,\n"
                  + "    usr.username as usrusername, \n"
                  + "    dsc.c_time as dscc_time,\n"
                  + "    usr.name AS usrname,\n"
                  + "    usr.surname AS usrsurname\n"
                  + "FROM\n"
                  + "    finance.discount dsc \n"
                  + "    INNER JOIN system.status_dict sttd ON (sttd.status_id = dsc.status_id AND sttd.language_id = ?)\n"
                  + "    INNER JOIN general.userdata usr   ON (usr.id = dsc.c_id)\n"
                  + "WHERE \n"
                  + "    dsc.deleted = FALSE "
                  + where + "\n"
                  + " ORDER BY " + sortField + " " + sortOrder + "  \n"
                  + " limit " + pageSize + " offset " + first;
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        List<Discount> result = getJdbcTemplate().query(sql, param, new DiscountMapper());
        return result;

    }

    @Override
    public int count(String where) {

        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            where = where + " AND dsc.centercampaign_id IS NULL  ";
        }

        String sql = " SELECT \n"
                  + "    count(dsc.id) as dscid  \n"
                  + "   \n"
                  + " FROM\n"
                  + "    finance.discount dsc \n"
                  + "    INNER JOIN system.status_dict sttd ON (sttd.status_id = dsc.status_id AND sttd.language_id = ?)\n"
                  + "    INNER JOIN general.userdata usr   ON (usr.id = dsc.c_id)\n"
                  + "WHERE \n"
                  + "    dsc.deleted = FALSE  " + where;
        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId()};
        int result = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return result;
    }

}
