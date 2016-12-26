package com.vrseen.vrstore.http;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jiangs on 16/4/29.
 */
public class ListResult<T> implements Serializable {

    @Override
    public String toString() {
        String ret = "list: ";
        for (T child : list) {
            ret += child.toString() + " ";
        }
        ret = ret + " total: " + total + " hasmore: " + hasMore + " newordernum: " + newOrderNum;
        return ret;
    }

    private List<T> list;

    private int total;

    private boolean hasMore = true;

    private int newOrderNum;

    public int getNewOrderNum() {
        return newOrderNum;
    }

    public void setNewOrderNum(int newOrderNum) {
        this.newOrderNum = newOrderNum;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
