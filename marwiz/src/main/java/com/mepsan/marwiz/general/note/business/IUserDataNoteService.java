/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.note.business;

import com.mepsan.marwiz.general.note.dao.UserDataNote;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author Gozde Gursel
 */
public interface IUserDataNoteService extends ICrudService<UserDataNote> {

    public List<UserDataNote> find();
    
    public int deleteNote(int noteId);
}
