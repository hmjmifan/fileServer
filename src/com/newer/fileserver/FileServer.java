package com.newer.fileserver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer {

	// 服务器套接字
	ServerSocket serversocket;
	// 客户端套接字
	Socket socket;

	HashMap<String, File> map = new HashMap<>();
	// 端口
	int port = 10086;

	// 线程池
	ExecutorService pool;
	//设置文件存放地址
	String filePath = "D:/files";

	public void start() {

		try {
			//建立服务器
			serversocket = new ServerSocket(port);
			//建立线程池
			pool = Executors.newFixedThreadPool(5);
			
			System.out.println("服务器启动");
			while (true) {
				// 建立连接
				socket = serversocket.accept();
				System.out.println("连接成功");
				System.out.println("loading……");
				// 线程发布任务
				pool.submit(new Runnable() {
					// 任务
					public void run() {

						// 从套接字流中读取散列值
						try (InputStream in = socket.getInputStream()) {
							byte[] hash = new byte[32];
							in.read(hash);
							String hashvalue = "";
							//将散列值toString
							hashvalue = new BigInteger(1, hash).toString(16);
							System.out.println("收到散列值，分析中……");
							//启用套接字输出流，给客户端发送信号，如果文件已存在则发送'1'，若文件不存在则发送'0'
							OutputStream out = socket.getOutputStream();
							byte[] x = new byte[1];
							if (map.containsKey(hashvalue)) {

								x[0] = '1';
								out.write(x);
							} 
								//文件不存在时，在目标路径新建一个文件，文件名为其散列值。
								else {

								x[0] = '0';
								out.write(x);
								File f1 = new File(filePath, hashvalue+".txt");
								//先读取客户端传来的套接字，然后将读到的内容通过文件输出流写入目标文件中
								FileOutputStream fileout = new FileOutputStream(f1);
								byte[] buf = new byte[1024 * 4];
								int size;
								while (-1 != (size = in.read(buf))) {
									fileout.write(buf, 0, size);
									fileout.flush();
								}
								fileout.close();
								out.close();
								//将hash值和文件分别以key和value形式存储在哈希map中
								map.put(hashvalue, f1);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e) {
			
				
		}
	}
}
