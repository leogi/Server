package Server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class DataTransfer implements Runnable{
	BufferedReader reader = null;
	PrintWriter writer = null;
    private Socket socket;
    public DataTransfer(Socket socket) {
		// TODO Auto-generated constructor stub
    	this.socket = socket;
    	//System.out.println("Accept new connection..."+socket.getPort());
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		InputStream input = null;
		OutputStream output = null;
		
		try {
			input = socket.getInputStream();
			output = socket.getOutputStream();
			reader = new BufferedReader(new InputStreamReader(input));
			writer = new PrintWriter(output,true);
			
			String line = reader.readLine();
			System.out.println(line);
			String[] split = line.split(":");
			if(split[0].equals("POST")){
				postProcess(split[1]);
			}
			else{
				getProcess(split[1]);
			}
			reader.close();
			writer.close();
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * post require processing
	 * @param receiver data was reveived from client
	 */
	public void postProcess(String receiver){
		Database db = new Database();
		String[] infor = receiver.split(",");
		if(db.searchUser(infor[0])){
			db.updateUser(infor);
		}
		else{
			db.insertUser(infor);
		}
		if(db.searchSongAndUser(infor[7], infor[0])){
			db.updateMusic(infor[7], infor[0], Integer.valueOf(infor[8]), Integer.valueOf(infor[9]), infor[10]);
		}
		else{
			db.insertMusic(infor[7], infor[0], Integer.valueOf(infor[8]), Integer.valueOf(infor[9]), infor[10]);
		}
		
		writer.println("ok");
		writer.println("ok");
	}
	/**
	 * get infor from database to send to client
	 * @param infor
	 * @return
	 */
	public void getProcess(String song){
		Database db = new Database();
		ArrayList<String[]> results = db.getSameMusicListening(song);
		for (int index = 0; index < results.size(); index++) {
			String[] result = results.get(index);
			String data = prepareData(result);
			System.out.println(data);
			writer.println(data);
			writer.flush();
		}
	}
	/**
	 * prepare data to send to client
	 * @param infor
	 * @return
	 */
	public String prepareData(String[] infor){
		String data = infor[0];
		for( int i = 1; i< infor.length; i++){
			data +=","+infor[i];
		}
		return data;
	}

}
