package csvwriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class attendancePeriodCSV {
	File file;
	ArrayList<String> heds = new ArrayList<String>();
	String[] period;
	ArrayList<String[]> data = new ArrayList<String[]>();
	
	public CSVFile(String fileNameWithPath) throws FileNotFoundException {
		file = new File(fileNameWithPath);
		heds = new ArrayList<String>();
		
		Scanner scan = new Scanner(file);
		String[] headers = scan.nextLine().split(",");
		
		for(String str : headers) {
			heds.add(str);
		}
		
		while(scan.hasNextLine()) {
			data.add(scan.nextLine().split(","));
		}
		scan.close();
	}
	
	void beginAttendancePeriod() {
		period = new String[heds.size()];
		for(int i = 0; i < period.length; i++) {
			period[i] = "0";
		}
		
		period[0] = Integer.toString((data.size()));
	}
	
	void endAttendancePeriod() throws IOException {
		data.add(period);
		FileWriter writer = new FileWriter(file);
		writer.write(toString());
		writer.close();
	}
	
	void submitAttendance(String name) {
		int index = heds.indexOf(name);
		if (index == -1) return; //error
		
		period[index] = "1";
	}
	
	public String toString() {
		StringBuilder toReturn = new StringBuilder();
		
		for(String str : heds) {
			toReturn.append(str + ",");
		}
		toReturn.deleteCharAt(toReturn.length()-1);
		toReturn.append("\n");
		
		for(String[] list : data) {
			for(String str : list) {
				toReturn.append(str +  ",");
			}
			toReturn.deleteCharAt(toReturn.length()-1);
			toReturn.append("\n");
		}
		return toReturn.toString();
	}
}
