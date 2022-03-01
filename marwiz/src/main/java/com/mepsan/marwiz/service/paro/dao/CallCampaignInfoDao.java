/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   05.07.2021 03:01:08
 */
package com.mepsan.marwiz.service.paro.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.system.branch.dao.BranchSettingMapper;
import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class CallCampaignInfoDao extends JdbcDaoSupport implements ICallCampaignInfoDao {

    @Override
    public List<BranchSetting> findBranchSettingsForCampaignInfo() {
        String sql
                  = "SELECT\n"
                  + "	brs.id AS brsid,\n"
                  + "	brs.parourl AS brsparourl,\n"
                  + "	brs.parocenteraccountcode AS brsparocenteraccountcode,\n"
                  + "	brs.parocenterresponsiblecode AS brsparocenterresponsiblecode,\n"
                  + "	br.licencecode AS brlicencecode,\n"
                  + "	br.id AS brid,\n"
                  + "   br.concepttype_id AS brnconcepttype_id\n"
                  + "FROM \n"
                  + "	general.branchsetting brs\n"
                  + "	INNER JOIN general.branch br ON(br.id=brs.branch_id)\n"
                  + "WHERE \n"
                  + "   brs.parourl IS NOT NULL\n"
                  + "	AND brs.deleted=FALSE\n"
                  + "ORDER BY br.id \n";

        List<BranchSetting> result = getJdbcTemplate().query(sql, new BranchSettingMapper());
        return result;
    }

    @Override
    public int updateParoInformation(BranchSetting obj, String pointOfSaleIntegrationList) {
        String sql = "";
        Object[] param;

        if (!pointOfSaleIntegrationList.equals("")) {

            sql = "UPDATE general.branchsetting\n"
                      + "SET\n"
                      + "paroaccountcode = ? ,\n"
                      + "parobranchcode = ? ,\n"
                      + "paroresponsiblecode = ? ,\n"
                      + "u_id= ? ,\n"
                      + "u_time= now()\n"
                      + "WHERE id = ? AND deleted = false;\n"
                      + "UPDATE\n"
                      + "   general.pointofsale pp\n"
                      + "SET\n"
                      + "   integrationcode 	= jj.integrationcode,\n"
                      + "   u_id		= ?,\n"
                      + "   u_time		= NOW()\n"
                      + "FROM \n"
                      + "	(SELECT \n"
                      + "          rr.id,\n"
                      + "          tt.integrationcode\n"
                      + "      FROM\n"
                      + "          (\n"
                      + "          SELECT \n"
                      + "              ROW_NUMBER () OVER (ORDER BY pos.id) AS row_id,\n"
                      + "              pos.*\n"
                      + "           FROM\n"
                      + "              general.pointofsale pos\n"
                      + "           WHERE  \n"
                      + "              pos.deleted=FALSE AND pos.branch_id=? AND pos.status_id = 9\n"
                      + "           ORDER BY pos.integrationcode ASC\n"
                      + "          ) rr\n"
                      + "          INNER JOIN (\n"
                      + "          SELECT \n"
                      + "              ROW_NUMBER () OVER (ORDER BY yy.integrationcode) AS row_id,\n"
                      + "              yy.integrationcode \n"
                      + "           FROM\n"
                      + "              (SELECT unnest(string_to_array('" + pointOfSaleIntegrationList + "', ',')) AS integrationcode) yy\n"
                      + "          ) tt ON (tt.row_id = rr.row_id)\n"
                      + "	) jj \n"
                      + "WHERE \n"
                      + "	jj.id = pp.id";
            param = new Object[]{obj.getParoAccountCode(), obj.getParoBranchCode(), obj.getParoResponsibleCode(),
                1, obj.getId(), 1, obj.getBranch().getId()};
        } else {
            sql = "UPDATE general.branchsetting\n"
                      + "SET\n"
                      + "paroaccountcode = ? ,\n"
                      + "parobranchcode = ? ,\n"
                      + "paroresponsiblecode = ? ,\n"
                      + "u_id= ? ,\n"
                      + "u_time= now()\n"
                      + "WHERE id = ? AND deleted = false;\n";

            param = new Object[]{obj.getParoAccountCode(), obj.getParoBranchCode(), obj.getParoResponsibleCode(),
                1, obj.getId()};
        }
        try {
            getJdbcTemplate().update(sql, param);
            return 1;
        } catch (DataAccessException e) {
            return -Integer.valueOf(((SQLException) e.getCause()).getSQLState());
        }
    }

}
