package application;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controller class for Main.fxml which calls 
 * initialize() prior to launching GUI. 
 * @author Derek DiLeo */
public class MainController implements Initializable {
	
	// Declare local FXML Tags
	@FXML Button showHideButton;
	@FXML Hyperlink hyperlink;
	@FXML ImageView image;
	@FXML Label copyrightLabel;
	@FXML Label labelText;
	@FXML MenuBar menuBar;
	@FXML MenuItem fileCloseButton;
	@FXML MenuItem helpAboutButton;

	// Local Lists and Maps to hold return values from Class methods
	private HashMap<String, Integer> wordFrequencyHashMap;
	private ArrayList<Word> wordsArrayListWords;
	
	// Varibles used to show / hide text on GUI
	//private boolean displayTopTen = false; 
	private StringBuilder sbTen;
	protected static StringBuilder sbAll;
	
	// int value of copyright symbol for GUI footer  
	private int copyrightSymbol = 169;

	/**
	 * Method runs prior to GUI being displayed and handles processing 
	 * of text file created by WebScrapeToFile.createFile(). 
	 * @author Derek DiLeo */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// Copyright symbol for GUI footer
		String s = Character.toString((char) copyrightSymbol);
		copyrightLabel.setText(s);
		
		// Display Raven image
		Image imageFile = new Image("/resources/img/image.png");
		ImageView iv1 = new ImageView();
		iv1.setImage(imageFile);
		
		// HashMap created from text file generated by WebScrapeToFile
		// with Key=word and value=frequency 
		wordFrequencyHashMap = new HashMap<String, Integer>(TextAnalyzer.formatFileToMap());
		
		// Process HashMap<String,Int> and return ArrayList<Word> to be sorted
		wordsArrayListWords = new ArrayList<Word>(WordFrequencyAnalyzer.processHashMap(wordFrequencyHashMap));
		
		// Sort wordsArrayList by frequency
		Collections.sort(wordsArrayListWords);

		// Reverse for highest frequency first
		Collections.reverse(wordsArrayListWords);
		
		// New versions of Java do not add the word "" to the list. However, Java version 1.8 does
		// This "" occurs 73 times and, therefore, appears at the top of the words list.
		// I tried for hours to fix it, but in the end, I had to bandaid with this. 
		wordsArrayListWords.remove(0);
		
		processText();
		
	}

	/**
	 * Method prints all results to console and pushes top ten to GUI.
	 * @author Derek DiLeo */
	private void processText() {

		// Build a string of top 10 results to push to Main.fxml GUI
		sbTen = new StringBuilder();
		sbTen.append("Top Ten Results\n\n");
		
		for (int index = 0; index < 10; index++) {
			sbTen.append(wordsArrayListWords.get(index).toString(index) + "\n");
		}
		
		// Push top 10 results to GUI
		labelText.setText(sbTen.toString());
		labelText.setStyle("-fx-font-alignment: center");
		
		// Boolean variable used as safety for handleShowHideButton() 
		//displayTopTen = true;
		
		
		// Build a string of all results to push to AllResults.fxml GUI
		sbAll = new StringBuilder();
		sbAll.append("All Results\n\n");
		
		// Print all results to console and append all results to sbAll
		System.out.println("\nSorted:");

		for (Word word : wordsArrayListWords) {
			
			// Get value of index location to pass into Word.toString(int index)
			int index = wordsArrayListWords.indexOf(word);
			
			// Print each Word in wordsArrayListWords
			System.out.println(wordsArrayListWords.get(index).toString(index));
			
			// Append word to String Builder to be pushed to GUI (via AllResults.fxml)
			sbAll.append(wordsArrayListWords.get(index).toString(index) + "\n");
			
		}

	}
	
	/**
	 * Method to call Main.closeProgram() when File > Close clicked.
	 * @param event when user clicks File > Close */
	@FXML public void handleFileClose(ActionEvent event) {
		Main.closeProgram();
	}


	/**
	 * Method to launch github repo page on default browser (send to readme file, once typed).
	 * @param event when user clicks Help > About */
	@FXML public void handleHelpAbout(ActionEvent event) {
		try {
			  Desktop desktop = java.awt.Desktop.getDesktop();
			  URI oURL = new URI("https://github.com/derekdileo/cen3024-module-006-word-frequency-count-gui");
			  desktop.browse(oURL);
			} catch (Exception e) {
			  e.printStackTrace();
			}
	}

	/**
	 * Method launches website where poem was sourced.
	 * @param event when user clicks on "Source" hyperlink in footer. */
	@FXML public void handleHyperlink(ActionEvent event) {
		try {
			  Desktop desktop = java.awt.Desktop.getDesktop();
			  URI oURL = new URI(WebScrapeToFile.website);
			  desktop.browse(oURL);
			} catch (Exception e) {
			  e.printStackTrace();
			}
	}

	/**
	 * Method to show / hide text printed to GUI by processText().
	 * @param event when user clicks "Show / Hide Text" button. */
	@FXML public void handleShowHideButton(ActionEvent event) {
		
		try {
			Parent allResultsViewParent = FXMLLoader.load(getClass().getResource("AllResults.fxml"));
			Scene allResultsViewScene = new Scene(allResultsViewParent);
			
			// Get the Stage information
			Stage window = (Stage)(((Node) event.getSource()).getScene().getWindow());
			window.setScene(allResultsViewScene);
			window.show();
		} catch (IOException e) {
			System.out.println("Error switching to AllResults.fxml: IOException: " + e);
			e.printStackTrace();
		}
		
	}
}
