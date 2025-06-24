import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import vo.CommuteVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InOut extends JFrame {

    JPanel jp;
    JComboBox<String> y_combo;
    JComboBox<String> m_combo;
    String[] Year = {"2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025"};
    String[] month = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    JButton btn;
    JTable table;
    JLabel jl;
    String[] date_name = {"사번","날짜", "출근", "퇴근", "상태"};
    String[][] chk;

    SqlSessionFactory factory;
    List<CommuteVO> list;

    //생성자
    public InOut(){

        jp = new JPanel();
        jp.add(y_combo = new JComboBox<>(Year));
        jp.add(jl = new JLabel(" "));
        jp.add(m_combo = new JComboBox<>(month));
        jp.add(jl = new JLabel(" "));
        jp.add(btn = new JButton("조 회"));
        this.add(jp, BorderLayout.NORTH);

        this.add(new JScrollPane(table = new JTable()));
        table.setModel(new DefaultTableModel(chk, date_name));


        this.setBounds(300,100,500,500);
        this.setVisible(true);


        //DB연결
        init();

        search();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


    }//기본 생성자 끝

    private void viewTable(List<CommuteVO> list){
        //인자로 받은 List구조를 2차원 배열로 변환한 후 JTable에 표현!
        chk = new String[list.size()][date_name.length];
        int i = 0;
        for (CommuteVO vo : list) {
            chk[i][0] = vo.getEmpno();
            chk[i][1] = vo.getDate();
            chk[i][2] = vo.getChkin();
            chk[i][3] = vo.getChkout();
            chk[i][4] = vo.getAttend_note();

            i++;
        }//for종료
        table.setModel(new DefaultTableModel(chk, date_name));
    }


    private void search(){
        //사용자가 입력한 검색어를 가져온다

            SqlSession ss = factory.openSession();
            list = ss.selectList("commute.menber_search");
            viewTable(list);
            ss.close();
        }




        private void init() {
        try {
            Reader r = Resources.getResourceAsReader("config/conf.xml");
            factory = new SqlSessionFactoryBuilder().build(r);
            r.close();
            System.out.println("DB연결 완료");
//            this.setTitle("준비완료");

            SqlSession ss = factory.openSession();
            Map<String, String> map = new HashMap<>();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[])  {
            new InOut();
        }

}
