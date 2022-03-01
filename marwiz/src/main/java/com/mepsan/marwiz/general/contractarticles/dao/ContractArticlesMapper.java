/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:49:25 PM
 */
package com.mepsan.marwiz.general.contractarticles.dao;

import com.mepsan.marwiz.general.model.general.ContractArticles;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ContractArticlesMapper implements RowMapper<ContractArticles> {

    @Override
    public ContractArticles mapRow(ResultSet rs, int i) throws SQLException {
        ContractArticles contractArticles = new ContractArticles();

        contractArticles.setId(rs.getInt("caid"));
        contractArticles.getStock().setId(rs.getInt("castock_id"));
        contractArticles.setArticltType(rs.getInt("caarticletype"));
        contractArticles.setBranchProfitRate(rs.getBigDecimal("cabranchprofitrate"));
        contractArticles.setRate1(rs.getBigDecimal("carate1"));
        contractArticles.setVolume1(rs.getBigDecimal("cavolume1"));
        contractArticles.setRate2(rs.getBigDecimal("carate2"));
        contractArticles.setVolume2(rs.getBigDecimal("cavolume2"));
        contractArticles.setRate3(rs.getBigDecimal("carate3"));
        contractArticles.setVolume3(rs.getBigDecimal("cavolume3"));
        contractArticles.setRate4(rs.getBigDecimal("carate4"));
        contractArticles.setVolume4(rs.getBigDecimal("cavolume4"));
        contractArticles.setRate5(rs.getBigDecimal("carate5"));
        contractArticles.setVolume5(rs.getBigDecimal("cavolume5"));
        contractArticles.setWarehouseCost(rs.getBigDecimal("cawarehousecost"));

        try {
            contractArticles.getStock().setName(rs.getString("stckname"));
        } catch (Exception e) {
        }

        return contractArticles;
    }

}
