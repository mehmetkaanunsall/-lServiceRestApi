/**
 * This interface ...
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * @date   23.11.2016 07:31:55
 */
package com.mepsan.marwiz.general.protocol.dao;

import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.general.Protocol;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IProtocolDao extends ICrud<Protocol> {

    public List<Protocol> findAll(Item item);

    public int testBeforeDelete(Protocol protocol);

    public int delete(Protocol protocol);

}
