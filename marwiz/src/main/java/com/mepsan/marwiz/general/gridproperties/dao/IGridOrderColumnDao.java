/**
 * Bu interface ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   04.01.2019 11:08:34
 */
package com.mepsan.marwiz.general.gridproperties.dao;


public interface IGridOrderColumnDao {

    
    public int reorder(int pageId,String gridId,String reorder);
    
    public int update(int pageId,String gridId,String reorder);
    
    public String bringOrder(int pageId,String gridId);


}
