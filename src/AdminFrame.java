import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.AttendanceVO;
import vo.EmpVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFrame extends JFrame {//JFrame

    SqlSessionFactory factory;
    SqlSession ss;
    JTable empTable; // 클래스 필드로 선언

    String[] a_name = {"사원 번호", "사원 이름", "날짜", "출근 시간", "퇴근 시간", "근태"};
    String[] e_name = {"사원번호", "사원이름", "부서명", "직급명", "급여", "재직상태", "입사일", "관리자"};

    public AdminFrame() throws IOException {
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

        JPanel admin_p = new JPanel(new BorderLayout());
        JLabel homeImage_l = new JLabel("", SwingConstants.CENTER);
        admin_p.add(homeImage_l, BorderLayout.CENTER);
        centerCard_p.add(admin_p, "adminCard");

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

        JTable attTable = new JTable(new javax.swing.table.DefaultTableModel(
                new Object[4][6],
                new String[]{"사원 번호", "사원 이름", "날짜", "출근 시간", "퇴근 시간", "근태"}
        ));
        JScrollPane jsp_attTable = new JScrollPane(attTable);
        adminAtt_p.add(jsp_attTable, BorderLayout.CENTER);
        centerCard_p.add(adminAtt_p, "adminAttCard");

        JPanel adminVac_p = new JPanel(new BorderLayout());
        adminVac_p.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 30));

        JPanel adminVac_north_p = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 8));
        JButton bt_cfirmDenyList = new JButton("승인 / 반려 내역");
        adminVac_north_p.add(bt_cfirmDenyList);
        adminVac_p.add(adminVac_north_p, BorderLayout.NORTH);

        JTable vacTable = new JTable(new javax.swing.table.DefaultTableModel(
                new Object[4][9],
                new String[]{"사원 번호", "사원 이름", "부서", "휴가 종류", "휴가 시작일", "휴가 기간", "남은 휴가", "결재 상태", "결재 날짜"}
        ));
        JScrollPane jsp_vacTable = new JScrollPane(vacTable);
        adminVac_p.add(jsp_vacTable, BorderLayout.CENTER);
        centerCard_p.add(adminVac_p, "adminVacCard");

        init();

        this.setSize(1000, 900);
        setLocationRelativeTo(null);
        setVisible(true);

        this.addWindowListener(new WindowAdapter() {//창 닫기 클릭시
            @Override
            public void windowClosing(WindowEvent e) {
                ss.close();
                System.exit(0);
            }
        });

        bt_adminEmp.addActionListener(new ActionListener() {//사원 관리 클릭시
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p,"adminEmpCard");
            }
        });

        bt_dsearch.addActionListener(new ActionListener() {//상세 조회
            @Override
            public void actionPerformed(ActionEvent e) {
                new EmpSearchDialog(AdminFrame.this).setVisible(true);
            }
        });

        bt_addEmp.addActionListener(new ActionListener() {//사원 추가
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

        bt_adminAtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(centerCard_p, "adminAttCard");
                bt_find.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String year = year_cb.getSelectedItem().toString();
                        String mon = month_cb.getSelectedItem().toString();
                        Map<String, String> attsearchmap = new HashMap<>();
                        attsearchmap.put("year", year);
                        attsearchmap.put("mon", mon);

                        List<AttendanceVO> list = ss.selectList("attendance.search", attsearchmap);
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
                });
            }
        });

        loadEmpData();
        EmpTableClick(empTable);
    }//생성자의 끝

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
                    EmpVO vo = ss.selectOne("adminemp.getEmpByEmpno", empno);
                    if(vo == null) return;

                    new EmpEditDialog(AdminFrame.this, vo, ss, () -> loadEmpData());
                }
            }
        });
    }

    private void init() throws IOException {
        Reader r = Resources.getResourceAsReader("config/conf.xml");
        factory = new SqlSessionFactoryBuilder().build(r);
        r.close();
        ss = factory.openSession();
    }

    public static void main(String[] args) throws IOException {
        new AdminFrame();
    }
}
