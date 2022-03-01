/**
 * Bu Sınıf ...for Global FilerData
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   02.08.2016 14:56:19
 */
package com.mepsan.marwiz.general.pattern;

import java.util.List;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;

public abstract class BookFilterBean<T> {

    public List<T> dataList;
    public LazyDataModel<T> dataListLazyLoading;
    public String autoCompleteValue;
    public List<T> selectedDataList;
    public boolean isAll;
    /* Genel filtreleme için inputtextin onkeyup olayında arama yapılıyordu.                                   
       Autocomplete özelliği pasif yapıldı. Daha sonra autocomplote acılabilir.*/

    private List<Object> params;

    T selectedData;
    public String update, dialogId, type, dlg;

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public LazyDataModel<T> getDataListLazyLoading() {
        return dataListLazyLoading;
    }

    public void setDataListLazyLoading(LazyDataModel<T> dataListLazyLoading) {
        this.dataListLazyLoading = dataListLazyLoading;
    }

    public T getSelectedData() {
        return selectedData;
    }

    public void setSelectedData(T selectedData) {
        this.selectedData = selectedData;
    }

    public List<T> getSelectedDataList() {
        return selectedDataList;
    }

    public void setSelectedDataList(List<T> selectedDataList) {
        this.selectedDataList = selectedDataList;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getAutoCompleteValue() {
        return autoCompleteValue;
    }

    public void setAutoCompleteValue(String autoCompleteValue) {
        this.autoCompleteValue = autoCompleteValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    public String getDlg() {
        return dlg;
    }

    public void setDlg(String dlg) {
        this.dlg = dlg;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    /**
     * this method will called when we want to get list of data
     *
     * @param params List of parameters for calling service of suitable object
     * @param update mention to updated component after select one object
     * @param dlg mention to Dialog or overload which will be hide after select
     * @param type
     */
    public void findFilterList(List<Object> params, String update, String dlg, String type) {
        this.dialogId = "PF('" + dlg + "').hide()";
        this.update = update;
        this.type = type;
        this.params = params;
        this.dlg = dlg;
        this.dataList = callService(params, type);

    }

    /**
     * this method will called when we want to get list of data
     *
     * @param params List of parameters for calling service of suitable object
     * @param update mention to updated component after select one object
     * @param dlg mention to Dialog or overload which will be hide after select
     * @param type
     */
    public void findFilterListLazyLoading(List<Object> params, String update, String dlg, String type) {
        this.autoCompleteValue = new String();
        this.dialogId = "PF('" + dlg + "').hide()";
        this.update = update;
        this.type = type;
        this.params = params;
        this.dlg = dlg;    
        this.dataListLazyLoading = callServiceLazyLoading(" ", params, type);

        //    RequestContext.getCurrentInstance().execute("PF('" + dlg + "').loadContents()");
    }

    public void refresh() {
        System.out.println("**********type*****" + type);
        findFilterListLazyLoading(params, update, dlg, type);
        RequestContext.getCurrentInstance().update(this.update);

    }

    /**
     * this method to override by who will create Object
     *
     * @param param List of parameters for calling service of suitable object
     * @param type
     * @return
     */
    public abstract List<T> callService(List<Object> param, String type);

    public abstract LazyDataModel<T> callServiceLazyLoading(String where, List<Object> param, String type);

    public abstract void generalFilter(String type, List<Object> param);

}
