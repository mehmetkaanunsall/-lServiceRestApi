/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 09.04.2019 08:30:07
 */
package com.mepsan.marwiz.finance.discount.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountItem;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DiscountItemDao extends JdbcDaoSupport implements IDiscountItemDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<DiscountItem> listofDiscountItem(Discount obj) {
        String sql = "SELECT \n"
                + "  dsi.id AS dsiid,\n"
                + "  dsi.discount_id AS dsidiscount_id,\n"
                + "  dsc.name AS dscname,\n"
                + "  dsi.stock_id AS dsistock_id,\n"
                + "  stck.name AS stckname,\n"
                + "  dsi.brand_id AS dsibrand_id ,\n"
                + "  brnd.name AS brndname,\n"
                + "  dsi.pricelist_id AS dsipricelist_id ,\n"
                + "  prl.name AS prlname,\n"
                + "  dsi.discountrate AS dsidiscountrate,\n"
                + "  dsi.discountamount AS dsidiscountamount ,\n"
                + "  dsi.is_taxincluded AS dsiis_taxincluded, \n"
                + "  dsi.salecount AS dsisalecount ,\n"
                + "  dsi.begindate AS dsibegindate,\n"
                + "  dsi.enddate AS dsienddate,\n"
                + "  dsi.beginprice AS dsibeginprice,\n"
                + "  dsi.endprice AS dsiendprice,\n"
                + "  dsi.begintime AS dsibegintime,\n"
                + "  dsi.endtime AS dsiendtime,\n"
                + "  dsi.specialday AS dsispecialday,\n"
                + "  dsi.specialmonth AS dsispecialmonth,\n"
                + "  dsi.specialmonthday AS dsispecialmonthday,\n"
                + "  dsi.is_discountcode AS dsiis_discountcode,\n"
                + "  dsi.necessarystocks AS dsinecessarystocks,\n"
                + "  dsi.promotionstocks AS  dsipromotionstocks, \n"
                + "  dsi.necessarybrands AS dsinecessarybrands ,\n"
                + "  dsi.promotionbrands AS dsipromotionbrands, \n"
                + "  dsi.c_id AS dsic_id ,\n"
                + "  dsi.c_time AS dsic_time,\n"
                + "  usr.name AS usrname,\n"
                + "  usr.surname AS usrsurname,\n"
                + "  usr.username AS usrusername \n"
                + "FROM \n"
                + "  finance.discountitem dsi\n"
                + "  INNER JOIN finance.discount dsc ON(dsc.id = dsi.discount_id AND dsc.deleted = FALSE)\n"
                + "  LEFT JOIN inventory.stock stck ON(stck.id = dsi.stock_id AND stck.deleted = FALSE)\n"
                + "  LEFT JOIN  inventory.pricelist prl ON(prl.id = dsi.pricelist_id AND prl.deleted = FALSE)\n"
                + "  LEFT JOIN  general.brand brnd ON(brnd.id = dsi.brand_id AND brnd.deleted = FALSE)\n"
                + "  INNER JOIN general.userdata usr   ON (usr.id = dsc.c_id)\n"
                + "WHERE \n"
                + "  dsi.deleted = FALSE AND dsi.discount_id = ?  ";
        Object[] param = new Object[]{obj.getId()};
        List<DiscountItem> result = getJdbcTemplate().query(sql, param, new DiscountItemMapper());
        return result;
    }

    @Override
    public int create(DiscountItem obj) {
        String sql = "INSERT INTO \n"
                + "finance.discountitem\n"
                + "(\n"
                + "  discount_id,\n"
                + "  stock_id,\n"
                + "  brand_id,\n"
                + "  discountrate,\n"
                + "  discountamount,\n"
                + "  pricelist_id,\n"
                + "  is_taxincluded,\n"
                + "  salecount,\n"
                + "  begindate,\n"
                + "  enddate,\n"
                + "  beginprice,\n"
                + "  endprice,\n"
                + "  begintime,\n"
                + "  endtime,\n"
                + "  specialday,\n"
                + "  specialmonth,\n"
                + "  specialmonthday,\n"
                + "  is_discountcode,\n"
                + "  necessarystocks ,\n"
                + "  promotionstocks , \n"
                + "  necessarybrands ,\n"
                + "  promotionbrands, \n"
                + "  c_id,\n"
                + "  u_id\n"
                + ")\n"
                + "VALUES (?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ?,  ?,  ?,  ?) RETURNING id;\n";

        Object[] param = new Object[]{obj.getDiscount().getId(), (obj.getStock().getId() == 0 ? null : obj.getStock().getId()), (obj.getBrand().getId() == 0 ? null : obj.getBrand().getId()), obj.getDiscountRate(), obj.getDiscountAmount(),
            (obj.getPriceList().getId() == 0 ? null : obj.getPriceList().getId()), obj.getIsTaxIncluded(), obj.getSaleCount(),
            obj.getBeginDate(), obj.getEndDate(), obj.getBeginPrice(), obj.getEndPrice(), obj.getBeginTime(), obj.getEndTime(), obj.getSpecialDay(), obj.getSpecialMonth(), obj.getSpecialMonthDay(), obj.isIsDiscountCode(),
            obj.getNecessaryStocks(), obj.getPromotionStocks(), obj.getNecessaryBrands(), obj.getPromotionBrands(), sessionBean.getUser().getId(), sessionBean.getUser().getId()
        };

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int update(DiscountItem obj) {

        String sql = "UPDATE \n"
                + "  finance.discountitem \n"
                + "SET \n"
                + "  stock_id = ?,\n"
                + "  brand_id = ?,\n"
                + "  discountrate = ?,\n"
                + "  discountamount = ?,\n"
                + "  pricelist_id = ?,\n"
                + "  is_taxincluded = ?,\n"
                + "  salecount = ?,\n"
                + "  begindate = ?,\n"
                + "  enddate = ?,\n"
                + "  beginprice = ?,\n"
                + "  endprice = ?,\n"
                + "  begintime = ?,\n"
                + "  endtime = ?,\n"
                + "  specialday = ?,\n"
                + "  specialmonth = ?,\n"
                + "  specialmonthday = ?,\n"
                + "  is_discountcode = ?,\n"
                + "  necessarystocks = ?  ,\n"
                + "  promotionstocks = ? , \n"
                + "  necessarybrands = ?  ,\n"
                + "  promotionbrands = ? , \n"
                + "  u_id = ?,\n"
                + "  u_time = NOW() \n"
                + "WHERE \n"
                + "  id = ?; \n";

        Object[] param = new Object[]{(obj.getStock().getId() == 0 ? null : obj.getStock().getId()), (obj.getBrand().getId() == 0 ? null : obj.getBrand().getId()), obj.getDiscountRate(), obj.getDiscountAmount(),
            (obj.getPriceList().getId() == 0 ? null : obj.getPriceList().getId()), obj.getIsTaxIncluded(), obj.getSaleCount(),
            obj.getBeginDate(), obj.getEndDate(), obj.getBeginPrice(), obj.getEndPrice(), obj.getBeginTime(), obj.getEndTime(), obj.getSpecialDay(), obj.getSpecialMonth(), obj.getSpecialMonthDay(), obj.isIsDiscountCode(),
            obj.getNecessaryStocks(), obj.getPromotionStocks(), obj.getNecessaryBrands(), obj.getPromotionBrands(), sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }

    }

    @Override
    public int testBeforeDelete(DiscountItem obj) {
        String sql = "SELECT CASE WHEN EXISTS (\n"
                + "              SELECT discountitem_id FROM finance.discountinfo WHERE discountitem_id=? AND deleted=False) THEN 1\n"
                + "                                  ELSE 0 END\n";

        Object[] param = new Object[]{obj.getId()};

        try {
            return getJdbcTemplate().queryForObject(sql, param, Integer.class);
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

    @Override
    public int delete(DiscountItem obj) {
        String sql = "UPDATE finance.discountitem SET deleted=TRUE, u_id=? , d_time=NOW()  WHERE deleted=False AND id=?;\n";

        Object[] param = new Object[]{sessionBean.getUser().getId(), obj.getId()};
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
