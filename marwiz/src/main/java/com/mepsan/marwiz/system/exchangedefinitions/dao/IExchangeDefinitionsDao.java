/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.exchangedefinitions.dao;

import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IExchangeDefinitionsDao extends ICrud<Currency> {

    public List<Currency> findAll();
}
