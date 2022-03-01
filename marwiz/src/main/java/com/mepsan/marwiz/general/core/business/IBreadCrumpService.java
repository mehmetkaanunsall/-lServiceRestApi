/**
 * Bu interface BreadCrumpService sınıfına arayüz oluşturur
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.07.2016 08:50:42
 */
package com.mepsan.marwiz.general.core.business;

public interface IBreadCrumpService {

    public void createBreadcrumb();

    public void addItemForPage(BreadCrumbService breadCrumb, int pageId, String pageUrl);

    public void addItemForDynamicPage(BreadCrumbService breadCrumb, int type);

}
