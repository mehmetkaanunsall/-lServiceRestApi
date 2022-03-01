/**
 *
 *
 *
 * @author SALİM VELA ABDULHADİ
 *
 * @date 10.10.2016 11:25:03
 */
package com.mepsan.marwiz.general.history.dao;

import com.mepsan.marwiz.general.model.general.History;
import java.util.List;
import java.util.Map;

public interface IHistoryDao {

    public List<History> findAll(int first, int pageSize, Map<String, Object> filters, String where, int rowId, String tableName,int pageId);

    public int count(String where, int rowId, String tableName,int pageId);

}
