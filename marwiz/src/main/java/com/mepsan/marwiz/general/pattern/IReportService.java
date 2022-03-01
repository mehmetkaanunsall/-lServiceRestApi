/**
 * Bu interface ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   17.03.2017 11:18:04
 */
package com.mepsan.marwiz.general.pattern;

public interface IReportService<T> extends ILazyGridService<T> {

    public String createWhere(T obj);

}
