package vo;

public class AttendanceVO {
    String attendno, empno, date, chkin, chkout, attend_status, attend_note;
    String ename;

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getAttendno() {
        return attendno;
    }

    public void setAttendno(String attendno) {
        this.attendno = attendno;
    }

    public String getEmpno() {
        return empno;
    }

    public void setEmpno(String empno) {
        this.empno = empno;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChkin() {
        return chkin;
    }

    public void setChkin(String chkin) {
        this.chkin = chkin;
    }

    public String getChkout() {
        return chkout;
    }

    public void setChkout(String chkout) {
        this.chkout = chkout;
    }

    public String getAttend_status() {
        return attend_status;
    }

    public void setAttend_status(String attend_status) {
        this.attend_status = attend_status;
    }

    public String getAttend_note() {
        return attend_note;
    }

    public void setAttend_note(String attend_note) {
        this.attend_note = attend_note;
    }
}
