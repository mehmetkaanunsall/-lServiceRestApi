/**
 *
 *
 *
 * @author SALEM WALAA ABDULHADIE
 *
 * @date 10.10.2016 11:22:37
 */
package com.mepsan.marwiz.general.history.dao;

import com.mepsan.marwiz.general.model.general.History;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class HistoryDao extends JdbcDaoSupport implements IHistoryDao {

    /**
     * Bu metot verilen sayfanın Tarihçesini listeler. Kullanıcak bütün
     * sayfalardan ortak bir şekilde çalışır.
     *
     * @param first
     * @param pageSize
     * @param filters
     * @param where
     * @param rowId
     * @param schemeName
     * @param tableName
     * @return History obje listelerini döndürür.
     */
    @Override
    public List<History> findAll(int first, int pageSize, Map<String, Object> filters, String where, int rowId, String tableName, int pageId) {

        String sql = "SELECT * FROM general.list_history(?,?,?,?,?,?);";
        Object[] param = new Object[]{
            String.valueOf(first),
            String.valueOf(pageSize),
            where.isEmpty() ? " " : where,
            String.valueOf(rowId),
            tableName,
            String.valueOf(pageId)
        };

        System.out.println("---- " + Arrays.toString(param));
        List<History> result = getJdbcTemplate().query(sql, param, new HistoryMapper());

        return result;
    }

    /**
     * Bu metot verilen sayfanın Tarihçesini listenin sayısını belirtir.
     *
     * @param where
     * @param rowId
     * @param tableName
     * @return history saysını döndürür..
     */
    @Override
    public int count(String where, int rowId, String tableName, int pageId) {
        String sql = "SELECT * FROM general.list_history_count (?,?,?,?);";
        Object[] param = new Object[]{
            where,
            String.valueOf(rowId),
            tableName,
            String.valueOf(pageId)
        };
        int result = getJdbcTemplate().queryForObject(sql, param, Integer.class);
        return result;
    }

}
