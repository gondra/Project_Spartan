package model;

import org.json.JSONObject;

import java.util.Vector;

public class RecordM{
    int currentPage;
    int rowPerPage;
    int totalPage;
    Vector<JSONObject> recordArray;

    public RecordM(){
        currentPage = 0;
        rowPerPage = 0;
        totalPage = 0;
        recordArray = new Vector<>();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public Vector<JSONObject> getRecordArray() {
        return recordArray;
    }

    public void setRecordArray(Vector<JSONObject> recordArray) {
        this.recordArray = recordArray;
    }

    public int getRowPerPage() {
        return rowPerPage;
    }

    public void setRowPerPage(int rowPerPage) {
        this.rowPerPage = rowPerPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
