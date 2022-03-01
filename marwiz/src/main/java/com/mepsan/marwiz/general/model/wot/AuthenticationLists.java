/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.10.2016 17:15:22
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.List;

public class AuthenticationLists {

    private List<Integer> listBtn;
    private List<Integer> listTab;

    public List<Integer> getListBtn() {
        return listBtn;
    }

    public void setListBtn(List<Integer> listBtn) {
        this.listBtn = listBtn;
    }

    public List<Integer> getListTab() {
        return listTab;
    }

    public void setListTab(List<Integer> listTab) {
        this.listTab = listTab;
    }

    public boolean getRendered(int id, int type) {


        if (type == 0) {
            return listBtn.contains(id);
        } else {
            return listTab.contains(id);
        }
    }

}
