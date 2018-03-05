package com.ssm.model;

import javax.persistence.Entity;
	    
@Entity
public class LoadInfo {
	//key值  
	private String key;
	//value值  
	private String value; 
	 
    public String getKey() {  
        return key;  
    }  
    public void setKey(String key) {  
        this.key = key;  
    }
    public String getValue() {  
        return value;  
    }  
    public void setValue(String value) {  
        this.value = value;  
    }
}