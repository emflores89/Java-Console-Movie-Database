package jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.sql.*;

public class DBUserInterface {
	
	private Scanner input;
	private DBcommands db;
	private SQLExceptionHandler handler;
	private Connection connection;
	
	public DBUserInterface() 
	{
		this.input = new Scanner(System.in);
		this.db = new DBcommands();	
		this.handler = new SQLExceptionHandler();
	}
	
	public boolean welcome() 
	{			
		boolean status = false;
		
		//Keep running until user either exits or has attempted login
		boolean running = false;
		while(!running) {
			
			System.out.println("");
			System.out.println("Welcome To Movie Database");
			System.out.println("-------------------------------");
			System.out.println("1. Continue To Login");
			System.out.println("2. Exit Program");
			System.out.println("");
		
			int userInput;	
			try {
				System.out.print("Choice: ");
				userInput = Integer.parseInt(input.nextLine());
				
				if(userInput == 1)
				{
					status = userlogin();
					
					//if login was successful stop
					//Otherwise print error and 
					//cont looping until exit
					if(status){
					running = true;
					}
					
				}
				
				else if(userInput == 2)
				{
					System.out.println("Exiting Program..");
					
					running = true;			
					close();
					
				}
				
				else
				{
					System.out.println("Please Choose either 1 or 2");
				}
				
			}
								
			catch(Exception e)
			{
				System.out.println("Error: Please enter numbers ONLY");							
			}							
		}
		
		//Return True for sucess login
		//Or Keep false for exit
		return status;
	}
	
	public boolean userlogin()
	{
			
		System.out.println("");
		System.out.println("Movie Database Login");
		System.out.println("-------------------------------");
		System.out.print("Username: ");
		String userName = input.nextLine();
		System.out.print("Password: ");
		String password = input.nextLine();
		
		boolean status = false;
		try 
        {
        	// Incorporate mySQL driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
        	// Connect to the test database
        	connection = DriverManager.getConnection("jdbc:mysql:///moviedb?useSSL=false",userName, password);
        	
        	
        	//Call ONLY if connection went without problems otherwise back to welcome screen
        	db.setConnection(connection);
        	//Success change to true otherwise it stays false;
        	status = true;
        
        }
			
		catch (SQLException e) 
        {
			System.out.println("Error: Wrong username and/or password.");
		
		}
		
		catch(InstantiationException e)
		{
			System.out.println("Error: mySQL driver could not be instantiated.");
			
		}
		
		catch(IllegalAccessException e)
		{
			System.out.println("Error: Something has occured during database access.");
			
		}
		
		catch(ClassNotFoundException e)
		{
			System.out.println("Error: Unable to load driver class.");
		}
		
		return status;
	
	}
	
	public void userlogout()
	{
		//Close the connection but keep input open
		try 
		{
			if(connection != null)
			{
				connection.close();
			}
			
			userInterface();
			
		} 
		catch (SQLException e) 
		{
			System.out.println("An error has occured while loging out");
		}
	
	}
	
	public void close()
	{
		//Close the connection and the input
		try
		{
					
			if(connection != null)
			{
				connection.close();
			}
			
			input.close();
		
		}

		catch(Exception e)
		{
			System.out.println("Error: Somthing has happend while closing connection");
		}		
	}
	
	public void userInterface()
	{
		boolean status = welcome();
	
		if(!status)
		{
			//Exit Program 
			//No Need to go beyond this point
			//Otherwise continue with program
			return;
		}
			
		input = new Scanner(System.in);
		boolean running = false;	
		while(!running)
		{
			System.out.println("");
			System.out.println("Movie Database Menu");
			System.out.println("-----------------------------");
			System.out.println("1. Add Star");
			System.out.println("2. Add Customer");
			System.out.println("3. Delete Customer");
			System.out.println("4. Lookup Movies Featuring Star");
			System.out.println("5. Database Metadata");
			System.out.println("6. SQL Query");
			System.out.println("7. Logout");
			System.out.println("8. Exit");
			System.out.println(" ");
			
			System.out.print("Choose Option: ");
			int userInput;
			
			try {
				userInput = Integer.parseInt(input.nextLine());
				
				switch(userInput)
				{
				case 1:
					addStar();
					break;
				case 2:
					addCustomer();
					break;
				case 3:
					deleteCustomer();
					break;
				case 4:
					featureStar();
					break;
				case 5:
					metaData();
					break;
				case 6:
					query();
					break;
				case 7:
					System.out.println("Logging Out..");
					System.out.println("");
					userlogout();
					running = true;
					break;
				case 8:
					System.out.println("Exiting Program");
					close();
					running = true;
					break;
					
				default:
					System.out.println("Error: Choose options 1-8");		
				}
			}
			
			catch(Exception e) {
				System.out.println("Error: Please enter a number");
			}
		}		
		
	}

	private void deleteCustomer() {
		System.out.println("Customer Deletion");
		System.out.println("----------------------------------------------------");
		System.out.print("Credit Card Number: ");
		String ccID = input.nextLine();
		
		if(ccID.trim().length() == 0)
		{
			System.out.println("Error: No Credit Card Number was entered");
			System.out.println("Returning to Main Menu");
			return;
		}
		
		try
		{
			db.deleteCustomer(ccID);
		}
		
		catch(SQLException e)
		{
			handler.handleException(e);
			System.out.println("");
		}
		
	}

	private void query() {
		// TODO Auto-generated method stub
		System.out.println("ONLY SELECT/UPDATE/INSERT/DELETE Commands Allowed");
		System.out.println("----------------------------------------------------");
		System.out.print("Enter SQL Command: ");
		String query = input.nextLine();
		
		
		
		ArrayList<String> allowed = new ArrayList<String>(Arrays.asList("SELECT","UPDATE","INSERT","DELETE")); 
		
		query = query.toUpperCase();
		String[] parsedQuery = query.split("\\s+");
		
		
		if(!allowed.contains(parsedQuery[0]))
		{
			System.out.println("Error: Command does not contain allowed commands (SELECT/UPDATE/INSERT/DELETE)");
			System.out.println("Returning to Main Menu");
			return;
		}
		
	    
		try
		{
			db.executeQuery(query);
		}
		catch(SQLException e)
		{
			System.out.println("");
			handler.handleException(e);
			System.out.println("Returning to Main Menu");
		}
		
	}

	private void metaData() {
		// TODO Auto-generated method stub
		System.out.println("");
		System.out.println("Database Metadata");
		System.out.println("-------------------------------------------");
		System.out.println("");
		
		try
		{
			db.getMetaData();
		}
		
		catch(SQLException e)
		{
			System.out.println("");
			handler.handleException(e);
			System.out.println("Returning to Menu");
		}
		
	}

	private void featureStar() 
	{
		String star_id, firstName, lastName;
		System.out.println("");
		System.out.println("To Find Movies Featuring a Star");
		System.out.println("Not All Information Is Required");
		System.out.println("-----------------------------------------");
		System.out.println("1. Provide Just a Star ID");
		System.out.println("2. Provide either First or Last Name");
		System.out.println("3. Provide both First and Last Name");
		System.out.println("4. Provide all ID, First and Last Name");
		System.out.println("");
		
		System.out.print("Star ID (Blank if None): ");
		star_id = input.nextLine().trim();
		
		System.out.print("First Name (Blank if None): ");
		firstName = input.nextLine().trim();
			
		System.out.print("Last Name (Blank if None): ");
		lastName = input.nextLine().trim();
		
		System.out.println("");
		int total_length = star_id.length() + firstName.length() + lastName.length();
		
		if(total_length == 0)
		{
			System.out.println("");
			System.out.println("Error: No information provided. Returning to Menu.");
			return;
		}
		
		HashMap<String, String> star = new HashMap<String,String>();
		
		if(!star_id.isEmpty()) 
		{
			star.put("stars.id", "'"+star_id+"'");
		}
		
		if(!firstName.isEmpty()) 
		{
			star.put("stars.first_name", "'"+firstName+"'");
		}
		
		if(!lastName.isEmpty()) 
		{
			star.put("stars.last_name", "'"+lastName+"'");
		}
		
		try
		{		
			db.printStar(star);
		}
		
		catch(SQLException e)
		{
			handler.handleException(e);
			System.out.println("Returning to Menu");
		}
		
	}

	private void addCustomer() 
	{
		String name, cc_id, address, email, password;
		
		System.out.println("");
		System.out.println("Insert Customer Into Database");
		System.out.println("--------------------------------------------");
		System.out.println("1. All Attributes are required");
		System.out.println("2. Name can be either first and last or last");
		System.out.println("");
		
		System.out.print("Name (First Last): ");
		name = input.nextLine();
		
		System.out.print("Credit Card ID: ");
		cc_id = input.nextLine();
		
		System.out.print("Address: ");
		address = input.nextLine();
		
		System.out.print("Email: ");
		email = input.nextLine();
		
		System.out.print("Password: ");
		password = input.nextLine();
		
		//Remove any white space and check for length (All attributes are required)
		if(password.trim().length() == 0 || email.trim().length() == 0 || address.trim().length() == 0 || 
			    cc_id.trim().length() == 0 || name.trim().length() == 0)
		{
			System.out.println("");
			System.out.println("Error: All attributes are required");
			System.out.println("Returning to Menu");
			return;
		}
		
		String [] nameParsed = name.split("\\s+");
		
		String firstName, lastName;
		if(nameParsed.length == 1)
		{
			//Only last Name Given
			firstName = "";
			lastName = nameParsed[0];
		}
		
		else
		{
		    firstName = nameParsed[0];
			lastName = nameParsed[1];	
		}
		
		String[] customer = {firstName,lastName,cc_id,address, email, password};
		
		
		try
		{
			db.insertCustomer(customer);
		}
		
		catch(SQLException e)
		{
			System.out.println(e);
			System.out.println("Error: There was problem in inserting customer into database");
		}	
		
	
	}

	private void addStar() {
		
		String firstName, lastName, dob, photoURL;
			
		System.out.println("");
		System.out.print("First Name (Blank if None): ");
		firstName = input.nextLine();
			
		System.out.print("Last Name: ");
		lastName = input.nextLine();
		
		if(lastName.length() == 0)
		{
			System.out.println("");
			System.out.println("Error: Last Name is Required");
			System.out.println("Returning to Menu");
			System.out.println("");
			
			return;
		}
		
		System.out.print("Date of Birth (YYYY-MM-DD)(Blank if None): ");
		dob = input.nextLine();		
		
		System.out.print("Photo URL (Blank if None): ");
		photoURL = input.nextLine();
		
		
		String[] star = {firstName,lastName,dob,photoURL};
		
		try 
		{
			db.addStar(star);
		} 
		
		catch (SQLException e)
		{
			System.out.println("");
			handler.handleException(e);
			System.out.println("Returning to main menu");
		}

	}

	public static void main(String[] args) {
		try{
			
			DBUserInterface process = new DBUserInterface();
			process.userInterface();
			
		}
		
		catch(Exception e)
		{	
			System.out.println(e);
		}
	
	}

}
