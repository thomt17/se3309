package se3309;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
public class BaseballBrowserMain extends JApplet {

	/**
	 * @param args
	 */
	static ResultSet resultSet;
	static JComboBox playerList;
	static Connection db2Conn;
	static Statement st;
	
	
	public static Object[] queryPlayerHistory() {
		ArrayList<String> playerHistories = new ArrayList<String>();
		try
		{
			String text="";
			
			// use a statement to gather data from the database
			st = db2Conn.createStatement();
			String myQuery = "SELECT * FROM PlayerHistory ORDER BY PlayerName";   		//BOOK is a table name
			resultSet = st.executeQuery(myQuery); 		 // execute the query         
			while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
			{
				String playerName = resultSet.getString("playerName"); 	//These are column names
				String playerNumber = resultSet.getString("playerNumber"); 		//These are column names
				String birthDate = resultSet.getString("birthDate");
				String birthPlace = resultSet.getString("birthPlace");
				text=(playerName+" #"+playerNumber+", born "+birthDate+" in "+birthPlace);
				playerHistories.add(text);
			}

			// clean up resources
			resultSet.close();
			st.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		return  playerHistories.toArray();
	}
	public void init() {
		
		//Open the DB2 connection
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver"); 	 // load the DB2 Driver
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 	
		
		try {
			db2Conn = DriverManager.getConnection	
					("jdbc:db2://ebitdb2.eng.uwo.ca:50000/SE3309","nward24","nicholas"); // establish a connection to DB2
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Start the GUI
		createGUI();
	}

	private void createGUI() {
		playerList = new JComboBox(queryPlayerHistory());

		JButton viewPlayers = new JButton("View Player Career");
		viewPlayers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				queryPlayerByYear(((String) playerList.getSelectedItem()).split(" #")[0]);

			}
		});

		getContentPane().setLayout(new GridLayout(0,1));
		getContentPane().add(playerList);
		getContentPane().add(viewPlayers);


	}

	public static void queryPlayerByYear(String playerName) {
		String text="";
		ArrayList<JLabel> info = new ArrayList<JLabel>();
		try
		{
			// use a statement to gather data from the database
			st = db2Conn.createStatement();
			String myQuery = "SELECT * FROM PlayerByYear WHERE PlayerName = '"+playerName+"'";   		//BOOK is a table name
			resultSet = st.executeQuery(myQuery); 		 // execute the query         
			while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
			{
				String year = resultSet.getString("year");
				String position = resultSet.getString("position");
				String ba = resultSet.getString("BattingAverage");
				String era = resultSet.getString("EarnedRunAverage");
				String war = resultSet.getString("WinsAboveReplacement");
				text="In "+year+", played "+position+" , batted "+ba+", with a "+era+" ERA and "+war+" WAR.\n";
				info.add(new JLabel(text));
			}

			// clean up resources
			resultSet.close();
			st.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		//resultSet.
		JFrame pbyFrame = new JFrame();
		pbyFrame.setTitle(playerName+" Info");
		pbyFrame.setLayout(new GridLayout(0,1));
		
		for (int i=0; i<info.size(); i++) {
			pbyFrame.add(info.get(i));
		}
		pbyFrame.setVisible(true);
		pbyFrame.pack();
	}




}
