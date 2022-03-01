/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.note.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Gozde Gursel
 */
public class UserDataNoteMapper implements RowMapper<UserDataNote> {

    @Override
    public UserDataNote mapRow(ResultSet rs, int i) throws SQLException {
        UserDataNote note = new UserDataNote();

        note.setId(rs.getInt("ntid"));
        note.getUserData().setId(rs.getInt("ntuserdata_id"));
        note.setDescription(rs.getString("ntdescription"));

        note.setDateCreated(rs.getTimestamp("ntc_time"));
        note.setDateUpdated(rs.getTimestamp("ntu_time"));

        return note;
    }

}
