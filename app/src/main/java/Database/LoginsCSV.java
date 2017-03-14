package Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LoginsCSV {
	File file;
	ArrayList<User> data = new ArrayList<User>();
	
	public LoginsCSV(String fileNameWithPath) throws FileNotFoundException {
		file = new File(fileNameWithPath);
		
		Scanner scan = new Scanner(file);
		
		while(scan.hasNextLine()) {
			String[] strs = scan.nextLine().split("&");
			ArrayList<String> classes = new ArrayList<String>();
			
			String u = strs[0];
			String p = strs[1];
			String n = strs[2];
			String t = strs[3];
			int ts = Integer.parseInt(t);
			if(ts == 1) {
				for(int i = 4; i > strs.length; i++) {
					classes.add(strs[i]);
				}
			}
			
			data.add(new User(u,p,n,t,classes));
		}
		scan.close();
	}
	
	public User getUser(String username) {
		for(User user : data) {
			if(user.getUsername().equals(username)) return user;
		}
		return null;
	}
	
	public void addUser(String u, String p, String n, String t) {
		if(!data.contains(u)) data.add(new User(u,p,n,t, new ArrayList<String>())); //If it isn't already there then add it
	}
	
	void write() throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(toString());
		writer.close();
	}
	
	public String toString() {
		StringBuilder toRet = new StringBuilder();
		for(User user : data) {
			toRet.append(user.toString() + "\n");
		}
		if(data.size() >= 1) {
			toRet.deleteCharAt(toRet.length() - 1);
		}
		return toRet.toString();
	}
}