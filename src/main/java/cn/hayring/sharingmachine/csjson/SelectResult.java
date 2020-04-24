package cn.hayring.sharingmachine.csjson;

import cn.hayring.sharingmachine.utils.Page;

import java.util.List;

public class SelectResult extends CSJson {

    public SelectResult() {
        super(SELECT_RESULT);
    }

    public SelectResult(List data) {
        super(SELECT_RESULT);
        this.data = data;
    }

    private List data;

    private Long totalCount;

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
