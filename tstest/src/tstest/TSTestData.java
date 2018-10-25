package tstest;

import java.util.LinkedHashMap;
import java.util.Map;

public class TSTestData {
	
	private String login;
	private String password;
	private String peer;
	private String server;
	
	private String service;
	private String caller;
	private String callee;
	
	public TSTestData(final String login, final String password, final String peer, final String service, 
			final String caller, final String callee) {
		super();
		this.login = login;
		this.password = password;
		this.peer = peer;
		this.service = service;
		this.caller = caller;
		this.callee = callee;
	}

	public TSTestData() {
		
	}

	public String getCallee() {
		return callee;
	}

	public void setCallee(String callee) {
		this.callee = callee;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPeer() {
		return peer;
	}

	public void setPeer(String peer) {
		this.peer = peer;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	
	public String getServer() {
		return server;
	}
	
	public void setServer(String server) {
		this.server = server;
	}
	public Map<String, String> errors(){
		Map<String, String> errors = new LinkedHashMap<String, String>();
		if (isNull(login)){
			errors.put("login", "Please enter a valid user name");
		}
		if (isNull(password)){
			errors.put("password", "Please enter a password");
		}
		if (isNull(peer)){
			errors.put("peer", "Please enter a peer to use");
		}
		if (isNull(server)){
			errors.put("server", "Please enter a server to use");
		}
		if (isNull(service)){
			errors.put("service", "Please enter a service to use");
		}
		if (isNull(caller)){
			errors.put("caller", "Please enter a caller");
		}
		if (isNull(callee)){
			errors.put("callee", "Please enter a callee");
		}
		return errors;
	}

	public String error(String key){
		return errors().get(key);
	}
	
	private boolean isNull(String test) {
		return (test==null || "".equals(test));
	}
}
