package ru.khl.bot.model;

public class Item {

    private String caption; // текст к посту
    private String link; // ссылка на фото/видео
    private String title; //только у файлов
    private PostType postType; // тип поста

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public enum PostType {

        KHL_PHOTO, KHL_VIDEO, KHL_FILE, KHL_ONLY_TEXT, KHL_LINK;

        public String value() {
            return name();
        }

        public static PostType fromValue(String v) {
            return valueOf(v);
        }
    }
}
