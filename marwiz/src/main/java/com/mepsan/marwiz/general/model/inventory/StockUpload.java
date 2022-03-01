/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:22:06 AM
 */
package com.mepsan.marwiz.general.model.inventory;

import com.mepsan.marwiz.general.model.general.Categorization;

public class StockUpload extends Stock {

    private int excelDataType; //import edilen dosyadan gelen kaydın hatalı olup olmadığı bilgisi için eklendi.
    private int purchaseTaxGroupId; // vergi grubu satın alma için 
    private int saleTaxGruopId; // vergi grubu satış için
    private StockAlternativeBarcode alternativeBarcode;
    private StockAlternativeBarcode alternativeBarcode2;
    private StockAlternativeBarcode alternativeBarcode3;
    private StockAlternativeBarcode alternativeBarcode4;
    private StockAlternativeBarcode alternativeBarcode5;
    private Categorization parentCategory;
    private Categorization subCategoty;
    
    public StockUpload(){
        this.alternativeBarcode=new StockAlternativeBarcode();
        this.alternativeBarcode2=new StockAlternativeBarcode();
        this.alternativeBarcode3=new StockAlternativeBarcode();
        this.alternativeBarcode4=new StockAlternativeBarcode();
        this.alternativeBarcode5=new StockAlternativeBarcode();
        this.parentCategory=new Categorization();
        this.subCategoty=new Categorization();
    }

    public int getExcelDataType() {
        return excelDataType;
    }

    public void setExcelDataType(int excelDataType) {
        this.excelDataType = excelDataType;
    }

    public int getPurchaseTaxGroupId() {
        return purchaseTaxGroupId;
    }

    public void setPurchaseTaxGroupId(int purchaseTaxGroupId) {
        this.purchaseTaxGroupId = purchaseTaxGroupId;
    }

    public int getSaleTaxGruopId() {
        return saleTaxGruopId;
    }

    public void setSaleTaxGruopId(int saleTaxGruopId) {
        this.saleTaxGruopId = saleTaxGruopId;
    }

    public StockAlternativeBarcode getAlternativeBarcode() {
        return alternativeBarcode;
    }

    public void setAlternativeBarcode(StockAlternativeBarcode alternativeBarcode) {
        this.alternativeBarcode = alternativeBarcode;
    }

    public StockAlternativeBarcode getAlternativeBarcode2() {
        return alternativeBarcode2;
    }

    public void setAlternativeBarcode2(StockAlternativeBarcode alternativeBarcode2) {
        this.alternativeBarcode2 = alternativeBarcode2;
    }

    public StockAlternativeBarcode getAlternativeBarcode3() {
        return alternativeBarcode3;
    }

    public void setAlternativeBarcode3(StockAlternativeBarcode alternativeBarcode3) {
        this.alternativeBarcode3 = alternativeBarcode3;
    }

    public StockAlternativeBarcode getAlternativeBarcode4() {
        return alternativeBarcode4;
    }

    public void setAlternativeBarcode4(StockAlternativeBarcode alternativeBarcode4) {
        this.alternativeBarcode4 = alternativeBarcode4;
    }

    public StockAlternativeBarcode getAlternativeBarcode5() {
        return alternativeBarcode5;
    }

    public void setAlternativeBarcode5(StockAlternativeBarcode alternativeBarcode5) {
        this.alternativeBarcode5 = alternativeBarcode5;
    }

    public Categorization getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Categorization parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Categorization getSubCategoty() {
        return subCategoty;
    }

    public void setSubCategoty(Categorization subCategoty) {
        this.subCategoty = subCategoty;
    }
    

}
