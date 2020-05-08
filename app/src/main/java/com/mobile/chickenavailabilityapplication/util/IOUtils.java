package com.mobile.chickenavailabilityapplication.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Harsha on 05/02/20.
 */

public class IOUtils {

	public static byte[] toByteArrayUsingReader(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuilder resp = new StringBuilder();
		String inputLine = null;
		while ((inputLine = in.readLine()) != null) {
			resp.append(inputLine);
		}
		return resp.toString().getBytes();
	}
	
	public static byte[] toByteArrayUsingOutputStream(InputStream is) throws IOException {
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((bytesRead = is.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		return output.toByteArray();
	}

	public static void toOutputStream(InputStream in, OutputStream out) throws IOException {
		out.write(toByteArrayUsingReader(in));
		out.flush();
	}
	
	public static void toOutputStream(String data, OutputStream out) throws IOException {
		OutputStreamWriter outw = new OutputStreamWriter(out);
		outw.write(data);
		outw.flush();
		outw.close();
	}
}
