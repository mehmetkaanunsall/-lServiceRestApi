
package de.kaanunsal.ilservice.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IL {
    
    @Id
     private String id;
    
     private Date createDate = new Date();
     
     private String name;
    
}
