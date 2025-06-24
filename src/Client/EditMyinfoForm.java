package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.EmpVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class EditMyinfoForm extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(EditMyinfoForm.class.getName());

    // 로그인한 사원의 모든 정보가 담긴 EmpVO를 LoginFrame 으로부터 받아올 변수 선언
    EmpVO vo;

    // 팩토리 생성
    SqlSessionFactory factory;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel att_l;
    private javax.swing.JPanel att_p;
    private javax.swing.JTextField att_tf;
    private javax.swing.JButton bt_cancel;
    private javax.swing.JButton bt_edit;
    private javax.swing.JPanel center_p;
    private javax.swing.JLabel dname_l;
    private javax.swing.JPanel dname_p;
    private javax.swing.JTextField dname_tf;
    private javax.swing.JLabel email_l;
    private javax.swing.JPanel email_p;
    private javax.swing.JTextField email_tf;
    private javax.swing.JLabel empno_l;
    private javax.swing.JPanel empno_p;
    private javax.swing.JTextField empno_tf;
    private javax.swing.JLabel ename_l;
    private javax.swing.JPanel ename_p;
    private javax.swing.JTextField ename_tf;
    private javax.swing.JLabel hiredate_l;
    private javax.swing.JPanel hiredate_p;
    private javax.swing.JTextField hiredate_tf;
    private javax.swing.JLabel mgr_l;
    private javax.swing.JPanel mgr_p;
    private javax.swing.JTextField mgr_tf;
    private javax.swing.JLabel phone_l;
    private javax.swing.JPanel phone_p;
    private javax.swing.JTextField phone_tf;
    private javax.swing.JLabel pos_l;
    private javax.swing.JPanel pos_p;
    private javax.swing.JTextField pos_tf;
    private javax.swing.JLabel sal_l;
    private javax.swing.JPanel sal_p;
    private javax.swing.JTextField sal_tf;
    private javax.swing.JPanel south_p;

    /**
     * Creates new form EditMyinfoForm
     */

    // LoginFrame 으로부터 로그인한 사원의 모든 정보를 받아오기 위해 기본 생성자에서 EmpVO 받기 (EmpVO vo)
    public EditMyinfoForm(UserFrame parent, boolean modal, EmpVO vo) {
        super(parent, modal);
        this.vo = vo; // LoginFrame 으로부터 받아온 vo를 앞서 선언한 변수에 저장

        initComponents();
        initdb();

        this.setBounds(770, 200, this.getWidth(), this.getHeight());

        // 내 정보 수정 창에 1차적으로 내 정보 설정하기
        SqlSession ss = factory.openSession();
        EmpVO evo = ss.selectOne("emp.getMyInfoEdit", this.vo.getEmpno());
        empno_tf.setText(evo.getEmpno());
        ename_tf.setText(evo.getEname());
        pos_tf.setText(evo.getPosname());
        dname_tf.setText(evo.getDname());
        sal_tf.setText(evo.getSal());
        hiredate_tf.setText(evo.getHireDATE());
        email_tf.setText(evo.getEmail());
        phone_tf.setText(evo.getPhone());
        att_tf.setText(evo.getAttend_status());
        mgr_tf.setText(evo.getMgr());

        // 수정 버튼을 눌렀을 때 수정 가능한 내 정보들의 텍스트 필드 값을 문자열 변수에 저장한 후
        // 위에서 사용했던 EmpVO evo를 재사용하여 필요한 정보들을 setter를 이용해 바꿔주고
        // 그 이후 업데이트 쿼리를 이용해서 DB에 업데이트 시킨다.
        bt_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ename = ename_tf.getText().trim();
                String email = email_tf.getText().trim();
                String phone = phone_tf.getText().trim();

                SqlSession ss = factory.openSession();

                evo.setEname(ename);
                evo.setEmail(email);
                evo.setPhone(phone);
                int cnt = ss.update("emp.editMyInfo", evo);

                // 변동 사항이 있을 경우에만 커밋을 통해 DB에 적용시키고
                // 실시간으로 내 정보 수정 창에서 변동된 사항을 바로 갱신시켜 보여준다.
                if (cnt != 0) {
                    ss.commit();
                    empno_tf.setText(evo.getEmpno());
                    ename_tf.setText(evo.getEname());
                    pos_tf.setText(evo.getPosname());
                    dname_tf.setText(evo.getDname());
                    sal_tf.setText(evo.getSal());
                    hiredate_tf.setText(evo.getHireDATE());
                    email_tf.setText(evo.getEmail());
                    phone_tf.setText(evo.getPhone());
                    att_tf.setText(evo.getAttend_status());
                    mgr_tf.setText(evo.getMgr());

                    parent.EditMyInfoTable(evo); // UserFrame의 테이블을 갱신시키기 위한 함수 호출
                    JOptionPane.showMessageDialog(EditMyinfoForm.this, "정보 수정이 완료됐습니다!");
                }
                else {
                    ss.rollback(); // 변동된 사항이 없을 경우 롤백시켜 취소한다.
                    JOptionPane.showMessageDialog(EditMyinfoForm.this, "수정된 값이 없습니다!");
                }

                ss.close();
            }
        });

        // 취소 버튼을 눌렀을 때 이 창을 닫기
        bt_cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditMyinfoForm.this.dispose();
            }
        });
    }

    // DB 연결 함수
    private void initdb(){
        try {
            Reader r = Resources.getResourceAsReader(
                    "config/conf.xml"
            );
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        south_p = new javax.swing.JPanel();
        bt_edit = new javax.swing.JButton();
        bt_cancel = new javax.swing.JButton();
        center_p = new javax.swing.JPanel();
        empno_p = new javax.swing.JPanel();
        empno_l = new javax.swing.JLabel();
        empno_tf = new javax.swing.JTextField();
        ename_p = new javax.swing.JPanel();
        ename_l = new javax.swing.JLabel();
        ename_tf = new javax.swing.JTextField();
        pos_p = new javax.swing.JPanel();
        pos_l = new javax.swing.JLabel();
        pos_tf = new javax.swing.JTextField();
        dname_p = new javax.swing.JPanel();
        dname_l = new javax.swing.JLabel();
        dname_tf = new javax.swing.JTextField();
        sal_p = new javax.swing.JPanel();
        sal_l = new javax.swing.JLabel();
        sal_tf = new javax.swing.JTextField();
        hiredate_p = new javax.swing.JPanel();
        hiredate_l = new javax.swing.JLabel();
        hiredate_tf = new javax.swing.JTextField();
        email_p = new javax.swing.JPanel();
        email_l = new javax.swing.JLabel();
        email_tf = new javax.swing.JTextField();
        phone_p = new javax.swing.JPanel();
        phone_l = new javax.swing.JLabel();
        phone_tf = new javax.swing.JTextField();
        att_p = new javax.swing.JPanel();
        att_l = new javax.swing.JLabel();
        att_tf = new javax.swing.JTextField();
        mgr_p = new javax.swing.JPanel();
        mgr_l = new javax.swing.JLabel();
        mgr_tf = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("내 정보 수정");

        south_p.setPreferredSize(new java.awt.Dimension(355, 50));
        south_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 12));

        bt_edit.setText("수정");
        south_p.add(bt_edit);

        bt_cancel.setText("취소");
        south_p.add(bt_cancel);

        getContentPane().add(south_p, java.awt.BorderLayout.PAGE_END);

        center_p.setLayout(new java.awt.GridLayout(10, 1));

        empno_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        empno_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        empno_l.setText("사원 번호 :");
        empno_p.add(empno_l);

        empno_tf.setEditable(false);
        empno_tf.setColumns(8);
        empno_p.add(empno_tf);

        center_p.add(empno_p);

        ename_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        ename_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        ename_l.setText("사원 이름 :");
        ename_p.add(ename_l);

        ename_tf.setColumns(8);
        ename_p.add(ename_tf);

        center_p.add(ename_p);

        pos_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        pos_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        pos_l.setText("직급 :");
        pos_p.add(pos_l);

        pos_tf.setEditable(false);
        pos_tf.setColumns(8);
        pos_p.add(pos_tf);

        center_p.add(pos_p);

        dname_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        dname_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        dname_l.setText("부서 :");
        dname_p.add(dname_l);

        dname_tf.setEditable(false);
        dname_tf.setColumns(8);
        dname_p.add(dname_tf);

        center_p.add(dname_p);

        sal_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        sal_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        sal_l.setText("급여 :");
        sal_p.add(sal_l);

        sal_tf.setEditable(false);
        sal_tf.setColumns(8);
        sal_p.add(sal_tf);

        center_p.add(sal_p);

        hiredate_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        hiredate_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        hiredate_l.setText("입사일 :");
        hiredate_p.add(hiredate_l);

        hiredate_tf.setEditable(false);
        hiredate_tf.setColumns(8);
        hiredate_p.add(hiredate_tf);

        center_p.add(hiredate_p);

        email_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        email_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        email_l.setText("이메일 :");
        email_p.add(email_l);

        email_tf.setColumns(18);
        email_p.add(email_tf);

        center_p.add(email_p);

        phone_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        phone_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        phone_l.setText("연락처 :");
        phone_p.add(phone_l);

        phone_tf.setColumns(8);
        phone_p.add(phone_tf);

        center_p.add(phone_p);

        att_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        att_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        att_l.setText("재직 상태 :");
        att_p.add(att_l);

        att_tf.setEditable(false);
        att_tf.setColumns(8);
        att_p.add(att_tf);

        center_p.add(att_p);

        mgr_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));
        mgr_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 12));

        mgr_l.setText("관리자 번호 :");
        mgr_p.add(mgr_l);

        mgr_tf.setEditable(false);
        mgr_tf.setColumns(8);
        mgr_p.add(mgr_tf);

        center_p.add(mgr_p);

        getContentPane().add(center_p, java.awt.BorderLayout.CENTER);

        pack();
    }
}