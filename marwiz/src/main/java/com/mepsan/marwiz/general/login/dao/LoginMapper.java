/**
 * Bu sınıf, USerData nesnesini oluşturur ve özelliklerini set eder.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 * @edited Zafer Yaşar - try-catch Blokları yazıldı.
 */
package com.mepsan.marwiz.general.login.dao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.common.HashPassword;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Currency;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.springframework.jdbc.core.RowMapper;

public class LoginMapper implements RowMapper<UserData> {

    private String password;
    private Boolean succsess = false;
    private final Gson gson = new Gson();

    public LoginMapper(String password) {
        this.password = password;

    }

    public LoginMapper() {
        succsess = true;
    }

    @Override
    public UserData mapRow(ResultSet rs, int i) throws SQLException {

        UserData user = new UserData();

        user.setId(rs.getInt("r_id"));
        user.setUsername(rs.getString("r_username"));
        

        user.setPassword("1");//kilitlendiğinde şifreyi geri getirmediğimizden user null oluyor. oyuzden once bir deger atadık
        try {
            user.setPassword(rs.getString("r_password"));

            HashPassword hashPassword = new HashPassword();
            succsess = hashPassword.passwordMatches(password, user.getPassword());

        } catch (Exception e) {
        }

//        try {
//            user.getUserDataLogin().setLastFailedLoginTime(rs.getTimestamp("failedtime"));//son hatalı giriş zamanı  
//        } catch (Exception ex) {
//        }
//        try {
//            user.getUserDataLogin().setFailedLoginSize(rs.getInt("locked"));
//        } catch (Exception exception) {
//        }
//        try {
//            user.getUserDataLogin().setLastLoginTime(rs.getTimestamp("last_login"));//son hatalı giriş zamanı  
//        } catch (Exception ex) {
//
//        }
        if (succsess) {
            
            user.setName(rs.getString("r_name"));
            user.setIsAdmin(rs.getBoolean("r_is_admin"));//
            

            
            user.setSurname(rs.getString("r_surname"));
            user.getStatus().setId(rs.getInt("r_userstatus_id"));
            user.getType().setId(rs.getInt("r_usertype_id"));
            user.getType().setTag(rs.getString("r_typename"));
            user.getCountry().setTag(rs.getString("r_country"));
            user.getCity().setTag(rs.getString("r_city"));
            user.getCounty().setName(rs.getString("r_county"));
            user.getLanguage().setId(rs.getInt("r_language_id"));
            user.getLanguage().setCode(rs.getString("r_languagecode"));
            user.setPhone(rs.getString("r_phone"));
            user.setMail(rs.getString("r_mail"));
            user.setAddress(rs.getString("r_address"));
            //    user.setLastBranch(new Branch(rs.getInt("r_lastbranch_id"), rs.getString("r_lastbranchname")));
            user.getLastBranch().setId(rs.getInt("r_lastbranch_id"));
            user.getLastBranch().setName(rs.getString("r_lastbranchname"));
            user.setLastTheme(rs.getString("r_lasttheme"));
            user.setGridSize(rs.getString("r_gridsize"));
            user.getAccount().setId(rs.getInt("r_account_id"));
            user.getLastBranch().setCurrency(new Currency(rs.getInt("r_currency_id")));
            user.getLastBranch().setDateFormat(rs.getString("r_dateformat"));
            user.getLastBranch().setCurrencyrounding(rs.getInt("r_currencyrounding"));
            user.getLastBranch().setDecimalsymbol(rs.getInt("r_decimelsymbol"));

            user.getLastBranch().setTaxNo(rs.getString("r_branchtaxno"));
            user.getLastBranch().setTaxOffice(rs.getString("r_branchtaxoffice"));
            user.getLastBranch().setPhone(rs.getString("r_branchphone"));
            user.getLastBranch().setMobile(rs.getString("r_branchmobile"));
            user.getLastBranch().setAddress(rs.getString("r_branchaddress"));
            user.getLastBranch().setMail(rs.getString("r_branchemail"));
            user.getLastBranch().setCountry(new Country(rs.getInt("r_branchcountry_id")));
            user.getLastBranch().setIsCentral(rs.getBoolean("r_is_central"));
            //  user.getLastBranch().getCountry().setId(rs.getInt("r_branchcountry_id"));
            user.setLastBranchSetting(new BranchSetting());
            user.getLastBranchSetting().setIsCentralIntegration(rs.getBoolean("r_is_centralintegration"));
            user.getLastBranchSetting().setIsPurchaseControl(rs.getBoolean("r_is_purchasecontrol"));
            user.getLastBranchSetting().setIsShiftControl(rs.getBoolean("r_is_shiftcontrol"));
            user.getLastBranchSetting().setIsReturnWithoutReceipt(rs.getBoolean("r_is_returnwithoutreceipt"));
            user.getLastBranchSetting().setPastPeriodClosingDate(rs.getTimestamp("r_pastperiodclosingdate"));
            user.getLastBranchSetting().setBranch(user.getLastBranch());

            user.setLastAuthorize((new Authorize(rs.getInt("r_authorize_id"))));

            user.getLastAuthorize().setListOfPages(StaticMethods.stringToList(rs.getString("r_pages"), ","));
            user.getLastAuthorize().setListOfFolders(StaticMethods.stringToList(rs.getString("r_folders"), ","));
            user.getLastAuthorize().setListOfModules(StaticMethods.stringToList(rs.getString("r_modules"), ","));
            user.getLastAuthorize().setListOfTabs(StaticMethods.stringToList(rs.getString("r_tabs"), ","));
            user.getLastAuthorize().setListOfButtons(StaticMethods.stringToList(rs.getString("r_buttons"), ","));
            user.setIsAuthorized(rs.getBoolean("r_is_authorized"));
            user.getLastBranch().setLicenceCode(rs.getString("r_licencecode"));
            user.getLastBranch().setIsAgency(rs.getBoolean("r_is_agency"));
            user.getLastBranch().setConceptType(rs.getInt("r_concepttype_id"));
            JsonArray jsonArray = gson.fromJson(rs.getString("r_branches"), JsonArray.class);
            user.setAuthorizedBranches(new ArrayList<>());
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Branch branch = new Branch(gson.fromJson(jsonObject.get("branch_id"), Integer.class), gson.fromJson(jsonObject.get("name"), String.class));
                user.getAuthorizedBranches().add(branch);

            }
            user.getLastBranchSetting().setShiftCurrencyRounding(rs.getInt("r_shiftcurrencyrounding"));
            user.getLastBranchSetting().setSpecialItem(rs.getInt("r_specialitem"));

//            try {
//                user.getUserConfig().setId(rs.getInt("usrcid"));
//                user.getUserConfig().setGridSize(rs.getString("grid_size"));
//                user.getUserConfig().setDefaultGridSize(rs.getString("grid_size").split(",")[0]);
            //           user.set("bluedanger");
//
//            } catch (Exception ex) {
//            }
        } else {

        }
        return user;

    }

}
