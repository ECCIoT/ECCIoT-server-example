package team.ecciot.server.example.model;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class DeviceModel extends BaseModel {
	private String itemID;
	private String model;
	private String version;
	//服务端缓存的设备状态
	private JSONObject data;
	//当前是否在线
	private boolean state;	
	
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public JSONObject getData() {
		return data;
	}
	public void setData(JSONObject data) {
		this.data = data;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
		//设备被设置为下线状态时保存设备信息到数据库
		if(!state){
			//save data
		}
	}
	@Override
	public void parse(JSONObject content) {
		itemID = content.getString("itemID");
		model = content.getString("model");
		version = content.getString("version");
	}
	public void updateData(JSONObject json){
		for (Map.Entry<String, Object> entry : json.entrySet()) {
			data.put(entry.getKey(),entry.getValue());
        }
	}
	
}
