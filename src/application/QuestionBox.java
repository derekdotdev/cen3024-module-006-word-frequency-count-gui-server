package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** ConfirmBox class utilizes an internal display method to confirm a user action
 * @author Derek DiLeo*/
public class QuestionBox {

    // Define String variable to be returned to caller
    static String userSite;
    static String startAt;
    static String endAt;
    static String[] responses = new String[3];

    /** Method that asks the user a question and returns a String of either their response or, if left
     * blank, a default response which in passed from the caller. 
     * @param title is a String at the top (title) of the pop-up window.
     * @param instruction is the question to ask the user and is displayed via Label class.
     * @param defaultValue is the value to return if user doesn't enter a response.
     * @return String value of the user's response to the question.
     * @author Derek DiLeo */
    public static String[] display(String[] prompts, String[] defaultEntries) {
        System.out.println("QuestionBox.display(3) called!");
    	Stage window = new Stage(); // window is easier to grasp than 'stage'

        // Create StackPanes (two needed for CSS to function correctly)
        StackPane qBoxStackPane1 = new StackPane();
        StackPane qBoxStackPane2 = new StackPane();

        // Create layout and add padding
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10,10,10,10));

        // Block any input events until this window is closed
        window.initModality(Modality.APPLICATION_MODAL);

        // Set window title and dimensions
        window.setTitle(prompts[0]);
        window.setMinWidth(250);
        window.setMinHeight(100);

        // Create a label to display the String message parameter which is passed from caller
        Label header = new Label();
        header.setText(prompts[1]);
        header.setFocusTraversable(true);
        header.setStyle("-fx-font-size: 13pt;");
        //header.setStyle("-fx-font-weight: bold;");
        
        // Create text fields for user input
        Label siteLabel = new Label();
        siteLabel.setId("site-label");
        siteLabel.setText("Website to Parse");
        TextField siteField = new TextField();
        siteField.setPromptText(prompts[2]);
        
        Label startLabel = new Label();
        startLabel.setText("Where should it start?");
        TextField startField = new TextField();
        startField.setPromptText(prompts[3]);
        
        Label endLabel = new Label();
        endLabel.setText("Where to finish.");
        TextField endField = new TextField();
        endField.setPromptText(prompts[4]);

        // Create two buttons
        Button submitButton = new Button("Submit");
        Button testButton = new Button("   ");
        
        // Handle when "Submit" button clicked
        submitButton.setOnAction(e -> {
            responses[0] = siteField.getText();
            responses[1] = startField.getText();
            responses[2] = endField.getText();
            window.close();
        });
        
        // Allow Enter key to trigger "Submit" button
        submitButton.setOnKeyPressed(e -> {
        	if (e.getCode().equals(KeyCode.ENTER)) {
        		submitButton.fire();
            }
        });
        
        // Handle when "test" button clicked
        testButton.setOnAction(e -> {
            responses[0] = defaultEntries[0];
            responses[1] = defaultEntries[1];
            responses[2] = defaultEntries[2];
            window.close();
        });
        
        // Allow Enter key to trigger "submit" button
        submitButton.setOnKeyPressed(e -> {
        	if (e.getCode().equals(KeyCode.ENTER)) {
        		submitButton.fire();
            }
        });
        
        
        // Create an AnchorPane container for buttons at bottom
        AnchorPane buttons = new AnchorPane();
        // Anchor them to either side of the Pane
        AnchorPane.setLeftAnchor(submitButton, 20.0);
        AnchorPane.setRightAnchor(testButton, 20.0);
//        buttons.setPadding(new Insets(10,50,10,50));
//        buttons.setStyle("-fx-hgap: 100;");
        
        // Add all elements to layout, add VBox layout and stackPane2 to stackPane1
        buttons.getChildren().addAll(submitButton, testButton);
        //buttons.setParent(Pos.CENTER);
        layout.getChildren().addAll(header, siteLabel, siteField, startLabel, startField, endLabel, endField, buttons);
        layout.setAlignment(Pos.CENTER_LEFT);
        qBoxStackPane1.getChildren().addAll(qBoxStackPane2, layout);

        // Create and set Scene from stackPane1
        Scene scene = new Scene(qBoxStackPane1);
        window.setScene(scene);

        // Show window and wait for user interaction
        window.showAndWait();
        return responses;
    }
    
    /** Overloaded method that asks the user a question and returns their response in String format
     * and will not throw AlertBox is response is left blank. 
     * @param title is a String at the top (title) of the pop-up window.
     * @param question is the question to ask the user and is displayed via Label class.
     * @return String value of the user's response to the question.
     * @author Derek DiLeo */
//    public static String display(String title, String message) {
//    	System.out.println("QuestionBox.display(2) called!");
//    	Stage window = new Stage(); // window is easier to grasp than 'stage'
//
//        // Create StackPanes (two needed for CSS to function correctly)
//        StackPane stackPane1 = new StackPane();
//        StackPane stackPane2 = new StackPane();
//
//        // Create layout and add padding
//        VBox layout = new VBox(10);
//        layout.setPadding(new Insets(10,10,10,10));
//
//        // Block any input events until this window is closed
//        window.initModality(Modality.APPLICATION_MODAL);
//
//        // Set window title and dimensions
//        window.setTitle(title);
//        window.setMinWidth(250);
//        window.setMinHeight(100);
//
//        // Create a label to display the String message parameter which is passed from caller
//        Label label = new Label();
//        label.setText(message);
//        label.setFocusTraversable(true);
//        
//        // Create a text field for user to input a string
//        TextField field = new TextField();
//        field.setPromptText(promptText);
//        //field.setFocusTraversable(false);
//
//        // Create two buttons and define their behavior
//        Button submitButton = new Button("Submit");
//
//        // Handle when "Submit" button clicked
//        submitButton.setOnAction(e -> {
//        	userSite = field.getText();
//        	if (!userSite.equals("")) {
//        		window.close();
//        	}
//        	if (userSite.equals("")) {
//        		AlertBox.display("Error!", "Please enter a valid response");
//        	}
//        });
//        
//        // Allow Enter key to trigger "Submit" button
//        submitButton.setOnKeyPressed(e -> {
//        	if (e.getCode().equals(KeyCode.ENTER)) {
//        		submitButton.fire();
//            }
//        });
//
//        // Add all elements to VBox layout, center, add VBox layout and stackPane2 to stackPane1
//        layout.getChildren().addAll(label, field, submitButton);
//        layout.setAlignment(Pos.CENTER);
//        stackPane1.getChildren().addAll(stackPane2, layout);
//
//        // Create and set Scene from stackPane1
//        Scene scene = new Scene(stackPane1);
//        window.setScene(scene);
//
//        // Show window and wait for user interaction
//        window.showAndWait();
//        return userSite;
//    }
    
    
    
    
    
}
