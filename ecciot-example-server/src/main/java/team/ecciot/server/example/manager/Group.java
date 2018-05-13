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
    
	public ArrayList<DeviceModel> getDeviceChannelList() {
		synchronized (this) {
			return deviceChannelList;
		}
	}
	
	public ArrayList<TerminalModel> getTerminalChannelList() {
		synchronized (this) {
			return terminalChannelList;
		}
	}

	public DeviceModel removeDeviceById(String itemId){
		for(DeviceModel dm : getDeviceChannelList()){
			if(dm.getItemID().equals(itemId)){
				getDeviceChannelList().remove(dm);
				return dm;
			}
		}
		return null;
	}
	
	public TerminalModel removeTerminalById(String token){
		for(TerminalModel tm : getTerminalChannelList()){
			if(tm.getToken().equals(token)){
				getDeviceChannelList().remove(tm);
				return tm;
			}
		}
		return null;
	}
}
