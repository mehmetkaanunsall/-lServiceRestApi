/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Mar 6, 2018 3:51:30 PM
 */
package com.mepsan.marwiz.general.model.wot;

import java.util.ArrayList;
import java.util.List;

public class PrintDocumentTemplate  {

    private List<DataTableColumn> items;
    private List<Boolean> toggleList;
    private List<DocumentTemplateObject> listOfObjects;

    public List<DataTableColumn> getItems() {
        return items;
    }

    public void setItems(List<DataTableColumn> items) {
        this.items = items;
    }

    public List<Boolean> getToggleList() {
        return toggleList;
    }

    public void setToggleList(List<Boolean> toggleList) {
        this.toggleList = toggleList;
    }

    public List<DocumentTemplateObject> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<DocumentTemplateObject> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public PrintDocumentTemplate() {
        listOfObjects= new ArrayList<>();
    }
    
    

}
