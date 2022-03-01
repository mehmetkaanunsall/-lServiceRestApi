/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 29.04.2019 14:57:34
 */
package com.mepsan.marwiz.system.sapintegration.dao;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.log.SendSap;
import com.mepsan.marwiz.system.sapintegration.business.SapIntegration;
import com.mepsan.marwiz.system.sapintegration.business.SapIntegrationMapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SapIntegrationDao extends JdbcDaoSupport implements ISapIntegrationDao {

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<SapIntegration> listOfCollections(Date begin, Date end, int isSend) {
        String sql = "SELECT\n"
                  + "    fdoc.id as fodcid,\n"
                  + "    fdoc.documentdate as fdocdocumentdate,\n"
                  + "    fdoc.type_id as fdoctype_id,\n"
                  + "    COALESCE(fdoc.description,'-') as fdocdesciption,\n"
                  + "    fdoc.price*fdoc.exchangerate as fdocprice,\n"
                  + "    fdoc.currency_id as fdoccurrency_id,\n"
                  + "    brs.erpintegrationcode as brserpintegrationcode,\n"
                  + "    br.id as branch_id,\n"
                  + "    COALESCE(sf.code,'') as sfcode,\n"
                  + "    COALESCE(ba.name,'') as baname,\n"
                  + "    COALESCE(ba.accountnumber,'') as baaccountnumber,\n"
                  + "    COALESCE(sap.is_send,FALSE) as sapis_send,\n"
                  + "    sap.response as sapresponse\n"
                  + "FROM general.accountmovement acm\n"
                  + "INNER JOIN finance.financingdocument fdoc ON (fdoc.id = acm.financingdocument_id AND fdoc.deleted = FALSE AND fdoc.type_id IN (47,73))--NT,KK\n"
                  + "LEFT JOIN finance.safemovement sfm ON (sfm.financingdocument_id = fdoc.id AND sfm.deleted = FALSE)\n"
                  + "LEFT JOIN finance.safe sf ON (sf.id = sfm.safe_id AND sf.deleted = FALSE AND sf.status_id = 23)\n"
                  + "LEFT JOIN finance.bankaccountmovement bam ON (bam.financingdocument_id = fdoc.id AND bam.deleted = FALSE)\n"
                  + "LEFT JOIN finance.bankaccount ba ON (ba.id = bam.bankaccount_id AND ba.deleted = FALSE AND ba.status_id = 21)\n"
                  + "LEFT JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = ba.id AND bbc.deleted=FALSE AND bbc.branch_id=?)\n"
                  + "LEFT JOIN general.branch br ON ((br.id = sf.branch_id OR br.id = bbc.branch_id) AND br.deleted = FALSE AND br.status_id = 47)\n"
                  + "LEFT JOIN general.branchsetting brs ON (brs.branch_id = br.id  AND brs.deleted = FALSE)\n"
                  + "LEFT JOIN log.sendsap sap ON (sap.financingdocument_id = fdoc.id)\n"
                  + "WHERE acm.deleted = FALSE "
                  + "AND acm.movementdate BETWEEN ? AND ? "
                  + "AND (? = 0 OR (? = 1 AND COALESCE(sap.is_send,FALSE)) OR (? = 2 AND COALESCE(sap.is_send,FALSE)<>TRUE))\n"
                  + "AND br.id = ? "
                  + "ORDER BY acm.movementdate DESC ";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), begin, end, isSend, isSend, isSend, sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new SapIntegrationMapper());
    }

    @Override
    public List<SapIntegration> listOfSafeToBank(Date begin, Date end, int isSend) {
        String sql = "SELECT\n"
                  + "    fdoc.id as fodcid,\n"
                  + "    fdoc.documentdate as fdocdocumentdate,\n"
                  + "    fdoc.type_id as fdoctype_id,\n"
                  + "    COALESCE(fdoc.description,'-') as fdocdesciption,\n"
                  + "    fdoc.price*fdoc.exchangerate as fdocprice,\n"
                  + "    fdoc.currency_id as fdoccurrency_id,\n"
                  + "    brs.erpintegrationcode as brserpintegrationcode,\n"
                  + "    br.id as branch_id,\n"
                  + "    COALESCE(sf.code,'') as sfcode,\n"
                  + "    COALESCE(ba.name,'') as baname,\n"
                  + "    COALESCE(ba.accountnumber,'') as baaccountnumber,\n"
                  + "    COALESCE(sap.is_send,FALSE) as sapis_send,\n"
                  + "    sap.response as sapresponse\n"
                  + "FROM finance.safemovement sfm\n"
                  + "INNER JOIN finance.financingdocument fdoc ON (fdoc.id = sfm.financingdocument_id AND fdoc.deleted = FALSE AND fdoc.type_id = 53)--Bankaya nakit yatırma\n"
                  + "INNER JOIN finance.safe sf ON (sf.id = sfm.safe_id AND sf.deleted = FALSE AND sf.status_id = 23)\n"
                  + "LEFT JOIN finance.bankaccountmovement bam ON (bam.financingdocument_id = fdoc.id AND bam.deleted = FALSE)\n"
                  + "LEFT JOIN finance.bankaccount ba ON (ba.id = bam.bankaccount_id AND ba.deleted = FALSE AND ba.status_id = 21)\n"
                  + "LEFT JOIN finance.bankaccount_branch_con bbc ON(bbc.bankaccount_id = ba.id AND bbc.deleted=FALSE AND bbc.branch_id=?)\n"
                  + "LEFT JOIN general.branch br ON ((br.id = sf.branch_id OR br.id = bbc.branch_id) AND br.deleted = FALSE AND br.status_id = 47)\n"
                  + "LEFT JOIN general.branchsetting brs ON (brs.branch_id = br.id  AND brs.deleted = FALSE)\n"
                  + "LEFT JOIN log.sendsap sap ON (sap.financingdocument_id = fdoc.id)\n"
                  + "WHERE sfm.deleted = FALSE\n"
                  + "AND sfm.movementdate BETWEEN ? AND ? "
                  + "AND (? = 0 OR (? = 1 AND COALESCE(sap.is_send,FALSE)) OR (? = 2 AND COALESCE(sap.is_send,FALSE)<>TRUE))\n"
                  + "AND br.id = ? "
                  + "ORDER BY sfm.movementdate DESC ";

        Object[] param = new Object[]{sessionBean.getUser().getLastBranch().getId(), begin, end, isSend, isSend, isSend, sessionBean.getUser().getLastBranch().getId()};
        return getJdbcTemplate().query(sql, param, new SapIntegrationMapper());
    }

    /**
     * bu metot gelen listedeki kayıtlarını logunu db ye ekler veya varsa
     * günceller
     *
     * @param integrations
     */
    @Override
    public int insertOrUpdateLog(SendSap sapResult) {

        String sql = "SELECT log.process_sap (?,?,?,?,?,?,?,?,?);";

        
         Object[] param = new Object[]{sapResult.getBranchId(), sapResult.getBranchCode(), sapResult.getFinancingDocumentId(), sapResult.getSendData(),
             sapResult.isIsSend(), sapResult.getSendBeginDate(), sapResult.getSendEndDate(), sapResult.getMessage(),sessionBean.getUser().getId() };


        try {
            return getJdbcTemplate().update(sql, param);
        } catch (DataAccessException e) {
            return 0;
        }
        
      
    }

}
