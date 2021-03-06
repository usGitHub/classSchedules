import java.util.*;
import java.io.*;

class Order
{
	private ArrayList<Student> schedulingOrder;
	private double fitness;
	private double chanceOfSurvival;
	
	public Order(ArrayList<Student> schedOrder)
	{
		schoolScheduling.resetCourses();//?? Where do I put resetCourses?
		for(int x = 0; x<schedOrder.size(); x++)
		{
			schedulingOrder.add(schedOrder.get(x));
		}
		assignFitness();
		chanceOfSurvival = 0;
	}
	
	public Order(Order toBeCopied)
	{
		ArrayList<Student> schedOrderToBeCopied = toBeCopied.getSchedulingOrder(); //??
		for(int x = 0; x<schedOrderToBeCopied.size(); x++)
		{
			schedulingOrder.add(schedOrderToBeCopied.get(x));
		}
		fitness = toBeCopied.getFitness();
		chanceOfSurvival = toBeCopied.getChanceOfSurvival();
	}
	
	public double getFitness()
	{
		return fitness;
	}
	
	public double getChanceOfSurvival()
	{
		return chanceOfSurvival;
	}
	
	public ArrayList<Student> getSchedulingOrder()
	{
		return schedulingOrder;
	}
	
	public void setChanceOfSurvival(double fitnessSum)
	{
		chanceOfSurvival = fitness/fitnessSum;
	}
	
	public void assignFitness()
	{
		schoolScheduling.resetCourses(); //??
		double schedulesWithErrors = 0;
		for(Student s: schedulingOrder)
		{
			Subject[] bestSchedule = schoolScheduling.findSchedule(s.getRequests(), schoolScheduling.findOpenClasses(), new Subject[4], 0);
			s.setSchedule(bestSchedule);
			for(int a = 0; a<bestSchedule.length; a++)
			{
				Subject course = bestSchedule[a]; //??
				course.decrementEmptySpots(a);
			}
			if(s.getNumErrors() > 0)
			{
				schedulesWithErrors++;
			}
		}
		double dError = schedulesWithErrors/schedulingOrder.size();
		double pError = dError * 100.0;
		fitness = 100.0 - pError;
	}
	
	public String toString()
	{
		return "Fitness: " + fitness;
	}
}

public class schoolScheduling()
{
	static double sumChances = 0.0;
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

		HashMap<Integer, ArrayList<Student>> grade = new HashMap<Integer, ArrayList<Student>>();
		for(int g = 9; g<= 12; g++)
		{
			grade.put(g, new ArrayList<Student>>);
		}
		
		for(Student s; students)
		{
			grade.get(s.getGradeLevel()).add(s);
		}

		for(int x = 0; x<100; x++)
		{
			ArrayList<Student> orderList = new ArrayList<Student>();
			for(int num = 12; num>= 9; num++)
			{
				Collections.shuffle(grade.get(num));
				for(Student st: grade.get(num))
				{
					orderList.add(st);
				}
			}
			ArrayList<Student> mutated = mutate(orderList); //??
			for(Student s: mutated)
			{
				orderList.add(s);
			}
			Order order = new Order(orderList);
			if(!orders.contains(order))
			{
				orders.add(order);
			}
		}
		
		double sumFitness = getOrdersFitnessSum(orders);
		for(int a = 0; a<orders.size(); a++)
		{
			Order order1 = orders.get(a);
			order1.setChanceOfSurvival(sumFitness);
			sumChances += order1.getChanceOfSurvival();
		}
		System.out.println("FIRST GENERATION: ");
		System.out.println(orders);

		ArrayList<Order> previousGeneration = new ArrayList<Order>();
		for(Order ord: orders)
		{
			previousGeneration.add(ord);
		}
		for(int i = 0; i<200; i++)
		{
			System.out.println("NEW GENERATION: ");
			ArrayList<Order> reproduced = reproduce(previousGeneration); //??
			ArrayList<Order> generation = new ArrayList<Order>();
			for(Order o: reproduced)
			{
				generation.add(o);
			}
			for(int a = 0; a<generation.size(); a++)
			{
				Order order2 = generation.get(a);
				order2.setSchedulingOrder(mutate(order2.getSchedulingOrder())); //mutate twice??
				order2.assignFitness();
			}
			double generationFitnessSum = getOrdersFitnessSum(generation);
			for(int b = 0; b<generation.size(); b++)
			{
				Order e = generation.get(b);
				e.setChanceOfSurvival(generationFitnessSum);
			}
			System.out.println(generation);
			System.out.println("Sum: " + getCosSum(generation));
			System.out.println("Avg Fitness: " + averageFitness(generation));
			previousGeneration = new ArrayList<Order>();
			for(Order order: generation)
			{
				previousGeneration.add(order);
			}
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
	
	public static double averageFitness(ArrayList<Order> or)
	{
		return getOrdersFitnessSum(or)/or.size();
	}
	
	public static double getCosSum(ArrayList<Order> or)
	{
		double sum = 0.0;
		for(Order o: or)
		{
			sum += o.getChanceOfSurvival();
		}
		return sum;
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
	
	public static void resetCourses() //PARAMETER
	{
		for(Subject course: courses) {
			course.resetEmptySpots();
		}
	}
	
	public static ArrayList<Order> reproduce(ArrayList<Order> orderList1) throws Exception
	{
		ArrayList<Order> nextGeneration = new ArrayList<Order>();
		double[] orderBoundaries = new double[orderList1.size()];
		orderBoundaries[0] = orderList1.get(0).getChanceOfSurvival();
		for(int a = 1; a<orderList1.size(); a++)
		{
			orderBoundaries[a] = orderList1.get(a).getChanceOfSurvival() + orderBoundaries[a-1];
		}
		for(int b = 0; b<orderList1.size(); b++)
		{
			double randomNumber = Math.random();
			int position = Arrays.binarySearch(orderBoundaries, randomNumber);
			int finalPosition = position >= 0 ? position : (position + 1) * -1; 
			nextGeneration.add(new Order(orderList1.get(finalPosition))); //Copy correctly?
		}
		return nextGeneration;
	}

	public static ArrayList<Student> mutate(ArrayList<Student> listOrder)
	{
		double percentMutation = 0.01;
		HashMap<Integer, ArrayList<Student>> grades = new HashMap<Integer, ArrayList<Student>>();
		for (int grade=9; grade<=12; grade++) {
			grades.put(grade, new ArrayList<Student>());
		}

		for(Student student: listOrder) {
			grades.get(student.getGradeLevel()).add(student);
		}

		studentsMutated = 0;
		for(Student student: listOrder)
		{
			double randomNum = Math.random();
			if(randomNum < percentMutation)
			{
				studentsMutated++;
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
		percentMutationOverall = studentsMutated/(listOrder.size()*1.0);
		avgPercentMutation += percentMutationOverall;
		ArrayList<Student> mutatedList = new ArrayList<Student>();
		for (int grade=9; grade<=12; grade++) {
			mutatedList.addAll(mutatedList.size(), grades.get(grade));
		}
		return mutatedList;
	}
	
	public static ArrayList<String> findOpenClasses() //How do you do this without course periods?
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
	