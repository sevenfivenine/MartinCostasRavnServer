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

	public static final int RESPONSE_OK = 0;
	public static final int RESPONSE_ERROR = 1;
	public static final int RESPONSE_PUSH = 2;
	public static final int RESPONSE_LIST = 3;

	private ServerSocket serverSocket;
	private final ArrayList<Socket> clientSockets;
	private boolean stopped;

	private Thread listenThread;

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

		listenThread = new Thread( () -> {
			DataInputStream in;

			while(!stopped)
			{
				synchronized ( clientSockets )
				{
					for ( Socket client : clientSockets )
					{
						try
						{
							in = new DataInputStream( client.getInputStream());

							if ( in.available() > 0 )
							{
								System.out.println("New data from client");
								JSONObject jsonRequest = new JSONObject(in.readUTF());
								onReceiveRequest(client, jsonRequest);
								System.out.println(Datastore.data.get( 0 ).getDate());
							}
						}
						catch ( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
			}
		} );

		listenThread.start();

		while ( !stopped )
		{
			System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
			Socket clientSocket = serverSocket.accept();

			synchronized ( clientSockets )
			{
				clientSockets.add( clientSocket );
			}
			onClientConnection( clientSocket );
		}

	}


	/**
	 * Called when a client connects to the server
	 * Processes requests
	 */
	private void onClientConnection(Socket clientSocket) throws IOException
	{
		System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
		DataInputStream in = new DataInputStream( clientSocket.getInputStream());
		//JSONObject jsonRequest = new JSONObject(in.readUTF());

		//onReceiveRequest( clientSocket, jsonRequest );

		pushToClient(clientSocket);

		//TODO close!

	}

	private void onReceiveRequest(Socket clientSocket, JSONObject jsonRequest) throws IOException
	{
		Request request = Request.JSONtoRequest( jsonRequest );

		if ( request.getRequestCode() == Request.REQUEST_CODE_LIST )
		{
			JSONArray dataJSONarray = parseData( Datastore.data );
			String dataString = dataJSONarray.toString();
			sendResponse( RESPONSE_LIST, clientSocket, dataString );
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_ADD )
		{
			Datastore.addRecord(request.getRecord());
		}

		else if (request.getRequestCode() == Request.REQUEST_CODE_UPDATE )
		{
			Datastore.updateRecord(request.getRecord());
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_REMOVE )
		{
			int responseCode = Datastore.removeRecord(request.getId());

			sendResponse( responseCode, clientSocket, null );
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_SORT )
		{
			Datastore.sort(request.getField(), request.getOrder());
		}
	}


	public void pushToClient(Socket clientSocket)
	{
		try
		{
			JSONArray dataJSONarray = parseData( Datastore.data );
			String dataString = dataJSONarray.toString();
			sendResponse( RESPONSE_PUSH, clientSocket, dataString );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	/**
	 * Send a push to notify clients data has been updated
	 */
	public void pushToClients()
	{
		for ( Socket clientSocket : clientSockets )
		{
			try
			{
				JSONArray dataJSONarray = parseData( Datastore.data );
				String dataString = dataJSONarray.toString();
				sendResponse( RESPONSE_PUSH, clientSocket, dataString );
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}


	private void sendResponse(int responseCode, Socket clientSocket, String response) throws IOException
	{
		switch ( responseCode )
		{
			case RESPONSE_OK:
				break;
			case RESPONSE_ERROR:
				break;
			case RESPONSE_PUSH:
				break;
			case RESPONSE_LIST:
				break;
		}

		DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream());
		out.writeUTF( String.valueOf( responseCode ) );

		// OK, ERROR has no String response
		if ( response == null )
		{

		}

		else
		{
			out.writeUTF( response );
		}
	}


	/**
	 * Called when a client sends a request to the server
	 */
	private void onClientRequest()
	{

	}


	public JSONArray parseData(ArrayList<Media> data)
	{
		JSONArray jsonArray = new JSONArray();

		for ( Media item : data )
		{
			jsonArray.put( item.toJSONObject() );
		}

		return jsonArray;
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
