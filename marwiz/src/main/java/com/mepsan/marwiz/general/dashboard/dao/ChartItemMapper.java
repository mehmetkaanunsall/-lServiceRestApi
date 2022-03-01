/**
 * @author Esra ÇABUK
 * @date 21.06.2018 10:23:12
 */
package com.mepsan.marwiz.general.dashboard.dao;

import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.ChartItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ChartItemMapper implements RowMapper<ChartItem> {

    int charLimit;

    public ChartItemMapper() {
        charLimit = 15;
    }

    public ChartItemMapper(int charLimit) {
        this.charLimit = charLimit;
    }

    @Override
    public ChartItem mapRow(ResultSet rs, int i) throws SQLException {
        ChartItem chartItem = new ChartItem();
                    Unit unit = new Unit();

        try { //getMostStock  getreturnedStock
            String name = rs.getString("stckname");
            if (name != null && name.length() > charLimit) {
                name = name.substring(0, charLimit);
            }
            chartItem.setName1(name);
            chartItem.setNumber1(rs.getBigDecimal("sliquantity"));
            unit.setSortName(rs.getString("untsortname"));
            chartItem.setUnit(unit);
            

        } catch (Exception e) {

        }
        
        try {
            chartItem.setTypeId(rs.getInt("slistock_id"));  

        } catch (Exception e) {
        }
       
        try {
            
        } catch (Exception e) {
        }

        try {//getFuelStock
            chartItem.setName1(rs.getString("stckname"));
            chartItem.setNumber1(rs.getBigDecimal("stckibalance"));
            unit.setSortName(rs.getString("untsortname"));
            chartItem.setUnit(unit);

        } catch (Exception e) {

        }

        try {
            chartItem.setName1(rs.getString("shftno"));
            chartItem.setNumber1(rs.getBigDecimal("totalmoney"));
        } catch (Exception e) {
        }

        try { //getMostCustomersBySale
            String name = rs.getString("accname");
            if (name != null && name.length() > charLimit) {
                name = name.substring(0, charLimit);
            }
            chartItem.setName1(name);
            chartItem.setNumber1(rs.getBigDecimal("totalmoney"));
            chartItem.getCurrency1().setId(rs.getInt("sl_currencyid"));

        } catch (Exception e) {

        }

        try {//getSalesByCategorization
            String name = rs.getString("cgname");
            if (name != null && name.length() > charLimit) {
                name = name.substring(0, charLimit);
            }
            chartItem.setName1(name);
            chartItem.setNumber1(rs.getBigDecimal("sliquantity"));

        } catch (Exception e) {

        }

        try {//decreasingStock   

            chartItem.setNumber2(rs.getBigDecimal("stckiminstocklevel"));
            chartItem.setName1(rs.getString("sttckname"));
            chartItem.setNumber1(rs.getBigDecimal("sumquantity"));

        } catch (Exception e) {
        }

        try {//getSalesByCashier //Kaldırılan StokLar  
            String name = rs.getString("usname") + " " + rs.getString("ussurname");
            if (name != null && name.length() > charLimit) {
                name = name.substring(0, charLimit);
            }
            chartItem.setName1(name);
            chartItem.setNumber1(rs.getBigDecimal("totalmoney"));

        } catch (Exception e) {

        }
        
        try {
            chartItem.getCurrency1().setId(rs.getInt("sl_currencyid"));
        } catch (Exception e) {
        }
        

        try {//getSalesBySaleType
            chartItem.setName1(rs.getString("typdname"));
            chartItem.setBigDecimal1(rs.getBigDecimal("slpprice"));
            chartItem.getCurrency1().setId(rs.getInt("slcurrency_id"));
        } catch (Exception e) {
        }

        try {//getStockRequest
            chartItem.setName1(rs.getString("srtname"));
            chartItem.setName2(rs.getString("srtcode"));
            chartItem.setName3(rs.getString("srtbarcode"));
        } catch (Exception e) {
        }

        try {//getRecorveires and getPayments
            chartItem.setBigDecimal1(rs.getBigDecimal("remainingmoney"));
            chartItem.setTypeId(rs.getInt("type"));
            chartItem.getCurrency1().setId(rs.getInt("currency_id"));
            chartItem.setTypeId2(rs.getInt("expiredate"));
        } catch (Exception e) {
        }

        try {//getWeeklyCashFlow 
            chartItem.setBeginDate(rs.getTimestamp("typestartdate"));
            chartItem.setEndDate(rs.getTimestamp("typeenddate"));
            chartItem.setBigDecimal1(rs.getBigDecimal("totalentry"));
            chartItem.setBigDecimal2(rs.getBigDecimal("totaloutflow"));
        } catch (Exception e) {
        }

        try { // fiyatı değişen ürünler - getpricesvaryingproducts
            chartItem.setName1(rs.getString("stckname"));
            chartItem.setBeginDate(rs.getTimestamp("hstprocessdate"));
            chartItem.setBigDecimal1(rs.getBigDecimal("oldprice"));
            chartItem.setBigDecimal2(rs.getBigDecimal("newprice"));
            chartItem.getOldCurrency().setId(rs.getInt("oldcurrency_id"));
            chartItem.getNewCurrency().setId(rs.getInt("newcurrency_id"));
            


        } catch (Exception e) {
        }
        
        try {
            chartItem.setName2(rs.getString("usname"));
            chartItem.setName3(rs.getString("ussurname"));
        } catch (Exception e) {
        }

        try { //Vardiya Sayfası ödemeler grafiği
            String name = rs.getString("typdname");
            if (name != null && name.length() > charLimit) {
                name = name.substring(0, charLimit);
            }
            chartItem.setName1(name);
            chartItem.setNumber1(rs.getBigDecimal("shpsaleprice"));
            chartItem.setName2(rs.getString("crrcode"));

        } catch (Exception e) {

        }
        try {///Vardiya SAyfasında önceki vardiya ile kıyaslamak içn yapılan dialog
            chartItem.setNumber1(rs.getBigDecimal("shpsaleprice"));
            chartItem.setName2(rs.getString("crrcode"));
            chartItem.setTypeId(rs.getInt("shpshift_id"));
        } catch (Exception e) {

        }

        try { // Alışı yüksek ürünler widgeti
            chartItem.setTypeId(rs.getInt("stckid"));
            chartItem.setName1(rs.getString("itemname"));
            chartItem.setBigDecimal1(rs.getBigDecimal("purcprice"));
            chartItem.setBigDecimal2(rs.getBigDecimal("saleprice"));
            chartItem.getNewCurrency().setId(rs.getInt("purccurid"));
            chartItem.getOldCurrency().setId(rs.getInt("salecurid"));

        } catch (Exception e) {
        }

        try { // Ürün karlılıkları widgeti
            chartItem.setTypeId(rs.getInt("stckid"));
            chartItem.setName1(rs.getString("name"));
            chartItem.setNumber1(rs.getBigDecimal("onceki_kar"));
            chartItem.setNumber2(rs.getBigDecimal("simdiki_kar"));
        } catch (Exception e) {
        }

        try {//getSalesByBrand
            String name = rs.getString("brname");
            if (name != null && name.length() > charLimit) {
                name = name.substring(0, charLimit);
            }
            chartItem.setName1(name);
            chartItem.setNumber1(rs.getBigDecimal("sliquantity"));
            unit.setId(rs.getInt("sli_unitid"));
            unit.setSortName(rs.getString("untsortname"));
            chartItem.setUnit(unit);
   
        } catch (Exception e) {

        }

        try {
            chartItem.setNameOther(rs.getString("brsname"));
        } catch (Exception e) {
        }

        try {

            unit.setId(rs.getInt("untid"));
            unit.setSortName(rs.getString("untsortname"));

            chartItem.setUnit(unit);
        } catch (Exception e) {
        }

        try {
            chartItem.setName(rs.getString("stckname"));
            chartItem.setQuantity(rs.getBigDecimal("quantitiy"));

        } catch (Exception e) {
        }

        try {
            chartItem.setStockId(rs.getInt("stckid"));
            chartItem.setMonth(rs.getInt("groupName"));
        } catch (Exception e) {
        }

        try {
            chartItem.setTotalIncome(rs.getBigDecimal("income"));
        } catch (Exception e) {
        }
        try {
            chartItem.setUnitName(rs.getString("untsrotname"));

        } catch (Exception e) {
        }

        try {

            chartItem.setTotalExpense(rs.getBigDecimal("expense"));
            chartItem.setTotalWinnings(rs.getBigDecimal("winngins"));
            chartItem.setElectricQuantity(rs.getBigDecimal("electricquantitiy"));
            chartItem.setElectricOperationTime(rs.getBigDecimal("slelectricoperationtime"));
            chartItem.setElectricExpense(rs.getBigDecimal("electricexpense"));
            chartItem.setOperationTime(rs.getInt("sloperationtime"));

        } catch (Exception e) {
        }
        try {
            chartItem.setTotalElectricAmount(rs.getBigDecimal("elecquantity"));
        } catch (Exception e) {
        }
        try {

            chartItem.setWaterWorkingAmount(rs.getBigDecimal("waterworkingamount"));
            chartItem.setWaterWorkingTime(rs.getInt("waterworkingtime"));
            chartItem.setWaterExpense(rs.getBigDecimal("waterexpense"));
            chartItem.setWaterWaste(rs.getBigDecimal("waterwase"));

        } catch (Exception e) {
        }
        try { // sarfiyat grafiği için
            chartItem.setWaste(rs.getBigDecimal("waste"));

        } catch (Exception e) {
        }

        try {
            chartItem.setName1(rs.getString("acntname"));
            chartItem.setBigDecimal1(rs.getBigDecimal("inv_remainingmoney"));
            chartItem.getCurrency1().setId(rs.getInt("inv_currencyid"));
        } catch (Exception e) {
        }

      
        
        

        try {//pumper sales
            chartItem.setName1(rs.getString("sslattendantname"));
            chartItem.setNumber1(rs.getBigDecimal("ssltotalmoney"));
            chartItem.getCurrency1().setId(rs.getInt("br_currency"));

        } catch (Exception e) {

        }

        try {//ürün satışları
            chartItem.setNumber2(rs.getBigDecimal("slitotalmoney"));
            chartItem.getCurrency1().setId(rs.getInt("sli_currencyid"));
        } catch (Exception e) {
        }
        
       

        return chartItem;
    }

}
