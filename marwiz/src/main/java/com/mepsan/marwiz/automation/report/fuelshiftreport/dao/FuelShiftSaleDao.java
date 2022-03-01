package com.mepsan.marwiz.automation.report.fuelshiftreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author Samet Dağ
 */
public class FuelShiftSaleDao extends JdbcDaoSupport implements IFuelShiftSaleDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<FuelShiftSales> listPrintRecords(FuelShift fuelShift) {

        String sql = "SELECT \n"
                  + "    ss.attendantname ssattendant,\n"
                  + "    ss.attendantcode as ssattendantcode,\n"
                  + "    fst.name fstname,\n"
                  + "    fst.id fstid,\n   "
                  + "    fst.typeno fsttypneno,\n  "
                  + "    SUM(ss.totalmoney) sstotalmoney,\n"
                  + "    SUM(ss.liter) ssliter,\n"
                  + "    COUNT(ss.id) as salecount,\n"
                  + "    gunt.id AS guntid,\n"
                  + "    gunt.sortname AS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitrounding\n"
                  + "    FROM automation.shiftsale ss\n"
                  + "    JOIN automation.fuelsaletype fst ON(fst.typeno=ss.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "    LEFT JOIN inventory.stock stck ON(stck.id=ss.stock_id)\n"
                  + "    LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                  + "    WHERE ss.shift_id=? and ss.deleted=FALSE\n"
                  + "    GROUP BY ss.attendantname,fst.name,fst.id,fst.typeno,ss.attendantcode, gunt.id, gunt.sortname, gunt.unitrounding \n"
                  + "    ORDER BY ss.attendantname";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftSaleMapper());

        return result;

    }

    @Override
    public List<FuelShiftSales> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, FuelShift fuelShift) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ss.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ss.deleted = FALSE ";
        }

        String sql = "   SELECT \n"
                  + "      ss.id ssid,\n"
                  + "      ss.shift_id ssshiftno,\n"
                  + "      ss.processdate ssprocessdate,\n"
                  + "      ss.pumpno sspumpno,\n"
                  + "      ss.nozzleno ssnozzleno,\n"
                  + "      ss.stockname ssstckname,\n"
                  + "      ss.liter ssliter,\n"
                  + "      ss.price ssprice,\n"
                  + "      ss.discounttotal ssdistotal,\n"
                  + "      ss.totalmoney sstotalmoney,\n"
                  + "      ss.plate ssplate,\n"
                  + "      ss.attendantname ssattendant,\n"
                  + "      fst.name fstname,\n"
                  + "      ss.paymenttype sspaymenttype,\n"
                  + "      ss.attendantcode ssattendantcode,\n "
                  + "      ss.stockcode ssstockcode,\n"
                  + "      ss.accountcode ssaccountcode,\n"
                  + "      gunt.id AS guntid,\n"
                  + "      gunt.sortname AS guntsortname,\n"
                  + "      gunt.unitrounding AS guntunitrounding\n"
                  + "      FROM automation.shiftsale ss\n"
                  + "      INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ss.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "      LEFT JOIN inventory.stock stck ON(stck.id=ss.stock_id)\n"
                  + "      LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                  + "      WHERE ss.shift_id = ? " + where1 + "\n"
                  + where
                  + "      ORDER BY ss.processdate DESC"
                  + " limit " + pageSize + " offset " + first;
        System.out.println("----fuelShiftSaleDao---"+sql);

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftSaleMapper());

        return result;
    }

    @Override
    public int count(String where, FuelShift fuelShift) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ss.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ss.deleted = FALSE ";
        }

        String sql = "SELECT COUNT(ss.id)\n"
                  + "FROM automation.shiftsale ss\n"
                  + "INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ss.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "LEFT JOIN inventory.stock stck ON(stck.id=ss.stock_id)\n"
                  + "LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                  + "WHERE ss.shift_id =? " + where1 + "\n"
                  + where
                  + "\n";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        int id = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return id;
    }

    @Override
    public List<FuelShiftSales> totals(String where, FuelShift fuelShift) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ss.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ss.deleted = FALSE ";
        }

        String sql = "SELECT\n"
                  + "    COUNT(ss.id) AS ssid,\n"
                  + "    COALESCE(SUM(ss.liter),0) AS ssliter,\n"
                  + "    COALESCE(SUM(ss.totalmoney),0) AS sstotalmoney,\n"
                  + "    gunt.id AS guntid,\n"
                  + "    gunt.sortname aS guntsortname,\n"
                  + "    gunt.unitrounding AS guntunitrounding\n"
                  + "FROM automation.shiftsale ss\n"
                  + "   INNER JOIN automation.fuelsaletype fst ON(fst.typeno=ss.saletype AND fst.deleted=FALSE AND fst.branch_id=?)\n"
                  + "   LEFT JOIN inventory.stock stck ON(stck.id=ss.stock_id)\n"
                  + "   LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                  + "WHERE ss.shift_id =? " + where1 + "\n"
                  + where + "\n"
                  + "GROUP BY \n"
                  + "	 gunt.id,\n"
                  + "    gunt.sortname,\n"
                  + "    gunt.unitrounding";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), fuelShift.getId()};

        List<FuelShiftSales> result = getJdbcTemplate().query(sql, param, new FuelShiftSaleMapper());
        return result;
    }

    @Override
    public String exportData(String where, FuelShift fuelShift) {

        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = "";
        if (fuelShift.isIsDeleted()) {//Silinenler gelsin
            where1 = " AND '" + sd.format(fuelShift.getDeletedTime()) + "' <= ss.d_time ";
        } else {///Silinenler gelmesin
            where1 = " AND ss.deleted = FALSE ";
        }

        String sql = "SELECT \n"
                  + "ss.shift_id ssshiftno,\n"
                  + "ss.processdate ssprocessdate,\n"
                  + "ss.pumpno sspumpno,\n"
                  + "ss.nozzleno ssnozzleno,\n"
                  + "ss.stockname ssstckname,\n"
                  + "ss.liter ssliter,\n"
                  + "ss.price ssprice,\n"
                  + "ss.discounttotal ssdistotal,\n"
                  + "ss.totalmoney sstotalmoney,\n"
                  + "ss.plate ssplate,\n"
                  + "ss.attendantname ssattendant,\n"
                  + "ss.saletype sssaletype,\n"
                  + "ss.paymenttype sspaymenttype,\n"
                  + "fst.name fstname ,\n"
                  + "ss.attendantcode ssattendantcode,\n"
                  + "ss.stockcode ssstockcode,\n"
                  + "ss.accountcode ssaccountcode,\n"
                  + "gunt.id AS guntid,\n"
                  + "gunt.sortname AS guntsortname,\n"
                  + "gunt.unitrounding AS guntunitrounding\n"
                  + "FROM automation.shiftsale ss\n"
                  + " JOIN automation.fuelsaletype fst ON(fst.typeno=ss.saletype AND fst.deleted=FALSE AND fst.branch_id=" + sessionBean.getUser().getLastBranch().getId() + ")\n"
                  + " LEFT JOIN inventory.stock stck ON(stck.id=ss.stock_id)\n"
                  + " LEFT JOIN general.unit gunt ON(gunt.id = stck.unit_id)\n"
                  + "WHERE ss.shift_id = " + fuelShift.getId() + "" + where1 + "\n"
                  + where
                  + " ORDER BY ss.processdate DESC";

        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
