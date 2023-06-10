package com.atguigu.es.pojo;

import java.io.Serializable;

/**
 * 包名:com.atguigu.es.pojo
 *
 * @author Weirdo
 * 日期2023/4/1 15:45
 */
public class Article implements Serializable {
    private Long id;
    private String title;
    private String content;

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

