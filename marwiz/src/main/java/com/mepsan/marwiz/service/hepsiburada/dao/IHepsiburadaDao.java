/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.03.2021 12:06:21
 */
package com.mepsan.marwiz.service.hepsiburada.dao;

import com.mepsan.marwiz.general.model.general.BranchIntegration;
import java.util.List;

public interface IHepsiburadaDao {

    public List<BranchIntegration> findBranchIntegration();

    public int processHepsiburada(String stockList, BranchIntegration branchIntegration, int type, String updateResult, String updateControlResult, boolean isSuccess);

    public String findSendingHepsiburada(BranchIntegration branchIntegration);

}
