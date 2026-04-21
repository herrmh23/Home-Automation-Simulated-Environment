package userAuthentication;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import smartHomeProject.SceneManager;

public class RegisterUserController {

    @FXML
    private Label messageLabel;

    @FXML
    private TextField passwordField;

    @FXML
    private Button registerButton;
    
    @FXML
    private Button exitButton;

    @FXML
    private TextField usernameField;
    
    @FXML
    private StackPane rootPane;

    @FXML
    private VBox registerCard;

    @FXML
    private Label titleLabel;

    @FXML
    void handelRegister(ActionEvent event) {
    	
    	String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            if (UserManager.userExists(username)) {
                messageLabel.setText("Username already exists.");
                return;
            }

            boolean created = UserManager.registerUser(username, password);

            if (created) {
                if (Session.isLoggedIn()) {
                    messageLabel.setText("Guest registered successfully.");
                } else {
                    messageLabel.setText("Admin account created successfully.");
                }

                usernameField.clear();
                passwordField.clear();
            } else {
                if (!Session.isLoggedIn()) {
                    messageLabel.setText("Unable to create first account.");
                } else if (!Session.isAdmin()) {
                    messageLabel.setText("Only admins can create users.");
                } else {
                    messageLabel.setText("Could not create user.");
                }
            }

        } catch (IOException e) {
            messageLabel.setText("Error saving user.");
        }
    }
    
    public void initialize() {
    	DarkThemeUtil.applyDarkBackground(rootPane);
        DarkThemeUtil.styleCard(registerCard);
        DarkThemeUtil.styleTitle(titleLabel);
        DarkThemeUtil.styleMessageLabel(messageLabel);

        DarkThemeUtil.styleTextField(usernameField);
        DarkThemeUtil.styleTextField(passwordField);

        DarkThemeUtil.stylePrimaryButton(registerButton);
        DarkThemeUtil.styleSecondaryButton(exitButton);
    	
    	exitButton.setOnAction(e -> {
    		SceneManager.switchScene(e, "mainMenu.fxml");
    	});
    	
    }
   

    }
