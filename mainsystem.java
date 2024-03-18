import java.util.*;
import java.io.*;

public class mainsystem {
    public static String systemdate = "0000-00-00";
    public int mainInterface() throws IOException{
        String output = "";
		output += "The System Date is now: " + systemdate + "\n";
		output += "<This is the Book Ordering System.>\n";
		output += "---------------------------------------\n";
		output += "1. System interface.\n";
		output += "2. Customer interface.\n";
		output += "3. Bookstore interface.\n";
		output += "4. Show System Date.\n";
		output += "5. Quit the system......\n";
		output += "\n";
        System.out.printf(output);

        Scanner scanner = new Scanner(System.in);
        String input = null;
        while(true)
        {
            System.out.println("Please enter your choice??..");
            input = scanner.nextLine();		
            if(input.equals("1") || input.equals("2") || input.equals("3") || input.equals("4") || input.equals("5")) 
            {   
                scanner.close();
                return input.charAt(0) - '0';
            }
            else 
            System.out.println("INVALID INPUT!\n");
        }
    }

    public static void main(String[] args) throws IOException{
        mainsystem mainsystem = new mainsystem();
        while(true)
        {
            int result = mainsystem.mainInterface();
            if(result==1)
            system.system_main();
            if(result==2)
            customer.customer_main();
            if(result==3)
            bookstore.bookstore_main();
            if(result==4)
            System.out.println("The System Date is now: " + systemdate);
            if(result==5)
            break;
        }
    
    }
}