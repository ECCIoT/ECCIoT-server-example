package team.ecciot.server.example.model;

import com.alibaba.fastjson.JSONObject;

public class TerminalModel extends BaseModel {
	private String token;
	private String platform;
	private String version;
	
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
	@Override
	public void parse(JSONObject content) {
		token = content.getString("token");
		platform = content.getString("platform");
		version = content.getString("version");
	}
	
	
}
