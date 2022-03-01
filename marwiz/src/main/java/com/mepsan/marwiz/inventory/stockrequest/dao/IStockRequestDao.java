/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   18.04.2018 12:19:42
 */
package com.mepsan.marwiz.inventory.stockrequest.dao;

import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IStockRequestDao extends ICrud<StockRequest> {

    public List<StockRequest> findall();

    public int update(StockRequest obj, boolean isSend, int type);

    public List<SendStockRequest> checkRequestSend(StockRequest obj);

    public List<SendStockRequest> checkRequestSendAllRecord(StockRequest obj);

    public int controlStockRequest(String where, StockRequest stockRequest);
}
