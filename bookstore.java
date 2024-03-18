import java.util.*;
import java.io.*;
import java.sql.*;

public class bookstore {
    public static String dbAddress = "jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
    public static String dbUsername = "h014";
    public static String dbPassword = "CaicOtan";
            
            
    public int bookstoreInterface() throws IOException
	{
		String Output = "";
		Output += "<This is the bookstore interface.>\n";
		Output += "---------------------------------------\n";
		Output += "1. Order Update.\n";
		Output += "2. Order Query.\n";
		Output += "3. N most Popular Book Query.\n";
		Output += "4. Back to main menu.\n";
		Output += "\n";
		
		//Prepare the reader which reads user inputs from the console
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
				
		System.out.printf(Output);
		
		while(true)
		{
			System.out.println("Please enter your choice??..");
			input = reader.readLine();		
			if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4"))
			{
				int choice = input.charAt(0) - '0';
				return choice;
			}
			else
				System.out.println("Invalid input. Please input again.");
		}
	}
    
    public static void bookstore_main() throws IOException
                {	
		// Database driver
		Connection con = null;
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(dbAddress, dbUsername, dbPassword);
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("JAVA MYSQL DB Driver not found!");
			System.exit(0);
		}
		catch (SQLException e)
		{
			System.out.println(e);
			System.exit(0);
		}	

		
		bookstore myBookstoreObj = new bookstore();
		
		String orderid = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		
		while (true)
		{
			int choice = myBookstoreObj.bookstoreInterface();
                        
			// Back to main page
			if (choice == 4) break;	 
		
			// Order Update
			if (choice == 1) 
			{
				System.out.printf("Please input the order ID:");
				orderid = reader.readLine();
				
				ResultSet rs3=null;
				PreparedStatement pstmt = null;
				
				boolean idvalue= false ; 
				
				
				ResultSet rs1=null, rs2 = null;
				String status=null;
				int quantity=0, updateStatus1=0;
				
				try {
					String psql = "SELECT O.shipping_status FROM orders O WHERE O.order_id=?";
					pstmt = con.prepareStatement(psql);
					pstmt.setString(1, orderid);
					rs1 = pstmt.executeQuery();
					
					String psq2 = "SELECT OL.quantity FROM ordering OL WHERE OL.order_id=?";
					pstmt = con.prepareStatement(psq2);
					pstmt.setString(1, orderid);
					rs2 = pstmt.executeQuery();
					
					while(rs1.next()) {
						status = rs1.getString("shipping_status");
					}
					while(rs2.next()) {
						quantity = rs2.getInt("quantity");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}


				
				if (status.equals("N") && quantity>=1) {
					System.out.printf("the Shipping status of %s is %s and %d books ordered\n", orderid,status,quantity);
					System.out.printf("Are you sure to update the shipping status? (Yes=Y)");
					String updateornot=reader.readLine();
					if (updateornot.equals("Y")) {
						
						try {
							String psq3 = "UPDATE orders OL SET OL.shipping_status='Y' WHERE OL.order_id=?";
							pstmt = con.prepareStatement(psq3);
							pstmt.setString(1, orderid);
							updateStatus1 = pstmt.executeUpdate();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						System.out.printf("Updated shipping status\n\n");
					}
					else{ 
					System.out.printf("Update is cancelled.\n\n");
					}
				}
				else {
				System.out.printf("Update failed. Reason1: order_id doesn't exist. Reason2: the shipping status is Y. Reason3: less than 1 book is ordered.\n\n");
				}
			
			}
			
			// Order Query
			if (choice == 2) 
			{
				System.out.printf("Please input the Month for Order Query (e.g.2005-09):");
				String monthofquery=reader.readLine();
				
				ResultSet rs3=null;
				PreparedStatement pstmt = null;
				
				try {
					String psql = "SELECT * FROM orders WHERE date LIKE ? ORDER BY order_id";
					pstmt = con.prepareStatement(psql);
					pstmt.setString(1, monthofquery+"-__");
					rs3 = pstmt.executeQuery();
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				int chargetotal = 0;
				int counter=0;
				try {
					while(rs3.next())
					{
						counter++;
						System.out.printf("Record: %d\n", counter);
						
						String order_id = rs3.getString("order_id");
						String customer_id = rs3.getString("customer_id");
						String date=rs3.getString("date");
						int charge = rs3.getInt("charge");
						
						chargetotal=chargetotal+charge;
						
						System.out.println("order_id: " + order_id);
						System.out.println("customer_id: " + customer_id);
						System.out.println("date: " + date);
						System.out.printf("charge: %d\n\n", charge);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				if (counter!=0) 
					System.out.printf("Total charges of the month is %d\n\n", chargetotal);
				else 
					System.out.printf("No record of the month.\n\n");
	
				
			}	
			
			// N most Popular Book Query
			if (choice == 3) 
			{
				System.out.printf("Please input the N popular books number:");
				String input = reader.readLine();

				
				int input_num = Integer.parseInt(input);
				
				if(input_num<=0) {
					System.out.printf("Invalid input. Please input again.\n");
				}
				else {
				
				ResultSet rs4=null;
				PreparedStatement pstmt = null;
				
				try {
					String psql = "SELECT a.sum, a.ISBN "
							+ "FROM (SELECT sum(quantity) as sum,ISBN FROM ordering GROUP BY ISBN)a "
							+ "ORDER BY a.sum DESC "
							+ "LIMIT ? ";
					pstmt = con.prepareStatement(psql);
					pstmt.setInt(1, input_num);
					rs4 = pstmt.executeQuery();
							
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				//quantity
				int limit=1000000;
				try {
					while(rs4.next())
					{
						int quantitysum = rs4.getInt("sum");
						
						if (quantitysum<limit) {
							limit=quantitysum;
						}
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				System.out.printf("ISBN              Title                Copies\n");
				
				ResultSet rs5=null;
				PreparedStatement pstmt1 = null;
				
				try {
					String psq2 = "SELECT b.ISBN, b.title, c.sum "
								+ "FROM book b, (SELECT sum(quantity) as sum,ISBN FROM ordering GROUP BY ISBN)c "
								+ "WHERE c.sum>=? AND c.ISBN=b.ISBN "
								+ "ORDER BY c.sum DESC, b.title, b.ISBN";
					pstmt1 = con.prepareStatement(psq2);
					pstmt1.setInt(1, limit);
					rs5 = pstmt1.executeQuery();
							
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				try {
					while(rs5.next())
					{
						String quantitysum = rs5.getString("sum");
						String ISBN=rs5.getString("ISBN");
						String title=rs5.getString("title");
						System.out.printf("%s     %s     %s\n", ISBN,title,quantitysum );
						
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				}
			}
			
		}
	}
}