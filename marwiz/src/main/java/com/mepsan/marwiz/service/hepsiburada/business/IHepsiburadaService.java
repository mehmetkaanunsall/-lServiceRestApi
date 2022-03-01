/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.03.2021 12:05:50
 */
package com.mepsan.marwiz.service.hepsiburada.business;

import com.mepsan.marwiz.general.model.general.BranchIntegration;
import java.util.List;

public interface IHepsiburadaService {

    public void listHepsiburada(BranchIntegration branchIntegration);

    public void executeListHepsiburada(List<BranchIntegration> branchIntegrations);

    public void listHepsiburadaAsync();

}
