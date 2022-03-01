/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 01:35:06
 */
package com.mepsan.marwiz.general.account.dao;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.general.UserData;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AccountMovementMapper implements RowMapper<AccountMovement> {

    public AccountMovementMapper() {

    }

    @Override
    public AccountMovement mapRow(ResultSet rs, int i) throws SQLException {
        AccountMovement accountMovement = new AccountMovement();

        accountMovement.setId(rs.getInt("accmid"));

        try {

            accountMovement.getFinancingDocument().setId(rs.getInt("fdocid"));

            accountMovement.getFinancingDocument().setDocumentNumber(rs.getString("fdocdocumentnumber"));
            accountMovement.getFinancingDocument().setDescription(rs.getString("fdocdescription"));
            accountMovement.getFinancingDocument().getFinancingType().setId(rs.getInt("fdoctype_id"));
            accountMovement.getFinancingDocument().getFinancingType().setTag(rs.getString("typdname"));
            accountMovement.getFinancingDocument().setDocumentDate(rs.getTimestamp("fdocdocumentdate"));

            UserData createUserData = new UserData(rs.getInt("fdocc_id"));
            accountMovement.setUserCreated(createUserData);
            accountMovement.getUserCreated().setName(rs.getString("usrname"));
            accountMovement.getUserCreated().setSurname(rs.getString("usrsurname"));
            accountMovement.setDateCreated(rs.getTimestamp("fdocc_time"));

            UserData updateUserData = new UserData(rs.getInt("fdocu_id"));
            accountMovement.setUserUpdated(updateUserData);
            accountMovement.getUserUpdated().setId(rs.getInt("fdocu_id"));
            accountMovement.getUserUpdated().setName(rs.getString("usr1name"));
            accountMovement.getUserUpdated().setSurname(rs.getString("usr1surname"));
            accountMovement.setDateUpdated(rs.getTimestamp("fdocu_time"));

            accountMovement.setBalance(rs.getBigDecimal("accbalance"));
            accountMovement.setPrice(rs.getBigDecimal("accmprice"));
            accountMovement.setIsDirection(rs.getBoolean("accmis_direction"));
            accountMovement.setMovementDate(rs.getTimestamp("accmmovementdate"));
            accountMovement.setExchangeRate(rs.getBigDecimal("accmexchangerate"));
            accountMovement.getInvoice().setId(rs.getInt("invid"));
            accountMovement.getInvoice().setDocumentNumber(rs.getString("invdocumentnumber"));
            accountMovement.getInvoice().setInvoiceDate(rs.getTimestamp("invinvoicedate"));
            accountMovement.getInvoice().setIsPurchase(rs.getBoolean("invis_purchase"));

            accountMovement.getChequeBill().setId(rs.getInt("accmchequebill_id"));
            accountMovement.getChequeBill().setPortfolioNumber(rs.getString("cqbportfolionumber"));
            accountMovement.getReceipt().setId(rs.getInt("accmreceipt_id"));
            accountMovement.getReceipt().setReceiptNo(rs.getString("rcpreceiptno"));
            accountMovement.getReceipt().setProcessDate(rs.getTimestamp("rcpprocessdate"));

            accountMovement.getCurrency().setId(rs.getInt("accmcurrency_id"));
            accountMovement.getCurrency().setSign(rs.getString("crrsign"));
            accountMovement.getCurrency().setCode(rs.getString("crrcode"));

            accountMovement.setShiftId(rs.getInt("shpshift_id"));
            accountMovement.setStockTakingId(rs.getInt("stfstocktaking_id"));
            accountMovement.getBranch().setName(rs.getString("brname"));
        } catch (Exception e) {
        }
        try {
            accountMovement.setTransferringbalance(rs.getBigDecimal("transferringbalance"));
        } catch (Exception e) {
        }
        try {
            accountMovement.setTotalIncoming(rs.getBigDecimal("sumincoming"));

        } catch (Exception e) {
        }
        try {
            accountMovement.setTotalOutcoming(rs.getBigDecimal("sumoutcoming"));

        } catch (Exception e) {
        }

        try {
            accountMovement.getInvoice().setDueDate(rs.getTimestamp("invduedate"));
        } catch (Exception e) {
        }
        return accountMovement;
    }

}
