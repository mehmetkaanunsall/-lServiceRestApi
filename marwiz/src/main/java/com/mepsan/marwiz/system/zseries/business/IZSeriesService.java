/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.zseries.business;

import com.mepsan.marwiz.general.model.general.ZSeries;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author m.duzoylum
 */
public interface IZSeriesService extends ICrudService<ZSeries> {

    public List<ZSeries> listofZseries(int branchId);

    public int delete(ZSeries obj);
}