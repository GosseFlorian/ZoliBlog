package fr.ada.java_blog.model;

public class Media {

    private Integer id;
    private MediaType type;
    private String url;

    public Media(Integer id, MediaType type, String url) {
        this.id = id;
        this.type = type;
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
