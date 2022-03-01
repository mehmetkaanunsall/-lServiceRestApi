/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 09.04.2019 08:28:40
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.finance.discount.dao.IDiscountItemDao;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountItem;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.inventory.Stock;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class DiscountItemService implements IDiscountItemService {

    @Autowired
    private IDiscountItemDao discountItemDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setDiscountItemDao(IDiscountItemDao discountItemDao) {
        this.discountItemDao = discountItemDao;
    }

    @Override
    public List<DiscountItem> listofDiscountItem(Discount obj) {
        return discountItemDao.listofDiscountItem(obj);
    }

    @Override
    public int create(DiscountItem obj) {
        bringDayMonth(obj);
        bringNecessaryPromotion(obj);
        return discountItemDao.create(obj);
    }

    @Override
    public int update(DiscountItem obj) {
        bringDayMonth(obj);
        bringNecessaryPromotion(obj);
        return discountItemDao.update(obj);
    }

    @Override
    public int testBeforeDelete(DiscountItem obj) {
        return discountItemDao.testBeforeDelete(obj);
    }

    @Override
    public int delete(DiscountItem obj) {
        return discountItemDao.delete(obj);
    }

    /**
     * Günleri Döndürür 1,7
     *
     * @return
     */
    @Override
    public List<String> listDay() {
        List<String> mapOfLists = new ArrayList<>();
        
        mapOfLists.add(0, sessionBean.getLoc().getString("sunday"));
        mapOfLists.add(1, sessionBean.getLoc().getString("monday"));
        mapOfLists.add(2, sessionBean.getLoc().getString("tuesday"));
        mapOfLists.add(3, sessionBean.getLoc().getString("wednesday"));
        mapOfLists.add(4, sessionBean.getLoc().getString("thursday"));
        mapOfLists.add(5, sessionBean.getLoc().getString("friday"));
        mapOfLists.add(6, sessionBean.getLoc().getString("saturday"));
        
        return mapOfLists;
    }

    /**
     * Ayları Dödürür 1,----12
     *
     * @return
     */
    @Override
    public List<String> listMonth() {
        List<String> mapOfLists = new ArrayList<>();
        mapOfLists.add(0, sessionBean.getLoc().getString("january"));
        mapOfLists.add(1, sessionBean.getLoc().getString("february"));
        mapOfLists.add(2, sessionBean.getLoc().getString("march"));
        mapOfLists.add(3, sessionBean.getLoc().getString("april"));
        mapOfLists.add(4, sessionBean.getLoc().getString("may"));
        mapOfLists.add(5, sessionBean.getLoc().getString("june"));
        mapOfLists.add(6, sessionBean.getLoc().getString("july"));
        mapOfLists.add(7, sessionBean.getLoc().getString("august"));
        mapOfLists.add(8, sessionBean.getLoc().getString("september"));
        mapOfLists.add(9, sessionBean.getLoc().getString("october"));
        mapOfLists.add(10, sessionBean.getLoc().getString("november"));
        mapOfLists.add(11, sessionBean.getLoc().getString("december"));
        return mapOfLists;
    }

    /**
     * Ayın Günleri Döndürür 1,2.....31
     *
     * @return
     */
    @Override
    public List<String> listMonthDay() {
        List<String> mapOfLists = new ArrayList<>();
        for (int i = 1; i < 32; i++) {
            mapOfLists.add(String.valueOf(i));
        }

        return mapOfLists;
    }

    /**
     * Veri Tabanından Gelen Günleri Ayları ve Ayın Günlerini Seçim Listelerine
     * Eklemek İçin Kullanılır
     *
     * @param discountItem
     */
    @Override
    public void customizeDayMonth(DiscountItem discountItem) {

        List<String> days = listDay();
        String[] d;
        if (discountItem.getSpecialDay() != null) {
            d = discountItem.getSpecialDay().split(",");
        } else {
            d = new String[]{};
        }
        discountItem.getSpecialDays().clear();
        for (int i = 0; i < d.length; i++) {
            discountItem.getSpecialDays().add(days.get(Integer.valueOf(d[i])));
        }

        List<String> months = listMonth();
        String[] m;
        if (discountItem.getSpecialMonth() != null) {
            m = discountItem.getSpecialMonth().split(",");
        } else {
            m = new String[]{};
        }
        discountItem.getSpecialMonths().clear();
        for (int i = 0; i < m.length; i++) {
            discountItem.getSpecialMonths().add(months.get(Integer.valueOf(m[i]) - 1));
        }

        List<String> monthDays = listMonthDay();

        String[] md;
        if (discountItem.getSpecialMonthDay() != null) {
            md = discountItem.getSpecialMonthDay().split(",");
        } else {
            md = new String[]{};
        }
        discountItem.getSpecialMonthDays().clear();
        for (int i = 0; i < md.length; i++) {
            discountItem.getSpecialMonthDays().add(monthDays.get(Integer.valueOf(md[i]) - 1));
        }
    }

    /**
     * bu metot gelen objeyi eklemede veya guncellemede calısır. seçilen gunlerı
     * ayları (1,2,3) formatına cevırır. Günler (0-6) Arasında Değerler Alır.
     * Aylar (1-12) Arasında Değerler Alır. Ayın Günleri (1-31) Arasında
     * Değerler Alır.
     *
     * @param obj
     */
    public void bringDayMonth(DiscountItem obj) {
        StringBuilder sbDays = new StringBuilder();
        List<String> days = listDay();
        boolean isThere = false;

        for (int i = 0; i < days.size(); i++) {
            if (obj.getSpecialDays().indexOf(days.get(i)) != -1) {
                sbDays.append(i + ",");
                isThere = true;
            }
        }
        if (isThere) {
            obj.setSpecialDay(sbDays.substring(0, sbDays.length() - 1));//sondaki virgülü kaldırdık
        } else {
            obj.setSpecialDay(null);
        }
        isThere = false;
        StringBuilder sbMonths = new StringBuilder();
        List<String> months = listMonth();
        for (int i = 0; i < months.size(); i++) {
            if (obj.getSpecialMonths().indexOf(months.get(i)) != -1) {
                sbMonths.append((i + 1) + ",");
                isThere = true;
            }
        }
        if (isThere) {
            obj.setSpecialMonth(sbMonths.substring(0, sbMonths.length() - 1));//sondaki virgülü kaldırdık
        } else {
            obj.setSpecialMonth(null);
        }
        
        isThere = false;
        StringBuilder sbMonthDays = new StringBuilder();
        List<String> monthDays = listMonthDay();
        for (int i = 0; i < monthDays.size(); i++) {
            if (obj.getSpecialMonthDays().indexOf(monthDays.get(i)) != -1) {
                sbMonthDays.append((i + 1) + ",");
                isThere = true;
            }
        }
        if (isThere) {
            obj.setSpecialMonthDay(sbMonthDays.substring(0, sbMonthDays.length() - 1));//sondaki virgülü kaldırdık
        } else {
            obj.setSpecialMonthDay(null);
        }

    }

    /**
     * Promosyon,Gerekli Ürünlerin Veri Tabanına Eklenmesi İçin Çevirme İşlemi
     * Yapar Promosyoni Gerekli Markaların Veri Tabanına Eklenmesi İçin Çevirme
     * İşlemi Yapar
     *
     * @param obj
     */
    public void bringNecessaryPromotion(DiscountItem obj) {

        String necessaryStock = "";
        if (obj.getNecessaryStockList().size() > 0) {
            necessaryStock = String.valueOf(obj.getNecessaryStockList().get(0).getId());
            for (int i = 1; i < obj.getNecessaryStockList().size(); i++) {
                necessaryStock = necessaryStock + "," + String.valueOf(obj.getNecessaryStockList().get(i).getId());
            }
            obj.setNecessaryStocks(necessaryStock);
        } else {
            obj.setNecessaryStocks(null);
        }

        String promotionStock = "";
        if (obj.getPromotionStockList().size() > 0) {
            promotionStock = String.valueOf(obj.getPromotionStockList().get(0).getId());
            for (int i = 1; i < obj.getPromotionStockList().size(); i++) {
                promotionStock = promotionStock + "," + String.valueOf(obj.getPromotionStockList().get(i).getId());
            }
            obj.setPromotionStocks(promotionStock);
        } else {
            obj.setPromotionStocks(null);
        }

        String necessaryBrand = "";
        if (obj.getNecessaryBrandList().size() > 0) {
            necessaryBrand = String.valueOf(obj.getNecessaryBrandList().get(0).getId());
            for (int i = 1; i < obj.getNecessaryBrandList().size(); i++) {
                necessaryBrand = necessaryBrand + "," + String.valueOf(obj.getNecessaryBrandList().get(i).getId());
            }
            obj.setNecessaryBrands(necessaryBrand);
        } else {
            obj.setNecessaryBrands(null);
        }

        String promotionBrand = "";
        if (obj.getPromotionBrandList().size() > 0) {
            promotionBrand = String.valueOf(obj.getPromotionBrandList().get(0).getId());
            for (int i = 1; i < obj.getPromotionBrandList().size(); i++) {
                promotionBrand = promotionBrand + "," + String.valueOf(obj.getPromotionBrandList().get(i).getId());
            }
            obj.setPromotionBrands(promotionBrand);
        } else {
            obj.setPromotionBrands(null);
        }

    }

    /**
     * Virgül ile Ayrılmış Ürünler Liste İçerisine Eklenerek Döndürülür
     *
     * @param stocks
     * @return
     */
    @Override
    public List<Stock> convertStock(String stocks) {
        List<Stock> stockList = new ArrayList<>();
        if (stocks != null) {
            String[] stockArray = stocks.split(",");
            for (String a : stockArray) {
                if (a.length() > 0) {
                    Stock s = new Stock();
                    s.setId(Integer.valueOf(a));
                    stockList.add(s);
                }
            }
        }
        return stockList;
    }

    /**
     * Virgül ile Ayrılmış Markalar Liste İçerisine Eklenerek Döndürülür
     *
     * @param brands
     * @param brandList
     * @return
     */
    @Override
    public List<Brand> convertBrand(String brands, List<Brand> brandList) {
        List<Brand> listOfBrand = new ArrayList<>();
        if (brands != null) {
            String[] stockArray = brands.split(",");
            for (String a : stockArray) {
                if (a.length() > 0) {
                    for (Brand brand : brandList) {
                        if (brand.getId() == Integer.valueOf(a)) {
                            listOfBrand.add(brand);
                            break;
                        }
                    }
                }
            }
        }
        return listOfBrand;
    }

}
