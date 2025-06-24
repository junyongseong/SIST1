package Client;

import vo.EmpVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.CommuteVO;
import vo.Leave_historyVO;
import vo.Leave_ofVO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(UserFrame.class.getName());

    Double used;

    //공통 맴버 변수
    CardLayout cl;
    SqlSessionFactory factory;
    List<CommuteVO> commuteList;
    SqlSession ss;

    //근태 상태 맴버 변수
    String[] date_name = {"사번", "날짜", "출근", "퇴근", "상태"};
    String[][] chk;

    // 로그인한 사원의 모든 정보를 LoginFrame 으로부터 받아올 변수 선언
    EmpVO vo;
    String ename; // 사원의 이름을 얻어내기 위한 문자열 변수 선언

    // 내 정보 테이블을 갱신하기 위해 사용할 2차원 오브젝트 배열과 1차원 문자열 배열 선언
    Object[][] myinfo;
    String[] myinfo_cname = {"사번", "이름", "직급", "부서", "급여", "연락처", "이메일", "입사일"};
    // 사원 조회 검색 테이블을 갱신할 때 사용할 2차원 배열과 1차원 배열 선언
    Object[][] searchInfo;
    String[] searchInfo_cname = {"사번", "이름", "직급", "부서", "전화번호", "이메일", "입사일"};

    // 휴가 히스토리
    Leave_historyVO lhvo;
    int year;
    double total;
    double remin;
    List<Leave_historyVO> history_List;
    List<Leave_ofVO> Leave_info ;
    List<Leave_ofVO> Leave_now;
    String[] vac_colum = { "휴가 항목", "휴가 기간", "남은 휴가", "신청 날짜", "결재 상태"};
    Object[][] vac_info;

    //기본 생성자
    public UserFrame(EmpVO vo) { // LoginFrame 으로부터 로그인한 사원의 모든 정보를 받기 위해 기본 생성자에서 EmpVO 받기
        // 위에서 선언한 변수를 이용해 로그인한 사원의 이름을 얻어 프레임 제목에 환영문구 띄우기
        ename = vo.getEname();
        setTitle(ename+"님 환영합니다!");

        this.vo = vo; // LoginFrame 으로부터 받아온 vo를 앞서 선언한 vo에 저장

        cl = new CardLayout(); // 앞서 선언했던 카드레이아웃 생성

        // MyBatis 초기화
        initDB();

        // 창 구성
        initComponents();

        // 창 열릴 때 위치 조정
        this.setBounds(410, 130, this.getWidth(), this.getHeight());

        // LoginFrame 으로부터 받아온 vo와 emp 매퍼의 getMyInfo 쿼리를 이용해 내 정보 테이블 갱신하기
        if (this.vo != null) {
            ss = factory.openSession();
            List<EmpVO> list = ss.selectList("emp.getMyInfo", this.vo.getEmpno());
            myinfo = new Object[list.size()][myinfo_cname.length];
            int i = 0;
            for (EmpVO evo : list) {
                myinfo[i][0] = evo.getEmpno();
                myinfo[i][1] = evo.getEname();
                myinfo[i][2] = evo.getPosname();
                myinfo[i][3] = evo.getDname();
                myinfo[i][4] = evo.getSal();
                myinfo[i][5] = evo.getPhone();
                myinfo[i][6] = evo.getEmail();
                myinfo[i][7] = evo.getHireDATE();
            }
            table_myInfo.setModel(new DefaultTableModel(myinfo, myinfo_cname));
            ss.close();
        } else {
            // 사번이 넘어오지 않는 경우 에러 출력
            JOptionPane.showMessageDialog(this, "사번이 확인되지 않습니다!");
        }

        // 홈 패널 중앙에 들어갈 이미지 설정
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/empOffice.png"));
        Image img = icon.getImage().getScaledInstance(750, 580, Image.SCALE_SMOOTH);
        homeImage_l.setIcon(new ImageIcon(img));

        // 내 정보 테이블의 컬럼들의 열 간격 조정
        table_myInfo.getColumnModel().getColumn(0).setPreferredWidth(50);   // 사번
        table_myInfo.getColumnModel().getColumn(1).setPreferredWidth(80);   // 이름
        table_myInfo.getColumnModel().getColumn(2).setPreferredWidth(100);  // 직급
        table_myInfo.getColumnModel().getColumn(3).setPreferredWidth(120);  // 부서
        table_myInfo.getColumnModel().getColumn(4).setPreferredWidth(60);   // 급여
        table_myInfo.getColumnModel().getColumn(5).setPreferredWidth(120);  // 연락처
        table_myInfo.getColumnModel().getColumn(6).setPreferredWidth(150);  // 이메일
        table_myInfo.getColumnModel().getColumn(7).setPreferredWidth(100);  // 입사일

        // 홈 버튼 눌렀을 때 화면 변경
        bt_home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "homeCard");
            }
        });

        // 내 정보 버튼 눌렀을 때 화면 변경
        bt_myInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "myInfoCard");
            }
        });

        // 사원 조회 버튼 눌렀을 때 화면 변경
        bt_searchEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "searchEmpCard");
            }
        });

        // 내 정보 - 내 정보 수정 버튼 눌렀을 때 창 띄우기
        bt_editMyInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditMyinfoForm(UserFrame.this, true, UserFrame.this.vo).setVisible(true);
            }
        });

        // 사원 조회 - 검색 버튼 눌렀을 때 수행
        // 콤보박스에 무엇이 선택됐는지와 검색창에 무엇이 입력되었는지를 알아내고
        // 콤보박스 값에 따라서 조건식에 맞는 사원들의 정보를 리스트에 담아서
        // 사원 조회 테이블에 갱신시킨다
        bt_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cnt = search_cbox.getSelectedIndex(); // 콤보박스에서 선택된 인덱스값 얻어내기
                String str = value_tf.getText().trim(); // 검색창 텍스트필드에 입력된값 얻기

                int i = 0; // 스위치문 안의 반복문에서 사용할 증가용 정수 선언
                List<EmpVO> list; // 스위치문에서 사용할 EmpVO를 자료형으로 받는 리스트 선언

                if (str.isEmpty() == false) {
                    switch (cnt) { // 콤보박스에서 선택된 인덱스값이 무엇인지에 따라서 스위치문 진행
                        case 0:
                            ss = factory.openSession();
                            list = ss.selectList("searchEmp.searchEmpno", str);
                            searchInfo = new Object[list.size()][searchInfo_cname.length];

                            i = 0;
                            for (EmpVO vo : list) {
                                searchInfo[i][0] = vo.getEmpno();
                                searchInfo[i][1] = vo.getEname();
                                searchInfo[i][2] = vo.getPosname();
                                searchInfo[i][3] = vo.getDname();
                                searchInfo[i][4] = vo.getPhone();
                                searchInfo[i][5] = vo.getEmail();
                                searchInfo[i][6] = vo.getHireDATE();
                                i++;
                            }
                            table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
                            ss.close();
                            break;
                        case 1:
                            ss = factory.openSession();
                            list = ss.selectList("searchEmp.searchEname", str);
                            searchInfo = new Object[list.size()][searchInfo_cname.length];

                            i = 0;
                            for (EmpVO vo : list) {
                                searchInfo[i][0] = vo.getEmpno();
                                searchInfo[i][1] = vo.getEname();
                                searchInfo[i][2] = vo.getPosname();
                                searchInfo[i][3] = vo.getDname();
                                searchInfo[i][4] = vo.getPhone();
                                searchInfo[i][5] = vo.getEmail();
                                searchInfo[i][6] = vo.getHireDATE();
                                i++;
                            }
                            table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
                            ss.close();
                            break;
                        case 2:
                            ss = factory.openSession();
                            list = ss.selectList("searchEmp.searchPos", str);
                            searchInfo = new Object[list.size()][searchInfo_cname.length];

                            i = 0;
                            for (EmpVO vo : list) {
                                searchInfo[i][0] = vo.getEmpno();
                                searchInfo[i][1] = vo.getEname();
                                searchInfo[i][2] = vo.getPosname();
                                searchInfo[i][3] = vo.getDname();
                                searchInfo[i][4] = vo.getPhone();
                                searchInfo[i][5] = vo.getEmail();
                                searchInfo[i][6] = vo.getHireDATE();
                                i++;
                            }
                            table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
                            ss.close();
                            break;
                        case 3:
                            ss = factory.openSession();
                            list = ss.selectList("searchEmp.searchEmp", str);
                            searchInfo = new Object[list.size()][searchInfo_cname.length];

                            i = 0;
                            for (EmpVO vo : list) {
                                searchInfo[i][0] = vo.getEmpno();
                                searchInfo[i][1] = vo.getEname();
                                searchInfo[i][2] = vo.getPosname();
                                searchInfo[i][3] = vo.getDname();
                                searchInfo[i][4] = vo.getPhone();
                                searchInfo[i][5] = vo.getEmail();
                                searchInfo[i][6] = vo.getHireDATE();
                                i++;
                            }
                            table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
                            ss.close();
                            break;
                        case 4:
                            ss = factory.openSession();
                            list = ss.selectList("searchEmp.searchPhone", str);
                            searchInfo = new Object[list.size()][searchInfo_cname.length];

                            i = 0;
                            for (EmpVO vo : list) {
                                searchInfo[i][0] = vo.getEmpno();
                                searchInfo[i][1] = vo.getEname();
                                searchInfo[i][2] = vo.getPosname();
                                searchInfo[i][3] = vo.getDname();
                                searchInfo[i][4] = vo.getPhone();
                                searchInfo[i][5] = vo.getEmail();
                                searchInfo[i][6] = vo.getHireDATE();
                                i++;
                            }
                            table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
                            ss.close();
                            break;
                        case 5:
                            ss = factory.openSession();
                            list = ss.selectList("searchEmp.searchEmail", str);
                            searchInfo = new Object[list.size()][searchInfo_cname.length];

                            i = 0;
                            for (EmpVO vo : list) {
                                searchInfo[i][0] = vo.getEmpno();
                                searchInfo[i][1] = vo.getEname();
                                searchInfo[i][2] = vo.getPosname();
                                searchInfo[i][3] = vo.getDname();
                                searchInfo[i][4] = vo.getPhone();
                                searchInfo[i][5] = vo.getEmail();
                                searchInfo[i][6] = vo.getHireDATE();
                                i++;
                            }
                            table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
                            ss.close();
                            break;
                        case 6:
                            ss = factory.openSession();
                            list = ss.selectList("searchEmp.searchHiredate", str);
                            searchInfo = new Object[list.size()][searchInfo_cname.length];

                            i = 0;
                            for (EmpVO vo : list) {
                                searchInfo[i][0] = vo.getEmpno();
                                searchInfo[i][1] = vo.getEname();
                                searchInfo[i][2] = vo.getPosname();
                                searchInfo[i][3] = vo.getDname();
                                searchInfo[i][4] = vo.getPhone();
                                searchInfo[i][5] = vo.getEmail();
                                searchInfo[i][6] = vo.getHireDATE();
                                i++;
                            }
                            table_emp.setModel(new DefaultTableModel(searchInfo, searchInfo_cname));
                            ss.close();
                            break;
                    }
                } else {
                    JOptionPane.showMessageDialog(UserFrame.this, "값을 입력하세요");
                }
            }
        });

        // 업무 일지 버튼 눌렀을 때 화면 변경
        bt_workLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p,"workLogCard");
            }
        });

        // 업무 일지 - 업무일지 작성 버튼 눌렀을 때 창 띄우기
        bt_workLogWrite.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new docs(vo);
            }
        });

        // 나의 근태정보 버튼 눌렀을 때 화면 변경
        bt_myAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "myAttCard");
                All_searchAttendance();
            }
        });

        // "나의 근태정보" 패널의 "조회" 버튼에 대한 ActionListener 추가
        bt_find.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAttendance();
            }
        });

        // 나의 휴가정보 버튼 눌렀을 때 화면 변경
        bt_myVac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(UserFrame.this.centerCard_p, "myVacCard");
                nowVac();
                setLabel();
            }
        });

        // 관리자 모드 버튼 눌렀을 시 관리자 인증 진행한 후
        // 권한번호가 일정 번호라면 인증 성공해서 창 닫고 AdminFrame 열기
        // 일정 번호가 안 된다면 인증 실패해서 메세지 띄우기
        bt_adminMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (vo.getRole_num().equals("3") || vo.getRole_num().equals("2")){
                    UserFrame.this.dispose();

                    try {
                        new AdminFrame(vo);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

                } else {
                    JOptionPane.showMessageDialog(UserFrame.this, "권한이 없습니다!");
                }
            }
        });

        // 출퇴근 버튼
        bt_workInOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            WorkInOut workInOutWindow = new WorkInOut(UserFrame.this);
            }

        });

        // 로그아웃 버튼 누를 시 창이 닫히고 LoginFrame 열기
        bt_logOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserFrame.this.dispose();

                new LoginFrame().setVisible(true);
            }
        });

        // 종료 버튼 누를 시 UserFrame 종료
        bt_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserFrame.this.dispose();
            }
        });
    } // 생성자의 끝

    // 내 정보 수정 창에서 사원 정보를 수정했을 때 내 정보 테이블 갱신하는 함수
    public void EditMyInfoTable(EmpVO vo){
        ss = factory.openSession();
        List<EmpVO> list = ss.selectList("emp.getMyInfo", this.vo.getEmpno());
        myinfo = new Object[list.size()][myinfo_cname.length];
        int i = 0;
        for (EmpVO evo : list) {
            myinfo[i][0] = evo.getEmpno();
            myinfo[i][1] = evo.getEname();
            myinfo[i][2] = evo.getPosname();
            myinfo[i][3] = evo.getDname();
            myinfo[i][4] = evo.getSal();
            myinfo[i][5] = evo.getPhone();
            myinfo[i][6] = evo.getEmail();
            myinfo[i][7] = evo.getHireDATE();
        }
        table_myInfo.setModel(new DefaultTableModel(myinfo, myinfo_cname));
        table_myInfo.getColumnModel().getColumn(0).setPreferredWidth(50);   // 사번
        table_myInfo.getColumnModel().getColumn(1).setPreferredWidth(80);   // 이름
        table_myInfo.getColumnModel().getColumn(2).setPreferredWidth(100);  // 직급
        table_myInfo.getColumnModel().getColumn(3).setPreferredWidth(120);  // 부서
        table_myInfo.getColumnModel().getColumn(4).setPreferredWidth(60);   // 급여
        table_myInfo.getColumnModel().getColumn(5).setPreferredWidth(120);  // 연락처
        table_myInfo.getColumnModel().getColumn(6).setPreferredWidth(150);  // 이메일
        table_myInfo.getColumnModel().getColumn(7).setPreferredWidth(100);  // 입사일
        ss.close();
    }

    // 휴가 상태 레이블 설정하는 함수
    private void setLabel() {
        LocalDate now = LocalDate.now();
        year = now.getYear();
        year_cb.setFont(new Font("나눔 고딕", Font.PLAIN, 15));

        ss = factory.openSession();
        try {
            Map<String, Object> remain_Vac_map = new HashMap<>();
            remain_Vac_map.put("empno", vo.getEmpno());
            remain_Vac_map.put("year", year);

            lhvo = ss.selectOne("history.remain_Vac", remain_Vac_map);
            allVac_l.setText("총 휴가 :" + lhvo.getTotal_leave());

            remainVac_l.setText("남은 휴가 :" + lhvo.getRemain_leave());

            // 사용 휴가 계산
            double total = Double.parseDouble(lhvo.getTotal_leave());
            double remain = Double.parseDouble(lhvo.getRemain_leave());
            used = total - remain;

            usedVac_l.setText("사용 휴가 :" + used);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ss != null) ss.close();
        }
        year_cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                vacTable();
            }
        });
    }

    // (함수 설명)
    private void nowVac(){
        String selectedYear = (String) year_cb.getSelectedItem();
        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
        // map.put("year", selectedYear);

        ss = null; // SqlSession 초기화
        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            Leave_info = ss.selectList("leave_of.vac_search", map); //
            viewVacTable(Leave_info); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();
    }

    // 휴가 상태 테이블 설정하는 함수
    private void vacTable() {
        String selectedYear = (String) year_cb.getSelectedItem();
        System.out.println(selectedYear);

        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
        map.put("year", selectedYear);

        ss = null; // SqlSession 초기화
        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            Leave_info = ss.selectList("leave.yearsSearch", map); //
            viewVacTable(Leave_info); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();
    }

    // 휴가 테이블 갱신시켜 보여주는 함수
    private void viewVacTable(List<Leave_ofVO> list) {
        vac_info = new Object[list.size()][vac_colum.length];
        int i = 0;
        for (Leave_ofVO vo : list) {
            vac_info[i][0] = vo.getLname();
            vac_info[i][1] = vo.getLdate();
            vac_info[i][2] = vo.getDuration();
            vac_info[i][3] = vo.getLprocessed();
            switch (vo.getLstatus()){
                case "0":
                    vac_info[i][4] = "신청";
                    break;
                case "1" :
                    vac_info[i][4] = "승인";
                    break;
                case "2" :
                    vac_info[i][4] = "반려";

            }
            // vac_info[i][4] = vo.getLstatus();
            i++;
        } // for문 종료
        vacTable.setModel(new DefaultTableModel(vac_info,vac_colum));
        vacTable.setDefaultEditor(Object.class, null); // 셀 편집 비활성화 하는 기능
    }

    // 근태 조회(검색) 함수
    private void searchAttendance() {
        String selectedYear = (String) year_cb.getSelectedItem();
        String selectedMonth = (String) month_cb.getSelectedItem();

        if (selectedYear == null || selectedMonth == null) {
            JOptionPane.showMessageDialog(this, "조회할 연도와 월을 선택해주세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
        map.put("year", selectedYear);
        map.put("month", selectedMonth);

        ss = null; // SqlSession 초기화
        try {
            ss = factory.openSession();
            // 로그인한 사번의 근태 조회
            commuteList = ss.selectList("commute.searchByYearMonth", map); //
            viewAttendanceTable(commuteList); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();
    } // searchAttendance 함수 종료

    // 나의 전체 년도 월 조회
    private void All_searchAttendance() {

        Map<String, String> map = new HashMap<>();
        map.put("empno", vo.getEmpno());
        // map.put("year", selectedYear);
        // map.put("month", selectedMonth);

        ss = null; // SqlSession 초기화
        try {
            ss = factory.openSession();
            commuteList = ss.selectList("commute.login", map); //
            viewAttendanceTable(commuteList); // 이 메소드는 JTable을 업데이트합니다.
        } catch (Exception e) {
            e.printStackTrace();
        }
        ss.close();

    } // All_searchAttendanece 종료

    // 근태 테이블 갱신시켜 보여주는 함수
    private void viewAttendanceTable(List<CommuteVO> list) {

        // 인자로 받은 List 구조를 2차원 배열로 변환한 후 JTable에 표현!
        chk = new String[list.size()][date_name.length];
        int i = 0;
        for (CommuteVO vo : list) {
            chk[i][0] = vo.getEmpno();
            chk[i][1] = vo.getDate();
            chk[i][2] = vo.getChkin();
            chk[i][3] = vo.getChkout();
            chk[i][4] = vo.getAttend_note();

            i++;
        } // for문 종료
        attTable.setModel(new DefaultTableModel(chk, date_name));
        attTable.setDefaultEditor(Object.class, null); // 셀 편집 비활성화 하는 기능
    }

    // DB 연결하는 함수 (한 번만 수행)
    private void initDB() {
        try {
            Reader r = Resources.getResourceAsReader("config/conf.xml"); // MyBatis 설정 파일 경로
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();

            System.out.println("DB 연결 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        workLogCenter_p = new javax.swing.JPanel();
        allVac_l = new javax.swing.JLabel();
        myVac_south_p = new javax.swing.JPanel();
        south_p = new javax.swing.JPanel();
        bt_logOut = new javax.swing.JButton();
        bt_exit = new javax.swing.JButton();
        west_p = new javax.swing.JPanel();
        northImage_l = new javax.swing.JLabel();
        bt_home = new javax.swing.JButton();
        bt_myInfo = new javax.swing.JButton();
        bt_searchEmp = new javax.swing.JButton();
        bt_workLog = new javax.swing.JButton();
        bt_myAtt = new javax.swing.JButton();
        bt_myVac = new javax.swing.JButton();
        bt_adminMode = new javax.swing.JButton();
        bt_workInOut = new javax.swing.JButton();
        center_p = new javax.swing.JPanel();
        centerNorth_p = new javax.swing.JPanel();
        centerCard_p = new javax.swing.JPanel();
        home_p = new javax.swing.JPanel();
        homeImage_l = new javax.swing.JLabel();
        myInfo_p = new javax.swing.JPanel();
        myInfo_north_p = new javax.swing.JPanel();
        bt_editMyInfo = new javax.swing.JButton();
        jsp_myInfo = new javax.swing.JScrollPane();
        table_myInfo = new javax.swing.JTable();
        searchEmp_p = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        search_l = new javax.swing.JLabel();
        search_cbox = new javax.swing.JComboBox<>();
        value_l = new javax.swing.JLabel();
        value_tf = new javax.swing.JTextField();
        empty_p = new javax.swing.JPanel();
        bt_search = new javax.swing.JButton();
        jsp_empTable = new javax.swing.JScrollPane();
        table_emp = new javax.swing.JTable();
        workLog_p = new javax.swing.JPanel();
        workLog_north_p = new javax.swing.JPanel();
        bt_workLogWrite = new javax.swing.JButton();
        bt_myList = new javax.swing.JButton();
        bt_dept = new javax.swing.JButton();
        jsp_logList = new javax.swing.JScrollPane();
        logList = new javax.swing.JList<>();
        myAtt_p = new javax.swing.JPanel();
        myAtt_north_p = new javax.swing.JPanel();
        year_cb = new javax.swing.JComboBox<>();
        month_cb = new javax.swing.JComboBox<>();
        bt_find = new javax.swing.JButton();
        jsp_attTable = new javax.swing.JScrollPane();
        attTable = new javax.swing.JTable();
        myVac_p = new javax.swing.JPanel();
        myVac_north_p = new javax.swing.JPanel();
        usedVac_l = new javax.swing.JLabel();
        remainVac_l = new javax.swing.JLabel();
        bt_addVac = new javax.swing.JButton();
        jsp_vacTable = new javax.swing.JScrollPane();
        vacTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1000, 1000));
        setMinimumSize(new java.awt.Dimension(100, 100));

        south_p.setPreferredSize(new java.awt.Dimension(884, 80));
        south_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 30, 30));

        bt_logOut.setText("로그아웃");
        south_p.add(bt_logOut);

        bt_exit.setText("종료");
        south_p.add(bt_exit);

        getContentPane().add(south_p, java.awt.BorderLayout.PAGE_END);

        west_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 30, 1, 30));
        west_p.setPreferredSize(new java.awt.Dimension(300, 591));
        west_p.setLayout(new java.awt.GridLayout(9, 1, 0, 15));

        northImage_l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/sist.png"));
        Image img = icon.getImage().getScaledInstance(240, 60, Image.SCALE_SMOOTH);
        northImage_l.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 1, 1, 1));
        northImage_l.setIcon(new ImageIcon(img));
        west_p.add(northImage_l);

        bt_home.setText("홈");
        west_p.add(bt_home);

        bt_myInfo.setText("내 정보");
        west_p.add(bt_myInfo);

        bt_searchEmp.setText("사원 조회");
        west_p.add(bt_searchEmp);

        bt_workLog.setText("업무 일지");
        west_p.add(bt_workLog);

        bt_myAtt.setText("나의 근태정보");
        west_p.add(bt_myAtt);

        bt_myVac.setText("나의 휴가정보");
        west_p.add(bt_myVac);

        bt_adminMode.setText("관리자 모드");
        west_p.add(bt_adminMode);

        bt_workInOut.setText("출 / 퇴근");
        west_p.add(bt_workInOut);

        getContentPane().add(west_p, java.awt.BorderLayout.LINE_START);

        center_p.setLayout(new java.awt.BorderLayout());

        centerNorth_p.setPreferredSize(new java.awt.Dimension(606, 70));

        javax.swing.GroupLayout centerNorth_pLayout = new javax.swing.GroupLayout(centerNorth_p);
        centerNorth_p.setLayout(centerNorth_pLayout);
        centerNorth_pLayout.setHorizontalGroup(
            centerNorth_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
        );
        centerNorth_pLayout.setVerticalGroup(
            centerNorth_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 70, Short.MAX_VALUE)
        );

        center_p.add(centerNorth_p, java.awt.BorderLayout.PAGE_START);

        // 카드레이아웃 지정
        centerCard_p.setLayout(cl);

        home_p.setLayout(new java.awt.BorderLayout());

        homeImage_l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        homeImage_l.setPreferredSize(new java.awt.Dimension(400, 400));
        home_p.add(homeImage_l, java.awt.BorderLayout.CENTER);

        centerCard_p.add(home_p, "homeCard");

        // 내 정보 패널 설정
        myInfo_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myInfo_p.setLayout(new java.awt.BorderLayout());
        myInfo_north_p.setPreferredSize(new java.awt.Dimension(606, 50));
        myInfo_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 30, 12));

        // 내 정보 - 내 정보 수정 설정
        bt_editMyInfo.setText("내 정보 수정");
        myInfo_north_p.add(bt_editMyInfo);
        myInfo_p.add(myInfo_north_p, java.awt.BorderLayout.NORTH);
        table_myInfo.setModel(new DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null, null}
                },
                new String [] {
                        "사번", "이름", "직급", "부서", "급여", "연락처", "이메일", "입사일"
                }
        ) {
            Class[] types = new Class [] {
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table_myInfo.setDefaultEditor(Object.class, null);
        jsp_myInfo.setViewportView(table_myInfo);
        myInfo_p.add(jsp_myInfo, java.awt.BorderLayout.CENTER);
        centerCard_p.add(myInfo_p, "myInfoCard");

        // 사원 조회 패널 설정
        searchEmp_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        searchEmp_p.setLayout(new java.awt.BorderLayout());
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 1));
        jPanel1.setLayout(new java.awt.GridLayout(3, 2, 0, 5));
        search_l.setText("검색 필드 :");
        jPanel1.add(search_l);
        search_cbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "사원 번호", "이름", "직급", "부서", "전화번호", "이메일", "입사일" }));
        jPanel1.add(search_cbox);
        value_l.setText("값 입력 :");
        jPanel1.add(value_l);
        jPanel1.add(value_tf);
        empty_p.setPreferredSize(new java.awt.Dimension(391, 10));
        javax.swing.GroupLayout empty_pLayout = new javax.swing.GroupLayout(empty_p);
        empty_p.setLayout(empty_pLayout);
        empty_pLayout.setHorizontalGroup(
            empty_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 374, Short.MAX_VALUE)
        );
        empty_pLayout.setVerticalGroup(
            empty_pLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 23, Short.MAX_VALUE)
        );
        jPanel1.add(empty_p);
        bt_search.setText("검색");
        jPanel1.add(bt_search);
        searchEmp_p.add(jPanel1, java.awt.BorderLayout.PAGE_START);
        table_emp.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "사원 번호", "이름", "직급", "부서", "전화번호", "이메일", "입사일"
            }
        ) {
            Class[] types = new Class [] {
                String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table_emp.setDefaultEditor(Object.class, null);
        jsp_empTable.setViewportView(table_emp);
        searchEmp_p.add(jsp_empTable, java.awt.BorderLayout.CENTER);
        centerCard_p.add(searchEmp_p, "searchEmpCard");

        // 업무 일지 패널 설정
        workLog_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        workLog_p.setLayout(new java.awt.BorderLayout());

        workLog_north_p.setPreferredSize(new java.awt.Dimension(782, 40));
        workLog_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 8));

        workLogCenter_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(70, 70, 70, 70));
        workLogCenter_p.setLayout(new java.awt.GridLayout(3, 1, 0, 60));

        bt_workLogWrite.setText("업무일지 작성");
        workLogCenter_p.add(bt_workLogWrite);

        bt_myList.setText("내 목록 조회");
        workLogCenter_p.add(bt_myList);

        bt_dept.setText("부서 조회");
        workLogCenter_p.add(bt_dept);

        workLog_p.add(workLogCenter_p, java.awt.BorderLayout.CENTER);

        workLog_p.add(workLog_north_p, java.awt.BorderLayout.PAGE_START);

        jsp_logList.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 10));

        logList.setPreferredSize(new java.awt.Dimension(300, 95));
        jsp_logList.setViewportView(logList);

        workLog_p.add(jsp_logList, java.awt.BorderLayout.LINE_START);

        centerCard_p.add(workLog_p, "workLogCard");

        // 나의 근태정보 패널 설정
        myAtt_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myAtt_p.setLayout(new java.awt.BorderLayout());

        myAtt_north_p.setPreferredSize(new java.awt.Dimension(782, 50));
        myAtt_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 15));

        year_cb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025" }));
        myAtt_north_p.add(year_cb);

        month_cb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" }));
        myAtt_north_p.add(month_cb);

        bt_find.setText("조회");
        myAtt_north_p.add(bt_find);

        myAtt_p.add(myAtt_north_p, java.awt.BorderLayout.PAGE_START);

        jsp_attTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 130, 1, 130));

        attTable.setModel(new DefaultTableModel(
                chk,date_name

        )

        {
            Class[] types = new Class [] {
                String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });

        jsp_attTable.setViewportView(attTable);


        myAtt_p.add(jsp_attTable, java.awt.BorderLayout.CENTER);
        centerCard_p.add(myAtt_p, "myAttCard");
        
        // 나의 휴가정보 패널 설정
        myVac_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 30));
        myVac_p.setLayout(new java.awt.BorderLayout());

        myVac_north_p.setPreferredSize(new java.awt.Dimension(782, 50));
        myVac_north_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 15));

        allVac_l.setBackground(new java.awt.Color(14, 180, 252));
        allVac_l.setHorizontalAlignment(SwingConstants.CENTER); // 수평 가운데 정렬
        allVac_l.setVerticalAlignment(SwingConstants.CENTER);   // 수직 가운데 정렬 추가
        allVac_l.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(14, 180, 252)));
        allVac_l.setOpaque(true);
        allVac_l.setPreferredSize(new java.awt.Dimension(120, 30));
        Font all_labelFont = new Font("맑은 고딕", Font.BOLD, 15);
        allVac_l.setFont(all_labelFont);

        year_cb = new JComboBox<>(new String[]{"2025", "2024", "2023", "2022"});
        myVac_north_p.add(year_cb);
        myVac_north_p.add(new JLabel("년"));
        myVac_north_p.add(allVac_l);

        usedVac_l.setBackground(new java.awt.Color(179, 110, 232));
        usedVac_l.setHorizontalAlignment(SwingConstants.CENTER);
        usedVac_l.setBorder(BorderFactory.createLineBorder(new java.awt.Color(179, 110, 232)));
        usedVac_l.setOpaque(true);
        usedVac_l.setPreferredSize(new java.awt.Dimension(120, 30));
        Font use_labelFont = new Font("맑은 고딕", Font.BOLD, 15);
        usedVac_l.setFont(use_labelFont);
        myVac_north_p.add(usedVac_l);

        remainVac_l.setBackground(new java.awt.Color(252, 205, 14));
        remainVac_l.setHorizontalAlignment(SwingConstants.CENTER);
        remainVac_l.setBorder(BorderFactory.createLineBorder(new java.awt.Color(252, 205, 14)));
        remainVac_l.setOpaque(true);
        remainVac_l.setPreferredSize(new java.awt.Dimension(120, 30));
        Font reamin_labelFont = new Font("맑은 고딕", Font.BOLD, 15);
        remainVac_l.setFont(reamin_labelFont);
        myVac_north_p.add(remainVac_l);

        myVac_south_p.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 1, 1, 1));
        myVac_south_p.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 10));
        bt_addVac.setText("휴가 신청");
        myVac_south_p.add(bt_addVac);
        myVac_p.add(myVac_south_p, java.awt.BorderLayout.PAGE_END);

        myVac_p.add(myVac_north_p, java.awt.BorderLayout.PAGE_START);


        vacTable.setModel(new DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "휴가 종류", "휴가 시작일", "휴가 기간", "남은 휴가", "결재 상태", "결재 날짜"
            }
        ) {
            Class[] types = new Class [] {
                String.class, String.class, String.class, String.class, String.class, String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        vacTable.setDefaultEditor(Object.class, null);
        jsp_vacTable.setViewportView(vacTable);

        myVac_p.add(jsp_vacTable, java.awt.BorderLayout.CENTER);

        centerCard_p.add(myVac_p, "myVacCard");

        //
        center_p.add(centerCard_p, java.awt.BorderLayout.CENTER);

        getContentPane().add(center_p, java.awt.BorderLayout.CENTER);

        pack();
    }

    public static void main(String args[]) {

        // Swing GUI 테마를 바꾸는 구문
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        // 프로그램 시작할 때 자동으로 로그인 프레임 창이 열리도록 하기
        new LoginFrame().setVisible(true);
    }

    private javax.swing.JPanel workLogCenter_p;
    private javax.swing.JLabel allVac_l;
    private javax.swing.JPanel myVac_south_p;
    private javax.swing.JTable attTable;
    private javax.swing.JButton bt_addVac;
    private javax.swing.JButton bt_adminMode;
    private javax.swing.JButton bt_dept;
    private javax.swing.JButton bt_editMyInfo;
    private javax.swing.JButton bt_exit;
    private javax.swing.JButton bt_find;
    private javax.swing.JButton bt_home;
    private javax.swing.JButton bt_logOut;
    private javax.swing.JButton bt_myAtt;
    private javax.swing.JButton bt_myInfo;
    private javax.swing.JButton bt_myList;
    private javax.swing.JButton bt_myVac;
    private javax.swing.JButton bt_search;
    private javax.swing.JButton bt_searchEmp;
    private javax.swing.JButton bt_workInOut;
    private javax.swing.JButton bt_workLog;
    private javax.swing.JButton bt_workLogWrite;
    private javax.swing.JPanel centerCard_p;
    private javax.swing.JPanel centerNorth_p;
    private javax.swing.JPanel center_p;
    private javax.swing.JPanel empty_p;
    private javax.swing.JLabel homeImage_l;
    private javax.swing.JPanel home_p;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel myVac_north_p;
    private javax.swing.JScrollPane jsp_attTable;
    private javax.swing.JScrollPane jsp_empTable;
    private javax.swing.JScrollPane jsp_logList;
    private javax.swing.JScrollPane jsp_myInfo;
    private javax.swing.JScrollPane jsp_vacTable;
    private javax.swing.JList<String> logList;
    private javax.swing.JComboBox<String> month_cb;
    private javax.swing.JPanel myAtt_north_p;
    private javax.swing.JPanel myAtt_p;
    private javax.swing.JPanel myInfo_north_p;
    private javax.swing.JPanel myInfo_p;
    private javax.swing.JPanel myVac_p;
    private javax.swing.JLabel northImage_l;
    private javax.swing.JLabel remainVac_l;
    private javax.swing.JPanel searchEmp_p;
    private javax.swing.JComboBox<String> search_cbox;
    private javax.swing.JLabel search_l;
    private javax.swing.JPanel south_p;
    private javax.swing.JTable table_emp;
    private javax.swing.JTable table_myInfo;
    private javax.swing.JLabel usedVac_l;
    private javax.swing.JTable vacTable;
    private javax.swing.JLabel value_l;
    private javax.swing.JTextField value_tf;
    private javax.swing.JPanel west_p;
    private javax.swing.JPanel workLog_north_p;
    private javax.swing.JPanel workLog_p;
    private javax.swing.JComboBox<String> year_cb;
}
