/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package pm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatFrame extends javax.swing.JFrame {
    
    CardLayout card;
    
    Socket s;
    ObjectInputStream in;
    ObjectOutputStream out;
    
    // 서버쪽에서 데이터가 넘어오는지? 항상 감시하는 스레드
    Thread t = new Thread() {
		@Override
		public void run() {
			bk:while(true) {
				try {
					Object obj = in.readObject();//서버로부터 데이터가 올 때까지
												// 대기한다.
					Protocol p = (Protocol) obj;
                    // 1:접속, 2:채팅, 3:종료, 4:방만들기, 5:방나가기
					switch(p.getCmd()) {
						case 1: // 어떤 누군가가 접속했을 때 수행
							//명단을 받아서 user_list라는 JList에 넣어준다.
							user_list.setListData(p.getUser_names());
							room_list.setListData(p.getRoom_names());
							break;
                        case 2://채팅
                            break;
						case 3:
							break bk;
						case 4:
							//내가 방을 만들고 다시 4번 프로토콜을 받는다.
							//명단과 입장메세지도 같이 받아 화면에 표현한다.							
							join_list.setListData(p.getUser_names());
							ta.append(p.getMsg());
							card.show(getContentPane(), "chatRoom");
							break;
                        case 5://방나가기
                            user_list.setListData(p.getUser_names());
                            room_list.setListData(p.getRoom_names());
                            card.show(getContentPane(), "roomList");
                            break;
					}//switch의 끝
				} catch (Exception e) {
					e.printStackTrace();
				}
			}// 무한반복
			closed();

			System.exit(0);//프로그램 종료!!!!!!!!!!
		}
    	
    };
    
    //채팅화면에 필요한 객체
    JPanel card3, card3_e, card3_s;
    JButton out_bt, send_bt;
    JTextArea ta;
    JTextField input_tf;
    JList<String> join_list;// 참여자 명단 표현
    
    public ChatFrame() {
        initComponents();//화면 구성
        setVisible(true);//화면 보여주기
                
        //이벤트 감지자등록
        this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// 접속이 안된 상태면 바로 종료!
				//그렇지 않다면 3번 프로토콜 생성 후 전송만 하자
				if(s == null)
					System.exit(0);
				else {
					Protocol p = new Protocol();
					p.setCmd(3);
					try {
						out.writeObject(p); //나와 유일하게 연결된 복사본에게만
						out.flush();		//종료 프로토콜 보낸다.
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
        
        jButton1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// 로그인 버튼을 클릭했을 때 수행하는 곳!
				
				// 사용자가 입력한 대화명 가져오기
				String n = jTextField1.getText().trim();
				
				if(n.length() < 1) {
					JOptionPane.showMessageDialog(ChatFrame.this, "대화명을 입력하세요");
					jTextField1.setText("");
					jTextField1.requestFocus();//커서 가져다놓기!
					return;
				}else {
					//서버 접속
					try {
						s = new Socket("192.168.0.4", 5555);
						out = new ObjectOutputStream(s.getOutputStream());
						in = new ObjectInputStream(s.getInputStream());
						t.start();// 스레드 시작
						
						card.show(getContentPane(), "roomList");
						
						//처음 접속했다는 의미로 프로토콜 데이터 보내야 한다.
						Protocol p = new Protocol();
						p.setCmd(1);
						p.setMsg(n);//대화명
						
						// 서버로 보낸다.
						out.writeObject(p);
						out.flush();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
		});
        
        jButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 방 만들기
				String str = JOptionPane.showInputDialog(
						ChatFrame.this, "방 제목을 입력하세요");
				if(str != null && str.trim().length() > 0) {
					//방제목을 1자라도 입력한 경우!
					// 방을 만들 수 있는 프로토콜 생성
					Protocol p = new Protocol();
					p.setCmd(4);
					p.setMsg(str);//방제목 담기
					
					try {
						out.writeObject(p);
						out.flush();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});

        out_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Protocol p = new Protocol();
                p.setCmd(5);
                try {
                    //서버로 전송--(나하고만 연결된 CopyClient에게 전달)
                    out.writeObject(p);
                    out.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        send_bt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = input_tf.getText().trim();
                if (msg.length() > 0) {
                    Protocol p = new Protocol();
                    p.setCmd(7); // 일반 채팅 메시지
                    p.setMsg(msg);
                    try {
                        out.writeObject(p);
                        out.flush();
                        input_tf.setText("");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        room_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int cnt = e.getClickCount();
                if(cnt == 2){//room_list에서 더블클릭했는지?
                    join();
                }
            }
        });
    }//////////////////////////생성자의 끝//////////////////////
    
    public void join(){
        //선택된 index값을 얻어내자!
        int idx = room_list.getSelectedIndex();

        // 방 참여를 위한 프로토콜 만들기
        Protocol p = new Protocol();
        p.setCmd(6);
        p.index = idx;

        try{
            out.writeObject(p);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void closed() {
    	try {
    		if(out != null)
				out.close();
			if(in != null)
				in.close();
			if(s != null)
				s.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        card1 = new JPanel();
        jLabel1 = new JLabel();
        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        jLabel2 = new JLabel();
        jTextField1 = new JTextField();
        jPanel3 = new JPanel();
        jButton1 = new JButton();
        card2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        room_list = new JList<>();
        jPanel4 = new JPanel();
        jLabel3 = new JLabel();
        jPanel5 = new JPanel();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
        jButton5 = new JButton();
        jScrollPane2 = new JScrollPane();
        user_list = new JList<>();

        getContentPane().setLayout(card = new CardLayout());

        card1.setPreferredSize(new java.awt.Dimension(390, 490));
        card1.setLayout(new BorderLayout());

        jLabel1.setMaximumSize(new java.awt.Dimension(390, 400));
        jLabel1.setMinimumSize(new java.awt.Dimension(390, 400));
        jLabel1.setName(""); // NOI18N
        ImageIcon icon = new ImageIcon("src/images/chat.png");
        Image img = icon.getImage().getScaledInstance(390, 400, Image.SCALE_SMOOTH);
        ImageIcon icon2 = new ImageIcon(img);
        jLabel1.setIcon(icon2);
        jLabel1.setPreferredSize(new java.awt.Dimension(390, 400));
        card1.add(jLabel1, BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(2, 1));

        jPanel2.setLayout(new java.awt.FlowLayout(2));

        jLabel2.setText("대화명 :");
        jPanel2.add(jLabel2);

        jTextField1.setColumns(10);
        jPanel2.add(jTextField1);

        jPanel1.add(jPanel2);

        jPanel3.setLayout(new java.awt.FlowLayout(2));

        jButton1.setText("로그인");
        jPanel3.add(jButton1);

        jPanel1.add(jPanel3);

        card1.add(jPanel1, BorderLayout.PAGE_END);

        getContentPane().add(card1, "first");

        card2.setLayout(new BorderLayout());

        jScrollPane1.setViewportView(room_list);

        card2.add(jScrollPane1, BorderLayout.CENTER);

        jPanel4.setPreferredSize(new java.awt.Dimension(100, 490));
        jPanel4.setLayout(new BorderLayout());

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("[대기실]");
        jPanel4.add(jLabel3, BorderLayout.NORTH);

        jPanel5.setLayout(new java.awt.GridLayout(4, 1));

        jButton2.setText("방 만들기");
        jPanel5.add(jButton2);

        jButton3.setText("방 참여");
        jPanel5.add(jButton3);

        jButton4.setText("쪽지보내기");
        jPanel5.add(jButton4);

        jButton5.setText("종 료");
        jPanel5.add(jButton5);

        jPanel4.add(jPanel5, BorderLayout.PAGE_END);

        jScrollPane2.setViewportView(user_list);

        jPanel4.add(jScrollPane2, BorderLayout.CENTER);

        card2.add(jPanel4, BorderLayout.LINE_END);

        getContentPane().add(card2, "roomList");
        
        //채팅 화면 구성하기--------------------------------
        card3 = new JPanel(new BorderLayout());
        card3_e = new JPanel(new BorderLayout());
        card3_s = new JPanel(new BorderLayout());
        
        card3.add(new JScrollPane(ta = new JTextArea()));
        ta.setEditable(false);//비활성화
        
        card3_e.add(new JLabel("[참여자]"), BorderLayout.NORTH);
        card3_e.add(new JScrollPane(join_list = new JList<String>()));
        card3_e.add(out_bt = new JButton("방 나가기"), BorderLayout.SOUTH);
        card3.add(card3_e, BorderLayout.EAST);
        
        card3_s.add(input_tf = new JTextField());
        card3_s.add(send_bt = new JButton("보내기"), BorderLayout.EAST);
        card3.add(card3_s, BorderLayout.SOUTH);
        
        //card3를 현재창에 "chatRoom"이라는 이름으로 추가
        getContentPane().add(card3, "chatRoom");
        //--------------------------------------------------
        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            
        }
        //</editor-fold>

        /* Create and display the form */
        new ChatFrame();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel card1;
    private JPanel card2;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextField jTextField1;
    private JList<String> room_list;
    private JList<String> user_list;
    // End of variables declaration//GEN-END:variables
}