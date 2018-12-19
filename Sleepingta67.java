import java.util.concurrent.Semaphore;
import java.util.*;
class Sleepingta67
{
	public static void main(String args[])throws Exception
	{
		Scanner sc=new Scanner(System.in);
		int numStud,numChairs;

		/*Number of students and no. of Chairs*/
        System.out.println("Enter Total No. Of Students : ");
		numStud=sc.nextInt();
		System.out.println("Enter Total No. Of Chairs : ");
		numChairs=sc.nextInt();

        /*Semaphores for Chairs and TA*/
        Semaphore chairs = new Semaphore(numChairs);
        Semaphore availableTA = new Semaphore(1); 
       
        //For randomly generating program time
        Random r = new Random(); 

		
		/*Create student threads and start them*/     
		Student st[]=new Student[numStud];
        for (int i = 0; i < numStud; i++)
        {
			st[i]=new Student(r.nextInt(20),chairs,availableTA,i+1);
            st[i].start();
		}
		
		/*Create TA thread and start it*/ 
		Teachingassist ta=new Teachingassist(chairs,availableTA,numChairs);
		ta.start();

	}
}
class Student extends Thread
{
	Thread t;				//For current student thread
	
    int r;    				//Time to program before asking for help (in seconds).
    
    int studNum;			//Student number.

	static int ch=0;		//For no. of Chair on which student is sitting
    
    Semaphore chairs;		//Semaphore used to wait in chairs outside office
    
    Semaphore availableTA;	//Mutex used to determine if TA is available.

    public Student(int r,Semaphore chairs,Semaphore availableTA,int studNum)
    {
        this.r=r;    
        this.chairs=chairs;
        this.availableTA=availableTA;
        this.studNum=studNum;
        t=Thread.currentThread();
	}

	public void run()
	{		
		while(true)
		{
			try
			{
				System.out.println("Student " +studNum+ " has started programming for "+r+" seconds.");
            	t.sleep(r*1000);
				System.out.println("Student "+studNum+" is checking whether TA is available or not.");
				/*If TA is available*/
				if(availableTA.tryAcquire())
				{
					System.out.println("TA is available.TA is awoke by Student. Student "+studNum+" has Started working with TA.");
					t.sleep(5000);
					System.out.println("Student "+studNum+" has Stopped working with TA.");
					availableTA.release();		//After completing work release semaphore of TA 
				}
				/*If TA is not available*/
				else
				{
					/*To check whether chairs are available*/
					System.out.println("Student "+studNum+" could not see the TA. Checking for available chairs.");
					if(chairs.tryAcquire())
					{
						ch++;
						System.out.println("Student "+studNum+" is Sitting on chair "+ch);
						System.out.println("Student "+studNum+" is checking whether TA is available or not.");
						availableTA.acquire();	//Again Check whether TA is available
						chairs.release();		//Release semaphore of chair if TA is available
						ch--;
                    	System.out.println("TA is available. Student "+studNum+" has Started working with TA.");
                    	t.sleep(5000);
                        System.out.println("Student "+studNum+" has stopped working with TA.");
                        availableTA.release();	//After completing work release semaphore of TA 
					}
					/*if Chairs are not available*/
					else
						System.out.println("Chairs are not available. Student " +studNum+" Back to programming");
				}
			}
			catch(Exception e){		}
		}		
	}		
}
class Teachingassist extends Thread
{
	Semaphore chairs;		
    Thread t1;
    Semaphore availableTA;	

	int numChairs;

	int cta,cchairs;

    public Teachingassist(Semaphore chairs,Semaphore availableTA,int numChairs)
    { 
        this.chairs=chairs;
        this.availableTA=availableTA;
		this.numChairs=numChairs;
		t1=Thread.currentThread();
	}
	public void run()
	{
		while(true)
		{
		try
		{
			
			cta=availableTA.availablePermits();			//Returns current value of semaphore availableta
			cchairs=chairs.availablePermits();			//Returns current value of semaphore chairs

			/*if no students in cabin and on chairs*/
			if(cta==1&&cchairs==numChairs)
			{
						System.out.println("TA is Sleeping");
						t1.sleep(1000);
			}
			
		}
		catch(Exception e)
		{
		}
		}
		
	}	
}
	
/*students@CE-Lab-602-D04:~/Downloads$ javac Sleepingta67.java
^[[Astudents@CE-Lab-602-D04:~/Downloads$ java Sleepingta67
Enter Total No. Of Students : 
5
Enter Total No. Of Chairs : 
3
Student 1 has started programming for 11 seconds.
Student 2 has started programming for 1 seconds.
Student 3 has started programming for 17 seconds.
Student 4 has started programming for 2 seconds.
Student 5 has started programming for 10 seconds.
TA is Sleeping
Student 2 is checking whether TA is available or not.
TA is available.TA is awoke by Student. Student 2 has Started working with TA.
Student 4 is checking whether TA is available or not.
Student 4 could not see the TA. Checking for available chairs.
Student 4 is Sitting on chair 1
Student 4 is checking whether TA is available or not.
Student 2 has Stopped working with TA.
Student 2 has started programming for 1 seconds.
TA is available. Student 4 has Started working with TA.
Student 2 is checking whether TA is available or not.
Student 2 could not see the TA. Checking for available chairs.
Student 2 is Sitting on chair 1
Student 2 is checking whether TA is available or not.
Student 5 is checking whether TA is available or not.
Student 5 could not see the TA. Checking for available chairs.
Student 5 is Sitting on chair 2
Student 5 is checking whether TA is available or not.
Student 1 is checking whether TA is available or not.
Student 1 could not see the TA. Checking for available chairs.
Student 1 is Sitting on chair 3
Student 1 is checking whether TA is available or not.
Student 4 has stopped working with TA.
Student 4 has started programming for 2 seconds.
TA is available. Student 2 has Started working with TA.
Student 4 is checking whether TA is available or not.
Student 4 could not see the TA. Checking for available chairs.
Student 4 is Sitting on chair 3
Student 4 is checking whether TA is available or not.
Student 2 has stopped working with TA.
Student 2 has started programming for 1 seconds.
TA is available. Student 5 has Started working with TA.
Student 3 is checking whether TA is available or not.
Student 3 could not see the TA. Checking for available chairs.
Student 3 is Sitting on chair 3
Student 3 is checking whether TA is available or not.
Student 2 is checking whether TA is available or not.
Student 2 could not see the TA. Checking for available chairs.
Chairs are not available. Student 2 Back to programming
Student 2 has started programming for 1 seconds.
Student 2 is checking whether TA is available or not.
Student 2 could not see the TA. Checking for available chairs.
Chairs are not available. Student 2 Back to programming
Student 2 has started programming for 1 seconds.
Student 2 is checking whether TA is available or not.
Student 2 could not see the TA. Checking for available chairs.
Chairs are not available. Student 2 Back to programming
Student 2 has started programming for 1 seconds.
Student 2 is checking whether TA is available or not.
Student 2 could not see the TA. Checking for available chairs.
Chairs are not available. Student 2 Back to programming
Student 2 has started programming for 1 seconds.
Student 5 has stopped working with TA.
Student 5 has started programming for 10 seconds.
TA is available. Student 1 has Started working with TA.
Student 2 is checking whether TA is available or not.
Student 2 could not see the TA. Checking for available chairs.
Student 2 is Sitting on chair 3
Student 2 is checking whether TA is available or not.
Student 1 has stopped working with TA.
Student 1 has started programming for 11 seconds.
TA is available. Student 4 has Started working with TA.
^Cstudents@CE-Lab-602-D04:~/Downloads$ 

*/

