/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.11.2019 10:40:28
 */
package com.mepsan.marwiz.general.responsible.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Responsible;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class ResponsibleDao extends JdbcDaoSupport implements IResponsibleDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<Responsible> findResponsible(Responsible responsible, int type) {

        String sql = "";
        Object[] param = null;

        if (type == 1) {//Cari Yetkili
            sql = "SELECT \n"
                      + "	 rsp.id AS rspid,\n"
                      + "    rsp.name AS rspname,\n"
                      + "    rsp.surname AS rspsurname,\n"
                      + "    rsp.identityno AS rspidentityno,\n"
                      + "    rsp.birthdate AS rspbirthdate,\n"
                      + "    rsp.position AS rspposition,\n"
                      + "    rsp.gender AS rspgender,\n"
                      + "    addr.fulladdress AS addrfulladdress,\n"
                      + "    phn.tag AS phntag,\n"
                      + "    intr.tag AS intrtag\n"
                      + "FROM\n"
                      + "	general.responsible rsp \n"
                      + "    LEFT JOIN general.address addr ON (addr.responsible_id = rsp.id AND addr.deleted = FALSE AND addr.is_default = TRUE) \n"
                      + "    LEFT JOIN general.phone phn ON (phn.responsible_id = rsp.id AND phn.deleted = FALSE AND phn.is_default = TRUE AND phn.type_id = 90) \n"
                      + "    LEFT JOIN general.internet intr ON (intr.responsible_id = rsp.id AND intr.deleted = FALSE AND intr.is_default = TRUE AND intr.type_id = 85) \n"
                      + "WHERE rsp.deleted=FALSE AND rsp.account_id =?";

            param = new Object[]{responsible.getAccount().getId()};
        } else if (type == 2 || type == 3) {//Banka Şubesi Yetkili - Banka Şube tanımları
            sql = "SELECT \n"
                      + "	 rsp.id AS rspid,\n"
                      + "    rsp.name AS rspname,\n"
                      + "    rsp.surname AS rspsurname,\n"
                      + "    rsp.identityno AS rspidentityno,\n"
                      + "    rsp.birthdate AS rspbirthdate,\n"
                      + "    rsp.position AS rspposition,\n"
                      + "    rsp.gender AS rspgender,\n"
                      + "    addr.fulladdress AS addrfulladdress,\n"
                      + "    phn.tag AS phntag,\n"
                      + "    intr.tag AS intrtag\n"
                      + "FROM\n"
                      + "	general.responsible rsp \n"
                      + "    LEFT JOIN general.address addr ON (addr.responsible_id = rsp.id AND addr.deleted = FALSE AND addr.is_default = TRUE) \n"
                      + "    LEFT JOIN general.phone phn ON (phn.responsible_id = rsp.id AND phn.deleted = FALSE AND phn.is_default = TRUE AND phn.type_id = 90) \n"
                      + "    LEFT JOIN general.internet intr ON (intr.responsible_id = rsp.id AND intr.deleted = FALSE AND intr.is_default = TRUE AND intr.type_id = 85) \n"
                      + "WHERE rsp.deleted=FALSE AND rsp.bankbranch_id =?";

            param = new Object[]{responsible.getBankBranch().getId()};
        }
        List<Responsible> result = getJdbcTemplate().query(sql, param, new ResponsibleMapper());
        return result;
    }

    @Override
    public int create(Responsible obj) {
        String sql = "INSERT INTO general.responsible (account_id, bankbranch_id, name, surname, identityno, birthdate, position, gender, c_id, u_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";

        Object[] param = new Object[]{obj.getAccount().getId() == 0 ? null : obj.getAccount().getId(), obj.getBankBranch().getId() == 0 ? null : obj.getBankBranch().getId(),
            obj.getName(), obj.getSurname(), obj.getIdentityNo(), obj.getBirthdate(), obj.getPosition(), obj.isGender(),
            sessionBean.getUser().getId(), sessionBean.getUser().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(Responsible obj) {
        String sql = "UPDATE \n"
                  + "	general.responsible \n"
                  + "SET \n"
                  + "	 name = ?,\n"
                  + "	 surname = ?,\n"
                  + "	 identityno = ?,\n"
                  + "	 birthdate = ?,\n"
                  + "	 position = ?,\n"
                  + "	 gender = ?,\n"
                  + "    u_id = ?,\n"
                  + "    u_time = NOW()\n"
                  + "WHERE id= ? AND deleted=FALSE";

        Object[] param = new Object[]{obj.getName(), obj.getSurname(), obj.getIdentityNo(), obj.getBirthdate(), obj.getPosition(), obj.isGender(),
            sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(Responsible obj) {
        String sql = "UPDATE general.responsible SET deleted = TRUE, u_id = ?, d_time = NOW() WHERE id= ? AND deleted=FALSE;\n"
                  + "UPDATE general.phone SET deleted = True, u_id = ?, d_time=NOW() WHERE responsible_id =  ? AND deleted=FALSE;\n"
                  + "UPDATE general.address SET deleted = True, u_id = ?, d_time=NOW() WHERE responsible_id =  ? AND deleted=FALSE;\n"
                  + "UPDATE general.internet SET deleted = True, u_id = ?, d_time=NOW() WHERE responsible_id =  ? AND deleted=FALSE;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId(), sessionBean.getUser().getId(), obj.getId(),
            sessionBean.getUser().getId(), obj.getId(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public Responsible findCommunications(Responsible obj) {
        String sql = "SELECT * FROM general.find_communication ( ? ,? , ? ) ";

        Object[] param = new Object[]{obj.getId(), 3, sessionBean.getUser().getLanguage().getId()};
        return getJdbcTemplate().queryForObject(sql, param, new ResponsibleCommunicationMapper(obj));
    }

}
