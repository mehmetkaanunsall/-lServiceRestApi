/**
 * Bu sınıf, iletişim bilgileri bulunan sınıfın dao katmanına arayüz oluşturur.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.pattern;


public interface ICommunicationDao<T, S> extends ICrud<T> {
    
    public int delete(T obj);

}
