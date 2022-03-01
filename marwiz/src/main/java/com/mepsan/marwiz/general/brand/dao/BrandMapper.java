/**
 *
 * Bu sınıf, Brand nesnesini oluşturur ve özelliklerini set eder.
 *
 * @author Ali Kurt
 *
 * Created on 12.01.2018 16:57:55
 */
package com.mepsan.marwiz.general.brand.dao;

import com.mepsan.marwiz.general.model.general.Brand;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class BrandMapper implements RowMapper<Brand> {

    @Override
    public Brand mapRow(ResultSet rs, int i) throws SQLException {

        Brand brand = new Brand();
        
        brand.setId(rs.getInt("brid"));
        brand.setCenterbrand_id(rs.getInt("brcenterbrand_id"));
        brand.setName(rs.getString("brname"));        
        
        return brand;

    }

}
