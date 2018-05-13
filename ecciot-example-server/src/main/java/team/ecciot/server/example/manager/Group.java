package team.ecciot.server.example.manager;

import java.util.ArrayList;
import team.ecciot.server.example.model.DeviceModel;
import team.ecciot.server.example.model.TerminalModel;

public class Group {

    private final String groupId;

	private ArrayList<DeviceModel> deviceChannelList;
    private ArrayList<TerminalModel> terminalChannelList;

    public Group(String groupId){
        this.groupId = groupId;
        deviceChannelList=new ArrayList<DeviceModel>();
        terminalChannelList=new ArrayList<TerminalModel>();
    }

    public String getGroupId() {
		return groupId;
	}
    
	public ArrayList<DeviceModel> getDeviceModelList() {
		synchronized (this) {
			return deviceChannelList;
		}
	}
	
	public ArrayList<TerminalModel> getTerminalModelList() {
		synchronized (this) {
			return terminalChannelList;
		}
	}

	public DeviceModel removeDeviceById(String itemId){
		for(DeviceModel dm : getDeviceModelList()){
			if(dm.getItemID().equals(itemId)){
				getDeviceModelList().remove(dm);
				return dm;
			}
		}
		return null;
	}
	
	public TerminalModel removeTerminalById(String token){
		for(TerminalModel tm : getTerminalModelList()){
			if(tm.getToken().equals(token)){
				getDeviceModelList().remove(tm);
				return tm;
			}
		}
		return null;
	}


	public DeviceModel getDeviceById(String itemId){
		for(DeviceModel dm : getDeviceModelList()){
			if(dm.getItemID().equals(itemId)){
				return dm;
			}
		}
		return null;
	}
	
	public TerminalModel getTerminalById(String token){
		for(TerminalModel tm : getTerminalModelList()){
			if(tm.getToken().equals(token)){
				return tm;
			}
		}
		return null;
	}
}
