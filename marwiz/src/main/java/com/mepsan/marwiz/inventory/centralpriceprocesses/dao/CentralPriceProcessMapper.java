/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.06.2020 11:56:18
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CentralPriceProcessMapper implements RowMapper<CentralPriceProcess> {

    @Override
    public CentralPriceProcess mapRow(ResultSet rs, int i) throws SQLException {
        CentralPriceProcess centralPriceProcess = new CentralPriceProcess();
        centralPriceProcess.setId(rs.getInt("id"));
        centralPriceProcess.getPriceListItem().getStock().setId(rs.getInt("stckid"));
        centralPriceProcess.getPriceListItem().getStock().setBarcode(rs.getString("stckbarcode"));
        centralPriceProcess.getPriceListItem().getStock().setName(rs.getString("stckname"));
        centralPriceProcess.getPriceListItem().getCurrency().setId(1);//TL
        centralPriceProcess.getPriceListItem().getCurrency().setTag(rs.getString("currencytag"));
        centralPriceProcess.getPriceListItem().setIs_taxIncluded(true);
        return centralPriceProcess;
    }

}
