package se3309;
import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.*;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JLabel;
public class BaseballBrowserMain extends JApplet {

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		test();
        
    }*/
	public static void test() {
		try
	       {
	            Class.forName("com.ibm.db2.jcc.DB2Driver"); 		 // load the DB2 Driver
		 // establish a connection to DB2
	            Connection db2Conn = DriverManager.getConnection	
	             ("jdbc:db2://ebitdb2.eng.uwo.ca:50000/SE3309","nward24","nicholas"); //Put your username and password here
	            // use a statement to gather data from the database
	            Statement st = db2Conn.createStatement();
	            String myQuery = "SELECT * FROM PlayerHistory";   		//BOOK is a table name
	            ResultSet resultSet = st.executeQuery(myQuery); 		 // execute the query         
	            while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
	            {
	             String playerName = resultSet.getString("playerName"); 	//These are column names
	             String playerNumber = resultSet.getString("playerNumber"); 		//These are column names
	               System.out.println(playerName+" "+playerNumber);
	           }
	           // clean up resources
	           resultSet.close();
	           st.close();
	           db2Conn.close();
	       }
	       catch (ClassNotFoundException cnfe)
	       {
	           cnfe.printStackTrace();
	       }
	       catch (SQLException sqle)
	       {
	           sqle.printStackTrace();
	       }
	}
	public void init() {
	    createGUI();
	    test();
	}

	private void createGUI() {
	    JLabel label = new JLabel(
	                       "You are successfully running a Swing applet!");
	    label.setHorizontalAlignment(JLabel.CENTER);
	    label.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
	    getContentPane().add(label, BorderLayout.CENTER);
	}
	

	

}
