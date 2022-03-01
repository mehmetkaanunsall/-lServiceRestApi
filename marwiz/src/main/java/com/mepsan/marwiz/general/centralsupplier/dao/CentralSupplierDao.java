/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2020 10:57:17
 */
package com.mepsan.marwiz.general.centralsupplier.dao;

import com.mepsan.marwiz.general.model.general.CentralSupplier;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CentralSupplierDao extends JdbcDaoSupport implements ICentralSupplierDao {

    @Override
    public List<CentralSupplier> centralSupplierBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param, int supplierType) {
        String whereSupplierType = "";

        if (supplierType == 0) {
            whereSupplierType = " AND (cspp.centersuppliertype_id != 2 OR cspp.centersuppliertype_id IS NULL) ";
        } else if (supplierType == 1) {
            whereSupplierType = " AND (cspp.centersuppliertype_id NOT IN (1,2) OR cspp.centersuppliertype_id IS NULL) ";
        } else if (supplierType == 2) {
            whereSupplierType = " AND cspp.centersuppliertype_id = 1 ";

        }

        String sql = "SELECT \n"
                + "	cspp.id AS csppid,\n"
                + "   cspp.name AS csppname\n"
                + "FROM general.centralsupplier cspp\n"
                + "WHERE cspp.deleted=FALSE\n"
                + whereSupplierType + "\n"
                + where + "\n"
                + "ORDER BY cspp.name\n"
                + " limit " + pageSize + " offset " + first;

        return getJdbcTemplate().query(sql, new CentralSupplierMapper());
    }

    @Override
    public int centralSupplierBookCount(String where, String type, List<Object> paramd, int supplierType) {
        String whereSupplierType = "";

        if (supplierType == 0) {
            whereSupplierType = " AND (cspp.centersuppliertype_id != 2 OR cspp.centersuppliertype_id IS NULL) ";
        } else if (supplierType == 1) {
            whereSupplierType = " AND (cspp.centersuppliertype_id NOT IN (1,2) OR cspp.centersuppliertype_id IS NULL) ";
        } else if (supplierType == 2) {
            whereSupplierType = " AND cspp.centersuppliertype_id = 1 ";

        }
        String sql = "SELECT \n"
                + "	COUNT(cspp.id) AS csppid \n"
                + "FROM  general.centralsupplier cspp\n"
                + "WHERE cspp.deleted = false\n"
                + whereSupplierType + "\n"
                + where;

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public List<CentralSupplier> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        String sql = "SELECT \n"
                + "	cspp.id AS csppid,\n"
                + "   cspp.name AS csppname,\n"
                + "   1 AS tagquantity\n"
                + "FROM general.centralsupplier cspp\n"
                + "WHERE cspp.deleted=FALSE\n"
                + where + "\n"
                + "ORDER BY cspp.name\n"
                + " limit " + pageSize + " offset " + first;

        return getJdbcTemplate().query(sql, new CentralSupplierMapper());
    }

    @Override
    public int count(String where) {
        String sql = "SELECT \n"
                + "	COUNT(cspp.id) AS csppid \n"
                + "FROM  general.centralsupplier cspp\n"
                + "WHERE cspp.deleted = false\n"
                + where;

        int id = getJdbcTemplate().queryForObject(sql, Integer.class);
        return id;
    }

    @Override
    public List<CentralSupplier> findAllCentralSupplier(String where) {
        String sql = "SELECT \n"
                + "	cspp.id AS csppid,\n"
                + "   cspp.name AS csppname\n"
                + "FROM general.centralsupplier cspp\n"
                + "WHERE cspp.deleted=FALSE\n"
                + where + "\n"
                + "ORDER BY cspp.name";

        return getJdbcTemplate().query(sql, new CentralSupplierMapper());

    }

}
