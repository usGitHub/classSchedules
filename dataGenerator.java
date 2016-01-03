import java.io.*;
import java.util.*;

public class dataGenerator
{
	public static void main(String[] args)
	{
		int total = 5000; //changeable
		ArrayList<String> studentInfo = new ArrayList<String>();
		int nine = total/4;
		int ten = nine + total/4;
		int eleven = ten + total/4;
		int twelve = eleven + total/4;
		for(int n = 1; n<=total; n++)
		{
			String info = "";
			if(n <= nine)
			{
				info += n + " " + 9 + " " + randomCourseRequests();
			}
			else if(n <= ten)
			{
				info += n + " " + 10 + " " + randomCourseRequests();
			}
			else if(n <= eleven)
			{
				info += n + " " + 11 + " " + randomCourseRequests();
			}
			else if(n <= twelve)
			{
				info += n + " " + 12 + " " + randomCourseRequests();
			}
			studentInfo.add(info);
		}
		
		for(String s: studentInfo)
		{
			System.out.println(s);
		}
	}
	
	public static String randomCourseRequests()
	{
		String courses = "";
		for(int a = 0; a<4; a++)
		{
			int random = (int)(Math.random() * 10) + 1; //Change 10 accordingly
			while(courses.contains(random + " "))
			{
				random = (int)(Math.random() * 10) + 1; //Change 10 accordingly
			}
			courses += random + " ";
		}
		return courses.trim();
	}
}