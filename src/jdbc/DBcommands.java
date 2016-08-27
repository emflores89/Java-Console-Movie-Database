package jdbc;

import java.sql.*;
import java.util.HashMap;                          

public class DBcommands{
	
	private Connection connection;
	
	public void setConnection(Connection conn)
	{
		this.connection = conn;
	}
	
	public void addStar(String[] star) throws SQLException
	{
		String firstName,lastName,dob,photoURL;
		
		firstName = star[0];
		lastName = star[1];
		dob = star[2];
		photoURL = star[3];
	
		String insertTableSQL;
		PreparedStatement preparedStatement;
		
		if(dob.trim().isEmpty())
		{
			insertTableSQL = "INSERT INTO stars"
					+ "(first_name, last_name, photo_url) VALUES"
					+ "(?,?,?)";
			
			preparedStatement = connection.prepareStatement(insertTableSQL);
			
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			preparedStatement.setString(3, photoURL);
		}
		
		else
		{
			insertTableSQL = "INSERT INTO stars"
					+ "(first_name, last_name, dob, photo_url) VALUES"
					+ "(?,?,?,?)";
			
			preparedStatement = connection.prepareStatement(insertTableSQL);
			
			preparedStatement.setString(1, firstName);
			preparedStatement.setString(2, lastName);
			preparedStatement.setString(3, dob);
			preparedStatement.setString(4, photoURL);
			
		}
		
		
		// execute insert SQL statement
		int rows = preparedStatement.executeUpdate();
		
		if(rows > 0)
		{
			System.out.println("Star added to database (Rows Affected: " + rows + ")");
		}
		
		else
		{
			System.out.println("Star not to database (Rows Affected: " + rows + ")");
		}
		
		preparedStatement.close();
	}
	
	public void printStar(HashMap<String,String> star) throws SQLException
	{
		/**Print out (to the screen) the movies featuring a given star*/
		

		//String lastName = "\"" + star[2] + "\"";
		
		// Create an execute an SQL statement to select all of table"Stars" records
		String query = "SELECT "
						+"movies.id as film_id,"
						+"movies.title,"
						+"movies.year,"
						+"movies.director,"
						+"movies.banner_url,"
						+"movies.trailer_url"
						+" FROM movies"
							+" INNER JOIN stars_in_movies"
								+" ON movies.id = stars_in_movies.movie_id"
							+" INNER JOIN stars"
								+" ON stars_in_movies.star_id = stars.id"
						+" WHERE";

		
		int count = star.size();
		
		for (String key : star.keySet()) {
		    String value = star.get(key);
		    
		    if(count == 1)
		    {
		    	query += " " + key + " =" + value;
		    }
		    
		    else
		    {
		    	query += " " + key + " =" + value + " AND";	  	    	
		    }
		    
		    count--;
		}
		
		
		Statement select = connection.createStatement();
        ResultSet result = select.executeQuery(query);
        
        int entry = 0;
        while (result.next()) {
        	 entry++;
        	System.out.println("Movie #" + entry);
        	System.out.println("---------------------------------------------");
        	
            System.out.println("Movie ID: " + result.getString(1));
            System.out.println("Title: " + result.getString(2));
            System.out.println("Year: " + result.getString(3));
            System.out.println("Director: " + result.getString(4));
            System.out.println("Banner URL: " + result.getString(5));
            System.out.println("Trailer URL: " + result.getString(6));
            System.out.println();
   
        }
        
        System.out.println("Total Number of Results: " + entry);
        
        select.close();
        result.close();
	}
	
	public void insertCustomer(String[] customer) throws SQLException
	{
		String firstName,lastName,cc_id,address,email,password;
		
	    firstName = customer[0];
		lastName = customer[1];
		cc_id = customer[2];
		address = customer[3];
		email = customer[4];
		password = customer[5];
		
		String query = "SELECT COUNT(*) FROM creditCards WHERE id=?";
		
		PreparedStatement statement = connection.prepareStatement(query);
		
		statement.setString(1, cc_id);

        ResultSet result = statement.executeQuery();
        
        result.next();
        if(result.getInt(1) == 0)
        {
        	System.out.println("Error: No Customer Found in Credit Card Database");
        	return;
        }
        
        String insertTableSQL = "INSERT INTO customers"
				+ "(first_name, last_name, cc_id, address, email, password) VALUES"
				+ "(?,?,?,?,?,?)";
        
        PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
		
		preparedStatement.setString(1, firstName);
		preparedStatement.setString(2, lastName);
		preparedStatement.setString(3, cc_id);
		preparedStatement.setString(4, address);
		preparedStatement.setString(5, email);
		preparedStatement.setString(6, password);
		
		int row = preparedStatement.executeUpdate();
		
		System.out.println("Success: Inserted customer " + lastName + " into the database ("+ row +" Rows affected)");
	
		preparedStatement.close();
	}
	
	public void deleteCustomer(String cc_id) throws SQLException 
	{
		
		Statement update = connection.createStatement();
		//String query = "DELETE FROM CUSTOMERS WHERE cc_id =" + cc_id;
		//Statement preparedStatement = connection.prepareStatement(query);
		int status = update.executeUpdate("DELETE FROM CUSTOMERS WHERE cc_id = '"+cc_id+"'");

		System.out.println("Deletion Status: " + status + " Rows deleted");
		
		update.close();
		
	}

	public void getMetaData() throws SQLException
	{
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, "%", null);
		
		
		while (rs.next()) 
		{
		  String tableName = rs.getString(3);
		  System.out.println("Table: " +tableName);
		  System.out.println("-----------------------------------------");
		    
		  Statement select = connection.createStatement();
	      ResultSet tableData = select.executeQuery("DESCRIBE " + tableName);
		  
	      System.out.printf("%-15s%-15s%-8s%s\n","Field","Type","NULL","key");

	      while(tableData.next())
	      {
	    	  String field = tableData.getString(1);
	    	  String type = tableData.getString(2);
	    	  String NULL = tableData.getString(3);
	    	  String key = tableData.getString(4);
	    	  
	    	  System.out.printf("%-15s%-15s%-8s%s\n",field,type,NULL,key);

	      }
	      System.out.println("");
	      select.close();
	      tableData.close();
	      
		}	
		rs.close();
		
	}

	public void executeQuery(String query) throws SQLException
	{
		
		if(query.contains("SELECT"))
		{
			Statement select = connection.createStatement();
	        ResultSet result = select.executeQuery(query);
	        ResultSetMetaData md = result.getMetaData();
	        
	        int columnsNumber = md.getColumnCount();
	        	   
	        int row = 0;
	        while(result.next())
	        {      
	        	row++;
	        	System.out.println("Row #" + row);
	        	System.out.println("---------------------------------------------------------");      	
	        	for(int i = 1; i <= columnsNumber; i++) {  
	        		String columnName = md.getColumnName(i);
	                String columnValue = result.getString(i);
	                
	                System.out.println(columnName + ": " + columnValue);
	            }
	    
	        	System.out.println("");
	        }
	        
	        System.out.println("Number of rows: " + row);
	        System.out.println("");
	        
	        select.close();
	        result.close();
		}
		
		else
		{
			
			Statement select = connection.createStatement();
	        int status = select.executeUpdate(query);
			
	        System.out.println(status + " Rows Afftected");
	        select.close();
		}
		
		
	}

}
