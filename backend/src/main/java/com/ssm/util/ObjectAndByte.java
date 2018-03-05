package com.ssm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectAndByte {
	   public static Object ByteToObject(byte[] bytes) {  
		   Object obj = null;  
		   try {  
		       // bytearray to object  
		       ByteArrayInputStream bi = new ByteArrayInputStream(bytes);  
		       ObjectInputStream oi = new ObjectInputStream(bi);  
		     
		       obj = oi.readObject();  
		       bi.close();  
		       oi.close();  
		   } catch (Exception e) {  
		       System.out.println("translation" + e.getMessage());  
		       e.printStackTrace();  
		   }  
		          return obj;  
		      }
	public static byte[] ObjectToByte(java.lang.Object obj) {  
	    byte[] bytes = null;  
	    try {  
	        // object to bytearray  
	        ByteArrayOutputStream bo = new ByteArrayOutputStream();  
	        ObjectOutputStream oo = new ObjectOutputStream(bo);  
	        oo.writeObject(obj);
	        bytes = bo.toByteArray();
	        bo.close();  
	        oo.close();  
	    } catch (Exception e) {  
	        System.out.println("translation" + e.getMessage());  
	        e.printStackTrace();  
	    }  
	    return bytes;  
	}
	public static final InputStream byte2Input(byte[] buf) {  
        return new ByteArrayInputStream(buf);  
    }  
  
    public static final byte[] input2byte(InputStream inStream)  
            throws IOException {  
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] in2b = swapStream.toByteArray();  
        return in2b;  
    }  
}
