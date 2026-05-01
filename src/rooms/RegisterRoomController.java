package rooms;

import devices.Devices;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import mainMenu.MainMenuController;
import smartHomeProject.SmartHomeSystem;

public class RegisterRoomController{

	private MainMenuController mainMenuController;

	public void setMainMenuController(MainMenuController controller) {
		this.mainMenuController = controller;
	}

	@FXML
	private VBox registerRoomVBox;

	@FXML
	private Label registerRoomLabel;

	@FXML
	private Label roomNameLabel;

	@FXML
	private TextField roomNameTextField;

	@FXML
	private Label errorLabel;

	@FXML
	private Button createRoomButton;

	@FXML
	private Button cancelRoomButton;

	@FXML
	private ListView<Room> roomListView;

	@FXML
	private Button deleteRoomButton;


	public void initialize() {

		registerRoomLabel.styleProperty().bind(
				Bindings.concat(
						"-fx-text-fill: #F2F2F2; ",
						"-fx-font-family: 'Comic Sans MS'; -fx-font-size: ",
						registerRoomVBox.widthProperty().multiply(0.1))
				);

		roomNameLabel.styleProperty().bind(
				Bindings.concat(
						"-fx-text-fill: #F2F2F2; ",
						"-fx-font-family: 'Comic Sans MS'; -fx-font-size: ",
						registerRoomVBox.widthProperty().multiply(0.05))
				);

		roomNameTextField.prefHeightProperty().bind(
				registerRoomVBox.heightProperty().multiply(0.05)
				);

		createRoomButton.prefHeightProperty().bind(
				registerRoomVBox.heightProperty().multiply(0.05)
				);

		cancelRoomButton.prefHeightProperty().bind(
				registerRoomVBox.heightProperty().multiply(0.05)
				);

		deleteRoomButton.prefHeightProperty().bind(
				registerRoomVBox.heightProperty().multiply(0.05)
				);

		//closes right side pannel
		cancelRoomButton.setOnAction(e -> mainMenuController.cancelAnything());

		//takes roomName from text field and makes it a room
		createRoomButton.setOnAction(e -> {
			String name = roomNameTextField.getText().trim().toUpperCase();
			Boolean exists = false;

			for(Room r: mainMenuController.getSmartHomeSystem().getRooms()) {
				if(r.getName().equalsIgnoreCase(name)) {
					exists = true;
					break;
				}
			}


			if(!exists && !name.isEmpty()) {
				Room room = new Room(name);
				mainMenuController.getSmartHomeSystem().registerRoom(room);
				roomNameTextField.clear();

				errorLabel.setText("Room " + name + " created");
				errorLabel.styleProperty().unbind();
				errorLabel.styleProperty().bind(
				        Bindings.concat(
				                "-fx-text-fill: #67D17A; -fx-font-family: 'Comic Sans MS'; -fx-font-size: ",
				                registerRoomVBox.widthProperty().multiply(0.05))
				);

			} else {
				errorLabel.setText("Room " + name + " already exists");
				errorLabel.styleProperty().unbind();
				errorLabel.styleProperty().bind(
				        Bindings.concat(
				                "-fx-text-fill: #FF6B6B; -fx-font-family: 'Comic Sans MS'; -fx-font-size: ",
				                registerRoomVBox.widthProperty().multiply(0.05))
				);
			}
		} 
				); //close create room button

		deleteRoomButton.setOnAction(e -> {
			Room selected = roomListView.getSelectionModel().getSelectedItem();
			if(selected != null) {
				
				//removes the room's devices from the system
				for(Devices d : selected.getDevices()) {
					d.setOn(false);
					mainMenuController.getHomeViewController().applyLightEffect(d.getNode(), false);
					mainMenuController.getHomeViewController().animateColor(d.getNode(), Color.web("#9e9e9e"));
					mainMenuController.getSmartHomeSystem().getDevices().remove(d);
				}
				
				//remove's the devices from the room
				 selected.getDevices().clear();
				 
				 //removes the room
				mainMenuController.getSmartHomeSystem().getRooms().remove(selected);
			}
			SmartHomeSystem.getInstance().saveCurrentSystemData();
		}
				);


		roomListView.setCellFactory(lv -> new ListCell<>() {
			@Override
			protected void updateItem(Room room, boolean empty) {
				super.updateItem(room, empty); // 🔥 REQUIRED

				if (empty || room == null) {
					setText(null);
				} else {
					if (room.getDevices().size() != 0) {
						StringBuilder deviceNames = new StringBuilder(room.getName() + " [Devices: ");
						String separator = "";

						for (Devices d : room.getDevices()) {
							deviceNames.append(separator).append(d.getName());
							separator = ", ";
						}

						setText(deviceNames.toString() + "]");
					} else {
						setText(room.getName());
					}
				}
			}
		});

		applyDarkTheme();
		//styles scroll bar
				Platform.runLater(() -> {
				    styleScrollBars(roomListView);
				});
	} //close initialize

	public void closeButton() {
		roomNameTextField.clear();
		errorLabel.setText(null);
	}
	
	//erases error message
	public void clearErrorMessage() {
		errorLabel.setText(null);
	}
	
	public void refreshRoomList() {
		roomListView.refresh();
	}


	public void initData() {
		roomListView.setItems(mainMenuController.getSmartHomeSystem().getRooms());
	}
	
	
	//create dark theme
	private void applyDarkTheme() {
	    String panel = "#1E1E1E";
	    String card = "#252525";
	    String border = "#3A3A3A";
	    String text = "#F2F2F2";
	    String selected = "#2F4F6B";

	    registerRoomVBox.setStyle(
	            "-fx-background-color: " + panel + ";" +
	            "-fx-padding: 14;"
	    );

	    // Do NOT style registerRoomLabel or roomNameLabel here
	    // because their styleProperty is already bound above

	    styleInput(roomNameTextField, card, border, text);
	    styleRoomListView(panel, card, border, text, selected);

	    styleDarkButton(createRoomButton, "#3E8E7E", "#FFFFFF");
	    styleDarkButton(deleteRoomButton, "#A94442", "#FFFFFF");
	    styleDarkButton(cancelRoomButton, "#444444", "#F2F2F2");
	}
	
	private void styleRoomListView(String panel, String card, String border, String text, String selected) {
	    roomListView.setStyle(
	            "-fx-control-inner-background: " + panel + ";" +
	            "-fx-background-color: " + panel + ";" +
	            "-fx-border-color: " + border + ";" +
	            "-fx-background-radius: 10;" +
	            "-fx-border-radius: 10;"
	    );

	    roomListView.setCellFactory(lv -> new ListCell<>() {
	        @Override
	        protected void updateItem(Room room, boolean empty) {
	            super.updateItem(room, empty);

	            if (empty || room == null) {
	                setText(null);
	                setStyle("-fx-background-color: transparent; -fx-text-fill: " + text + ";");
	                return;
	            }

	            if (room.getDevices().size() != 0) {
	                StringBuilder deviceNames = new StringBuilder(room.getName() + " [Devices: ");
	                String separator = "";

	                for (Devices d : room.getDevices()) {
	                    deviceNames.append(separator).append(d.getName());
	                    separator = ", ";
	                }

	                setText(deviceNames.toString() + "]");
	            } else {
	                setText(room.getName());
	            }

	            if (isSelected()) {
	                setStyle(
	                        "-fx-background-color: " + selected + ";" +
	                        "-fx-text-fill: " + text + ";" +
	                        "-fx-font-family: 'Comic Sans MS';"
	                );
	            } else {
	                setStyle(
	                        "-fx-background-color: " + card + ";" +
	                        "-fx-text-fill: " + text + ";" +
	                        "-fx-font-family: 'Comic Sans MS';"
	                );
	            }
	        }

	        @Override
	        public void updateSelected(boolean isSelected) {
	            super.updateSelected(isSelected);

	            if (!isEmpty()) {
	                if (isSelected()) {
	                    setStyle(
	                            "-fx-background-color: " + selected + ";" +
	                            "-fx-text-fill: " + text + ";" +
	                            "-fx-font-family: 'Comic Sans MS';"
	                    );
	                } else {
	                    setStyle(
	                            "-fx-background-color: " + card + ";" +
	                            "-fx-text-fill: " + text + ";" +
	                            "-fx-font-family: 'Comic Sans MS';"
	                    );
	                }
	            }
	        }
	    });
	}
	
	private void styleLabel(Label label, String textColor) {
	    if (label.styleProperty().isBound()) return;

	    label.setStyle(
	            "-fx-text-fill: " + textColor + ";" +
	            "-fx-font-family: 'Comic Sans MS';"
	    );
	}
	
	private void styleInput(javafx.scene.control.TextField field, String bg, String border, String text) {
	    field.setStyle(
	            "-fx-background-color: " + bg + ";" +
	            "-fx-text-fill: " + text + ";" +
	            "-fx-prompt-text-fill: #888888;" +
	            "-fx-border-color: " + border + ";" +
	            "-fx-border-radius: 8;" +
	            "-fx-background-radius: 8;" +
	            "-fx-font-family: 'Comic Sans MS';"
	    );
	}
	
	private <T> void styleListView(ListView<T> listView, String bg, String border, String text) {
	    listView.setStyle(
	            "-fx-control-inner-background: " + bg + ";" +
	            "-fx-background-color: " + bg + ";" +
	            "-fx-border-color: " + border + ";" +
	            "-fx-background-radius: 10;" +
	            "-fx-border-radius: 10;" +
	            "-fx-text-fill: " + text + ";"
	    );
	}
	
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



} //end class

