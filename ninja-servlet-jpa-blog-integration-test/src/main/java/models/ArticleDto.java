package models;

import javax.validation.constraints.Size;

public class ArticleDto {

    @Size(min = 5)
    public String title;
    
    @Size(min = 5)
    public String content;
    
    public ArticleDto() {}

}
