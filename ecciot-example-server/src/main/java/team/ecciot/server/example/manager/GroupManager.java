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

    //API_KEY,ApplicationGroup
    private HashMap<String,Group> hmGroup = new HashMap<String,Group>();
    
    private HashMap<String, Group> getHmGroup() {
		return hmGroup;
	}
    
    /**
     * 通过apikey获取ApplicationGroup
     * @param apikey
     * @return
     */
    public Group getGroupByI(String apikey){
    	synchronized (hmGroup) {
    		return getHmGroup().get(apikey);
		}
    }
    
    /**
     * 添加一个ApplicationGroup
     * @param apikey
     * @param group
     * @return 若已存在相同apikey，则返回false
     */
    public boolean addApplicationGroup(String apikey,Group group){
    	if(getHmGroup().containsKey(apikey)){
    		return false;
    	}else{
    		getHmGroup().put(apikey, group);
    		return true;
    	}
    }

}