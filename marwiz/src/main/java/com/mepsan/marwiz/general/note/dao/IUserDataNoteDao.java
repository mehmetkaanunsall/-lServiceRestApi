/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.note.dao;

import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author Gozde Gursel
 */
public interface IUserDataNoteDao extends ICrud<UserDataNote> {

    public List<UserDataNote> find();

    public int deleteNote(int noteId);

}
