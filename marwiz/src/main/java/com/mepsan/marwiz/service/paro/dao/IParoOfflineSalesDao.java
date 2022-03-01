/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2019 08:27:20
 */
package com.mepsan.marwiz.service.paro.dao;

import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.service.model.LogParo;
import java.util.List;

public interface IParoOfflineSalesDao {

    public List<LogParo> listOfLog();

    public int updateSaleLog(LogParo logParo);

    public int updateAllRequestLog(String transactionNo, long requestId);

    public LogParo selectParoPayment(long requestId);

    public int createSaleLog(LogParo logParo);

    public int updateSale(String transactionNo, int saleId);

    public int createJsonSale(LogParo logParo);

    public boolean isThereFinishedSale(String transactionNo);

    public List<PointOfSale> listPointOfSale(int branch_id);

    public int updateSaleCancelLog(LogParo logParo);
}
