package com.hujiang.hujiangapp.model;

public class Dict {
    private String id;
    private String text;
    private String category;
    private String groupTitle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dict dict = (Dict) o;

        return id != null ? id.equals(dict.id) : dict.id == null;
    }

    @Override
    public String toString() {
        return text == null ? "" : text;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static Dict dictWithId(String id) {
        Dict dict = new Dict();
        dict.setId(id);
        return dict;
    }

    public static Dict sexMale() {
        return dictWithId("SEX_MAN");
    }

    public static Dict sexFemale() {
        return dictWithId("SEX_WOMAN");
    }

    public static Dict attendIn() {
        return dictWithId("in");
    }

    public static Dict attendOut() {
        return dictWithId("out");
    }
}
