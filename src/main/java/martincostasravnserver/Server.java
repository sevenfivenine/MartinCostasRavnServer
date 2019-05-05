package martincostasravnserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
	private ServerSocket serverSocket;
	private boolean stopped;

	public Server() {
	}

	public void bindPort(int port) throws IOException
	{
		serverSocket = new ServerSocket( port );

		while ( !stopped )
		{
			System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
			Socket clientSocket = serverSocket.accept();

			System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
			DataInputStream in = new DataInputStream( clientSocket.getInputStream());

			System.out.println(in.readUTF());
			DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream());
			out.writeUTF("Thank you for connecting to " + clientSocket.getLocalSocketAddress() + "\nGoodbye!");
			clientSocket.close();
		}

	}
}
