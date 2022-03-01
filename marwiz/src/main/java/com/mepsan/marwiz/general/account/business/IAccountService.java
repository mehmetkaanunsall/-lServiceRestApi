/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 01:34:35
 */
package com.mepsan.marwiz.general.account.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountUpload;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface IAccountService extends ICrudService<Account> {

    public String createWhere(boolean isWithoutMovement, boolean isZeroBalance, List<Categorization> listCategorization, int type);

    public List<Account> accountBook(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String type, List<Object> param);

    public int accountBookCount(String where, String type, List<Object> param);

    public List<Account> findAll(String where);

    public List<Account> findAllAccount(int typeId);

    public int delete(Account account);

    public int testBeforeDelete(Account account);

    public List<Account> findAllAccountToIntegrationCode();

    public List<Account> findSupplier();

    public List<AccountUpload> createSampleList();

    public List<AccountUpload> processUploadFile(InputStream inputStream);

    public String jsonToList(List<AccountUpload> uploadList);

    public void exportPdf(List<Account> listOfObjects, String clmName, List<Boolean> toogleList);

    public List<Account> taxPayerİnquiryRequest(Account account);

    public Account taxPayerİnquiryRequestU(Account account);

    public Account requestAccountInfo(Account acc);

    public void downloadSampleList(List<AccountUpload> sampleList);

    public int controlCashierUser(Account account);
}
