/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.finance.invoice.dao.*;
import com.mepsan.marwiz.general.model.finance.Order;
import java.util.List;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author ebubekir.buker
 */
public class OrderRelatedRecordDao extends JdbcDaoSupport implements IOrderRelatedRecordDao {

    @Override
    public List<OrderRelatedRecord> listOfOrderRelatedRecords(Order order) {
        String Sql
                  = "SELECT \n"
                  + "   row_number() over (ORDER BY tt.processdate DESC)as id,\n"
                  + "   tt.documentnumber as documentnumber,\n"
                  + "   tt.processdate as processdate,\n"
                  + "   tt.type as type,\n"
                  + "   tt.rowid as rowid\n"
                  + "FROM\n"
                  + "(\n"
                  + "   SELECT \n"
                  + "       wyb.id as rowid,\n"
                  + "       COALESCE(wyb.documentserial,'')||wyb.documentnumber as documentnumber,\n"
                  + "       wyb.waybilldate as processdate,\n"
                  + "       0 as type\n"
                  + "   FROM \n"
                  + "   finance.order fo \n"
                  + "   INNER JOIN finance.orderitem foi on (foi.order_id = fo.id AND foi.deleted = FALSE)    \n"
                  + "   INNER JOIN finance.orderitem_waybillitem_con owc ON (foi.id = owc.orderitem_id AND owc.deleted = FALSE)\n"
                  + "   INNER JOIN finance.waybillitem wybt on (owc.waybillitem_id=wybt.id and wybt.deleted=false)\n"
                  + "   INNER JOIN finance.waybill wyb on (wybt.waybill_id=wyb.id and wyb.deleted=FALSE)\n"
                  + "   WHERE fo.deleted = FALSE\n"
                  + "         AND fo.id = ?\n"
                  + "   GROUP BY wyb.id,\n"
                  + "            COALESCE(wyb.documentserial,'')||wyb.documentnumber,\n"
                  + "            wyb.waybilldate\n"
                  + "   UNION ALL\n"
                  + "   SELECT \n"
                  + "       fi.id as rowid,\n"
                  + "       COALESCE(fi.documentserial,'')||fi.documentnumber as documentnumber,\n"
                  + "       fi.invoicedate as processdate,\n"
                  + "       1 as type\n"
                  + "   FROM finance.order fo \n"
                  + "   INNER JOIN finance.orderitem foi on (foi.order_id = fo.id AND foi.deleted = FALSE) \n"
                  + "   INNER JOIN finance.orderitem_waybillitem_con owc ON (foi.id = owc.orderitem_id AND owc.deleted = FALSE)\n"
                  + "   INNER JOIN finance.waybillitem fwi ON (fwi.id = owc.waybillitem_id AND fwi.deleted = FALSE)\n"
                  + "   INNER JOIN finance.waybillitem_invoiceitem_con fwic on (fwic.waybillitem_id = fwi.id AND fwic.deleted = FALSE)\n"
                  + "   INNER JOIN finance.invoiceitem fii ON (fii.id = fwic.invoiceitem_id AND fii.deleted = FALSE)\n"
                  + "   INNER JOIN finance.invoice fi ON (fi.id = fii.invoice_id AND fi.deleted = FALSE)\n"
                  + "   WHERE fo.deleted = FALSE\n"
                  + "         AND fo.id = ?\n"
                  + "   GROUP BY fi.id,\n"
                  + "            COALESCE(fi.documentserial,'')||fi.documentnumber,\n"
                  + "            fi.invoicedate\n"
                  + ") AS tt\n"
                  + "ORDER BY tt.processdate DESC";

        Object[] param = new Object[]{order.getId(), order.getId()};
        return getJdbcTemplate().query(Sql, param, new OrderRelatedRecordMapper());

    }

}
