package com.newer.fileserver;


/**
 * 服务端：基于TCP套接字建立连接进行文件传输
 * 
 * @author 贺
 *
 */
public class App {
	
	
	public static void main(String[] args) {
		FileServer fileserver = new FileServer();
		fileserver.start();
	}
}
