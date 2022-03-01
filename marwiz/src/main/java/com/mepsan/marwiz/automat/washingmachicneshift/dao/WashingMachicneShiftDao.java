/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:01:27 PM
 */
package com.mepsan.marwiz.automat.washingmachicneshift.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WashingMachicneShiftDao extends JdbcDaoSupport implements IWashingMachicneShiftDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public AutomatShift controlOpenShift() {
        String sql = "SELECT\n"
                + "      shf.id as shfid,\n"
                + "      shf.shiftno as shfshiftno\n"
                + "      FROM wms.shift shf\n"
                + "      WHERE shf.deleted=FALSE AND shf.status_id=53 AND shf.branch_id=?";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, new WashingMachicneShiftMapper());
        } catch (EmptyResultDataAccessException e) {
            return new AutomatShift();
        }
    }

    @Override
    public int create(AutomatShift obj) {
        String sql = "SELECT r_shift_id FROM wms.process_shift (? , ?)";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -((SQLException) e.getCause()).getErrorCode();
        }
    }

    @Override
    public int update(AutomatShift obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<AutomatShift> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {

        String sql = "SELECT \n"
                + "       shf.id as shfid,\n"
                + "       shf.shiftno as shfshiftno,\n"
                + "       shf.begindate as shfbegindate,\n"
                + "       shf.enddate as shfenddate,\n"
                + "       shf.status_id as shfstatus_id,\n"
                + "       sttd.name as sttdname\n"
                + "       FROM wms.shift shf\n"
                + "       LEFT JOIN system.status_dict sttd ON(sttd.status_id=shf.status_id AND sttd.language_id=?)\n"
                + "       WHERE shf.deleted=FALSE AND shf.branch_id=? \n" + where + "\n"
                + "       ORDER BY shf.id DESC,shf.begindate DESC  \n"
                + " OFFSET " + first + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY \n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        List<AutomatShift> result = getJdbcTemplate().query(sql, param, new WashingMachicneShiftMapper());
        return result;

    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                + "       COUNT(shf.id) as shfid\n"
                + "       FROM wms.shift shf\n"
                + "       LEFT JOIN system.status_dict sttd ON(sttd.status_id=shf.status_id AND sttd.language_id=?)\n"
                + "       WHERE shf.deleted=FALSE AND shf.branch_id=? \n" + where + "\n";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;

    }

}
