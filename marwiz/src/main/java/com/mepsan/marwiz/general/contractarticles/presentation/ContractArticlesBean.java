/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:18:20 PM
 */
package com.mepsan.marwiz.general.contractarticles.presentation;

import com.mepsan.marwiz.general.contractarticles.business.IContractArticleService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.ContractArticles;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean    
@ViewScoped    
public class ContractArticlesBean extends GeneralDefinitionBean<ContractArticles> {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{contractArticleService}") // session
    public IContractArticleService contractArticleService;

    @ManagedProperty(value = "#{stockService}") // session
    public IStockService stockService;

    private int processType;
    private List<Stock> listOfFuelStock;
    private int isThereStock;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public void setContractArticleService(IContractArticleService contractArticleService) {
        this.contractArticleService = contractArticleService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Stock> getListOfFuelStock() {
        return listOfFuelStock;
    }

    public void setListOfFuelStock(List<Stock> listOfFuelStock) {
        this.listOfFuelStock = listOfFuelStock;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--------ContractArticlesBean------");
        processType = 1;
        listOfObjects = findall();
        listOfFuelStock = stockService.findFuelStock();
        
        setListBtn(sessionBean.checkAuthority(new int[]{254, 255, 256}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new ContractArticles();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_contractarticlesproc').show();");
    }

    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
        stockControlArticles(selectedObject);
        if (processType == 1) {
            if (isThereStock == 0) {
                result = contractArticleService.create(selectedObject);
                sessionBean.createUpdateMessage(result);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects.add(selectedObject);
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("agreementonproductinformationisavailableinthesystem")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else {
            if (selectedObject.getArticltType() == 1) {
                selectedObject.setBranchProfitRate(null);
                selectedObject.setVolume1(null);
                selectedObject.setVolume2(null);
                selectedObject.setVolume3(null);
                selectedObject.setVolume4(null);
                selectedObject.setVolume5(null);

                selectedObject.setRate2(null);
                selectedObject.setRate3(null);
                selectedObject.setRate4(null);
                selectedObject.setRate5(null);

            } else if (selectedObject.getArticltType() == 2) {
                selectedObject.setBranchProfitRate(null);
            } else {
                selectedObject.setVolume1(null);
                selectedObject.setVolume2(null);
                selectedObject.setVolume3(null);
                selectedObject.setVolume4(null);
                selectedObject.setVolume5(null);

                selectedObject.setRate1(null);
                selectedObject.setRate2(null);
                selectedObject.setRate3(null);
                selectedObject.setRate4(null);
                selectedObject.setRate5(null);
            }
            result = contractArticleService.update(selectedObject);
            sessionBean.createUpdateMessage(result);
        }

        if (result > 0) {
            context.execute("PF('dlg_contractarticlesproc').hide();");
            context.update("frmContractArticlesDefinitons:dtbContractArticlesDefinition");
            context.execute("PF('ContractArticlesPF').filter();");
        }
    }

    public int stockControlArticles(ContractArticles articles) {
        isThereStock = 0;
        isThereStock = contractArticleService.stockControl(selectedObject);
        return isThereStock;
    }

    @Override
    public List<ContractArticles> findall() {
        return contractArticleService.findAll();
    }

    public void update() {
        processType = 2;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_contractarticlesproc').show();");
        context.update("frmContractArticlesDefinitionProcess:pgrContractArticlesProcess");
    }

    public void delete() {
        int result = 0;
        result = contractArticleService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_contractarticlesproc').hide();");
            context.update("frmContractArticlesDefinitons:dtbContractArticlesDefinition");
            context.execute("PF('ContractArticlesPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    /**
     * Stok adı bilgisini selectedObjecte set eder.
     */
    public void bringNameOfStock() {
        for (Stock stock : listOfFuelStock) {
            if (stock.getId() == selectedObject.getStock().getId()) {
                selectedObject.getStock().setName(stock.getName());
                break;
            }
        }
        stockControlArticles(selectedObject);
    }
}
