package echo;

import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EchoServer {
	private static final int SERVER_PORT = 6000;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		
		try {
			//1. 서버소켓 생성
			serverSocket = new ServerSocket();
			List<Writer> listWriters = new ArrayList<Writer>();
			
			//2. 바인딩(Binding)
			String localhostAddress = InetAddress.getLocalHost().getHostAddress();
			serverSocket.bind( new InetSocketAddress(localhostAddress, SERVER_PORT) );
			consoleLog( "binding " +  localhostAddress + ":" + SERVER_PORT );
			
			while( true ) {
				//3. 연결 요청 기다림(accept)
				Socket socket = serverSocket.accept(); // blocking
				new EchoServerReceiveThread(socket, listWriters).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if( serverSocket != null && serverSocket.isClosed() == false ) {
					serverSocket.close();
				}
			} catch( IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void consoleLog( String log ) {
		System.out.println( "[server:" + Thread.currentThread().getId() + "] " + log );
	}
}
