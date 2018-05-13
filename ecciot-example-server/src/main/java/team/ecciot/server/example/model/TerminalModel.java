package team.ecciot.server.example.model;

import com.alibaba.fastjson.JSONObject;

public class TerminalModel extends BaseModel {
	private String token;
	private String platform;
	private String version;
	private boolean state;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	@Override
	public void parse(JSONObject content) {
		token = content.getString("token");
		platform = content.getString("platform");
		version = content.getString("version");
	}
	
	
}
