package pm;

import java.io.Serializable;

public class Protocol implements Serializable {

	int cmd; // 1:접속, 2:채팅, 3:종료, 4:방만들기, 5:방나가기....
	String msg;
	int index;
	
	String[] user_names;// 대기자 명단
	String[] room_names;// 방 제목들
	public int getCmd() {
		return cmd;
	}
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String[] getUser_names() {
		return user_names;
	}
	public void setUser_names(String[] user_names) {
		this.user_names = user_names;
	}
	public String[] getRoom_names() {
		return room_names;
	}
	public void setRoom_names(String[] room_names) {
		this.room_names = room_names;
	}

}
