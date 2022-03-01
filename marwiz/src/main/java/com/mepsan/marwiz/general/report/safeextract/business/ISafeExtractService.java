/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.03.2018 16:55:21
 */
package com.mepsan.marwiz.general.report.safeextract.business;

import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.finance.SafeMovement;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.List;

public interface ISafeExtractService {

    public String createWhere(List<Safe> selectedSafe, List<Branch> selectedBranchList);

    public List<SafeMovement> findAll(String where);

    public void exportPdf(String where, List<SafeMovement> listOfSafeExtract, List<Safe> selectedSafe, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance);

    public void exportExcel(String where, List<SafeMovement> listOfSafeExtract, List<Safe> selectedSafe, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance);

    public String exportPrinter(String where, List<SafeMovement> listOfSafeExtract, List<Safe> selectedSafe, List<Branch> selectedBranchList, List<Boolean> toogleList, String subTotalIncome, String subTotalOutcome, String subTotalBalance);

}
