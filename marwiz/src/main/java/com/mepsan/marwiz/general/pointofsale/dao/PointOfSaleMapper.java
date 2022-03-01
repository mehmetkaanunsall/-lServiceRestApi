/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:20:11
 */
package com.mepsan.marwiz.general.pointofsale.dao;

import com.mepsan.marwiz.general.model.general.PointOfSale;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PointOfSaleMapper implements RowMapper<PointOfSale> {

    @Override
    public PointOfSale mapRow(ResultSet rs, int i) throws SQLException {
        PointOfSale pointOfSale = new PointOfSale();

        pointOfSale.setId(rs.getInt("posid"));
        pointOfSale.setCode(rs.getString("poscode"));
        pointOfSale.setName(rs.getString("posname"));

        try {
            pointOfSale.setLocalIpAddress(rs.getString("poslocalipaddress"));
        } catch (Exception e) {
        }

        try {
            pointOfSale.setBrand(rs.getString("posbrand"));
            pointOfSale.setModel(rs.getString("posmodel"));
            pointOfSale.getStatus().setId(rs.getInt("posstatus_id"));
            pointOfSale.getStatus().setTag(rs.getString("sttdname"));
            pointOfSale.setSerialNumber(rs.getString("posserialnumber"));
            pointOfSale.setVersion(rs.getString("posversion"));
            pointOfSale.setSoftwareVersion(rs.getString("possoftwareversion"));
            pointOfSale.setPort(rs.getString("posport"));
            pointOfSale.getCashRegister().setId(rs.getInt("poscashregister_id"));
            pointOfSale.getCashRegister().setName(rs.getString("cashname"));
            pointOfSale.getWareHouse().setId(rs.getInt("poswarehouse_id"));
            pointOfSale.getWareHouse().setName(rs.getString("iwname"));
            pointOfSale.getSafe().setId(rs.getInt("possafe_id"));
            pointOfSale.getSafe().getCurrency().setId(rs.getInt("sfcurrency_id"));
            pointOfSale.setLocalIpAddress(rs.getString("poslocalipaddress"));
            pointOfSale.setIsOffline(rs.getBoolean("posis_offline"));
            pointOfSale.setStockTime(rs.getBigDecimal("posstocktime"));
            pointOfSale.setUnitTaxTime(rs.getBigDecimal("posunittaxtime"));
            pointOfSale.setUserTime(rs.getBigDecimal("posusertime"));
            pointOfSale.setCategorizationTime(rs.getBigDecimal("poscategorizationtime"));
            pointOfSale.setBankAccountTime(rs.getBigDecimal("posbankaccounttime"));
            pointOfSale.setPointOfSaleTime(rs.getBigDecimal("pospointofsaletime"));
            pointOfSale.setVendingMachineTime(rs.getBigDecimal("posvendingmachinetime"));
        } catch (Exception e) {
        }
        try {
            pointOfSale.setMacAddress(rs.getString("posmacaddress"));
            pointOfSale.setIpAddress(rs.getString("posipaddress"));
            pointOfSale.setIntegrationCode(rs.getString("posintegrationcode"));
        } catch (Exception e) {
        }
        try {
            pointOfSale.setWashingMachicneIntegrationCode(rs.getString("poswashingintegrationcode"));
        } catch (Exception e) {
        }

        return pointOfSale;
    }

}
