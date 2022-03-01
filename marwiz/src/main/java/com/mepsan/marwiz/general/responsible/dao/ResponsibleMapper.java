/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.11.2019 10:40:39
 */
package com.mepsan.marwiz.general.responsible.dao;

import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.wot.Address;
import com.mepsan.marwiz.general.model.wot.Internet;
import com.mepsan.marwiz.general.model.wot.Phone;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

public class ResponsibleMapper implements RowMapper<Responsible> {

    @Override
    public Responsible mapRow(ResultSet rs, int i) throws SQLException {
        Responsible responsible = new Responsible();
        responsible.setId(rs.getInt("rspid"));
        responsible.setName(rs.getString("rspname"));
        responsible.setSurname(rs.getString("rspsurname"));
        responsible.setIdentityNo(rs.getString("rspidentityno"));
        responsible.setBirthdate(rs.getTimestamp("rspbirthdate"));
        responsible.setPosition(rs.getString("rspposition"));
        responsible.setGender(rs.getBoolean("rspgender"));

        Address<Responsible> responsibleAddress = new Address<>();
        List<Address<Responsible>> listAddress = new ArrayList<>();

        List<Phone<Responsible>> listPhone = new ArrayList<>();
        Phone<Responsible> responsiblePhone = new Phone<>();

        List<Internet<Responsible>> listInternet = new ArrayList<>();
        Internet<Responsible> responsibleInternet = new Internet<>();

        responsibleAddress.setFulladdress(rs.getString("addrfulladdress"));
        listAddress.add(responsibleAddress);
        responsible.setListOfAddresses(listAddress);

        responsiblePhone.setTag(rs.getString("phntag"));
        listPhone.add(responsiblePhone);
        responsible.setListOfPhones(listPhone);

        responsibleInternet.setTag(rs.getString("intrtag"));
        listInternet.add(responsibleInternet);
        responsible.setListOfinternet(listInternet);

        return responsible;
    }

}
