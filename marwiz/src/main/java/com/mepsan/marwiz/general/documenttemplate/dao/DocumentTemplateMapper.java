/**
 * This class ...
 *
 *
 * @author Cihat Küçükbağrıaçık
 *
 * @date   07.03.2018 02:46:21
 */
package com.mepsan.marwiz.general.documenttemplate.dao;

import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DocumentTemplateMapper implements RowMapper<DocumentTemplate> {

    @Override
    public DocumentTemplate mapRow(ResultSet rs, int i) throws SQLException {
        DocumentTemplate documentTemplate = new DocumentTemplate();
        try {
            documentTemplate.setId(rs.getInt("gdtid"));
            documentTemplate.setName(rs.getString("gdtname"));
            documentTemplate.setIsDefault(rs.getBoolean("gdtis_default"));
            documentTemplate.getType().setId(rs.getInt("gdttype_id"));
            documentTemplate.getType().setTag(rs.getString("typname"));
            documentTemplate.setIsUseTemplate(rs.getBoolean("gdtis_usetemplate"));
        } catch (Exception e) {
            System.out.println("select template ");
        }

        documentTemplate.setPaperSize(rs.getInt("gdtpapersize"));
        documentTemplate.setWidth(rs.getBigDecimal("gdtwidth"));
        documentTemplate.setHeight(rs.getBigDecimal("gdtheight"));
        documentTemplate.setMargin_top(rs.getInt("gdttop"));
        documentTemplate.setMargin_bottom(rs.getInt("gdtbottom"));
        documentTemplate.setMargin_left(rs.getInt("gdtleft"));
        documentTemplate.setMargin_right(rs.getInt("gdtright"));
        documentTemplate.setIsVertical(rs.getBoolean("gdtis_vertical"));
        documentTemplate.setJson(rs.getString("gdtjson"));
        System.out.println(rs.getInt("gdttop") +" margin from mapper " + documentTemplate.getMargin_top());
        return documentTemplate; 
    }

}
