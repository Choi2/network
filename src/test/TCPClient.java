package test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {
	private static final String SERVER_IP = "192.168.1.11";
	private static final int SERVER_PORT = 5000;
	
	public static void main(String[] args) {
		Socket socket = null;
		
		try {
			//1. 소켓 생성
			socket = new Socket();

			//2. 서버연결
			socket.connect( new InetSocketAddress(SERVER_IP, SERVER_PORT ) );
		
			//3. I/O Stream 받아 오기
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			//4. 쓰기/읽기
			String data = "hello";
			os.write( data.getBytes( "utf-8" ) );
			
			byte[] buffer = new byte[256];
			int readByteCount = is.read( buffer );
			
			if( readByteCount == -1 ) {
				System.out.println( "[client] disconnected by Server" );
				return;
			}
			
			data = new String( buffer, 0, readByteCount, "utf-8" );
			System.out.println( "[client] received:" + data );
			
		} catch(ConnectException e ) {
			System.out.println( "[client] Not Connected");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if( socket != null && socket.isClosed() == false ) {
					socket.close();
				}
			} catch( IOException e ) {
				e.printStackTrace();
			}
		}
		
	}

}
