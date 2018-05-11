package team.ecciot.server.example.db;

/**
 * Device entity. @author MyEclipse Persistence Tools
 */

public class Device implements java.io.Serializable {

	// Fields

	private Integer deviceId;
	private String deviceItemid;
	private Integer deivceGroup;
	private String deviceVersion;
	private String deviceModel;
	private String deviceName;
	private String deviceData;
	private String deviceNote;

	// Constructors

	/** default constructor */
	public Device() {
	}

	/** minimal constructor */
	public Device(String deviceItemid, Integer deivceGroup) {
		this.deviceItemid = deviceItemid;
		this.deivceGroup = deivceGroup;
	}

	/** full constructor */
	public Device(String deviceItemid, Integer deivceGroup, String deviceVersion, String deviceModel, String deviceName,
			String deviceData, String deviceNote) {
		this.deviceItemid = deviceItemid;
		this.deivceGroup = deivceGroup;
		this.deviceVersion = deviceVersion;
		this.deviceModel = deviceModel;
		this.deviceName = deviceName;
		this.deviceData = deviceData;
		this.deviceNote = deviceNote;
	}

	// Property accessors

	public Integer getDeviceId() {
		return this.deviceId;
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceItemid() {
		return this.deviceItemid;
	}

	public void setDeviceItemid(String deviceItemid) {
		this.deviceItemid = deviceItemid;
	}

	public Integer getDeivceGroup() {
		return this.deivceGroup;
	}

	public void setDeivceGroup(Integer deivceGroup) {
		this.deivceGroup = deivceGroup;
	}

	public String getDeviceVersion() {
		return this.deviceVersion;
	}

	public void setDeviceVersion(String deviceVersion) {
		this.deviceVersion = deviceVersion;
	}

	public String getDeviceModel() {
		return this.deviceModel;
	}

	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}

	public String getDeviceName() {
		return this.deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceData() {
		return this.deviceData;
	}

	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}

	public String getDeviceNote() {
		return this.deviceNote;
	}

	public void setDeviceNote(String deviceNote) {
		this.deviceNote = deviceNote;
	}

}