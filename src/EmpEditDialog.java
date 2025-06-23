import vo.EmpVO;
import org.apache.ibatis.session.SqlSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpEditDialog extends JDialog {
    private EmpVO emp;
    private SqlSession ss;
    private Runnable refresh; //thread 대신 runable함수 사용함 근데 바꾸려면 바꿀수 있긴함

    private JTextField tfEname, tfSal, tfHire, tfResign, tfEmail, tfPhone, tfUsername, tfPassword;
    private JComboBox<DeptItem> cbDept;
    private JComboBox<String> cbStatus, cbMgr, cbPos;

    private final List<String> statusOptions = Arrays.asList("재직", "퇴직");
    private final List<String> posOptions = Arrays.asList("사원", "대리", "팀장");
    private final Map<String, String> mgrMap = new HashMap<>(); //관리자 가져오기위함 Map


    // 부서 항목을 담는 함수
    static class DeptItem {
        String deptNo;
        String deptName;

        DeptItem(String deptNo, String deptName) {
            this.deptNo = deptNo;
            this.deptName = deptName;
        }

        @Override
        public String toString() {
            return deptName; // 콤보박스에 보일 이름
        }
    }

    //생성자
    public EmpEditDialog(JFrame parent, EmpVO vo, SqlSession ss, Runnable refresh) {
        super(parent, "사원 정보 수정", true);
        this.emp = vo;
        this.ss = ss;
        this.refresh = refresh;

        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tfEname = new JTextField(emp.getEname());

        // 부서 콤보박스
        cbDept = new JComboBox<>();
        cbDept.addItem(new DeptItem("10", "개발부"));
        cbDept.addItem(new DeptItem("20", "인사부"));
        cbDept.addItem(new DeptItem("30", "영업부"));
        cbDept.addItem(new DeptItem("40", "관리부"));
        cbDept.addItem(new DeptItem("50", "총무부"));

        for (int i = 0; i < cbDept.getItemCount(); i++) {
            DeptItem item = cbDept.getItemAt(i);
            if (item.deptNo.equals(emp.getDeptno())) {
                cbDept.setSelectedIndex(i);
                break;
            }
        }

        // 직급명 콤보박스
        cbPos = new JComboBox<>(posOptions.toArray(new String[0]));
        String cleanPos = emp.getPosname() == null ? "" : emp.getPosname().trim();//자바 삼항 연산자 null이면 공백 처리 아니면 posname가져오기
        if (posOptions.contains(cleanPos)) {
            cbPos.setSelectedItem(cleanPos);
        } else {
            cbPos.setSelectedIndex(0);
        }

        tfSal = new JTextField(emp.getSal());

        cbStatus = new JComboBox<>(statusOptions.toArray(new String[0]));
        cbStatus.setSelectedIndex("1".equals(emp.getWork_status()) ? 1 : 0);//1인지 0인지 확인

        tfHire = new JTextField(emp.getHireDATE());
        tfHire.setEditable(false);

        //비어있는지 아닌지 확인용
        tfResign = new JTextField(emp.getResign_DATE() == null ? "" : emp.getResign_DATE());

        //관리자 추가하는 부분 일단 3개임 추가로 관리자가 생기면 넣을 수 있음
        // 관리자 목록 DB에서 가져오기
        List<EmpVO> mgrList = ss.selectList("adminemp.getAllMgrCandidates");

        // 맵에 사번과 이름을 저장하고 콤보박스  사용 예정
        for (EmpVO mgr : mgrList) {
            mgrMap.put(mgr.getEname(), String.valueOf(mgr.getEmpno()));
        }

        cbMgr = new JComboBox<>(mgrMap.keySet().toArray(new String[0]));

        // 현재 사원의 관리자 이름으로 나오게함
        String currentMgrName = emp.getMgr_name();
        if (currentMgrName != null && mgrMap.containsKey(currentMgrName)) {
            cbMgr.setSelectedItem(currentMgrName);
        } else {
            cbMgr.setSelectedIndex(0); // 기본값
        }
        String mgrName = (String) cbMgr.getSelectedItem();
        emp.setMgr(mgrMap.get(mgrName)); // DB에 들어갈 사번 세팅!

        tfEmail = new JTextField(emp.getEmail());
        tfPhone = new JTextField(emp.getPhone());
        tfUsername = new JTextField(emp.getUsername());
        tfPassword = new JTextField(emp.getPassword());

        form.add(new JLabel("이름")); form.add(tfEname);
        form.add(new JLabel("부서명")); form.add(cbDept);
        form.add(new JLabel("직급명")); form.add(cbPos);
        form.add(new JLabel("급여")); form.add(tfSal);
        form.add(new JLabel("재직상태")); form.add(cbStatus);
        form.add(new JLabel("입사일")); form.add(tfHire);
        form.add(new JLabel("퇴사일")); form.add(tfResign);
        form.add(new JLabel("관리자")); form.add(cbMgr);
        form.add(new JLabel("이메일")); form.add(tfEmail);
        form.add(new JLabel("연락처")); form.add(tfPhone);
        form.add(new JLabel("아이디")); form.add(tfUsername);
        form.add(new JLabel("비밀번호")); form.add(tfPassword);

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton save = new JButton("저장");
        JButton cancel = new JButton("취소");
        btnPanel.add(save);
        btnPanel.add(cancel);
        add(btnPanel, BorderLayout.SOUTH);

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emp.setEname(tfEname.getText());

                DeptItem selectedDept = (DeptItem) cbDept.getSelectedItem();
                emp.setDeptno(selectedDept.deptNo); // 부서번호 저장를 저장함

                emp.setPosname((String) cbPos.getSelectedItem());
                emp.setSal(tfSal.getText());

                String status = (String) cbStatus.getSelectedItem();
                emp.setWork_status("퇴직".equals(status) ? "1" : "0"); //퇴직이면 1
                //퇴직이면 현재 시간 찍히게 하는 코드 아니면 null
                emp.setResign_DATE("퇴직".equals(status) ? java.time.LocalDate.now().toString() : null);

                String mgrName = (String) cbMgr.getSelectedItem();
                emp.setMgr(mgrMap.get(mgrName)); // 사번으로 저장됨

                emp.setEmail(tfEmail.getText());
                emp.setPhone(tfPhone.getText());
                emp.setUsername(tfUsername.getText());
                emp.setPassword(tfPassword.getText());

                int result = ss.update("adminemp.updateEmpByAdmin", emp);
                ss.commit();
                if (result > 0) {
                    JOptionPane.showMessageDialog(EmpEditDialog.this, "수정 완료");
                    refresh.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(EmpEditDialog.this, "수정 실패");
                }
            }
        });

        //취소버튼
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();//이창 닫기
            }
        });

        setSize(400, 550);
        setLocationRelativeTo(parent);// 창위치 고정해주는 함수임 부모창의 정가운데에 생성
        //setLocation(500,300);
        setVisible(true);
    }
}
