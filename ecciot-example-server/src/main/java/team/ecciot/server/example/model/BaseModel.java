package team.ecciot.server.example.model;

import com.alibaba.fastjson.JSONObject;

public abstract class BaseModel {
	public abstract void parse(JSONObject content);
}
