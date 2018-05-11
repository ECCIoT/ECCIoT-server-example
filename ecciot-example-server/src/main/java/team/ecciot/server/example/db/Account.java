package team.ecciot.server.example.db;

/**
 * Account entity. @author MyEclipse Persistence Tools
 */

public class Account implements java.io.Serializable {

	// Fields

	private Integer accountId;
	private String accountName;
	private String accountPwd;
	private String accountToken;

	// Constructors

	/** default constructor */
	public Account() {
	}

	/** minimal constructor */
	public Account(String accountName, String accountPwd) {
		this.accountName = accountName;
		this.accountPwd = accountPwd;
	}

	/** full constructor */
	public Account(String accountName, String accountPwd, String accountToken) {
		this.accountName = accountName;
		this.accountPwd = accountPwd;
		this.accountToken = accountToken;
	}

	// Property accessors

	public Integer getAccountId() {
		return this.accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountPwd() {
		return this.accountPwd;
	}

	public void setAccountPwd(String accountPwd) {
		this.accountPwd = accountPwd;
	}

	public String getAccountToken() {
		return this.accountToken;
	}

	public void setAccountToken(String accountToken) {
		this.accountToken = accountToken;
	}

}