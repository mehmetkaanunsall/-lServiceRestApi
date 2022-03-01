/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Mar 1, 2018 3:02:42 PM
 */
package com.mepsan.marwiz.general.documenttemplate.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DocumentTemplateDao extends JdbcDaoSupport implements IDocumentTemplateDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<DocumentTemplate> listOfDocumentTemplate() {
        String sql = "SELECT \n"
                + "  gdt.id AS gdtid,\n"
                + "  gdt.name AS gdtname,\n"
                + "  gdt.is_default AS gdtis_default,\n"
                + "  gdt.type_id AS gdttype_id,\n"
                + "  typ.name AS typname,\n"
                + "  gdt.papersize AS gdtpapersize,\n"
                + "  gdt.width AS gdtwidth,\n"
                + "  gdt.height AS gdtheight,\n"
                + "  gdt.m_top AS gdttop,\n"
                + "  gdt.m_bottom AS gdtbottom,\n"
                + "  gdt.m_left AS gdtleft,\n"
                + "  gdt.m_right AS gdtright,\n"
                + "  gdt.is_vertical AS gdtis_vertical,\n"
                + "  gdt.json AS gdtjson,\n"
                + "  gdt.is_usetemplate AS gdtis_usetemplate\n"
                + "FROM \n"
                + "  general.documenttemplate gdt \n"
                + "  INNER JOIN system.type_dict typ ON (typ.type_id = gdt.type_id AND typ.language_id = ?)   \n"
                + "WHERE\n"
                + "  gdt.deleted = FALSE AND gdt.branch_id = ? order by gdt.id";

        Object[] param = new Object[]{sessionBean.getUser().getLanguage().getId(), sessionBean.getUser().getLastBranch().getId()};
        List<DocumentTemplate> result = getJdbcTemplate().query(sql, param, new DocumentTemplateMapper());
        return result;
    }

    @Override
    public int create(DocumentTemplate obj) {
        Object[] param;
        String sql = "";
        if (obj.isIsDefault()) {
            sql = "\n"
                    + "    UPDATE \n"
                    + "       general.documenttemplate \n"
                    + "    SET \n"
                    + "        is_default = false,\n"
                    + "        u_id = ?, \n"
                    + "        u_time = now()\n"
                    + "     WHERE\n"
                    + "       deleted = false AND is_default = true and type_id = ? and branch_id = ? RETURNING id; \n";
            param = new Object[]{sessionBean.getUser().getId(), obj.getType().getId(), sessionBean.getUser().getLastBranch().getId()};

            try {
                getJdbcTemplate().queryForObject(sql, param, Integer.class);
            } catch (DataAccessException e) {
            }

        }

        sql = "INSERT INTO general.documenttemplate (branch_id, name, is_default, type_id, papersize, width, height, is_vertical, json, is_usetemplate, c_id, u_id , m_top , m_bottom , m_left , m_right)\n"
                + "SELECT \n"
                + " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? \n"
                + "WHERE ((? = false) OR (? = true AND NOT EXISTS(SELECT id FROM general.documenttemplate WHERE deleted = false AND is_default = true AND type_id = ? AND branch_id = ?))) RETURNING id; ";

        param = new Object[]{sessionBean.getUser().getLastBranch().getId(), obj.getName(), obj.isIsDefault(), obj.getType().getId(), obj.getPaperSize(), obj.getWidth(), obj.getHeight(),
            obj.isIsVertical(), obj.getJson(), false, sessionBean.getUser().getId(), sessionBean.getUser().getId(),
            obj.getMargin_top(), obj.getMargin_bottom(), obj.getMargin_left(), obj.getMargin_right(),
            obj.isIsDefault(), obj.isIsDefault(), obj.getType().getId(), sessionBean.getUser().getLastBranch().getId()};

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
    public int update(DocumentTemplate obj
    ) {
        String sql = "UPDATE general.documenttemplate \n"
                + "SET\n"
                + "name = ?, "
                + "is_default = ?, "
                + "papersize = ?, "
                + "width = ?, "
                + "height = ?, "
                + "m_top = ?, "
                + "m_bottom = ?, "
                + "m_left = ?, "
                + "m_right = ?, "
                + "is_vertical = ?, "
                + "json = ?, "
                + "is_usetemplate = ?,"
                + "u_id = ? ,"
                + "u_time = now() "
                + "WHERE id = ? AND \n"
                + " ((? = false) OR (? = true AND NOT EXISTS(SELECT id FROM general.documenttemplate WHERE deleted = false AND is_default = true AND type_id = ? AND id <> ? AND branch_id = ?))) RETURNING id; ";

        Object[] param = new Object[]{obj.getName(), obj.isIsDefault(), obj.getPaperSize(), obj.getWidth(), obj.getHeight(),
            obj.getMargin_top(), obj.getMargin_bottom(), obj.getMargin_left(), obj.getMargin_right(),
            obj.isIsVertical(),
            obj.getJson(), obj.isIsUseTemplate(), sessionBean.getUser().getId(), obj.getId(), obj.isIsDefault(), obj.isIsDefault(), obj.getType().getId(), obj.getId(), sessionBean.getUser().getLastBranch().getId()};
        System.out.println(Arrays.asList(param));
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
    public int updateOnlyJson(DocumentTemplate obj
    ) {
        String sql = "UPDATE general.documenttemplate \n"
                + "SET\n"
                + "json = ?, "
                + "u_id = ? ,"
                + "u_time = now()\n"
                + "WHERE id = ? ";

        Object[] param = new Object[]{obj.getJson(), sessionBean.getUser().getId(), obj.getId()};

        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public DocumentTemplate bringInvoiceTemplate(int type_id
    ) {
        String sql = "SELECT \n"
                + "     gdt.id AS gdtid,\n"
                + "     gdt.papersize AS gdtpapersize,\n"
                + "     gdt.width AS gdtwidth,\n"
                + "     gdt.height AS gdtheight,\n"
                + "     gdt.m_top AS gdttop,\n"
                + "     gdt.m_bottom AS gdtbottom,\n"
                + "     gdt.m_left AS gdtleft,\n"
                + "     gdt.m_right AS gdtright,\n"
                + "     gdt.is_vertical AS gdtis_vertical,\n"
                + "     gdt.json AS gdtjson\n"
                + "  FROM \n"
                + "     general.documenttemplate gdt \n"
                + "  WHERE \n"
                + "     gdt.type_id = ? \n"
                + "     AND gdt.is_default = true \n"
                + "     AND gdt.deleted = false \n"
                + "     AND gdt.branch_id = ?";
        Object[] param = new Object[]{type_id, sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, new DocumentTemplateMapper());
        } catch (DataAccessException e) {
            return null;
        }

    }

    @Override
    public int delete(DocumentTemplate documentTemplate
    ) {

        String sql = "UPDATE general.documenttemplate set deleted=TRUE ,u_id=? , d_time=NOW()  WHERE deleted=FALSE AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), documentTemplate.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

}
