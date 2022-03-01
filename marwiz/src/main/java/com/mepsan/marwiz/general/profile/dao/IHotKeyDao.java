/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 18.04.2017 09:07:21
 */
package com.mepsan.marwiz.general.profile.dao;

import com.mepsan.marwiz.general.model.general.HotKey;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IHotKeyDao extends ICrud<HotKey> {

    List<HotKey> listHotKeys();

    public int delete(HotKey hotKey);

}
