/**
 * Bu sınıf, Dao sınıflarına ara yüz oluşturur.
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.pattern;

public interface ICrud<T> {

    public int create(T obj);

    public int update(T obj);

}
