/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 11.02.2019 14:18:48
 */
package com.mepsan.marwiz.automation.nozzle.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class NozzleMovementDao extends JdbcDaoSupport implements INozzleMovementDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<NozzleMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        String sql = "SELECT \n"
                + "    shs.id AS shsid,\n"
                + "    shs.processdate AS shsprocessdate,\n"
                + "    shs.liter AS shsliter , \n"
                + "    shs.receiptno AS shsreceiptno,\n"
                + "    shs.nozzle_id AS shsnozzle_id,\n"
                + "    nz.name AS nzname,\n"
                + "    nz.nozzleno AS nznozzleno,\n"
                + "    shs.nozzleno AS shsnozzleno,\n"
                + "    shs.shift_id AS shsshift_id, \n"
                + "    shf.shiftno AS shfshiftno , \n"
                + "    shs.stock_id AS shsstock_id ,\n"
                + "    stck.name AS stckname,\n"
                + "    shs.c_id AS shsc_id,\n"
                + "    us.name AS usname,\n"
                + "    us.surname AS ussurname ,\n"
                + "    stck.unit_id AS stckunit_id,\n"
                + "    gunt.name AS guntname,\n"
                + "    gunt.sortname AS guntsortname,\n"
                + "    gunt.unitrounding AS guntunitrounding \n"
                + "FROM\n"
                + "	automation.shiftsale shs \n"
                + "     INNER JOIN automation.shift shf ON(shf.id = shs.shift_id AND shf.deleted = FALSE) \n"
                + "    INNER JOIN automation.nozzle nz ON(nz.id = shs.nozzle_id AND nz.deleted = FALSE)\n"
                + "    INNER JOIN  inventory.stock stck ON(stck.id = shs.stock_id AND stck.deleted = FALSE)\n"
                + "    INNER JOIN general.userdata us ON(us.id = shs.c_id)\n"
                + "    LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id AND gunt.deleted = FALSE) \n"
                + "WHERE \n"
                + " shs.deleted = FALSE  AND shf.branch_id = ? \n" + where
                + " limit " + pageSize + " offset " + first;

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};
        List<NozzleMovement> result = getJdbcTemplate().query(sql, param, new NozzleMovementMapper());
        return result;
    }

    @Override
    public int count(String where) {
        String sql = "SELECT COUNT(shs.id)\n"
                + "FROM\n"
                + "	automation.shiftsale shs\n"
                + "    INNER JOIN automation.nozzle nz ON(nz.id = shs.nozzle_id AND nz.deleted = FALSE)\n"
                + "    INNER JOIN  inventory.stock stck ON(stck.id = shs.stock_id AND stck.deleted = FALSE)\n"
                + "    INNER JOIN general.userdata us ON(us.id = shs.c_id)\n"
                + "    \n"
                + "WHERE\n"
                + " shs.deleted = FALSE \n" + where;

        Object[] param = new Object[]{};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

}
