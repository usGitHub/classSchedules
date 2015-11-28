import java.util.*;
import java.io.*;

class Student implements Comparator<Student>
{
	private Subject[] classRequests;
	private Subject[] assignedClasses;
	private int idNumber;
	private int gradeLevel;

	public Student()
	{}

	public Student(Subject[] requests, int num, int g)
	{
		classRequests = requests;
		assignedClasses = new Subject[4];
		idNumber = num;
		gradeLevel = g;
	}

	public int compare(Student s, Student s1)
	{
		return s1.gradeLevel - s.gradeLevel;
	}

	public void setSchedule(Subject[] classes)
	{
		assignedClasses = classes;
	}

	public int getId()
	{
		return idNumber;
	}

	public int getGradeLevel()
	{
		return gradeLevel;
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
		return Arrays.toString(assignedClasses) + " ID: " + idNumber;//"ID Number: " + idNumber + " -- Grade Level: " + gradeLevel + " -- Course Requests: " + Arrays.toString(classRequests);
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
		if(periodSize.get(n+1)>0)
			periodSize.put(n+1,periodSize.get(n+1)-1);
	}

	public boolean spotsAvailable(int e)
	{
		return periodSize.get(e+1)!=0;
	}

	public String getName()
	{
		return name;
	}
	
	public void setPeriodSize(int period, int size)
	{
		periodSize.put(period,size);
	}

	public String toString()
	{
		return name;
	}
}

class Order
{
	private ArrayList<Student> schedulingOrder;
	private double fitness;

	public Order(ArrayList<Student> o) throws Exception
	{
		schoolScheduling.resetCourses();
		schedulingOrder = o;
		fitness = assignFitness();
	}

	public double getFitness()
	{
		return fitness;
	}

	public double assignFitness() 
	{
		//schoolScheduling.resetCourses();
		int errorsTotal = 0;
		int scheduleNumWithErrors = 0;
		for(int a = 0; a<schedulingOrder.size(); a++)
		{
			Student s = schedulingOrder.get(a);
			//System.out.println(a);
			Subject[] bestSchedule = schoolScheduling.findSchedule(
					s.getRequests(), schoolScheduling.findOpenClasses(), new Subject[4], 0);
			// System.out.println(a + ' ' + Arrays.toString(bestSchedule));
			s.setSchedule(bestSchedule);
			schoolScheduling.changeNumSpotsPerClass(s.getSchedule());
			schoolScheduling.changeCoursePeriods();
			errorsTotal += s.getNumErrors();
			if(s.getNumErrors()>0)
				scheduleNumWithErrors++;
		}
		double decimError = (double)scheduleNumWithErrors/schedulingOrder.size();
		double pError = decimError * 100;
		return pError;
	}

	public String toString()
	{
		return "Fitness: " + fitness + ""; // + " Order: " + schedulingOrder;
	}
}

public class schoolScheduling
{
	static ArrayList<Subject> courses = new ArrayList<Subject>(); //Should I make this and coursePeriods static so I can access them in the scheduler method?
	static Subject[][] coursePeriods = new Subject[4][courses.size()];	//Should I change how I declared the row or column number?
	static ArrayList<Student> students = new ArrayList<Student>();
	static int totalErrors = 0;
	static int numStudents = 0;
	static int schedulesWithErrors = 0;
	static ArrayList<Student> grade12 = new ArrayList<Student>();
	static ArrayList<Student> grade11 = new ArrayList<Student>();
	static ArrayList<Student> grade10 = new ArrayList<Student>();
	static ArrayList<Student> grade9 = new ArrayList<Student>();
	static ArrayList<Order> orders = new ArrayList<Order>();
	static int ordersMutated = 0;
	static int numOrders = 0;
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
						if(courses.get(h).getPeriodSize(k)>0)
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
			students.add(b);
			numStudents++;
		}

		Collections.sort(students, new Student());

		for(int b = 0; b<students.size(); b++)
		{
			if(students.get(b).getGradeLevel() == 12)
				grade12.add(students.get(b));
			if(students.get(b).getGradeLevel() == 11)
				grade11.add(students.get(b));
			if(students.get(b).getGradeLevel() == 10)
				grade10.add(students.get(b));
			if(students.get(b).getGradeLevel() == 9)
				grade9.add(students.get(b));
		}

		for(int x = 0; x<200; x++)
		{
			ArrayList<Student> orderList = new ArrayList<Student>();
			Collections.shuffle(grade12);
			Collections.shuffle(grade11);
			Collections.shuffle(grade10);
			Collections.shuffle(grade9);
			for(int i = 0; i<grade12.size(); i++)
			{
				orderList.add(grade12.get(i));
			}
			for(int k = 0; k<grade11.size(); k++)
			{
				orderList.add(grade11.get(k));
			}
			for(int g = 0; g<grade10.size(); g++)
			{
				orderList.add(grade10.get(g));
			}
			for(int h = 0; h<grade9.size(); h++)
			{
				orderList.add(grade9.get(h));
			}
			orderList = (ArrayList<Student>) (mutate(orderList).clone());
			Order order = new Order(orderList);
			if(!orders.contains(order))
			{
				orders.add(order);
				numOrders++;
				System.out.println(order);
			}
		}
		System.out.println("Percent Mutation: " + percentMutation());
		//System.out.println("Fittest Order: " + getFittestOrder());

		/*for(int a = 0; a<students.size(); a++)
		{
			Student s = students.get(a);
			scheduler(s);
			totalErrors += s.getNumErrors();
			if(s.getNumErrors()>0)
				schedulesWithErrors++;
			System.out.println("ID: " + s.getId() + "\nGrade Level: " + s.getGradeLevel() + "\nCourse Requests: " + Arrays.toString(s.getRequests()) + "\nSchedule: " + Arrays.toString(s.getSchedule()) + "\nErrors: " + s.getNumErrors());
			System.out.println();
		}*/

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
		//System.out.println("The Average Number of Errors per Schedule: " + countAvgNumErrors());
		//System.out.println("The Percent Error (The percent of schedules with 1 or more errors): " + overallPercentError() + " %");
	}

	//RANDOM SCHEDULER
	/*public static void scheduler(Student t) //Must be static, right?
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
	}*/

	/*public static void scheduler(Student t)
	{
		ArrayList<Subject> requests = new ArrayList<Subject>();
		for(int a = 0; a<t.getRequests().length; a++)
		{
			requests.add(t.getRequests()[a]);
		}
		Collections.shuffle(requests);
		Subject[] requests = t.getRequests();
		for(int x = 0; x<requests.length; x++)
		{
			int pos = courses.indexOf(requests[x]);
			for(int a = 0; a<4; a++)
			{
				if(t.getSchedule()[a] == null && scheduleNotContainsClass(courses.get(pos), t) && checkCoursePeriods(courses.get(pos), a) && courses.get(pos).spotsAvailable(a))
				{
					t.setPeriod(a, courses.get(pos));
					courses.get(pos).changeNumSpots(a);
					break;
				}
			}
		}
	}*/


	public static boolean checkCoursePeriods(Subject a, int g)
	{
		for(int w = 0; w<coursePeriods[g].length; w++)
			if(coursePeriods[g][w] == a)
				return true;
		return false;
	}

	public static boolean scheduleNotContainsClass(Subject s, Student x)
	{
		Subject[] schedule = x.getSchedule();
		for(int a = 0; a<schedule.length; a++)
		{
			if(schedule[a] == s)
				return false;
		}
		return true;
	}

	public static double countAvgNumErrors()
	{
		double avg = totalErrors/numStudents;
		return avg;
	}

	public static double overallPercentError()
	{
		double decimalError = (double)schedulesWithErrors/numStudents;
		double percentError = decimalError * 100;
		return percentError;
	}

	public static Order getFittestOrder()
	{
		double fittest = orders.get(0).getFitness();
		Order fittestOrder = orders.get(0);
		for(int a = 0; a<orders.size(); a++)
		{
			if(orders.get(a).getFitness() < fittest)
			{
				fittest = orders.get(a).getFitness();
				fittestOrder = orders.get(0);
			}
		}
		return fittestOrder;
	}
	
	public static void resetCourses() throws Exception
	{
		Scanner input = new Scanner(new File("courseData.txt"));
		for(int a = 0; a<courses.size(); a++)
		{
			Subject course = courses.get(a);
			String str = input.nextLine();
			String[] arr = str.split(",");
			String[] arr2 = arr[2].split("-");
			for(int b = 0; b<arr2.length; b++)
			{
				String[] periodSize = arr2[b].split(";");
				course.setPeriodSize(Integer.parseInt(periodSize[0]), Integer.parseInt(periodSize[1]));
			}
		}
		changeCoursePeriods();
		System.out.println("Reset");
	}

	public static void changeCoursePeriods()
	{
		for(int k = 1; k<=coursePeriods.length; k++)
		{
			ArrayList<Subject> availablePeriods = new ArrayList<Subject>();
				for(int h = 0; h<courses.size(); h++)
				{
						if(courses.get(h).getPeriodSize(k)>0)
							availablePeriods.add(courses.get(h));
				}
				Subject[] tempPeriods = new Subject[availablePeriods.size()];
				for(int t = 0; t<tempPeriods.length; t++)
				{
					tempPeriods[t] = availablePeriods.get(t);
				}
			coursePeriods[k-1] = tempPeriods;
		}
		//System.out.println("ChangedCoursePeriods");
	}

	public static void changeNumSpotsPerClass(Subject[] schedule1)
	{
		for(int a = 0; a<schedule1.length; a++)
		{
			Subject course = schedule1[a];
			if (course != null) {
				course.changeNumSpots(a);
			}
		}
		//System.out.println("Changed");
	}
	
	public static ArrayList<Student> mutate(ArrayList<Student> listOrder)
	{
		List<Integer> nums = Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30);
		Collections.shuffle(nums);
		int randomNum = nums.get(0);
		System.out.println(randomNum);
		if(randomNum > 29)
		{
			ArrayList<Student> g12 = new ArrayList<Student>();
			ArrayList<Student> g11 = new ArrayList<Student>();
			ArrayList<Student> g10 = new ArrayList<Student>();
			ArrayList<Student> g9 = new ArrayList<Student>();
			for(int a = 0; a<listOrder.size(); a++)
			{
				Student s = listOrder.get(a);
				if(s.getGradeLevel() == 12)
					g12.add(s);
				if(s.getGradeLevel() == 11)
					g11.add(s);
				if(s.getGradeLevel() == 10)
					g10.add(s);
				if(s.getGradeLevel() == 9)
					g9.add(s);
			}
			int randomGrade = (int)(Math.random()*4)+9;
			if(randomGrade == 9)
			{
				int randomPos1 = (int)(Math.random()*g9.size())+0;
				int randomPos2 = (int)(Math.random()*g9.size())+0;
				while(randomPos1 == randomPos2)
				{
					randomPos2 = (int)(Math.random()*g9.size())+0;
				}
				Student temp = g9.get(randomPos1);
				g9.set(randomPos1, g9.get(randomPos2));
				g9.set(randomPos2, temp);
			}
			if(randomGrade == 10)
			{
				int randomPos1 = (int)(Math.random()*g10.size())+0;
				int randomPos2 = (int)(Math.random()*g10.size())+0;
				while(randomPos1 == randomPos2)
				{
					randomPos2 = (int)(Math.random()*g10.size())+0;
				}
				Student temp = g10.get(randomPos1);
				g10.set(randomPos1, g10.get(randomPos2));
				g10.set(randomPos2, temp);
			}
			if(randomGrade == 11)
			{
				int randomPos1 = (int)(Math.random()*g11.size())+0;
				int randomPos2 = (int)(Math.random()*g11.size())+0;
				while(randomPos1 == randomPos2)
				{
					randomPos2 = (int)(Math.random()*g11.size())+0;
				}
				Student temp = g11.get(randomPos1);
				g11.set(randomPos1, g11.get(randomPos2));
				g11.set(randomPos2, temp);
			}
			if(randomGrade == 12)
			{
				int randomPos1 = (int)(Math.random()*g12.size())+0;
				int randomPos2 = (int)(Math.random()*g12.size())+0;
				while(randomPos1 == randomPos2)
				{
					randomPos2 = (int)(Math.random()*g12.size())+0;
				}
				Student temp = g12.get(randomPos1);
				g12.set(randomPos1, g12.get(randomPos2));
				g12.set(randomPos2, temp);
			}
			ArrayList<Student> mutatedList = new ArrayList<Student>();
			for(int i = 0; i<g12.size(); i++)
			{
				mutatedList.add(g12.get(i));
			}
			for(int k = 0; k<g11.size(); k++)
			{
				mutatedList.add(g11.get(k));
			}
			for(int g = 0; g<g10.size(); g++)
			{
				mutatedList.add(g10.get(g));
			}
			for(int h = 0; h<g9.size(); h++)
			{
				mutatedList.add(g9.get(h));
			}
			ordersMutated++;
			System.out.println("Mutated");
			return mutatedList;
		}
		else
		{
			return listOrder;
		}
	}
	
	public static double percentMutation()
	{
		double mutationPercent = (ordersMutated/(numOrders*1.0)) * 100.0;
		return mutationPercent;
	}

	public static ArrayList<String> findOpenClasses()
	{
		ArrayList<String> open = new ArrayList<String>();
		for(int r = 0; r<coursePeriods.length; r++)
		{
			for(int c = 0; c<coursePeriods[r].length; c++)
			{
				String namePlusPeriod = coursePeriods[r][c].getName() + ";" + (r+1);
				open.add(namePlusPeriod);
			}
		}
		return open;
	}

	public static ArrayList<Integer> match(Subject course, ArrayList<String> open)
	{
		ArrayList<Integer> periods = new ArrayList<Integer>();
		CharSequence courseNm = (CharSequence) course.getName();
		for (String openClass: open) {
			if (openClass.contains(courseNm + ";")) {
				periods.add(Integer.parseInt(openClass.split(";")[1]));
			}
		}

		return periods;
	}

	public static int countNotNull(Object[] objects) {
		int count = 0;
		for (Object o: objects) {
			if (o != null) {
				count++;
			}
		}

		return count;
	}

	public static Subject[] findSchedule(Subject[] requested, ArrayList<String> open, Subject[] schedule, int index){
	 	if(index == requested.length) {
	 		//System.out.println(Arrays.toString(schedule));
	 		return schedule;
	 	} else {
			Subject[] bestSchedule = null;
			Subject req = requested[index];
		  // find all classes that match the requested class
		  // in open (I'm skipping that in the pseudocode)
		  	ArrayList<Integer> matching = match(req, open);

		  	for(int m: matching)
		  	{
		    	int period = m - 1; //-1 because period 1 is arr[0]
		    	if (schedule[period] == null) // check if period is available in schedule
		    	{
			      schedule[period] = req; // set the period to the class
			      Subject[] filledSchedule = findSchedule(requested, open, schedule, index + 1); // recursion
			      if (bestSchedule == null){
			      	bestSchedule = (Subject[]) (filledSchedule.clone());
			      } else {
			      	int bestCount = countNotNull(bestSchedule);
			      	int myCount = countNotNull(filledSchedule);
			      	if (myCount > bestCount) {
			      		bestSchedule = (Subject[]) (filledSchedule.clone());
			      	}
			      }
			      schedule[period] = null; // reset the period to null (after recursion)
			    }
			 }
			 if (bestSchedule == null) { // no matches available for this period, skip the class
			 	bestSchedule = (Subject[]) findSchedule(requested, open, schedule, index + 1);
			 }
			 return bestSchedule;
		}

	}
}