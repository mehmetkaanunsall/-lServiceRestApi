/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   17.01.2018 10:01:20
 */
package com.mepsan.marwiz.general.categorization.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.io.InputStream;
import java.util.List;

public interface ICategorizationService extends ICrudService<Categorization> {

    public List<Categorization> listCategorization(Categorization obj);

    public int testBeforeDelete(Categorization categorization);

    public int delete(Categorization categorization);
    
    public String jsonArrayCategories(List<Categorization> categorizations);
    
    public String jsonArrayStocks(List<Stock> stocks);
    
    public String jsonArrayAccounts(List<Account> accounts);
    
    public List<Stock> createSampleList();
    
    public List<Stock> processUploadFile(InputStream inputStream);
    
    public String importProductList(List<Stock> stocks);
    
    public int addToItem(int itemId,Categorization categorization,String categorizations,String items);
    
    public void downloadSampleList(List<Stock>sampleList);

}
