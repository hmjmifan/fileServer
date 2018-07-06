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

	String filePath = "D:/files";

	public void start() {

		try {
			serversocket = new ServerSocket(port);
			pool = Executors.newFixedThreadPool(5);

			System.out.println("服务器启动");
			while (true) {
				// 建立连接
				socket = serversocket.accept();
				System.out.println("连接成功");
				// 线程发布任务
				pool.submit(new Runnable() {
					// 任务
					public void run() {

						// 从套接字流中读取散列值
						try (InputStream in = socket.getInputStream()) {
							byte[] hash = new byte[32];
							System.out.println("start");
							in.read(hash);
							String hashvalue = "";

							hashvalue = new BigInteger(1, hash).toString(16);
							//
							OutputStream out = socket.getOutputStream();
							byte[] x = new byte[1];
							if (map.containsKey(hashvalue)) {

								x[0] = '1';
								out.write(x);
							} else {

								x[0] = '0';
								out.write(x);
								File f1 = new File(filePath, hashvalue+".txt");
								FileOutputStream fileout = new FileOutputStream(f1);
								byte[] buf = new byte[1024 * 4];
								int size;
								while (-1 != (size = in.read(buf))) {
									fileout.write(buf, 0, size);
									fileout.flush();
								}
								fileout.close();
								out.close();

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
