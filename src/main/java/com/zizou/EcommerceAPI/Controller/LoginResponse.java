package com.zizou.EcommerceAPI.Controller;

import lombok.Getter;

@Getter
public class LoginResponse {
    private String token;
    private String userName ; 
    private String email ; 
    
    public LoginResponse(String token, String email , String userName) {
    		this.token = token ; 
    		this.email = email ; 
    		this.userName = userName ; 
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
    
}