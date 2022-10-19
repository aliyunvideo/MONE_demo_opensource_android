package com.alivc.live.beautyui.bean;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 美颜TAB UI bean
 */
public class BeautyTabBean implements Cloneable {

    private final int id;
    private String title;

    private boolean canMultiSelect;
    private boolean isResetShow;

    private ArrayList<BeautyItemBean> itemBeans;

    public BeautyTabBean(final int id, String title, ArrayList<BeautyItemBean> itemBeans) {
        this.id = id;
        this.title = title;
        this.itemBeans = itemBeans;
    }

    public BeautyTabBean(final int id, String title, boolean canMultiSelect, boolean isResetShow, ArrayList<BeautyItemBean> itemBeans) {
        this.id = id;
        this.title = title;
        this.canMultiSelect = canMultiSelect;
        this.isResetShow = isResetShow;
        this.itemBeans = itemBeans;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCanMultiSelect() {
        return canMultiSelect;
    }

    public void setCanMultiSelect(boolean canMultiSelect) {
        this.canMultiSelect = canMultiSelect;
    }

    public boolean isResetShow() {
        return isResetShow;
    }

    public void setResetShow(boolean resetShow) {
        isResetShow = resetShow;
    }

    public ArrayList<BeautyItemBean> getItemBeans() {
        return itemBeans;
    }

    public void setItemBeans(ArrayList<BeautyItemBean> itemBeans) {
        this.itemBeans = itemBeans;
    }

    @Override
    public String toString() {
        return "BeautyTabBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", canMultiSelect=" + canMultiSelect +
                ", isResetShow=" + isResetShow +
                ", itemBeans=" + itemBeans +
                '}';
    }

    @NonNull
    @Override
    public BeautyTabBean clone() {
        ArrayList<BeautyItemBean> itemBeans = new ArrayList<BeautyItemBean>();
        for (BeautyItemBean bean : this.itemBeans) {
            itemBeans.add(bean.clone());
        }
        return new BeautyTabBean(this.id, this.title, this.canMultiSelect, this.isResetShow, itemBeans);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeautyTabBean tabBean = (BeautyTabBean) o;
        return id == tabBean.id &&
                canMultiSelect == tabBean.canMultiSelect &&
                isResetShow == tabBean.isResetShow &&
                Objects.equals(title, tabBean.title) &&
                Objects.equals(itemBeans, tabBean.itemBeans);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, canMultiSelect, isResetShow, itemBeans);
    }
}
