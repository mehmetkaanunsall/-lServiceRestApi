/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.exchangedefinitions.business;

import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IExchangeDefinitionsService extends ICrudService<Currency> {

    public List<Currency> findAll();

}
