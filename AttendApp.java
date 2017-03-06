import java.io.*;
import java.util.*;
import com.opencsv.*;


public class AttendApp{
	public static void main (String args[]) throws IOException {
		
		Console c = System.console();
       			 if (c == null) {
           			 System.err.println("No console.");
            			 System.exit(1);
        		 }//end if

		System.out.println("\nWelcome to AttendApp.\nPlease enter your administrative username and password.\n");
		String username = c.readLine("\nUsername: ");
		char[] password = c.readPassword("\nPassword: ");
		boolean flagU = checkUser(username, password);

		while(flagU == false){
			System.out.println("Incorrect Username/Password.\nPlease try again.\n");
			username = c.readLine("\nUsername: ");
			password = c.readPassword("\nPassword: ");
			flagU = checkUser(username, password);
		} //end while

		System.out.println("\n Welcome back, " + username + "\n");
		boolean exitProgram = false;
		String classSelection = null;

		while(exitProgram == false){
			System.out.println("\nSelect an option with a number: \n 1. Select a Class\n 2. Exit\n");
			String menuSelect = c.readLine(">>>>>>>>>");
			menuSelect.replaceAll("\\s+","");
			switch (menuSelect){
				case "1":

					boolean classCheck = false;
					while(classCheck == false){
						classSelection = c.readLine("\nPlease specify class with abbreviation and course number, ex: CS100, or E for to return.\n>>>>>");
						classCheck = checkClass(classSelection);
						if(classSelection.equals("E"))
							break;
						if(classCheck == false)
							System.out.println("\nClass not Found. Try again.\n");
					}//end while

					if(classSelection.equals("E"))
						break;

					boolean flagUU = false;
					while(flagUU == false){
						System.out.println("\nSelect an option for " + classSelection + "\n1. Display Attendance Record\n2. Start Attendance Period\n3. Delete Class\n4. Return\n");
						String optionSelect = c.readLine(">>>>>>>>");
						switch(optionSelect){
							case"1":
								System.out.println("\nDisplaying Attendance Record for " +classSelection +"\n");
								printAttendanceRecord(classSelection);
								break;
							case"2":
								System.out.println("\nBegining Attendance Period\n");
								beginAttendance();
								break;
							case"3":
								String confirm = c.readLine("Are you sure? Y/N >>>> ");
								if(confirm.toLowerCase().equals("n"))
									break;
								if(confirm.toLowerCase().equals("y")){
									System.out.println("Deleting Class...\n");
									deleteClass(classSelection);
									flagUU = true;
									break;
								}
								System.out.println("Input not recognized, returning.");
								break;
							case"4":
								flagUU = true;
								break;
							default:
								System.out.println("Option not recognized.");
								break;

					}//end switch2

				}//end while

				break;
			case "2":
				exitProgram = true;
				break;
			default:
				System.out.println("\nInvalid selection, Try again.\n");
				break;

		}//end Switch1
	}//end While loop
}//end Main


	//Checks if username and password is in users.csv and returns boolean
	static boolean checkUser(String username, char[] password) throws IOException{
		
		String userDatabase = "users.csv";
		CSVReader csvReader = new CSVReader(new FileReader(userDatabase));
		String row[] = null;
		String usernameCheck = null;
		String passwordTemp = null;
		while((row = csvReader.readNext()) != null){
			usernameCheck = row[0];
			if(usernameCheck.equals(username)){
				passwordTemp = row[1];
				if(Arrays.equals(password, passwordTemp.toCharArray()))
					return true;
			}	

		}
		csvReader.close();
		return false;
		
	}//end checkUser

	//checks if the csv file for the class exists and returns boolean
	static boolean checkClass(String className){
		File tempFile = new File(className+".csv");
		boolean classCheck = tempFile.exists();
		return classCheck;
		
	}//end checkClass

	//prints the attendance record
	static void printAttendanceRecord(String classSelection)throws IOException{

			//NECCESARY VARIABLE FOR NUMBER OF STUDENTS OTHERWISE THIS BECOMES DIFFICULT, NEED WAY TO GET NUMBER OF KIDS IN THE CLASS
				//TESTING WITH ONLY 3 KIDS

		System.out.println("Date \t\t Name \t\t Name \t\t Name \n");
		CSVReader csvReader = new CSVReader(new FileReader(classSelection+".csv"));
		String row[] = null;
		while((row = csvReader.readNext()) != null){
			System.out.print(row[0]);
			System.out.print(" \t\t ");
			System.out.print(row[1]);
			System.out.print(" \t\t ");
			System.out.print(row[2]);
			System.out.print(" \t\t ");	
			System.out.print(row[3]);
			System.out.print("\n");
		}
		csvReader.close();

	}

	//deletes specified class csv file
	static void deleteClass(String classSelection){
		File deleteFile = new File(classSelection+".csv");
		deleteFile.delete();

	}//end printAttendanceRecord
	static void beginAttendance(){
	//TODO
	}//End beginAttendance()

		
} // End Class
