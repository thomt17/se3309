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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
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
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
public class BaseballBrowserMain extends JApplet {

	/**
	 * @param args
	 */
	
	int team1Wins;
	int team2Wins;
	int team1Losses;
	int team2Losses;
	String team1Text;
	String team2Text;

	
	double team1Weight;
	double team2Weight;
	double team1odds;
	double team2odds;
	
	final JComboBox team1 = new JComboBox();
	final JComboBox team2 = new JComboBox();
	
	JComboBox fireTeamList = new JComboBox();
	

	DefaultComboBoxModel teamModel;
	DefaultListModel playerModel;
	DefaultListModel gameModel;
	DefaultComboBoxModel filterModel;
	DefaultComboBoxModel fireTeamModel;
	
	DefaultComboBoxModel playingTeamList1 = new DefaultComboBoxModel();
	DefaultComboBoxModel playingTeamList2 = new DefaultComboBoxModel();
	
	DefaultComboBoxModel contractList1 = new DefaultComboBoxModel();
	DefaultComboBoxModel contractList2 = new DefaultComboBoxModel();
	
	DefaultListModel teamPlayersModel;

	static ResultSet resultSet;
	static JLabel playerDetail;
	static JList playerList;
	static JList teamPlayerList;
	static JList teamList;
	static JList gameList;
	static JComboBox contractBox1;
	static JComboBox contractBox2;
	static Connection db2Conn;
	static Statement st;
	
	JTextField yearField = new JTextField("2013");
	
	JLabel odds1 = new JLabel("Odds1");
	JLabel odds2 = new JLabel("Odds2");
	

	boolean loadTeams = true;
	boolean loadPlayers = true;
	boolean loadGames = true;
	boolean loadContracts = true;
	boolean loadGamesLists = true;
	
	boolean loading = false;


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
				text=(teamName+" Total Wins:"+totalWins+", Total Losses: "+totalLosses+", founded in "+yearFounded);
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

	public static Object[] queryTeamPlayers(String team, String year){
		ArrayList<String> players = new ArrayList<String>();
		try
		{
			String text="";

			// use a statement to gather data from the database
			st = db2Conn.createStatement();
			String myQuery = "SELECT * FROM Contract WHERE teamName='" + team + "' AND YEAR=" + year;   		//BOOK is a table name
			resultSet = st.executeQuery(myQuery); 		 // execute the query         
			while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
			{
				String playerName = resultSet.getString("playerName"); 	//These are column names
				String playerNumber = resultSet.getString("playerNumber"); 		//These are column names
				String timePeriod = resultSet.getString("timePeriod"); 		//These are column names
				String salary = resultSet.getString("salary");
				text=(playerName+" #:"+playerNumber+", Time Period: "+timePeriod+", Salary: "+salary);
				players.add(text);
			}

			// clean up resources
			resultSet.close();
			st.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}

		return  players.toArray();
	}

	public Object[] queryGames(String team) {
		// TODO Auto-generated method stub
		ArrayList<String> games = new ArrayList<String>();
		try
		{
			String text="";

			// use a statement to gather data from the database
			st = db2Conn.createStatement();

			String myQuery;

			if(team.equals("*"))
				myQuery = "SELECT * FROM Game";
			else
				myQuery = "SELECT * FROM Game WHERE winningTeamName='" + team + "' OR losingTeamName='" + team + "'";      		//BOOK is a table name


			resultSet = st.executeQuery(myQuery); 		 // execute the query         
			while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
			{
				String winner = resultSet.getString("winningTeamName"); 	//These are column names
				String loser = resultSet.getString("losingTeamName"); 		//These are column names
				String year = resultSet.getString("year"); 		//These are column names
				String date = resultSet.getString("date");
				String location = resultSet.getString("location");
				String winScore = resultSet.getString("winningTeamScore");
				String loseScore = resultSet.getString("losingTeamScore");
				text=(winner+" ("+winScore+") vs " + loser + "(" + loseScore + ") " + date  + " at " + location);
				games.add(text);
			}

			// clean up resources
			resultSet.close();
			st.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}

		return  games.toArray();
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

	public JPanel createViewTeamsTab(){


		teamList = new JList();

		teamModel = new DefaultComboBoxModel();
		teamList.setModel(teamModel);

		JPanel viewTeams = new JPanel();
		viewTeams.setLayout(new GridLayout(2,0));
		viewTeams.add(new JScrollPane(teamList));

		JButton viewTeamsBtn = new JButton("View Team History");
		viewTeamsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				queryTeamByYear(((String) teamList.getSelectedValue()).split(" Total")[0]);

			}
		});

		final JList teamPlayers = new JList();
		teamPlayersModel = new DefaultListModel();

		teamPlayers.setModel(teamPlayersModel);

		
		teamList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				
				updatePlayers();

			}
		});



		final JPanel teamsLowerPanel = new JPanel();
		teamsLowerPanel.setLayout(new GridLayout(2,0));

		JPanel teamsButtonPanel = new JPanel();
		teamsButtonPanel.setLayout(new GridLayout(3,0));

		JPanel flowButtons = new JPanel();
		flowButtons.setLayout(new FlowLayout());

		flowButtons.add(viewTeamsBtn);

		teamsButtonPanel.add(flowButtons);



		JPanel spacer = new JPanel();

		teamsButtonPanel.add(spacer);

		JLabel yearLabel = new JLabel("Players during: ");


		yearField.setPreferredSize( new Dimension( 100, 24 ) );

		JButton setYearBtn = new JButton("Change Year");
		setYearBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				teamPlayersModel.clear();

				//queryTeamByYear(((String) teamList.getSelectedValue()).split(" Total")[0]);
				String team = ((String) teamList.getSelectedValue()).split(" Total")[0];

				for(Object player: queryTeamPlayers(team, yearField.getText())){
					teamPlayersModel.addElement(player);
				}

			}
		});

		JPanel yearPanel = new JPanel();
		yearPanel.setLayout(new FlowLayout());
		yearPanel.add(yearLabel);
		yearPanel.add(yearField);
		yearPanel.add(setYearBtn);


		teamsButtonPanel.add(yearPanel);

		teamsLowerPanel.add(teamsButtonPanel);
		teamsLowerPanel.add(new JScrollPane(teamPlayers));



		viewTeams.add(teamsLowerPanel);

		return viewTeams;

	}
	
	public void updatePlayers(){
		if(loading)
			return;

		teamPlayersModel.clear();

		//queryTeamByYear(((String) teamList.getSelectedValue()).split(" Total")[0]);
		String team = ((String) teamList.getSelectedValue()).split(" Total")[0];

		for(Object player: queryTeamPlayers(team,"2013")){
			teamPlayersModel.addElement(player);
		}

		yearField.setText("2013");
	}

	public JPanel createViewPlayersTab(){

		playerList = new JList();
		playerModel = new DefaultListModel();
		playerList.setModel(playerModel);

		teamPlayerList = new JList();
		final DefaultListModel teamPlayerModel = new DefaultListModel();
		teamPlayerList.setModel(playerModel);

		//For viewing player careers
		playerList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if(loading)
					return;
				
				queryPlayerByYear(((String) playerList.getSelectedValue()).split(" #")[0]);
			}
		});

		final JPanel viewPlayers = new JPanel();
		viewPlayers.setLayout(new GridLayout(2,0));
		viewPlayers.add(new JScrollPane(playerList));

		JPanel playerGrid = new JPanel();
		playerGrid.setLayout(new GridLayout(3,0));


		playerDetail = new JLabel();

		playerGrid.add(playerDetail);

		viewPlayers.add(playerGrid);

		return viewPlayers;

	}

	public JPanel createViewGamesTab(){

		gameList = new JList();
		gameModel = new DefaultListModel();
		gameList.setModel(gameModel);

		JPanel viewGames = new JPanel();
		viewGames.setLayout(new GridLayout(2,0));
		viewGames.add(new JScrollPane(gameList));

		JPanel flowPanel = new JPanel();
		flowPanel.setLayout(new FlowLayout());

		final JComboBox teamFilter = new JComboBox();
		filterModel = new DefaultComboBoxModel();

		JLabel filterLabel = new JLabel("Team Filter: ");

		teamFilter.setModel(filterModel);
		filterModel.addElement("<None>");

		teamFilter.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				if(((String) teamFilter.getSelectedItem()).equals("<None>")){
					gameModel.clear();
					loadGames loader = new loadGames(gameModel,"*");
					loader.execute();
					return;
				}

				gameModel.clear();
				String team = ((String) teamFilter.getSelectedItem()).split(" Total")[0];
				loadGames loader = new loadGames(gameModel,team);
				loader.execute();

			}
		});

		flowPanel.add(filterLabel);
		flowPanel.add(teamFilter);

		viewGames.add(flowPanel);

		return viewGames;
	}

	public JPanel createPlayGamesTab(){
		
		JPanel playGamesTab = new JPanel();
		playGamesTab.setLayout(new GridLayout(3,0));
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout());
		
		
		
		
		team1.setModel(playingTeamList1);
		
		final JLabel stats1 = new JLabel();
		final JLabel stats2 = new JLabel();
		
		team1.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
			
				
				int index = team1.getSelectedIndex();
				String text = (String) teamModel.getElementAt(index);
				String wins = (text.split("Total Wins:")[1]).split(",")[0];
				String losses = (text.split("Total Losses: ")[1]).split(",")[0];
				team1Wins = Integer.parseInt(wins);
				team1Losses = Integer.parseInt(losses);
				
				
				double sum = team1Wins + team1Losses;
				
				team1Weight = (team1Wins / sum) * 100;
				
				team1odds = team1Weight / (team1Weight + team2Weight) * 100;
				team2odds = team2Weight / (team1Weight + team2Weight) * 100;
				
				stats1.setText("<html><body>Total Wins: " + wins + "<br> Total Losses: " + losses + "</body></html>");
				
				odds1.setText(new DecimalFormat("#.##").format(team1odds) + "% Odds");
				odds2.setText(new DecimalFormat("#.##").format(team2odds) + "% Odds");

			}
		});
		
		
		team2.setModel(playingTeamList2);
		
		team2.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				

				int index = team2.getSelectedIndex();
				String text = (String) teamModel.getElementAt(index);
				String wins = (text.split("Total Wins:")[1]).split(",")[0];
				String losses = (text.split("Total Losses: ")[1]).split(",")[0];
				team2Wins = Integer.parseInt(wins);
				team2Losses = Integer.parseInt(losses);
				
				//team2Weight = (team2Wins / (team2Wins + team2Losses)) * 100;
				
				double sum = team2Wins + team2Losses;
				
				team2Weight = (team2Wins / sum) * 100;
				
				team1odds = team1Weight / (team1Weight + team2Weight) * 100;
				team2odds = team2Weight / (team1Weight + team2Weight) * 100;
				
				stats2.setText("<html><body>Total Wins: " + wins + "<br> Total Losses: " + losses + "</body></html>");
				
				odds1.setText(new DecimalFormat("#.##").format(team1odds) + "% Odds");
				odds2.setText(new DecimalFormat("#.##").format(team2odds) + "% Odds");
				
			}
		});
		
		
		JLabel versus = new JLabel(" VS ");
		
		listPanel.add(team1);
		listPanel.add(versus);
		listPanel.add(team2);
		
		JPanel stats = new JPanel();
		stats.setLayout(new GridLayout(2,2));
		
		
		JPanel flowStats1 = new JPanel();
		flowStats1.setLayout(new FlowLayout());
		
		flowStats1.add(stats1);
		
		JPanel flowStats2 = new JPanel();
		flowStats2.setLayout(new FlowLayout());
		
		flowStats2.add(stats2);
		
		
		
		JPanel odds1Flow = new JPanel();
		odds1Flow.setLayout(new FlowLayout());
		odds1Flow.add(odds1);
		
		
		JPanel odds2Flow = new JPanel();
		odds2Flow.setLayout(new FlowLayout());
		odds2Flow.add(odds2);
		
		stats.add(flowStats1);
		
		stats.add(flowStats2);
		stats.add(odds1Flow);
		stats.add(odds2Flow);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		JButton playGame = new JButton("Simulate Game");
		
		
		
		playGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				
				if(team1.getSelectedItem().toString().equals(team2.getSelectedItem().toString())){
					JOptionPane.showMessageDialog(null, "A team can't play itself!");
					return;
				}
				
				double sumWeight = team1Weight + team2Weight;
				
				double random = Math.random() * sumWeight;
				
				double[] teamWeights = new double[2];
				
				teamWeights[0] = team1Weight;
				teamWeights[1] = team2Weight;
				
				int winner = -1;
				
				for(int i=0; i < 1; i++) {
					  if(random < teamWeights[i]){
					    winner = i;
					    break;
					  }
					  random -= teamWeights[i];
				}
				
				//team1 Wins
				if(winner == 0)
				{
					String winScore = new DecimalFormat("#").format(10 + (Math.random() * (17 - 10)));
					String loseScore = new DecimalFormat("#").format( 0 + (Math.random() * (9 - 0)));
					
					
					
					JOptionPane.showMessageDialog(null, team1.getSelectedItem().toString() +"(" + winScore + ") Won vs " + team2.getSelectedItem().toString() + "(" + loseScore + ")");
					
					newGame(team1.getSelectedItem().toString(), team2.getSelectedItem().toString(), "2013", "2013-01-01", generateString(20),  winScore, loseScore);
				}
				
				//team2 wins
				else{
					
					String winScore = new DecimalFormat("#").format(10 + (Math.random() * (17 - 10)));
					String loseScore = new DecimalFormat("#").format( 0 + (Math.random() * (9 - 0)));
					
					
					
					JOptionPane.showMessageDialog(null, team2.getSelectedItem().toString() +"(" + winScore + ") Won vs " + team1.getSelectedItem().toString() + "(" + loseScore + ")");
					
					newGame(team2.getSelectedItem().toString(), team1.getSelectedItem().toString(), "2013", "2013-01-01", generateString(20), winScore, loseScore);
					
				}
				
				
			}
		});
		
		buttons.add(playGame);
		
		playGamesTab.add(listPanel);
		playGamesTab.add(stats);
		playGamesTab.add(buttons);
				
		
		return playGamesTab;
		
	}

	
	public JPanel createTradeTab(){
		JPanel tradeTab = new JPanel();
		tradeTab.setLayout(new GridLayout(2,0));
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout());
		
		final JComboBox cb1 = new JComboBox();
		contractBox1 = cb1;
		contractBox1.setModel(contractList1);
		
		final JComboBox cb2 = new JComboBox();
		contractBox2 = cb2;
		contractBox2.setModel(contractList2);
		
		JButton tradeButton = new JButton("Make Trade!");
		
		tradeButton.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				int index = cb1.getSelectedIndex();
				String text1 = (String) contractList1.getElementAt(index);
				String player1Name = (text1.split(" #")[0]);
				String player1Team = (text1.split("from ")[1]).split(",")[0];
				int index2 = cb2.getSelectedIndex();
				String text2 = (String) contractList2.getElementAt(index2);
				String player2Name = (text2.split(" #")[0]);
				String player2Team = (text2.split("from ")[1]).split(",")[0];
				executeTrade(player1Name,player1Team,player2Name,player2Team);
				
				//String playerNumber = (text.split(" #")[1]).split(",")[0];
				//team2Wins = Integer.parseInt(wins);
				//stats2.setText("<html><body>Total Wins: " + wins + "<br> Total Losses: " + losses + "</body></html>");

			}
		});
		listPanel.add(cb1);
		listPanel.add(cb2);
		tradeTab.add(listPanel);
		tradeTab.add(tradeButton);
		
		return tradeTab;
	}
	
	public JPanel createSalaryTab() {
		JPanel salaryTab = new JPanel();
		salaryTab.setLayout(new GridLayout(2,0));
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new FlowLayout());
		JLabel instructLabel = new JLabel("Enter a % to raise or cut salaries by: ");
		final JTextField modifierBox = new JTextField(20);
		textPanel.add(instructLabel);
		textPanel.add(modifierBox);
		salaryTab.add(textPanel);
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		JButton raiseButton = new JButton("Raise");
		raiseButton.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				double mod = Double.parseDouble(modifierBox.getText());
				convertSalaries(mod);
			}
		});
		JButton cutButton = new JButton("Cut");
		cutButton.addActionListener (new ActionListener () {
			public void actionPerformed(ActionEvent e) {

				double mod = Double.parseDouble(modifierBox.getText());
				convertSalaries(mod*(-1));
			}
		});
		buttons.add(raiseButton);
		buttons.add(cutButton);
		salaryTab.add(buttons);
		return salaryTab;
	}
	
	public JPanel createFireTab(){
		
		fireTeamList = new JComboBox();

		fireTeamModel = new DefaultComboBoxModel();
		fireTeamList.setModel(teamModel);

		JPanel viewFirePlayers = new JPanel();
		viewFirePlayers.setLayout(new GridLayout(2,0));
		
		JPanel fireFlow = new JPanel();
		fireFlow.add(fireTeamList);
		
		JPanel grid = new JPanel();
		grid.setLayout(new GridLayout(2,0));
		
		grid.add(fireFlow);
		
		

		final JList fireTeamPlayers = new JList();
		final DefaultListModel fireTeamPlayersModel = new DefaultListModel();

		fireTeamPlayers.setModel(fireTeamPlayersModel);

		fireTeamList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(loading)
					return;

				fireTeamPlayersModel.clear();

				//queryTeamByYear(((String) teamList.getSelectedValue()).split(" Total")[0]);
				String team = ((String) fireTeamList.getSelectedItem()).split(" Total")[0];

				for(Object player: queryTeamPlayers(team,"2013")){
					fireTeamPlayersModel.addElement(player);
				}
				
			}
		});

		
		
		
		viewFirePlayers.add(grid);
		
		JPanel fireBtnFlow = new JPanel();
		fireBtnFlow.setLayout(new FlowLayout());
		
		JButton fireBtn = new JButton("FIRE");
		
		fireBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				String name = fireTeamPlayers.getSelectedValue().toString().split(" #")[0];
				String team = fireTeamList.getSelectedItem().toString().split(" Total")[0];
				
				try {
					firePlayer(fireTeamPlayers.getSelectedValue().toString().split(" #")[0]);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				JOptionPane.showMessageDialog(null, "<html><body>You fired " + name + " from the " + team + "<br>You Monster</body></html>" );
				
				fireTeamList.setSelectedIndex(fireTeamList.getSelectedIndex());
				updatePlayers();
				
				loadContracts = true;
				loadPlayers = true;
				
				
				
			}
			
			
		});
		
		fireBtnFlow.add(fireBtn);
		
		grid.add(fireBtnFlow);
		
		viewFirePlayers.add(new JScrollPane(fireTeamPlayers));
		


		return viewFirePlayers;
	}
	
	public void firePlayer(String name) throws SQLException {
		// TODO Auto-generated method stub
		System.out.println(name);
		
		// use a statement to gather data from the database
		st = db2Conn.createStatement();

		String myQuery = "DELETE FROM playerHistory WHERE playerName='" + name + "'";
		System.out.println(myQuery);//BOOK is a table name
		
		st.execute(myQuery); 		 // execute the query   
		
		myQuery = "DELETE FROM playerByYear WHERE playerName='" + name + "'";
		System.out.println(myQuery);//BOOK is a table name
		
		st.execute(myQuery); 		 // execute the query  
		
		myQuery = "DELETE FROM contract WHERE playerName='" + name + "'";
		System.out.println(myQuery);//BOOK is a table name
		
		st.execute(myQuery); 		 // execute the query  
		
		st.close();
		
		
		
		
	}

	private void executeTrade(String p1, String t1, String p2, String t2) {
		try
		{

			// use a statement to gather data from the database
			st = db2Conn.createStatement();

			String myQuery1;

			myQuery1 = "UPDATE Contract SET teamName='" + t2+"' WHERE playerName='"+p1+"'";  
			System.out.println(myQuery1);//BOOK is a table name


			st.execute(myQuery1); 		 // execute the query   
			st.close();
			st = db2Conn.createStatement();
			String myQuery2 = "UPDATE Contract SET teamName='" + t1+"' WHERE playerName='"+p2+"'";
			System.out.println(myQuery2);
			st.execute(myQuery2);
			
			st.close();
			loadTeams=true;
			contractList1.removeAllElements();
			contractList2.removeAllElements();
			loadContracts loadAgain = new loadContracts(contractList1,contractList2);
			loadAgain.execute();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}
	
	private void convertSalaries(double mod) {
		try
		{

			// use a statement to gather data from the database
			st = db2Conn.createStatement();

			String myQuery;

			myQuery = "UPDATE Contract SET salary=salary +salary *" +(mod*0.01);  
			System.out.println(myQuery);//BOOK is a table name


			st.execute(myQuery); 		 // execute the query   
			st.close();
			st = db2Conn.createStatement();
			
			st.close();
			loadTeams=true;
			contractList1.removeAllElements();
			contractList2.removeAllElements();
			loadContracts loadAgain = new loadContracts(contractList1,contractList2);
			loadAgain.execute();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
	}
	
	private void createGUI() {


		//Sets up the tabbed pane
		final JTabbedPane tabbedPane = new JTabbedPane();


		tabbedPane.addTab("View Teams", createViewTeamsTab());
		tabbedPane.addTab("View Players", createViewPlayersTab());
		tabbedPane.addTab("View Games", createViewGamesTab());
		tabbedPane.addTab("Play Games!", createPlayGamesTab());
		tabbedPane.addTab("Trade Players!", createTradeTab());
		tabbedPane.addTab("Convert Salaries!", createSalaryTab());
		tabbedPane.addTab("FIRE PEOPLE, RUIN THEIR LIVES!", createFireTab());



		tabbedPane.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {


				if(tabbedPane.getSelectedIndex() == 0 && loadTeams){
					
					loadTeamHistory loader = new loadTeamHistory(teamModel);
					loader.execute();
					loadTeams = false;
				}
				else if(tabbedPane.getSelectedIndex() == 1 && loadPlayers){
					loading = true;
					loadPlayerHistory loader = new loadPlayerHistory(playerModel);
					loader.execute();
					loadPlayers = false;
				}
				else if(tabbedPane.getSelectedIndex() == 2 && loadGames){
					loadGames loader = new loadGames(gameModel,"*");
					loader.execute();
					loadGames = false;
				}
				else if (((tabbedPane.getSelectedIndex() == 4) || (tabbedPane.getSelectedIndex() == 5)) && loadContracts) {
					loadContracts loader = new loadContracts(contractList1,contractList2);
					loader.execute();
					loadContracts = false;
				}

			}

		});



		add(tabbedPane, BorderLayout.CENTER);

		loadTeamHistory loader = new loadTeamHistory(teamModel);
		loader.execute();

		loadTeams = false;



	}
	
	public void newGame(String winner, String loser, String year, String date, String location, String winScore, String loseScore){
		
		// use a statement to gather data from the database
		try {
			st = db2Conn.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String myQuery = "INSERT INTO GAME VALUES('" + winner + "','" + loser + "'," + year + ",'" + date + "','" + location + "'," + winScore + "," + loseScore + ")" ; 
		
		
		int winnerWins = -1;
		int loserLosses = -1;
		
		if(team1.getSelectedItem().toString().equals(winner)){
			winnerWins = team1Wins;
			loserLosses = team2Losses;
		}
		else{
			winnerWins = team2Wins;
			loserLosses = team2Losses;
		}
			
			winnerWins++;
			loserLosses++;
			
		String updateQuery1 = "UPDATE teamHistory SET totalWins=" + winnerWins + " WHERE teamName='" + winner + "'";
		System.out.println(updateQuery1);
		
		String updateQuery2 = "UPDATE teamHistory SET totalLosses=" + loserLosses + " WHERE teamName='" + loser + "'";
		System.out.println(updateQuery2);
		
		
		try {
			st.execute(myQuery);
			st.execute(updateQuery1);
			st.execute(updateQuery2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		loading = true;
		
		teamModel.removeAllElements();
		
		
		loadTeamHistory loader = new loadTeamHistory(teamModel);
		loader.execute();
		loadTeams = false;
		
	}

	public static void queryPlayerByYear(String playerName) {
		String text="<html><body>";
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
				text+="In "+year+", played "+position+" , batted "+ba+", with a "+era+" ERA and "+war+" WAR.<br>";
			}

			text+="</body></html>";

			// clean up resources
			resultSet.close();
			st.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		//resultSet.



		playerDetail.setText(text);

	}

	public static void queryTeamByYear(String teamName) {
		String text="";
		ArrayList<String> info = new ArrayList<String>();
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
				info.add(text);
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

		JList years = new JList();
		DefaultListModel yearModel = new DefaultListModel();

		for (String year : info) {
			yearModel.addElement(year);
		}

		years.setModel(yearModel);

		pbyFrame.add(new JScrollPane(years));

		pbyFrame.setVisible(true);
		pbyFrame.pack();
	}
	
	public static Object[] queryContracts() {
		ArrayList<String> contracts = new ArrayList<String>();
		try
		{
			String text="";

			// use a statement to gather data from the database
			st = db2Conn.createStatement();

			String myQuery;

			myQuery = "SELECT * FROM Contract WHERE Year = 2013";      		//BOOK is a table name


			resultSet = st.executeQuery(myQuery); 		 // execute the query         
			while (resultSet.next())				 // cycle through the resulSet and display what was grabbed
			{
				String playerName = resultSet.getString("playerName"); 	//These are column names
				String playerNumber = resultSet.getString("playerNumber"); 		//These are column names
				//String year = resultSet.getString("year"); 		//These are column names
				String teamName = resultSet.getString("teamName");
				String salary = resultSet.getString("salary");
				text=(playerName+" #"+playerNumber+", from "+teamName+", earning $"+salary);
				contracts.add(text);
			}

			// clean up resources
			resultSet.close();
			st.close();
		}
		catch (SQLException sqle)
		{
			sqle.printStackTrace();
		}
		return contracts.toArray();
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
				
				dcm.removeAllElements();

				for( Object newRow : players ) {
					dcm.addElement( newRow );
				}
				
				loading = false;

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}

	}

	public class loadTeamHistory extends SwingWorker<Object[], Void >{

		DefaultComboBoxModel dcm;


		public loadTeamHistory(DefaultComboBoxModel teamModel){
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
					String team = newRow.toString().split(" Total")[0];
					
					if(loadGamesLists){
						playingTeamList1.addElement(team);
						playingTeamList2.addElement(team);
						
					}
					
					fireTeamModel.addElement(team);
				}
				
				loadGamesLists = false;
				loading = false;
				
				team1.setSelectedIndex(team1.getSelectedIndex());
				team2.setSelectedIndex(team2.getSelectedIndex());
				

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}

	}
	public class loadGames extends SwingWorker<Object[], Void>{

		DefaultListModel dcm;
		String team;

		public loadGames(DefaultListModel teamModel, String _team){
			dcm = teamModel;
			team = _team;
		}

		@Override
		protected Object[] doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return queryGames(team);
			
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

			loadTeamHistory gameFilterLoader = new loadTeamHistory(filterModel);
			gameFilterLoader.execute();



		}

	}
	
	public class loadContracts extends SwingWorker<Object[], Void>{

		DefaultComboBoxModel dcm;
		DefaultComboBoxModel dcm2;

		public loadContracts(DefaultComboBoxModel contractModel1, DefaultComboBoxModel contractModel2){
			dcm = contractModel1;
			dcm2 = contractModel2;
		}

		@Override
		protected Object[] doInBackground() throws Exception {
			// TODO Auto-generated method stub
			//return queryGames(team);
			return queryContracts();
		}

		@Override
		public void done() {
			try {
				Object[] contracts = get();

				for( Object newRow : contracts ) {
					dcm.addElement( newRow );
					dcm2.addElement(newRow);
				}
				

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//loadContracts contractLoader = new loadContracts(contractList1, contractList2);
			//contractLoader.execute();

		}

	}

	public static String generateString(int length)
	{
		String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		
		Random rng = new Random();
		
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++)
	    {
	        text[i] = characters.charAt(rng.nextInt(characters.length()));
	    }
	    return new String(text);
	}


}
