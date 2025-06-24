/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.CommuteVO;
import vo.EmpVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zhfja
 */
public class WorkInOut extends JFrame {

    // 변수 선언
    private JButton bt_in;
    private JButton bt_out;
    private JLabel inOutImage_l;
    private JPanel north_p;
    private String user_name;
    private String loginedEmpno; // 로그인한 사번을 저장할 변수
    private int status; // handleClockOut에서 쓰일 status값 저장소

    //DB관련 변수
    SqlSessionFactory factory;
    SqlSession ss;

    // 변수 선언 끝

    //기본 생성자
    public WorkInOut(UserFrame userFrame) {
        //UseerFrame에 있는 enmpno를 가져 오기 위해 변수를 선언한다.
        user_name = userFrame.vo.getEname();
        loginedEmpno = userFrame.vo.getEmpno();
        initComponents();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        System.out.println(userFrame.vo.getEmpno());
        System.out.println(user_name);

        initDB();

        //출근 버튼 이벤트
        bt_in.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClockIn();
                dispose();
            }

        });
        // 퇴근 버튼 이벤트
        bt_out.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleClockOut();
                dispose();
            }

        });
    }//기본 생성자의 끝

    public void handleClockIn() {
        LocalTime now = LocalTime.now(); // LocalTime -> java에서 현재 시간 가져오는 함수
        LocalTime lateTime = LocalTime.of(9, 10); // 지각 기준 시간 (9시 10분)

        JOptionPane.showMessageDialog(WorkInOut.this,
                user_name+"님 출근을 환영합니다.");

        Map<String, String> map = new HashMap<>();
        map.put("empno", loginedEmpno);

        if (now.isAfter(lateTime)) {

            map.put("status", "5");
            map.put("note", "지각");

        } else {
            map.put("status", "0");
            map.put("note", "출근");
        }

        SqlSession ss = null; // SqlSession 변수를 try 블록 바깥에 선언

        try {
            ss = factory.openSession(); // factory는 SqlSessionFactory 객체로 가정
            ss.insert("commute.chkin", map); // INSERT 실행
            ss.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close(); // 세션 닫기
    }


    private void handleClockOut() {
        JOptionPane.showMessageDialog(WorkInOut.this,
                user_name+"님 오늘하루도 고생하셨습니다.");

        try {
            ss = factory.openSession();
            status = ss.selectOne("commute.getStatus", loginedEmpno); // empno로 status 조회
            System.out.println(status);
        }catch (Exception e){
            e.printStackTrace();
        }

        Map<String, String> map = new HashMap<>();
        map.put("empno", loginedEmpno);
        map.put("status", "1");

        if (status == 5) {
            map.put("note", "지각");
        } else if (status == 0) {
            map.put("note", "정상 퇴근");
        }

        SqlSession ss = null; // SqlSession 변수를 try 블록 바깥에 선언

        try {
            ss = factory.openSession();
            ss.update("commute.chkout", map);
            ss.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close(); // 세션 닫기

    }

    private void initDB() {
        try {
            Reader r = Resources.getResourceAsReader("config/conf.xml"); // MyBatis 설정 파일 경로
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();

            System.out.println("DB연결 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initComponents() {

        north_p = new JPanel();
        bt_in = new JButton();
        bt_out = new JButton();
        inOutImage_l = new JLabel();
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/outImage.jpg"));
        Image img = icon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        inOutImage_l.setIcon(new ImageIcon(img));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("출 / 퇴근");

        north_p.setPreferredSize(new java.awt.Dimension(364, 150));
        north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        bt_in.setText("출근");
        north_p.add(bt_in);

        bt_out.setText("퇴근");
        north_p.add(bt_out);

        getContentPane().add(north_p, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(inOutImage_l, java.awt.BorderLayout.CENTER);

        pack();
        this.setVisible(true);
    }

}
