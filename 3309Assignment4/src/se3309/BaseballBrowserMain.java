package se3309;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
public class BaseballBrowserMain extends JApplet {

	/**
	 * @param args
	 */
	static ResultSet resultSet;
	static JList playerList;
	static JList teamList;
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
	
	public static Object[] queryTeamHistory() {
		ArrayList<String> teamHistories = new ArrayList<String>();
		try
		{
			String text="";
			
			// use a statement to gather data from the database
			st = db2Conn.createStatement();
			String myQuery = "SELECT * FROM TeamHistory ORDER BY TeamName";   		//BOOK is a table name
			resultSet = st.executeQuery(myQuery); 		 // execute the query         
			while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
			{
				String teamName = resultSet.getString("teamName"); 	//These are column names
				String totalWins = resultSet.getString("totalWins"); 		//These are column names
				String totalLosses = resultSet.getString("totalLosses");
				String yearFounded = resultSet.getString("yearFounded");
				text=(teamName+" Total Wins:"+totalWins+", Total Losses "+totalLosses+", founded in "+yearFounded);
				teamHistories.add(text);
			}

			// clean up resources
			resultSet.close();
			st.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		
		return  teamHistories.toArray();
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
		setSize(500, 500);
		createGUI();
	}

	private void createGUI() {
		
		
		playerList = new JList();
		final DefaultListModel playerModel = new DefaultListModel();
		playerList.setModel(playerModel);
		
		teamList = new JList();
		final DefaultListModel teamModel = new DefaultListModel();
		teamList.setModel(teamModel);
		
		JButton viewPlayersBtn = new JButton("View Player Career");
		viewPlayersBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				queryPlayerByYear(((String) playerList.getSelectedValue()).split(" #")[0]);

			}
		});

		JButton viewGamesBtn = new JButton("View Games");
		
		final JTabbedPane tabbedPane = new JTabbedPane();
		
		final JPanel viewPlayers = new JPanel();
		viewPlayers.setLayout(new GridLayout(2,0));
		viewPlayers.add(new JScrollPane(playerList));
		
		
		final JPanel viewPlayersButtons = new JPanel();
		viewPlayersButtons.setLayout(new FlowLayout());
		viewPlayersButtons.add(viewPlayersBtn);
		
		viewPlayers.add(viewPlayersButtons);
		
		JPanel viewTeams = new JPanel();
		viewTeams.setLayout(new GridLayout(2,0));
		viewTeams.add(new JScrollPane(teamList));
		
		JButton viewTeamsBtn = new JButton("View Team History");
		viewTeamsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				queryTeamByYear(((String) teamList.getSelectedValue()).split(" Total")[0]);

			}
		});
		
		final JPanel viewTeamsButtons = new JPanel();
		viewTeamsButtons.setLayout(new FlowLayout());
		viewTeamsButtons.add(viewTeamsBtn);
		
		viewTeams.add(viewTeamsButtons);
		
		
		
		//viewTeams.add(viewTeamsBtn);
		JPanel viewGames = new JPanel();
		//viewGames.add(viewGamesBtn);
		
		tabbedPane.addTab("View Teams", viewTeams);
		tabbedPane.addTab("View Player", viewPlayers);
		tabbedPane.addTab("View Games", viewGames);
		
		
		
		tabbedPane.addChangeListener(new ChangeListener() {
			
		      public void stateChanged(ChangeEvent e) {
		    	  
		    	  if(tabbedPane.getSelectedIndex() == 0){
		    		  loadTeamHistory loader = new loadTeamHistory(teamModel);
		    		  loader.execute();
		    	  }
		    	  
		    	  if(tabbedPane.getSelectedIndex() == 1){
		    		  loadPlayerHistory loader = new loadPlayerHistory(playerModel);
		    		  loader.execute();
		    	  }
		    	  
		    	  
		      }

		});

		
		
		add(tabbedPane, BorderLayout.CENTER);
		


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
	
	public static void queryTeamByYear(String teamName) {
		String text="";
		ArrayList<JLabel> info = new ArrayList<JLabel>();
		try
		{
			// use a statement to gather data from the database
			st = db2Conn.createStatement();
			String myQuery = "SELECT * FROM TeamByYear WHERE teamName = '"+teamName+"'";   		//BOOK is a table name
			resultSet = st.executeQuery(myQuery); 		 // execute the query         
			while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
			{
				String year = resultSet.getString("year");
				String wins = resultSet.getString("wins");
				String losses = resultSet.getString("losses");
				String location = resultSet.getString("location");
				String manager = resultSet.getString("manager");
				String payroll = resultSet.getString("payroll");
				String profit = resultSet.getString("profit");
				String owner = resultSet.getString("owner");
				text="In "+year+", Wins: "+wins+" , Losses: "+losses+", Location: "+location+" Manager: "+manager+" Payroll: " + payroll + " Profit: " + profit + " Owner: " + owner + "\n";
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
		pbyFrame.setTitle(teamName+" Info");
		pbyFrame.setLayout(new GridLayout(0,1));
		
		for (int i=0; i<info.size(); i++) {
			pbyFrame.add(info.get(i));
		}
		pbyFrame.setVisible(true);
		pbyFrame.pack();
	}
	
	
	public class loadPlayerHistory extends SwingWorker<Object[], Void >{

		DefaultListModel dcm;
		
		
		public loadPlayerHistory(DefaultListModel playerModel){
			dcm = playerModel;
		}
		
		@Override
		protected Object[] doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return queryPlayerHistory();
		}
		
		@Override
	    public void done() {
	    	try {
				Object[] players = get();
				
				for( Object newRow : players ) {
				    dcm.addElement( newRow );
				}
				
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	        
	    }
		
	}

	public class loadTeamHistory extends SwingWorker<Object[], Void >{

		DefaultListModel dcm;
		
		
		public loadTeamHistory(DefaultListModel teamModel){
			dcm = teamModel;
		}
		
		@Override
		protected Object[] doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return queryTeamHistory();
		}
		
		@Override
	    public void done() {
	    	try {
				Object[] teams = get();
				
				for( Object newRow : teams ) {
				    dcm.addElement( newRow );
				}
				
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	        
	    }
		
	}

}
