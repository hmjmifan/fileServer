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

	// 套接字
	Socket socket;
	
	// 文件输入流
	FileInputStream fileIn;
	// 套接字输出流
	OutputStream out;
	// 套接字输入流
	InputStream in;
	
	// 获取本机地址
	String serverAddress = "localhost";

	// 端口号
	int serverPort = 10086;

	public void start() {
		// 通过扫描器输入一个文件并获得它
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入需要上传的文件");
		String file = sc.next();
		// 关闭扫描器
		sc.close();
		try {
			// 建立套接字
			socket = new Socket(serverAddress, serverPort);
			// 从套接字获得输出流，发送数据
			out = socket.getOutputStream();
			// 定义一个缓冲区
			byte[] buf = new byte[1024 * 4];
			int size;
			// 打开文件输入流，从文件读取数据
			fileIn = new FileInputStream(file);
			// 新建一个长度可变字节数组中，将文件输入流读到的文件写入这个数组
			ByteArrayOutputStream byt = new ByteArrayOutputStream();
			// 将文件写入byt内
			while (-1 != (size = fileIn.read(buf))) {
				byt.write(buf, 0, size);
			}

			byte[] info = byt.toByteArray();
			byte[] hash;
			// 获取哈希值，并用套接字输入流写入
			try {
				hash = MessageDigest.getInstance("SHA-256").digest(info);
				out.write(hash);
				System.out.println("hashvalue已传输");

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			// 获取套接字输入流
			in = socket.getInputStream();
			//读取客户端发来的信号
			byte[] x = new byte[1];
			in.read(x);
			System.out.println("得到反馈");
			if (x[0] == '1') {
				System.out.println("该文件已存在，无需再次上传");

			} else if (x[0] == '0') {
				out.write(info);
				System.out.println("传输文件完成");
			}
			//关闭文件输入流，套接字输入流，套接字输出流，套接字
			in.close();
			fileIn.close();
			out.close();
			socket.close();

		} catch (IOException e) {
		}

	}
}
