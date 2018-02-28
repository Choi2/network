package echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EchoServerReceiveThread extends Thread {
	private Socket socket;
	private String nickName;
	List<Writer> listWriters;

	public EchoServerReceiveThread(Socket socket, List<Writer> listWriters) {
		this.socket = socket;
		this.listWriters = listWriters;
	}

	@Override
	public void run() {
		// 4. 연결 성공
		InetSocketAddress remoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		int remoteHostPort = remoteSocketAddress.getPort();
		String remoteHostAddress = remoteSocketAddress.getAddress().getHostAddress();
		consoleLog("connected from " + remoteHostAddress + ":" + remoteHostPort);

		try {
			// 5. I/O Stream 받아오기
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter printWriter = new PrintWriter(
					new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

			// 3. 요청 처리
			while (true) {
				String request = br.readLine();
				if (request == null) {
					consoleLog("클라이언트로 부터 연결 끊김");
					break;
				}

				// 4. 프로토콜 분석

				String[] tokens = request.split(":");

				if ("join".equals(tokens[0])) {
					doJoin(tokens[1], printWriter);
				} else if ("message".equals(tokens[0])) {
					doMessage(tokens[1]);
				} else if ("quit".equals(tokens[0])) {
					doQuit(listWriters);
				} else {
					consoleLog("에러:알수 없는 요청(" + tokes[0] + ")");
				}

			}
		} catch (SocketException e) {
			// 상대편이 정상적으로 소켓을 닫지 않고 종료 한 경우
			consoleLog("sudden closed by client");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void doQuit(Writer writer) {
		removeWriter(writer);

		String data = nickName + "님이 퇴장 하였습니다.";
		broadcast(data);

	}

	private void removeWriter(Writer writer) {
		
	}

	private void doJoin(String nickName, Writer writer) throws IOException {
		this.nickName = nickName;
		addWriter(writer);

		((PrintWriter) writer).println("join:ok");
		writer.flush();
	}

	private void addWriter(Writer writer) {
		synchronized (listWriters) {
			listWriters.add(writer);
		}
	}

	private void consoleLog(String log) {
		System.out.println("[server:" + getId() + "] " + log);
	}

	private void broadcast(String data) {

		synchronized (listWriters) {
			for (Writer writer : listWriters) {
				PrintWriter printWriter = (PrintWriter) writer;
				printWriter.println(data);
				printWriter.flush();
			}

		}
	}

}
