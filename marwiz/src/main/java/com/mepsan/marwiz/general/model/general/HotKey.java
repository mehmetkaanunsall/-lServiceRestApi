/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 18.04.2017 09:02:03
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import javax.validation.constraints.Size;

public class HotKey extends WotLogging {

    private int id;
    private UserData userData;

    @Size(max = 60)
    private String hotkey;
    private Page page;

    public HotKey(int id) {
        this.id = id;
    }

    public HotKey() {
        this.page = new Page();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public String getHotkey() {
        return hotkey;
    }

    public void setHotkey(String hotkey) {
        this.hotkey = hotkey;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return this.getHotkey();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
