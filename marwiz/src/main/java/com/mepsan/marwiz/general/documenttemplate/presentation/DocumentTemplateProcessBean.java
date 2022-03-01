/**
 *
 * @author SALİM VELA ABDULHADİ
 * @author EMRULLAH YAKIŞAN
 *
 * Mar 1, 2018 3:01:41 PM
 */
package com.mepsan.marwiz.general.documenttemplate.presentation;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documenttemplate.business.IDocumentTemplateService;
import com.mepsan.marwiz.general.ftpConnection.presentation.FtpConnectionBean;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.model.wot.DataTableColumn;
import com.mepsan.marwiz.general.model.wot.DocumentTemplateObject;
import com.mepsan.marwiz.general.model.wot.PrintDocumentTemplate;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.BehaviorEvent;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.column.Column;
import org.primefaces.component.columntoggler.ColumnToggler;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.dnd.Draggable;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.resizable.Resizable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.Visibility;

@ManagedBean
@ViewScoped
public class DocumentTemplateProcessBean {

    @ManagedProperty("#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty("#{marwiz}")
    private Marwiz marwiz;

    @ManagedProperty(value = "#{documentTemplateService}")
    private IDocumentTemplateService documentTemplateService;

    @ManagedProperty(value = "#{ftpConnectionBean}")
    private FtpConnectionBean ftpConnectionBean;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    private DocumentTemplate selectedObject;
    private DocumentTemplateObject documentTemplateObject;
    private List<Boolean> toggleList;
    private List<Boolean> list;
    private PrintDocumentTemplate printDocumentTemplate;
    private String editText;
    private boolean renderEditText;
    private boolean renderImgUpload;
    private boolean renderDomesticProducts;
    private UploadedFile uploadedFile = null;
    private String fileName;
    private boolean withoutlabel;

    public boolean isWithoutlabel() {
        return withoutlabel;
    }

    public void setWithoutlabel(boolean withoutlabel) {
        this.withoutlabel = withoutlabel;
    }

    public boolean isRenderImgUpload() {
        return renderImgUpload;
    }

    public void setRenderImgUpload(boolean renderImgUpload) {
        this.renderImgUpload = renderImgUpload;
    }

    public void setFtpConnectionBean(FtpConnectionBean ftpConnectionBean) {
        this.ftpConnectionBean = ftpConnectionBean;
    }

    public IDocumentTemplateService getDocumentTemplateService() {
        return documentTemplateService;
    }

    public boolean isRenderEditText() {
        return renderEditText;
    }

    public void setRenderEditText(boolean renderEditText) {
        this.renderEditText = renderEditText;
    }

    public String getEditText() {
        return editText;
    }

    public void setEditText(String editText) {
        this.editText = editText;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setDocumentTemplateService(IDocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public DocumentTemplate getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(DocumentTemplate selectedObject) {
        this.selectedObject = selectedObject;
    }

    public DocumentTemplateObject getDocumentTemplateObject() {
        return documentTemplateObject;
    }

    public void setDocumentTemplateObject(DocumentTemplateObject documentTemplateObject) {
        this.documentTemplateObject = documentTemplateObject;
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public boolean isRenderDomesticProducts() {
        return renderDomesticProducts;
    }

    public void setRenderDomesticProducts(boolean renderDomesticProducts) {
        this.renderDomesticProducts = renderDomesticProducts;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @PostConstruct
    public void init() {
        printDocumentTemplate = new PrintDocumentTemplate();
        documentTemplateObject = new DocumentTemplateObject();
        printDocumentTemplate.setListOfObjects(new ArrayList<>());

        if (sessionBean.parameter instanceof DocumentTemplate) {
            selectedObject = (DocumentTemplate) sessionBean.parameter;

            if (selectedObject.getType().getId() == 62) { // fatura ise
                toggleList = Lists.newArrayList(true, true, true, true, true, true, true, true, true, true, true);

                printDocumentTemplate.setItems(Lists.newArrayList(new DataTableColumn("stockname"), new DataTableColumn("stockcode"), new DataTableColumn("stockbarcode"), new DataTableColumn("description"),
                          new DataTableColumn("quantity"), new DataTableColumn("unitprice"), new DataTableColumn("discountrate"), new DataTableColumn("taxrate"), new DataTableColumn("taxprice"), new DataTableColumn("taxfreeamount"), new DataTableColumn("totalprice")));

            } else if (selectedObject.getType().getId() == 63) { // irsaliye ise 
                toggleList = Lists.newArrayList(true, true, true, true, true, true, true, true);
                printDocumentTemplate.setItems(Lists.newArrayList(new DataTableColumn("stockname"), new DataTableColumn("stockcode"), new DataTableColumn("description"),
                          new DataTableColumn("quantity"), new DataTableColumn("unitprice"), new DataTableColumn("discount"), new DataTableColumn("totaltax"), new DataTableColumn("totalmoney")));

            } else if (selectedObject.getType().getId() == 70 || selectedObject.getType().getId() == 71) {

                toggleList = Lists.newArrayList(true, true, true, true, true, true, true, true);
                printDocumentTemplate.setItems(Lists.newArrayList(new DataTableColumn("payer"), new DataTableColumn("bankname"), new DataTableColumn("branchname"),
                          new DataTableColumn("chequenumber"), new DataTableColumn("accountnumber"), new DataTableColumn("duedate"), new DataTableColumn("chequeprice"), new DataTableColumn("totalmoney")));

            }
            //   System.out.println(selectedObject.getJson());
            loadJson(selectedObject.getJson());
            for (DocumentTemplateObject dto : printDocumentTemplate.getListOfObjects()) {
                if (dto.getKeyWord().contains("itemspnl")) {
                    if (selectedObject.getType().getId() == 62) {
                        if (printDocumentTemplate.getItems().get(0).isVisibility()) {
                            toggleList.set(0, true);
                        } else {
                            toggleList.set(0, false);
                        }
                        if (printDocumentTemplate.getItems().get(1).isVisibility()) {
                            toggleList.set(1, true);
                        } else {
                            toggleList.set(1, false);
                        }
                        if (printDocumentTemplate.getItems().get(2).isVisibility()) {
                            toggleList.set(2, true);
                        } else {
                            toggleList.set(2, false);
                        }
                        if (printDocumentTemplate.getItems().get(3).isVisibility()) {
                            toggleList.set(3, true);
                        } else {
                            toggleList.set(3, false);
                        }
                        if (printDocumentTemplate.getItems().get(4).isVisibility()) {
                            toggleList.set(4, true);
                        } else {
                            toggleList.set(4, false);
                        }
                        if (printDocumentTemplate.getItems().get(5).isVisibility()) {
                            toggleList.set(5, true);
                        } else {
                            toggleList.set(5, false);
                        }
                        if (printDocumentTemplate.getItems().get(6).isVisibility()) {
                            toggleList.set(6, true);
                        } else {
                            toggleList.set(6, false);
                        }
                        if (printDocumentTemplate.getItems().get(7).isVisibility()) {
                            toggleList.set(7, true);
                        } else {
                            toggleList.set(7, false);
                        }
                        if (printDocumentTemplate.getItems().get(8).isVisibility()) {
                            toggleList.set(8, true);
                        } else {
                            toggleList.set(8, false);
                        }
                        if (printDocumentTemplate.getItems().get(9).isVisibility()) {
                            toggleList.set(9, true);
                        } else {
                            toggleList.set(9, false);
                        }
                        if (printDocumentTemplate.getItems().get(10).isVisibility()) {
                            toggleList.set(10, true);
                        } else {
                            toggleList.set(10, false);
                        }

                    } else if (selectedObject.getType().getId() == 63) {
                        if (printDocumentTemplate.getItems().get(0).isVisibility()) {
                            toggleList.set(0, true);
                        } else {
                            toggleList.set(0, false);
                        }
                        if (printDocumentTemplate.getItems().get(1).isVisibility()) {
                            toggleList.set(1, true);
                        } else {
                            toggleList.set(1, false);
                        }
                        if (printDocumentTemplate.getItems().get(2).isVisibility()) {
                            toggleList.set(2, true);
                        } else {
                            toggleList.set(2, false);
                        }
                        if (printDocumentTemplate.getItems().get(3).isVisibility()) {
                            toggleList.set(3, true);
                        } else {
                            toggleList.set(3, false);
                        }
                        if (printDocumentTemplate.getItems().get(4).isVisibility()) {
                            toggleList.set(4, true);
                        } else {
                            toggleList.set(4, false);
                        }
                        if (printDocumentTemplate.getItems().get(5).isVisibility()) {
                            toggleList.set(5, true);
                        } else {
                            toggleList.set(5, false);
                        }
                        if (printDocumentTemplate.getItems().get(6).isVisibility()) {
                            toggleList.set(6, true);
                        } else {
                            toggleList.set(6, false);
                        }
                        if (printDocumentTemplate.getItems().get(7).isVisibility()) {
                            toggleList.set(7, true);
                        } else {
                            toggleList.set(7, false);
                        }
                    } else if (selectedObject.getType().getId() == 70 || selectedObject.getType().getId() == 71) {
                        if (printDocumentTemplate.getItems().get(0).isVisibility()) {
                            toggleList.set(0, true);
                        } else {
                            toggleList.set(0, false);
                        }
                        if (printDocumentTemplate.getItems().get(1).isVisibility()) {
                            toggleList.set(1, true);
                        } else {
                            toggleList.set(1, false);
                        }
                        if (printDocumentTemplate.getItems().get(2).isVisibility()) {
                            toggleList.set(2, true);
                        } else {
                            toggleList.set(2, false);
                        }
                        if (printDocumentTemplate.getItems().get(3).isVisibility()) {
                            toggleList.set(3, true);
                        } else {
                            toggleList.set(3, false);
                        }
                        if (printDocumentTemplate.getItems().get(4).isVisibility()) {
                            toggleList.set(4, true);
                        } else {
                            toggleList.set(4, false);
                        }
                        if (printDocumentTemplate.getItems().get(5).isVisibility()) {
                            toggleList.set(5, true);
                        } else {
                            toggleList.set(5, false);
                        }
                        if (printDocumentTemplate.getItems().get(6).isVisibility()) {
                            toggleList.set(6, true);
                        } else {
                            toggleList.set(6, false);
                        }
                        if (printDocumentTemplate.getItems().get(7).isVisibility()) {
                            toggleList.set(7, true);
                        } else {
                            toggleList.set(7, false);
                        }
                    }
                }
            }
        }

        ftpConnectionBean.initializeImage("template", String.valueOf(selectedObject.getId()));

    }

    public void onResize(ResizeEvent event) {
        documentTemplateObject.setWidth(Double.valueOf(event.getWidth()) * 0.25);
        documentTemplateObject.setHeight(Double.valueOf(event.getHeight()) * 0.25);
        documentTemplateObject.setKeyWord(((Resizable) event.getComponent()).getFor());
        System.out.println("Resize : " + ((Resizable) event.getComponent()).getFor());

        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        Panel panel = (Panel) root.findComponent(((Resizable) event.getComponent()).getFor());
        panel.setStyle(panel.getStyle() + "width:" + documentTemplateObject.getWidth() * 4 + "px;height:" + documentTemplateObject.getHeight() * 4 + "px;");
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setWidth(documentTemplateObject.getWidth());
                documentTemplate.setHeight(documentTemplateObject.getHeight());

                break;
            }
        }
        RequestContext.getCurrentInstance().execute("$('#" + ((Resizable) event.getComponent()).getFor() + "').click()");

    }

    public void unSelect() {
        renderEditText = false;
        RequestContext.getCurrentInstance().update("textPropPnl");
        RequestContext.getCurrentInstance().execute("PF('bui').show();");

    }

    public void onSelect() {
        try {
            Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            //    System.out.println("*-*-*-* height " + params.get("height"));
            String id = params.get("id");
            String top = params.get("top").replace("px", "");
            String left = params.get("left").replace("px", "");
            String width = params.get("width").replace("px", "");
            String height = params.get("height").replace("px", "");
            String fontsize = params.get("fontsize").replace("px", "");
            String textalign = params.get("textalign");
            String fontstyle = params.get("fontstyle");
            String fontweight = params.get("fontweight");
            String text = params.get("text");
            // System.out.println(id + " select ----- " + top + " ----- " + left + " ----- " + width + " ----- " + height + " ----- "
            //     + fontsize + " --------- " + textalign + " --------- " + fontstyle + " --------- " + fontweight + "   -----  " + text);

            documentTemplateObject.setWidth(Double.valueOf(width) * 0.25);
            documentTemplateObject.setHeight(Double.valueOf(height) * 0.25);
            documentTemplateObject.setLeft(Double.valueOf(left) * 0.25);
            documentTemplateObject.setTop(Double.valueOf(top) * 0.25);
            documentTemplateObject.setFontSize((int) (Math.round(Double.valueOf(fontsize))) * 72 / 96);
            documentTemplateObject.setFontAlign(textalign);
            documentTemplateObject.setKeyWord(id);
            documentTemplateObject.setFontStyle(Lists.newArrayList(fontstyle, fontweight));
            documentTemplateObject.setName(text);
            System.out.println(documentTemplateObject.getKeyWord());
            if (documentTemplateObject.getKeyWord().contains("invoicenopnl") || documentTemplateObject.getKeyWord().contains("dispatchdatepnl")
                      || documentTemplateObject.getKeyWord().contains("duedatepnl") || documentTemplateObject.getKeyWord().contains("invoicedate")) {
                withoutlabel = true;
                for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                    if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                        documentTemplateObject.setLabel(documentTemplate.isLabel());
                        break;
                    }
                }
            } else if (selectedObject.getType().getId() == 62) {//Fatura ise
                if (documentTemplateObject.getKeyWord().contains("dispatchaddresspnl") || documentTemplateObject.getKeyWord().contains("branchnamepnl")
                          || documentTemplateObject.getKeyWord().contains("branchaddresspnl") || documentTemplateObject.getKeyWord().contains("branchmailpnl")
                          || documentTemplateObject.getKeyWord().contains("branchtaxofficepnl") || documentTemplateObject.getKeyWord().contains("branchtaxnumberpnl")
                          || documentTemplateObject.getKeyWord().contains("branchtelephonepnl")
                          || documentTemplateObject.getKeyWord().contains("customertitlepnl") || documentTemplateObject.getKeyWord().contains("customertaxofficenumberpnl")
                          || documentTemplateObject.getKeyWord().contains("customeraddresspnl") || documentTemplateObject.getKeyWord().contains("customerphonepnl")
                          || documentTemplateObject.getKeyWord().contains("customertaxnumberpnl") || documentTemplateObject.getKeyWord().contains("customertaxofficepnl")
                          || documentTemplateObject.getKeyWord().contains("customerbalancepnl") || documentTemplateObject.getKeyWord().contains("itemspnl")
                          || documentTemplateObject.getKeyWord().contains("totalmoneypnl") || documentTemplateObject.getKeyWord().contains("totalpricetaxpnl")
                          || documentTemplateObject.getKeyWord().contains("totaltaxpnl") || documentTemplateObject.getKeyWord().contains("grandtotalmoneywritepnl")
                          || documentTemplateObject.getKeyWord().contains("totalpricepnl") || documentTemplateObject.getKeyWord().contains("totaldiscountpnl")
                          || documentTemplateObject.getKeyWord().contains("exchangeratepnl") || documentTemplateObject.getKeyWord().contains("deliverypersonpnl")) {
                    withoutlabel = true;
                    for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                        if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                            documentTemplateObject.setLabel(documentTemplate.isLabel());
                            break;
                        }
                    }
                } else {
                    withoutlabel = false;
                }
            } else if (selectedObject.getType().getId() == 96) {//Gider Pusulası
                if (documentTemplateObject.getKeyWord().contains("receiptitempnl")) {
                    withoutlabel = true;
                    for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                        if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                            documentTemplateObject.setLabel(documentTemplate.isLabel());
                            break;
                        }
                    }
                } else {
                    withoutlabel = false;
                }
            } else {
                withoutlabel = false;
            }

            if ((documentTemplateObject.getKeyWord()).substring(0, Math.min((documentTemplateObject.getKeyWord()).length(), 7)).equals("textpnl")) {
                renderEditText = true;
            } else {
                renderEditText = false;

            }
            if (documentTemplateObject.getKeyWord().contains("image")) {
                ftpConnectionBean.initializeImage("template", String.valueOf(selectedObject.getId() + "_" + documentTemplateObject.getKeyWord()));
                renderImgUpload = true;
            } else {
                renderImgUpload = false;

            }
            if (documentTemplateObject.getKeyWord().contains("domesticproductsimage")) {
                ftpConnectionBean.initializeImage("template", String.valueOf(selectedObject.getId() + "_" + documentTemplateObject.getKeyWord()));
                renderDomesticProducts = true;
            } else {
                renderDomesticProducts = false;

            }
            RequestContext.getCurrentInstance().update("form");
            RequestContext.getCurrentInstance().update("imgUploadPnl");
            RequestContext.getCurrentInstance().update("textPropPnl");

            RequestContext.getCurrentInstance().execute("toolbarIcons()");
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            // System.out.println(documentTemplateObject.getFontAlign() + " ------ exception selected object  ----- " + documentTemplateObject.getFontSize());
        }
    }

    public void onDrop() {
        documentTemplateObject = new DocumentTemplateObject();

        boolean found = false;

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        String text = params.get("text");
        String top = params.get("top").replace("px", "");
        String left = params.get("left").replace("px", "");
        String width = params.get("width").replace("px", "");
        String height = params.get("height").replace("px", "");
        String fontsize = params.get("fontsize").replace("px", "");
        String textalign = params.get("textalign");
        String fontstyle = params.get("fontstyle");
        String fontweight = params.get("fontweight");

        RequestContext.getCurrentInstance().update("textPropPnl");

        //  System.out.println(id + " ----- " + text + " ----- " + top + " ----- " + left + " ----- " + width + " ----- " + height + " ----- "
        //      + (int) (Math.round(Double.valueOf(fontsize))) * 72 / 96 + " --------- " + textalign + " --------- " + fontstyle + " --------- " + fontweight);
        //if (id.length() > 3 ) {
        /* String removeTxt;
        String textPnl = id.substring(0, Math.min(id.length(), 7));
        if (textPnl.equals("textpnl")) {
            renderEditText = true;
            removeTxt = textPnl.substring(textPnl.length() - 3);
        } else {
            renderEditText = false;
            removeTxt = id.substring(id.length() - 3);
        }*/
        if (id.contains("textpnl")) {
            renderEditText = true;
        } else {
            renderEditText = false;
        }

        if (!id.contains("pnl")) {
            for (DocumentTemplateObject dt : printDocumentTemplate.getListOfObjects()) {
                if (dt.getKeyWord().equals(id + "pnl") || dt.getKeyWord().equals(id)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                documentTemplateObject.setLeft(Double.valueOf(left) * 0.25);
                documentTemplateObject.setTop(Double.valueOf(top) * 0.25);
                if (id.equals("items")) {
                    documentTemplateObject.setWidth(160);
                    documentTemplateObject.setHeight(125);
                } else {
                    documentTemplateObject.setWidth(45);
                    documentTemplateObject.setHeight(7);
                }
//                int lastIndex = 0;
//                for (DocumentTemplateObject obj : printDocumentTemplate.getListOfObjects()) {
//                    if (obj.getId() > lastIndex) {
//                        lastIndex = obj.getId();
//                    }
//                }
//                System.out.println("printDocumentTemplate.getListOfObjects().size()=" + printDocumentTemplate.getListOfObjects().size());
//                System.out.println("lastIndex=" + lastIndex);
//                documentTemplateObject.setId(lastIndex + 1);

                Collections.sort(printDocumentTemplate.getListOfObjects(), new Comparator<DocumentTemplateObject>() {
                    public int compare(DocumentTemplateObject o1, DocumentTemplateObject o2) {
                        return Integer.compare(o1.getId(), o2.getId());
                    }
                });
                if (printDocumentTemplate.getListOfObjects().size() > 0) {
                    documentTemplateObject.setId(printDocumentTemplate.getListOfObjects().get(printDocumentTemplate.getListOfObjects().size() - 1).getId() + 1);
                } else {
                    documentTemplateObject.setId(1);
                }

                // if (id.equals("text")) {
                documentTemplateObject.setKeyWord(id + "pnl" + documentTemplateObject.getId());
                /*   } else {
                    documentTemplateObject.setKeyWord(id + "pnl");
                }*/
                documentTemplateObject.setName(text);
                documentTemplateObject.setFontSize(12);
                documentTemplateObject.setFontAlign("center");
                documentTemplateObject.setFontStyle(Lists.newArrayList("normal", "400"));
                //  System.out.println("----- lis before add " + printDocumentTemplate.getListOfObjects().size());

                //   System.out.println("*-*-*-*-* list " + printDocumentTemplate.getListOfObjects().size());
                if (id.equals("items")) {
                    boolean isThere = false;
                    for (DocumentTemplateObject a : printDocumentTemplate.getListOfObjects()) {
                        if (a.getKeyWord().contains("items")) {
                            isThere = true;
                            break;
                        }
                    }
                    if (!isThere) {
                        printDocumentTemplate.getListOfObjects().add(documentTemplateObject);
                    }

                    addItems(documentTemplateObject, printDocumentTemplate.getItems(), 1);
                } else {
                    printDocumentTemplate.getListOfObjects().add(documentTemplateObject);
                    addPanel(documentTemplateObject, 1);
                }

                System.out.println(documentTemplateObject.getKeyWord());
                if (documentTemplateObject.getKeyWord().contains("invoicenopnl") || documentTemplateObject.getKeyWord().contains("dispatchdatepnl")
                          || documentTemplateObject.getKeyWord().contains("duedatepnl") || documentTemplateObject.getKeyWord().contains("invoicedate")) {
                    withoutlabel = true;
                    for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                        if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                            documentTemplateObject.setLabel(documentTemplate.isLabel());
                            break;
                        }
                    }
                } else if (selectedObject.getType().getId() == 62) {//Fatura ise
                    if (documentTemplateObject.getKeyWord().contains("dispatchaddresspnl") || documentTemplateObject.getKeyWord().contains("branchnamepnl")
                              || documentTemplateObject.getKeyWord().contains("branchaddresspnl") || documentTemplateObject.getKeyWord().contains("branchmailpnl")
                              || documentTemplateObject.getKeyWord().contains("branchtaxofficepnl") || documentTemplateObject.getKeyWord().contains("branchtaxnumberpnl")
                              || documentTemplateObject.getKeyWord().contains("branchtelephonepnl")
                              || documentTemplateObject.getKeyWord().contains("customertitlepnl") || documentTemplateObject.getKeyWord().contains("customertaxofficenumberpnl")
                              || documentTemplateObject.getKeyWord().contains("customeraddresspnl") || documentTemplateObject.getKeyWord().contains("customerphonepnl")
                              || documentTemplateObject.getKeyWord().contains("customertaxnumberpnl") || documentTemplateObject.getKeyWord().contains("customertaxofficepnl")
                              || documentTemplateObject.getKeyWord().contains("customerbalancepnl") || documentTemplateObject.getKeyWord().contains("itemspnl")
                              || documentTemplateObject.getKeyWord().contains("totalmoneypnl") || documentTemplateObject.getKeyWord().contains("totalpricetaxpnl")
                              || documentTemplateObject.getKeyWord().contains("totaltaxpnl") || documentTemplateObject.getKeyWord().contains("grandtotalmoneywritepnl")
                              || documentTemplateObject.getKeyWord().contains("totalpricepnl") || documentTemplateObject.getKeyWord().contains("totaldiscountpnl")
                              || documentTemplateObject.getKeyWord().contains("exchangeratepnl") || documentTemplateObject.getKeyWord().contains("deliverypersonpnl")) {
                        withoutlabel = true;
                        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                                documentTemplateObject.setLabel(documentTemplate.isLabel());
                                break;
                            }
                        }
                    } else {
                        withoutlabel = false;
                    }
                } else if (selectedObject.getType().getId() == 96) {//Gider Pusulası
                    if (documentTemplateObject.getKeyWord().contains("receiptitempnl")) {
                        withoutlabel = true;
                        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                                documentTemplateObject.setLabel(documentTemplate.isLabel());
                                break;
                            }
                        }
                    } else {
                        withoutlabel = false;
                    }
                } else {
                    withoutlabel = false;
                }
                RequestContext.getCurrentInstance().update("form");
                RequestContext.getCurrentInstance().execute("toolbarIcons()");
            } else {
                // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Uyarı", "Bu Etiket Daha Önce Eklenmiştir"));
                // RequestContext.getCurrentInstance().update("duplicate");
            }

        } else {
            //System.out.println("*-*-*- ");
            documentTemplateObject.setKeyWord(id);
            documentTemplateObject.setLeft(Double.valueOf(left) * 0.25);
            documentTemplateObject.setTop(Double.valueOf(top) * 0.25);
            documentTemplateObject.setWidth(Double.valueOf(width) * 0.25);
            documentTemplateObject.setHeight(Double.valueOf(height) * 0.25);
            documentTemplateObject.setFontSize((int) (Math.round(Double.valueOf(fontsize))) * 72 / 96);
            documentTemplateObject.setFontAlign(textalign);
            documentTemplateObject.setFontStyle(Lists.newArrayList(fontstyle, fontweight));
            documentTemplateObject.setName(text);
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            OutputPanel droppable = (OutputPanel) root.findComponent("droppable");
            for (UIComponent component : droppable.getChildren()) {
                if (component.getId().equals(id)) {
                    ((Panel) component).setStyle("position:absolute;width: " + width + "px;height: " + height + "px;top:" + top + "px;left:" + left + "px;");
                    break;
                }
            }

            for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                if (documentTemplate.getKeyWord().equals(id)) {
                    documentTemplate.setWidth(documentTemplateObject.getWidth());
                    documentTemplate.setHeight(documentTemplateObject.getHeight());
                    documentTemplate.setTop(documentTemplateObject.getTop());
                    documentTemplate.setLeft(documentTemplateObject.getLeft());
                    documentTemplate.setFontSize(documentTemplateObject.getFontSize());
                    documentTemplate.setFontAlign(documentTemplateObject.getFontAlign());
                    break;
                }
            }

            System.out.println(documentTemplateObject.getKeyWord());
            if (documentTemplateObject.getKeyWord().contains("invoicenopnl") || documentTemplateObject.getKeyWord().contains("dispatchdatepnl")
                      || documentTemplateObject.getKeyWord().contains("duedatepnl") || documentTemplateObject.getKeyWord().contains("invoicedate")) {
                withoutlabel = true;
                for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                    if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                        documentTemplateObject.setLabel(documentTemplate.isLabel());
                        break;
                    }
                }
            } else if (selectedObject.getType().getId() == 62) {//Fatura ise
                if (documentTemplateObject.getKeyWord().contains("dispatchaddresspnl") || documentTemplateObject.getKeyWord().contains("branchnamepnl")
                          || documentTemplateObject.getKeyWord().contains("branchaddresspnl") || documentTemplateObject.getKeyWord().contains("branchmailpnl")
                          || documentTemplateObject.getKeyWord().contains("branchtaxofficepnl") || documentTemplateObject.getKeyWord().contains("branchtaxnumberpnl")
                          || documentTemplateObject.getKeyWord().contains("branchtelephonepnl")
                          || documentTemplateObject.getKeyWord().contains("customertitlepnl") || documentTemplateObject.getKeyWord().contains("customertaxofficenumberpnl")
                          || documentTemplateObject.getKeyWord().contains("customeraddresspnl") || documentTemplateObject.getKeyWord().contains("customerphonepnl")
                          || documentTemplateObject.getKeyWord().contains("customertaxnumberpnl") || documentTemplateObject.getKeyWord().contains("customertaxofficepnl")
                          || documentTemplateObject.getKeyWord().contains("customerbalancepnl") || documentTemplateObject.getKeyWord().contains("itemspnl")
                          || documentTemplateObject.getKeyWord().contains("totalmoneypnl") || documentTemplateObject.getKeyWord().contains("totalpricetaxpnl")
                          || documentTemplateObject.getKeyWord().contains("totaltaxpnl") || documentTemplateObject.getKeyWord().contains("grandtotalmoneywritepnl")
                          || documentTemplateObject.getKeyWord().contains("totalpricepnl") || documentTemplateObject.getKeyWord().contains("totaldiscountpnl")
                          || documentTemplateObject.getKeyWord().contains("exchangeratepnl") || documentTemplateObject.getKeyWord().contains("deliverypersonpnl")) {
                    withoutlabel = true;
                    for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                        if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                            documentTemplateObject.setLabel(documentTemplate.isLabel());
                            break;
                        }
                    }
                } else {
                    withoutlabel = false;
                }
            } else if (selectedObject.getType().getId() == 96) {//Gider Pusulası
                if (documentTemplateObject.getKeyWord().contains("receiptitempnl")) {
                    withoutlabel = true;
                    for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
                        if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                            documentTemplateObject.setLabel(documentTemplate.isLabel());
                            break;
                        }
                    }
                } else {
                    withoutlabel = false;
                }
            } else {
                withoutlabel = false;
            }
            RequestContext.getCurrentInstance().update("form");
            RequestContext.getCurrentInstance().execute("toolbarIcons()");

        }

    }

    public void addPanel(DocumentTemplateObject documentTemplateObject, int type_id) {
//RequestContext.getCurrentInstance().execute("$('.drgPanel').die('click'); $('.drgPanel').die('mousedown');");
        FacesContext fc = FacesContext.getCurrentInstance();
        ExpressionFactory ef = fc.getApplication().getExpressionFactory();
        //   System.out.println("add panel ");
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("droppable");

        Panel p = new Panel();
        //  System.out.println("documentTemplateObject.getKeyWord()=" + documentTemplateObject.getKeyWord() + " type_id = " + type_id);
        p.setId(documentTemplateObject.getKeyWord());
        p.setStyleClass("drgPanel");
        p.setStyle("z-index : 90 ;width: " + documentTemplateObject.getWidth() * 4 + "px;height: " + documentTemplateObject.getHeight() * 4 + "px; position:absolute;top:" + documentTemplateObject.getTop() * 4 + "px;left:" + documentTemplateObject.getLeft() * 4 + "px;");
        p.setWidgetVar(documentTemplateObject.getKeyWord());
        OutputLabel label = new OutputLabel();
        label.setId(p.getId() + "lb");
        if (documentTemplateObject.getKeyWord().contains("container")) {
            p.setStyle("z-index : 90 ;width: " + documentTemplateObject.getWidth() * 4 + "px;height: " + documentTemplateObject.getHeight() * 4 + "px; position:absolute;top:" + documentTemplateObject.getTop() * 4 + "px;left:" + documentTemplateObject.getLeft() * 4 + "px;");
            label.setValue("");

        } else {
            p.setStyle("z-index : 99 ;width: " + documentTemplateObject.getWidth() * 4 + "px;height: " + documentTemplateObject.getHeight() * 4 + "px; position:absolute;top:" + documentTemplateObject.getTop() * 4 + "px;left:" + documentTemplateObject.getLeft() * 4 + "px;");
            label.setValue(documentTemplateObject.getName());

        }
        String style = "";
        if (documentTemplateObject.getFontStyle().size() > 0) {

            for (String s : documentTemplateObject.getFontStyle()) {
                if (s.equals("italic")) {
                    style = style + "font-style:italic;";
                } else {
                    style = style + "font-weight:700 !important;";
                }
            }

        }
        if (type_id != 0) {
            //  System.out.println("*-*-*-* yeni");
            documentTemplateObject.setFontSize(11);
            documentTemplateObject.setFontAlign("left");
        }
        label.setStyle(style + "word-wrap:break-word;font-size:" + documentTemplateObject.getFontSize() + "pt;display:block;text-align:" + documentTemplateObject.getFontAlign() + ";");

        p.getChildren().add(label);
        // }
        Resizable resizable = new Resizable();
        resizable.setFor(p.getId());
        // resizable.setAnimate(true);
        resizable.setGhost(true);

        MethodExpression me = ef.createMethodExpression(fc.getELContext(), "#{documentTemplateProcessBean.onResize}", null, new Class<?>[]{BehaviorEvent.class});
        AjaxBehavior ajaxBehavior = (AjaxBehavior) fc.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
        ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me, me));
        ajaxBehavior.setOncomplete("toolbarIcons()");
        ajaxBehavior.setGlobal(false);
        resizable.addClientBehavior("resize", ajaxBehavior);

        Draggable draggable = new Draggable();
        draggable.setFor(p.getId());
        draggable.setContainment("parent");
        draggable.setRevert(false);
        draggable.setScope("documenttemplate");
        p.getChildren().add(draggable);
        p.getChildren().add(resizable);

        if (selectedObject.getType().getId() != 96) {
            CommandLink commandLink = new CommandLink();
            commandLink.setStyleClass("Fright icon-cancel-2 panelCloseBtn");
            commandLink.setOnclick("PF('" + p.getId() + "').close();remove([{name: 'keyword', value:'" + p.getId() + "'}]);");

            p.getChildren().add(commandLink);
        }

        //   System.out.println("p.getId() = " + p.getId());
        droppable.getChildren().add(p);

        RequestContext.getCurrentInstance().update("droppable");
        if (type_id == 1) {
            RequestContext.getCurrentInstance().execute("pnlSelect()");
        }
    }

    public void removePnl() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

        String id = params.get("keyword");
        // System.out.println("remove " + id);
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("droppable");

        //Datatable da resizable ve dragable alanları çalışması için droppablepanele ekleniyor.
        //Silme Yapılınca ise dragable ve resizable alanları da silinyor.
        if (id.contains("itemspnl")) {
            //2 For yazılmasının Nedeni panelden obje silince bir sonraki elemana geçiş yapılmıyor.
            for (UIComponent ui : droppable.getChildren()) {
                if (ui.getId().equals(id + "rz")) {//resizable
                    droppable.getChildren().remove(ui);
                }
            }
            for (UIComponent ui : droppable.getChildren()) {
                if (ui.getId().equals(id + "dr")) {//dragable
                    droppable.getChildren().remove(ui);

                }
            }
        }

        for (UIComponent component : droppable.getChildren()) {
            if (component.getId().equals(id)) {
                droppable.getChildren().remove(component);
                break;
            }
        }
        if (id.substring(0, Math.min(id.length(), 7)).contains("textpnl")) {
            renderEditText = true;
        } else {
            renderEditText = false;
        }
        RequestContext.getCurrentInstance().update("frmTextprop:textPropPnl");

        for (DocumentTemplateObject doc : printDocumentTemplate.getListOfObjects()) {
            if (doc.getKeyWord().equals(id)) {
                printDocumentTemplate.getListOfObjects().remove(doc);
                break;
            }
        }
        documentTemplateObject = new DocumentTemplateObject();
    }

    public void SaveEditText() {
        // System.out.println("*-*-*- " + documentTemplateObject.getKeyWord());
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputLabel label = (OutputLabel) root.findComponent(documentTemplateObject.getKeyWord() + "lb");
        label.setValue(documentTemplateObject.getName());
        // System.out.println(documentTemplateObject.getName());
        RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "lb').text('" + documentTemplateObject.getName() + "');");
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setName(documentTemplateObject.getName());
                break;
            }
        }

    }

    public void changeFontAlign() {
        //    System.out.println("align  " + documentTemplateObject.getKeyWord());
        if (documentTemplateObject.getKeyWord().contains("itemspnl")) {
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            DataTable table = (DataTable) root.findComponent("itemsdt");
            table.setTableStyle(table.getTableStyle() + "text-align:" + documentTemplateObject.getFontAlign() + ";");
            RequestContext.getCurrentInstance().execute("document.getElementById('itemsdt').getElementsByTagName('table')[0].style.setProperty('text-align', '" + documentTemplateObject.getFontAlign() + "', 'important')");
        } else {
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            OutputLabel label = (OutputLabel) root.findComponent(documentTemplateObject.getKeyWord() + "lb");
            label.setStyle(label.getStyle() + "text-align:" + documentTemplateObject.getFontAlign() + ";");
            RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "lb').css('text-align','" + documentTemplateObject.getFontAlign() + "');");

        }

        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setFontAlign(documentTemplateObject.getFontAlign());
                break;
            }
        }
    }

    public void changeFontSize() {
        if (documentTemplateObject.getKeyWord().contains("itemspnl")) {
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            DataTable table = (DataTable) root.findComponent("itemsdt");
            table.setTableStyle(table.getTableStyle() + "font-size:" + documentTemplateObject.getFontSize() + "pt !important;");
            RequestContext.getCurrentInstance().update(table.getClientId());
        } else {
            //  RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "lb').css('font-size','" + documentTemplateObject.getFontSize() + "pt');");
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            OutputLabel label = (OutputLabel) root.findComponent(documentTemplateObject.getKeyWord() + "lb");
            label.setStyle(label.getStyle() + "font-size:" + documentTemplateObject.getFontSize() + "pt;");
            RequestContext.getCurrentInstance().update(label.getClientId());

        }
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setFontSize(documentTemplateObject.getFontSize());
                break;
            }
        }
    }

    public void changeHeight() {
        RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "').css('height','" + documentTemplateObject.getHeight() * 4 + "px');");
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        Panel panel = (Panel) root.findComponent(documentTemplateObject.getKeyWord());
        panel.setStyle(panel.getStyle() + "height:" + documentTemplateObject.getHeight() * 4 + "px;");
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setHeight(documentTemplateObject.getHeight());
                break;
            }
        }
    }

    public void changeWidth() {
        RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "').css('width','" + documentTemplateObject.getWidth() * 4 + "px');");
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        Panel panel = (Panel) root.findComponent(documentTemplateObject.getKeyWord());
        panel.setStyle(panel.getStyle() + "width:" + documentTemplateObject.getWidth() * 4 + "px;");
        //RequestContext.getCurrentInstance().update(panel.getClientId());
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setWidth(documentTemplateObject.getWidth());
                break;
            }
        }
    }

    public void changeTop() {
        RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "').css('top','" + documentTemplateObject.getTop() * 4 + "px');");
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        Panel panel = (Panel) root.findComponent(documentTemplateObject.getKeyWord());
        panel.setStyle(panel.getStyle() + "top:" + documentTemplateObject.getTop() * 4 + "px;");
        //  RequestContext.getCurrentInstance().update(panel.getClientId());
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setTop(documentTemplateObject.getTop());
                break;
            }
        }
    }

    public void changeLeft() {
        RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "').css('left','" + documentTemplateObject.getLeft() * 4 + "px');");
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        Panel panel = (Panel) root.findComponent(documentTemplateObject.getKeyWord());
        panel.setStyle(panel.getStyle() + "left:" + documentTemplateObject.getLeft() * 4 + "px;");
        // RequestContext.getCurrentInstance().update(panel.getClientId());
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setLeft(documentTemplateObject.getLeft());
                break;
            }
        }
    }

    public void changeLabel() {
        System.out.println(documentTemplateObject.getKeyWord());
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setLabel(documentTemplateObject.isLabel());
                break;
            }
        }
    }

    public void changeFontStyle() {
        //  System.out.println("font style " + documentTemplateObject.getFontStyle());

        if (documentTemplateObject.getKeyWord().contains("itemspnl")) {
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            DataTable table = (DataTable) root.findComponent("itemsdt");
            table.setTableStyle(table.getTableStyle() + "font-weight:400;font-style:normal;");
            RequestContext.getCurrentInstance().update(table.getClientId());
        } else {
            RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "lb').css('font-weight','');");
            RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "lb').css('font-style','');");
        }
        for (String s : documentTemplateObject.getFontStyle()) {
            if (s.equals("italic")) {
                if (documentTemplateObject.getKeyWord().contains("itemspnl")) {
                    UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
                    DataTable table = (DataTable) root.findComponent("itemsdt");
                    table.setTableStyle(table.getTableStyle() + "font-style:italic;");
                    RequestContext.getCurrentInstance().execute("document.getElementById('itemsdt').getElementsByTagName('table')[0].style.setProperty('font-style', 'italic', 'important')");
                } else {
                    UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
                    OutputLabel label = (OutputLabel) root.findComponent(documentTemplateObject.getKeyWord() + "lb");
                    label.setStyle(label.getStyle() + "font-style:italic;");
                    RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "lb').css('font-style','italic');");
                }
            } else if (documentTemplateObject.getKeyWord().contains("itemspnl")) {
                UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
                DataTable table = (DataTable) root.findComponent("itemsdt");
                table.setTableStyle(table.getTableStyle() + "font-weight:bold;");
                RequestContext.getCurrentInstance().execute("$('#itemsdt table label').css('cssText','font-weight: 700 !important');");
            } else {
                UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
                OutputLabel label = (OutputLabel) root.findComponent(documentTemplateObject.getKeyWord() + "lb");
                label.setStyle(label.getStyle() + "font-weight:700 !important;");
                RequestContext.getCurrentInstance().execute("document.getElementById('" + documentTemplateObject.getKeyWord() + "lb').style.setProperty('font-weight','700','important')");
            }
        }
        RequestContext.getCurrentInstance().execute("$('#" + documentTemplateObject.getKeyWord() + "').css('left','" + documentTemplateObject.getLeft() * 4 + "px');");
        for (DocumentTemplateObject documentTemplate : printDocumentTemplate.getListOfObjects()) {
            if (documentTemplate.getKeyWord().equals(documentTemplateObject.getKeyWord())) {
                documentTemplate.setFontStyle(documentTemplateObject.getFontStyle());
                break;
            }
        }
    }

    public void changePaperSize(Boolean isVertical) {
        selectedObject.setIsVertical(isVertical);

        if (selectedObject.getPaperSize() == 1) {
            if (isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(210));
                selectedObject.setHeight(BigDecimal.valueOf(297));
            } else if (!isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(297));
                selectedObject.setHeight(BigDecimal.valueOf(210));
            }
        } else if (selectedObject.getPaperSize() == 2) {
            if (isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(148));
                selectedObject.setHeight(BigDecimal.valueOf(210));
            } else if (!isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(210));
                selectedObject.setHeight(BigDecimal.valueOf(148));
            }
        }
    }

    public void save() {
        int result = 0;
        //   RequestContext.getCurrentInstance().update("droppable");

        result = documentTemplateService.update(selectedObject);

        if (result == -1) {//başka default varsa
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("defaulttemplatealreadyexists")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            sessionBean.createUpdateMessage(result);
            /*RequestContext.getCurrentInstance().update("pagePanel");
            RequestContext.getCurrentInstance().execute("rulers();"
                    + "$('#pagePanel').find('.stage').css('cssText','width: " + (selectedObject.getWidth().intValue() * 4) + "px !important; height: " + (selectedObject.getHeight().intValue() * 4) + "px !important;');"
                    + "PF('dlg_DocumentTemplateProcess').hide();");*/
            RequestContext.getCurrentInstance().execute("PF('dlg_DocumentTemplateProcess').hide();");
            //   RequestContext.getCurrentInstance().execute("$('#pagePanel').css('background-image','url("+ftpConnectionBean.getPath()+")')");
        }
    }

    public void jsonSave() {
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("droppable");

        droppable.getChildren().clear();
        RequestContext.getCurrentInstance().update("droppable");
        String json = new Gson().toJson(printDocumentTemplate);
        System.out.println("*-*-*-*-* " + json);
        selectedObject.setJson(json);
        sessionBean.createUpdateMessage(
                  documentTemplateService.update(selectedObject));
        marwiz.goToPage("/pages/general/documenttemplate/documenttemplate.xhtml", null, 1, 62);
    }

    public void loadJson(String json) {
        boolean b = false;
        Gson gson = new Gson();
        printDocumentTemplate = gson.fromJson(json, new TypeToken<PrintDocumentTemplate>() {
        }.getType());
        // System.out.println("*-*-*-* " + printDocumentTemplate.getListOfObjects().size());
        if (printDocumentTemplate != null) {
            List<String> keywords = new ArrayList<>();
            List<DocumentTemplateObject> removableList = new ArrayList<>();

            //   System.out.println(printDocumentTemplate.getListOfObjects().size());
            for (DocumentTemplateObject dto : printDocumentTemplate.getListOfObjects()) {
                if (keywords.contains(dto.getKeyWord())) {
                    removableList.add(dto);
                    System.out.println("Dublicated");
                } else {
                    //      System.out.println("*-*--*-" + dto.getKeyWord());
                    keywords.add(dto.getKeyWord());
                    if (dto.getKeyWord().contains("itemspnl")) {
                        b = true;

                        addItems(dto, printDocumentTemplate.getItems(), 0);
                    } else {
                        addPanel(dto, 0);
                    }
                }
            }

            printDocumentTemplate.getListOfObjects().removeAll(removableList);
        } else {
            printDocumentTemplate = new PrintDocumentTemplate();
        }
        if (!b) {
            try {
                if (selectedObject.getType().getId() == 62) { // fatura ise
                    printDocumentTemplate.setItems(Lists.newArrayList(new DataTableColumn("stockname"), new DataTableColumn("stockcode"), new DataTableColumn("stockbarcode"), new DataTableColumn("description"),
                              new DataTableColumn("quantity"), new DataTableColumn("unitprice"), new DataTableColumn("discountrate"), new DataTableColumn("taxrate"), new DataTableColumn("taxprice"), new DataTableColumn("totalprice"), new DataTableColumn("totalmoney")));
                } else if (selectedObject.getType().getId() == 63) { // irsaliye ise
                    printDocumentTemplate.setItems(Lists.newArrayList(new DataTableColumn("stockname"), new DataTableColumn("stockcode"), new DataTableColumn("description"),
                              new DataTableColumn("quantity"), new DataTableColumn("unitprice"), new DataTableColumn("discount"), new DataTableColumn("totaltax"), new DataTableColumn("totalmoney")));

                } else if (selectedObject.getType().getId() == 70 || selectedObject.getType().getId() == 71) {
                    printDocumentTemplate.setItems(Lists.newArrayList(new DataTableColumn("payer"), new DataTableColumn("bankname"), new DataTableColumn("branchname"),
                              new DataTableColumn("chequenumber"), new DataTableColumn("accountnumber"), new DataTableColumn("duedate"), new DataTableColumn("chequeprice"), new DataTableColumn("totalmoney")));

                }
            } catch (Exception e) {
                System.out.println("*----eee---" + e);
                // printDocumentTemplate.setListOfObjects(new ArrayList<>());
            }

        }

    }

    public void onToggle(ToggleEvent e) {
        //int count = 0;
        toggleList.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
        // System.out.println("*-*-*-*- " + e.getComponent().getClass().getSimpleName());
        for (int i = 0; i < toggleList.size(); i++) {
            printDocumentTemplate.getItems().get(i).setVisibility(toggleList.get(i));
            UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
            Column c = (Column) root.findComponent("itemsdt:" + printDocumentTemplate.getItems().get(i).getId());
            c.setVisible(toggleList.get(i));
        }

    }

    public void addItems(DocumentTemplateObject documentTemplateObject, List<DataTableColumn> items, int type_id) {
        list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            list.add(false);
        }
        if (type_id == 1) {
            for (DataTableColumn column : items) {
                column.setWidth((int) documentTemplateObject.getWidth() * 2 / items.size());
            }
        }
        System.out.println("add panel: " + documentTemplateObject.getKeyWord());
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("droppable");

        Panel p = new Panel();
        System.out.println("documentTemplateObject.getKeyWord()= " + documentTemplateObject.getKeyWord());
        p.setId(documentTemplateObject.getKeyWord());
        p.setStyleClass("drgPanel");
        //    p.setStyle("height: 500px;width: 640px;");
        p.setStyle("height: " + documentTemplateObject.getHeight() * 4 + "px;width: " + documentTemplateObject.getWidth() * 4 + "px; position:absolute;top:" + documentTemplateObject.getTop() * 4 + "px;left:" + documentTemplateObject.getLeft() * 4 + "px;");
        p.setWidgetVar(documentTemplateObject.getKeyWord());

        DataTable dt = new DataTable();
        dt.setStyle("height:  " + documentTemplateObject.getHeight() * 4 + "px;text-align:left");
        dt.setStyleClass("stockColumns");
        dt.setVar("stocks");
        dt.setId("itemsdt");
        dt.setResizableColumns(true);
        dt.setLiveResize(true);
        if (type_id != 0) {
            documentTemplateObject.setFontSize(9);
            documentTemplateObject.setFontAlign("left");
        }
        dt.setTableStyle("font-size:" + documentTemplateObject.getFontSize() + "pt !important;text-align:" + documentTemplateObject.getFontAlign() + ";");

        //  dt.set
        //dt.setDraggableColumns(true);
        //dt.setHeader(dt);
        FacesContext fc = FacesContext.getCurrentInstance();
        ExpressionFactory ef = fc.getApplication().getExpressionFactory();

        MethodExpression me = ef.createMethodExpression(fc.getELContext(), "#{documentTemplateProcessBean.onResizeDataTable}", null, new Class<?>[]{BehaviorEvent.class});
        AjaxBehavior ajaxBehavior = (AjaxBehavior) fc.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
        ajaxBehavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me, me));
        ajaxBehavior.setGlobal(false);
        dt.addClientBehavior("colResize", ajaxBehavior);

        dt.setValueExpression("value", ef.createValueExpression(fc.getELContext(), "#{documentTemplateProcessBean.list}", ArrayList.class));
        OutputPanel op = new OutputPanel();

        CommandLink cl = new CommandLink();
        cl.setId("toggler");
        cl.setStyleClass("icon-table stocksVisibleBtn");
        op.getChildren().add(cl);

        ColumnToggler ct = new ColumnToggler();
        ct.setDatasource("itemsdt");
        ct.setTrigger("toggler");
        MethodExpression me1 = ef.createMethodExpression(fc.getELContext(), "#{documentTemplateProcessBean.onToggle}", null, new Class<?>[]{BehaviorEvent.class});
        AjaxBehavior ajaxBehavior1 = (AjaxBehavior) fc.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
        ajaxBehavior1.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me1, me1));
        ajaxBehavior1.setGlobal(false);
        ct.addClientBehavior("toggle", ajaxBehavior1);
        op.getChildren().add(ct);

        dt.getFacets().put("header", op);

        String column1Name = "", column2Name = "", column3Name = "", column4Name = "", column5Name = "", column6Name = "", column7Name = "", column8Name = "", column9Name = "", column10Name = "", column11Name = "";
        String column6Exp = "", column7Exp = "", column9Exp = "", column8Exp = "";

        if (selectedObject.getType().getId() == 62) { // sadece fatura için kdv oranı , barcode , net tutar alanları da gösterilecek
            column1Name = sessionBean.loc.getString("stockname");
            column2Name = sessionBean.loc.getString("stockcode");
            column3Name = sessionBean.loc.getString("stockbarcode");
            column4Name = sessionBean.loc.getString("description");
            column5Name = sessionBean.loc.getString("quantity");
            column6Name = sessionBean.loc.getString("unitprice");
            column7Name = sessionBean.loc.getString("discountrate");
            column7Exp = "5%";
            column8Name = sessionBean.loc.getString("taxrate");
            column9Name = sessionBean.loc.getString("taxprice");
            column9Exp = "18%";
            column10Name = sessionBean.loc.getString("taxfreeamount");
            column11Name = sessionBean.loc.getString("totalprice");

        } else if (selectedObject.getType().getId() == 63) {
            column1Name = sessionBean.loc.getString("stockname");
            column2Name = sessionBean.loc.getString("stockcode");
            column3Name = sessionBean.loc.getString("description");
            column4Name = sessionBean.loc.getString("quantity");
            column5Name = sessionBean.loc.getString("unitprice");
            column6Name = sessionBean.loc.getString("discount");
            column6Exp = "5%";
            column7Name = sessionBean.loc.getString("totaltax");
            column7Exp = "18%";
            column8Name = sessionBean.loc.getString("totalmoney");

        } else if (selectedObject.getType().getId() == 70 || selectedObject.getType().getId() == 71) {
            column1Name = sessionBean.loc.getString("payer");
            column2Name = sessionBean.loc.getString("bankname");
            column3Name = sessionBean.loc.getString("branchname");
            column4Name = sessionBean.loc.getString("chequenumber");
            column5Name = sessionBean.loc.getString("accountnumber");
            column6Name = sessionBean.loc.getString("duedate");
            column6Exp = "11.11.2019";
            column7Name = sessionBean.loc.getString("chequeprice");
            column8Name = sessionBean.loc.getString("totalmoney");

        }
        //  System.out.println("*-*-*-* " + items.get(0).getWidth());
        if (selectedObject.getType().getId() == 62) {
            Column column1 = new Column();
            //  column1.setWidth(items.get(0).getWidth() + "");
            column1.setId(items.get(0).getId());
            column1.setVisible(items.get(0).isVisibility());
            column1.setWidth("" + items.get(0).getWidth());
            column1.setHeaderText(column1Name);
            OutputLabel label1 = new OutputLabel();
            label1.setValue(column1Name);
            column1.getChildren().add(label1);

            Column column2 = new Column();
            column2.setHeaderText(column2Name);
            column2.setId(items.get(1).getId());
            column2.setVisible(items.get(1).isVisibility());
            column2.setWidth("" + items.get(1).getWidth());
            OutputLabel label2 = new OutputLabel();
            label2.setValue(column2Name);
            column2.getChildren().add(label2);

            Column column3 = new Column();
            column3.setId(items.get(2).getId());
            column3.setVisible(items.get(2).isVisibility());
            column3.setWidth("" + items.get(2).getWidth());
            column3.setHeaderText(column3Name);
            OutputLabel label3 = new OutputLabel();
            label3.setValue(column3Name);
            column3.getChildren().add(label3);

            Column column4 = new Column();
            column4.setId(items.get(3).getId());
            column4.setVisible(items.get(3).isVisibility());
            column4.setWidth("" + items.get(3).getWidth());
            column4.setHeaderText(column4Name);
            OutputLabel label4 = new OutputLabel();
            label4.setValue("10");
            column4.getChildren().add(label4);

            Column column5 = new Column();
            column5.setId(items.get(4).getId());
            column5.setVisible(items.get(4).isVisibility());
            column5.setWidth("" + items.get(4).getWidth());
            column5.setHeaderText(column5Name);
            OutputLabel label5 = new OutputLabel();
            label5.setValue("5");
            column5.getChildren().add(label5);

            Column column6 = new Column();
            column6.setId(items.get(5).getId());
            column6.setVisible(items.get(5).isVisibility());
            column6.setWidth("" + items.get(5).getWidth());
            column6.setHeaderText(column6Name);
            OutputLabel label6 = new OutputLabel();
            label6.setValue(column6Exp.equals("") ? "5 TL" : column6Exp);
            column6.getChildren().add(label6);

            Column column7 = new Column();
            column7.setId(items.get(6).getId());
            column7.setVisible(items.get(6).isVisibility());
            column7.setWidth("" + items.get(6).getWidth());
            column7.setHeaderText(column7Name);
            OutputLabel label7 = new OutputLabel();
            label7.setValue("5%");
            column7.getChildren().add(label7);

            Column column8 = new Column();
            column8.setId(items.get(7).getId());
            column8.setVisible(items.get(7).isVisibility());
            column8.setWidth("" + items.get(7).getWidth());
            column8.setHeaderText(column8Name);
            OutputLabel label8 = new OutputLabel();
            label8.setValue(column9Exp.equals("") ? "50 TL" : column9Exp);
            column8.getChildren().add(label8);

            dt.getChildren().add(column1);
            dt.getChildren().add(column2);
            dt.getChildren().add(column3);
            dt.getChildren().add(column4);
            dt.getChildren().add(column5);
            dt.getChildren().add(column6);
            dt.getChildren().add(column7);
            dt.getChildren().add(column8);

            try { // faturada açılan yeni alanlar olduğu için try catch bloğu içerisine alnındı.
                Column column9 = new Column();
                column9.setId(items.get(8).getId());
                column9.setVisible(items.get(8).isVisibility());
                column9.setWidth("" + items.get(8).getWidth());
                column9.setHeaderText(column9Name);
                OutputLabel label9 = new OutputLabel();
                label9.setValue("50 TL");
                column9.getChildren().add(label9);

                Column column10 = new Column();
                column10.setId(items.get(9).getId());
                column10.setVisible(items.get(9).isVisibility());
                column10.setWidth("" + items.get(9).getWidth());
                column10.setHeaderText(column10Name);
                OutputLabel label10 = new OutputLabel();
                label10.setValue("50 TL");
                column10.getChildren().add(label10);

                Column column11 = new Column();
                column11.setId(items.get(10).getId());
                column11.setVisible(items.get(10).isVisibility());
                column11.setWidth("" + items.get(10).getWidth());
                column11.setHeaderText(column11Name);
                OutputLabel label11 = new OutputLabel();
                label11.setValue("50 TL");
                column11.getChildren().add(label11);

                dt.getChildren().add(column9);
                dt.getChildren().add(column10);
                dt.getChildren().add(column11);
            } catch (Exception e) {
                //  System.out.println("---excep---" + e);
            }

        } else { // irsaliye ve diğer tipler için burası çalışacak
            Column column1 = new Column();
            //  column1.setWidth(items.get(0).getWidth() + "");
            column1.setId(items.get(0).getId());
            column1.setVisible(items.get(0).isVisibility());
            column1.setWidth("" + items.get(0).getWidth());
            column1.setHeaderText(column1Name);
            OutputLabel label1 = new OutputLabel();
            label1.setValue(column1Name);
            column1.getChildren().add(label1);

            Column column2 = new Column();
            column2.setHeaderText(column2Name);
            column2.setId(items.get(1).getId());
            column2.setVisible(items.get(1).isVisibility());
            column2.setWidth("" + items.get(1).getWidth());
            OutputLabel label2 = new OutputLabel();
            label2.setValue(column2Name);
            column2.getChildren().add(label2);

            Column column3 = new Column();
            column3.setId(items.get(2).getId());
            column3.setVisible(items.get(2).isVisibility());
            column3.setWidth("" + items.get(2).getWidth());
            column3.setHeaderText(column3Name);
            OutputLabel label3 = new OutputLabel();
            label3.setValue(column3Name);
            column3.getChildren().add(label3);

            Column column4 = new Column();
            column4.setId(items.get(3).getId());
            column4.setVisible(items.get(3).isVisibility());
            column4.setWidth("" + items.get(3).getWidth());
            column4.setHeaderText(column4Name);
            OutputLabel label4 = new OutputLabel();
            label4.setValue("10");
            column4.getChildren().add(label4);

            Column column5 = new Column();
            column5.setId(items.get(4).getId());
            column5.setVisible(items.get(4).isVisibility());
            column5.setWidth("" + items.get(4).getWidth());
            column5.setHeaderText(column5Name);
            OutputLabel label5 = new OutputLabel();
            label5.setValue("5");
            column5.getChildren().add(label5);

            Column column6 = new Column();
            column6.setId(items.get(5).getId());
            column6.setVisible(items.get(5).isVisibility());
            column6.setWidth("" + items.get(5).getWidth());
            column6.setHeaderText(column6Name);
            OutputLabel label6 = new OutputLabel();
            label6.setValue(column6Exp);
            column6.getChildren().add(label6);

            Column column7 = new Column();
            column7.setId(items.get(6).getId());
            column7.setVisible(items.get(6).isVisibility());
            column7.setWidth("" + items.get(6).getWidth());
            column7.setHeaderText(column7Name);
            OutputLabel label7 = new OutputLabel();
            label7.setValue(column7Exp);
            column7.getChildren().add(label7);

            Column column8 = new Column();
            column8.setId(items.get(7).getId());
            column8.setVisible(items.get(7).isVisibility());
            column8.setWidth("" + items.get(7).getWidth());
            column8.setHeaderText(column8Name);
            OutputLabel label8 = new OutputLabel();
            label8.setValue("50 TL");
            column8.getChildren().add(label8);

            dt.getChildren().add(column1);
            dt.getChildren().add(column2);
            dt.getChildren().add(column3);
            dt.getChildren().add(column4);
            dt.getChildren().add(column5);
            dt.getChildren().add(column6);
            dt.getChildren().add(column7);
            dt.getChildren().add(column8);
        }

        p.getChildren().add(dt);

        Draggable d = new Draggable();
        d.setId(p.getId() + "dr");
        d.setFor(documentTemplateObject.getKeyWord());
        d.setContainment("parent");
        d.setScope("documenttemplate");

        Resizable r = new Resizable();
        r.setId(p.getId() + "rz");
        r.setFor(documentTemplateObject.getKeyWord());
        r.setOnStop("$('#itemsdt').height($('#" + documentTemplateObject.getKeyWord() + "').height());");
        r.setGhost(true);

        MethodExpression me2 = ef.createMethodExpression(fc.getELContext(), "#{documentTemplateProcessBean.onResize}", null, new Class<?>[]{BehaviorEvent.class});
        AjaxBehavior ajaxBehavior2 = (AjaxBehavior) fc.getApplication().createBehavior(AjaxBehavior.BEHAVIOR_ID);
        ajaxBehavior2.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(me2, me2));
        ajaxBehavior2.setOncomplete("toolbarIcons()");
        ajaxBehavior2.setGlobal(false);
        r.addClientBehavior("resize", ajaxBehavior2);

        if (selectedObject.getType().getId() != 96) {
            CommandLink commandLink = new CommandLink();
            commandLink.setStyleClass("Fright icon-cancel-2 panelCloseBtn");
            commandLink.setOnclick("PF('" + documentTemplateObject.getKeyWord() + "').close();remove([{name: 'keyword', value:'" + documentTemplateObject.getKeyWord() + "'}]);");

            p.getChildren().add(commandLink);
        }

        boolean isThere = false;
        DataTable dTable = (DataTable) root.findComponent("itemsdt");
        if (dTable != null) {
            for (UIComponent uIComponent : p.getChildren()) {
                if (uIComponent.getId().equals(dTable.getId())) {
                    isThere = true;
                    break;
                }
            }
        }
        if (!isThere) {
            droppable.getChildren().add(p);
            droppable.getChildren().add(r);
            droppable.getChildren().add(d);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("youcanputonlyonetable")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

        RequestContext.getCurrentInstance().update("droppable");
        if (type_id == 1) {
            RequestContext.getCurrentInstance().execute("pnlSelect()");
        }

    }

//    public void onReorder(AjaxBehaviorEvent event) {
//        DataTable dataTable = (DataTable) event.getComponent();
//        List<UIColumn> printDocumentTemplate.getItems() = dataTable.getColumns();
//
////     
//    }
    private String yaziyaCevir(BigDecimal tutar) {

        String yazi = "Yalnız ";

        System.out.println("tutar.toString()" + tutar.toString());
        tutar = tutar.setScale(2, RoundingMode.CEILING);
        System.out.println("tutar.toString()" + tutar.toString());
        String[] alanlar = tutar.toString().split("\\.");

        Integer tamKisim = 0;
        Integer ondalik = 0;
        tamKisim = Integer.parseInt(alanlar[0]); // tam kısmını aldım

        try {
            ondalik = Integer.parseInt(alanlar[1].substring(0, 2)); // ondalık kısmın ilk2 2 hanesini aldım
        } catch (Exception e) {
        }

        System.out.println("tamKisim--------" + tamKisim);
        System.out.println("ondalik--------" + ondalik);

        String[] birlik = {"", "Bir", "Iki", "Üç", "Dört", "Bes", "Alti", "Yedi", "Sekiz", "Dokuz"};
        String[] Onluk = {"", "On", "Yirmi", "Otuz", "Kirk", "Elli", "Altmis", "Yetmis", "Seksen", "Doksan"};
        String[] Yuzluk = {"", "Yüz", "Ikiyüz", "Üçyüz", "Dörtyüz", "Besyüz", "Altiyüz", "Yediyüz", "Sekizyüz", "Dokuzyüz"};

        String tamKisimYazi = tamKisim.toString();
        System.out.println("tamKisimYazi--------" + tamKisimYazi);
        // 12 hane yaptık
        while (tamKisimYazi.length() < 15) {
            tamKisimYazi = "0" + tamKisimYazi;
        }
        String trilyonlar = tamKisimYazi.substring(0, 3);
        System.out.println("trilyonlar--------" + trilyonlar);
        if (Integer.parseInt(trilyonlar) > 0) {
            // trilyonlar hanesi var..
            yazi = yazi + Yuzluk[Integer.parseInt(trilyonlar.substring(0, 1))].toString();
            yazi = yazi + Onluk[Integer.parseInt(trilyonlar.substring(1, 2))].toString();
            yazi = yazi + birlik[Integer.parseInt(trilyonlar.substring(2, 3))].toString();
            yazi = yazi + "trilyon";
        }
        System.out.println("yazi--------" + yazi);
        String milyarlar = tamKisimYazi.substring(3, 6);
        System.out.println("milyarlar--------" + milyarlar);
        if (Integer.parseInt(milyarlar) > 0) {
            // milyar hanesi var..

            yazi = yazi + Yuzluk[Integer.parseInt(milyarlar.substring(0, 1))].toString();
            yazi = yazi + Onluk[Integer.parseInt(milyarlar.substring(1, 2))].toString();
            yazi = yazi + birlik[Integer.parseInt(milyarlar.substring(2, 3))].toString();
            yazi = yazi + "milyar";
        }

        String milyonlar = tamKisimYazi.substring(6, 9);
        System.out.println("milyonlar--------" + milyonlar);
        if (Integer.parseInt(milyonlar) > 0) {
            // milyonlar hanesi var..
            yazi = yazi + Yuzluk[Integer.parseInt(milyonlar.substring(0, 1))].toString();
            yazi = yazi + Onluk[Integer.parseInt(milyonlar.substring(1, 2))].toString();
            yazi = yazi + birlik[Integer.parseInt(milyonlar.substring(2, 3))].toString();
            yazi = yazi + "milyon";
        }
        String binler = tamKisimYazi.substring(9, 12);
        System.out.println("binler--------" + binler);
        if (Integer.parseInt(binler) > 0) {
            // binler hanesi var..
            if (Integer.parseInt(binler) > 1) {
                // 1 den büüyk değil 1 e eşit ise sadece bin yazacağı için burası atlandı
                yazi = yazi + Yuzluk[Integer.parseInt(binler.substring(0, 1))].toString();
                yazi = yazi + Onluk[Integer.parseInt(binler.substring(1, 2))].toString();
                yazi = yazi + birlik[Integer.parseInt(binler.substring(2, 3))].toString();
            }
            yazi = yazi + "bin";
        }
        String birler = tamKisimYazi.substring(12, 15);
        System.out.println("birler--------" + birler);
        if (Integer.parseInt(birler) > 0) {
            // birler hanesi var..
            yazi = yazi + Yuzluk[Integer.parseInt(birler.substring(0, 1))].toString();
            yazi = yazi + Onluk[Integer.parseInt(birler.substring(1, 2))].toString();
            yazi = yazi + birlik[Integer.parseInt(birler.substring(2, 3))].toString();
        }

        yazi = yazi + " TL ";

        // ondalık işlemleri
        if (ondalik > 0) {
            // odalık hanesi var..
            yazi = yazi + Onluk[Integer.parseInt(ondalik.toString().substring(0, 1))].toString();
            yazi = yazi + birlik[Integer.parseInt(ondalik.toString().substring(1, 2))].toString();
            yazi = yazi + " Kuruş";
        }

        return yazi;
    }

    public void onResizeDataTable(ColumnResizeEvent event) {
        for (DataTableColumn column : printDocumentTemplate.getItems()) {
            if (("itemsdt:" + column.getId()).equals(event.getColumn().getClientId())) {
                column.setWidth(event.getWidth());
                /*   UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
                Column c = (Column) root.findComponent(event.getColumn().getClientId());
                c.setWidth(event.getWidth() + ""); *///setTableStyle(table.getTableStyle() + "font-weight:bold;");
                //RequestContext.getCurrentInstance().update(event.getColumn().getClientId());
                //  break;
            }
        }

    }

    public void preview() {
        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        OutputPanel droppable = (OutputPanel) root.findComponent("printPanel");
        droppable.getChildren().clear();
        PrintDocumentTemplate documentTemplate = new PrintDocumentTemplate();
        documentTemplate = printDocumentTemplate;
        for (DocumentTemplateObject dto : documentTemplate.getListOfObjects()) {
            if (dto.getKeyWord().contains("container")) {
                System.out.println("------  " + dto.getKeyWord());

                OutputPanel op = new OutputPanel();
                op.setStyle("border: 1px solid black ;width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                // op.setId(dto.getKeyWord());

                droppable.getChildren().add(op);
            } else if (dto.getKeyWord().contains("imagepnl")) {
                GraphicImage gr = new GraphicImage();
                gr.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                gr.setUrl("../upload/template/" + selectedObject.getId() + "_" + dto.getKeyWord() + "." + "png");
                gr.setCache(false);
                droppable.getChildren().add(gr);
                RequestContext.getCurrentInstance().update("printPanel");
            } else if (dto.getKeyWord().contains("itemspnl")) {
                OutputPanel op = new OutputPanel();
                op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");
                op.setId(dto.getKeyWord() + "Prev");

                droppable.getChildren().add(op);
                RequestContext.getCurrentInstance().update("printPanel");
                String direction = selectedObject.isIsVertical() == true ? "horizontal" : "landscape";
                String imgUrl = "";
                if (selectedObject.isIsUseTemplate()) {
                    imgUrl = ftpConnectionBean.getPath();
                }

                StringBuilder sb = new StringBuilder();
                sb.append(
                          " <style>"
                          + "        #" + dto.getKeyWord() + "Prev table {"
                          + "            font-family: arial, sans-serif;"
                          + "            border-collapse: collapse;"
                          + "            width: 100%;"
                          + "       table-layout: fixed;"
                          + "        }"
                          + "        #" + dto.getKeyWord() + "Prev table tr td, #" + dto.getKeyWord() + "Prev table tr th {"
                          + "            border: 1px solid #dddddd;"
                          + "            text-align: " + dto.getFontAlign() + ";"
                          + "            padding: 3px;"
                          + "            word-wrap: break-word;"
                          + "            height: 20px;"
                          + "            font-size: " + dto.getFontSize() + "pt;"
                          + "        }"
                          + "   @page { size: " + direction + "; }"
                          + "   @media print {"
                          + "     html, body {"
                          + "    width: " + selectedObject.getWidth() + "mm;"
                          + "    height: " + selectedObject.getHeight() + "mm;"
                          + "     }}"
                          + "     #printPanel{"
                          + "       background-image : url(" + imgUrl + ");"
                          + "       background-repeat : no-repeat;"
                          + "       background-size : contain;"
                          + "       background-position-x : 20px;"
                          + "       background-position-y : 10px;"
                          + "      }"
                          + "    </style> ");
                sb.append("<table><colgroup> ");
                List<Integer> widthList = new ArrayList<>();
                int id = 0;
                int countWidths = 0;
                int j = 0;
                for (DataTableColumn dtc : documentTemplate.getItems()) {
                    if (dtc.isVisibility()) {
                        int width = (int) (dtc.getWidth() * 100 / (dto.getWidth() * 3));
                        widthList.add(id, width);
                        countWidths = countWidths + width;
                        id++;
                        if (width == 0) {
                            j++;
                        }
                    }
                }
                for (Integer i : widthList) {
                    if (i == 0) {
                        i = (100 - countWidths) / j;
                    }
                    System.out.println("i " + i);
                    sb.append("<col style=\"width:" + i + "%\" />");
                }
                sb.append("</colgroup>");

                sb.append("<tr>");
                for (DataTableColumn dtc : documentTemplate.getItems()) {
                    if (dtc.isVisibility()) {
                        if (selectedObject.isIsUseTemplate()) {
                            sb.append("<th> ").append("").append("</th>");
                        } else {
                            sb.append("<th> ").append(sessionBean.getLoc().getString(dtc.getId())).append("</th>");
                        }

                    }
                }
                sb.append("</tr>");

                int rowCount = (int) (dto.getHeight() / (33 * 0.25));
                System.out.println("--------- " + rowCount);
                for (int i = 0; i < rowCount - 1; i++) {

                    sb.append("<tr>");

                    if (documentTemplate.getItems().get(0).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }
                    if (documentTemplate.getItems().get(1).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }
                    if (documentTemplate.getItems().get(2).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }
                    if (documentTemplate.getItems().get(3).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }
                    if (documentTemplate.getItems().get(4).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }
                    if (documentTemplate.getItems().get(5).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }
                    if (documentTemplate.getItems().get(6).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }
                    if (documentTemplate.getItems().get(7).isVisibility()) {
                        sb.append("<td>deneme</td>");
                    }

                    if (selectedObject.getType().getId() == 62) {
                        if (documentTemplate.getItems().get(8).isVisibility()) {
                            sb.append("<td>deneme</td>");
                        }
                        if (documentTemplate.getItems().get(9).isVisibility()) {
                            sb.append("<td>deneme</td>");
                        }
                        if (documentTemplate.getItems().get(10).isVisibility()) {
                            sb.append("<td>deneme</td>");
                        }
                    }
                    sb.append("</tr>");
                }

                sb.append("</table>");

                RequestContext.getCurrentInstance().execute("$('#" + dto.getKeyWord() + "Prev').append('" + sb + "')");
            } else {
                OutputPanel op = new OutputPanel();
                op.setStyle("width: " + dto.getWidth() * 4 + "px;height: " + dto.getHeight() * 4 + "px; position:absolute;top:" + dto.getTop() * 4 + "px;left:" + dto.getLeft() * 4 + "px;");

                OutputLabel label = new OutputLabel();
                OutputLabel labelTitle = new OutputLabel();
                if (dto.getFontStyle().size() > 0) {
                    String style = "";
                    for (String s : dto.getFontStyle()) {
                        if (s.equals("italic")) {
                            style = style + "font-style:italic;";
                        } else {
                            style = style + "font-weight:700 !important;";
                        }
                    }
                    labelTitle.setStyle("float:left;font-weight:700 !important;word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                    label.setStyle(style + "word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                } else {
                    labelTitle.setStyle("float:left;font-weight:700 !important;word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");
                    label.setStyle("word-wrap:break-word;font-size:" + dto.getFontSize() + "pt;display:block;text-align:" + dto.getFontAlign() + ";");

                }
                if (selectedObject.isIsUseTemplate()) {

                } else if (!dto.getKeyWord().contains("textpnl")) {
                    labelTitle.setValue(dto.getName() + " : ");
                }

                if (dto.getKeyWord().trim().contains("textpnl")) {
                    labelTitle.setValue(dto.getName());
                } else if (dto.getKeyWord().contains("customertitlepnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("customeraddresspnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("customerphonepnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("customertaxofficepnl")) {

                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("customertaxofficenumberpnl")) {
                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("customertaxnumberpnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("customerbalancepnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("invoicenopnl")) {
                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("dispatchdatepnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("dispatchaddresspnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("duedatepnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("totalpricepnl")) {

                    label.setValue("deneme");

                } else if (dto.getKeyWord().contains("totaldiscountpnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("totaltaxpnl")) {

                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("exchangeratepnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("totalmoneypnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("invoicedatepnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("branchnamepnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("branchaddresspnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("branchmailpnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("branchtelephonepnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("branchtaxofficepnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("branchtaxnumberpnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("cashpnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("checkbillpnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("totalpricetaxpnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("grandtotalmoneywritepnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("recipientpersonpnl")) {
                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("deliverypersonpnl")) {

                    label.setValue("deneme");
                } else if (dto.getKeyWord().contains("signaturepnl")) {
                    label.setValue("deneme");
                }

                op.getChildren().add(labelTitle);
                op.getChildren().add(label);
                droppable.getChildren().add(op);
                RequestContext.getCurrentInstance().update("printPanel");

            }
        }

        RequestContext.getCurrentInstance().execute("printData();");

    }

    public void testBeforeDelete() {
        if (selectedObject.isIsDefault()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cannotbedeletedthedefaultdocumenttemplate")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        } else {
            RequestContext.getCurrentInstance().update("dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        }
    }

    public void delete() {
        int result = 0;
        result = documentTemplateService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/general/documenttemplate/documenttemplate.xhtml", null, 1, 62);
        }
        sessionBean.createUpdateMessage(result);
    }

    public void handleFileUploadFile(FileUploadEvent event) throws IOException {
        uploadedFile = event.getFile();
        fileName = uploadedFile.getFileName();
        fileName = new String(fileName.getBytes(Charset.defaultCharset()), "UTF-8"); // gelen türkçe karakterli utf8 formatında düzenler.
    }

    public void clearData() {
        uploadedFile = null;
    }

    public void importTemplateFromTxt() {
        int result = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(uploadedFile.getInputstream(), "iso-8859-9"));
            if (uploadedFile.getInputstream() != null) {
                StringBuffer response = new StringBuffer();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                if (response.toString() != null && !response.toString().equals("")) {
                    selectedObject.setJson(response.toString());
                    result = documentTemplateService.updateOnlyJson(selectedObject);
                }
                reader.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(DocumentTemplateProcessBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                uploadedFile.getInputstream().close();
            } catch (Throwable ignore) {
            }
        }
        sessionBean.createUpdateMessage(result);
        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_TemplateUpload').hide();");
            RequestContext.getCurrentInstance().execute("goToGrid();");
        }
    }

    public void goToGrid() {
        marwiz.goToPage("/pages/general/documenttemplate/documenttemplate.xhtml", null, 1, 62);
    }

    public StreamedContent prepDownload() throws Exception {
        StreamedContent download = new DefaultStreamedContent();
        String name = selectedObject.getName() + ".txt";
        File file = new File(name);

        String content = "";

        try (Writer out = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(file), "iso-8859-9"))) {

            if (!file.exists()) {
                file.createNewFile();
            }

            if (selectedObject.getJson() != null) {
                content = selectedObject.getJson();
                out.write(content);
            }

            out.flush();
            out.close();

            InputStream input = new FileInputStream(file);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            download = new DefaultStreamedContent(input, externalContext.getMimeType(file.getName()), file.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return download;
    }
}
