/**
 * Copyright (C) the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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