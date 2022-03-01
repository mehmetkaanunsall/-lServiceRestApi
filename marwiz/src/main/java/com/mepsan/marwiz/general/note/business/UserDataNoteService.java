/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.note.business;

import com.mepsan.marwiz.general.note.dao.IUserDataNoteDao;
import com.mepsan.marwiz.general.note.dao.UserDataNote;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Gozde Gursel
 */
public class UserDataNoteService implements IUserDataNoteService {

    @Autowired
    IUserDataNoteDao userDataNoteDao;

    public void setUserDataNoteDao(IUserDataNoteDao userDataNoteDao) {
        this.userDataNoteDao = userDataNoteDao;
    }

    @Override
    public List<UserDataNote> find() {
        return userDataNoteDao.find();
    }

    @Override
    public int create(UserDataNote obj) {
        return userDataNoteDao.create(obj);
    }

    @Override
    public int update(UserDataNote obj) {
        return userDataNoteDao.update(obj);
    }

    @Override
    public int deleteNote(int noteId) {
        return userDataNoteDao.deleteNote(noteId);
    }

}
