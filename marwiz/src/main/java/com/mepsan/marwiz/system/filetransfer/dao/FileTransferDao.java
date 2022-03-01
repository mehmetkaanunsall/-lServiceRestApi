/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 15.04.2019 10:18:39
 */
package com.mepsan.marwiz.system.filetransfer.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.dao.MarketShiftMapper;
import com.mepsan.marwiz.general.model.general.Shift;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class FileTransferDao extends JdbcDaoSupport implements IFileTransferDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public String listOfSale(String where) {
        String sql = "SELECT  array_to_json(array_agg(to_json(saledetail) )) r_result\n"
                  + "FROM (\n"
                  + "SELECT                          \n"
                  + "     sl.id AS slid , \n"
                  + "     sl.branch_id AS slbranch_id,\n"
                  + "     sl.userdata_id AS sluserdata_id, \n"
                  + "     sl.is_return AS slis_return,\n"
                  + "     COALESCE(sl.pointofsale_id,0) AS slpointofsale_id,\n"
                  + "      COALESCE(pos.code , '0') AS poscode, \n "
                  + "     sl.processdate AS slprocessdate, \n"
                  + "     COALESCE(sl.receipt_id,0) AS slreceipt_id, \n"
                  + "     COALESCE( sl.invoice_id,0) AS slinvoice_id, \n"
                  + "     inv.documentserial || inv.documentnumber AS invoiceserialno ,\n"
                  + "     sl.account_id AS slaccount_id,\n"
                  + "     COALESCE(sl.totaldiscount,0) AS sltotaldiscount,\n"
                  + "     sl.totalmoney AS  sltotalmoney,\n"
                  + "     sl.totalprice AS sltotalprice,\n"
                  + "     sl.totaltax AS sltotaltax,\n"
                  + "     sl.currency_id AS slcurrency_id,\n"
                  + "    (\n"
                  + "        SELECT \n"
                  + "            array_agg(row_to_json(subsaleitem)) as saleitem\n"
                  + "        FROM	\n"
                  + "        (\n"
                  + "            SELECT \n"
                  + "                COALESCE(sli.discountprice,0) AS slidiscountprice,\n"
                  + "                sli.discountrate AS slidiscountrate,\n"
                  + "                sli.is_managerdiscount AS sliis_managerdiscount,\n"
                  + "                sli.manageruserdata_id AS slimanageruserdata_id ,\n"
                  + "                sli.quantity AS sliquantity,\n"
                  + "                sli.stock_id AS slistock_id,\n"
                  + "                stck.name AS stckname,\n"
                  + "                stck.centerstock_id AS  stckcentersctock_id, \n"
                  + "                stck.is_service AS stckis_service, \n"
                  + "                stck.barcode AS stckbarcode, \n"
                  + "                COALESCE( stck.code,'') AS stckcode , \n"
                  + "                sli.taxrate AS slitaxrate,\n"
                  + "                COALESCE(sli.exchangerate ,1) AS  sliexchangerate,\n"
                  + "                ((sli.totalmoney - ((sli.totalmoney * COALESCE(sl1.discountrate, 0))/100)) * COALESCE(sli.exchangerate, 1)) AS slitotalmoney,\n"
                  + "                ((sli.totalprice - ((sli.totalprice * COALESCE(sl1.discountrate, 0))/100)) * COALESCE(sli.exchangerate, 1)) AS slitotalprice,\n"
                  + "                sli.totaltax  AS slitotaltax ,\n"
                  + "                (((sli.totalmoney - ((sli.totalmoney * COALESCE(sl1.discountrate, 0))/100)) * COALESCE(sli.exchangerate, 1)) / COALESCE(quantity,1)) AS sliunitprice,\n"
                  + "                unt.id AS untid, \n"
                  + "                unt.name AS untname,      \n"
                  + "                unt.sortname AS untsortname,      \n"
                  + "                COALESCE(unt.unitrounding ,1)AS untunitrounding ,\n"
                  + "                unt.centerunit_id AS  untcenterunit_id \n"
                  + "            FROM \n"
                  + "                general.saleitem sli\n"
                  + "                INNER JOIN general.sale sl1 ON(sl1.id = sli.sale_id AND sl1.deleted=FALSE)\n"
                  + "                INNER JOIN inventory.stock stck ON(stck.id = sli.stock_id AND stck.deleted = FALSE) \n"
                  + "                LEFT JOIN general.unit unt ON(unt.id = stck.unit_id AND unt.deleted = FALSE)\n"
                  + "                WHERE \n"
                  + "                sli.sale_id = sl.id AND sli.deleted = FALSE\n"
                  + "                                                \n"
                  + "         ) subsaleitem\n"
                  + "      ),\n"
                  + "      (\n"
                  + "        SELECT \n"
                  + "            array_agg(row_to_json(subsalepayment)) as salepayment\n"
                  + "        FROM	\n"
                  + "        (\n"
                  + "            SELECT \n"
                  + "                slp.currency_id AS slpcurrency_id,\n"
                  + "                 cr.code AS crcode,\n"
                  + "                COALESCE(slp.exchangerate,1) AS slpexchangerate,\n"
                  + "                (slp.price * COALESCE(slp.exchangerate ,1)) AS slpprice,\n"
                  + "                (slp.price) AS slpprice2,\n"
                  + "                slp.type_id AS slptype_id\n"
                  + "            FROM \n"
                  + "                general.salepayment slp \n"
                  + "                LEFT JOIN system.currency cr ON( cr.id = slp.currency_id ANd cr.deleted = FALSE)\n"
                  + "            WHERE \n"
                  + "                slp.sale_id = sl.id AND slp.deleted = FALSE \n"
                  + "                                                \n"
                  + "         ) subsalepayment\n"
                  + "      )\n"
                  + "                                          \n"
                  + "FROM \n"
                  + "    general.sale sl\n"
                  + "    LEFT JOIN general.account acc ON (sl.account_id = acc.id AND acc.deleted = FALSE)\n"
                  + "    LEFT JOIN finance.invoice inv ON(inv.id = sl.invoice_id AND inv.deleted = FALSE) \n"
                  + "     LEFT JOIN general.pointofsale pos ON(pos.id = sl.pointofsale_id AND pos.deleted = FALSE) \n"
                  + "WHERE \n"
                  + "   	sl.deleted = FALSE  AND sl.branch_id = ?  " + where + " \n"
                  + "ORDER BY sl.id DESC     \n"
                  + ") saledetail";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, String.class);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Shift> listOfShift(String where) {
        String sql = "SELECT \n"
                  + "    shf.id AS shfid,\n"
                  + "    shf.shiftno AS shfshiftno\n"
                  + "FROM general.shift shf \n"
                  + "WHERE shf.deleted=False  AND shf.branch_id=?   \n"
                  + where + "  \n"
                  + " ORDER BY shf.id DESC  \n";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId()};

        List<Shift> result = getJdbcTemplate().query(sql, param, new MarketShiftMapper());
        return result;
    }

}
