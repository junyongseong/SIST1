package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.AttendanceVO;
import vo.EmpVO;
import vo.Leave_ofVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFrame extends JFrame {

    EmpVO testadmin = new EmpVO(); // 테스트용 관리자

    SqlSessionFactory factory;
    SqlSession ss;

    // 사원조회테이블 컬럼명
    String[] e_name = {"사원번호", "사원이름", "부서명", "직급명", "급여", "재직상태", "입사일", "관리자"};

    // 근태조회테이블 컬럼명
    String[] a_name = {"사원 번호", "사원 이름", "날짜", "출근 시간", "퇴근 시간", "근태"};

    // 휴가테이블 컬럼명
    String[] v_name = {"사원 번호", "사원 이름", "부서", "휴가 종류", "휴가 시작일",
            "휴가 기간", "남은 휴가", "결재 상태", "휴가 코드"};

    JTable vacTable;

    JTable empTable; // 클래스 필드로 선언

    EmpVO vo;
    public AdminFrame(EmpVO vo) throws IOException {
        this.vo = vo;

        testadmin.setEmpno("1001"); // 사번이 1001인 관리자 테스트용으로 임시 지정
        testadmin.setDeptno("10");

        setTitle("관리자 모드");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel north_p = new JPanel();
        north_p.setPreferredSize(new Dimension(965, 50));
        add(north_p, BorderLayout.NORTH);

        JPanel south_p = new JPanel();
        south_p.setPreferredSize(new Dimension(965, 50));
        add(south_p, BorderLayout.SOUTH);

        JPanel center_p = new JPanel(new BorderLayout());
        add(center_p, BorderLayout.CENTER);

        JPanel centerWest_p = new JPanel(new GridLayout(5, 1, 0, 40));
        centerWest_p.setBorder(BorderFactory.createEmptyBorder(1, 30, 1, 30));
        centerWest_p.setPreferredSize(new Dimension(240, 480));

        JButton bt_home = new JButton("홈");
        JButton bt_adminEmp = new JButton("사원 관리");
        JButton bt_adminAtt = new JButton("근태 관리");
        JButton bt_adminVac = new JButton("휴가 관리");
        JButton bt_userMode = new JButton("사용자 모드");

        centerWest_p.add(bt_home);
        centerWest_p.add(bt_adminEmp);
        centerWest_p.add(bt_adminAtt);
        centerWest_p.add(bt_adminVac);
        centerWest_p.add(bt_userMode);
        center_p.add(centerWest_p, BorderLayout.WEST);

        JPanel centerCard_p = new JPanel(new CardLayout());
        center_p.add(centerCard_p, BorderLayout.CENTER);
        CardLayout cl = (CardLayout)(centerCard_p.getLayout());

        // 홈
        JPanel admin_p = new JPanel(new BorderLayout());
        JLabel homeImage_l = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/adminHome.jpg"));
        Image img = icon.getImage().getScaledInstance(600, 760, Image.SCALE_SMOOTH);
        homeImage_l.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 1, 1, 1));
        homeImage_l.setIcon(new ImageIcon(img));
        admin_p.add(homeImage_l, BorderLayout.CENTER);
        centerCard_p.add(admin_p, "adminCard");

        // 사원 관리
        JPanel adminEmp_p = new JPanel(new BorderLayout());
        adminEmp_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminEmp_north_p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        JButton bt_dsearch = new JButton("상세 조회");
        JButton bt_addEmp = new JButton("사원 추가");
        adminEmp_north_p.add(bt_dsearch);
        adminEmp_north_p.add(bt_addEmp);
        adminEmp_p.add(adminEmp_north_p, BorderLayout.NORTH);

        empTable = new JTable(new DefaultTableModel(null, e_name));
        empTable.setDefaultEditor(Object.class, null);

        JScrollPane jsp_empTable = new JScrollPane(empTable);
        adminEmp_p.add(jsp_empTable, BorderLayout.CENTER);
        centerCard_p.add(adminEmp_p, "adminEmpCard");

        // 근태 관리
        JPanel adminAtt_p = new JPanel(new BorderLayout());
        adminAtt_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminAtt_north_p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        JComboBox<String> year_cb = new JComboBox<>(new String[]{"2022", "2023", "2024", "2025"});
        JComboBox<String> month_cb = new JComboBox<>(new String[]{"01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"});

        JButton bt_find = new JButton("조회");

        adminAtt_north_p.add(year_cb);
        adminAtt_north_p.add(new JLabel("년"));
        adminAtt_north_p.add(month_cb);
        adminAtt_north_p.add(new JLabel("월"));
        adminAtt_north_p.add(bt_find);
        adminAtt_p.add(adminAtt_north_p, BorderLayout.NORTH);

        // 근태관리 테이블
        JTable attTable = new JTable(new javax.swing.table.DefaultTableModel(
                null,
                a_name
        ));
        JScrollPane jsp_attTable = new JScrollPane(attTable);
        adminAtt_p.add(jsp_attTable, BorderLayout.CENTER);
        centerCard_p.add(adminAtt_p, "adminAttCard");

        // 휴가 관리
        JPanel adminVac_p = new JPanel(new BorderLayout());
        adminVac_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminVac_north_p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        JButton bt_cfirmDeny = new JButton("승인 / 반려");
        JButton bt_cfirmDenyList = new JButton("승인 / 반려 내역");
        adminVac_north_p.add(bt_cfirmDeny);
        adminVac_north_p.add(bt_cfirmDenyList);
        adminVac_p.add(adminVac_north_p, BorderLayout.NORTH);

        vacTable = new JTable(new javax.swing.table.DefaultTableModel(
                new Object[4][9],
                new String[]{"사원 번호", "사원 이름", "부서", "휴가 종류", "휴가 시작일", "휴가 기간", "남은 휴가", "결재 상태", "결재 날짜"}
        ));
        JScrollPane jsp_vacTable = new JScrollPane(vacTable);
        vacTable.setDefaultEditor(Object.class, null);
        adminVac_p.add(jsp_vacTable, BorderLayout.CENTER);
        centerCard_p.add(adminVac_p, "adminVacCard");

        init();

        //System.out.println(vo.getRole_num());권한번호 테스트
        this.setSize(1000, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ss.close();
                System.exit(0);
            }
        });

        // 홈 버튼 눌렀을 때 화면 변경
        bt_home.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminCard");
            }
        });

        //사원 관리 클릭시
        bt_adminEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p,"adminEmpCard");
            }

        });

        //상세 조회
        bt_dsearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EmpSearchDialog(AdminFrame.this).setVisible(true);
            }
        });

        //사원 추가
        bt_addEmp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmpAddDialog dialog = new EmpAddDialog(AdminFrame.this, factory);
                dialog.setEmpAddedListener(new EmpAddDialog.EmpAddedListener() {
                    @Override
                    public void onEmpAdded() {
                        loadEmpData(); // 테이블 리로드
                    }
                });
                dialog.setVisible(true);
            }
        });

        // 근태조회 버튼을 눌렀을 때
        bt_adminAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminAttCard");
                // 근태조회 카드에서 조회 버튼을 눌렀을 때
                bt_find.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // 각각 검색할 연도, 월 받아와 맵구조에 저장
                        String year = year_cb.getSelectedItem().toString();
                        String mon = month_cb.getSelectedItem().toString();
                        Map<String, String> attsearchmap = new HashMap<>();
                        attsearchmap.put("year", year);
                        attsearchmap.put("mon", mon);
                        // 해당부서장과 동일한 부서원들을 특정하기 위해 deptno받아오기
                        attsearchmap.put("deptno", testadmin.getDeptno());

                        // 우리 부서 근태 조회하기
                        List<AttendanceVO> list = ss.selectList("attendance.search",
                                attsearchmap);
                        String[][] data = new String[list.size()][a_name.length];
                        int i = 0;
                        for(AttendanceVO vo : list) {
                            data[i][0] = vo.getEmpno();
                            data[i][1] = vo.getEname();
                            data[i][2] = vo.getDate();
                            data[i][3] = vo.getChkin();
                            data[i][4] = vo.getChkout();
                            data[i][5] = vo.getAttend_note();
                            i++;
                        }
                        attTable.setModel(new DefaultTableModel(data, a_name));

                    }
                }); // 근태조회 카드에서의 조회 버튼 끝
            }
        }); // 근태조회 버튼 끝

        // 휴가 관리 버튼을 눌렀을 때
        bt_adminVac.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminVacCard");

                // 승인/반려 버튼 이벤트
                bt_cfirmDeny.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ss = factory.openSession();

                        List<Leave_ofVO> list = ss.selectList("leave_of.approvevac", vo.getDeptno());
                        String[][] data = new String[list.size()][v_name.length];

                        ViewvacTable(list);

                        int i = 0;
                        for(Leave_ofVO vo : list) {
                            data[i][0] = vo.getEmpno();
                            data[i][1] = vo.getEname();
                            data[i][2] = vo.getDeptno();
                            data[i][3] = vo.getLname();
                            data[i][4] = vo.getLdate();
                            data[i][5] = vo.getDuration();
                            data[i][6] = vo.getRemain_leave();
                            data[i][7] = vo.getLstatus();
                            data[i][8] = vo.getLnum();
                            i++;
                        }
                        vacTable.setModel(new DefaultTableModel(data, v_name));
                        ss.close();
                    }
                });

                // 승인/반려내역 이벤트
                bt_cfirmDenyList.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ss = factory.openSession();

                        List<Leave_ofVO> list = ss.selectList("leave_of.searchvac", vo.getDeptno());
                        String[][] data = new String[list.size()][v_name.length];
                        ViewvacTable(list);

                        int i = 0;
                        for(Leave_ofVO vo : list) {
                            data[i][0] = vo.getEmpno();
                            data[i][1] = vo.getEname();
                            data[i][2] = vo.getDeptno();
                            data[i][3] = vo.getLname();
                            data[i][4] = vo.getLdate();
                            data[i][5] = vo.getDuration();
                            data[i][6] = vo.getRemain_leave();
                            data[i][7] = vo.getLstatus();
                            data[i][8] = vo.getLprocessed();
                            i++;
                        }
                        vacTable.setModel(new DefaultTableModel(data, v_name));
                        ss.close();
                    }
                });
            }
        });

        // 휴가 관리 - 승인/반려 테이블에서 특정 휴가 신청을 승인했을 경우 수행하는 감지자
        vacTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cnt = e.getClickCount();
                if (cnt == 2) { // 테이블에서 더블클릭을 했다면
                    int i = vacTable.getSelectedRow(); // 정수형 변수 i에 선택된 열의 인덱스값을 저장
                    String empno = vacTable.getValueAt(i, 0).toString(); // 선택된 열의 사번 저장
                    String lname = vacTable.getValueAt(i, 3).toString(); // 선택된 열의 휴가 유형 저장

                    // 선택된 열의 휴가 기간 저장
                    String durationStr = vacTable.getValueAt(i, 5).toString();
                    BigDecimal duration = new BigDecimal(durationStr);

                    // 선택된 열의 휴가 시작일 저장
                    String ldateStr = vacTable.getValueAt(i, 4).toString();
                    Date ldate = Date.valueOf(ldateStr); // java.sql.Date

                    String lnum = vacTable.getValueAt(i, 8).toString();

                    // 컨펌 다얄로그를 띄우고 승인할지를 물어봄
                    int num = JOptionPane.showConfirmDialog(AdminFrame.this, "승인하시겠습니까?");
                    if (num == 0){ // 승인을 했다면
                        ss = factory.openSession();
                        //String lnum = ss.selectOne("leave_of.getLnum", empno);
                        //System.out.println(lnum);
                        //Leave_ofVO lvo = ss.selectOne("leave_of.getOne", listvo);

                        // 해당하는 휴가코드를 가지는 leave_of 테이블의 레코드의 휴가상태를 1 (휴가 승인) 으로 업데이트
                        int update = ss.update("leave_of.statusUpdate", lnum);
                        if (update == 0){ // 변경된 사항이 없다면 롤백시키고 돌아가기
                            ss.rollback();
                            ss.close();
                            return;
                        }

                        List<Leave_ofVO> list = ss.selectList("leave_of.approvevac", vo.getDeptno());
                        String[][] data = new String[list.size()][v_name.length];

                        ViewvacTable(list);

                        i = 0;
                        for(Leave_ofVO vo : list) {
                            data[i][0] = vo.getEmpno();
                            data[i][1] = vo.getEname();
                            data[i][2] = vo.getDeptno();
                            data[i][3] = vo.getLname();
                            data[i][4] = vo.getLdate();
                            data[i][5] = vo.getDuration();
                            data[i][6] = vo.getRemain_leave();
                            data[i][7] = vo.getLstatus();
                            data[i][8] = vo.getLnum();
                            i++;
                        }
                        vacTable.setModel(new DefaultTableModel(data, v_name));

                        //
                        int days = duration.intValue(); // 소수점은 버림. 2.5 → 2
                        List<Date> dates = new ArrayList<>();
                        LocalDate startDate = ldate.toLocalDate();

                        // 휴가 기간을 비교해서 0.5라면 반차이므로 근태 테이블에 레코드를 하나만 추가하고
                        // 그 외의 경우라면 연차이므로 앞서 얻어낸 휴가 기간의 수만큼 날짜를 얻어내 dates 리스트에 저장
                        if (duration.compareTo(new BigDecimal("0.5")) == 0) {
                            dates.add(Date.valueOf(startDate));
                        } else {
                            for (int k = 0; k < days; k++) {
                                dates.add(Date.valueOf(startDate.plusDays(k)));
                            }
                        }

                        System.out.println("lname: [" + lname + "]");
                        System.out.println("dates: " + dates);
                        for (Date d : dates) {
                            System.out.println(" date: " + d);
                        }

                        // 승인된 휴가가 각각 연차, 오전 반차, 오후 반차일 경우를 구분해 근태 태이블에 레코드를 인서트하는 쿼리
                        if (lname.equals("가족행사") || lname.equals("개인 사유 휴가") || lname.equals("경조사")) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("empno", empno);
                            map.put("dates", dates);
                            map.put("lname", lname);
                            ss.insert("leave_of.insertAttLeave", map);
                        } else if (lname.equals("오전 반차")){
                            Map<String, Object> map = new HashMap<>();
                            map.put("empno", empno);
                            map.put("dates", dates);
                            map.put("lname", lname);
                            ss.insert("leave_of.insertAttLeave3", map);
                        } else if (lname.equals("오후 반차")){
                            Map<String, Object> map = new HashMap<>();
                            map.put("empno", empno);
                            map.put("dates", dates);
                            map.put("lname", lname);
                            ss.insert("leave_of.insertAttLeave4", map);
                        }

                        // leave_history 테이블에서 남은 휴가를 사용한 휴가 기간만큼 빼는 쿼리
                        Map<String, Object> map = new HashMap<>();
                        map.put("empno", empno);
                        map.put("duration", duration); // BigDecimal
                        ss.update("leave_of.remainLeaveUpdate", map);

                        // DB 상에서 변경된 모든 내용을 커밋해서 반영하고 세션 닫기
                        ss.commit();
                        ss.close();
                    }
                }
            }
        });

        // 사용자 모드 버튼을 눌렀을 때
        bt_userMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminFrame.this.dispose();

                new UserFrame(vo).setVisible(true);
            }
        });

        loadEmpData();
        EmpTableClick(empTable);
    } // 생성자 끝

    private void ViewvacTable(List<Leave_ofVO> list) {
        String[][] data = new String[list.size()][v_name.length];
        int i = 0;
        for(Leave_ofVO vo : list) {
            data[i][0] = vo.getEmpno();
            data[i][1] = vo.getEname();
            data[i][2] = vo.getDeptno();
            data[i][3] = vo.getLname();
            data[i][4] = vo.getLdate();
            data[i][5] = vo.getDuration();
            data[i][6] = vo.getRemain_leave();
            data[i][7] = vo.getLstatus();
            data[i][8] = vo.getLprocessed();
            i++;
        }
    }

    private void loadEmpData() {
        try (SqlSession ss = factory.openSession()) {
            List<EmpVO> list = ss.selectList("adminemp.getALLemp");

            DefaultTableModel model = new DefaultTableModel(null, e_name);
            for (EmpVO vo : list) {
                model.addRow(new Object[]{
                        vo.getEmpno(),
                        vo.getEname(),
                        vo.getDept_name(),
                        vo.getPosname(),
                        vo.getSal(),
                        "0".equals(vo.getWork_status()) ? "재직" : "퇴직",
                        vo.getHireDATE(),
                        vo.getMgr_name() == null ? "-" : vo.getMgr_name()
                });
            }
            empTable.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터 불러오기 실패!");
        }
    }

    private void EmpTableClick(JTable empTable){
        empTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = empTable.getSelectedRow();
                    if(row == -1) return;

                    Long empno = Long.parseLong(empTable.getValueAt(row,0).toString());
                    EmpVO targetEmp = ss.selectOne("adminemp.getEmpByEmpno", empno);
                    // 로그인한 사용자가 팀장인데, 수정하려는 대상도 팀장 이상이면 수정 불가능
                    if (vo.getRole_num().equals("2") &&
                            (targetEmp.getRole_num().equals("2") || targetEmp.getRole_num().equals("3"))) {
                        JOptionPane.showMessageDialog(AdminFrame.this, "팀장이상의 직급을 수정할 수 있는 권한이 없습니다.");
                        return;
                    }
                    new EmpEditDialog(AdminFrame.this, targetEmp, AdminFrame.this.vo, ss, () -> loadEmpData());

                }
            }
        });
    }

    private void init() throws IOException {
        // 스트림 생성
        Reader r = Resources.getResourceAsReader(
                "config/conf.xml");
        // 팩토리 생성
        factory = new SqlSessionFactoryBuilder().build(r);
        r.close();

        // sql세션 열기
        ss = factory.openSession();
    }
}
