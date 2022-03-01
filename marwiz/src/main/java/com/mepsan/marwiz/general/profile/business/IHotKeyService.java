/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 18.04.2017 09:09:20
 */
package com.mepsan.marwiz.general.profile.business;

import com.mepsan.marwiz.general.model.general.HotKey;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IHotKeyService extends ICrudService<HotKey> {

    public List<HotKey> listHotKeys();

    public int delete(HotKey hotKey);
}
