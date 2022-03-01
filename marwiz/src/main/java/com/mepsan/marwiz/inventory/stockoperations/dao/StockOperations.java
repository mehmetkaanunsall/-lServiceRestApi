/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 17.01.2019 11:03:19
 */
package com.mepsan.marwiz.inventory.stockoperations.dao;

import com.mepsan.marwiz.general.dashboard.dao.NotificationRecommendedPrice;
import com.mepsan.marwiz.general.dashboard.dao.UserNotification;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockOperations {

    private int id;
    private Stock stock;
    private int changeType;//Değişiklik Yapan
    private int process;//İşlem->Fiyat Değişikliği -> Fiyat Önerisi
    private Date processDate;
    private String description;
    private int centerstock_id;
    private String stockname;
    private String processdate;
    private BigDecimal price;
    private BigDecimal oldPrice;
    private Currency currency;
    private Currency oldCurrency;
    private BigDecimal saleMandatoryPrice;
    private List<Categorization> listOfCategorization;
    private List<CentralSupplier> listOfCentralSupplier;
    private List<Account>listOfAccount;
    private Account account;
    private int ticketCount;

    public StockOperations() {
        this.stock = new Stock();
        this.currency = new Currency();
        this.oldCurrency = new Currency();
        this.listOfCategorization = new ArrayList<>();
        this.listOfCentralSupplier = new ArrayList<>();
        this.listOfAccount = new ArrayList<>();
        this.account = new Account();
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getChangeType() {
        return changeType;
    }

    public void setChangeType(int changeType) {
        this.changeType = changeType;
    }

    public int getProcess() {
        return process;
    }

    public void setProcess(int process) {
        this.process = process;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCenterstock_id() {
        return centerstock_id;
    }

    public void setCenterstock_id(int centerstock_id) {
        this.centerstock_id = centerstock_id;
    }

    public String getStockname() {
        return stockname;
    }

    public void setStockname(String stockname) {
        this.stockname = stockname;
    }

    public String getProcessdate() {
        return processdate;
    }

    public void setProcessdate(String processdate) {
        this.processdate = processdate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(BigDecimal oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getOldCurrency() {
        return oldCurrency;
    }

    public void setOldCurrency(Currency oldCurrency) {
        this.oldCurrency = oldCurrency;
    }

    public BigDecimal getSaleMandatoryPrice() {
        return saleMandatoryPrice;
    }

    public void setSaleMandatoryPrice(BigDecimal saleMandatoryPrice) {
        this.saleMandatoryPrice = saleMandatoryPrice;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public List<CentralSupplier> getListOfCentralSupplier() {
        return listOfCentralSupplier;
    }

    public void setListOfCentralSupplier(List<CentralSupplier> listOfCentralSupplier) {
        this.listOfCentralSupplier = listOfCentralSupplier;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }

}
