/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 24.01.2018 14:09:47
 */
package com.mepsan.marwiz.finance.invoice.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.finance.invoice.dao.IInvoiceDao;
import com.mepsan.marwiz.finance.invoice.dao.IInvoiceItemDao;
import com.mepsan.marwiz.finance.waybill.dao.IWaybillItemDao;
import com.mepsan.marwiz.general.contractarticles.dao.IContractArticlesDao;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.IExchangeService;
import com.mepsan.marwiz.general.exchange.dao.IExchangeDao;
import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoiceItem;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.general.ContractArticles;
import com.mepsan.marwiz.general.model.general.RefineryStockPrice;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.log.IncomingEInvoice;
import com.mepsan.marwiz.general.refinerypurchase.dao.IRefineryPurchaseDao;
import com.mepsan.marwiz.inventory.pricelist.dao.IPriceListItemDao;
import com.mepsan.marwiz.inventory.stock.dao.IStockDao;
import com.mepsan.marwiz.inventory.taxgroup.dao.ITaxGroupDao;
import com.mepsan.marwiz.system.einvoiceintegration.dao.IIncomingEInvoicesDao;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

public class InvoiceItemService implements IInvoiceItemService {

    @Autowired
    private IInvoiceItemDao invoiceItemDao;

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IInvoiceDao invoiceDao;

    @Autowired
    private IContractArticlesDao contractArticlesDao;

    @Autowired
    private IRefineryPurchaseDao refineryPurchaseDao;

    @Autowired
    private IStockDao stockDao;

    @Autowired
    private IPriceListItemDao priceListItemDao;

    @Autowired
    private ITaxGroupDao taxGroupDao;

    @Autowired
    private IWaybillItemDao waybillItemDao;

    @Autowired
    private IExchangeDao exchangeDao;

    @Autowired
    private IExchangeService exchangeService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setInvoiceItemDao(IInvoiceItemDao invoiceItemDao) {
        this.invoiceItemDao = invoiceItemDao;
    }

    public void setStockDao(IStockDao stockDao) {
        this.stockDao = stockDao;
    }

    public void setContractArticlesDao(IContractArticlesDao contractArticlesDao) {
        this.contractArticlesDao = contractArticlesDao;
    }

    public void setTaxGroupDao(ITaxGroupDao taxGroupDao) {
        this.taxGroupDao = taxGroupDao;
    }

    public void setRefineryPurchaseDao(IRefineryPurchaseDao refineryPurchaseDao) {
        this.refineryPurchaseDao = refineryPurchaseDao;
    }

    public void setPriceListItemDao(IPriceListItemDao priceListItemDao) {
        this.priceListItemDao = priceListItemDao;
    }

    public void setWaybillItemDao(IWaybillItemDao waybillItemDao) {
        this.waybillItemDao = waybillItemDao;
    }

    public void setExchangeDao(IExchangeDao exchangeDao) {
        this.exchangeDao = exchangeDao;
    }

    public void setInvoiceDao(IInvoiceDao invoiceDao) {
        this.invoiceDao = invoiceDao;
    }

    public void setExchangeService(IExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Override
    public List<InvoiceItem> listInvoiceStocks(Invoice invoice, String type) {
        return invoiceItemDao.listInvoiceStocks(invoice, type);
    }

    @Override
    public int create(InvoiceItem obj) {

        List<InvoiceItem> list = new ArrayList<>();
        list.add(obj);
        obj.setJsonItems(jsonArrayInvoiceItems(list));
        return invoiceItemDao.create(obj);
    }

    @Override
    public int update(InvoiceItem obj) {
        List<InvoiceItem> list = new ArrayList<>();

        if (obj.getInvoice().getType().getId() != 59 && obj.getWaybillItemIds().contains(",")) {//ürün birden fazla irsaliyeye bağlı ise

            BigDecimal quant = obj.getQuantity();
            String waybillIds = obj.getWaybillItemIds();
            BigDecimal waybillQuantity;

            String[] quantitys = obj.getWaybillItemQuantity().split(",");
            String[] remQuantitys = obj.getWaybillItemQuantitys().split(",");

            BigDecimal quantityControl = BigDecimal.ZERO;
            for (int i = 0; i < quantitys.length; i++) {
                quantityControl = quantityControl.add(new BigDecimal(quantitys[i])).add(new BigDecimal(remQuantitys[i]));
            }

            if (quant.doubleValue() > quantityControl.doubleValue()) {//girilen değer bağlı olduğu irsaliye değerlerinden yüksek ise çık.
                return -101;
            }

            //ürünün bağlı olduğu irsaliye kadar dön
            for (int i = 0; i < quantitys.length; i++) {
                waybillQuantity = new BigDecimal(quantitys[i]);
                waybillQuantity = waybillQuantity.add(new BigDecimal(remQuantitys[i]));

                InvoiceItem item = new InvoiceItem();
                item.setId(obj.getId());
                item.setStock(obj.getStock());
                item.setUnit(obj.getUnit());
                item.setCurrency(obj.getCurrency());
                item.setDescription(obj.getDescription());
                item.setDiscountPrice(obj.getDiscountPrice());
                item.setDiscountRate(obj.getDiscountRate());
                item.setExchangeRate(obj.getExchangeRate());
                item.setInvoice(obj.getInvoice());
                item.setIsService(obj.isIsService());
                item.setIsTaxIncluded(obj.isIsTaxIncluded());
                item.setUnitPrice(obj.getUnitPrice());
                item.setTotalTax(obj.getTotalTax());
                item.setTotalPrice(obj.getTotalPrice());
                item.setTotalMoney(obj.getTotalMoney());
                item.setStockCount(obj.getStockCount());
                item.setTaxRate(obj.getTaxRate());
                if (!obj.getInvoice().getListOfWarehouse().isEmpty()) {
                    item.getInvoice().getListOfWarehouse().add(obj.getInvoice().getListOfWarehouse().get(0));
                }
                if (quant.compareTo(BigDecimal.ZERO) <= 0) {
                    item.setWaybillItemIds(waybillIds.split(",")[i]);
                    item.setQuantity(BigDecimal.ZERO);
                    list.add(item);
                } else if (quant.doubleValue() >= waybillQuantity.doubleValue()) {//girilen miktar irsaliye miktarından büyükse azalt
                    quant = quant.subtract(waybillQuantity);
                    item.setWaybillItemIds(waybillIds.split(",")[i]);
                    item.setQuantity(waybillQuantity);
                    list.add(item);
                } else {
                    item.setWaybillItemIds(waybillIds.split(",")[i]);
                    item.setQuantity(quant);
                    list.add(item);
                    quant = BigDecimal.ZERO;
                }
            }
        } else {
            list.add(obj);
        }
        if (obj.getOrderItemIds() != null && !obj.getOrderItemIds().equals("")) {
            obj.setJsonItems(jsonArrayInvoiceItemsforOrder(list));
        } else {
            obj.setJsonItems(jsonArrayInvoiceItems(list));
        }

        return invoiceItemDao.update(obj);
    }

    /**
     * Bu metot irsaliyeden faturaya çoklu ürün aktardığımızda çalışır
     *
     *
     * @param list
     * @param obj
     * @return
     */
    @Override
    public int createAll(List<InvoiceItem> list, Invoice obj) {

        InvoiceItem invoiceItem = list.get(0);
        invoiceItem.setInvoice(obj);
        invoiceItem.setJsonItems(jsonArrayInvoiceItems(list));

        return invoiceItemDao.create(invoiceItem);
    }

    /**
     * Bu metot irsaliyeden aktarılan ürünlerin toplu güncelleneceği zaman
     * çalışır
     *
     * @param list
     * @param obj
     * @return
     */
    @Override
    public int updateAll(List<InvoiceItem> list, Invoice inv) {
        InvoiceItem invoiceItem = list.get(0);
        invoiceItem.setInvoice(inv);
        List<InvoiceItem> jsonList = new ArrayList<>();

        for (InvoiceItem obj : list) {
            if (obj.getInvoice().getType().getId() != 59 && obj.getWaybillItemIds().contains(",")) {//ürün birden fazla irsaliyeye bağlı ise

                BigDecimal quant = obj.getQuantity();
                String waybillIds = obj.getWaybillItemIds();
                BigDecimal waybillQuantity;

                String[] quantitys = obj.getWaybillItemQuantity().split(",");
                String[] remQuantitys = obj.getWaybillItemQuantitys().split(",");

                BigDecimal quantityControl = BigDecimal.ZERO;
                for (int i = 0; i < quantitys.length; i++) {
                    quantityControl = quantityControl.add(new BigDecimal(quantitys[i])).add(new BigDecimal(remQuantitys[i]));
                }

                if (quant.doubleValue() > quantityControl.doubleValue()) {//girilen değer bağlı olduğu irsaliye değerlerinden yüksek ise çık.
                    return -101;
                }

                //ürünün bağlı olduğu irsaliye kadar dön
                for (int i = 0; i < quantitys.length; i++) {
                    waybillQuantity = new BigDecimal(quantitys[i]);
                    waybillQuantity = waybillQuantity.add(new BigDecimal(remQuantitys[i]));

                    InvoiceItem item = new InvoiceItem();
                    item.setId(obj.getId());
                    item.setStock(obj.getStock());
                    item.setUnit(obj.getUnit());
                    item.setCurrency(obj.getCurrency());
                    item.setDescription(obj.getDescription());
                    item.setDiscountPrice(obj.getDiscountPrice());
                    item.setDiscountRate(obj.getDiscountRate());
                    item.setExchangeRate(obj.getExchangeRate());
                    item.setInvoice(obj.getInvoice());
                    item.setIsService(obj.isIsService());
                    item.setIsTaxIncluded(obj.isIsTaxIncluded());
                    item.setUnitPrice(obj.getUnitPrice());
                    item.setTotalTax(obj.getTotalTax());
                    item.setTotalPrice(obj.getTotalPrice());
                    item.setTotalMoney(obj.getTotalMoney());
                    item.setStockCount(obj.getStockCount());
                    item.setTaxRate(obj.getTaxRate());
                    if (!obj.getInvoice().getListOfWarehouse().isEmpty()) {
                        item.getInvoice().getListOfWarehouse().add(obj.getInvoice().getListOfWarehouse().get(0));
                    }
                    if (quant.compareTo(BigDecimal.ZERO) <= 0) {
                        item.setWaybillItemIds(waybillIds.split(",")[i]);
                        item.setQuantity(BigDecimal.ZERO);
                        jsonList.add(item);
                    } else if (quant.doubleValue() >= waybillQuantity.doubleValue()) {//girilen miktar irsaliye miktarından büyükse azalt
                        quant = quant.subtract(waybillQuantity);
                        item.setWaybillItemIds(waybillIds.split(",")[i]);
                        item.setQuantity(waybillQuantity);
                        jsonList.add(item);
                    } else {
                        item.setWaybillItemIds(waybillIds.split(",")[i]);
                        item.setQuantity(quant);
                        jsonList.add(item);
                        quant = BigDecimal.ZERO;
                    }
                }
            } else {
                jsonList.add(obj);
            }
        }

        for (InvoiceItem invoiceItem1 : jsonList) {
            invoiceItem1.setInvoice(inv);
        }
        invoiceItem.setJsonItems(jsonArrayInvoiceItems(jsonList));

        return invoiceItemDao.update(invoiceItem);
    }

    @Override
    public int delete(InvoiceItem item) {
        List<InvoiceItem> list = new ArrayList<>();
        item.setWaybillItemIds("0");
        list.add(item);
        item.setJsonItems(jsonArrayInvoiceItems(list));
        return invoiceItemDao.delete(item);
    }

    @Override
    public String jsonArrayInvoiceItems(List<InvoiceItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (InvoiceItem obj : list) {

            //kdv hariç ise kdv'li birim fiyat hesapla
            if (!obj.isIsTaxIncluded()
                    && obj.getTaxRate() != null
                    && obj.getTaxRate().doubleValue() > 0
                    && obj.getUnitPrice() != null
                    && obj.getUnitPrice().doubleValue() > 0) {

                BigDecimal x = BigDecimal.ONE.add(obj.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                obj.setUnitPrice(obj.getUnitPrice().multiply(x));
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("is_service", obj.isIsService());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getId() : obj.getStock().getUnit().getId());
            jsonObject.addProperty("unitprice", obj.getUnitPrice() == null ? 0 : obj.getUnitPrice());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("totalprice", obj.getTotalPrice());
            jsonObject.addProperty("taxrate", obj.getTaxRate());
            jsonObject.addProperty("totaltax", obj.getTotalTax());
            jsonObject.addProperty("is_discountrate", obj.isIsDiscountRate());
            jsonObject.addProperty("discountrate", obj.getDiscountRate() == null ? 0 : obj.getDiscountRate());
            jsonObject.addProperty("discountprice", obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice());
            jsonObject.addProperty("is_discountrate2", obj.isIsDiscountRate2());
            jsonObject.addProperty("discountrate2", obj.getDiscountRate2() == null ? 0 : obj.getDiscountRate2());
            jsonObject.addProperty("discountprice2", obj.getDiscountPrice2() == null ? 0 : obj.getDiscountPrice2());
            jsonObject.addProperty("currency_id", obj.getCurrency().getId());
            jsonObject.addProperty("exchangerate", obj.getExchangeRate());
            jsonObject.addProperty("totalmoney", obj.getTotalMoney());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("stockcount", obj.getStockCount());
            jsonObject.addProperty("waybillitem_id", obj.getWaybillItemIds());//irsaliyeden aktarıldı ise
            jsonObject.addProperty("managerUserDataid", ((obj.getDiscountPrice() == null || obj.getDiscountPrice().doubleValue() == 0)
                    && (obj.getDiscountPrice2() == null || obj.getDiscountPrice2().doubleValue() == 0)) ? null : sessionBean.getUser().getId());
            jsonObject.addProperty("isManagerDiscount", ((obj.getDiscountPrice() != null && obj.getDiscountPrice().doubleValue() > 0)
                    || (obj.getDiscountPrice2() != null && obj.getDiscountPrice2().doubleValue() > 0)));
            jsonObject.addProperty("recommendedprice", obj.getStock().getStockInfo().getRecommendedPrice() == null ? BigDecimal.ZERO : obj.getStock().getStockInfo().getRecommendedPrice());
            jsonObject.addProperty("warehouse_id", obj.getWarehouse().getId() > 0 ? obj.getWarehouse().getId() : obj.getInvoice().getListOfWarehouse().isEmpty() ? 0 : obj.getInvoice().getListOfWarehouse().get(0).getId());
            jsonObject.addProperty("is_free", obj.getUnitPrice() == null ? true : obj.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 0 ? true : false);
            jsonObject.addProperty("differentinvoiceitem_id", obj.getPriceDifferentInvoiceItem() != null ? obj.getPriceDifferentInvoiceItem().getId() == 0 ? null : obj.getPriceDifferentInvoiceItem().getId() : null);
            jsonObject.addProperty("differenttotalmoney", obj.getPriceDifferentTotalMoney() == null ? 0 : obj.getPriceDifferentTotalMoney());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    @Override
    public InvoiceItem calculater(InvoiceItem invoiceItem, int type) {
        BigDecimal up = null;

        //miktar veya birim fiyat yoksa hesaplama yapılamaz!
        if (invoiceItem.getQuantity() == null || invoiceItem.getUnitPrice() == null || invoiceItem.getQuantity().doubleValue() == 0 || invoiceItem.getUnitPrice().doubleValue() == 0) {

            invoiceItem.setTotalPrice(BigDecimal.ZERO);
            invoiceItem.setTotalMoney(BigDecimal.ZERO);
            invoiceItem.setDiscountPrice(BigDecimal.ZERO);
            invoiceItem.setDiscountPrice2(BigDecimal.ZERO);
            invoiceItem.setTotalTax(BigDecimal.ZERO);
            return invoiceItem;
        }

        if (!invoiceItem.isIsNotCalcTotalPrice()) { // total price yeniden hesaplar
            //vergi dahil ise vergi haric birim fiyata göre topam vergi çıkar
            //System.out.println("-invoiceItem.isIsTaxIncluded()---" + invoiceItem.isIsTaxIncluded());
            if (invoiceItem.isIsTaxIncluded()) {
                if (invoiceItem.getTaxRate() != null) {
                    BigDecimal x = BigDecimal.ONE.add(invoiceItem.getTaxRate().divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
                    up = invoiceItem.getUnitPrice().divide(x, 4, RoundingMode.HALF_EVEN);
                } else {
                    up = invoiceItem.getUnitPrice();
                }

            } else {
                up = invoiceItem.getUnitPrice();
            }
            //System.out.println("up"+up);

            //vergisiz birim fiyat ve miktarı çarpıp vergisiz tutarı hesapladık
            if (invoiceItem.getQuantity() != null && up != null) {
                invoiceItem.setTotalPrice(invoiceItem.getQuantity().multiply(up));
            } else {
                invoiceItem.setTotalPrice(BigDecimal.ZERO);
            }

            //System.out.println("invoiceItem.getTotalPrice"+invoiceItem.getTotalPrice());
            //iskonto tutarını hesapladık.
            //   System.out.println("CALCULATER---invoiceItem.isIsDiscountRate()---" + invoiceItem.isIsDiscountRate());
            if (!invoiceItem.isIsDiscountRate()) {//tutar girildi oran hesapla
                if (invoiceItem.getDiscountPrice() != null && invoiceItem.getDiscountPrice().doubleValue() > 0) {
                    invoiceItem.setDiscountRate((invoiceItem.getDiscountPrice().divide(invoiceItem.getTotalPrice(), 4, RoundingMode.HALF_EVEN)).multiply(new BigDecimal(100)));
                } else {
                    invoiceItem.setDiscountPrice(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate(BigDecimal.ZERO);
                }
            } else if (invoiceItem.isIsDiscountRate()) {//oran girildi tutar hesapla
                if (invoiceItem.getDiscountRate() != null && invoiceItem.getDiscountRate().doubleValue() > 0) {
                    invoiceItem.setDiscountPrice((invoiceItem.getTotalPrice().multiply(invoiceItem.getDiscountRate())).movePointLeft(2));
                } else {
                    invoiceItem.setDiscountPrice(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate(BigDecimal.ZERO);
                }
            } else {
                invoiceItem.setDiscountPrice(BigDecimal.ZERO);
                invoiceItem.setDiscountRate(BigDecimal.ZERO);
            }

            invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().subtract(invoiceItem.getDiscountPrice() != null ? invoiceItem.getDiscountPrice() : BigDecimal.valueOf(0)));

            //2.iskonto hesabı
            if (!invoiceItem.isIsDiscountRate2()) {//tutar girildi oran hesapla
                if (invoiceItem.getDiscountPrice2() != null && invoiceItem.getDiscountPrice2().doubleValue() > 0) {
                    invoiceItem.setDiscountRate2((invoiceItem.getDiscountPrice2().divide(invoiceItem.getTotalPrice(), 4, RoundingMode.HALF_EVEN)).multiply(new BigDecimal(100)));
                } else {
                    invoiceItem.setDiscountPrice2(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate2(BigDecimal.ZERO);
                }
            } else if (invoiceItem.isIsDiscountRate2()) {//oran girildi tutar hesapla
                if (invoiceItem.getDiscountRate2() != null && invoiceItem.getDiscountRate2().doubleValue() > 0) {
                    invoiceItem.setDiscountPrice2((invoiceItem.getTotalPrice().multiply(invoiceItem.getDiscountRate2())).movePointLeft(2));
                } else {
                    invoiceItem.setDiscountPrice2(BigDecimal.ZERO);
                    invoiceItem.setDiscountRate2(BigDecimal.ZERO);
                }
            } else {
                invoiceItem.setDiscountPrice2(BigDecimal.ZERO);
                invoiceItem.setDiscountRate2(BigDecimal.ZERO);
            }
            //   System.out.println("-Ürün--iskonto oranu ===" + invoiceItem.getDiscountRate());
            // System.out.println("-ÜRün-iskonto tutarı===" + invoiceItem.getDiscountPrice());
            //iskontoyu düştük
            invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().subtract(invoiceItem.getDiscountPrice2()));

            //System.out.println("--Ürün İskontosu Düştükten Sonra--" + invoiceItem.getTotalPrice());
            //Fatura bazında tutar iskontosu girildi ise önce oran bul sonra uygula
            if (!invoiceItem.getInvoice().isIsDiscountRate() && invoiceItem.getInvoice().getDiscountPrice() != null && invoiceItem.getInvoice().getDiscountPrice().doubleValue() > 0) {
                //fatura bazında tutar iskontosu varsa yeniden oran hesaplayıp iskontosunu bul
                BigDecimal tempTotalPrice = invoiceItem.getInvoice().getTotalPrice();

                //hiç ürün yoksa iskontoyu ekleme
                if (tempTotalPrice.compareTo(BigDecimal.ZERO) != 0) {
                    tempTotalPrice = tempTotalPrice.add(invoiceItem.getInvoice().getDiscountPrice());
                }

                //yeni ürün ekleniyorsa onun tutarınıda ekle.
                if (invoiceItem.getId() == 0) {
                    tempTotalPrice = tempTotalPrice.add(invoiceItem.getTotalPrice());
                }

                BigDecimal rate = BigDecimal.valueOf(0);
                BigDecimal disc = BigDecimal.valueOf(0);
                if (tempTotalPrice != null && tempTotalPrice.compareTo(BigDecimal.valueOf(0)) != 0) {
                    rate = new BigDecimal(100).multiply(invoiceItem.getInvoice().getDiscountPrice()).divide(tempTotalPrice, 8, RoundingMode.HALF_EVEN);
                    disc = new BigDecimal(BigInteger.ONE).subtract(rate.divide(new BigDecimal(100), 8, RoundingMode.HALF_EVEN));
                }

                invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().multiply(disc));
            }

            //Fatura bazında oran iskontosu girildi ise
            if (invoiceItem.getInvoice().isIsDiscountRate() && invoiceItem.getInvoice().getDiscountRate() != null && invoiceItem.getInvoice().getDiscountRate().doubleValue() > 0) {

                BigDecimal disc = new BigDecimal(BigInteger.ONE).subtract(invoiceItem.getInvoice().getDiscountRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                invoiceItem.setTotalPrice(invoiceItem.getTotalPrice().multiply(disc));
            }
        }
        //  System.out.println("*Fstura bazında iskonto düşüldükte sonra---" + invoiceItem.getTotalPrice());
        // vergi oranını hesapladık.
        if (invoiceItem.getTaxRate() != null) {
            if (invoiceItem.getTaxRate().doubleValue() > 0) {
                invoiceItem.setTotalTax(invoiceItem.getTotalPrice().multiply(invoiceItem.getTaxRate()).divide(new BigDecimal(100.0000), 4, RoundingMode.HALF_EVEN));
            } else {
                invoiceItem.setTotalTax(BigDecimal.ZERO);
            }
        } else {
            invoiceItem.setTotalTax(BigDecimal.ZERO);
        }

        // System.out.println("invoiceItem.getTotalTax"+invoiceItem.getTotalTax());
        invoiceItem.setTotalMoney(invoiceItem.getTotalPrice().add(invoiceItem.getTotalTax()));

        //System.out.println("invoiceItem.getTotalMoney"+invoiceItem.getTotalMoney());
        calculateProfit(invoiceItem, type);

        return invoiceItem;
    }

    @Override
    public void calculateProfit(InvoiceItem obj, int type) {
        BigDecimal salePrice = BigDecimal.valueOf(0);
        BigDecimal purchasePrice = BigDecimal.valueOf(0);
        BigDecimal profitRate = BigDecimal.valueOf(0);
        Stock stockPrice = new Stock();
        if (type == 2) {
            stockPrice = obj.getStock();
        } else {
            stockPrice = stockDao.findStockLastPrice(obj.getStock().getId(), obj.getInvoice().getBranchSetting());
        }

        if (obj.getInvoice().isIsPurchase()) {

            purchasePrice = obj.getTotalMoney().divide(obj.getQuantity(), 4, RoundingMode.HALF_EVEN);

            BigDecimal bd = exchangeService.bringExchangeRate(stockPrice.getStockInfo().getCurrentSaleCurrency(), stockPrice.getStockInfo().getCurrentPurchaseCurrency(), sessionBean.getUser());
            if (stockPrice.getStockInfo().getCurrentSalePrice() != null) {
                salePrice = stockPrice.getStockInfo().getCurrentSalePrice().multiply(bd);
            }

            if (stockPrice.getStockInfo().getTempCurrentSalePrice() != null) {// satış fiyatı varsa kontrolü
                if (purchasePrice.compareTo(BigDecimal.ZERO) != 0) {

                    profitRate = ((salePrice.subtract(purchasePrice)).divide(purchasePrice, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                } else if (purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
                    profitRate = BigDecimal.valueOf(100);
                }
            } else {
                profitRate = null;
            }
        } else if (!obj.getInvoice().isIsPurchase()) {

            BigDecimal bd = exchangeService.bringExchangeRate(stockPrice.getStockInfo().getCurrentPurchaseCurrency(), stockPrice.getStockInfo().getCurrentSaleCurrency(), sessionBean.getUser());
            if (stockPrice.getStockInfo().getCurrentPurchasePrice() != null) {
                purchasePrice = stockPrice.getStockInfo().getCurrentPurchasePrice().multiply(bd);
            }

            if (purchasePrice.compareTo(BigDecimal.ZERO) != 0) {
                purchasePrice = (purchasePrice.multiply((BigDecimal.valueOf(1).add((stockPrice.getPurchaseKdv().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))))));

                //vergisiz iskonto uygulanmış birim fiyat.
                salePrice = obj.getTotalMoney().divide(obj.getQuantity(), 4, RoundingMode.HALF_EVEN);
                profitRate = ((salePrice.subtract(purchasePrice)).divide(purchasePrice, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            }

        }
        obj.setProfitPercentage(profitRate);

        if (obj.getInvoice().isIsPurchase() && stockPrice.getStockInfo().getTempCurrentSalePrice() == null) {
            obj.setProfitPrice(null);
        } else {
            if (obj.getQuantity().compareTo(BigDecimal.valueOf(0)) == 1) {
                obj.setProfitPrice((salePrice.subtract(purchasePrice)).multiply(obj.getQuantity()));
            }
        }

    }

    @Override
    public List<InvoiceItem> findAllSaleItemForCredit(CustomerAgreements customerAgreements, String where) {
        return invoiceItemDao.findAllSaleItemForCredit(customerAgreements, where);
    }

    @Override
    public InvoiceItem findStock(String barcode, Invoice obj, boolean isAlternativeBarcode, boolean isInvoiceStockSalePriceList) {
        return invoiceItemDao.findStock(barcode, obj, isAlternativeBarcode, isInvoiceStockSalePriceList);
    }

    @Override
    public InvoiceItem totalQuantityForInvoice(Invoice obj, int stockId, Date begin, Date end, Branch branch) {
        return invoiceItemDao.totalQuantityForInvoice(obj, stockId, begin, end, branch);
    }

    public boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<InvoiceItem> createSampleList() {
        List<InvoiceItem> list = new ArrayList<>();

        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.getStock().setBarcode("963852741123");
        invoiceItem.setQuantity(BigDecimal.valueOf(5));
        invoiceItem.setUnitPrice(BigDecimal.valueOf(4));
        invoiceItem.setIsTaxIncluded(true);

        list.add(invoiceItem);

        invoiceItem = new InvoiceItem();
        invoiceItem.getStock().setBarcode("8690504086529");
        invoiceItem.setQuantity(BigDecimal.valueOf(12));
        invoiceItem.setUnitPrice(BigDecimal.valueOf(1.25));
        invoiceItem.setIsTaxIncluded(false);

        list.add(invoiceItem);

        invoiceItem = new InvoiceItem();
        invoiceItem.getStock().setBarcode("8690504067108");
        invoiceItem.setQuantity(BigDecimal.valueOf(1));
        invoiceItem.setUnitPrice(BigDecimal.valueOf(1));
        invoiceItem.setIsTaxIncluded(true);

        list.add(invoiceItem);

        return list;
    }

    //Excel dosyasından verileri okur 
    @Override
    public List<InvoiceItem> processUploadFile(InputStream inputStream, Invoice invoice) {

        InvoiceItem invoiceItem = new InvoiceItem();
        List<InvoiceItem> excelStockList = new ArrayList<>();
        try {
            Workbook workbook;
            workbook = WorkbookFactory.create(inputStream); // HSSF veya XSSF olarak oluşması için bu şekilde çalışma dosyası oluşturuldu.
            Sheet sheet = workbook.getSheetAt(0);
            Row row;
            int rows;
            rows = sheet.getPhysicalNumberOfRows();
            int cols = 4;
            int tmp = 0;

            for (int i = 0; i < 10 || i < rows; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    tmp = sheet.getRow(i).getPhysicalNumberOfCells();
                    if (tmp > cols) {
                        tmp = 4;
                    }
                }
            }
            List<InvoiceItem> list = new ArrayList();

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            excelStockList.clear();

            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                boolean isMinStockLevel = false;
                boolean isMaxStockLevel = false;

                row = sheet.getRow(r);

                invoiceItem = new InvoiceItem();
                if (row != null && !isRowEmpty(row)) { // eğer satır boş değilse 
                    invoiceItem.setExcelDataType(1);
                    if (row.getCell(0) != null) {
                        try {
                            CellValue cellValue0 = evaluator.evaluate(row.getCell(0));
                            switch (cellValue0.getCellTypeEnum()) { //BArcode
                                case NUMERIC:
                                    String sBarcode = String.valueOf(row.getCell(0).getNumericCellValue()); // bilimsel gösterimdeki barkod değerini uzun olduğu için bigInt değerine set edilid.
                                    BigDecimal bigDecimal = new BigDecimal(sBarcode);
                                    BigInteger toBigInteger = bigDecimal.toBigInteger();
                                    String barcode = String.valueOf(toBigInteger);

                                    invoiceItem.getStock().setBarcode(barcode);

                                    break;
                                case STRING:
                                    invoiceItem.getStock().setBarcode(String.valueOf(row.getCell(0).getRichStringCellValue()));
                                    break;
                            }
                        } catch (Exception e) {
                            invoiceItem.getStock().setBarcode("-1");
                            invoiceItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(0) == null) {
                        invoiceItem.getStock().setBarcode("-1");
                        invoiceItem.setExcelDataType(-1);
                    }

                    if (row.getCell(1) != null) { // Miktar
                        try {
                            CellValue cellValue1 = evaluator.evaluate(row.getCell(1));
                            switch (cellValue1.getCellTypeEnum()) { //Alternatif barkod karşılığı
                                case NUMERIC:
                                    double equavilent = row.getCell(1).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);

                                    invoiceItem.setQuantity(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(1).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    invoiceItem.setQuantity(bd);
                                    break;
                            }

                            if (invoiceItem.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                                invoiceItem.setExcelDataType(-1);
                            }

                        } catch (Exception e) {
                            invoiceItem.setExcelDataType(-1);
                            invoiceItem.setQuantity(BigDecimal.valueOf(0));
                        }
                    } else if (row.getCell(1) == null) {
                        invoiceItem.setQuantity(BigDecimal.valueOf(0));
                        invoiceItem.setExcelDataType(-1);
                    }

                    if (row.getCell(2) != null) { // Birim fiyat
                        try {
                            CellValue cellValue2 = evaluator.evaluate(row.getCell(2));
                            switch (cellValue2.getCellTypeEnum()) {
                                case NUMERIC:
                                    double equavilent = row.getCell(2).getNumericCellValue();
                                    BigDecimal bigDecimal = BigDecimal.valueOf(equavilent);
                                    invoiceItem.setUnitPrice(bigDecimal);
                                    invoiceItem.setExcelUnitPrice(bigDecimal);
                                    break;
                                case STRING:
                                    String s = String.valueOf(row.getCell(2).getRichStringCellValue());
                                    double d = Double.valueOf(s);
                                    BigDecimal bd = BigDecimal.valueOf(d);
                                    invoiceItem.setUnitPrice(bd);
                                    invoiceItem.setExcelUnitPrice(bd);
                                    break;
                            }
                        } catch (Exception e) {
                            invoiceItem.setExcelDataType(-1);
                            invoiceItem.setUnitPrice(BigDecimal.valueOf(0));
                            invoiceItem.setExcelUnitPrice(BigDecimal.valueOf(0));

                        }
                    } else if (row.getCell(2) == null) {
                        invoiceItem.setUnitPrice(BigDecimal.valueOf(0));
                        invoiceItem.setExcelUnitPrice(BigDecimal.valueOf(0));
                        invoiceItem.setExcelDataType(-1);
                    }

                    if (row.getCell(3) != null) { // Vergi dahil---hariç
                        try {
                            CellValue cellValue4 = evaluator.evaluate(row.getCell(3));
                            switch (cellValue4.getCellTypeEnum()) {
                                case NUMERIC:
                                    Double bData = row.getCell(3).getNumericCellValue(); // veriyi boolean şekilde set etmek için kullanıldı.
                                    int boolService = bData.intValue();
                                    if (boolService == 1) {
                                        invoiceItem.setIsTaxIncluded(Boolean.TRUE);
                                    } else {
                                        invoiceItem.setIsTaxIncluded(Boolean.FALSE);
                                    }
                                    break;
                                default:
                                    int value = Integer.valueOf(String.valueOf(row.getCell(3).getRichStringCellValue()));
                                    if (value == 1) {
                                        invoiceItem.setIsTaxIncluded(Boolean.TRUE);
                                    } else {
                                        invoiceItem.setIsTaxIncluded(Boolean.FALSE);
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            invoiceItem.setIsTaxIncluded(Boolean.FALSE);
                            invoiceItem.setExcelDataType(-1);
                        }
                    } else if (row.getCell(3) == null) {
                        invoiceItem.setIsTaxIncluded(Boolean.FALSE);
                        invoiceItem.setExcelDataType(-1);
                    }

                    /*Fatura ve item için gerkeli değerler set edilir. */
                    invoiceItem.setCurrency(invoice.getCurrency());
                    invoiceItem.setInvoice(invoice);
                    invoiceItem.setRowId(r);

                    excelStockList.add(invoiceItem);

                }
            }

            return excelStockList;
        } catch (IOException ex) {
            return new ArrayList<>();
        } catch (InvalidFormatException ex) {
            return new ArrayList<>();
        } catch (EncryptedDocumentException ex) {
            return new ArrayList<>();
        }
    }

    //Excel dosyasından okunan veriler için stok bilgilerini kontrol eder.
    @Override
    public List<InvoiceItem> processExcelUpload(List<InvoiceItem> excelList, Invoice invoice) {

        InvoiceItem invoiceItem = new InvoiceItem();
        List<InvoiceItem> excelStockList = new ArrayList<>();
        List<InvoiceItem> resultList = new ArrayList<>();
        String excelItemJson = jsonArrayForExcelUploadControl(excelList);
        resultList = invoiceItemDao.processUploadExcelItemsControl(excelItemJson, invoice);
        BigDecimal stockMandatoryPrice = BigDecimal.ZERO;
        try {

            List<InvoiceItem> list = new ArrayList();

            excelStockList.clear();
            for (InvoiceItem invitem : resultList) {
                boolean isMinStockLevel = false;
                boolean isMaxStockLevel = false;
                stockMandatoryPrice = invitem.getStock().getStockInfo().getSaleMandatoryPrice();
                invitem.getStock().getStockInfo().setSaleMandatoryPrice(BigDecimal.ZERO);

                invoiceItem = new InvoiceItem();
                invoiceItem = invitem;
                invoiceItem.setExcelDataType(1);

                invoiceItem.setQuantity(invitem.getQuantity().multiply(invitem.getStock().getAlternativeQuantity()));

                if (invoice.isIsPurchase() && invoice.getBranchSetting().getBranch().isIsAgency() && !sessionBean.getUser().isIsAuthorized()) {
                    if (invitem.getUnitPrice().compareTo(invitem.getStock().getStockInfo().getPurchaseRecommendedPrice()) != 0) {
                        invoiceItem.setExcelDataType(5);
                    }
                }

//                /*Fatura ve item için gerkeli değerler set edilir. */
                invoiceItem.setCurrency(invoice.getCurrency());
                invoiceItem.setInvoice(invoice);
                invoiceItem.setRowId(invitem.getRowId());
                invoiceItem.getStock().setBarcode(invitem.getStock().getBarcode());

                if (invitem.getStock().getId() != 0) {// Eğer gerçekten stok tablosunda stok varsa 

                    if ((invoice.isIsPurchase() && !invitem.getStock().getStockInfo().isIsDelist()) || !invoice.isIsPurchase()) {
                        invoiceItem.setStock(invitem.getStock());

                        if (!invoiceItem.getStock().getStockInfo().isIsMinusStockLevel()) {

                            if (invoiceItem.getStock().getAvailableQuantity() != null) {

                                if (invoice.getType().getId() == 59 && !invoice.isIsPurchase()) {
                                    if (invoiceItem.getStock().getAvailableQuantity().compareTo(invoiceItem.getQuantity()) == -1) {
                                        invoiceItem.setExcelDataType(2);
                                        excelStockList.add(invoiceItem);
                                        isMinStockLevel = true;
                                    }
                                }
                            }

                        }

                        if ((invoice.isIsPurchase() && invoice.getType().getId() == 59) && invoiceItem.getStock().getStockInfo().getMaxStockLevel() != null) {
                            if (invoiceItem.getStock().getStockInfo().getBalance() != null) {
                                BigDecimal purchaseAmount = BigDecimal.ZERO;
                                purchaseAmount = invoiceItem.getStock().getStockInfo().getMaxStockLevel().subtract(invoiceItem.getStock().getStockInfo().getBalance());
                                if (purchaseAmount.compareTo(invoiceItem.getQuantity()) == -1) {
                                    invoiceItem.setExcelDataType(3);
                                    excelStockList.add(invoiceItem);
                                    isMaxStockLevel = true;
                                }
                            }

                        }
                        if ((invoiceItem.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) && !isMaxStockLevel) {
                            if (invoiceItem.getStock().getStockInfo().isIsMinusStockLevel() || !isMinStockLevel) {

                                invoiceItem.setId(invitem.getId());
                                invoiceItem.setIsService(invitem.isIsService());
                                invoiceItem.setIsDiscountRate(invitem.isIsDiscountRate());
                                invoiceItem.setTaxRate(invitem.getTaxGroup().getId() == 0 ? BigDecimal.ZERO : invitem.getTaxGroup().getRate());
                                invoiceItem.setStockCount(1);
                                invoiceItem.setExchangeRate(BigDecimal.ONE);
                                invoiceItem.setRecommendedSalesPrice(BigDecimal.ZERO);

                                invoiceItem.setExcelIsTaxInclued(invitem.isIsTaxIncluded()); // önizleme ekranında kullanıcının girdiği bilgileri set edecek.
                                invoiceItem.setIsTaxIncluded(invitem.isIsTaxIncluded());
                                invoiceItem.setUnitPrice(invitem.getUnitPrice());
                                BigDecimal excelUnitPriceTaxExcluded = BigDecimal.ZERO;
                                if (!invoiceItem.isIsTaxIncluded()) { // kullanıcı excelden hariç gönderirse her zaman dahil fiyat hesaplanıp karşılaştırma yapılacak.
                                    if (invoiceItem.getTaxRate() != null) {
                                        excelUnitPriceTaxExcluded = invoiceItem.getUnitPrice().add(invoiceItem.getUnitPrice().multiply((invoiceItem.getTaxRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))));
                                    }
                                    invoiceItem.setExcelUnitPriceTaxExcluded(invoiceItem.getUnitPrice());
                                } else {
                                    BigDecimal unitPriceTaxExcluded = BigDecimal.ZERO;
                                    if (invoiceItem.getTaxRate() != null) {
                                        unitPriceTaxExcluded = invoiceItem.getUnitPrice().divide((BigDecimal.valueOf(1).add(invoiceItem.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN))), 4, RoundingMode.HALF_EVEN);
                                        invoiceItem.setExcelUnitPriceTaxExcluded(unitPriceTaxExcluded);

                                    }
                                    excelUnitPriceTaxExcluded = invoiceItem.getUnitPrice();
                                }
                                invoiceItem.setExcelUnitPrice(invoiceItem.getUnitPrice()); // önizleme pencerisnde kullanıcı ne girdi ise onu gösterecek , o sebepten başka bir değere set edildi.
                                invoiceItem.setIsTaxIncluded(true);
                                invoiceItem.setUnitPrice(excelUnitPriceTaxExcluded);

                                calculater(invoiceItem, 2);
                                // Eğer fatura satın alma faturası ise rafineri fiyat kontrolünü yap
                                if (invoiceItem.getInvoice().isIsPurchase()) {
                                    BigDecimal totalFuelPrice = BigDecimal.ZERO;
                                    if (invoiceItem.getStock().getStockInfo().isIsFuel()) { // akaryakıt ürünü mü ? o zaman rafineri fiyat kontrolünü yap.
                                        totalFuelPrice = fuelStockArticlesControl(invoiceItem.getStock().getId(), invoice, invoiceItem.getQuantity());
                                        if (invoiceItem.getUnitPrice().compareTo(totalFuelPrice) > 0) { // eğer girilen birim fiyat rafineri alım fiyatından büyük ise fiyatı set eder.
                                            invoiceItem.setFuelPrice(totalFuelPrice);
                                        }
                                    }

                                    /// Karlılık hesaplanarak önerilen satış fiyatı belirlenir.
                                    BigDecimal currentPurchasePrice = BigDecimal.ZERO;
                                    currentPurchasePrice = currentPurchasePrice.add(invoiceItem.getUnitPrice());

                                    // Eğer son alış fiyatı varsa kar oranını bul 
                                    if (invoiceItem.getStock().getStockInfo().getCurrentPurchasePrice().compareTo(BigDecimal.ZERO) >= 0) {
                                        invoiceItem.setLastPurchasePrice(invoiceItem.getStock().getStockInfo().getCurrentPurchasePrice());

                                        if (stockMandatoryPrice.compareTo(BigDecimal.ZERO) > 0) {
                                            BigDecimal excahnge = exchangeService.bringExchangeRate(invoiceItem.getStock().getStockInfo().getSaleMandatoryCurrency(), invoiceItem.getStock().getStockInfo().getCurrentPurchaseCurrency(), sessionBean.getUser());

                                            invoiceItem.setRecommendedSalesPrice(stockMandatoryPrice.multiply(excahnge));
                                            calculateProfit(invoiceItem, 2);

                                            invoiceItem.setProfitRate(invoiceItem.getProfitPercentage());
                                            invoiceItem.setIsThereMandatoryPrice(true);
                                        } else {
                                            // alış satış arasındaki kur farkını hesaplar.
                                            BigDecimal excahnge = exchangeService.bringExchangeRate(invoiceItem.getStock().getStockInfo().getSaleMandatoryCurrency(), invoiceItem.getStock().getStockInfo().getCurrentPurchaseCurrency(), sessionBean.getUser());

                                            if (invoiceItem.getStock().getStockInfo().getCurrentPurchasePrice().compareTo(BigDecimal.ZERO) != 0) {
                                                calculateProfit(invoiceItem, 2);
                                                invoiceItem.setProfitRate(invoiceItem.getProfitPercentage());
                                            } else {
                                                invoiceItem.setProfitRate(BigDecimal.ZERO);
                                            }
                                            invoiceItem.setIsThereMandatoryPrice(false);
                                            if (invoiceItem.getProfitRate() != null) {
                                                invoiceItem.setRecommendedSalesPrice((((BigDecimal.valueOf(100).add(invoiceItem.getProfitRate())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN))).multiply(currentPurchasePrice));
                                            }
                                        }
                                    }

                                } else if (!invoiceItem.getInvoice().isIsPurchase()) { // zorunlu fiyat var mı diye kontrol eder.
                                    //  Stock stockMandatory = stockDao.findSaleMandatoryPrice(invoiceItem.getStock().getId(), invoice.getBranchSetting());
                                    invoiceItem.getStock().getStockInfo().setSaleMandatoryPrice(stockMandatoryPrice);
                                    invoiceItem.getStock().getStockInfo().getSaleMandatoryCurrency().setId(invitem.getStock().getStockInfo().getSaleMandatoryCurrency().getId());
                                }

                                if (invoice.isIsPurchase() && invoiceItem.getExcelUnitPriceTaxExcluded().compareTo(stockMandatoryPrice) == 1 && stockMandatoryPrice.compareTo(BigDecimal.ZERO) == 1) {
                                    invoiceItem.setExcelDataType(7);
                                }

                                if (invoiceItem.getStock().getUnit().getUnitRounding() == 0) {

                                    int quantity = invoiceItem.getQuantity().intValue();

                                    if (invoiceItem.getQuantity().subtract(BigDecimal.valueOf(quantity)).compareTo(BigDecimal.ZERO) == 1) {
                                        invoiceItem.setExcelDataType(8);
                                    }

                                }

                                if (excelStockList.isEmpty()) {
                                    excelStockList.add(invoiceItem);
                                } else {
                                    boolean isThere = false;
                                    for (InvoiceItem item : excelStockList) {
                                        if (invoiceItem.getStock().getId() == item.getStock().getId()
                                                && invoiceItem.getUnitPrice().compareTo(item.getUnitPrice()) == 0 //hep kdv hariçleri karşılaştırdık.
                                                && invoiceItem.getTaxRate().compareTo(item.getTaxRate()) == 0) {
                                            isThere = false;
                                            item.setQuantity(invoiceItem.getQuantity().add(item.getQuantity()));

                                            calculater(item, 2);
                                            break;

                                        } else if (invoiceItem.getStock().getId() == item.getStock().getId() && invoiceItem.getUnitPrice().compareTo(item.getUnitPrice()) != 0) {
                                            // stok aynı fakat birim fiyat farklı ise
                                            invoiceItem.setStockCount(item.getStockCount() + 1);
                                            isThere = true;
                                        } else {
                                            isThere = true;
                                        }

                                    }
                                    if (isThere) {
                                        excelStockList.add(invoiceItem);
                                    }
                                }
                            }

                        }
                        invoiceItem.setIsFuelWarehouse(invitem.getIsFuelWarehouse());
                        invoiceItem.setIsFuelWarehouseItem(invitem.getIsFuelWarehouseItem());
                        int isFuelWarehouseItem = 1;
                        int isFuelWarehouse = 1;
                        if (invoice.getType().getId() == 59) {
                            if (invoice.getWarehouseIdList() != null && !invoice.getWarehouseIdList().equals("")) {
                                isFuelWarehouse = invoiceItem.getIsFuelWarehouse();
                                if (isFuelWarehouse == 1) {
                                    isFuelWarehouseItem = invoiceItem.getIsFuelWarehouseItem();
                                }
                            }

                        }
                        if (isFuelWarehouseItem == 0) {
                            invoiceItem.setExcelDataType(4);
                        }

                    } else {
                        invoiceItem.setExcelDataType(6);
                        invoiceItem.setQuantity(BigDecimal.ZERO);
                        excelStockList.add(invoiceItem);

                    }

                } else {
                    invoiceItem.setExcelDataType(-1);
                    invoiceItem.setQuantity(BigDecimal.ZERO);
                    excelStockList.add(invoiceItem);
                }

            }

            return excelStockList;
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public String jsonArrayForExcelUploadControl(List<InvoiceItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (InvoiceItem obj : list) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("barcode", obj.getStock().getBarcode());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("unitprice", obj.getUnitPrice());
            jsonObject.addProperty("is_taxincluded", obj.isIsTaxIncluded());
            jsonObject.addProperty("rowid", obj.getRowId());

            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public String jsonArrayForExcelUpload(Invoice invoice, List<InvoiceItem> list
    ) {
        JsonArray jsonArray = new JsonArray();
        for (InvoiceItem obj : list) {
            if (obj.getExcelDataType() == 1) { // hatalı olmayan kayıtlar alınır.
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("barcode", obj.getStock().getBarcode());
                jsonObject.addProperty("quantity", obj.getQuantity());
                jsonObject.addProperty("unitprice", obj.getUnitPrice());
                jsonObject.addProperty("processtype", obj.getProcessType());

                jsonObject.addProperty("id", obj.getId());
                jsonObject.addProperty("is_service", obj.isIsService());
                jsonObject.addProperty("stock_id", obj.getStock().getId());
                jsonObject.addProperty("unit_id", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getId() : obj.getStock().getUnit().getId());
                jsonObject.addProperty("unitprice", obj.getUnitPrice());
                jsonObject.addProperty("quantity", obj.getQuantity());
                jsonObject.addProperty("totalprice", obj.getTotalPrice());
                jsonObject.addProperty("taxrate", obj.getTaxRate());
                jsonObject.addProperty("totaltax", obj.getTotalTax());
                jsonObject.addProperty("is_discountrate", obj.isIsDiscountRate());
                jsonObject.addProperty("discountrate", obj.getDiscountRate() == null ? 0 : obj.getDiscountRate());
                jsonObject.addProperty("discountprice", obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice());
                jsonObject.addProperty("is_discountrate2", obj.isIsDiscountRate2());
                jsonObject.addProperty("discountrate2", obj.getDiscountRate2() == null ? 0 : obj.getDiscountRate2());
                jsonObject.addProperty("discountprice2", obj.getDiscountPrice2() == null ? 0 : obj.getDiscountPrice2());
                jsonObject.addProperty("currency_id", obj.getCurrency().getId());
                jsonObject.addProperty("exchangerate", obj.getExchangeRate());
                jsonObject.addProperty("totalmoney", obj.getTotalMoney());
                jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
                jsonObject.addProperty("stockcount", obj.getStockCount());
                jsonObject.addProperty("waybillitem_id", obj.getWaybillItemIds() == null ? null : obj.getWaybillItemIds());//irsaliyeden aktarıldı ise
                jsonObject.addProperty("managerUserDataid", ((obj.getDiscountPrice() == null || obj.getDiscountPrice().doubleValue() == 0)
                        && (obj.getDiscountPrice2() == null || obj.getDiscountPrice2().doubleValue() == 0)) ? null : sessionBean.getUser().getId());
                jsonObject.addProperty("isManagerDiscount", ((obj.getDiscountPrice() != null && obj.getDiscountPrice().doubleValue() > 0)
                        || (obj.getDiscountPrice2() != null && obj.getDiscountPrice2().doubleValue() > 0)));
                jsonObject.addProperty("recommendedprice", obj.getStock().getStockInfo().getRecommendedPrice() == null ? BigDecimal.ZERO : obj.getStock().getStockInfo().getRecommendedPrice());
                jsonObject.addProperty("warehouse_id", obj.getInvoice().getListOfWarehouse().isEmpty() ? 0 : obj.getInvoice().getListOfWarehouse().get(0).getId());
                jsonObject.addProperty("is_free", obj.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 0 ? true : false);

                jsonArray.add(jsonObject);
            }
        }
        return invoiceItemDao.excelItemInsert(invoice, jsonArray.toString());
    }

    /**
     * Bu metot akaryakıy ürünü için tüpraş fiyatını kontrol eder.Satın alınan
     * fiyat hesaplanan fiyattan fazla ise uyarı verir.
     */
    public BigDecimal fuelStockArticlesControl(int stockId, Invoice selectedInvoice, BigDecimal quantity) {
        ContractArticles contractArticles = new ContractArticles();
        RefineryStockPrice refineryStockPrice = new RefineryStockPrice();

        BigDecimal total = BigDecimal.ZERO;

        contractArticles = contractArticlesDao.findStockArticles(stockId, selectedInvoice.getBranchSetting().getBranch());
        refineryStockPrice = refineryPurchaseDao.findStockRefineryPrice(stockId, selectedInvoice.getBranchSetting().getBranch());
        InvoiceItem invoiceItem = new InvoiceItem();

        Date begin = new Date();
        Date end = new Date();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        begin = (calendar.getTime());
        invoiceItem = totalQuantityForInvoice(selectedInvoice, stockId, begin, end, selectedInvoice.getBranchSetting().getBranch());
        BigDecimal generalQuantity = BigDecimal.ZERO;
        if (invoiceItem.getQuantity() != null) {
            if (invoiceItem.getQuantity().compareTo(BigDecimal.ZERO) == 0) { // o ay içerisindeki o ürüne ait tüm alışları topladık.
                generalQuantity = quantity;
            } else {
                generalQuantity = invoiceItem.getQuantity().add(quantity);
            }
        } else {
            generalQuantity = quantity;
        }

        if (refineryStockPrice.getId() != 0 && refineryStockPrice.getPrice().compareTo(BigDecimal.ZERO) > 0 && contractArticles.getId() != 0) {
            total = BigDecimal.ZERO;
            BigDecimal priceRate = BigDecimal.ZERO;
            switch (contractArticles.getArticltType()) {
                case 1:
                    priceRate = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(contractArticles.getRate1())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                    total = (refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(priceRate);
                    break;
                case 2:
                    BigDecimal rate = BigDecimal.ZERO;
                    BigDecimal rateVal = BigDecimal.ZERO;
                    BigDecimal volume = BigDecimal.ZERO;
                    BigDecimal remainingVolume = BigDecimal.ZERO;
                    if (contractArticles.getVolume1().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume1()) == -1 || generalQuantity.compareTo(contractArticles.getVolume1()) == 0)) {
                        rate = contractArticles.getRate1();

                    } else if (contractArticles.getVolume2().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume2()) == -1 || generalQuantity.compareTo(contractArticles.getVolume2()) == 0) && generalQuantity.compareTo(contractArticles.getVolume1()) > 0) {
                        if (contractArticles.getVolume1().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume1()) == 1)) {
                            rateVal = contractArticles.getRate1();
                            volume = contractArticles.getVolume1();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume1());
                        }
                        rate = contractArticles.getRate2();
                    } else if (contractArticles.getVolume3().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume3()) == -1 || generalQuantity.compareTo(contractArticles.getVolume3()) == 0) && generalQuantity.compareTo(contractArticles.getVolume2()) > 0) {
                        if (contractArticles.getVolume2().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume2()) == 1)) {
                            rateVal = contractArticles.getRate2();
                            volume = contractArticles.getVolume2();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume2());

                        }
                        rate = contractArticles.getRate3();
                    } else if (contractArticles.getVolume4().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume4()) == -1 || generalQuantity.compareTo(contractArticles.getVolume4()) == 0) && generalQuantity.compareTo(contractArticles.getVolume3()) > 0) {
                        if (contractArticles.getVolume3().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume3()) == 1)) {
                            rateVal = contractArticles.getRate3();
                            volume = contractArticles.getVolume3();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume3());

                        }
                        rate = contractArticles.getRate4();
                    } else if (contractArticles.getVolume5().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume5()) == -1 || generalQuantity.compareTo(contractArticles.getVolume5()) == 0) && generalQuantity.compareTo(contractArticles.getVolume4()) > 0) {
                        if (contractArticles.getVolume4().compareTo(BigDecimal.ZERO) == 1 && (generalQuantity.compareTo(contractArticles.getVolume4()) == 1)) {
                            rateVal = contractArticles.getRate4();
                            volume = contractArticles.getVolume4();
                            remainingVolume = generalQuantity.subtract(contractArticles.getVolume4());
                        }
                        rate = contractArticles.getRate5();
                    }

                    if (rate.compareTo(BigDecimal.ZERO) == 1 && rateVal.compareTo(BigDecimal.ZERO) == 0) {
                        priceRate = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(rate)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        total = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(priceRate));
                    } else if (rate.compareTo(BigDecimal.ZERO) == 1 && rateVal.compareTo(BigDecimal.ZERO) == 1) {
                        BigDecimal val1 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(rateVal)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        BigDecimal tot1 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(val1));

                        BigDecimal val2 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).multiply(rate)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        BigDecimal tot2 = ((refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost())).add(val2));

                        total = (((tot1.multiply(volume)).add(tot2.multiply(remainingVolume))).divide(generalQuantity, 4, RoundingMode.HALF_EVEN));
                    } else {
                        total = BigDecimal.ZERO;
                    }
                    break;
                case 3:
                    Stock stock = new Stock();
                    stock.setId(stockId);
                    PriceListItem priceList = priceListItemDao.findStockPrice(stock, false, selectedInvoice.getBranchSetting().getBranch());
                    BigDecimal t1 = BigDecimal.ZERO;
                    BigDecimal m1 = BigDecimal.ZERO;
                    BigDecimal m2 = BigDecimal.ZERO;
                    if (priceList.getPrice().compareTo(BigDecimal.ZERO) == 1) {
                        t1 = refineryStockPrice.getPrice().add(contractArticles.getWarehouseCost());
                        m1 = priceList.getPrice().subtract(t1);
                        priceRate = (m1.multiply(contractArticles.getBranchProfitRate())).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
                        total = priceList.getPrice().subtract(priceRate);
                    }
                    break;
            }
        }
        return total;
    }

    @Override
    public List<InvoiceItem> findInvoiceItemLastPrice(String stockList, BranchSetting branchSetting) {
        return invoiceItemDao.findInvoiceItemLastPrice(stockList, branchSetting);
    }

    @Override
    public List<CheckDelete> testBeforeDelete(InvoiceItem invoiceitem) {
        return invoiceItemDao.testBeforeDelete(invoiceitem);
    }

    @Override
    public String jsonArrayInvoiceItemsForWaitedInvoice(List<InvoiceItem> list, Invoice invoice) {
        JsonArray jsonArray = new JsonArray();
        for (InvoiceItem obj : list) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("is_service", obj.isIsService());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("stockname", obj.getStock().getName() == null ? "" : obj.getStock().getName());
            jsonObject.addProperty("stockcode", obj.getStock().getCode() == null ? "" : obj.getStock().getCode());
            jsonObject.addProperty("stockbarcode", obj.getStock().getBarcode() == null ? "" : obj.getStock().getBarcode());
            jsonObject.addProperty("unit_id", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getId() : obj.getStock().getUnit().getId());
            jsonObject.addProperty("sortname", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getSortName() : obj.getStock().getUnit().getSortName());
            jsonObject.addProperty("unitname", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getName() : obj.getStock().getUnit().getName());
            jsonObject.addProperty("unitrounding", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getUnitRounding() : obj.getStock().getUnit().getUnitRounding());
            //kdv hariç ise kdv'li birim fiyat hesapla
            if (!obj.isIsTaxIncluded()
                    && obj.getTaxRate() != null
                    && obj.getTaxRate().doubleValue() > 0
                    && obj.getUnitPrice() != null
                    && obj.getUnitPrice().doubleValue() > 0) {

                BigDecimal x = BigDecimal.ONE.add(obj.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                jsonObject.addProperty("unitprice", obj.getUnitPrice().multiply(x));
            } else {
                jsonObject.addProperty("unitprice", obj.getUnitPrice());
            }
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("totalprice", obj.getTotalPrice());
            jsonObject.addProperty("taxrate", obj.getTaxRate());
            jsonObject.addProperty("totaltax", obj.getTotalTax());
            jsonObject.addProperty("is_discountrate", obj.isIsDiscountRate());
            jsonObject.addProperty("discountrate", obj.getDiscountRate() == null ? 0 : obj.getDiscountRate());
            jsonObject.addProperty("discountprice", obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice());
            jsonObject.addProperty("is_discountrate2", obj.isIsDiscountRate2());
            jsonObject.addProperty("discountrate2", obj.getDiscountRate2() == null ? 0 : obj.getDiscountRate2());
            jsonObject.addProperty("discountprice2", obj.getDiscountPrice2() == null ? 0 : obj.getDiscountPrice2());
            jsonObject.addProperty("currency_id", obj.getCurrency().getId());
            jsonObject.addProperty("exchangerate", obj.getExchangeRate());
            jsonObject.addProperty("totalmoney", obj.getTotalMoney());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("is_free", obj.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 0 ? true : false);
            jsonObject.addProperty("currencycode", obj.getCurrency().getCode() == null ? "" : obj.getCurrency().getCode());
            jsonObject.addProperty("currencyname", obj.getCurrency().getTag() == null ? "" : obj.getCurrency().getTag());
            jsonObject.addProperty("waybillitem_id", ((obj.getWaybillItemIds() == null || obj.getWaybillItemIds().equals("")) ? null : obj.getWaybillItemIds()));//irsaliyeden aktarıldı is
            int sCount = 0;
            if (invoice.getType().getId() == 59) {
                for (InvoiceItem item : list) {
                    if (item.getStock().getId() == obj.getStock().getId()) {
                        sCount++;
                    }
                }
            }
            jsonObject.addProperty("stockcount", sCount);
            jsonObject.addProperty("managerUserDataid", ((obj.getDiscountPrice() == null || obj.getDiscountPrice().doubleValue() == 0)
                    && (obj.getDiscountPrice2() == null || obj.getDiscountPrice2().doubleValue() == 0)) ? null : sessionBean.getUser().getId());
            jsonObject.addProperty("isManagerDiscount", ((obj.getDiscountPrice() != null && obj.getDiscountPrice().doubleValue() > 0)
                    || (obj.getDiscountPrice2() != null && obj.getDiscountPrice2().doubleValue() > 0)));
            jsonObject.addProperty("recommendedprice", obj.getStock().getStockInfo().getRecommendedPrice() == null ? BigDecimal.ZERO : obj.getStock().getStockInfo().getRecommendedPrice());
            jsonObject.addProperty("warehouse_id", invoice.getListOfWarehouse().isEmpty() ? 0 : invoice.getListOfWarehouse().get(0).getId());
            jsonObject.addProperty("differentinvoiceitem_id", obj.getPriceDifferentInvoiceItem() != null ? obj.getPriceDifferentInvoiceItem().getId() == 0 ? null : obj.getPriceDifferentInvoiceItem().getId() : null);
            jsonObject.addProperty("differenttotalmoney", obj.getPriceDifferentTotalMoney() == null ? 0 : obj.getPriceDifferentTotalMoney());
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public int updateWaitedInvoiceJson(List<InvoiceItem> listOfItem, Invoice invoice) {
        return invoiceItemDao.updateWaitedInvoiceJson(jsonArrayInvoiceItemsForWaitedInvoice(listOfItem, invoice), invoice);
    }

    @Override
    public String jsonArrayInvoiceItemsforOrder(List<InvoiceItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (InvoiceItem obj : list) {

            //kdv hariç ise kdv'li birim fiyat hesapla
            if (!obj.isIsTaxIncluded()
                    && obj.getTaxRate() != null
                    && obj.getTaxRate().doubleValue() > 0
                    && obj.getUnitPrice() != null
                    && obj.getUnitPrice().doubleValue() > 0) {

                BigDecimal x = BigDecimal.ONE.add(obj.getTaxRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_EVEN));
                obj.setUnitPrice(obj.getUnitPrice().multiply(x));
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("is_service", obj.isIsService());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getStock().getUnit().getId() == 0 ? obj.getUnit().getId() : obj.getStock().getUnit().getId());
            jsonObject.addProperty("unitprice", obj.getUnitPrice());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("totalprice", obj.getTotalPrice());
            jsonObject.addProperty("taxrate", obj.getTaxRate());
            jsonObject.addProperty("totaltax", obj.getTotalTax());
            jsonObject.addProperty("is_discountrate", obj.isIsDiscountRate());
            jsonObject.addProperty("discountrate", obj.getDiscountRate() == null ? 0 : obj.getDiscountRate());
            jsonObject.addProperty("discountprice", obj.getDiscountPrice() == null ? 0 : obj.getDiscountPrice());
            jsonObject.addProperty("is_discountrate2", obj.isIsDiscountRate2());
            jsonObject.addProperty("discountrate2", obj.getDiscountRate2() == null ? 0 : obj.getDiscountRate2());
            jsonObject.addProperty("discountprice2", obj.getDiscountPrice2() == null ? 0 : obj.getDiscountPrice2());
            jsonObject.addProperty("currency_id", obj.getCurrency().getId());
            jsonObject.addProperty("exchangerate", obj.getExchangeRate());
            jsonObject.addProperty("totalmoney", obj.getTotalMoney());
            jsonObject.addProperty("description", obj.getDescription() == null ? "" : obj.getDescription());
            jsonObject.addProperty("stockcount", obj.getStockCount());
            jsonObject.addProperty("waybillitem_id", obj.getWaybillItemIds());//irsaliyeden aktarıldı ise
            jsonObject.addProperty("managerUserDataid", ((obj.getDiscountPrice() == null || obj.getDiscountPrice().doubleValue() == 0)
                    && (obj.getDiscountPrice2() == null || obj.getDiscountPrice2().doubleValue() == 0)) ? null : sessionBean.getUser().getId());
            jsonObject.addProperty("isManagerDiscount", ((obj.getDiscountPrice() != null && obj.getDiscountPrice().doubleValue() > 0)
                    || (obj.getDiscountPrice2() != null && obj.getDiscountPrice2().doubleValue() > 0)));
            jsonObject.addProperty("recommendedprice", obj.getStock().getStockInfo().getRecommendedPrice() == null ? BigDecimal.ZERO : obj.getStock().getStockInfo().getRecommendedPrice());
            jsonObject.addProperty("warehouse_id", obj.getInvoice().getListOfWarehouse().isEmpty() ? 0 : obj.getInvoice().getListOfWarehouse().get(0).getId());
            jsonObject.addProperty("is_free", obj.getUnitPrice().compareTo(BigDecimal.valueOf(0)) == 0 ? true : false);
            jsonObject.addProperty("differentinvoiceitem_id", obj.getPriceDifferentInvoiceItem() != null ? obj.getPriceDifferentInvoiceItem().getId() == 0 ? null : obj.getPriceDifferentInvoiceItem().getId() : null);
            jsonObject.addProperty("differenttotalmoney", obj.getPriceDifferentTotalMoney() == null ? 0 : obj.getPriceDifferentTotalMoney());
            JsonArray jsonArrayOrderItems = new JsonArray();
            System.out.println("obj.getOrderItemIds()" + obj.getOrderItemIds());
            System.out.println("obj.getOrderItemQuantitys()" + obj.getOrderItemQuantitys());
            String[] orderitemids = obj.getOrderItemIds().split(",");
            String[] quantitys = obj.getOrderItemQuantitys().split(",");
            for (int i = 0; i < orderitemids.length; i++) {
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("orderitem_id", Integer.parseInt(orderitemids[i]));
                jsonObject1.addProperty("quantity", new BigDecimal(quantitys[i]));
                jsonArrayOrderItems.add(jsonObject1);

            }

            jsonObject.add("orderitem_json", jsonArrayOrderItems);
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }

    @Override
    public List<Warehouse> findFuelStockWarehouse(InvoiceItem invoiceItem, Invoice inv) {

        return invoiceItemDao.findFuelStockWarehouse(invoiceItem, inv);
    }

    @Override
    public List<BranchSetting> findUserAuthorizeBranch() {
        return invoiceItemDao.findUserAuthorizeBranch();
    }

}
