/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.entryexitsummaryreport.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author esra.cabuk
 */
public class EntryExitSummaryReportMapper implements RowMapper<EntryExitSummary> {

    @Override
    public EntryExitSummary mapRow(ResultSet rs, int i) throws SQLException {
        EntryExitSummary entryExitSummary = new EntryExitSummary();
        entryExitSummary.getStock().setId(rs.getInt("stckid"));
        entryExitSummary.getStock().setName(rs.getString("stckname"));
        entryExitSummary.getStock().setCode(rs.getString("stckcode"));
        entryExitSummary.getStock().setCenterProductCode(rs.getString("stckcenterproductcode"));
        entryExitSummary.getStock().setBarcode(rs.getString("stckbarcode"));
        entryExitSummary.getStock().getUnit().setSortName(rs.getString("guntsortname"));
        entryExitSummary.getStock().getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        entryExitSummary.getWarehouse().setId(rs.getInt("iwid"));
        entryExitSummary.getWarehouse().setName(rs.getString("iwname"));
        entryExitSummary.setEntryAmount(rs.getBigDecimal("entry"));
        entryExitSummary.setExitAmount(rs.getBigDecimal("exit"));
        entryExitSummary.setLastQuantity(rs.getBigDecimal("lastquantity"));
        entryExitSummary.setCategory(rs.getString("category"));
        entryExitSummary.setCategory(StaticMethods.findCategories(entryExitSummary.getCategory()));
        entryExitSummary.getStock().getBrand().setId(rs.getInt("stckbrand_id"));
        entryExitSummary.getStock().getBrand().setName(rs.getString("brname"));
        entryExitSummary.getStock().getSupplier().setId(rs.getInt("stcksupplier_id"));
        entryExitSummary.getStock().getSupplier().setName(rs.getString("accname"));
        entryExitSummary.getStock().getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
        entryExitSummary.getStock().getCentralSupplier().setName(rs.getString("csppname"));
        entryExitSummary.getBranch().setId(rs.getInt("iwbranch_id"));
        entryExitSummary.getBranch().setName(rs.getString("brcname"));

        return entryExitSummary;
    }

}
