/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.stockinventoryreport.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 *
 * @author esra.cabuk
 */
public class StockInventoryReportDao extends JdbcDaoSupport implements IStockInventoryReportDao {

    @Autowired
    SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<StockInventoryReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, StockInventoryReport obj, String branchList, int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier) {

        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String where1 = " ";

        String stockList = "";
        for (Stock stock : obj.getListOfStock()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());

        }

        String categoryList = "";
        if (obj.getReportType() == 2) {
            for (Categorization category : obj.getListOfStockCategorization()) {
                categoryList = categoryList + "," + String.valueOf(category.getId());
                if (category.getId() == 0) {
                    categoryList = "";
                    break;
                }
            }
            if (!categoryList.equals("")) {
                categoryList = categoryList.substring(1, categoryList.length());

            }
        }

        String centralSupplierList = "";
        String supplierList = "";
        if (obj.getReportType() == 3) {
            if (centralIntegrationIf != 0) {

                for (CentralSupplier centralSupplier : obj.getListOfCentralSupplier()) {
                    centralSupplierList = centralSupplierList + "," + String.valueOf(centralSupplier.getId());
                    if (centralSupplier.getId() == 0) {
                        centralSupplierList = "";
                        break;
                    }
                }
                if (!centralSupplierList.equals("")) {
                    centralSupplierList = centralSupplierList.substring(1, centralSupplierList.length());
                }

            }
            for (Account account : obj.getListOfAccount()) {
                supplierList = supplierList + "," + String.valueOf(account.getId());
                if (account.getId() == 0) {
                    supplierList = "";
                    break;
                }
            }
            if (!supplierList.equals("")) {
                supplierList = supplierList.substring(1, supplierList.length());
            }
        }

        String sql = "";

        if (obj.getCost() == 1) //-----------son fiyata göre----------
        {
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_stock_id AS stckid,\n"
                        + "r_stockname AS stckname,\n"
                        + "r_stockcode AS stckcode,\n"
                        + "r_centerproductcode AS stckcenterproductcode,\n"
                        + "r_barcode AS stckbarcode,\n"
                        + "r_unit_id AS guntid,\n"
                        + "r_unitsortname AS guntsortname,\n"
                        + "r_unitrounding AS guntunitrounding,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsaleprice AS sicurrentsaleprice,\n"
                        + "r_currentpurchaseprice AS sicurrentpurchaseprice,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_currentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                        + "r_category AS category,\n"
                        + "r_brand_id AS stckbrand_id,\n"
                        + "r_brandname AS brname,\n"
                        + "r_supplier_id AS supplier_id,\n"
                        + "r_suppliername AS accname,\n"
                        + "r_centersupplier_id AS stckcentralsupplier_id,\n"
                        + "r_centersuppliername AS csppname,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_lastprice(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_tax_id as tgid,\n"
                        + "r_taxrate AS tgrate,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_currentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_lastprice_kdv(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            }
        } else if (obj.getCost() == 2) { //Fifo ya Göre
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_stock_id AS stckid,\n"
                        + "r_stockname AS stckname,\n"
                        + "r_stockcode AS stckcode,\n"
                        + "r_centerproductcode AS stckcenterproductcode,\n"
                        + "r_barcode AS stckbarcode,\n"
                        + "r_unit_id AS guntid,\n"
                        + "r_unitsortname AS guntsortname,\n"
                        + "r_unitrounding AS guntunitrounding,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsaleprice AS sicurrentsaleprice,\n"
                        + "r_currentpurchaseprice AS sicurrentpurchaseprice,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_category AS category,\n"
                        + "r_brand_id AS stckbrand_id,\n"
                        + "r_brandname AS brname,\n"
                        + "r_supplier_id AS supplier_id,\n"
                        + "r_suppliername AS accname,\n"
                        + "r_centersupplier_id AS stckcentralsupplier_id,\n"
                        + "r_centersuppliername AS csppname,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_fifo(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_tax_id as tgid,\n"
                        + "r_taxrate AS tgrate,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_fifo_kdv(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            }

        } else if (obj.getCost() == 3) { //Ağırlıklı Ortalama
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_stock_id AS stckid,\n"
                        + "r_stockname AS stckname,\n"
                        + "r_stockcode AS stckcode,\n"
                        + "r_centerproductcode AS stckcenterproductcode,\n"
                        + "r_barcode AS stckbarcode,\n"
                        + "r_unit_id AS guntid,\n"
                        + "r_unitsortname AS guntsortname,\n"
                        + "r_unitrounding AS guntunitrounding,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsaleprice AS sicurrentsaleprice,\n"
                        + "r_currentpurchaseprice AS sicurrentpurchaseprice,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_category AS category,\n"
                        + "r_brand_id AS stckbrand_id,\n"
                        + "r_brandname AS brname,\n"
                        + "r_supplier_id AS supplier_id,\n"
                        + "r_suppliername AS accname,\n"
                        + "r_centersupplier_id AS stckcentralsupplier_id,\n"
                        + "r_centersuppliername AS csppname,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_weightavarage(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_tax_id as tgid,\n"
                        + "r_taxrate AS tgrate,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_weightavarage_kdv(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }
        }

        List<StockInventoryReport> result = new ArrayList<>();
        if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {
            Object[] param = new Object[]{obj.getDate(), branchList.equals("") ? null : branchList, stockList.equals("") ? null : stockList, categoryList.equals("") ? null : categoryList,
                supplierList.equals("") ? null : supplierList, centralSupplierList.equals("") ? null : centralSupplierList,
                obj.isIsTax(), obj.isZeroStock(), obj.isMinusStock(), obj.isOnlyMinusStock(), obj.isOnlyNotForSaleStock(), (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false),
                isCentralBranch, centralIntegrationIf, false, String.valueOf(pageSize), String.valueOf(first), supplierType, isCentralSupplier};
            result = getJdbcTemplate().query(sql, param, new StockInventoryReportMapper());
        } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {
            Object[] param = new Object[]{obj.getDate(), branchList.equals("") ? null : branchList, stockList.equals("") ? null : stockList, categoryList.equals("") ? null : categoryList,
                supplierList.equals("") ? null : supplierList, centralSupplierList.equals("") ? null : centralSupplierList,
                obj.isIsTax(), obj.isZeroStock(), obj.isMinusStock(), obj.isOnlyMinusStock(), obj.isOnlyNotForSaleStock(), (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false),
                isCentralBranch, centralIntegrationIf, false, String.valueOf(pageSize), String.valueOf(first), obj.getReportType() == 4 ? false : true};
            result = getJdbcTemplate().query(sql, param, new StockInventoryReportMapper());
        }

        return result;
    }

    @Override
    public List<StockInventoryReport> totals(String where, StockInventoryReport obj, String branchList, int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier) {

        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
        String where1 = " ";

        String stockList = "";
        for (Stock stock : obj.getListOfStock()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());

        }
        String categoryList = "";

        if (obj.getReportType() == 2) {

            for (Categorization category : obj.getListOfStockCategorization()) {
                categoryList = categoryList + "," + String.valueOf(category.getId());
                if (category.getId() == 0) {
                    categoryList = "";
                    break;
                }
            }
            if (!categoryList.equals("")) {
                categoryList = categoryList.substring(1, categoryList.length());

            }
        }
        String centralSupplierList = "";
        String supplierList = "";
        if (obj.getReportType() == 3) {

            if (centralIntegrationIf != 0) {
                for (CentralSupplier centralSupplier : obj.getListOfCentralSupplier()) {
                    centralSupplierList = centralSupplierList + "," + String.valueOf(centralSupplier.getId());
                    if (centralSupplier.getId() == 0) {
                        centralSupplierList = "";
                        break;
                    }
                }
                if (!centralSupplierList.equals("")) {
                    centralSupplierList = centralSupplierList.substring(1, centralSupplierList.length());
                }
            }
            for (Account account : obj.getListOfAccount()) {
                supplierList = supplierList + "," + String.valueOf(account.getId());
                if (account.getId() == 0) {
                    supplierList = "";
                    break;
                }
            }
            if (!supplierList.equals("")) {
                supplierList = supplierList.substring(1, supplierList.length());

            }

        }

        String sql = "";

        if (obj.getCost() == 1) //-----------son fiyata göre----------
        {
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_stock_id AS stckid,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost,\n"
                        + "r_salecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_purchasecurrency_id AS sicurrentpurchasecurrency_id\n"
                        + " FROM general.rpt_stockinventoryreport_lastprice_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_tax_id AS tgid,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost,\n"
                        + "r_salecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_purchasecurrency_id AS sicurrentpurchasecurrency_id\n"
                        + " FROM general.rpt_stockinventoryreport_lastprice_kdv_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?)";

            }

        } else if (obj.getCost() == 2) {
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_stock_id AS stckid,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost,\n"
                        + "r_salecurrency_id AS sicurrentsalecurrency_id\n"
                        + " FROM general.rpt_stockinventoryreport_fifo_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_tax_id AS tgid,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost,\n"
                        + "r_salecurrency_id AS sicurrentsalecurrency_id\n"
                        + " FROM general.rpt_stockinventoryreport_fifo_kdv_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?)";

            }
        } else if (obj.getCost() == 3) {
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_stock_id AS stckid,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost,\n"
                        + "r_salecurrency_id AS sicurrentsalecurrency_id\n"
                        + " FROM general.rpt_stockinventoryreport_weightaverage_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_tax_id AS tgid,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost,\n"
                        + "r_salecurrency_id AS sicurrentsalecurrency_id\n"
                        + " FROM general.rpt_stockinventoryreport_weightaverage_kdv_count(?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?)";

            }
        }

        List<StockInventoryReport> result = new ArrayList<>();
        if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {
            Object[] param = new Object[]{obj.getDate(), branchList.equals("") ? null : branchList, stockList.equals("") ? null : stockList, categoryList.equals("") ? null : categoryList,
                supplierList.equals("") ? null : supplierList, centralSupplierList.equals("") ? null : centralSupplierList,
                obj.isIsTax(), obj.isZeroStock(), obj.isMinusStock(), obj.isOnlyMinusStock(), obj.isOnlyNotForSaleStock(), (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false),
                isCentralBranch, centralIntegrationIf, supplierType, isCentralSupplier};
            result = getJdbcTemplate().query(sql, param, new StockInventoryReportMapper());
        } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {
            Object[] param = new Object[]{obj.getDate(), branchList.equals("") ? null : branchList, stockList.equals("") ? null : stockList, categoryList.equals("") ? null : categoryList,
                supplierList.equals("") ? null : supplierList, centralSupplierList.equals("") ? null : centralSupplierList,
                obj.isIsTax(), obj.isZeroStock(), obj.isMinusStock(), obj.isOnlyMinusStock(), obj.isOnlyNotForSaleStock(), (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false),
                isCentralBranch, centralIntegrationIf, obj.getReportType() == 4 ? false : true};
            result = getJdbcTemplate().query(sql, param, new StockInventoryReportMapper());
        }

        return result;

    }

    @Override
    public String exportData(String where, StockInventoryReport obj, String branchList, int centralIntegrationIf, boolean isCentralBranch, int supplierType, boolean isCentralSupplier) {

        SimpleDateFormat format = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        String where1 = " ";

        String stockList = "";
        for (Stock stock : obj.getListOfStock()) {
            stockList = stockList + "," + String.valueOf(stock.getId());
            if (stock.getId() == 0) {
                stockList = "";
                break;
            }
        }
        if (!stockList.equals("")) {
            stockList = stockList.substring(1, stockList.length());

        }

        String categoryList = "";
        if (obj.getReportType() == 2) {

            for (Categorization category : obj.getListOfStockCategorization()) {
                categoryList = categoryList + "," + String.valueOf(category.getId());
                if (category.getId() == 0) {
                    categoryList = "";
                    break;
                }
            }
            if (!categoryList.equals("")) {
                categoryList = categoryList.substring(1, categoryList.length());

            }
        }
        String centralSupplierList = "";
        String supplierList = "";
        if (obj.getReportType() == 3) {

            if (centralIntegrationIf != 0) {

                for (CentralSupplier centralSupplier : obj.getListOfCentralSupplier()) {
                    centralSupplierList = centralSupplierList + "," + String.valueOf(centralSupplier.getId());
                    if (centralSupplier.getId() == 0) {
                        centralSupplierList = "";
                        break;
                    }
                }
                if (!centralSupplierList.equals("")) {
                    centralSupplierList = centralSupplierList.substring(1, centralSupplierList.length());
                }
            }

            for (Account account : obj.getListOfAccount()) {
                supplierList = supplierList + "," + String.valueOf(account.getId());
                if (account.getId() == 0) {
                    supplierList = "";
                    break;
                }
            }
            if (!supplierList.equals("")) {
                supplierList = supplierList.substring(1, supplierList.length());

            }

        }

        String sql = "";

        if (obj.getCost() == 1) //-----------son fiyata göre----------
        {
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_stock_id AS stckid,\n"
                        + "r_stockname AS stckname,\n"
                        + "r_stockcode AS stckcode,\n"
                        + "r_centerproductcode AS stckcenterproductcode,\n"
                        + "r_barcode AS stckbarcode,\n"
                        + "r_unit_id AS guntid,\n"
                        + "r_unitsortname AS guntsortname,\n"
                        + "r_unitrounding AS guntunitrounding,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsaleprice AS sicurrentsaleprice,\n"
                        + "r_currentpurchaseprice AS sicurrentpurchaseprice,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_currentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                        + "r_category AS category,\n"
                        + "r_brand_id AS stckbrand_id,\n"
                        + "r_brandname AS brname,\n"
                        + "r_supplier_id AS supplier_id,\n"
                        + "r_suppliername AS accname,\n"
                        + "r_centersupplier_id AS stckcentralsupplier_id,\n"
                        + "r_centersuppliername AS csppname,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_lastprice('" + format.format(obj.getDate()) + "', '" + branchList + "', '" + stockList + "', '" + categoryList + "', '" + supplierList + "', '" + centralSupplierList + "'," + obj.isIsTax() + ", " + obj.isZeroStock() + ", " + obj.isMinusStock() + ", " + obj.isOnlyMinusStock() + ", " + obj.isOnlyNotForSaleStock() + ", " + (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false) + ", " + isCentralBranch + "," + centralIntegrationIf + ", true, '0', '0' ," + supplierType + "," + isCentralSupplier + ")";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_tax_id as tgid,\n"
                        + "r_taxrate AS tgrate,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_currentpurchasecurrency_id AS sicurrentpurchasecurrency_id,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_lastprice_kdv('" + format.format(obj.getDate()) + "', '" + branchList + "', '" + stockList + "', '" + categoryList + "', '" + supplierList + "', '" + centralSupplierList + "'," + obj.isIsTax() + ", " + obj.isZeroStock() + ", " + obj.isMinusStock() + ", " + obj.isOnlyMinusStock() + ", " + obj.isOnlyNotForSaleStock() + ", " + (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false) + ", " + isCentralBranch + "," + centralIntegrationIf + ", true, '0', '0'," + (obj.getReportType() == 4 ? false : true) + ")";

            }

        } else if (obj.getCost() == 2) { //Fifo ya Göre
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_stock_id AS stckid,\n"
                        + "r_stockname AS stckname,\n"
                        + "r_stockcode AS stckcode,\n"
                        + "r_centerproductcode AS stckcenterproductcode,\n"
                        + "r_barcode AS stckbarcode,\n"
                        + "r_unit_id AS guntid,\n"
                        + "r_unitsortname AS guntsortname,\n"
                        + "r_unitrounding AS guntunitrounding,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsaleprice AS sicurrentsaleprice,\n"
                        + "r_currentpurchaseprice AS sicurrentpurchaseprice,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_category AS category,\n"
                        + "r_brand_id AS stckbrand_id,\n"
                        + "r_brandname AS brname,\n"
                        + "r_supplier_id AS supplier_id,\n"
                        + "r_suppliername AS accname,\n"
                        + "r_centersupplier_id AS stckcentralsupplier_id,\n"
                        + "r_centersuppliername AS csppname,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_fifo('" + format.format(obj.getDate()) + "', '" + branchList + "', '" + stockList + "', '" + categoryList + "', '" + supplierList + "', '" + centralSupplierList + "'," + obj.isIsTax() + ", " + obj.isZeroStock() + ", " + obj.isMinusStock() + ", " + obj.isOnlyMinusStock() + ", " + obj.isOnlyNotForSaleStock() + ", " + (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false) + ", " + isCentralBranch + "," + centralIntegrationIf + ", true, '0', '0' ," + supplierType + "," + isCentralSupplier + ")";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_tax_id as tgid,\n"
                        + "r_taxrate AS tgrate,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_fifo_kdv('" + format.format(obj.getDate()) + "', '" + branchList + "', '" + stockList + "', '" + categoryList + "', '" + supplierList + "', '" + centralSupplierList + "'," + obj.isIsTax() + ", " + obj.isZeroStock() + ", " + obj.isMinusStock() + ", " + obj.isOnlyMinusStock() + ", " + obj.isOnlyNotForSaleStock() + ", " + (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false) + ", " + isCentralBranch + "," + centralIntegrationIf + ", true, '0', '0'," + (obj.getReportType() == 4 ? false : true) + ")";

            }

        } else if (obj.getCost() == 3) { //Ağırlıklı Ortalama
            if (obj.getReportType() == 1 || obj.getReportType() == 2 || obj.getReportType() == 3) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_stock_id AS stckid,\n"
                        + "r_stockname AS stckname,\n"
                        + "r_stockcode AS stckcode,\n"
                        + "r_centerproductcode AS stckcenterproductcode,\n"
                        + "r_barcode AS stckbarcode,\n"
                        + "r_unit_id AS guntid,\n"
                        + "r_unitsortname AS guntsortname,\n"
                        + "r_unitrounding AS guntunitrounding,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsaleprice AS sicurrentsaleprice,\n"
                        + "r_currentpurchaseprice AS sicurrentpurchaseprice,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_category AS category,\n"
                        + "r_brand_id AS stckbrand_id,\n"
                        + "r_brandname AS brname,\n"
                        + "r_supplier_id AS supplier_id,\n"
                        + "r_suppliername AS accname,\n"
                        + "r_centersupplier_id AS stckcentralsupplier_id,\n"
                        + "r_centersuppliername AS csppname,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_weightavarage('" + format.format(obj.getDate()) + "', '" + branchList + "', '" + stockList + "', '" + categoryList + "', '" + supplierList + "', '" + centralSupplierList + "'," + obj.isIsTax() + ", " + obj.isZeroStock() + ", " + obj.isMinusStock() + ", " + obj.isOnlyMinusStock() + ", " + obj.isOnlyNotForSaleStock() + ", " + (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false) + ", " + isCentralBranch + "," + centralIntegrationIf + ", true, '0', '0' ," + supplierType + "," + isCentralSupplier + " )";

            } else if (obj.getReportType() == 4 || obj.getReportType() == 5) {

                sql = " SELECT \n"
                        + "r_branch_id AS brnid,\n"
                        + "r_branchname AS brnname,\n"
                        + "r_tax_id as tgid,\n"
                        + "r_taxrate AS tgrate,\n"
                        + "r_quantity AS quantity,\n"
                        + "r_currentsalecurrency_id AS sicurrentsalecurrency_id,\n"
                        + "r_salecost AS salecost,\n"
                        + "r_purchasecost AS purchasecost\n"
                        + " FROM general.rpt_stockinventoryreport_weightavarage_kdv('" + format.format(obj.getDate()) + "', '" + branchList + "', '" + stockList + "', '" + categoryList + "', '" + supplierList + "', '" + centralSupplierList + "'," + obj.isIsTax() + ", " + obj.isZeroStock() + ", " + obj.isMinusStock() + ", " + obj.isOnlyMinusStock() + ", " + obj.isOnlyNotForSaleStock() + ", " + (obj.isRetailStock() == obj.isFuelStock() ? null : obj.isRetailStock() ? true : false) + ", " + isCentralBranch + "," + centralIntegrationIf + ", true, '0', '0'," + (obj.getReportType() == 4 ? false : true) + ")";

            }
        }
        
        return sql;
    }

    @Override
    public DataSource getDatasource() {
        return getDataSource();
    }

}
