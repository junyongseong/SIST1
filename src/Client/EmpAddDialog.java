package Client;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import vo.EmpVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpAddDialog extends JDialog {
    private JTextField tfEname, tfSal, tfHire, tfResign, tfEmail, tfPhone, tfUsername, tfPassword;
    private JComboBox<String> cbDept, cbStatus, cbMgr, cbPos;
    private final Map<String, String> mgrMap = new HashMap<>();

    private EmpAddedListener empAddedListener;

    public EmpAddDialog(JFrame parent, SqlSessionFactory factory) {
        super(parent, "사원 추가", true);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(0, 2, 5, 5));
        tfEname = new JTextField();
        cbDept = new JComboBox<>(new String[]{"개발부", "인사부", "영업부", "관리부", "총무부"});
        cbPos = new JComboBox<>(new String[]{"사원", "대리", "팀장"});
        tfSal = new JTextField();
        cbStatus = new JComboBox<>(new String[]{"재직", "퇴직"});
        tfHire = new JTextField();
        tfResign = new JTextField();

        try (SqlSession ss = factory.openSession()) {
            List<EmpVO> mgrList = ss.selectList("adminemp.getAllMgrCandidates");
            for (EmpVO mgr : mgrList) {
                mgrMap.put(mgr.getEname(), String.valueOf(mgr.getEmpno()));
            }
        }

        cbMgr = new JComboBox<>(mgrMap.keySet().toArray(new String[0]));
        tfEmail = new JTextField();
        tfPhone = new JTextField();
        tfUsername = new JTextField();
        tfPassword = new JTextField();

        form.add(new JLabel("이름 *       (*는 필수항목입니다)")); form.add(tfEname);
        form.add(new JLabel("부서 *")); form.add(cbDept);
        form.add(new JLabel("직급 *")); form.add(cbPos);
        form.add(new JLabel("급여 *")); form.add(tfSal);
        form.add(new JLabel("재직상태 *")); form.add(cbStatus);
        form.add(new JLabel("입사일 * (yyyy-mm-dd)")); form.add(tfHire);
        form.add(new JLabel("퇴사일")); form.add(tfResign);
        form.add(new JLabel("관리자")); form.add(cbMgr);
        form.add(new JLabel("이메일")); form.add(tfEmail);
        form.add(new JLabel("연락처 * (000-0000-0000)")); form.add(tfPhone);
        form.add(new JLabel("아이디 *")); form.add(tfUsername);
        form.add(new JLabel("비밀번호 *")); form.add(tfPassword);

        add(form, BorderLayout.CENTER);

        JButton btnAdd = new JButton("추가");
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 유효성 검사
                if (tfEname.getText().trim().isEmpty() || tfSal.getText().trim().isEmpty()
                        || tfHire.getText().trim().isEmpty() || tfPhone.getText().trim().isEmpty()
                        || tfUsername.getText().trim().isEmpty() || tfPassword.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(EmpAddDialog.this, "필수 항목을 모두 입력해주세요.");
                    return;
                }
                try {
                    Integer.parseInt(tfSal.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(EmpAddDialog.this, "급여는 숫자로 입력해주세요.");
                    return;
                }
                try (SqlSession ss = factory.openSession()) {//trt -with resources 이러면 닫아줄 필요 없음
                    EmpVO emp = new EmpVO();
                    emp.setEname(tfEname.getText());
                    emp.setDeptno(getDeptCode(cbDept.getSelectedItem().toString()));
                    emp.setPosname(cbPos.getSelectedItem().toString());
                    emp.setSal(tfSal.getText());
                    emp.setWork_status("퇴직".equals(cbStatus.getSelectedItem()) ? "1" : "0");
                    emp.setHireDATE(tfHire.getText());
                    emp.setResign_DATE(tfResign.getText().isEmpty() ? null : tfResign.getText());
                    emp.setMgr(mgrMap.get(cbMgr.getSelectedItem()));
                    emp.setEmail(tfEmail.getText());
                    emp.setPhone(tfPhone.getText());
                    emp.setUsername(tfUsername.getText());
                    emp.setPassword(tfPassword.getText());
                    emp.setRole_num("2");

                    int result = ss.insert("adminemp.insertEmp", emp);
                    ss.commit();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(EmpAddDialog.this, "사원 추가 성공");
                        if (empAddedListener != null) {
                            empAddedListener.onEmpAdded();
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(EmpAddDialog.this, "사원 추가 실패");
                    }
                }
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAdd);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 600);
        setLocationRelativeTo(parent);
    }

    private String getDeptCode(String deptName) {
        return switch (deptName) {
            case "개발부" -> "10";
            case "인사부" -> "20";
            case "영업부" -> "30";
            case "관리부" -> "40";
            case "총무부" -> "50";
            default -> "10";
        };
    }

    public void setEmpAddedListener(EmpAddedListener listener) {
        this.empAddedListener = listener;
    }

    public interface EmpAddedListener {
        void onEmpAdded();
    }
}