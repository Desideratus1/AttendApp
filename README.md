# AttendApp
What is this I don't even?

  Attendance App Group 8 Senior Design Spring 2017 Android App (Hopefully will son be IOS as well)
  All work done in this repository is done using Android Studio (latest verison as of 2/22/2017) using an emulator (First through Nexus 5 and later through KittyKat versions of Android phones. The distributions form the Android phones give the impression that using the KittyKat API 19 we will be able to run this app on ~75% of all android phones.
  
Where is everything?

  Java files containing the various Activities and other files are located within
  * AttendApp/app/src/main/java/attendApp/attendApp/
  
Confusing but unavoidable for now.
  Layout files are located in 
  * AttendApp/app/src/main/res/layout/
  
These contain the buttons, text fields etc. that dictate where each the various pieces of the activity are located.
  
Reference for the Java files
  LoginActivity.java is the first java file loaded into the program and its corresponding layout (login_layout) will be the first screen the user sees. AdministratorDashboard currently does not have any functionality and remains untested.
  
  General TODO.
  
 * Logins - Need to actually send data to and from the Raspberry Pi containing login information. Also need to determine if the user is a teacher or not.
 * Register - Same as Logins, we need to send data to and from. Differentiating between teachers and students may be done with a checkbox
 * StudentDashboard - Sending data to and from Raspberry Pi. Also needs to get the GPS coordinates of the Raspberry Pi and the student to tell exactly how far away they even are. Problem being how accurate is the GPS system that Google uses, and how do we translate longitude and latitude into a distance from one object to another?
 * GENERAL - Raspberry Pi communication, encryption. BACK BUTTONS must be placed everywhere.
 * DOCUMENTATION MUST OCCUR BEFORE TOO MUCH PROGRESS IS MADE.
