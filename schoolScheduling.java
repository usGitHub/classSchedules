import java.util.*;
import java.io.*;

class Student
{
	private Subject[] classRequests;
	private Subject[] assignedClasses; 
	private int idNumber;
	private int gradeLevel;
	
	public Student(Subject[] requests, int num, int g)
	{
		classRequests = requests;
		assignedClasses = new Subject[4];
		idNumber = num;
		gradeLevel = g;
	}
	
	public int getId()
	{
		return idNumber;
	}
	
	public void setPeriod(int n, Subject z)
	{
		assignedClasses[n] = z;
	}
	
	public Subject[] getSchedule()
	{
		return assignedClasses;
	}
	
	public Subject[] getRequests()
	{
		return classRequests;
	}
	
	public int getNumErrors()
	{
		int count = assignedClasses.length; 
		for(int q = 0; q<classRequests.length; q++)
		{
			for(int d = 0; d<assignedClasses.length; d++)
			{
				if(assignedClasses[d] == classRequests[q])
				{
					count--;
				}
			}
		}
		return count;
	}
	
	public String toString()
	{
		return "ID Number: " + idNumber + " -- Grade Level: " + gradeLevel + " -- Course Requests: " + Arrays.toString(classRequests);
	}
}

class Subject
{
	private String name;
	private int id;
	private HashMap<Integer,Integer> periodSize;
	
	public Subject(String s, int n, String[] x)
	{
		name = s;
		id = n;
		periodSize = new HashMap<>();
		for(int a = 0; a<x.length; a++)
		{
			String[] nums = x[a].split(";");
			int period = Integer.parseInt(nums[0]); //Can you declare these 3 variables inside the for loop? Because it works
			int studentNum = Integer.parseInt(nums[1]);
			periodSize.put(period,studentNum);
		}
	}
	
	public HashMap<Integer,Integer> getPeriodMap()
	{
		return periodSize;
	}
	
	public int getIdNum()
	{
		return id;
	}
	
	public int getPeriodSize(int y) 
	{
		return periodSize.get(y);
	}

	public void changeNumSpots(int n) 
	{
		if(periodSize.get(n+1)!=0)
			periodSize.put(n+1,periodSize.get(n+1)-1);
	}
	
	public boolean spotsAvailable(int e)
	{
		return periodSize.get(e+1)!=0;
	}
	
	public String toString()
	{
		return name;
	}
}

public class schoolScheduling
{
	static ArrayList<Subject> courses = new ArrayList<Subject>(); //Should I make this and coursePeriods static so I can access them in the scheduler method?
	static Subject[][] coursePeriods = new Subject[4][courses.size()];	//Should I change how I declared the row or column number?
	public static void main(String[] args) throws Exception
	{
		Scanner dataInput = new Scanner(new File("courseData.txt"));
		while(dataInput.hasNextLine())
		{
			String s = dataInput.nextLine();
			String[] courseInfo = s.split(",");
			String courseName = courseInfo[0];
			int courseId = Integer.parseInt(courseInfo[1]);
			String[] courseSize = courseInfo[2].split("-");
			Subject newCourse = new Subject(courseName, courseId, courseSize);
			courses.add(newCourse);
		}
			
		for(int k = 1; k<=coursePeriods.length; k++) 
		{
			ArrayList<Subject> availablePeriods = new ArrayList<Subject>();
				for(int h = 0; h<courses.size(); h++)
				{
						if(courses.get(h).getPeriodSize(k)!= 0)
							availablePeriods.add(courses.get(h));
				}
				Subject[] tempPeriods = new Subject[availablePeriods.size()];
				for(int t = 0; t<tempPeriods.length; t++)
				{
					tempPeriods[t] = availablePeriods.get(t);
				}
			coursePeriods[k-1] = tempPeriods;
		}

		Scanner input = new Scanner(new File("studentInfoData.txt"));
		while(input.hasNextLine())
		{
			String str = input.nextLine();
			String[] nums = str.split(" ");
			int[] studentRequests = new int[nums.length];
			for(int y = 0; y<nums.length; y++)
				studentRequests[y] = Integer.parseInt(nums[y]);
			int idNum = studentRequests[0];
			int gradeNum = studentRequests[1];
			Subject[] courseRequests = new Subject[studentRequests.length-2];
			for(int z = 2; z<studentRequests.length; z++)
			{
				for(int a = 0; a<courses.size(); a++)
				{					
					if(studentRequests[z] == courses.get(a).getIdNum())
					{
						courseRequests[z-2] = courses.get(a);
					}
				}
			}
			Student b = new Student(courseRequests, idNum, gradeNum);
			scheduler(b);
			System.out.println("ID: " + b.getId() + "\nCourse Requests: " + Arrays.toString(b.getRequests()) + "\nSchedule: " + Arrays.toString(b.getSchedule()) + "\nErrors: " + b.getNumErrors());
			System.out.println();
		}
		
		System.out.println();
		System.out.println("SPOTS LEFT AFTER MAKING ALL THE SCHEDULES: ");
		for(int y = 0; y<courses.size(); y++)
		{
			for(int x = 1; x<=4; x++)
			{
				System.out.println("Spots left in period " + x + " of " + courses.get(y) + ": " + courses.get(y).getPeriodSize(x));
			}
			System.out.println();
		}
	}
	
	public static void scheduler(Student t) //Must be static, right?
	{
		for(int v = 0; v<courses.size(); v++)
		{
			for(int x = 0; x<4; x++)//
			{
				if(t.getSchedule()[x]==null && checkCoursePeriods(courses.get(v),x) && courses.get(v).spotsAvailable(x)) //Do I need to take into account if the class is already in the student's schedule? Because results show that no class is repeated in the same schedule
				{	
					t.setPeriod(x,courses.get(v));
					courses.get(v).changeNumSpots(x);
					break;
				}
			}
		}
	}
	
	public static boolean checkCoursePeriods(Subject a, int g)
	{
		for(int w = 0; w<coursePeriods[g].length; w++)
			if(coursePeriods[g][w] == a)
				return true;
		return false;
	}
}