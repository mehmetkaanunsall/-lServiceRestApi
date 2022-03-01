/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 18.10.2018 17:52:41
 */
package com.mepsan.marwiz.inventory.pricelist.dao;

import com.mepsan.marwiz.general.model.wot.DocumentTemplateObject;
import java.util.List;

public class PriceTagTemplate {

    private List<DocumentTemplateObject> listOfObjects;

    public List<DocumentTemplateObject> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<DocumentTemplateObject> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }
}
