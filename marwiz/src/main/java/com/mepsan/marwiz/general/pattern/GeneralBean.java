/**
 * Bu Sınıf Global Bean
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   23.09.2016 11:46:48
 */
package com.mepsan.marwiz.general.pattern;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.gridproperties.service.IGridOrderColumnService;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.general.UserDataGridColumn;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.model.wot.DataTableColumn;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.column.Column;
import org.primefaces.component.columntoggler.ColumnToggler;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.event.data.SortEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.model.Visibility;

public abstract class GeneralBean<T> extends AuthenticationLists {

//    @ManagedProperty(value = "#{gridToggleColumnService}")
//    private IGridToggleColumnService toggleColumnService;
    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean1;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz1;

    @ManagedProperty(value = "#{gridOrderColumnService}")
    public IGridOrderColumnService gridOrderColumnService;

//    public void setToggleColumnService(IGridToggleColumnService toggleColumnService) {
//        this.toggleColumnService = toggleColumnService;
//    }
    public LazyDataModel<T> listOfObjects;
    public T selectedObject;
    public String autoCompleteValue;
    public List<Boolean> toogleList;
    public boolean renderExportButton = true;
    private int gridId;
    private UserDataGridColumn userDataGridColumn;
    private PDFOptions pdfOpt;
    private ExcelOptions excelOptions;
    private String nowDate;
    private int countToggle;
    private List<DataTableColumn> columns = new ArrayList<>();
    public List<Boolean> toogleList_test;

    public void setGridOrderColumnService(IGridOrderColumnService gridOrderColumnService) {
        this.gridOrderColumnService = gridOrderColumnService;
    }

    public void setMarwiz1(Marwiz marwiz) {
        this.marwiz1 = marwiz;
    }

    public void setSessionBean1(SessionBean sessionBean1) {
        this.sessionBean1 = sessionBean1;
    }

    public LazyDataModel<T> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(LazyDataModel<T> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public T getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(T selectedObject) {
        this.selectedObject = selectedObject;
    }

    public String getAutoCompleteValue() {
        return autoCompleteValue;
    }

    public void setAutoCompleteValue(String autoCompleteValue) {
        this.autoCompleteValue = autoCompleteValue;
    }

    public List<Boolean> getToogleList() {
        return toogleList;
    }

    public void setToogleList(List<Boolean> toogleList) {
        this.toogleList = toogleList;
    }

    public boolean isRenderExportButton() {
        return renderExportButton;
    }

    public void setRenderExportButton(boolean renderExportButton) {
        this.renderExportButton = renderExportButton;
    }

    public ExcelOptions getExcelOptions() {
        return excelOptions;
    }

    public void setExcelOptions(ExcelOptions excelOptions) {
        this.excelOptions = excelOptions;
    }

    public int getGridId() {
        return gridId;
    }

    public void setGridId(int gridId) {
        this.gridId = gridId;
    }

    public UserDataGridColumn getUserDataGridColumn() {
        return userDataGridColumn;
    }

    public void setUserDataGridColumn(UserDataGridColumn userDataGridColumn) {
        this.userDataGridColumn = userDataGridColumn;
    }

    public String getNowDate() {
        Date date = new Date();
        return StaticMethods.convertToDateFormat(sessionBean1.getUser().getLastBranch().getDateFormat(), date);
    }

    public void setNowDate(String nowDate) {
        this.nowDate = nowDate;
    }

    public int getCountToggle() {
        return countToggle;
    }

    public void setCountToggle(int countToggle) {
        this.countToggle = countToggle;
    }

    public abstract void init();

    public abstract void create();

    public abstract void save();

    public abstract void generalFilter();

    public abstract LazyDataModel<T> findall(String where);

    public abstract void detailFilter();

    /**
     *
     * @param document
     * @throws IOException
     * @throws BadElementException
     * @throws DocumentException
     */
    public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {

        StaticMethods.preProcessPDF(document);

    }

    /**
     *
     * @param document
     */
    public void postProcessXLS(Object document) {

        StaticMethods.postProcessXLS(document);

    }

    /**
     * Export edilen dosyanın adındaki türkçe karakterlerin de yazılmasını
     * sağlayan methodtur.
     *
     * @param filename
     * @return
     * @throws UnsupportedEncodingException
     */
    public String encodeFileNameForExportData(String filename) throws UnsupportedEncodingException {
        return StaticMethods.encodeFileNameForExportData(filename);
    }

    public String encodeFileNameForExportDataPdf(String filename) throws UnsupportedEncodingException {

        return StaticMethods.encodeFileNameForExportDataPdf(filename);
    }

    /**
     *
     */
    public void reset() {
        autoCompleteValue = null;
        generalFilter();
    }

    /**
     *
     * @param e
     */
    public void onResize(ColumnResizeEvent event) {
        System.out.println(((DataTable) event.getSource()).getClientId() + "    Column " + event.getColumn().getClientId() + " resized W:" + event.getWidth() + ", H:" + event.getHeight());
        // System.out.println(this.columns.size());
        for (DataTableColumn column : this.columns) {
            if (column.getId().equals(event.getColumn().getClientId())) {
                column.setWidth(event.getWidth());
                ((Column) event.getColumn()).setWidth(String.valueOf(event.getWidth()));
                // System.out.println("*-*-*- " + column.getWidth());
                break;
            }
        }

        String json = new Gson().toJson(this.columns);
        //   System.out.println("*-*-*-*-* " + json);

        gridOrderColumnService.reorder(marwiz1.getPageIdOfGoToPage(), ((DataTable) event.getSource()).getClientId(), json);

        /*   for (DataTableColumn column1 : columns) {
            System.out.println(column1.getId() + " - " + column1.getIndex() + " - " + column1.getWidth() + " - " + column1.isVisibility());
        }*/
    }

    public void reorder(AjaxBehaviorEvent event) {
        DataTable table = (DataTable) event.getSource();
        for (UIColumn column : table.getColumns()) {
            for (DataTableColumn column1 : this.columns) {
                if (column.getClientId().equals(column1.getId())) {
                    column1.setIndex(table.getColumns().indexOf(column));
                }
            }

        }

        gridOrderColumnService.reorder(marwiz1.getPageIdOfGoToPage(), table.getClientId(), new Gson().toJson(this.columns));

        // RequestContext.getCurrentInstance().execute("order1();");
        // RequestContext.getCurrentInstance().update("frmToolbarAccount:toggle");

        /*   for (DataTableColumn column1 : columns) {
            System.out.println(column1.getId() + " - " + column1.getIndex() + " - " + column1.getWidth() + " - " + column1.isVisibility());
        }*/
    }

    public void bringOrder(String gridId) {
        // System.out.println("*-*--*-*-*-*-*- " + gridId);
        String jsonProperties = gridOrderColumnService
                .bringOrder(
                        marwiz1
                                .getPageIdOfGoToPage(),
                        gridId);
        columns.clear();
        Gson gson = new Gson();
        try {
            this.columns = gson.fromJson(jsonProperties, new TypeToken<ArrayList<DataTableColumn>>() {
            }.getType());

            if (this.columns == null) {
                this.columns = new ArrayList<>();
            }

            /*  for (DataTableColumn column1 : this.columns) {
                System.out.println(column1.getId() + " - " + column1.getIndex() + " - " + column1.getWidth() + " - " + column1.isVisibility());
            }*/
        } catch (Exception e) {
            this.columns = new ArrayList<>();
        }
        updateGridProperties(gridId);

    }

    public void onToggle(ToggleEvent e) {
        // System.out.println(this.columns.size());

        ColumnToggler columnToggler = (ColumnToggler) e.getSource();
        // System.out.println(columns.size() + "*-*-*--* " + columnToggler.getDatasource());

        if (columns != null && !columns.isEmpty()) {
            for (DataTableColumn column1 : columns) {
                if (column1.getIndex() == (Integer) e.getData()) {
                    column1.setVisibility(e.getVisibility() == Visibility.VISIBLE);
                }
            }

            String json = new Gson().toJson(columns);
            //  System.out.println("json " + json);
            gridOrderColumnService.reorder(marwiz1.getPageIdOfGoToPage(), columnToggler.getDatasource(), json);
            /*  for (DataTableColumn column1 : columns) {
            System.out.println(column1.getId() + " - " + column1.getIndex() + " - " + column1.getWidth() + " - " + column1.isVisibility());
        }*/

            updateGridProperties(columnToggler.getDatasource());
        }

        countToggle = 0;
        //Duruma Göre Görünmesini istemedğimiz kolonları gizlemek için
        if (toogleList_test == null) {
            this.toogleList_test = new ArrayList<>();
            this.toogleList_test.addAll(toogleList);
        }
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(columnToggler.getDatasource());
        
        int data = (Integer) e.getData();
        int c = -1;
        for (UIColumn column : dataTable.getColumns()) {
            if (!column.isRendered()) {
                data++;
            }
            c++;
            if (c == data) {
                toogleList.set(c, e.getVisibility() == Visibility.VISIBLE);
                break;
            }
        }
        //Duruma Göre Görünmesini istemedğimiz kolonları gizlemek için

        String columnindexes = "";
        int i = 0;
        for (Boolean toggle : toogleList) {
            if (i > 0) {
                if (toggle.equals(false)) {
                    columnindexes = columnindexes + ",0";
                    countToggle++;
                } else {
                    columnindexes = columnindexes + ",1";
                }
            } else if (toggle.equals(false)) {
                columnindexes = columnindexes + "0";
                countToggle++;
            } else {
                columnindexes = columnindexes + "1";
            }
            i++;
        }

        //  System.out.println("-*-*-*-* " + columnindexes);
        if (countToggle == toogleList.size()) {
            renderExportButton = false;
        } else {
            renderExportButton = true;
        }
    }

    public PDFOptions getPdfOpt() {
        return pdfOpt;
    }

    public void setPdfOpt(PDFOptions pdfOpt) {
        this.pdfOpt = pdfOpt;
    }

    public List<Boolean> createToggleList(UserData userData) {
        pdfOpt = new PDFOptions();
        pdfOpt.setFacetBgColor("#000000");
        pdfOpt.setFacetFontColor("#FFFFFF");
        pdfOpt.setFacetFontStyle("BOLD");
        pdfOpt.setCellFontSize("11");
        excelOptions = new ExcelOptions();
        excelOptions.setFacetBgColor("#000000");
        excelOptions.setFacetFontColor("#FFFFFF");
        excelOptions.setFacetFontStyle("BOLD");
        excelOptions.setCellFontSize("10");

        List<Boolean> result = new ArrayList<>();

        return result;
    }

    public void updateGridProperties(String gridId) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        DataTable table = (DataTable) root.findComponent(gridId);
        int i = 0;
        if (columns.isEmpty()) {
            for (UIColumn column : table.getColumns()) {

                if (!(column.getClientId().equals("frmStock:dtbStockFull:clmCentralSupplier") && "frmStock:dtbStockFull".equals(gridId))) {

                    DataTableColumn dataTableColumn = new DataTableColumn(column.getClientId());
                    dataTableColumn.setIndex(i);
                    dataTableColumn.setVisibility(column.isVisible());
                    i++;

                    columns.add(dataTableColumn);
                }
            }
        } else {

            List<UIColumn> newOrder = new ArrayList<>();
            for (UIColumn column : table.getColumns()) {
                if (!(column.getClientId().equals("frmStock:dtbStockFull:clmCentralSupplier") && "frmStock:dtbStockFull".equals(gridId))) {
                    newOrder.add(null);
                }
            }
            List<DataTableColumn> tempList = new ArrayList<>();
            tempList.addAll(columns);
            Collections.sort(tempList, new Comparator<DataTableColumn>() { // küçükten büyüğe doğru liste içerisinde sıralama yaptı.
                public int compare(DataTableColumn o1, DataTableColumn o2) {
                    return Integer.compare(o1.getIndex(), o2.getIndex());
                }
            });

            if (tempList.get(tempList.size() - 1).getIndex() >= columns.size()) { // nidex o zaman büyüktür liste baştan çekilir.
                columns.clear();

                for (UIColumn column : table.getColumns()) {
                    if (!(column.getClientId().equals("frmStock:dtbStockFull:clmCentralSupplier") && "frmStock:dtbStockFull".equals(gridId))) {
                        DataTableColumn dataTableColumn = new DataTableColumn(column.getClientId());
                        dataTableColumn.setIndex(i);
                        dataTableColumn.setVisibility(column.isVisible());
                        i++;
                        columns.add(dataTableColumn);
                    }
                }
            }

            for (UIColumn column : table.getColumns()) {

                Column newcolumn = (Column) column;
                if (!(column.getClientId().equals("frmStock:dtbStockFull:clmCentralSupplier") && "frmStock:dtbStockFull".equals(gridId))) {

                    for (DataTableColumn column1 : columns) {

                        if (column1.getId().equals(newcolumn.getClientId())) {
                            if (column1.getWidth() != 0) {
                                newcolumn.setWidth(String.valueOf(column1.getWidth()));
                            }

                            newcolumn.setVisible(column1.isVisibility());
                            newOrder.set(column1.getIndex(), column);
                            break;
                        }
                    }

                }

            }

            if ("frmStock:dtbStock".equals(gridId) || "frmStock:dtbStockNoIntegration".equals(gridId) || "frmStock:dtbStockFull".equals(gridId)) {

                List<UIComponent> newOrder2 = new ArrayList<>();
                for (UIComponent column : table.getColumnGroup("footer").getChildren()) {
                    newOrder2.add(new Column());
                }
                for (UIComponent column : table.getColumnGroup("footer").getChildren()) {
                    Column newcolumn = (Column) column;
                    for (DataTableColumn column1 : columns) {
                        if (column1.getId().equals(newcolumn.getClientId().substring(0, newcolumn.getClientId().length() - 1))) {
                            if (column1.getWidth() != 0) {
                                newcolumn.setWidth(String.valueOf(column1.getWidth()));
                            }
                            newcolumn.setVisible(column1.isVisibility());
                            newOrder2.set(column1.getIndex(), column);
                            break;
                        }
                    }
                }
                table.getColumnGroup("footer").getChildren().clear();
                table.getColumnGroup("footer").getChildren().addAll(newOrder2);
            }

            table.setColumns(newOrder);
            RequestContext.getCurrentInstance().update(table.getClientId());
        }
        RequestContext.getCurrentInstance().update(table.getClientId());

    }

    public boolean getColumnVisibility(String columnId) {
        for (DataTableColumn column : columns) {
            if (column.getId().equals(columnId)) {
                return column.isVisibility();
            }
        }
        return true;
    }

    public void sortListener(SortEvent event) {

        System.out.println((event.isAscending() ? "ascending" : "descending") + "  -------- " + event.getSortColumn().getValueExpression("sortBy").getExpressionString());

//        userDataGridColumn = new UserDataGridColumn();
//        userDataGridColumn.setSortColumn(event.getSortColumn().getValueExpression("sortBy").getExpressionString().replace("#{", "").replace("}", ""));
//        userDataGridColumn.setSortOrder(event.isAscending() ? "ASC" : "DESC");
//        userDataGridColumn.setGrid(new Grid(gridId));
        //   toggleColumnService.SetSortColumn(userDataGridColumn);
    }

}
