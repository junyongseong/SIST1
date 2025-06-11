package pm;

import java.io.IOException;
import java.util.ArrayList;

public class ChatRoom {
	ArrayList<CopyClient> ru_list;//방에 참여한 사용자들
	String roomName;
	ChatServer server;
	
	public ChatRoom(String n,ChatServer server) {
		this.roomName = n;
		this.server = server;// 나중에 방 나가기를 할 때
		// 현재 ru_list에서 삭제가 된 후, 다시 ChatServer에 있는
		// u_list에 CopyClient를 추가해야 하기 때문에 필요하다
		ru_list = new ArrayList<>();
	}
	
	public void sendMsg(Protocol p) { // 현재 방에 접속되어 있는 모든 사용자들에게
										// 데이터를 전달하는 기능
		//for(int i=0; i<ru_list.size(); i++) {
		//	CopyClient cc = ru_list.get(i);
		for( CopyClient cc:ru_list ) {
			
			try {
				cc.out.writeObject(p);
				cc.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getOut(CopyClient cc) { //방 나가기
		ru_list.remove(cc);
		
		//만약 지금 나가는 CopyClient가 마지막일 때
		// 현재 방은 삭제되어야 한다.
		if(ru_list.size() == 0){
			// 자신이 마지막으로 나가는 경우는
			// 그냥 현재 방을 삭제하면 된다.
			server.r_list.remove(this);
		}else{
			//아직 방에 남아있는 사람이 있는 경우
			// 명단갱신과 메세지 전달을 해야 한다.
			Protocol protocol = new Protocol();
			protocol.setCmd(4);
			//원본 클라이언트에게 전달할 프로토콜은 다시 작업한다.
			//먼저 방에 참여한 참여자 명단 수집
			protocol.setUser_names(getJoinNames());
			protocol.setMsg("*** "+cc.nickName+"님 퇴장 ***\r\n");

			//이제 준비된 프로토콜은 현재 방에 참여된
			//사용자들에게만 보낸다.
			sendMsg(protocol);
		}
		//대기실로 추가되어야 한다. (CopyClient가)
		server.addClient(cc);
	}
	
	public void joinUser(CopyClient cc) { //방 참여
		ru_list.add(cc);
		
		Protocol protocol = new Protocol();
		protocol.setCmd(4);
		//원본 클라이언트에게 전달할 프로토콜은 다시 작업한다.
		//먼저 방에 참여한 참여자 명단 수집
		protocol.setUser_names(getJoinNames());
		protocol.setMsg("*** "+cc.nickName+"님 입장 ***\r\n");
		
		//이제 준비된 프로토콜은 현재 방에 참여된 
		//사용자들에게만 보낸다.
		sendMsg(protocol);
	}
	
	//현재 방에 참여하고 있는 사용자들의 명단
	public String[] getJoinNames() { 
		String[] names = new String[ru_list.size()];
		int i=0;
		for(CopyClient cc : ru_list) {
			names[i++] = cc.getNickName();
		}
		return names;
	}
	
	
	
	
}
