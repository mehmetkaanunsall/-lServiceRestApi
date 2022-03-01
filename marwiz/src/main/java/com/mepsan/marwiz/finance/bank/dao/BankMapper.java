/**
 *
 *
 *
 * @author Merve Karakarçayıldız
 *
 * @date 15.01.2018 14:05:40
 */
package com.mepsan.marwiz.finance.bank.dao;

import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.springframework.jdbc.core.RowMapper;

public class BankMapper implements RowMapper<Bank> {

    @Override
    public Bank mapRow(ResultSet rs, int i) throws SQLException {
        Bank bank = new Bank();

        bank.setId(rs.getInt("bnkid"));
        bank.setName(rs.getString("bnkname"));

        try {
            bank.setCode(rs.getString("bnkcode"));
        } catch (Exception e) {

        }
        try {
            bank.setPhone(rs.getString("bnkphone"));
            bank.setEmail(rs.getString("bnkmail"));
            bank.setAddress(rs.getString("bnkaddress"));
            bank.getStatus().setId(rs.getInt("bnkstatus_id"));
            bank.getStatus().setTag(rs.getString("sttdname"));
            bank.getCity().setId(rs.getInt("bnkcity_id"));
            bank.getCity().setTag(rs.getString("ctydname"));
            bank.getCountry().setId(rs.getInt("bnkcountry_id"));
            bank.getCountry().setTag(rs.getString("ctrdname"));
            bank.getCounty().setName(rs.getString("cntyname"));
            bank.getCounty().setId(rs.getInt("bnkcounty_id"));
        } catch (Exception e) {

        }

        try {
            List<BankBranch> listOfBranch = new ArrayList<>();
            if (rs.getString("bnkbranchs") != null) {
                JSONArray jsonArray = new JSONArray(rs.getString("bnkbranchs"));
                for (int j = 0; j < jsonArray.length(); j++) {
                    BankBranch branch = new BankBranch();
                    branch.setId(jsonArray.getJSONObject(j).getInt("bkbid"));
                    if (!jsonArray.getJSONObject(j).isNull("bkbname")) {
                        branch.setName(jsonArray.getJSONObject(j).getString("bkbname"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbcode")) {
                        branch.setCode(jsonArray.getJSONObject(j).getString("bkbcode"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbstatus_id")) {
                        branch.getStatus().setId(jsonArray.getJSONObject(j).getInt("bkbstatus_id"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbsttdname")) {
                        branch.getStatus().setTag(jsonArray.getJSONObject(j).getString("bkbsttdname"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbphone")) {
                        branch.setPhone(jsonArray.getJSONObject(j).getString("bkbphone"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbemail")) {
                        branch.setEmail(jsonArray.getJSONObject(j).getString("bkbemail"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbaddress")) {
                        branch.setAddress(jsonArray.getJSONObject(j).getString("bkbaddress"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbcity_id")) {
                        branch.getCity().setId(jsonArray.getJSONObject(j).getInt("bkbcity_id"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbctydname")) {
                        branch.getCity().setTag(jsonArray.getJSONObject(j).getString("bkbctydname"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbctycountry_id")) {
                        branch.getCountry().setId(jsonArray.getJSONObject(j).getInt("bkbctycountry_id"));
                    }

                    if (!jsonArray.getJSONObject(j).isNull("bkbctrdname")) {
                        branch.getCountry().setTag(jsonArray.getJSONObject(j).getString("bkbctrdname"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbcounty_id")) {
                        branch.getCounty().setId(jsonArray.getJSONObject(j).getInt("bkbcounty_id"));
                    }
                    if (!jsonArray.getJSONObject(j).isNull("bkbcntyname")) {
                        branch.getCounty().setName(jsonArray.getJSONObject(j).getString("bkbcntyname"));
                    }

                    branch.setBank(bank);
                    listOfBranch.add(branch);
                }

            }

            bank.setListOfBranchs(listOfBranch);
        } catch (Exception e) {

        }

        return bank;
    }

}
