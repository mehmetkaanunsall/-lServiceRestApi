/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 01.02.2017 13:47:49
 */
package com.mepsan.marwiz.general.history.business;

import com.mepsan.marwiz.general.model.general.History;
import java.util.List;
import java.util.Map;

public interface IHistoryService {

    public List<History> findAll(int first, int pageSize, Map<String, Object> filters, String where, int rowId, String tableName,int pageId);

    public int count(String where, int rowId,String tableName,int pageId);

    public String bringWhereForLimit(String type,String  table, int id);
}
