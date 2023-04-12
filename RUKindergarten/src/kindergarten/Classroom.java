package kindergarten;

import java.rmi.StubNotFoundException;

import javax.lang.model.util.ElementScanner6;
import javax.naming.NameAlreadyBoundException;

/**
 * This class represents a Classroom, with:
 * - an SNode instance variable for students in line,
 * - an SNode instance variable for musical chairs, pointing to the last student in the list,
 * - a boolean array for seating availability (eg. can a student sit in a given seat), and
 * - a Student array parallel to seatingAvailability to show students filed into seats 
 * --- (more formally, seatingAvailability[i][j] also refers to the same seat in studentsSitting[i][j])
 * 
 * @author Ethan Chou
 * @author Kal Pandit
 * @author Maksims Kurjanovics Kravcenko
 */
public class Classroom {
    private SNode studentsInLine;             // when students are in line: references the FIRST student in the LL
    private SNode musicalChairs;              // when students are in musical chairs: references the LAST student in the CLL
    private boolean[][] seatingAvailability;  // represents the classroom seats that are available to students
    private Student[][] studentsSitting;      // when students are sitting in the classroom: contains the students

    /**
     * Constructor for classrooms. Do not edit.
     * @param l passes in students in line
     * @param m passes in musical chairs
     * @param a passes in availability
     * @param s passes in students sitting
     */
    public Classroom ( SNode l, SNode m, boolean[][] a, Student[][] s ) {
		studentsInLine      = l;
        musicalChairs       = m;
		seatingAvailability = a;
        studentsSitting     = s;
	}
    /**
     * Default constructor starts an empty classroom. Do not edit.
     */
    public Classroom() {
        this(null, null, null, null);
    }

    /**
     * This method simulates students coming into the classroom and standing in line.
     * 
     * Reads students from input file and inserts these students in alphabetical 
     * order to studentsInLine singly linked list.
     * 
     * Input file has:
     * 1) one line containing an integer representing the number of students in the file, say x
     * 2) x lines containing one student per line. Each line has the following student 
     * information separated by spaces: FirstName LastName Height
     * 
     * @param filename the student information input file
     */
    public void makeClassroom ( String filename ) {
        //Set file
        StdIn.setFile(filename);

        //read int for loop
        int numOfStud = StdIn.readInt();
        //loop for each student
        for(int i=0;i<numOfStud;i++){
            //read first name
            //read last name
            //read height
            //make student object
            Student s = new Student(StdIn.readString(), StdIn.readString(), StdIn.readInt());
            //Make node
            SNode n = new SNode(s, null);
            //Need to add the first one to line so the loop will work
            if(studentsInLine==null){
                studentsInLine = n;
            } else {
                //loop for the rest of students
                //using while so I can refer to ptr
                SNode ptr = studentsInLine;
                //but i need to check for if the name is before the first name in the list
                if(ptr.getStudent().compareNameTo(s)>0){
                    n.setNext(ptr);
                    studentsInLine = n;
                } else {
                    while(ptr!=null){
                        //I want the case where the name before is neg and the name after is pos
                        if(ptr.getNext()!=null){
                            if(ptr.getStudent().compareNameTo(s)<0 && ptr.getNext().getStudent().compareNameTo(s)>0){
                                n.setNext(ptr.getNext());
                                ptr.setNext(n);
                                break;
                            }
                        } else {
                            ptr.setNext(n);
                            ptr = ptr.getNext();
                        }
                        //move to next SNode
                        ptr = ptr.getNext();
                    }
                }
            //want the first instance that the current name is less than but the next name is greater 

            //if name unable to be added, add to FRONT
            }
        }
            
    }

    /**
     * 
     * This method creates and initializes the seatingAvailability (2D array) of 
     * available seats inside the classroom. Imagine that unavailable seats are broken and cannot be used.
     * 
     * Reads seating chart input file with the format:
     * An integer representing the number of rows in the classroom, say r
     * An integer representing the number of columns in the classroom, say c
     * Number of r lines, each containing c true or false values (true denotes an available seat)
     *  
     * This method also creates the studentsSitting array with the same number of
     * rows and columns as the seatingAvailability array
     * 
     * This method does not seat students on the seats.
     * 
     * @param seatingChart the seating chart input file
     */
    public void setupSeats(String seatingChart) {
        //read in seating.in
        StdIn.setFile(seatingChart);
        //read the row and col count
        int rows = StdIn.readInt();
        int cols = StdIn.readInt();

        //make a seatingAvailability array
        seatingAvailability = new boolean[rows][cols]; 
        //make a studentsSitting array
        studentsSitting = new Student[rows][cols];
        //Traversing the file
        for(int i=0; i<rows;i++){
            for(int j=0;j<cols;j++){
                if(StdIn.readBoolean()){//change to true when it finds new
                    seatingAvailability[i][j] = true;
                }
            }
        }
    }

    /**
     * 
     * This method simulates students taking their seats in the classroom.
     * 
     * 1. seats any remaining students from the musicalChairs starting from the front of the list
     * 2. starting from the front of the studentsInLine singly linked list
     * 3. removes one student at a time from the list and inserts them into studentsSitting according to
     *    seatingAvailability
     * 
     * studentsInLine will then be empty
     */
    public void seatStudents () {

        //loop through it and load then into seats
        for(int i=0;i<seatingAvailability.length;i++){
            for(int j=0;j<seatingAvailability[i].length;j++){
                //find an open seat
                if(seatingAvailability[i][j] && studentsSitting[i][j]==null){
                    //1 check musical chairs CLL
                    //there is only one winner from musical chairs
                    //condition for only one person in musical chairs if all kids are in musical chairs
                    if(musicalChairs != null && musicalChairs.getNext()==musicalChairs){
                        studentsSitting[i][j] = musicalChairs.getStudent();
                        musicalChairs = null;
                        //seatingAvailability[i][j] = false;
                    } else 
                    //2 Start from beginning of studentInLine
                    //put the FIRST student into a chair
                    //ONLY if there are students in the line
                    if(studentsInLine!=null){
                        studentsSitting[i][j] = studentsInLine.getStudent();
                        //3 Clean up standing students (studentsInLine)
                        studentsInLine = studentsInLine.getNext(); 
                        //seatingAvailability[i][j] = false;
                    }
                    
                }
            }
        }
    }

    /**
     * Traverses studentsSitting row-wise (starting at row 0) removing a seated
     * student and adding that student to the end of the musicalChairs list.
     * 
     * row-wise: starts at index [0][0] traverses the entire first row and then moves
     * into second row.
     */
    public void insertMusicalChairs () {
        
        //go through studentSitting
        for(int i=0;i<studentsSitting.length;i++){
            for(int j=0;j<studentsSitting[i].length;j++){
                //look for open seats
                Student studentPlayer = studentsSitting[i][j];
                if(studentPlayer!=null){
                    //add student to musical chair ll  
                    addToLast(studentPlayer);
                    //make no student in that seat
                    studentsSitting[i][j] = null;
                    //make that seat available again
                    //seatingAvailability[i][j] = true;
                }
            }
        }
     }

    /**
     * 
     * This method repeatedly removes students from the musicalChairs until there is only one
     * student (the winner).
     * 
     * Choose a student to be elimnated from the musicalChairs using StdRandom.uniform(int b),
     * where b is the number of students in the musicalChairs. 0 is the first student in the 
     * list, b-1 is the last.
     * 
     * Removes eliminated student from the list and inserts students back in studentsInLine 
     * in ascending height order (shortest to tallest).
     * 
     * The last line of this method calls the seatStudents() method so that students can be seated.
     */
    public void playMusicalChairs() {
        //only play if there are people in muscial chair
        if(musicalChairs!=null){
            //setting the seed
            //make a new node to become the front of the studentsInLine
            //place a node before the beginning of the list so I only have to go down the list
            SNode startOfLine = new SNode(null, studentsInLine);

            //loop until only one remain
            while(musicalChairs.getNext()!=musicalChairs){
                //playing muscial chairs involves random # to remove
                //need to find number of people playing muscical chairs
                int players = 1;
                SNode counter = musicalChairs.getNext();
                while(counter!=musicalChairs){
                    players++;
                    counter = counter.getNext();
                }
                
                int loserLoc = StdRandom.uniform(players);//CORRECTION: where 0 is the FRONT and N is the back
                SNode loser = musicalChairs;//start one back so I have a ref to the one before the one I want to delete
                //find the loser
                for(int i= 0;i<(loserLoc);i++){
                    loser = loser.getNext();
                }

                //save loser to a node
                SNode standingStudent = new SNode(null, null);
                //handle if loser is the last person in line
                if(loserLoc==players-1){
                    SNode chaser = musicalChairs;
                    while(chaser.getNext()!=musicalChairs){
                        chaser = chaser.getNext();
                    }
                    standingStudent.setStudent(chaser.getNext().getStudent());
                    chaser.setNext(chaser.getNext().getNext());
                    musicalChairs = chaser;
                } else{
                    //loser is spot right after the ptr loser
                    standingStudent.setStudent(loser.getNext().getStudent());
                    //"remove" loser from muscialChairs
                    loser.setNext(loser.getNext().getNext());
                }
                

                //loop for sorting studentsInLine
                //handle for first loser
                if(startOfLine.getNext()==null){
                    startOfLine.setNext(standingStudent);
                } else {
                    SNode ptr = startOfLine;
                    while(ptr.getNext()!=null){
                        //so if the next student is taller than the loser then we place loser before next
                        if(ptr.getNext().getStudent().getHeight()>=standingStudent.getStudent().getHeight()){
                            standingStudent.setNext(ptr.getNext());
                            ptr.setNext(standingStudent);
                            break;
                        }
                        ptr = ptr.getNext();
                    }
                    //they are the tallest in the list
                    if(ptr.getNext()==null){
                        ptr.setNext(standingStudent);
                    }
                }
            }
            //remove that extra node before sitting people
            studentsInLine = startOfLine.getNext();
            startOfLine.setNext(null);
            //seat students
            seatStudents();
        }
    } 

    /**
     * Insert a student to wherever the students are at (ie. whatever activity is not empty)
     * Note: adds to the end of either linked list or the next available empty seat
     * @param firstName the first name
     * @param lastName the last name
     * @param height the height of the student
     */
    public void addLateStudent ( String firstName, String lastName, int height ) {
        //Make a new student object
        Student lateStudent = new Student(firstName, lastName, height);
        SNode ls = new SNode(lateStudent,null);
        //find which activity the kids are playing
        if(studentsInLine!=null){
            //add to end of linked list
            //for students standing
            for(SNode ptr = studentsInLine; ptr!=null; ptr = ptr.getNext()){
                if(ptr.getNext()==null){
                    ptr.setNext(ls);
                    break;//once I add I am done
                }
            }
        } else if (musicalChairs!=null){
            //for musical chairs
            addToLast(lateStudent);
        } else {
            //add to first avail chair
            //"easiest way" to do this is to do add to list and then run searStudents
            studentsInLine = ls;
            seatStudents();
        }
        
    }

    /**
     * A student decides to leave early
     * This method deletes an early-leaving student from wherever the students 
     * are at (ie. whatever activity is not empty)
     * 
     * Assume the student's name is unique
     * 
     * @param firstName the student's first name
     * @param lastName the student's last name
     */
    public void deleteLeavingStudent ( String firstName, String lastName ) {
        //find current activity 
        if(studentsInLine!=null){
            //remove then from linked list by changing next nodes
            
            for(SNode ptr=studentsInLine; ptr.getNext()!=null; ptr = ptr.getNext()){
                //what if person is first?
                if(ptr.getStudent().getFullName().equals(firstName + " " + lastName)){
                    studentsInLine=ptr.getNext();
                    break;
                } else if (ptr.getNext().getStudent().getFullName().equalsIgnoreCase(firstName + " " + lastName)){
                    ptr.setNext(ptr.getNext().getNext());
                    break;
                } 
            }
        } else if (musicalChairs!=null){
            //search and remove
            //need to use a while loop
            SNode ptr = musicalChairs;
            do{
                if(ptr.getNext().getStudent().getFullName().equalsIgnoreCase(firstName + " " + lastName)){
                    ptr.setNext(ptr.getNext().getNext());
                    musicalChairs = ptr;
                } 
                ptr = ptr.getNext();
            } while(ptr!=musicalChairs);
        } else {
            //otherwise remove from seating chart make their seat open
            for(int i=0;i<studentsSitting.length;i++){
                for(int j=0;j<studentsSitting[i].length;j++){
                    if(studentsSitting[i][j]!=null && studentsSitting[i][j].getFullName().equalsIgnoreCase(firstName + " " + lastName)){
                        studentsSitting[i][j] = null;
                        //seatingAvailability[i][j] = true;
                    }
                }
            }
        }
        
    }

    //helper methods
    //addToLast for circular linked list
    private void addToLast(Student s){
        //two pointers one for head and one for tail
        //algorithms taken from Fall 2022 test
        SNode tail = musicalChairs;
        musicalChairs = new SNode(s, null);

        //handle case that it is empty 
        if(tail==null){
            musicalChairs.setNext(musicalChairs);
        } else {
            //for a CLL the old tail is the node before the new one 
            musicalChairs.setNext(tail.getNext());
            tail.setNext(musicalChairs);
        }
    }



    /**
     * Used by driver to display students in line
     * DO NOT edit.
     */
    public void printStudentsInLine () {

        //Print studentsInLine
        StdOut.println ( "Students in Line:" );
        if ( studentsInLine == null ) { StdOut.println("EMPTY"); }

        for ( SNode ptr = studentsInLine; ptr != null; ptr = ptr.getNext() ) {
            StdOut.print ( ptr.getStudent().print() );
            if ( ptr.getNext() != null ) { StdOut.print ( " -> " ); }
        }
        StdOut.println();
        StdOut.println();
    }

    /**
     * Prints the seated students; can use this method to debug.
     * DO NOT edit.
     */
    public void printSeatedStudents () {

        StdOut.println("Sitting Students:");

        if ( studentsSitting != null ) {
        
            for ( int i = 0; i < studentsSitting.length; i++ ) {
                for ( int j = 0; j < studentsSitting[i].length; j++ ) {

                    String stringToPrint = "";
                    if ( studentsSitting[i][j] == null ) {

                        if (seatingAvailability[i][j] == false) {stringToPrint = "X";}
                        else { stringToPrint = "EMPTY"; }

                    } else { stringToPrint = studentsSitting[i][j].print();}

                    StdOut.print ( stringToPrint );
                    
                    for ( int o = 0; o < (10 - stringToPrint.length()); o++ ) {
                        StdOut.print (" ");
                    }
                }
                StdOut.println();
            }
        } else {
            StdOut.println("EMPTY");
        }
        StdOut.println();
    }

    /**
     * Prints the musical chairs; can use this method to debug.
     * DO NOT edit.
     */
    public void printMusicalChairs () {
        StdOut.println ( "Students in Musical Chairs:" );

        if ( musicalChairs == null ) {
            StdOut.println("EMPTY");
            StdOut.println();
            return;
        }
        SNode ptr;
        for ( ptr = musicalChairs.getNext(); ptr != musicalChairs; ptr = ptr.getNext() ) {
            StdOut.print(ptr.getStudent().print() + " -> ");
        }
        if ( ptr == musicalChairs) {
            StdOut.print(musicalChairs.getStudent().print() + " - POINTS TO FRONT");
        }
        StdOut.println();
    }

    /**
     * Prints the state of the classroom; can use this method to debug.
     * DO NOT edit.
     */
    public void printClassroom() {
        printStudentsInLine();
        printSeatedStudents();
        printMusicalChairs();
    }

    /**
     * Used to get and set objects.
     * DO NOT edit.
     */

    public SNode getStudentsInLine() { return studentsInLine; }
    public void setStudentsInLine(SNode l) { studentsInLine = l; }

    public SNode getMusicalChairs() { return musicalChairs; }
    public void setMusicalChairs(SNode m) { musicalChairs = m; }

    public boolean[][] getSeatingAvailability() { return seatingAvailability; }
    public void setSeatingAvailability(boolean[][] a) { seatingAvailability = a; }

    public Student[][] getStudentsSitting() { return studentsSitting; }
    public void setStudentsSitting(Student[][] s) { studentsSitting = s; }

}
