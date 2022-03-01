/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.zseries.business;

import com.mepsan.marwiz.general.model.general.ZSeries;
import com.mepsan.marwiz.system.zseries.dao.IZSeriesDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author m.duzoylum
 */
public class ZSeriesService implements IZSeriesService {

    @Autowired
    private IZSeriesDao zSeriesDao;

    @Override
    public List<ZSeries> listofZseries(int branchId) {
        return zSeriesDao.listofZseries(branchId);
    }

    @Override
    public int delete(ZSeries obj) {
        return zSeriesDao.delete(obj);
    }

    @Override
    public int create(ZSeries obj) {
        return zSeriesDao.create(obj);
    }

    @Override
    public int update(ZSeries obj) {
        return zSeriesDao.update(obj);
    }

}
