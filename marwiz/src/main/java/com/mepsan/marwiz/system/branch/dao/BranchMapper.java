/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.02.2018 10:52:02
 */
package com.mepsan.marwiz.system.branch.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BranchMapper implements RowMapper<Branch> {
    
    @Override
    public Branch mapRow(ResultSet rs, int i) throws SQLException {
        Branch branch = new Branch();
        UserData userData = new UserData();
        
        try {
            branch.setName(rs.getString("brname"));
        } catch (Exception e) {
        }
        try {
            branch.setId(rs.getInt("brid"));
        } catch (Exception e) {
        }
        try {
            branch.setTitle(rs.getString("brtitle"));
            branch.setTaxNo(rs.getString("brtaxno"));
            branch.setTaxOffice(rs.getString("brtaxoffice"));
            branch.getStatus().setId(rs.getInt("brstatus_id"));
            branch.getStatus().setTag(rs.getString("sttdname"));
            branch.getType().setId(rs.getInt("brtype_id"));
            branch.getType().setTag(rs.getString("typdname"));
            branch.getCurrency().setId(rs.getInt("brcurrency_id"));
            branch.getCurrency().setTag(rs.getString("crrdname"));
            branch.setCurrencyrounding(rs.getInt("brcurrencyrounding"));
            branch.setDateFormat(rs.getString("brdateformat"));
            branch.setDecimalsymbol(rs.getInt("brdecimalsymbol"));
            branch.setPhone(rs.getString("brphone"));
            branch.setMobile(rs.getString("brmobile"));
            branch.setMail(rs.getString("bremail"));
            branch.setAddress(rs.getString("braddress"));
            branch.setLatitude(rs.getString("brlatitude"));
            branch.setLongitude(rs.getString("brlongitude"));
            branch.getLanguage().setId(rs.getInt("brlanguage_id"));
            
        } catch (Exception e) {
        }
        
        try {
            branch.getCounty().setId(rs.getInt("brcounty_id"));
            branch.getCity().setId(rs.getInt("brcity_id"));
            branch.getCountry().setId(rs.getInt("brcountry_id"));
        } catch (Exception e) {
        }
        try {
            branch.getCounty().setName(rs.getString("cntyname"));
            branch.getCountry().setTag(rs.getString("ctrdname"));
            branch.getCity().setTag(rs.getString("ctydname"));
        } catch (Exception e) {
        }
        
        try {
            branch.setLicenceCode(rs.getString("brlicencecode"));
        } catch (Exception e) {
        }
        
        try {
            branch.setMersisNo(rs.getString("brmersisno"));
            branch.setWebAddress(rs.getString("brwebaddress"));
            branch.setTradeRegisterNo(rs.getString("brtraderegisterno"));
        } catch (Exception e) {
        }
        
        try {
            branch.setIsCentral(rs.getBoolean("bris_central"));
        } catch (Exception e) {
        }
        try {
            branch.setIsAgency(rs.getBoolean("bris_agency"));
            branch.setIsLicenceCodeCheck(rs.getBoolean("bris_licencecheck"));
        } catch (Exception e) {
        }
        
        try {
            branch.setConceptType(rs.getInt("brconcepttype_id"));
        } catch (Exception e) {
        }
        try {
            userData.setId(rs.getInt("brc_id"));
            userData.setName(rs.getString("usdname"));
            userData.setSurname(rs.getString("usdsurname"));
            userData.setUsername(rs.getString("usdusername"));
            branch.setUserCreated(userData);
            branch.setDateCreated(rs.getTimestamp("brc_time"));
        } catch (Exception e) {
            
        }
        
        try {
            branch.setIsTakeAway(rs.getBoolean("bris_takeaway"));
            branch.setIsVehicleDelivery(rs.getBoolean("bris_vehicledelivery"));
        } catch (Exception e) {
        }
        try {
            branch.setSpecialItem(rs.getInt("brsspecialitem"));
            branch.setIsCentralIntegration(rs.getBoolean("brsis_centralintegration"));
        } catch (Exception e) {
        }
        
        return branch;
        
    }
}
