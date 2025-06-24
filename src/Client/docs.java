package Client;

import vo.DeptVO;
import vo.DocsVO;
import vo.EmpVO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class docs extends JFrame {
    JTextField title;
    JScrollPane jsp_workLogWrite, save_p;
    JTextArea ta_workLogWrite;
    JMenuBar bar;
    JMenu menu;
    JMenuItem menuItem, view_menu, saveview;
    JPanel north_p;
    JTable table, stable;
    JLabel jl1, dateL;
    int i, j;
    String docNum;

    DocsVO dvo;
    EmpVO evo;
    DeptVO dpvo;
    List<DocsVO> Docslist;
    SqlSessionFactory factory;

    public docs(EmpVO vo){
        evo = vo;

        init();

        initComponents();

        //문서저장
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //문서저장함수
                insertDocs();
            }
        });

        //문서 조회 테이블의 열 선택 후 열람, 공유 선택
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cnt = e.getClickCount();
                System.out.println("들어감");
                if (cnt == 2 && cnt != -1) {
                    System.out.println("더블클릭");
                    i = table.getSelectedRow();
                    String docNum = table.getValueAt(i, 0).toString();
                    String[] select_m = {"열람", "공유"};
                    int select_op = JOptionPane.showOptionDialog(docs.this, "선택", "타이틀", 0, JOptionPane.INFORMATION_MESSAGE, null, select_m, select_m[0]);

                    if (select_op == 0) {
                        //열람 선택 함수
                        showdocs(docNum);
                    } else if (select_op == 1){
                        //공유 선택 함수
                        share_docs(docNum);
                    }

                }

            }
        });

        //부서별 문서 조회
        view_menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SqlSession ss = factory.openSession();
                Docslist = ss.selectList("docs.Docs_Dept", "10"); //dvo.getDeptno());
                String[] column = {"문서번호", "제목", "내용"};
                String[][] data = new String[Docslist.size()][column.length];
                for (int i = 0; i < Docslist.size(); i++) {
                    DocsVO dvo = Docslist.get(i);
                    data[i][0] = dvo.getDocs_num();
                    data[i][1] = dvo.getTitle();
                    data[i][2] = dvo.getContent();

                    table.setModel(new DefaultTableModel(data, column));

                }
                ss.close();
                JScrollPane scrollPane = new JScrollPane(table);
                JOptionPane.showMessageDialog(docs.this, scrollPane, "부서 문서 목록", JOptionPane.INFORMATION_MESSAGE);

            }
        });

        //공유된 문서 확인
        stable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cnt = e.getClickCount();
                System.out.println("들어감");
                if (cnt == 2) {
                    System.out.println("더블클릭");
                    j = stable.getSelectedRow();
                    String docNum = stable.getValueAt(j, 1).toString();
                    showdocs(docNum);
                }

            }
        });

        //공유된 문서 확인
        saveview.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SqlSession ss = factory.openSession();
                Docslist = ss.selectList("reDocs", vo.getDeptno());//dvo.getDeptno());
                String[] scolumn = {"공유문서번호", "문서번호", "제목", "내용", "부서명"};
                String[][] sdata = new String[Docslist.size()][scolumn.length];
                for (int i = 0; i < Docslist.size(); i++) {
                    DocsVO dvo = Docslist.get(i);
                    sdata[i][0] = dvo.getShare_id();
                    sdata[i][1] = dvo.getDocs_num();
                    sdata[i][2] = dvo.getTitle();
                    sdata[i][3] = dvo.getContent();
                    sdata[i][4] = dvo.getDname();
                }

                stable.setModel(new DefaultTableModel(sdata, scolumn));

                save_p = new JScrollPane(stable);
                JOptionPane.showMessageDialog(docs.this, save_p, "부서 문서 목록", JOptionPane.INFORMATION_MESSAGE);

                ss.close();
            }
        });

    }

    //문서저장함수
    private void insertDocs(){
        SqlSession ss = factory.openSession();
        String str = ta_workLogWrite.getText();
        dvo = new DocsVO();
        dvo.setTitle(title.getText());
        dvo.setContent(ta_workLogWrite.getText());
        dvo.setEmpno(evo.getEmpno()); //evo.getEmpno());
        dvo.setDeptno(evo.getDeptno());//evo.getDeptno());
        dvo.setVisibility("dept");
        dvo.setDate(dateL.getText());
        ss.insert("docs.insertDoc",dvo);
        if (title.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(docs.this, "제목을 입력해주세요.");
            return;
        }
        if (ta_workLogWrite.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(docs.this, "내용을 입력해주세요.");
            return;
        }
        ss.commit();
        ss.close();
        JOptionPane.showMessageDialog(docs.this, "문서 저장 완료");
    }

    //조회 테이블 더블클릭 후 열람을 누르면 발생
    private void showdocs(String docNum){ // 선택된 열의 문서번호 값
        SqlSession ss = factory.openSession();
        DocsVO dvo = ss.selectOne("docs.getDoc", docNum);
        if (dvo == null) {
            JOptionPane.showMessageDialog(this, "문서를 찾을 수 없습니다.");
            return;
        }
        String message = String.format("제목: %s\n내용: %s\n작성일: %s\n작성자: %s\n부서명:%s", dvo.getTitle(), dvo.getContent(), dvo.getDate(), dvo.getEname(), dvo.getDname());
        JOptionPane.showMessageDialog(this, message, "문서 열람", JOptionPane.INFORMATION_MESSAGE);
        ss.close();
    }

    //조회 테이블 더블클릭 후 공유, 체크박스 체크 후 확인을 누르면
    //그 부서들에만 문서 공유
    private void share_docs(String docNum){// 선택된 열의 문서번호 값

        SqlSession ss = factory.openSession();
        List<DeptVO> deptlist = ss.selectList("docs.allDept");
        JPanel cb_p = new JPanel(new GridLayout(deptlist.size(), 1));
        Map<JCheckBox, String> cbMap = new HashMap<>();

        //부서번호를 기준으로 체크박스에 부서명 입력
        for(DeptVO dpvo : deptlist){
            JCheckBox dept_cb = new JCheckBox(dpvo.getDname());
            cbMap.put(dept_cb, dpvo.getDeptno());
            cb_p.add(dept_cb);
        }
        int result = JOptionPane.showConfirmDialog(
                docs.this, cb_p, "공유할 부서를 선택하세요", JOptionPane.OK_CANCEL_OPTION
        );

        //부서 선택 후 문서 번호와 공유받은 부서 번호가 테이블에 저장
        if (result == JOptionPane.OK_OPTION) {
            SqlSession shareSession = factory.openSession();
            for (Map.Entry<JCheckBox, String> entry : cbMap.entrySet()) {
                if (entry.getKey().isSelected()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("docs_num", docNum);
                    map.put("deptno", entry.getValue()); // 선택한 부서의 deptno로 저장
                    ss.insert("docs.share_Docs", map);
                    ss.commit();
                }
            }
        }
        JOptionPane.showMessageDialog(docs.this, "공유 완료");
        ss.close();

    }

    private void init(){
        try {
            Reader r = Resources.getResourceAsReader(
                    "config/conf.xml"
            );

            factory = new SqlSessionFactoryBuilder().build(r);

            r.close();
            this.setTitle("준비 완료");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //화면생성
    private void initComponents() {

        north_p = new JPanel();
        jsp_workLogWrite = new javax.swing.JScrollPane();
        save_p = new JScrollPane();
        ta_workLogWrite = new javax.swing.JTextArea();
        bar = new javax.swing.JMenuBar();
        menu = new javax.swing.JMenu();
        menuItem = new javax.swing.JMenuItem();
        view_menu = new JMenuItem();
        title = new JTextField(10);
        table = new JTable();
        stable = new JTable();
        saveview = new JMenuItem();

        table.setDefaultEditor(Object.class, null);
        stable.setDefaultEditor(Object.class, null);

        setTitle("업무 일지 작성");

        jsp_workLogWrite.setPreferredSize(new java.awt.Dimension(410, 521));
        north_p.add(jl1 = new JLabel("제목: "));
        north_p.add(title);
        LocalDate now = LocalDate.now();
        north_p.add(dateL = new JLabel(now.toString()));
        this.add(north_p, BorderLayout.NORTH);
        ta_workLogWrite.setRows(5);
        jsp_workLogWrite.setViewportView(ta_workLogWrite);

        getContentPane().add(jsp_workLogWrite, java.awt.BorderLayout.CENTER);

        menu.setText("파일");

        menuItem.setText("저장");
        menu.add(menuItem);

        view_menu.setText("공유");
        menu.add(view_menu);

        saveview.setText("받은 문서 조회");
        menu.add(saveview);

        bar.add(menu);

        setJMenuBar(bar);

        this.setBounds(300, 300, 300, 300);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                docs.this.dispose();
            }
        });

        pack();
    }
}
