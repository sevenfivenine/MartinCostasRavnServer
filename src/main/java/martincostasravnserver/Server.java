package martincostasravnserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Server
{
	private ServerSocket serverSocket;
	private ArrayList<Socket> clientSockets;
	private boolean stopped;

	public Server() {
		clientSockets = new ArrayList<>();
	}


	/**
	 * Binds server to selected port and waits for clients to connect
	 * @param port
	 * @throws IOException
	 */
	public void bindPort(int port) throws IOException
	{
		serverSocket = new ServerSocket( port );

		while ( !stopped )
		{
			System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
			Socket clientSocket = serverSocket.accept();
			clientSockets.add( clientSocket );
			onClientConnection();

			System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
			DataInputStream in = new DataInputStream( clientSocket.getInputStream());

			System.out.println(in.readUTF());
			DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream());
			out.writeUTF("Thank you for connecting to " + clientSocket.getLocalSocketAddress() + "\nGoodbye!");

			//TODO close!
		}

	}


	/**
	 * Called when a client connects to the server
	 */
	private void onClientConnection()
	{

	}


	/**
	 * Called when a client sends a request to the server
	 */
	private void onClientRequest()
	{

	}


	public void parseData(ArrayList<Media> data)
	{
		JSONArray jsonArray = new JSONArray();

		for ( Media item : data )
		{
			jsonArray.put( item.toJSONObject() );
		}


	}


	/**
	 * Closes connections to all clients
	 */
	public void closeAllConnections() throws IOException
	{
		for ( Socket client : clientSockets )
		{
			client.close();
		}
	}
}
