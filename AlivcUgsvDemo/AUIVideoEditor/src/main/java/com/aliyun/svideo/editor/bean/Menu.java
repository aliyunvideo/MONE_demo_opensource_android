package com.aliyun.svideo.editor.bean;

public class Menu {
    public static final int MENU_MUSIC = 0;
    public static final int MENU_TRANSITION = 1;
    public static final int MENU_CAPTION = 2;
    public static final int MENU_STICKER = 3;
    public static final int MENU_FILTER = 4;
    public static final int MENU_EFFECT = 5;
    public static final int MENU_EDIT_CAPTION = 6;

    public int id;
    public int icon;
    public int text;

    public Menu(int id, int icon, int text) {
        this.id = id;
        this.icon = icon;
        this.text = text;
    }
}
