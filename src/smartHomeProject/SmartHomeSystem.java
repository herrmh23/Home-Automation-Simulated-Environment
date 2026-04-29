package smartHomeProject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import devices.DeviceData;
import devices.Devices;
import devices.HVAC;
import devices.Lights;
import devices.Lock;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import mainMenu.MainMenuController;
import rooms.Room;
import rooms.RoomData;
import rules.Action;
import rules.ActionData;
import rules.BinaryAction;
import rules.HVACAction;
import rules.Rule;
import rules.RuleData;
import userAuthentication.Session;

public class SmartHomeSystem {
	//private ArrayList<Devices> deviceList; //storage list for all devices
	private ObservableList<Devices> devices = FXCollections.observableArrayList();
	private ObservableList<Rule> rules = FXCollections.observableArrayList();
	private ObservableList<Room> rooms = FXCollections.observableArrayList();
	//private ArrayList<Room> roomList;
	private int simulatedTime = 0;

//	public SmartHomeSystem() {
//		System.out.println("NEW SYSTEM CREATED: " + this);
//		 //Thread.dumpStack(); for debugging
//	} //for testing purposes
	
	//for creating smart home system instances to connect to controllers
	private static SmartHomeSystem instance;

	public static SmartHomeSystem getInstance() {
	    if (instance == null) {
	        instance = new SmartHomeSystem();
	    }
	    return instance;
	}

	//connects main menu controller to smart home system
	private MainMenuController controller;

	public void setController(MainMenuController controller) {
		this.controller = controller;
		System.out.println("Controller set for: " + this);

	}

	public ObservableList<Room> getRooms() {
		return rooms;
	}

	public void registerRoom(Room room) {
		rooms.add(room);
		saveCurrentSystemData();
	}

	public void registerDevice(Devices device) { //adds new device to the deviceList array
		devices.add(device);
		
		 if (controller != null) {
		        Node node = controller.getHomeViewController().getNodeFromMap(device.getDeviceID());

		        device.setNode(node);

		        controller.refreshUI();
		    }
		 saveCurrentSystemData();
	}

	public ObservableList<Devices> getDevices() { //returns the deviceList array
		return devices;
	}

	public void addRule(Rule rule) {
		rules.add(rule);
		 FXCollections.sort(rules, (r1, r2) ->
        Integer.compare(r1.getStartTime(), r2.getStartTime())
    );
		saveCurrentSystemData();
	}

	public ObservableList<Rule> getRule() {
		return rules;
	}



	// executes the rule against current time
	public void executeRules(int simulatedTime) {

		List<Rule> activeRules = new ArrayList<>();

		for (Rule rule : rules) {

			//reset executed status once time has passed
			if(simulatedTime != rule.getStartTime()) rule.resetExecuted();

			if (simulatedTime == rule.getStartTime() && !rule.getHasExecuted()) {
				System.out.println(rule);
				activeRules.add(rule);
				rule.setAsExecuted();
			}
		}

		if(!activeRules.isEmpty()) {
			Platform.runLater(() -> applyRulesWithPriority(activeRules));
		}
	}

	//method that handles sorting rules by priority
	public void applyRulesWithPriority(List<Rule> rules) {

		Map<Devices, Action> finalActions = new HashMap<>();
		Map<Devices, Integer> devicePriority = new HashMap<>();
		Map<Devices, Rule> winningRules = new HashMap<>();

		for (Rule rule : rules) {

			List<Devices> affectedDevices = rule.getAffectedDevices(this);

			for (Devices device : affectedDevices) {
				
				
				for (Action action : rule.getActions()) {

				    if (!action.supports(device)) {
				        continue;
				    }

				    int currentPriority = rule.getPriority();
				    Integer existingPriority = devicePriority.get(device);

				    if (existingPriority == null) {

				        System.out.println("[NEW] " + rule.getRuleName() +
				                " sets " + device.getName());

				        Action newAction = null;

				        if (action instanceof BinaryAction binary) {
				            newAction = new BinaryAction(binary.isTurnOn(), binary.getTargetClass());
				        }

				        if (action instanceof HVACAction hvacAction) {
				            newAction = new HVACAction(
				                hvacAction.getMode(),
				                hvacAction.getTemp(),
				                hvacAction.isTurnOn()
				            );
				        }

				        finalActions.put(device, newAction);
				        devicePriority.put(device, currentPriority);
				        winningRules.put(device, rule);

				    } else if (currentPriority > existingPriority) {

				        System.out.println("[OVERRIDE - higher priority] " +
				                rule.getRuleName() + " overrides " +
				                winningRules.get(device).getRuleName() +
				                " for " + device.getName());

				        Action newAction = null;

				        if (action instanceof BinaryAction binary) {
				            newAction = new BinaryAction(binary.isTurnOn(), binary.getTargetClass());
				        }

				        if (action instanceof HVACAction hvacAction) {
				            newAction = new HVACAction(
				                hvacAction.getMode(),
				                hvacAction.getTemp(),
				                hvacAction.isTurnOn()
				            );
				        }

				        finalActions.put(device, newAction);
				        devicePriority.put(device, currentPriority);
				        winningRules.put(device, rule);

				    } else if (currentPriority == existingPriority) {

				        System.out.println("[OVERRIDE - same priority] " +
				                rule.getRuleName() + " overrides " +
				                winningRules.get(device).getRuleName() +
				                " for " + device.getName());

				        Action newAction = null;

				        if (action instanceof BinaryAction binary) {
				            newAction = new BinaryAction(binary.isTurnOn(), binary.getTargetClass());
				        }

				        if (action instanceof HVACAction hvacAction) {
				            newAction = new HVACAction(
				                hvacAction.getMode(),
				                hvacAction.getTemp(),
				                hvacAction.isTurnOn()
				            );
				        }

				        finalActions.put(device, newAction);
				        winningRules.put(device, rule);
				    } else {

				        System.out.println("[IGNORED] " +
				                rule.getRuleName() + " lost to " +
				                winningRules.get(device).getRuleName() +
				                " for " + device.getName());
				    }
				}
				


			}
		}

		//Final result summary
		System.out.println("\n=== FINAL ACTIONS ===");
		for (Map.Entry<Devices, Action> entry : finalActions.entrySet()) {
			Devices device = entry.getKey();
			Rule winner = winningRules.get(device);

			System.out.println(device.getName() +
					" controlled by " + winner.getRuleName());
		}


		// Execute final resolved actions
//		for (Action action : finalActions.values()) {
//			action.execute();
//		}
		for (Map.Entry<Devices, Action> entry : finalActions.entrySet()) {
		    Devices device = entry.getKey();
		    Action action = entry.getValue();

		    action.apply(device);
		}
		
		saveCurrentSystemData();
		
		if(controller != null) {
			controller.refreshUI();
		}
	}

	public List<String> checkConflicts(Rule newRule) {
		List<String> conflicts = new ArrayList<>();

		for(Rule existingRule : rules) {

			//checks rules with same start time
			if (existingRule.getStartTime() != newRule.getStartTime()) continue;

			//gets devices from both rules
			List<Devices> newDevices = newRule.getAffectedDevices(this);
			List<Devices> existingDevices = existingRule.getAffectedDevices(this);

			//checks if the new rule shared a device with the old rule
			for (Devices device : newDevices) {
				if (existingDevices.contains(device)) {

					//checks if the new rule conflicts actions with the old rule
					for (Action newAction : newRule.getActions()) {
						for (Action existingAction : existingRule.getActions()) {

							//creates error message
							if (actionsConflict(newAction, existingAction)) {
								conflicts.add(
										"Conflict with \"" + existingRule.getRuleName() +
										"\" for " + device.getName()
										);
							}
						}
					}
				}
			}
		}
		return conflicts;
	}

	//used to check if the actions conflict before alerting user
	private boolean actionsConflict(Action a1, Action a2) {

		//compares binary actions of two rules
		if (a1 instanceof BinaryAction b1 && a2 instanceof BinaryAction b2) {
			return b1.isTurnOn() != b2.isTurnOn();
		}

		//compares actions of two hvac rules
		if (a1 instanceof HVACAction h1 && a2 instanceof HVACAction h2) {
			return h1.isTurnOn() != h2.isTurnOn()
					|| h1.getTemp() != h2.getTemp()
					|| h1.getMode() != h2.getMode();
		}

		return false;
	}





	//timer (turns 1 real minute into 1 virtual, and 1 real minute into 1 virtual hour, so full virtual days are 24 real minutes)
	public void startSimulation() {

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			int hour;
			int minute;

			@Override
			public void run() {

				simulatedTime++; //increased the time by one minute

				if(simulatedTime >= 1440) simulatedTime = 0; //reset the time after 24 minutes

				executeRules(simulatedTime); //runs executeRules, which checks if it is time to run commands

				//prints simulated time to console every real minute
				hour = simulatedTime / 60;
				minute = simulatedTime % 60;
				String timeString = String.format("%02d:%02d", hour, minute);
				if(simulatedTime % 60 == 0) System.out.println(timeString);

				Platform.runLater(() -> {
					if (controller != null) {
						controller.updateTimeLabel(hour, minute);
					}
				});


			}

		}, 0, 1000); //every second

	}
	
	public int getSimulatedTime() {
		return simulatedTime;
	}
	
	public void setSimulatedTime(int hours, int minutes) {
		this.simulatedTime = hours * 60 + minutes;
	}
	
	
	//methods to save user data –––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
	
	private boolean isLoadingData = false;
	
	//save user data
	public SystemData toSystemHomeData() {
		SystemData data = new SystemData();
		
		//saves all base info for devices
		for(Devices d : devices) {
			DeviceData dd = new DeviceData();
			dd.setType(d.getClass().getSimpleName());
			dd.setName(d.getName());
			dd.setDeviceID(d.getDeviceID());
			dd.setOn(d.isOn());
			
			//saves hvac special data
			if(d instanceof HVAC hvac) {
				dd.setHvacMode(hvac.getMode().name());
				dd.setHvacTemp(hvac.getTemp());
			}
			
			//add device to stored user data
			data.getDevices().add(dd);
		}
		
		//saves room data
		for(Room room : rooms) {
			RoomData rd = new RoomData();
			rd.setRoomName(room.getName());
			
			//adds device id to room device list
			for(Devices d : room.getDevices()) {
				rd.getDeviceIDs().add(d.getDeviceID());
			}
			
			//add room to stored user data
			data.getRooms().add(rd);
		}
		
		//saves rule info
		for(Rule rule : rules) {
			RuleData rd = new RuleData();
			rd.setRuleName(rule.getRuleName());
			rd.setStartHour(rule.getHours());
			rd.setStartMinute(rule.getMinutes());
			rd.setTargetType(rule.getTargetType().name());
			
			//saves target rooms
			for(Room room : rule.getTargetRooms()) {
				rd.getTargetRoomNames().add(room.getName());
			}
			
			//saves target devices
			for(Devices d : rule.getTargetDevices()) {
				rd.getTargetDeviceIDs().add(d.getDeviceID());
			}
			
			//saves rule actions
			for(Action action : rule.getActions()) {
				ActionData ad = new ActionData();
				
				if(action instanceof BinaryAction binary) {
					ad.setActionType("BINARY");
					ad.setTurnOn(binary.isTurnOn());
					ad.setTargetClassName(binary.getTargetClass().getSimpleName());
				} else if(action instanceof HVACAction hvacAction) {
					ad.setActionType("HVAC");
					ad.setTurnOn(hvacAction.isTurnOn());
					ad.setHvacMode(hvacAction.getMode().name());
					ad.setHvacTemp(hvacAction.getTemp());
				}
				
				//add action to stored rule data
				rd.getActions().add(ad);
			}
			
			//add rule to stored user data
			data.getRules().add(rd);
		}
		
		//saves system time
		data.setHours(simulatedTime);
		data.setMinutes(simulatedTime);
		
		return data;
	}
	
	//method to load user data when user logs back in
	public void loadFromSystemHomeData(SystemData data) {
		isLoadingData = true;
		
		try {
		devices.clear();
		rooms.clear();
		rules.clear();
		
		Map<String, Devices> deviceMap = new HashMap<>();
		Map<String, Room> roomMap = new HashMap<>();
		
		//gets devices and info from user data
		for(DeviceData dd : data.getDevices()) {
			Devices device = null;
			
			switch(dd.getType()) {
			case "Lights":
				device = new Lights(dd.getName(), dd.getDeviceID());
				break;
			case "Lock":
				device = new Lock(dd.getName(), dd.getDeviceID());
				break;
			case "HVAC":
				HVAC hvac = new HVAC(dd.getName(), dd.getDeviceID());
				if(dd.getHvacMode() != null) {
					hvac.setMode(HVAC.Mode.valueOf(dd.getHvacMode()));
				}
				hvac.setTemp(dd.getHvacTemp());
				device = hvac;
				break;
			}
			
			//registers device, sets to previous state, adds to map
			if(device != null) {
				registerDevice(device);
				deviceMap.put(device.getDeviceID(), device);
				device.setOn(dd.isOn());
			}
		}
		
		//gets rooms from user data
		for(RoomData rd : data.getRooms()) {
			Room room = new Room(rd.getRoomName());
			
			//adds devices back to their rooms
			for(String deviceID : rd.getDeviceIDs()) {
				Devices d = deviceMap.get(deviceID);
				if(d != null) {
					room.addDevice(d);
				}
			}
			
			//registers room and adds to map
			registerRoom(room);
			roomMap.put(room.getName(), room);
		}
		
		//gets rule data from user data
		for(RuleData rd : data.getRules()) {
			Rule rule = new Rule(rd.getRuleName(), rd.getStartHour(), rd.getStartMinute());
			rule.setTargetType(Rule.TargetType.valueOf(rd.getTargetType()));
			
			//for room target rules
			for(String roomName : rd.getTargetRoomNames()) {
				Room room = roomMap.get(roomName);
				if(room != null) {
					rule.addTargetRoom(room);
				}
			}
			
			//for device target rules
			for(String deviceID : rd.getTargetDeviceIDs()) {
				Devices d = deviceMap.get(deviceID);
				if(d != null) {
					rule.addTargetDevice(d);
				}
			}
			
			//get actions
			for(ActionData ad : rd.getActions()) {
				if("BINARY".equals(ad.getActionType())) {
					Class<? extends Devices> targetClass = null;
					
					if("Lights".equals(ad.getTargetClassName())) {
						targetClass = Lights.class;
					} else if("Lock".equals(ad.getTargetClassName())) {
						targetClass = Lock.class;
					}
					
					if(targetClass != null) {
						rule.addAction(new BinaryAction(ad.isTurnOn(), targetClass));
						
					}
				} else if("HVAC".equals(ad.getActionType())) {
					HVAC.Mode mode = HVAC.Mode.valueOf(ad.getHvacMode());
					rule.addAction(new HVACAction(mode, ad.getHvacTemp(), ad.isTurnOn()));
				}
			}
		
			//creates rule
			addRule(rule);
		}
		
		//load time: returns to last used time
		int hours = data.getHours();
		int minutes = data.getMinutes();
		setSimulatedTime(hours, minutes);
		
		
		
		
		} finally {
		        isLoadingData = false;
		}
	}
	
	//associated current data to current user and saves it
	public void saveCurrentSystemData() {
		 if (isLoadingData /*|| !Session.isLoggedIn()*/) return;  // might need to uncomment this part ******%*#*%*#%*WQ*^**^
		
		try {
			SystemData data = toSystemHomeData();
			SystemDataManager.save("shared_home", data);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadCurrentSystemData() {
		try {
			SystemData data = SystemDataManager.load("shared_home");
			loadFromSystemHomeData(data);
		} catch(Exception e) {
			System.out.println("Save file corrupted, loading empty system...");
		    loadFromSystemHomeData(new SystemData());
		}
	}

	
}
