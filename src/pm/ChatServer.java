package pm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

	ServerSocket ss;
	
	Thread thread = new Thread() {

		@Override
		public void run() {
			while(true) {
				try {
					Socket s = ss.accept();// 접속자가 올 때까지 기다린다.
					CopyClient cc = new CopyClient(s, ChatServer.this);
					u_list.add(cc);
					cc.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}// 무한반복
		}
		
	};
	
	ArrayList<CopyClient> u_list; //대기자들
	ArrayList<ChatRoom> r_list; //방 목록
	
	public ChatServer() {
		try {
			ss = new ServerSocket(5555);
			System.out.println("서버 시작");
			u_list = new ArrayList<>();//대기자들이 접속하면 저장될 곳!
			r_list = new ArrayList<>();//방이 만들어지면 저장될 곳!
			
			thread.start();//서버 스레드 시작!!
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void removeClient(CopyClient cc) {
		u_list.remove(cc);
		
		//대기실 명단과 방 목록을 갱신하도록 프로토콜 작업
		Protocol p = new Protocol();
		p.setCmd(1);
		p.setUser_names(getNames());
		p.setRoom_names(getRoomNames());//방 목록 수집
		
		//대기실 모두에게 전달
		sendProtocol(p);
	}

	public void addClient(CopyClient cc) {
		u_list.add(cc);

		//대기실 명단과 방 목록을 갱신하도록 프로토콜 작업
		Protocol p = new Protocol();
		p.setCmd(1);
		p.setUser_names(getNames());
		p.setRoom_names(getRoomNames());//방 목록 수집

		//대기실 모두에게 전달
		sendProtocol(p);
	}
	
	//대기자 모두에게 전달하는 기능
	public void sendProtocol(Protocol p) {
		for(int i=0; i<u_list.size(); i++) {
			CopyClient cc = u_list.get(i);
			
			try {
				cc.out.writeObject(p);
				cc.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//대기실 명단을 수집하여 반환하는 기능
	public String[] getNames() {
		// ArrayList에 있는 요소들에게 이름을 받아서 배열화 시킨다.
		String[] names = new String[u_list.size()];
		for(int i=0; i<u_list.size(); i++) {
			//클라이언트의 복사본을 하나씩 얻어낸다.
			CopyClient cc = u_list.get(i);
			names[i] = cc.getNickName();
		}
		return names;
	}
	
	//방 목록을 수집하여 반환하는 기능
	public String[] getRoomNames() {
		// ArrayList에 있는 요소들에게 이름을 받아서 배열화 시킨다.
		String[] names = new String[r_list.size()];
		int i = 0;
		for(ChatRoom cr : r_list) {
			//채팅방 객체를 하나씩 얻어내어 방 제목을 수집한다.
			names[i++] = cr.roomName;
		}
		return names;
	}
	
	public static void main(String[] args) {
		// 프로그램 시작
		new ChatServer();
	}

}
