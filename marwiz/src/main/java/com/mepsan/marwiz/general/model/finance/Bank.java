/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 09:25:41
 */
package com.mepsan.marwiz.general.model.finance;

import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Size;

public class Bank extends WotLogging {

    private int id;
    @Size(max = 60)
    private String name;
    @Size(max = 30)
    private String code;
    private Status status;
    @Size(max = 30)
    private String phone;
    @Size(max = 60)
    private String email;
    private String address;
    private City city;
    private County county;
    private Country country;
    private List<BankBranch> listOfBranchs;

    public Bank(int id) {
        this.id = id;
    }

    public Bank() {
        this.city = new City();
        this.county = new County();
        this.country = new Country();
        this.status = new Status();
        this.listOfBranchs = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public List<BankBranch> getListOfBranchs() {
        return listOfBranchs;
    }

    public void setListOfBranchs(List<BankBranch> listOfBranchs) {
        this.listOfBranchs = listOfBranchs;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
