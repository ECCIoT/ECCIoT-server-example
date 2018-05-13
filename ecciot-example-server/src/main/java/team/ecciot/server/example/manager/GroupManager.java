package team.ecciot.server.example.manager;

import java.util.HashMap;

public class GroupManager {

	private static volatile GroupManager instance = null;

	public static GroupManager getInstance() {
		if (instance == null) {
			synchronized (GroupManager.class) {
				if (instance == null) {
					instance = new GroupManager();
				}
			}
		}
		return instance;
	}

	private GroupManager() {
		/* 测试数据初始化 */
		hmDevice4GroupId.put("device_001", "group_001");
		hmDevice4GroupId.put("device_002", "group_001");
		hmDevice4GroupId.put("device_003", "group_002");
		hmDevice4GroupId.put("device_004", "group_002");
		hmDevice4GroupId.put("device_005", "group_001");
		hmDevice4GroupId.put("device_006", "group_001");
		hmDevice4GroupId.put("device_007", "group_002");

		hmTermianl4GroupId.put("terminal_001", "group_001");
		hmTermianl4GroupId.put("terminal_002", "group_001");
		hmTermianl4GroupId.put("terminal_003", "group_002");
		hmTermianl4GroupId.put("terminal_004", "group_002");
	}

	// groupId, group
	private HashMap<String, Group> hmGroup = new HashMap<String, Group>();

	// itemId, groupId
	private HashMap<String, String> hmDevice4GroupId = new HashMap<String, String>();
	// token, groupId
	private HashMap<String, String> hmTermianl4GroupId = new HashMap<String, String>();

	private HashMap<String, Group> getHmGroup() {
		return hmGroup;
	}

	/**
	 * 通过groupId获取Group
	 * 
	 * @param groupId
	 * @return
	 */
	public Group getGroupById(String groupId) {
		synchronized (hmGroup) {
			if (!hmGroup.containsKey(groupId)) {
				Group group = new Group(groupId);
				hmGroup.put(groupId, group);
				return group;
			}
			return getHmGroup().get(groupId);
		}
	}

	/**
	 * 添加一个Group
	 * 
	 * @param groupId
	 * @param group
	 * @return 若已存在相同groupId，则返回false
	 */
	public boolean addGroup(String groupId, Group group) {
		if (getHmGroup().containsKey(groupId)) {
			return false;
		} else {
			getHmGroup().put(groupId, group);
			return true;
		}
	}

	public HashMap<String, String> getDevice4GroupId() {
		return hmDevice4GroupId;
	}

	public HashMap<String, String> getTermianl4GroupId() {
		return hmTermianl4GroupId;
	}
}