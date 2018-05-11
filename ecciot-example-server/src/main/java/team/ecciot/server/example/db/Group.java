package team.ecciot.server.example.db;

/**
 * Group entity. @author MyEclipse Persistence Tools
 */

public class Group implements java.io.Serializable {

	// Fields

	private Integer groupId;
	private String groupName;
	private Integer groupCreaterId;
	private String groupNote;

	// Constructors

	/** default constructor */
	public Group() {
	}

	/** minimal constructor */
	public Group(String groupName, Integer groupCreaterId) {
		this.groupName = groupName;
		this.groupCreaterId = groupCreaterId;
	}

	/** full constructor */
	public Group(String groupName, Integer groupCreaterId, String groupNote) {
		this.groupName = groupName;
		this.groupCreaterId = groupCreaterId;
		this.groupNote = groupNote;
	}

	// Property accessors

	public Integer getGroupId() {
		return this.groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Integer getGroupCreaterId() {
		return this.groupCreaterId;
	}

	public void setGroupCreaterId(Integer groupCreaterId) {
		this.groupCreaterId = groupCreaterId;
	}

	public String getGroupNote() {
		return this.groupNote;
	}

	public void setGroupNote(String groupNote) {
		this.groupNote = groupNote;
	}

}