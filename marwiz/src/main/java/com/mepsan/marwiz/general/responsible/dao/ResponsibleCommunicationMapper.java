/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.10.2019 03:23:09
 */
package com.mepsan.marwiz.general.responsible.dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.Address;
import com.mepsan.marwiz.general.model.wot.Internet;
import com.mepsan.marwiz.general.model.wot.Phone;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;

public class ResponsibleCommunicationMapper implements RowMapper<Responsible> {

    Responsible responsible;

    public ResponsibleCommunicationMapper(Responsible responsible) {
        this.responsible = responsible;
    }

    @Override
    public Responsible mapRow(ResultSet rs, int i) throws SQLException {
        List<Phone<Responsible>> phones = new ArrayList<>();
        List<Address<Responsible>> addresses = new ArrayList<>();
        List<Internet<Responsible>> internets = new ArrayList<>();

        JsonParser jsonParser = new JsonParser();
        if (rs.getString("r_phone") != null && !rs.getString("r_phone").equals("")) {

            JsonArray jArrayPhone = jsonParser.parse(rs.getString("r_phone")).getAsJsonArray();

            for (JsonElement jsonObj : jArrayPhone) {

                Phone<Responsible> phone = new Phone<>();

                phone.setObject(responsible);
                phone.setId(jsonObj.getAsJsonObject().get("id").getAsInt());
                phone.setPhoneType(new Type(jsonObj.getAsJsonObject().get("type_id").getAsInt(), jsonObj.getAsJsonObject().get("type_name").getAsString(), new Item(35, "phone")));
                phone.setTag(jsonObj.getAsJsonObject().get("tag").getAsString());
                phone.setCountry(new Country(jsonObj.getAsJsonObject().get("country_id").getAsInt()));
                if (jsonObj.getAsJsonObject().get("is_default").getAsBoolean() == false) {
                    phone.setDefaultValue(false);
                } else {
                    phone.setDefaultValue(true);
                }
                phones.add(phone);
            }
        }

        if (rs.getString("r_internet") != null && !rs.getString("r_internet").equals("")) {
            JsonArray jArrayInternet = jsonParser.parse(rs.getString("r_internet")).getAsJsonArray();

            for (JsonElement jsonObj : jArrayInternet) {

                Internet<Responsible> internet = new Internet<>();

                internet.setObject(responsible);
                internet.setId(jsonObj.getAsJsonObject().get("id").getAsInt());
                internet.setInternetType(new Type(jsonObj.getAsJsonObject().get("type_id").getAsInt(), jsonObj.getAsJsonObject().get("type_name").getAsString(), new Item(34, "internet")));
                internet.setTag(jsonObj.getAsJsonObject().get("tag").getAsString());
                if (jsonObj.getAsJsonObject().get("is_default").getAsBoolean() == false) {
                    internet.setDefaultValue(false);
                } else {
                    internet.setDefaultValue(true);
                }
                internets.add(internet);
            }
        }

        if (rs.getString("r_address") != null && !rs.getString("r_address").equals("")) {
            JsonArray jArrayAddress = jsonParser.parse(rs.getString("r_address")).getAsJsonArray();

            for (JsonElement jsonObj : jArrayAddress) {

                Address<Responsible> address = new Address<>();

                address.setObject(responsible);
                address.setId(jsonObj.getAsJsonObject().get("id").getAsInt());
                address.setCountry(new Country(jsonObj.getAsJsonObject().get("country_id").getAsInt()));
                address.setCity(new City(jsonObj.getAsJsonObject().get("city_id").getAsInt()));
                address.setCounty(new County(jsonObj.getAsJsonObject().get("county_id").getAsInt(), jsonObj.getAsJsonObject().get("county_name").getAsString(), address.getCity()));
                address.setFulladdress(jsonObj.getAsJsonObject().get("tag").getAsString());
                if (jsonObj.getAsJsonObject().get("is_default").getAsBoolean() == false) {
                    address.setDefaultValue(false);
                } else {
                    address.setDefaultValue(true);
                }
                addresses.add(address);
            }
        }

        responsible.setListOfPhones(phones);
        responsible.setListOfAddresses(addresses);
        responsible.setListOfinternet(internets);

        return responsible;
    }

}
