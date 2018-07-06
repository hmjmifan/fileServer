package com.newer.client;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Client {
	
	//从文件中读取数据
		FileInputStream fileIn;
		
		//套接字
		Socket socket;
		//套接字输出流
		OutputStream out;
		
		//获取本机地址
		String serverAddress="localhost";
		
		//端口号
		int serverPort  = 10086;
		
		public void start() {
			//通过扫描器输入一个文件并获得它
			Scanner sc = new Scanner(System.in);
			System.out.println("请输入需要上传的文件");
			String file = sc.next();
			sc.close();
			try {
				socket = new Socket(serverAddress,serverPort);
				//从套接字获得输出流，发送数据
				out = socket.getOutputStream();
				byte[] buf =new byte[1024*4];
				int size;
				//打开文件输入流，从文件读取数据
				fileIn = new FileInputStream(file);
				//新建一个长度可变字节数组中，将文件输入流读到的文件写入这个数组
				ByteArrayOutputStream byt = new ByteArrayOutputStream();
				while(-1!=(size=fileIn.read(buf))) {
					byt.write(buf, 0, size);
				}
				byte[] info = byt.toByteArray();
				byte[] hash;
				
				try {
					hash = MessageDigest.getInstance("SHA-256").digest(info);
					out.write(hash);
					System.out.println("get");

					InputStream in = socket.getInputStream();
					byte[] x=new byte[1];
					in.read(x);
					System.out.println(1);
					if(x[0]=='1') {
						System.out.println("该文件已存在");
						
					}else if(x[0]=='0'){
						out.write(info);
					}
					
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}  catch (IOException e) {
			}
			
			
		}
}
