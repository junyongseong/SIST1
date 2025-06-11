package pm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class CopyClient extends Thread {

	Socket s;
	ChatServer server;
	//통신을 위한 스트림들
	ObjectOutputStream out;
	ObjectInputStream in;
	
	String ip, nickName;
	
	ChatRoom currentRoom;//현재 참여하고 있는 방! 만약 currentRoom이 null일때는
						// 현재 사용자는 대기실에 있는 것이다.
	
	public CopyClient(Socket s, ChatServer ex3_ChatServer) {
		this.s = s;
		this.server = ex3_ChatServer;
		
		// in/out스트림들 생성, ip도 얻어내야 한다.
		try {
			in = new ObjectInputStream(s.getInputStream());
			out = new ObjectOutputStream(s.getOutputStream());
			ip = s.getInetAddress().getHostAddress();//접속자 IP
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//생성자의 끝

	@Override
	public void run() {
		// 현재 스레드는 서버에서 실제 클라이언트를 대신하면서
		// 언제, 어느 때에 원격에 있는 클라이언트가 서버로 메세지를 보낼지 모르므로
		// 항상 inputstream을 read를 수행하여 메세지가 올 때까지 기다려야 한다.
		bk:while(true) {
			try {
				// 스트림으로부터 객체를 읽어낸다.
				Object obj = in.readObject();//대기상태
				if(obj != null) {
					Protocol protocol = (Protocol) obj;
					//protocol의 cmd값이 뭐냐에 따라 작업의 구분을 구현한다.
					switch(protocol.getCmd()) {
						case 3:
							//원격의 클라이언트에 있는 스레드를 소멸시키기 위해 
							//메세지 보내온 것이다.
							out.writeObject(protocol);
							out.flush();
							break bk;
						case 1:
							// 서버에 접속한 경우는
							// 사용자가 입력한 대화명을 얻어내어 nickName에 저장한다.
							this.nickName = protocol.getMsg();
						
							//환영메세지를 보내기 위해 Ex3_Protocol객체 생성
							Protocol p = new Protocol();
							p.cmd = 1;
							p.setUser_names(server.getNames());//명단 수집
							p.setRoom_names(server.getRoomNames());//방 목록 수집
							
							server.sendProtocol(p);//접속자 모두에게 전달!
							break;
						case 2:
							//채팅메세지
							// 메세지 앞에 nickName을 붙여서 msg에 다시 저장하자!
							protocol.setMsg(nickName+":"+protocol.getMsg());
							
							server.sendProtocol(protocol);//접속자 모두에게 전달
							break;
						case 4:
							// 방 만들기
							//ChatRoom cr = new ChatRoom(protocol.getMsg(), server);
							//server.r_list.add(cr);
							//currentRoom = cr;
							currentRoom = new ChatRoom(protocol.getMsg(), server);
							server.r_list.add(currentRoom);
							
							//대기실에서 현재 CopyClient를 삭제
							server.removeClient(this);
							currentRoom.joinUser(this);
							
							break;
						case 5:
							if(currentRoom != null){
								//원격에 있는 클라이언트가 [방 나가기]라는
								//버튼을 클릭했을 때 수행!
								currentRoom.getOut(this);
								currentRoom = null;//************
								protocol.setUser_names(server.getNames());
								protocol.setRoom_names(server.getRoomNames());
								out.writeObject(protocol);
								out.flush();
							}
							break;
						case 6:
							//사용자 선택한 방의 index값을 얻어내자
							int idx = protocol.index;
							//참여하는 방을 가져오기
							currentRoom = server.r_list.get(idx);

							server.removeClient(this);//대기실에서 자신을 삭제
							currentRoom.joinUser(this);
							break;
						case 7: // 채팅 메시지 전송
							ChatRoom room = this.currentRoom;
							if (room != null) {
								Protocol msgProtocol = new Protocol();
								msgProtocol.setCmd(7);
								msgProtocol.setMsg("[" + nickName + "] " + protocol.getMsg() + "\r\n");
								room.sendMsg(msgProtocol);
							}
							break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}//무한반복의 끝
		
		try {
			if(out != null)
				out.close();
			if(in != null)
				in.close();
			if(s != null)
				s.close();
			
			if(currentRoom != null)
				currentRoom.getOut(this);
			else {
				//서버의 ArrayList에서 현재객체를 삭제한다.
				server.removeClient(this);
				
				//서버에 다른 접속자들에게 현재객체가 접속해제하다는 메세지를 보낸다.
				Protocol p = new Protocol();
				p.cmd = 1;
				//String[] ar = server.getNames();
				//p.setUser_names(ar);
				p.setUser_names(server.getNames());
				
				server.sendProtocol(p);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	// 현재 이름을 반환하는 기능
	public String getNickName() {
		return nickName;
	}
}