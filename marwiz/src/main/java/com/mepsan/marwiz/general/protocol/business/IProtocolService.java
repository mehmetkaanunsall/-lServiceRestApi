/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 23.11.2016 16:12:07
 */
package com.mepsan.marwiz.general.protocol.business;

import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.general.Protocol;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IProtocolService extends ICrudService<Protocol> {

    public List<Protocol> findAll(Item item);

    public int testBeforeDelete(Protocol protocol);

    public int delete(Protocol protocol);

}
