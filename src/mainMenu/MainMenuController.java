package mainMenu;

import devices.Devices;
import devices.RegisterDeviceController;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import rooms.RegisterRoomController;
import rooms.Room;
import rules.CreateRuleController;
import rules.Rule;
import smartHomeProject.SceneManager;
import smartHomeProject.SmartHomeSystem;
import userAuthentication.Session;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class MainMenuController {

	@FXML
	private BorderPane baseLayerBorderPane;

	@FXML
	private Label rulesLabel;

	@FXML
	private VBox ruleVBox;

	@FXML
	private Button createRuleButton;

	@FXML
	private Button registerRoomButton;

	@FXML
	private Button registerDeviceButton;

	@FXML
	private ListView<Rule> ruleListView;

	@FXML
	private VBox ruleBilderVbox;

	@FXML
	private VBox registerRoomVBox;

	@FXML 
	private VBox registerDeviceVBox; 

	@FXML
	private Label registerDeviceLabel; 

	@FXML 
	private StackPane rightSideStackPane;

	//other fxml controllers
	@FXML
	private RegisterRoomController registerRoomController;

	@FXML 
	private HomeViewController homeViewController;

	@FXML
	private RegisterDeviceController registerDeviceController;

	@FXML
	private HBox topBarHBox;

	@FXML
	private HBox userHBox;

	@FXML
	private Label userLabel;

	@FXML
	private HBox timeHBox;

	@FXML
	private Label timeLabel;

	@FXML
	private Button deleteRuleButton;

	@FXML
	private Button registerNewUserButton;

	@FXML
	private HBox logOutHBox;

	@FXML
	private Button logOutButton;

	@FXML
	private Label errorLabel;

	@FXML
	private Button editRuleButton;

	public RegisterRoomController getRegisterRoomController() {
		return registerRoomController;
	}

	public HomeViewController getHomeViewController() {
		return homeViewController;
	}

	@FXML
	private CreateRuleController createRuleController;

	public CreateRuleController getCreateRulesController() {
		return createRuleController;
	}


	private SmartHomeSystem system;

	public void setSystem(SmartHomeSystem system) {
		this.system = system;

		//set the list to display rules
		ruleListView.setItems(system.getRule());


		if (registerRoomController != null) {
			registerRoomController.initData();
		}

		if (registerDeviceController != null) {
			registerDeviceController.initData();
		}

	}

	public SmartHomeSystem getSmartHomeSystem() {
		return system;
	}


	public void initialize() {

		system = SmartHomeSystem.getInstance();

		system.setController(this);     

		registerDeviceController.setMainMenuController(this);
		registerRoomController.setMainMenuController(this);
		homeViewController.setMainMenuController(this);
		createRuleController.setMainMenuController(this);



		//set the list to display rules
		ruleListView.setItems(system.getRule());

		//sizes the top bar
		userHBox.prefWidthProperty().bind(
				topBarHBox.widthProperty().multiply(0.5)
				);

		timeHBox.prefWidthProperty().bind(
				topBarHBox.widthProperty().multiply(0.25)
				);

		logOutHBox.prefWidthProperty().bind(
				topBarHBox.widthProperty().multiply(0.25)
				);


		//make left side 25% of the window width
		ruleVBox.prefWidthProperty().bind(
				baseLayerBorderPane.widthProperty().multiply(0.25)
				); 

		//make right side 25% of the window width
		rightSideStackPane.prefWidthProperty().bind(
				baseLayerBorderPane.widthProperty().multiply(0.25)
				);

		//make labels scale proportionally to window size
		rulesLabel.styleProperty().bind(
				Bindings.concat(
						"-fx-text-fill: #F2F2F2; ",
						"-fx-font-family: 'Comic Sans MS'; ",
						"-fx-font-size: ",
						ruleVBox.widthProperty().multiply(0.1)
						)
				);

		//makes top bar display username and role
		userLabel.setText(Session.getCurrentUser().getUsername() + " - " + Session.getCurrentUser().getRole().toString().toLowerCase() );

		logOutButton.setOnAction(e -> {
			SceneManager.switchScene(e, "../userAuthentication/loginpage.fxml");
			Session.logout();
		});

		//makes the rules look nice
		ruleListView.setCellFactory(lv -> new ListCell<>() {

			private final Label nameLabel = new Label();
			private final Label detailsLabel = new Label();
			private final VBox container = new VBox(nameLabel, detailsLabel);

			{
				// Style the name (big + bold)
				nameLabel.setStyle(
						"-fx-font-family: 'Comic Sans MS'; " +
								"-fx-font-size: 16px; " +
								"-fx-font-weight: bold;"
						);

				// Style the details (smaller + softer color)
				detailsLabel.setStyle(
						"-fx-font-family: 'Comic Sans MS'; " +
								"-fx-font-size: 12px; " +
								"-fx-text-fill: #555555;"
						);

				detailsLabel.setWrapText(true);

				container.setSpacing(4);
				container.setStyle("-fx-padding: 8;");

				setOnMouseEntered(e -> setStyle("-fx-background-color: #f0f0f0;"));
				setOnMouseExited(e -> setStyle(""));
				selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
					if (isNowSelected) {
						setStyle("-fx-background-color: #d6eaff;");
					} else {
						setStyle("");
					}
				});
			}

			@Override
			protected void updateItem(Rule rule, boolean empty) {
				super.updateItem(rule, empty);

				if (empty || rule == null) {
					setText(null);
					setGraphic(null);
				} else {
					nameLabel.setText(rule.getRuleName());

					detailsLabel.setText(
							"Time: " + rule.getFormattedTime() + "\n" +
									"Target: " + rule.getTargetDescription() + "\n" +
									"Action: " + rule.getActionDescription()
							);

					setGraphic(container);
				}
			}
		});


		//make rule creation screen not visible and not managed when not open
		rightSideStackPane.setVisible(false);
		rightSideStackPane.setManaged(false);

		//set rule builder invisible
		ruleBilderVbox.setVisible(false);
		ruleBilderVbox.setManaged(false);

		//set room register invisible
		registerRoomVBox.setVisible(false);
		registerRoomVBox.setManaged(false);

		//set device register invisible
		registerDeviceVBox.setVisible(false);
		registerDeviceVBox.setManaged(false);

		//make errorLabel not visible
		errorLabel.setVisible(false);
		errorLabel.setManaged(false);


		//controls button pressed actions to open designated screen
		createRuleButton.setOnAction(e -> openRuleBuilder());
		registerRoomButton.setOnAction(e -> openRegisterRoom());
		registerDeviceButton.setOnAction(e -> openRegisterDevice());
		editRuleButton.setOnAction(e -> {
			Rule selected = ruleListView.getSelectionModel().getSelectedItem();

			if(selected == null) return;

			openRuleBuilder();
			createRuleController.startEditMode(selected);
		});

		//register new user opens log in page
		registerNewUserButton.setOnAction(e -> {
			SceneManager.switchScene(e, "../userAuthentication/RegisterUser.fxml");
		});

		//makes buttons only accessible by admin
		registerNewUserButton.setVisible(Session.isAdmin());
		registerNewUserButton.setManaged(Session.isAdmin());
		createRuleButton.setVisible(Session.isAdmin());
		createRuleButton.setManaged(Session.isAdmin());
		registerDeviceButton.setVisible(Session.isAdmin());
		registerDeviceButton.setManaged(Session.isAdmin());
		editRuleButton.setVisible(Session.isAdmin());
		editRuleButton.setManaged(Session.isAdmin());
		deleteRuleButton.setVisible(Session.isAdmin());
		deleteRuleButton.setManaged(Session.isAdmin());
		registerRoomButton.setVisible(Session.isAdmin());
		registerRoomButton.setManaged(Session.isAdmin());

		//makes delete rule button work
		deleteRuleButton.setOnAction(e -> {
			Rule selected = ruleListView.getSelectionModel().getSelectedItem();

			if (selected == null) return;

			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.initOwner(deleteRuleButton.getScene().getWindow());
			alert.initModality(Modality.WINDOW_MODAL);
			
			alert.setTitle("Delete Rule");
			alert.setHeaderText("Delete Rule: " + selected.getRuleName());
			alert.setContentText("This action cannot be undone.");

			if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
				errorLabel.setVisible(true);
				errorLabel.setManaged(true);
				errorLabel.setText("Rule \"" + selected.getRuleName() +"\" deleted");
				errorLabel.styleProperty().unbind();
				errorLabel.styleProperty().bind(
						Bindings.concat(
								"-fx-text-fill: #67D17A; -fx-font-family: 'Comic Sans MS'; -fx-font-size: ",
								ruleVBox.widthProperty().multiply(0.05))
						);
				PauseTransition pause = new PauseTransition(Duration.seconds(5));
				pause.setOnFinished(ev -> {
					errorLabel.setVisible(false);
					errorLabel.setManaged(false);
				});
				pause.play();
				system.getRule().remove(selected);
				ruleListView.getSelectionModel().clearSelection();
		        SmartHomeSystem.getInstance().saveCurrentSystemData();
			}
		});

		// Only run after system exists
		if (system != null) {
			registerRoomController.initData();
		}

		//sets dark theme
		applyDarkTheme();
		//styles scroll bar
		Platform.runLater(() -> {
			styleScrollBars(ruleListView);
		});

		//calls on loop for preloaded devices
		homeViewController.setupDeviceNodes(); 
		homeViewController.updateDevice();
	} // close initialize

	//Methods to open the different right side panels

	public boolean rulePanelActive = false;
	public boolean roomPanelActive = false;
	public boolean devicePanelActive = false;

	private void openRuleBuilder() {
		//set right side visible
		rightSideStackPane.setVisible(true);
		rightSideStackPane.setManaged(true);

		//set rule maker visible
		createRuleController.clearErrorMessage();
		ruleBilderVbox.setVisible(true);
		ruleBilderVbox.setManaged(true);
		rulePanelActive = true;

		//set room register invisible
		registerRoomVBox.setVisible(false);
		registerRoomVBox.setManaged(false);
		roomPanelActive = false;

		//set device register invisible
		registerDeviceVBox.setVisible(false);
		registerDeviceVBox.setManaged(false);
		devicePanelActive = false;
	}

	private void openRegisterRoom() {
		//set right side visible
		rightSideStackPane.setVisible(true);
		rightSideStackPane.setManaged(true);

		//set rule maker visible
		ruleBilderVbox.setVisible(false);
		ruleBilderVbox.setManaged(false);
		rulePanelActive = false;

		//set room register visible
		registerRoomController.clearErrorMessage();
		registerRoomVBox.setVisible(true);
		registerRoomVBox.setManaged(true);
		roomPanelActive = true;

		//set device register invisible
		registerDeviceVBox.setVisible(false);
		registerDeviceVBox.setManaged(false);
		devicePanelActive = false;
	}

	private void openRegisterDevice() {
		//set right side visible
		rightSideStackPane.setVisible(true);
		rightSideStackPane.setManaged(true);

		//set rule maker visible
		ruleBilderVbox.setVisible(false);
		ruleBilderVbox.setManaged(false);
		rulePanelActive = false;

		//set room register invisible
		registerRoomVBox.setVisible(false);
		registerRoomVBox.setManaged(false);
		roomPanelActive = false;

		//set device register invisible
		registerDeviceController.clearErrorMessage();
		registerDeviceController.initData();
		registerDeviceVBox.setVisible(true);
		registerDeviceVBox.setManaged(true);
		devicePanelActive = true;
	}

	public void cancelAnything() {
		//reset fields in the active panel
		if(devicePanelActive) registerDeviceController.closeButton();
		else if (roomPanelActive) registerRoomController.closeButton();
		else if(rulePanelActive) createRuleController.closeButton();


		//set right side visible
		rightSideStackPane.setVisible(false);
		rightSideStackPane.setManaged(false);

		//set rule maker visible
		ruleBilderVbox.setVisible(false);
		ruleBilderVbox.setManaged(false);
		rulePanelActive = false;

		//set room register invisible
		registerRoomVBox.setVisible(false);
		registerRoomVBox.setManaged(false);
		roomPanelActive = false;

		//set device register invisible
		registerDeviceVBox.setVisible(false);
		registerDeviceVBox.setManaged(false);
		devicePanelActive = false;
	}

	//getters for the rooms and devices observable lists. Used to show lists in the create rule panel
	public ObservableList<Room> getRooms() {
		return system.getRooms();
	}

	public ObservableList<Devices> getDevices() {
		return system.getDevices();
	}


	public void setSelectedDeviceID(String id) {
		if(registerDeviceController != null) registerDeviceController.setDeviceID(id);
	}

	//makes clock work
	public void updateTimeLabel(int hour, int minute) {
		if(hour == 0) timeLabel.setText(String.format("12:%02d am", minute));
		else if(hour <= 12) timeLabel.setText(String.format("%02d:%02d am", hour, minute));
		else {
			hour -= 12;
			timeLabel.setText(String.format("%02d:%02d pm", hour, minute));
		}
	}

	public void refreshUI() {
		homeViewController.updateDevice();
	}

	//creates the dark theme
	private void applyDarkTheme() {
		String bgMain = "#121212";
		String bgPanel = "#1E1E1E";
		String bgCard = "#252525";
		String bgSelected = "#2F4F6B";
		String border = "#3A3A3A";
		String textMain = "#F2F2F2";
		String textMuted = "#B8B8B8";
		String accent = "#5AA9FF";

		baseLayerBorderPane.setStyle(
				"-fx-background-color: " + bgMain + ";"
				);

		ruleVBox.setStyle(
				"-fx-background-color: " + bgPanel + ";" +
						"-fx-border-color: " + border + ";" +
						"-fx-border-width: 0 1 0 0;" +
						"-fx-padding: 12;"
				);

		rightSideStackPane.setStyle(
				"-fx-background-color: " + bgPanel + ";" +
						"-fx-border-color: " + border + ";" +
						"-fx-border-width: 0 0 0 1;"
				);

		topBarHBox.setStyle(
				"-fx-background-color: " + bgPanel + ";" +
						"-fx-border-color: " + border + ";" +
						"-fx-border-width: 0 0 1 0;" +
						"-fx-padding: 10 14 10 14;"
				);

		// Do NOT setStyle on rulesLabel — it is bound

		userLabel.setStyle(
				"-fx-text-fill: " + textMain + ";" +
						"-fx-font-family: 'Comic Sans MS';" +
						"-fx-font-size: 16px;"
				);

		timeLabel.setStyle(
				"-fx-text-fill: " + accent + ";" +
						"-fx-font-family: 'Comic Sans MS';" +
						"-fx-font-size: 16px;" +
						"-fx-font-weight: bold;"
				);

		styleDarkButton(createRuleButton, accent, "#FFFFFF");
		styleDarkButton(editRuleButton, "#F17FCC", "#FFFFFF");
		styleDarkButton(registerRoomButton, "#3E8E7E", "#FFFFFF");
		styleDarkButton(registerDeviceButton, "#8C64D8", "#FFFFFF");
		styleDarkButton(deleteRuleButton, "#A94442", "#FFFFFF");

		if (logOutButton != null) {
			styleDarkButton(logOutButton, "#444444", textMain);
		}

		if (registerNewUserButton != null) {
			styleDarkButton(registerNewUserButton, "#444444", textMain);
		}

		if (!errorLabel.styleProperty().isBound()) {
			errorLabel.setStyle(
					"-fx-text-fill: #67D17A;" +
							"-fx-font-family: 'Comic Sans MS';" +
							"-fx-font-size: 14px;"
					);
		}

		styleRuleListView(bgPanel, bgCard, bgSelected, border, textMain, textMuted, accent);
	}

	private void styleRuleListView(String bgPanel, String bgCard, String bgSelected,
			String border, String textMain, String textMuted, String accent) {
		ruleListView.setStyle(
				"-fx-control-inner-background: " + bgPanel + ";" +
						"-fx-background-color: " + bgPanel + ";" +
						"-fx-border-color: " + border + ";" +
						"-fx-border-radius: 10;" +
						"-fx-background-radius: 10;"
				);

		ruleListView.setCellFactory(lv -> new ListCell<>() {

			private final Label nameLabel = new Label();
			private final Label detailsLabel = new Label();
			private final VBox container = new VBox(nameLabel, detailsLabel);

			{
				nameLabel.setStyle(
						"-fx-text-fill: " + textMain + ";" +
								"-fx-font-family: 'Comic Sans MS';" +
								"-fx-font-size: 16px;" +
								"-fx-font-weight: bold;"
						);

				detailsLabel.setStyle(
						"-fx-text-fill: " + textMuted + ";" +
								"-fx-font-family: 'Comic Sans MS';" +
								"-fx-font-size: 12px;"
						);

				detailsLabel.setWrapText(true);
				container.setSpacing(4);
				setStyle("-fx-background-color: transparent; -fx-padding: 6;");
			}

			@Override
			protected void updateItem(Rule rule, boolean empty) {
				super.updateItem(rule, empty);

				if (empty || rule == null) {
					setText(null);
					setGraphic(null);
					return;
				}

				nameLabel.setText(rule.getRuleName());
				detailsLabel.setText(
						"Time: " + rule.getFormattedTime() + "\n" +
								"Target: " + rule.getTargetDescription() + "\n" +
								"Action: " + rule.getActionDescription()
						);

				if (isSelected()) {
					container.setStyle(
							"-fx-padding: 10;" +
									"-fx-background-color: " + bgSelected + ";" +
									"-fx-background-radius: 10;" +
									"-fx-border-color: " + accent + ";" +
									"-fx-border-radius: 10;"
							);
				} else {
					container.setStyle(
							"-fx-padding: 10;" +
									"-fx-background-color: " + bgCard + ";" +
									"-fx-background-radius: 10;" +
									"-fx-border-color: " + border + ";" +
									"-fx-border-radius: 10;"
							);
				}

				setGraphic(container);
			}

			@Override
			public void updateSelected(boolean selected) {
				super.updateSelected(selected);

				if (getItem() != null) {
					if (selected) {
						container.setStyle(
								"-fx-padding: 10;" +
										"-fx-background-color: " + bgSelected + ";" +
										"-fx-background-radius: 10;" +
										"-fx-border-color: " + accent + ";" +
										"-fx-border-radius: 10;"
								);
					} else {
						container.setStyle(
								"-fx-padding: 10;" +
										"-fx-background-color: " + bgCard + ";" +
										"-fx-background-radius: 10;" +
										"-fx-border-color: " + border + ";" +
										"-fx-border-radius: 10;"
								);
					}
				}
			}
		});
	}

	//dark theme helper
	private void styleDarkButton(Button button, String bgColor, String textColor) {
		String normalStyle =
				"-fx-background-color: " + bgColor + ";" +
						"-fx-text-fill: " + textColor + ";" +
						"-fx-font-family: 'Comic Sans MS';" +
						"-fx-font-size: 14px;" +
						"-fx-font-weight: bold;" +
						"-fx-background-radius: 10;" +
						"-fx-border-radius: 10;" +
						"-fx-cursor: hand;" +
						"-fx-padding: 8 12 8 12;";

		String hoverStyle =
				"-fx-background-color: derive(" + bgColor + ", 15%);" +
						"-fx-text-fill: " + textColor + ";" +
						"-fx-font-family: 'Comic Sans MS';" +
						"-fx-font-size: 14px;" +
						"-fx-font-weight: bold;" +
						"-fx-background-radius: 10;" +
						"-fx-border-radius: 10;" +
						"-fx-cursor: hand;" +
						"-fx-padding: 8 12 8 12;";

		button.setStyle(normalStyle);

		button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
		button.setOnMouseExited(e -> button.setStyle(normalStyle));
	}

	private void styleScrollBars(Node node) {
		node.lookupAll(".scroll-bar").forEach(scroll -> {
			scroll.setStyle(
					"-fx-background-color: transparent;"
					);
		});

		node.lookupAll(".scroll-bar .track").forEach(track -> {
			track.setStyle(
					"-fx-background-color: #1E1E1E;" +
							"-fx-background-radius: 8;"
					);
		});

		node.lookupAll(".scroll-bar .thumb").forEach(thumb -> {
			thumb.setStyle(
					"-fx-background-color: #3A3A3A;" +
							"-fx-background-radius: 8;"
					);
		});

		node.lookupAll(".scroll-bar .thumb:hover").forEach(thumb -> {
			thumb.setStyle(
					"-fx-background-color: #5A5A5A;" +
							"-fx-background-radius: 8;"
					);
		});
	}

}
