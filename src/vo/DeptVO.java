package vo;

import java.util.List;

public class DeptVO {
    private String deptno, dname;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private List<DocsVO> list;

    public List<DocsVO> getList() {
        return list;
    }

    public void setList(List<DocsVO> list) {
        this.list = list;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }
}
