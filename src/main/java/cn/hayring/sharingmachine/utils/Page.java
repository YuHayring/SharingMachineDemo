package cn.hayring.sharingmachine.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象. 包含当前页数据及分页信息如总记录数.
 */
public class Page<T> implements Serializable {

    public static int DEFAULT_PAGE_SIZE = 10;

    protected int pageSize = DEFAULT_PAGE_SIZE; // 每页的记录数

    protected long start; // 当前页第一条数据在List中的位置,从0开始

    protected List<T> data; // 当前页中存放的记录,类型一般为List

    protected long totalCount; // 总记录数

    /**
     * 构造方法，只构造空页.
     */
    public Page() {
        this(0, 0, DEFAULT_PAGE_SIZE, new ArrayList<T>());
    }

    /**
     * 默认构造方法.
     *
     * @param start     本页数据在数据库中的起始位置
     * @param totalSize 数据库中总记录条数
     * @param pageSize  本页容量
     * @param data      本页包含的数据
     */
    public Page(long start, long totalSize, int pageSize, List<T> data) {
        this.pageSize = pageSize;
        this.start = start;
        this.totalCount = totalSize;
        this.data = data;
    }

    /**
     * 取总记录数.
     */
    public long getTotalCount() {
        return this.totalCount;
    }

    public String getTotalCountString() {
        Long l = getTotalCount();
        String ls = l.toString();
        return ls;
    }

    /**
     * 取总页数.
     */
    public long getTotalPageCount() {
        if (totalCount % pageSize == 0)
            return totalCount / pageSize;
        else
            return totalCount / pageSize + 1;
    }

    public String getTotalPageCountString() {
        Long l = getTotalCount();
        return l.toString();
    }

    /**
     * 取每页数据容量.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 取当前页中的记录.
     */
    public List<T> getResult() {
        return data;
    }

    /**
     * 取该页当前页码,页码从1开始.
     */
    public long getCurrentPageNo() {
        return start / pageSize + 1;
    }

    public String getCurrenttPageNoString() {
        Long l = getCurrentPageNo();
        return l.toString();
    }


    /**
     * 该页是否有下一页.
     */
    public boolean isHasNextPage() {
        return this.getCurrentPageNo() < this.getTotalPageCount();
    }

    /**
     * 该页是否有上一页.
     */
    public boolean isHasPreviousPage() {
        return this.getCurrentPageNo() > 1;
    }

    /**
     * 获取任一页第一条数据在数据集的位置，每页条数使用默认值.
     *
     * @see #getStartOfPage(int, int)
     */
    protected static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
    }

    /**
     * 获取任一页第一条数据在数据集的位置.
     *
     * @param pageNo   从1开始的页号
     * @param pageSize 每页记录条数
     * @return 该页第一条数据
     */
    public static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }


    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}