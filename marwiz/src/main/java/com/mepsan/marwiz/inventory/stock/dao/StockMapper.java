/**
 *
 *
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 10:55:15
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StockMapper implements RowMapper<Stock> {
    
    @Override
    public Stock mapRow(ResultSet rs, int i) throws SQLException {
        
        Stock stock = new Stock();
        
        try {
            stock.setId(rs.getInt("stckid"));
            stock.setBarcode(rs.getString("stckbarcode"));
        } catch (Exception e) {
        }
        
        try {
            stock.setId(rs.getInt("stckid"));
            stock.getUnit().setId(rs.getInt("stckunitid"));
            stock.getUnit().setSortName(rs.getString("untsortname"));
            stock.getUnit().setUnitRounding(rs.getInt("untunitrounding"));
            stock.getStockInfo().setIsFuel(rs.getBoolean("stckiisfuel"));
            stock.setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setMaxStockLevel(rs.getBigDecimal("stckimaxstocklevel"));
            stock.getStockInfo().setBalance(rs.getBigDecimal("stckibalance"));
        } catch (Exception e) {
        }
        
        try {
            stock.getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("stckisalemandatorycurrency_id"));
            stock.getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("stckisalemandatoryprice"));
        } catch (Exception e) {
        }

        //-----------totals----------
        try {
            stock.setId(rs.getInt("stckid"));
            stock.getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("sicurrentpurchasepricewithoutkdv"));
            stock.getStockInfo().setCurrentSalePrice(rs.getBigDecimal("sicurrentsalepricewithkdv"));
            stock.getStockInfo().setPurchaseCount(rs.getBigDecimal("sipurchasecount"));
            stock.getStockInfo().setSaleCount(rs.getBigDecimal("sisalecount"));
            stock.setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            stock.getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("sicurrentpurchasecurrency_id"));
            stock.getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("sicurrentsalecurrency_id"));
            stock.getStockInfo().setCurrentPurchasePriceWithKdv(rs.getBigDecimal("sicurrentpurchasepricewithkdv"));
            stock.getStockInfo().setCurrentSalePriceWithoutKdv(rs.getBigDecimal("sicurrentsalepricewithoutkdv"));
            stock.getStockInfo().setAvailablePurchasePriceWithoutKdv(rs.getBigDecimal("availablepurchasepricewithoutkdv"));
            stock.getStockInfo().setAvailablePurchasePriceWithKdv(rs.getBigDecimal("availablepurchasepricewithkdv"));
            stock.getStockInfo().setAvailableSalePriceWithoutKdv(rs.getBigDecimal("availablesalepricewithoutkdv"));
            stock.getStockInfo().setAvailableSalePriceWithKdv(rs.getBigDecimal("availablesalepricewithkdv"));
            
        } catch (Exception e) {
        }

        //-----------book----------
        try {
            stock.setId(rs.getInt("stckid"));
            stock.setBarcode(rs.getString("stckbarcode"));
            stock.setName(rs.getString("stckname"));
            stock.setCode(rs.getString("stckcode"));
            
            stock.getUnit().setCenterunit_id(rs.getInt("guntcenterunit_id"));
            stock.getStockInfo().setRecommendedPrice(rs.getBigDecimal("sirecommendedprice"));
            stock.getStockInfo().setMinProfitRate(rs.getBigDecimal("siminprofitrate"));
            stock.getStockInfo().getCurrency().setId(rs.getInt("sicurrency_id"));
            stock.getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("sisalemandatoryprice"));
            stock.getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("sisalemandatorycurrency_id"));
            stock.getStockInfo().setMaxStockLevel(rs.getBigDecimal("simaxstocklevel"));
            stock.getStockInfo().setBalance(rs.getBigDecimal("sibalance"));
            
        } catch (Exception e) {
        }
        
        try {
            stock.setCenterProductCode(rs.getString("stckcenterproductcode"));
            
        } catch (Exception e) {
        }
        try {
            stock.setAvailableQuantity(rs.getBigDecimal("availablequantity"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setIsFuel(rs.getBoolean("siis_fuel"));
        } catch (Exception e) {
        }
        
        try {
            stock.getStockInfo().setPurchaseControlDate(rs.getTimestamp("sipurchasecontroldate"));
            
        } catch (Exception e) {
        }
        try {
            stock.getSupplier().setId(rs.getInt("stcksupplier_id"));
            stock.getSupplier().setName(rs.getString("accname"));
            stock.setSupplierProductCode(rs.getString("stcksupplierproductcode"));
        } catch (Exception e) {
        }
        try {
            stock.getCentralSupplier().setId(rs.getInt("stckcentralsupplier_id"));
            stock.getCentralSupplier().setName(rs.getString("csppname"));
            stock.setCentralSupplierProductCode(rs.getString("stckcentralsupplierproductcode"));
        } catch (Exception e) {
        }
        //-----------findall----------
        try {
            UserData createUserData = new UserData(rs.getInt("stckc_id"));
            stock.setUserCreated(createUserData);
            stock.setDateCreated(rs.getTimestamp("stckc_time"));
            stock.getUserCreated().setUsername(rs.getString("usrusername"));
            stock.getUserCreated().setName(rs.getString("usrname"));
            stock.getUserCreated().setSurname(rs.getString("usrsurname"));
            stock.getStockInfo().setIsQuickSale(rs.getBoolean("siis_quicksale"));
            stock.getStockInfo().setMinStockLevel(rs.getBigDecimal("siminstocklevel"));
            stock.getStockInfo().setRecommendedPrice(rs.getBigDecimal("sirecommendedprice"));
            stock.getStockInfo().getCurrency().setId(rs.getInt("sicurrency_id"));
            stock.setSaleKdv(rs.getBigDecimal("salekdv"));
            stock.setPurchaseKdv(rs.getBigDecimal("purchasekdv"));
            stock.setCategory(StaticMethods.findCategories(rs.getString("category")));
            stock.getStockInfo().getCurrency().setTag(rs.getString("crydname"));
            stock.setPurchasePriceListItem(new PriceListItem());
            stock.setSalePriceListItem(new PriceListItem());
            stock.getPurchasePriceListItem().setPrice(rs.getBigDecimal("purchaseprice"));
            stock.getPurchasePriceListItem().getCurrency().setId(rs.getInt("purchasecurrency_id"));
            stock.getPurchasePriceListItem().setIs_taxIncluded(rs.getBoolean("purchaseis_taxincluded"));
            stock.getSalePriceListItem().setPrice(rs.getBigDecimal("saleprice"));
            stock.getSalePriceListItem().getCurrency().setId(rs.getInt("salecurrency_id"));
            stock.getSalePriceListItem().setIs_taxIncluded(rs.getBoolean("saleis_taxincluded"));
            
            stock.getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
            stock.getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("sicurrentpurchasecurrency_id"));
            stock.getStockInfo().setCurrentSalePrice(rs.getBigDecimal("sicurrentsaleprice"));
            stock.getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("sicurrentsalecurrency_id"));
            stock.getStockInfo().setCurrentPurchasePriceWithKdv(rs.getBigDecimal("sicurrentpurchasepricewithkdv"));
            stock.getStockInfo().setCurrentSalePriceWithoutKdv(rs.getBigDecimal("sicurrentsalepricewithoutkdv"));
            stock.getStockInfo().setAvailablePurchasePriceWithoutKdv(rs.getBigDecimal("availablepurchasepricewithoutkdv"));
            stock.getStockInfo().setAvailablePurchasePriceWithKdv(rs.getBigDecimal("availablepurchasepricewithkdv"));
            stock.getStockInfo().setAvailableSalePriceWithoutKdv(rs.getBigDecimal("availablesalepricewithoutkdv"));
            stock.getStockInfo().setAvailableSalePriceWithKdv(rs.getBigDecimal("availablesalepricewithkdv"));
            stock.getStockInfo().setPurchaseCount(rs.getBigDecimal("sipurchasecount"));
            stock.getStockInfo().setSaleCount(rs.getBigDecimal("sisalecount"));
            stock.setAvailableQuantity(rs.getBigDecimal("availablequantity"));
            stock.setProfitPercentage(rs.getBigDecimal("profitpercentage"));
            stock.getStockInfo().setSaleMandatoryPrice(rs.getBigDecimal("sisalemandatoryprice"));
            stock.getStockInfo().getSaleMandatoryCurrency().setId(rs.getInt("sisalemandatorycurrency_id"));
            stock.getStockInfo().setIsFuel(rs.getBoolean("siis_fuel"));
            stock.getStockInfo().setFuelIntegrationCode(rs.getString("sifuelintegrationcode"));
            stock.getStockInfo().setWeight(rs.getBigDecimal("siweight"));
            stock.getStockInfo().getWeightUnit().setId(rs.getInt("siweightunit_id"));
            stock.getStockInfo().setMainWeight(rs.getBigDecimal("simainweight"));
            stock.getStockInfo().getMainWeightUnit().setId(rs.getInt("simainweightunit_id"));
            stock.getStockInfo().setMaxStockLevel(rs.getBigDecimal("simaxstocklevel"));
            stock.getStockInfo().setBalance(rs.getBigDecimal("sibalance"));
            
            stock.setBoxQuantity(rs.getBigDecimal("stckboxquantity"));
            stock.getStockInfo().setShelfQuantity(rs.getBigDecimal("sishelfquantity"));
            stock.getStockInfo().setStockEnoughDay(rs.getBigDecimal("sistockenoughday"));
            stock.getStockInfo().setMinFactorValue(rs.getBigDecimal("siminfactorvalue"));
            stock.getStockInfo().setMaxFactorValue(rs.getBigDecimal("simaxfactorvalue"));
            stock.getStockInfo().getIncomeExpense().setId(rs.getInt("siincomeexpense_id"));
            stock.getStockInfo().getIncomeExpense().setName(rs.getString("fiename"));
            stock.getStockInfo().getIncomeExpense().setIsIncome(rs.getBoolean("fieis_income"));
            stock.getStockInfo().setWarehouseStockDivisorValue(rs.getBigDecimal("siwarehousestockdivisorvalue"));
            
            stock.getStockInfo().setOrderDeliverySalePrice(rs.getBigDecimal("siorderdeliverysaleprice"));
            stock.getStockInfo().getOrderDeliverySaleCurrency().setId(rs.getInt("siorderdeliverysalecurrency_id"));
            
        } catch (Exception e) {
        }
        
        try {
            stock.getStockInfo().setCurrentPurchasePrice(rs.getBigDecimal("sicurrentpurchaseprice"));
            stock.getStockInfo().getCurrentPurchaseCurrency().setId(rs.getInt("sicurrentpurchasecurrency_id"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setCurrentSalePrice(rs.getBigDecimal("sicurrentsaleprice"));
            stock.getStockInfo().getCurrentSaleCurrency().setId(rs.getInt("sicurrentsalecurrency_id"));
        } catch (Exception e) {
        }
        try {
            stock.setPurchaseKdv(rs.getBigDecimal("purchasekdv"));
        } catch (Exception e) {
        }
        
        try {
            stock.setId(rs.getInt("stckid"));
            stock.setName(rs.getString("stckname"));
        } catch (Exception e) {
        }
        
        try {
            stock.setOtherQuantity(rs.getBigDecimal("otherquantity"));
            
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().getTaxDepartment().setId(rs.getInt("sitaxdepartment_id"));
        } catch (Exception e) {
        }
        try {
            stock.setAlternativeQuantity(rs.getBigDecimal("sabquantity"));
        } catch (Exception e) {
        }
        
        try {
            stock.setTaxRate(rs.getBigDecimal("taxrate"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setTempCurrentSalePrice(rs.getBigDecimal("tempsicurrentsaleprice"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setIsMinusStockLevel(rs.getBoolean("siis_minusstocklevel"));
        } catch (Exception e) {
        }
        try {
            stock.getCountry().setId(rs.getInt("stckcountry_id"));
            stock.getCountry().setTag(rs.getString("ctrydname"));
        } catch (Exception e) {
        }
        try {
            stock.getUnit().setId(rs.getInt("guntid"));
            stock.getUnit().setSortName(rs.getString("guntsortname"));
            stock.getUnit().setUnitRounding(rs.getInt("guntunitrounding"));
        } catch (Exception e) {
        }
        try {
            stock.getBrand().setId(rs.getInt("brid"));
            stock.getBrand().setName(rs.getString("brname"));
        } catch (Exception e) {
        }
        try {
            stock.setIsService(rs.getBoolean("stckis_service"));
        } catch (Exception e) {
        }
        try {
            stock.setDescription(rs.getString("stckdescription"));
        } catch (Exception e) {
        }
        try {
            stock.getStatus().setId(rs.getInt("sttdid"));
            stock.getStatus().setTag(rs.getString("sttdname"));
            
        } catch (Exception e) {
        }
        try {
            stock.setCenterstock_id(rs.getInt("stckcenterstock_id"));
            
        } catch (Exception e) {
        }
        try {
            stock.getUnit().setName(rs.getString("guntname"));
        } catch (Exception e) {
        }
        
        try {
            stock.getStockInfo().setTurnoverPremium(rs.getBigDecimal("siturnoverpremium"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setPurchaseRecommendedPrice(rs.getBigDecimal("sipurchaserecommendedprice"));
            stock.getStockInfo().getPurchaseCurrency().setId(rs.getInt("sipurchasecurrency_id"));
        } catch (Exception e) {
        }
        try {
            stock.getSalePriceListItem().getPriceList().setId(rs.getInt("pl2id"));
            stock.getSalePriceListItem().setId(rs.getInt("pli2id"));
        } catch (Exception e) {
        }
        try {
            stock.setStockType_id(rs.getInt("stcktype_id"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setIsPassive(rs.getBoolean("sii_passive"));
        } catch (Exception e) {
        }
        
        try {
            
            stock.getStockEInvoiceUnitCon().setId(rs.getInt("seiucid"));
            stock.getStockEInvoiceUnitCon().setStockIntegrationCode(rs.getString("seiucstockintegrationcode"));
            stock.getStockEInvoiceUnitCon().setQuantity(rs.getBigDecimal("seiucquantity"));
            stock.getStockEInvoiceUnitCon().setStockId(rs.getInt("seiucstock_id"));
            stock.getUnit().setInternationalCode(rs.getString("guntinternationalcode"));
            
        } catch (Exception e) {
        }
        
        try {
            stock.getStockInfo().seteInvoiceIntegrationCode(rs.getString("sieinvoiceintegrationcode"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setId(rs.getInt("siid"));
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setIsDelist(rs.getBoolean("stckiis_delist"));
        } catch (Exception e) {
        }
        
        try {
            stock.setCategoryName(rs.getString("gcname"));
            
        } catch (Exception e) {
        }
        try {
            stock.getStockInfo().setIsCampaign(rs.getBoolean("siis_campaign"));
            stock.setIs_get(rs.getBoolean("stckis_get"));
            stock.getStockInfo().getTaxDepartment().setName(rs.getString("txdname"));
            stock.getStockInfo().setOrderDeliveryRate(rs.getBigDecimal("siorderdeliveryrate"));
            
        } catch (Exception e) {
        }
        
        return stock;
    }
    
}
