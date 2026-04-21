package userAuthentication;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import smartHomeProject.SceneManager;
import smartHomeProject.SmartHomeSystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import javafx.scene.control.PasswordField;

public class LoginController {
	
	@FXML
	private StackPane rootPane;

	@FXML
	private VBox loginCard;

	@FXML
	private Label titleLabel;

	@FXML
	private TextField usernameField;

	@FXML
	private TextField passwordField;

	@FXML
	private Button loginButton;

	@FXML
	private Button registerButton;

	@FXML
	private Label messageLabel;

    @FXML
    void handelLogin(ActionEvent event) {
    	
    	 String username = usernameField.getText();
         String password = passwordField.getText();

         try {
             User user = UserManager.authenticate(username, password);

             if (user != null) {
                 Session.login(user);
                 messageLabel.setText("Login successful!");
                 SceneManager.switchScene(event, "../mainMenu/mainMenu.fxml");
             } else {
                 messageLabel.setText("Invalid username or password.");
             }

         } catch (IOException e) {
             messageLabel.setText("Error reading user data.");
         }
     }
      

    @FXML
    void handelRegister(ActionEvent event) {

         try {
            if (!UserManager.adminExists()) {
        		SceneManager.switchScene(event, "usercreation.fxml");
        		} else {
        		messageLabel.setText("Registration is disabled. Only admins can create users.");
        		}
        	} catch (IOException e) {
        		messageLabel.setText("Error checking registration status.");
        		}
        	}
     }
    
    @FXML
    public void initialize() {
    	 	DarkThemeUtil.applyDarkBackground(rootPane);
    	    DarkThemeUtil.styleCard(loginCard);
    	    DarkThemeUtil.styleTitle(titleLabel);
    	    DarkThemeUtil.styleMessageLabel(messageLabel);

    	    DarkThemeUtil.styleTextField(usernameField);
    	    DarkThemeUtil.styleTextField(passwordField);

    	    DarkThemeUtil.stylePrimaryButton(loginButton);
    	    DarkThemeUtil.styleSecondaryButton(registerButton);

		try {
    	registerButton.setVisible(!UserManager.adminExists());
    } catch (IOException e) {
    		messageLabel.setText("Error loading page.");
    	}
		
		
    }
    
  
    }
