package Client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.EmpVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpSearchDialog extends JDialog {

    private SqlSessionFactory factory;
    private JTable empTable;
    private JComboBox<String> searchField_cb;
    private JTextField value_tf;
    int i=0;

    private final String[] columnNames = {
            "사원번호", "사원이름", "부서명", "직급명", "급여", "재직상태", "입사일", "관리자"
    };

    public EmpSearchDialog(JFrame parent) {
        super(parent, "사원 상세 조회", true);//부모, 이름, modal true로 지정
        init();//init함수 호출
        initComponents(); //initComponents 함수 호출
    }

    private void init() {
        try {
            Reader r = Resources.getResourceAsReader("config/conf.xml");
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 검색을 위한 패널 netbeans로 만든 부분 일부 수정함
        JPanel searchPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchField_l = new JLabel("검색 필드:");
        searchField_cb = new JComboBox<>(new String[]{
                "사원번호", "사원이름", "직급명", "부서명"
        });
        JLabel value_l = new JLabel("값 입력:");
        value_tf = new JTextField();
        JButton bt_search = new JButton("검색");

        searchPanel.add(searchField_l);
        searchPanel.add(searchField_cb);
        searchPanel.add(value_l);
        searchPanel.add(value_tf);
        searchPanel.add(new JLabel()); // empty
        searchPanel.add(bt_search);

        add(searchPanel, BorderLayout.NORTH);

        // 테이블
        empTable = new JTable(new DefaultTableModel(null, columnNames));
        JScrollPane scroll = new JScrollPane(empTable);
        add(scroll, BorderLayout.CENTER);

        //검색 버튼을 눌렀을때
        bt_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = (String) searchField_cb.getSelectedItem();
                String keyword = value_tf.getText().trim();
                if (keyword.isEmpty()) {
                    JOptionPane.showMessageDialog(EmpSearchDialog.this, "검색할 내용을 입력하세요");
                    return;
                }

                Map<String, Object> map = new HashMap<>();
                map.put("str", str);
                map.put("text", keyword);

                try (SqlSession ss = factory.openSession()) {
                    List<EmpVO> list = ss.selectList("adminemp.searchEmp", map);
                    DefaultTableModel model = new DefaultTableModel(null, columnNames);
                    for (EmpVO vo : list) {
                        model.addRow(new Object[]{
                                vo.getEmpno(), vo.getEname(), vo.getDept_name(),
                                vo.getPosname(), vo.getSal(),
                                "0".equals(vo.getWork_status()) ? "재직" : "퇴직",
                                vo.getHireDATE(),
                                vo.getMgr_name() == null ? "-" : vo.getMgr_name()
                        });
                    }
                    empTable.setModel(model); //여기서 오류나는건가?
                }
            }
        });
        value_tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bt_search.doClick(); // 엔터 누르면 검색 버튼 클릭 효과!
            }
        });

        setSize(800, 500);
        setLocationRelativeTo(getParent());//창 위치를 부모의 정중앙에다가
    }

    public static void main(String[] args) {
        new EmpSearchDialog(null).setVisible(true);
    }
}
