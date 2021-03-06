package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/** Server side of an application which scrapes text from a user-selected 
 *  website and displays top10 (and all) word occurrences to a Client-side JavaFX GUI.
 *  @author derekdileo */
public class Main extends Application {
	
	// String array to hold QuestionBox.display() responses from Client.
	protected static String[] userResponses;
	
	// Local Lists and Maps to hold return values from Class methods
	private String[] wordsArray;
	
	// Varibles used to show / hide text on GUI
	private StringBuilder sbTen;
	private StringBuilder sbAll;
	
	// These are accessed by the four Controller classes to print to GUI 
	protected static String sbTenString;
	protected static String sbAllString;
	
	/** Main method calls launch() to start JavaFX GUI.
	 *  @param args mandatory parameters for command line method call */
	public static void main(String[] args) {
		// Create wordsTable if it doesn't exist
		try {
			Database.createWordsTable("words");
		} catch (Exception e) {
			System.out.println("Error creating words table in main()!");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		launch();
	}
	
	// Declare Stage outside of start() Method so it is accessible to closeProgram() method
	private static Stage window;
	
	/** The start method (which is called on the JavaFX Application Thread) 
	 * is the entry point for this application and is called after the init 
	 * method has returned- most of the application logic takes place here. */
	@Override
	public void start(Stage primaryStage) {
		
		// Rename stage to window for sanity
		window = primaryStage;
		
		// Text area for displaying Server contents
		TextArea ta = new TextArea();
		
		// Create a scene and place it in the stage
		Scene scene = new Scene(new ScrollPane(ta), 450, 200);
		
		// Set stage (window) title & scene
		window.setTitle("Word Frequency Analyzer Server");
		window.setScene(scene);
		
		// Show GUI
		window.show();
		
		// Handle close button request. 
		// Launch ConfirmBox to confirm if user wishes to quit
		window.setOnCloseRequest(e -> {
			// Consume the event to allow closeProgram() to do its job
			e.consume();
			closeProgram();
		});
		
		// Create and start an anonymous Thread to handle input from Client
		new Thread( () -> {
			
			// Create ServerSocket
			try (ServerSocket serverSocket = new ServerSocket(8000)){
				
				// Print start time to server TextArea
				Platform.runLater(() -> {
					ta.appendText("Server started at " + new Date() + '\n');
				});

				// Accept connection request from Client
				Socket socket = serverSocket.accept();
				
				// Print start time to server TextArea
				Platform.runLater(() -> {
					ta.appendText("Client Connected" + '\n');
				});

				// Wrap input stream with a buffered reader
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				// Wrap output stream with a print writer
				// true = auto-flush output stream to ensure data is sent
				PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);
				
				// Create String array to hold user input
				userResponses = new String[4];
				
				// Infinite loop to collect input from user
				// Not necessary here, but useful when in enclosing loop is unknown
				while(true) {
					
					for(int i = 0; i < 5; i++) {
						
						// Read and store each response line sent by Client
						String response = fromClient.readLine();

						userResponses[i] = response; 
						
						// Display inputs to Server text area for troubleshooting purposes
						ta.appendText("\n " + i + ": " + response);
						
						// Fourth response should terminate the infinte loop
						if(userResponses[i].equals("quit...")) {
							 ta.appendText("\nuserResponses == quit...");
							 break;
						}
						 
					}
					
					break;
				}
				
				// String array created by WebScrape.parseSite() 
				// which contains every word (and multiples)
				wordsArray = WebScrape.parseSite(userResponses[0], userResponses[1], userResponses[2]);
				
				// Process wordsArray and push to database. 
				// If word exists in DB, increment its frequency
				WebScrape.wordsToDB(wordsArray);
				
				// SELECT * FROM words ORDER BY DESC and return ResultSet
				ResultSet results = Database.getResults();
				
				// Use StringBuilder to Convert ResultSet into sbTen and sbAll Strings
				displayResults(results);
				 
				// Send top ten results back to client
				toClient.println(sbTenString);
				toClient.println("pause...");

				// Send all results back to client
				toClient.println(sbAllString);
				toClient.println("pause...");
				 
			} catch(IOException ex) {
				ta.appendText("Error in Server start(): " + ex.getMessage());
			}
			
		}).start();
		
	}
	
	/** Method to convert printed database contents to topTen and All windows on JavaFX GUI.
	 *  @param rs is the ResultSet returned from Database.getResults() method. */
	private void displayResults(ResultSet rs) {
		try {
			// Build a string of top 10 results to push to Main.fxml GUI
			sbTen = new StringBuilder();
			sbTen.append("\nTop Ten Results\n\n");
			sbTen.append(",");
			
			// Build a string of all results to push to AllResults.fxml GUI
			sbAll = new StringBuilder();
			sbAll.append("\nAll Results\n\n");
			sbAll.append(",");
			
			// Variables for buildString()
			String word = null;
			int frequency = 0;
			int wordCount = 0;
			
			// Scan through result set
			while(rs.next()) {
				word = rs.getString(1);
				frequency = rs.getInt(2);
			
				String line = buildString(word, frequency, wordCount);
			
				// Handle top10 and all results lists
				if (wordCount < 10) {
					sbTen.append(line);
					sbTen.append(",");
					sbAll.append(line);
					sbAll.append(",");
				} else {
					sbAll.append(line);
					sbAll.append(",");
				}
				
				wordCount++;
				
			}
			
			// Save results to String variables which are called from either:
			// MainC-, MainDefaultC-, AllResultsC- or AllResultsDefaultC- ontrollers to push to GUI
			sbTenString = sbTen.toString();
			sbAllString = sbAll.toString();
		
			rs.close();
		} catch (Exception e) {
			System.out.println("Exception in Main.displayResults()" + e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	/** Method to create a string for each word/frequency set in database which uses \t to account for word size 
	 *  and places a blank space before the numbers 1-9 in order to make top10 results more uniform.
	 *  @param word is the word pulled from the database.
	 *  @param frequency is the number of times the word occurred on the parsed site.
	 *  @param count keeps track of the position in the list (which is in descending order by frequency). 
	 *  @return String to append to StringBuilder for top10 and/or all results which is pushed to GUI. */
	private String buildString(String word, int frequency, int count) {
		
		int size = word.length();
		
		// Add a space before numbers 1-9 to make top10 list appear more uniform
		if (count < 9) {
			// An attempt to make the word / frequency printouts more uniform, regardless of word length
			if(size <= 4) {
				return "\n " + (count + 1) + ") Word: " + word + "\t\t\t\tFrequency: " + frequency;
			} else if (size > 4 && size <= 11) {
				return "\n " + (count + 1) + ") Word: " + word + "\t\t\tFrequency: " + frequency;
			} else if (size > 11 && size <= 13){
				return "\n " + (count + 1) + ") Word: " + word + "\t\tFrequency: " + frequency;
			} else {
				return "\n " + (count + 1) + ") Word: " + word + "\tFrequency: " + frequency;
			}
		} else {
			if(size <= 4) {
				return "\n" + (count + 1) + ") Word: " + word + "\t\t\t\tFrequency: " + frequency;
			} else if (size > 4 && size <= 11) {
				return "\n" + (count + 1) + ") Word: " + word + "\t\t\tFrequency: " + frequency;
			} else if (size > 11 && size <= 13){
				return "\n" + (count + 1) + ") Word: " + word + "\t\tFrequency: " + frequency;
			} else {
				return "\n" + (count + 1) + ") Word: " + word + "\tFrequency: " + frequency;
			}
		
		}
		
	}
		
	/** closeProgram() Method uses ConfirmBox class to confirm is user wants to quit */
	protected static void closeProgram() {
       // Ask if user wants to exit (no title necessary, leave blank)
       Boolean answer = ConfirmBox.display("", "Are you sure you want to quit?");
       if (answer) {
           // Run any necessary code before window closes:
		   try {
	    		// Drop and re-create words table
				Database.deleteTable("words");
				Database.createWordsTable("words");
				System.out.println("Window Closed!");
				window.close();
		   } catch (Exception e) {
			   System.out.println(e.getMessage());
			   e.printStackTrace();
		   }
    	   
       }
       
	}
	
}
