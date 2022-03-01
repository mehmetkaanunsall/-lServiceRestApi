/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   15.01.2018 08:22:54
 */
package com.mepsan.marwiz.finance.safe.dao;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SafeMovementMapper implements RowMapper {
    
    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        SafeMovement safeMovement = new SafeMovement();
        try {
            safeMovement.setId(rs.getInt("r_sfmid"));
        } catch (Exception e) {
        }
        
        try {
            safeMovement.getFinancingDocument().setId(rs.getInt("r_fdocid"));
            safeMovement.getFinancingDocument().setDocumentNumber(rs.getString("r_fdocdocumentnumber"));
            safeMovement.getFinancingDocument().setDescription(rs.getString("r_fdocdescription"));
            safeMovement.getFinancingDocument().getIncomeExpense().setId(rs.getInt("r_fiemincomeexpense_id"));
            safeMovement.getFinancingDocument().getIncomeExpense().setName(rs.getString("r_fiename"));
            safeMovement.getFinancingDocument().setDocumentDate(rs.getTimestamp("r_fdocdocumnetdate"));
            safeMovement.setBalance(rs.getBigDecimal("r_sfbalance"));
            safeMovement.setPrice(rs.getBigDecimal("r_sfmprice"));
            safeMovement.setIsDirection(rs.getBoolean("r_sfmis_direction"));
            
            safeMovement.getFinancingDocument().getFinancingType().setId(rs.getInt("r_fdoctype_id"));
            safeMovement.getFinancingDocument().getFinancingType().setTag(rs.getString("r_typdname"));
            
            UserData createUserData = new UserData(rs.getInt("r_fdocc_id"));
            safeMovement.setUserCreated(createUserData);
            safeMovement.getUserCreated().setName(rs.getString("r_usrname"));
            safeMovement.getUserCreated().setSurname(rs.getString("r_usrsurname"));
            safeMovement.setDateCreated(rs.getTimestamp("r_fdocc_time"));
            
            UserData updateUserData = new UserData(rs.getInt("r_fdocu_id"));
            safeMovement.setUserUpdated(updateUserData);
            safeMovement.getUserUpdated().setId(rs.getInt("r_fdocu_id"));
            safeMovement.getUserUpdated().setName(rs.getString("r_usr1name"));
            safeMovement.getUserUpdated().setSurname(rs.getString("r_usr1surname"));
            safeMovement.setDateUpdated(rs.getTimestamp("r_fdocu_time"));
            safeMovement.getBranch().setId(rs.getInt("r_sfmbranch_id"));
            safeMovement.getBranch().setName(rs.getString("r_brname"));
            safeMovement.getSafe().setName(rs.getString("r_sfname"));
        } catch (Exception e) {
                        
        }
        
        try {
            safeMovement.setTransferringbalance(rs.getBigDecimal("r_transferringbalance"));
        } catch (Exception e) {
        }
        try {
            safeMovement.setTotalIncoming(rs.getBigDecimal("r_sumincoming"));
            
        } catch (Exception e) {
        }
        try {
            safeMovement.setTotalOutcoming(rs.getBigDecimal("r_sumoutcoming"));
            
        } catch (Exception e) {
        }
        try {
            safeMovement.getSafe().getCurrency().setId(rs.getInt("r_sfcurrency_id"));
        } catch (Exception e) {
        }
        
      
        return safeMovement;
    }
    
}
