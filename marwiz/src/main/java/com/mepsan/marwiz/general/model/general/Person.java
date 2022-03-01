/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.Address;
import com.mepsan.marwiz.general.model.wot.Internet;
import com.mepsan.marwiz.general.model.wot.Phone;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.Size;

public class Person extends WotLogging implements Cloneable {

    private int id;
    @Size(max = 50)
    private String name;
    @Size(max = 50)
    private String surname;
    @Size(max = 30)
    private String identityNo;
    private Date birthDate;
    private boolean gender;
    private List<Address<Person>> listOfAddresses;
    private List<Phone<Person>> listOfPhones;
    private List<Internet<Person>> listOfinternet;
    private String fullName;

    public Person() {
        listOfAddresses = new ArrayList<>();
        listOfPhones = new ArrayList<>();
        listOfinternet = new ArrayList<>();
    }

    public Person(int id) {
        this.id = id;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getFullName() {
        if (name != null || surname != null) {
            return name + " " + surname;
        } else {
            return null;
        }
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Address<Person>> getListOfAddresses() {
        return listOfAddresses;
    }

    public void setListOfAddresses(List<Address<Person>> listOfAddresses) {
        this.listOfAddresses = listOfAddresses;
    }

    public List<Phone<Person>> getListOfPhones() {
        return listOfPhones;
    }

    public void setListOfPhones(List<Phone<Person>> listOfPhones) {
        this.listOfPhones = listOfPhones;
    }

    public List<Internet<Person>> getListOfinternet() {
        return listOfinternet;
    }

    public void setListOfinternet(List<Internet<Person>> listOfinternet) {
        this.listOfinternet = listOfinternet;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    @Override
    public boolean equals(Object obj) {

        if (((Person) obj).getId() == this.getId()) {
            return true;
        }
        return false;
    }

//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        Person p = (Person) super.clone();
//
//        List<Address<Person>> list = new ArrayList<>();
//
//        for (Address<Person> a : getListOfAddresses()) {
//            list.add((Address<Person>) a.clone());
//        }
//
//        p.setListOfAddresses(list);
//
//        return p;
//    }
}
