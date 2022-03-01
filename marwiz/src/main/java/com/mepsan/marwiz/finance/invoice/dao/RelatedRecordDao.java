/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 31.01.2018 13:38:09
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import java.util.List;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class RelatedRecordDao extends JdbcDaoSupport implements IRelatedRecordDao {

    @Override
    public List<RelatedRecord> listOfRelatedRecords(Invoice invoice) {
        String sql = "SELECT\n"
                  + "  row_number() over(ORDER BY tt.processdate DESC) as id,\n"
                  + "  *\n"
                  + "FROM\n"
                  + "(\n"
                  + "  SELECT\n"
                  + "        wb.id as rowid,\n"
                  + "        COALESCE(wb.documentserial,'')||wb.documentnumber as documentnumber,\n"
                  + "        wb.waybilldate as processdate,\n"
                  + "        0 as type								--İrsaliye\n"
                  + "  FROM finance.waybillitem_invoiceitem_con wic\n"
                  + "  INNER JOIN finance.invoiceitem invi ON(invi.id = wic.invoiceitem_id AND invi.deleted = FALSE)\n"
                  + "  INNER JOIN finance.waybillitem wbi ON(wbi.id = wic.waybillitem_id AND wbi.deleted = FALSE)\n"
                  + "  INNER JOIN finance.waybill wb ON(wb.id = wbi.waybill_id AND wb.deleted = FALSE)\n"
                  + "  WHERE wic.deleted = FALSE\n"
                  + "  AND invi.invoice_id = ?\n"
                  + "  GROUP BY wb.id,wb.waybilldate,COALESCE(wb.documentserial,'')||wb.documentnumber\n"
                  + "      \n"
                  + "  UNION ALL\n"
                  + "  SELECT\n"
                  + "        ord.id as rowid,\n"
                  + "        COALESCE(ord.documentserial,'')||ord.documentnumber as documentnumber,\n"
                  + "        ord.orderdate as processdate,\n"
                  + "        4 as type               -- Sipariş\n"
                  + "  FROM finance.waybillitem_invoiceitem_con wic\n"
                  + "  INNER JOIN finance.invoiceitem invi ON(invi.id = wic.invoiceitem_id AND invi.deleted = FALSE)\n"
                  + "  INNER JOIN finance.waybillitem wbi ON(wbi.id = wic.waybillitem_id AND wbi.deleted = FALSE)\n"
                  + "  INNER JOIN finance.orderitem_waybillitem_con owc ON(owc.waybillitem_id = wbi.id AND owc.deleted = FALSE)\n"
                  + "  INNER JOIN finance.orderitem ordi ON (ordi.id = owc.orderitem_id AND ordi.deleted = FALSE)\n"
                  + "  INNER JOIN finance.order ord ON (ord.id = ordi.order_id AND ord.deleted = FALSE )\n"
                  + "  WHERE wic.deleted = FALSE "
                  + "  AND invi.invoice_id= ?\n"
                  + "  GROUP BY ord.id,ord.orderdate,COALESCE(ord.documentserial,'')||ord.documentnumber\n"
                  + "  UNION ALL \n"
                  + "  SELECT \n"
                  + "        inv.id as rowid,\n"
                  + "        COALESCE(inv.documentserial,'')||inv.documentnumber as documentnumber,\n"
                  + "        inv.invoicedate as processdate,\n"
                  + "        5 as type                  -- Fiyat Farkı\n"
                  + "  FROM finance.invoice inv\n"
                  + "  WHERE inv.deleted = FALSE AND inv.differentinvoice_id = ?\n"
                  + "  GROUP BY inv.id, inv.invoicedate,COALESCE(inv.documentserial,'')||inv.documentnumber\n"
                  + "  UNION ALL \n"
                  + "  SELECT \n"
                  + "        inv1.id as rowid,\n"
                  + "        COALESCE(inv1.documentserial,'')||inv1.documentnumber as documentnumber,\n"
                  + "        inv1.invoicedate as processdate,\n"
                  + "        6 as type                  -- Fiyat Farkının faturası\n"
                  + "  FROM finance.invoice inv\n"
                  + "  LEFT JOIN finance.invoice inv1 ON(inv1.id = inv.differentinvoice_id AND inv1.deleted=FALSE)\n"
                  + "  WHERE inv.deleted = FALSE AND inv.differentinvoice_id = ?\n"
                  + "  GROUP BY inv1.id, inv1.invoicedate,COALESCE(inv1.documentserial,'')||inv1.documentnumber\n"
                  + "  UNION ALL\n"
                  + "      \n"
                  + "  SELECT\n"
                  + "      CASE WHEN crd.id IS NOT NULL THEN crd.id \n"
                  + "           WHEN chq.id IS NOT NULL THEN chq.id \n"
                  + "      END as rowid,\n"
                  + "      CASE WHEN crd.id IS NOT NULL THEN ''\n"
                  + "           WHEN chq.id IS NOT NULL THEN  COALESCE(chq.documentserial,'')||chq.documentnumber\n"
                  + "      END as documentnumber,\n"
                  + "      CASE WHEN crd.id IS NOT NULL THEN crd.duedate \n"
                  + "           WHEN chq.id IS NOT NULL THEN chq.expirydate\n"
                  + "      END as processdate,\n"
                  + "      CASE WHEN ip.credit_id IS NOT NULL THEN 1				--Kredi\n"
                  + "           WHEN ip.chequebill_id IS NOT NULL AND chq.is_cheque THEN 2	--Çek\n"
                  + "           ELSE 3 								--Senet\n"
                  + "      END as type\n"
                  + "  FROM finance.invoicepayment ip\n"
                  + "  INNER JOIN finance.invoice inv ON (inv.id = ip.invoice_id AND inv.deleted = FALSE)\n"
                  + "  LEFT JOIN finance.credit crd ON (crd.id = ip.credit_id AND crd.deleted = FALSE)\n"
                  + "  LEFT JOIN finance.chequebill chq ON (chq.id = ip.chequebill_id AND chq.deleted = FALSE)\n"
                  + "  WHERE ip.deleted = FALSE\n"
                  + "  AND ip.invoice_id = ?\n"
                  + "  AND inv.is_periodinvoice = FALSE\n"
                  + "  AND (ip.credit_id IS NOT NULL OR ip.chequebill_id IS NOT NULL)\n"
                  + "\n"
                  + "  UNION ALL\n"
                  + "\n"
                  + "  SELECT DISTINCT\n"
                  + "      crd.id as rowid,\n"
                  + "      '' as documentnumber,\n"
                  + "      crd.duedate as processdate,\n"
                  + "      1 as type\n"
                  + "  FROM general.sale sl\n"
                  + "  INNER JOIN finance.invoice inv ON (inv.id = sl.invoice_id AND inv.deleted = FALSE)\n"
                  + "  INNER JOIN general.salepayment slp ON (sl.id = slp.sale_id AND slp.deleted = FALSE)\n"
                  + "  INNER JOIN finance.credit crd ON (crd.id = slp.credit_id AND crd.deleted = FALSE)\n"
                  + "  WHERE sl.deleted = FALSE\n"
                  + "  AND sl.invoice_id = ?\n"
                  + "  AND inv.is_periodinvoice = TRUE\n"
                  + "\n"
                  + ") as tt\n"
                  + "ORDER BY tt.processdate DESC";

        Object[] param = new Object[]{invoice.getId(), invoice.getId(), invoice.getId(), invoice.getPriceDifferenceInvoice().getId(), invoice.getId(), invoice.getId()};
        return getJdbcTemplate().query(sql, param, new RelatedRecordMapper());

    }
}
