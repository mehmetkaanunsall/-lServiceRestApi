/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Jan 18, 2019 4:26:15 PM
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.ArrayList;
import java.util.List;

public class Authentication {

    private int pageId;
    private int userId;
    private List<Integer> list;

    public Authentication() {
        list = new ArrayList<>();
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

}
