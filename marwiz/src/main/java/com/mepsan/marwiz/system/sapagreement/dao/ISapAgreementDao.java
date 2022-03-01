/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapagreement.dao;

import com.mepsan.marwiz.general.model.general.Exchange;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface ISapAgreementDao {

    public List<Exchange> findAllExchange(Date beginDate, Date endDate);

    public List<SapAgreement> listCurrency();

    public List<SapAgreement> findMarketSalesTotal(Date beginDate, Date endDate);

    public List<SapAgreement> findMarketSaleReturnTotal(Date beginDate, Date endDate);

    public int save(String automationJson, String posSaleJson, String expenseJson, String exchangeJson, String fuelZJson, String marketZJson, String safeTransferJson, String bankSendJson, String totalJson, Date date, int dateint, BigDecimal automationSaleDifference, int type, String sendData, boolean isSend, Date sendDate, String response, BigDecimal marketSaleDifference);

    public int insertOrUpdateLog(SapAgreement sap, BigDecimal automationSaleDifference, int type, BigDecimal marketSaleDifference);

    public List<SapAgreement> findall(Date beginDate, Date endDate, Date date);

    public List<SapAgreement> calculateTransferSaleDiffAmount(Date beginDate, Date endDate);

    public int delete(SapAgreement obj);

    public int update(SapAgreement obj);

//    public List<SapAgreement> findPaymentType(Date beginDate, Date endDate);

}
