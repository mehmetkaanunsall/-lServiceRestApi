/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 19.07.2019 09:07:27
 */
package com.mepsan.marwiz.service.paro.business;

import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.service.model.LogParo;
import java.util.List;

public interface IParoOfflineSalesService {

    public void sendSalesAsync();

    public void sendSales(List<LogParo> listOfLog);

    public LogParo parseResponse(String response,int typeId);

}
