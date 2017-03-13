package Database;

import java.util.ArrayList;

public class User {
	private String username;
	private String password;
	private String fullName;
	private String isTeacher;
	private ArrayList<String> classes;
	
	public User(String user, String pass, String name, String isT, ArrayList<String> clas) {
		username = user;
		password = pass;
		fullName = name;
		isTeacher = isT;
		classes = clas;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return username;
	}
	
	public String getName() {
		return username;
	}
	
	public boolean isTeacher() {
		if(isTeacher.equals("1")) return true;
		else return false;
	}
	
	public String[] getClassList() {
		return (String[]) classes.toArray();
	}
	
	public void addClass(String str) {
		classes.add(str);
	}
	
	public String toString() {
		StringBuilder toRet = new StringBuilder();
		toRet.append(username + "&" + password + "&" + fullName + "&" + isTeacher + "&");
		for(String str : classes) {
			toRet.append(str + "&");
		}
		toRet.deleteCharAt(toRet.length()-1);
		return toRet.toString();
	}
	
}
