import java.util.*;
import java.io.*;

class Student
{
	private Subject[] classRequests;
	private int idNumber;
	private int gradeLevel;
	
	public Student(Subject[] requests, int num, int g)
	{
		classRequests = requests;
		idNumber = num;
		gradeLevel = g;
	}
	
	public int getId()
	{
		return idNumber;
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
			int period = Integer.parseInt(nums[0]); //You can declare these 3 variables inside the for loop? Because it works
			int studentNum = Integer.parseInt(nums[1]);
			periodSize.put(period,studentNum);
		}
	}
	
	public int getIdNum()
	{
		return id;
	}
	
	public int getPeriodSize(int y)
	{
		return periodSize.get(y);
	}
	
	public String toString()
	{
		return name;
	}
}

public class schoolScheduling
{
	public static void main(String[] args) throws Exception
	{
		ArrayList<Subject> courses = new ArrayList<Subject>();
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
		}
	}
}