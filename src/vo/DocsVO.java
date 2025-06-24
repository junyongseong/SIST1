package vo;

public class DocsVO {
    private String docs_num, empno, deptno, title, visibility, logs_num, content, date, co_letter;
    private String ename;
    private String dname;
    public DSharedVO dsvo;

    public DSharedVO getDsvo() {
        return dsvo;
    }

    public void setDsvo(DSharedVO dsvo) {
        this.dsvo = dsvo;
    }

    public String getShare_id() {
        return share_id;
    }

    public void setShare_id(String share_id) {
        this.share_id = share_id;
    }

    private String share_id;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getDname() {
        return dname;
    }

    public void setDname(String dname) {
        this.dname = dname;
    }

    private DeptVO dpvo;
    private EmpVO evo;

    public EmpVO getEvo() {
        return evo;
    }

    public void setEvo(EmpVO evo) {
        this.evo = evo;
    }

    public DeptVO getDpvo() {
        return dpvo;
    }

    public void setDpvo(DeptVO dpvo) {
        this.dpvo = dpvo;
    }

    public String getDocs_num() {
        return docs_num;
    }

    public void setDocs_num(String docs_num) {
        this.docs_num = docs_num;
    }

    public String getEmpno() {
        return empno;
    }

    public void setEmpno(String empno) {
        this.empno = empno;
    }

    public String getDeptno() {
        return deptno;
    }

    public void setDeptno(String deptno) {
        this.deptno = deptno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getLogs_num() {
        return logs_num;
    }

    public void setLogs_num(String logs_num) {
        this.logs_num = logs_num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCo_letter() {
        return co_letter;
    }

    public void setCo_letter(String co_letter) {
        this.co_letter = co_letter;
    }
}
