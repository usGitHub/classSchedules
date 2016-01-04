import java.util.*;
import java.io.*;

class Student implements Comparable<Student>
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

	public int compareTo(Student other)
	{
		return gradeLevel - other.gradeLevel;
	}

	public int getId()
	{
		return idNumber;
	}

	public int getGradeLevel()
	{
		return gradeLevel;
	}

	public Subject[] getRequests()
	{
		return classRequests;
	}

	public String toString()
	{
		return " ID: " + idNumber;
		// "ID Number: " + idNumber + " -- Grade Level: " + gradeLevel +
		// " -- Course Requests: " + Arrays.toString(classRequests);
	}
}

class Subject
{
	private String name;
	private int id;
	private HashMap<Integer,Integer> periodSize;
	private HashMap<Integer,Integer> emptySpots;

	public Subject(String s, int n, String[] x)
	{
		name = s;
		id = n;
		periodSize = new HashMap<Integer, Integer>();
		emptySpots = new HashMap<Integer, Integer>();
		for(int a = 0; a<x.length; a++)
		{
			String[] nums = x[a].split(";");
			int period = Integer.parseInt(nums[0]);
			int studentNum = Integer.parseInt(nums[1]);
			periodSize.put(period,studentNum);
			emptySpots.put(period,studentNum);
		}
	}

	public int getIdNum()
	{
		return id;
	}

	public int getEmptySpots(int period)
	{
		return emptySpots.get(period);
	}

	public void decrementEmptySpots(int period)
	{
		int emptySpotsForPeriod = emptySpots.get(period+1);
		if(emptySpotsForPeriod > 0)
			emptySpots.put(period+1, emptySpotsForPeriod-1);
	}

	public ArrayList<Integer> getOpenPeriods() {
		ArrayList<Integer> openPeriods = new ArrayList<Integer>();
		for (int period: emptySpots.keySet()) {
			if (emptySpots.get(period) > 0) {
				openPeriods.add(period);
			}
		}

		return openPeriods;
	}

	public String getName()
	{
		return name;
	}

	public void resetEmptySpots() {
		for (int period: periodSize.keySet()) {
			emptySpots.put(period, periodSize.get(period));
		}
	}

	public String toString()
	{
		return name;
	}
}

class Order
{
	private ArrayList<Student> schedulingOrder;
	private ArrayList<Subject[]> bestSchedule;
	private double fitness;

	public Order(ArrayList<Student> schedulingOrder)
	{
		this.schedulingOrder = schedulingOrder;
	}

	public double getFitness()
	{
		return fitness;
	}

	public ArrayList<Student> getSchedulingOrder()
	{
		return schedulingOrder;
	}

	public ArrayList<Subject[]> getBestSchedule()
	{
		return bestSchedule;
	}

	public void setBestSchedule(ArrayList<Subject[]> bestSchedule)
	{
		this.bestSchedule = bestSchedule;
	}

	public void setFitness(double fitness)
	{
		this.fitness = fitness;
	}

	public String toString()
	{
		return "Fitness: " + fitness;
		// + " Order: " + schedulingOrder;
	}
}

public class schoolScheduling
{
	public static ArrayList<Subject[]> findBestSchedule(Order order, ArrayList<Subject> courses)
	{
		for(Subject course: courses) {
			course.resetEmptySpots();
		}
		ArrayList<Subject[]> schedules = new ArrayList<Subject[]>();
		for (Student s: order.getSchedulingOrder()) {
			Subject[] requested = s.getRequests();
			Subject[] schedule = schoolScheduling.findSchedule(
					requested, findOpenClasses(courses), new Subject[requested.length], 0);
			for(int a = 0; a<schedule.length; a++)
			{
				Subject course = schedule[a];
				if (course != null) {
					course.decrementEmptySpots(a);
				}
			}
			schedules.add(schedule);
		}

		return schedules;
	}

	public static double findFitness(ArrayList<Subject[]> schedules) {
		int errorsTotal = 0;
		int scheduleNumWithErrors = 0;
		for(Subject[] schedule: schedules) {
			int scheduleErrors = schedule.length - countNotNull(schedule);
			if (scheduleErrors > 0) {
				errorsTotal += scheduleErrors;
				scheduleNumWithErrors++;
			}
		}

		return 1.0 - ((double)scheduleNumWithErrors/schedules.size());
	}

	public static HashMap<Integer, ArrayList<Student>> separateStudentsByGrade(ArrayList<Student> students)
	{
		HashMap<Integer, ArrayList<Student>> grades = new HashMap<Integer, ArrayList<Student>>();
		for (int grade=9; grade<=12; grade++) {
			grades.put(grade, new ArrayList<Student>());
		}

		for(Student student: students) {
			grades.get(student.getGradeLevel()).add(student);
		}

		return grades;
	}

	public static void main(String[] args) throws Exception
	{
		ArrayList<Subject> courses = new ArrayList<Subject>();
		ArrayList<Student> students = new ArrayList<Student>();
		ArrayList<Order> orders = new ArrayList<Order>();
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
			students.add(b);
		}

		Collections.sort(students);
		HashMap<Integer, ArrayList<Student>> gradeToStudents = separateStudentsByGrade(students);
		for(int x = 0; x<100; x++)
		{
			ArrayList<Student> orderList = new ArrayList<Student>();
			for(int grade = 12; grade >= 9; grade--)
			{
				Collections.shuffle(gradeToStudents.get(grade));
				for(Student student: gradeToStudents.get(grade))
				{
					orderList.add(student);
				}
			}

			Order order = new Order(orderList);
			order.setBestSchedule(findBestSchedule(order, courses));
			order.setFitness(findFitness(order.getBestSchedule()));
			orders.add(order);
		}

		System.out.println("FIRST GENERATION: ");
		System.out.println(orders);

		ArrayList<Order> previousGeneration = orders;

		for(int i = 0; i<200; i++)
		{
			System.out.println("NEW GENERATION: ");
			ArrayList<Order> generation = reproduce(previousGeneration, courses);
			System.out.println(generation);
			previousGeneration = generation;
		}
	}

	public static double getOrdersFitnessSum(ArrayList<Order> ors)
	{
		double sum = 0.0;
		for(Order e: ors)
		{
			sum += e.getFitness();
		}
		return sum;
	}

	public static ArrayList<Order> reproduce(ArrayList<Order> orders, ArrayList<Subject> courses)
	{
		double fitnessSum = getOrdersFitnessSum(orders);
		ArrayList<Order> nextGeneration = new ArrayList<Order>();
		double[] orderBoundaries = new double[orders.size()];
		orderBoundaries[0] = orders.get(0).getFitness() / fitnessSum;
		for(int a = 1; a<orders.size(); a++)
		{
			orderBoundaries[a] = orders.get(a).getFitness() / fitnessSum + orderBoundaries[a-1];
		}

		for(int b = 0; b<orders.size(); b++)
		{
			double randomNumber = Math.random();
			int _position = Arrays.binarySearch(orderBoundaries, randomNumber);
			int reproducePosition = _position >= 0 ? _position : (_position + 1) * -1;

			// mutation and fitness assignment
			Order parent = orders.get(reproducePosition);
			ArrayList<Student> originalOrder = parent.getSchedulingOrder();
			ArrayList<Student> mutatedOrder = mutate(originalOrder);
			boolean mutated = !mutatedOrder.equals(originalOrder);
			Order child = new Order(mutatedOrder);
			if (mutated) {
				child.setBestSchedule(findBestSchedule(child, courses));
				child.setFitness(findFitness(child.getBestSchedule()));
			} else {
				child.setBestSchedule(parent.getBestSchedule());
				child.setFitness(parent.getFitness());
			}

			nextGeneration.add(child);
		}

		return nextGeneration;
	}

	public static ArrayList<Student> mutate(ArrayList<Student> listOrder)
	{
		double percentMutation = 0.01;
		HashMap<Integer, ArrayList<Student>> grades = separateStudentsByGrade(listOrder);

		for(Student student: listOrder)
		{
			double randomNum = Math.random();
			if(randomNum < percentMutation)
			{
				int grade = student.getGradeLevel();
				ArrayList<Student> studentsInGrade = grades.get(grade);
				int studentPos = studentsInGrade.indexOf(student);
				int randomPos = (int)(Math.random() * studentsInGrade.size());
				while (studentPos == randomPos) {
					randomPos = (int)(Math.random() * studentsInGrade.size());
				}
				studentsInGrade.set(studentPos, studentsInGrade.get(randomPos));
				studentsInGrade.set(randomPos, student);
			}
		}
		ArrayList<Student> mutatedList = new ArrayList<Student>();
		for (int grade=9; grade<=12; grade++) {
			mutatedList.addAll(mutatedList.size(), grades.get(grade));
		}
		return mutatedList;
	}

	public static ArrayList<String> findOpenClasses(ArrayList<Subject> courses)
	{
		ArrayList<String> open = new ArrayList<String>();
		for (Subject course: courses) {
			for (int openPeriod: course.getOpenPeriods()) {
				String namePlusPeriod = course.getName() + ";" + openPeriod;
				open.add(namePlusPeriod);
			}
		}
		return open;
	}

	public static ArrayList<Integer> match(Subject course, ArrayList<String> open)
	{
		ArrayList<Integer> periods = new ArrayList<Integer>();
		for (String openClass: open) {
			if (openClass.startsWith(course.getName() + ";")) {
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
		  	ArrayList<Integer> matching = match(req, open);

		  	for(int m: matching)
		  	{
		    	int period = m - 1; //-1 because period 1 is arr[0]
		    	if (schedule[period] == null) // check if period is available in schedule
		    	{
			      schedule[period] = req; // set the period to the class
			      Subject[] filledSchedule = findSchedule(requested, open, schedule, index + 1); // recursion
			      if (bestSchedule == null){
			      	bestSchedule = filledSchedule.clone();
			      } else {
			      	int bestCount = countNotNull(bestSchedule);
			      	int myCount = countNotNull(filledSchedule);
			      	if (myCount > bestCount) {
			      		bestSchedule = filledSchedule.clone();
			      	}
			      }
			      schedule[period] = null; // reset the period to null (after recursion)
			    }
			 }
			 if (bestSchedule == null) { // no matches available for this period, skip the class
			 	bestSchedule = findSchedule(requested, open, schedule, index + 1);
			 }
			 return bestSchedule;
		}

	}
}