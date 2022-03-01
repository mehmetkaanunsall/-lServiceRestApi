/**
 * Bu Sınıf  şeherleri ve ulkelere golbal için
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   02.08.2016 10:46:29
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class CitiesAndCountiesBean {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    private List<City> cities;
    private List<County> counties;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public List<County> getCounties() {
        return counties;
    }

    public void setCounties(List<County> counties) {
        this.counties = counties;
    }

    /**
     * ülke comboboxı değiştikçe seçilen ülkenin şehirlerini getirmek için kullanılır
     *
     * @param country
     * @return
     */
    public List<City> findListOfCities(Country country) {
        for (Country c : sessionBean.getCountries()) {
            if (c.getId() == country.getId()) {
                return c.getListOfCities();
            }
        }
        return null;
    }

    /**
     * şehir comboboxı değiştikçe seçilen şehirin ilçelerini getirmek için kullanılır
     *
     * @param city
     * @param citys
     * @return
     */
    public List<County> findListOfCounties(City city, List<City> citys) {
        for (City c : citys) {
            if (c.getId() == city.getId()) {
                return c.getListOfCounty();
            }
        }
        return null;
    }

    /**
     * Bu methot ülke şehir ilçe olan sayfalarda ülke şehir ilçe bilgilerini comboboxlara getirmek için güncelleme sayfalarının initinde kullanılır.
     *
     * @param country
     * @param city
     */
    public void updateCityAndCounty(Country country, City city) {
        cities = country.getId() > 0 ? findListOfCities(country) : new ArrayList<>();
        counties = city.getId() > 0 ? findListOfCounties(city, cities) : new ArrayList<>();      
    }

    /**
     * Bu methot ülke comboboxının change olayında çağırılır. Ülke değiştikçe ülkeye göre şehirler gelir. İlçeler sıfırlanır.
     *
     * @param country
     * @param city
     * @param county
     */
    public void updateCity(Country country, City city, County county) {
        System.out.println("---country id----"+country.getId());
        cities = country.getId() > 0 ? findListOfCities(country) : new ArrayList<>();
        counties = new ArrayList<>();
        city.setId(0);
        if (county != null) {
            county.setId(0);
        }
    }

    /**
     * Bu methot şehir comboboxının change olayında çağırılır. Şehir değiştikçe ona göre ilçeler gelir.
     *
     * @param country
     * @param city
     * @param county
     */
    public void updateCounty(Country country, City city, County county) {
        counties = city.getId() > 0 ? findListOfCounties(city, cities) : new ArrayList<>();
        if (county != null) {
            county.setId(0);
        }
        for (County county1 : counties) {
            System.out.println("-----"+county1.getId()+"----"+county1.getName());
        }
    }

    /**
     * Yeni iletişim eklerken şehir ve ilçe listelerini sıfırlamak için kullanılır.
     */
    public void reset() {
        cities = new ArrayList<>();
        counties = new ArrayList<>();
    }

}
