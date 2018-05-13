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

    private GroupManager(){}

    //groupId, group
    private HashMap<String,Group> hmGroup = new HashMap<String,Group>();
    
    //itemId, groupId
    private HashMap<String,Group> hmdDevice4GroupId = new HashMap<String,Group>();
    
    private HashMap<String, Group> getHmGroup() {
		return hmGroup;
	}
    
    /**
     * 通过apikey获取ApplicationGroup
     * @param groupId
     * @return
     */
    public Group getGroupById(String groupId){
    	synchronized (hmGroup) {
    		return getHmGroup().get(groupId);
		}
    }
    
    /**
     * 添加一个ApplicationGroup
     * @param groupId
     * @param group
     * @return 若已存在相同apikey，则返回false
     */
    public boolean addGroup(String groupId,Group group){
    	if(getHmGroup().containsKey(groupId)){
    		return false;
    	}else{
    		getHmGroup().put(groupId, group);
    		return true;
    	}
    }

}