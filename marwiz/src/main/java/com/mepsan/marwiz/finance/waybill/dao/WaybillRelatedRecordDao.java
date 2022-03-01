/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:38:09
 */
package com.mepsan.marwiz.finance.waybill.dao;

import com.mepsan.marwiz.finance.invoice.dao.*;
import com.mepsan.marwiz.general.model.finance.Waybill;
import java.util.List;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class WaybillRelatedRecordDao extends JdbcDaoSupport implements IWaybillRelatedRecordDao {

    @Override
    public List<RelatedRecord> listOfRelatedRecords(Waybill waybill) {
        String sql = "SELECT\n"
                  + "  row_number() over(ORDER BY tt.processdate DESC) as id,\n"
                  + "      *\n"
                  + "      FROM(\n"
                  + "      SELECT\n"
                  + "      inv.id as rowid,\n"
                  + "      COALESCE(inv.documentserial,'')||inv.documentnumber as documentnumber,\n"
                  + "      inv.invoicedate as processdate,\n"
                  + "      0 as type              --irsaliye\n"
                  + "FROM finance.waybillitem_invoiceitem_con wic\n"
                  + "INNER JOIN finance.invoiceitem invi ON(invi.id = wic.invoiceitem_id AND invi.deleted = FALSE)\n"
                  + "INNER JOIN finance.waybillitem wbi ON(wbi.id = wic.waybillitem_id AND wbi.deleted = FALSE)\n"
                  + "INNER JOIN finance.invoice inv ON(inv.id = invi.invoice_id AND inv.deleted = FALSE)\n"
                  + "WHERE wic.deleted = FALSE\n"
                  + "AND wbi.waybill_id = ?\n"
                  + "GROUP BY inv.id,inv.invoicedate,COALESCE(inv.documentserial,'')||inv.documentnumber\n"
                  + "\n"
                  + "UNION ALL\n"
                  + "\n"
                  + "SELECT\n"
                  + "        od.id as rowid,\n"
                  + "        COALESCE(od.documentserial,'')||od.documentnumber as documentnumber,\n"
                  + "        od.orderdate as processdate,\n"
                  + "        1 as type               -- Sipariş\n"
                  + "        \n"
                  + "  FROM finance.orderitem_waybillitem_con owc\n"
                  + "  INNER JOIN finance.waybillitem wbi ON(wbi.id = owc.waybillitem_id AND wbi.deleted = FALSE)\n"
                  + "  INNER JOIN finance.orderitem ordi ON (ordi.id = owc.orderitem_id AND ordi.deleted = FALSE)\n"
                  + "  INNER JOIN finance.\"order\" od ON (od.id = ordi.order_id AND od.deleted = FALSE )\n"
                  + "  WHERE owc.deleted = FALSE  AND wbi.waybill_id= ?\n"
                  + "  GROUP BY od.id,od.orderdate,COALESCE(od.documentserial,'')||od.documentnumber\n"
                  + "  \n"
                  + ") as tt\n"
                  + "ORDER BY tt.processdate DESC";

        Object[] param = new Object[]{waybill.getId(), waybill.getId()};
        return getJdbcTemplate().query(sql, param, new RelatedRecordMapper());
    }

}
