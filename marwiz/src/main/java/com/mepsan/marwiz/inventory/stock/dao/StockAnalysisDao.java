/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.02.2018 17:15:59
 */
package com.mepsan.marwiz.inventory.stock.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class StockAnalysisDao extends JdbcDaoSupport implements IStockAnalysisDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public StockAnalysis selectStockAnalysis(Stock stock, Branch branch) {
        String sql = "SELECT\n"
                + "	isi.currentpurchasecurrency_id as lastpurchasecurrency_id,\n"
                + "	isi.currentpurchaseprice as lastpurchaseprice,\n"
                + "	isi.currentsalecurrency_id as lastsalecurrency_id,\n"
                + "	isi.currentsaleprice as lastsaleprice,\n"
                + "   sale.lastmonth AS lastmonth,\n"
                + "   sale.lastweek AS lastweek,\n"
                + "   sale.lastday AS lastday\n"
                + "FROM inventory.stock stck \n"
                + "	LEFT JOIN inventory.stockinfo isi ON (stck.id=isi.stock_id AND isi.branch_id=? AND isi.deleted=FALSE)\n"
                + "   LEFT JOIN (\n"
                + "    	SELECT \n"
                + "               sli.stock_id AS slistock_id,\n"
                + "               SUM(CASE WHEN sl.is_return = TRUE THEN -sli.quantity ELSE sli.quantity END) as lastmonth, \n"
                + "               SUM(CASE WHEN sl.is_return = TRUE AND date_part('week',  sl.processdate) =date_part('week',  CURRENT_DATE) THEN  -sli.quantity \n"
                + "      		WHEN date_part('week',  sl.processdate) =date_part('week',  CURRENT_DATE) THEN sli.quantity \n"
                + "         		ELSE 0 END) as lastweek,\n"
                + "               SUM(CASE WHEN sl.is_return = TRUE AND date_part('doy',  sl.processdate) =date_part('doy',  CURRENT_DATE) THEN  -sli.quantity\n"
                + "      		WHEN date_part('doy',  sl.processdate) =date_part('doy',  CURRENT_DATE) THEN sli.quantity \n"
                + "         		ELSE 0 END) as lastday \n"
                + "		FROM general.sale sl \n"
                + "               INNER JOIN general.saleitem sli ON(sli.sale_id=sl.id AND sli.deleted=FALSE)\n"
                + "		WHERE sl.branch_id=? AND sl.deleted=False AND sli.is_calcincluded = FALSE\n"
                + "               AND date_part('month',  sl.processdate) =date_part('month', CURRENT_DATE) AND date_part('year', sl.processdate) =date_part('year',  CURRENT_DATE)\n"
                + "    	GROUP BY sli.stock_id\n"
                + "    ) sale ON (sale.slistock_id=stck.id)\n"
                + "WHERE stck.id = ?";

        Object[] param = new Object[]{branch.getId(), branch.getId(), stock.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, new StockAnalysisMapper());
        } catch (EmptyResultDataAccessException e) {
            return new StockAnalysis();
        }
    }

    @Override
    public List<StockAnalysis> listOfMonthAverage(Stock stock, Branch branch) {
        String sql = "SELECT\n"
                + "    COALESCE(SUM(CASE WHEN s.is_return = TRUE THEN -si.quantity ELSE si.quantity END),0) as lastmonth\n"
                + "    ,date_part('day', s.processdate) as day\n"
                + "FROM general.saleitem si \n"
                + "INNER JOIN general.sale s ON (si.sale_id = s.id AND s.deleted = FALSE)\n"
                + "WHERE si.stock_id = ? AND s.branch_id=?\n"
                + "AND si.deleted = FALSE AND si.is_calcincluded = FALSE\n"
                + "AND s.processdate > date_trunc('month', current_date)\n"
                + "GROUP BY si.stock_id,day\n"
                + "ORDER BY day ";

        Object[] param = new Object[]{stock.getId(), branch.getId()};
        return getJdbcTemplate().query(sql, param, new StockAnalysisMapper());
    }

    @Override
    public List<StockAnalysis> listOfThreeMonthAverage(Stock stock, Branch branch) {
        String sql = "SELECT\n"
                + "    COALESCE(SUM(CASE WHEN s.is_return = TRUE THEN -si.quantity ELSE si.quantity END),0) as lastmonth\n"
                + "    ,date_part('month', s.processdate) as month\n"
                + "FROM general.saleitem si \n"
                + "INNER JOIN general.sale s ON (si.sale_id = s.id AND s.deleted = FALSE)\n"
                + "WHERE si.stock_id = ? AND s.branch_id=? AND si.is_calcincluded = FALSE\n"
                + "AND si.deleted = FALSE\n"
                + "AND s.processdate > now()+INTERVAL'-3 month'\n"
                + "GROUP BY si.stock_id,month\n"
                + "ORDER BY month";

        Object[] param = new Object[]{stock.getId(), branch.getId()};
        return getJdbcTemplate().query(sql, param, new StockAnalysisMapper());
    }

    @Override
    public List<StockAnalysis> listOfYearAverage(Stock stock, Branch branch) {
        String sql = "WITH recursive historytemp as\n"
                + "( SELECT\n"
                + "hst2.newvalue,\n"
                + "hst2.processdate\n"
                + "FROM \n"
                + "general.history hst2\n"
                + "WHERE\n"
                + "hst2.tablename = 'inventory.stockinfo'   \n"
                + "AND hst2.columnname IN ('currentsaleprice')\n"
                + "AND hst2.row_id = (select si.id from inventory.stockinfo si where si.deleted=false and si.stock_id=? and si.branch_id=? limit 1)\n"
                + " AND hst2.branch_id =?\n"
                + " AND hst2.processdate >=  now() - interval '1 year'\n"
                + " AND hst2.processdate <   now() \n"
                + " ORDER BY \n"
                + "  hst2.id DESC\n"
                + "),months (date)\n"
                + "AS(\n"
                + "SELECT (now() - interval '1 year')  + interval '1 month'\n"
                + "UNION ALL\n"
                + "SELECT date + interval '1 month'\n"
                + "from months\n"
                + "where date + interval '1 month'<=now() )\n"
                + "select \n"
                + "TO_CHAR(date, 'YYYYMM')::integer as month,\n"
                + "COALESCE(\n"
                + "(select \n"
                + " max(hst.newvalue::numeric)  \n"
                + "from historytemp hst\n"
                + "WHERE date_part('year', hst.processdate)<= date_part('year', date) and date_part('month', hst.processdate)<=date_part('month', date)\n"
                + "GROUP BY date_part('month', hst.processdate),date_part('year', hst.processdate)\n"
                + "ORDER BY date_part('year', hst.processdate) desc,date_part('month', hst.processdate) desc\n"
                + "limit 1\n"
                + "),0) as price\n"
                + "from months\n";

     
        Object[] param = new Object[]{stock.getId(), branch.getId(), branch.getId()};
        return getJdbcTemplate().query(sql, param, new StockAnalysisMapper());
        
    }

}
