/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 09.04.2019 08:18:39
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscountItem extends WotLogging {

    private int id;
    private Discount discount;
    private Stock stock;
    private Brand brand;
    private BigDecimal discountRate;
    private BigDecimal discountAmount;
    private PriceList priceList;
    private Boolean isTaxIncluded;
    private int saleCount;
    private Date beginDate;
    private Date endDate;
    private BigDecimal beginPrice;
    private BigDecimal endPrice;
    private Date beginTime;
    private Date endTime;
    private String specialDay;
    private String specialMonth;
    private String specialMonthDay;
    private boolean isDiscountCode;
    private String necessaryStocks;
    private String promotionStocks;
    private String necessaryBrands;
    private String promotionBrands;

    private List<String> specialDays;
    private List<String> specialMonths;
    private List<String> specialMonthDays;

    private List<Stock> necessaryStockList;
    private List<Stock> promotionStockList;
    private List<Brand> necessaryBrandList;
    private List<Brand> promotionBrandList;

    public DiscountItem() {
        this.discount = new Discount();
        this.stock = new Stock();
        this.brand = new Brand();
        this.priceList = new PriceList();
        this.specialDays = new ArrayList<>();
        this.specialMonths = new ArrayList<>();
        this.specialMonthDays = new ArrayList<>();
        this.necessaryStockList = new ArrayList<>();
        this.promotionStockList = new ArrayList<>();
        this.necessaryBrandList = new ArrayList<>();
        this.promotionBrandList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public PriceList getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }

    public Boolean getIsTaxIncluded() {
        return isTaxIncluded;
    }

    public void setIsTaxIncluded(Boolean isTaxIncluded) {
        this.isTaxIncluded = isTaxIncluded;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBeginPrice() {
        return beginPrice;
    }

    public void setBeginPrice(BigDecimal beginPrice) {
        this.beginPrice = beginPrice;
    }

    public BigDecimal getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(BigDecimal endPrice) {
        this.endPrice = endPrice;
    }

    public String getSpecialDay() {
        return specialDay;
    }

    public void setSpecialDay(String specialDay) {
        this.specialDay = specialDay;
    }

    public String getSpecialMonth() {
        return specialMonth;
    }

    public void setSpecialMonth(String specialMonth) {
        this.specialMonth = specialMonth;
    }

    public String getSpecialMonthDay() {
        return specialMonthDay;
    }

    public void setSpecialMonthDay(String specialMonthDay) {
        this.specialMonthDay = specialMonthDay;
    }

    public boolean isIsDiscountCode() {
        return isDiscountCode;
    }

    public void setIsDiscountCode(boolean isDiscountCode) {
        this.isDiscountCode = isDiscountCode;
    }

    public List<String> getSpecialDays() {
        return specialDays;
    }

    public void setSpecialDays(List<String> specialDays) {
        this.specialDays = specialDays;
    }

    public List<String> getSpecialMonths() {
        return specialMonths;
    }

    public void setSpecialMonths(List<String> specialMonths) {
        this.specialMonths = specialMonths;
    }

    public List<String> getSpecialMonthDays() {
        return specialMonthDays;
    }

    public void setSpecialMonthDays(List<String> specialMonthDays) {
        this.specialMonthDays = specialMonthDays;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getNecessaryStocks() {
        return necessaryStocks;
    }

    public void setNecessaryStocks(String necessaryStocks) {
        this.necessaryStocks = necessaryStocks;
    }

    public String getPromotionStocks() {
        return promotionStocks;
    }

    public void setPromotionStocks(String promotionStocks) {
        this.promotionStocks = promotionStocks;
    }

    public String getNecessaryBrands() {
        return necessaryBrands;
    }

    public void setNecessaryBrands(String necessaryBrands) {
        this.necessaryBrands = necessaryBrands;
    }

    public String getPromotionBrands() {
        return promotionBrands;
    }

    public void setPromotionBrands(String promotionBrands) {
        this.promotionBrands = promotionBrands;
    }

    public List<Stock> getNecessaryStockList() {
        return necessaryStockList;
    }

    public void setNecessaryStockList(List<Stock> necessaryStockList) {
        this.necessaryStockList = necessaryStockList;
    }

    public List<Stock> getPromotionStockList() {
        return promotionStockList;
    }

    public void setPromotionStockList(List<Stock> promotionStockList) {
        this.promotionStockList = promotionStockList;
    }

    public List<Brand> getNecessaryBrandList() {
        return necessaryBrandList;
    }

    public void setNecessaryBrandList(List<Brand> necessaryBrandList) {
        this.necessaryBrandList = necessaryBrandList;
    }

    public List<Brand> getPromotionBrandList() {
        return promotionBrandList;
    }

    public void setPromotionBrandList(List<Brand> promotionBrandList) {
        this.promotionBrandList = promotionBrandList;
    }

}
