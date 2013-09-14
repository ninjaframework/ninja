package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

@Entity
public class Article {
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Long id;
    
    public String title;
    
    public Date postedAt;
    
    @Column(length = 5000) //init with VARCHAR(1000)
    public String content;
    
    @ElementCollection(fetch=FetchType.EAGER)
    public List<Long> authorIds;
    
    public Article() {}
    
    public Article(User author, String title, String content) {
        this.authorIds = Lists.newArrayList(author.id);
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }
 
}