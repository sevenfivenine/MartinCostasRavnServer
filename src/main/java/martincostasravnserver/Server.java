package martincostasravnserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.crypto.Data;

public class Server
{

	public static final int RESPONSE_OK    = 0;
	public static final int RESPONSE_ERROR = 1;
	public static final int RESPONSE_PUSH  = 2;
	public static final int RESPONSE_LIST  = 3;

	private       ServerSocket serverSocket;
	private final List<Socket> clientSockets;
	private       boolean      stopped;

	private Thread listenThread;


	public Server()
	{
		clientSockets = Collections.synchronizedList(new ArrayList<Socket>());
	}


	/**
	 * Binds server to selected port and waits for clients to connect
	 *
	 * @param port
	 * @throws IOException
	 */
	public void bindPort(int port) throws IOException
	{
		serverSocket = new ServerSocket( port );

		// This thread listens for new requests from clients already connected
		listenThread = new Thread( () -> {
			DataInputStream in;

			while ( !stopped )
			{
				synchronized ( clientSockets )
				{
					Socket closed = null;

					Iterator<Socket> itr = clientSockets.iterator();
					while (itr.hasNext())
					{
						try
						{
							Socket client = itr.next();

							// Closed client. Remove from clientSockets
							if ( client.isOutputShutdown() )
							{
								closed = client;
							}

							in = new DataInputStream( client.getInputStream() );

							if ( in.available() > 0 )
							{
								JSONObject jsonRequest = new JSONObject( in.readUTF() );
								onReceiveRequest( client, jsonRequest );
							}
						}
						catch ( IOException e )
						{
							e.printStackTrace();
						}
					}

					if ( closed != null )
					{
						clientSockets.remove( closed );
					}
				}
			}
		} );

		listenThread.start();

		// Before shutdown, close all connections
		// Unfortunately, shutdown hooks do not always run (i.e. SIGKILL, Windows terminate, or JVM crash)
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				System.out.println("Shutting down...");

				try
				{
					closeAllConnections();
				}
				catch ( IOException e )
				{
					e.printStackTrace();
				}
			}
		});

		// On the main thread, wait for new clients
		while ( !stopped )
		{
			System.out.println( "Waiting for new clients on port " + serverSocket.getLocalPort() + "..." );
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
	 * When a new client connects, we should PUSH data to it
	 */
	private void onClientConnection(Socket clientSocket)
	{
		System.out.println( "Just connected to " + clientSocket.getRemoteSocketAddress() );

		try
		{
			pushToClient( clientSocket );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}


	/**
	 * On receiving a request, process and send a response
	 *
	 * @param clientSocket
	 * @param jsonRequest
	 * @throws IOException
	 */
	private void onReceiveRequest(Socket clientSocket, JSONObject jsonRequest) throws IOException
	{
		Request request = Request.JSONtoRequest( jsonRequest );

		System.out.println( "Request code " + request.getRequestCode() + " received from client " + clientSocket.getRemoteSocketAddress() );

		if ( request.getRequestCode() == Request.REQUEST_CODE_LIST )
		{
			JSONArray dataJSONarray = parseData( Datastore.data );
			String dataString = dataJSONarray.toString();

			sendResponse( RESPONSE_LIST, clientSocket, dataString );
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_ADD )
		{
			int responseCode = Datastore.addRecord( request.getRecord() );

			sendResponse( responseCode, clientSocket, null );
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_UPDATE )
		{
			int responseCode = Datastore.updateRecord( request.getRecord() );

			sendResponse( responseCode, clientSocket, null );
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_REMOVE )
		{
			int responseCode = Datastore.removeRecord( request.getId() );

			sendResponse( responseCode, clientSocket, null );
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_SORT )
		{
			int responseCode = Datastore.sort( request.getField(), request.getOrder() );

			sendResponse( responseCode, clientSocket, null );
		}

		else if ( request.getRequestCode() == Request.REQUEST_CODE_CLOSE )
		{
			clientSocket.close();
			clientSockets.remove( clientSocket );
		}
	}


	private void sendResponse(int responseCode, Socket clientSocket, String response) throws IOException
	{
		System.out.println( "Sending response code " + responseCode + " to client " + clientSocket.getRemoteSocketAddress() );

		DataOutputStream out = new DataOutputStream( clientSocket.getOutputStream() );
		out.writeUTF( String.valueOf( responseCode ) );

		// Send response data if present
		if ( response != null )
		{
			out.writeUTF( response );
		}
	}


	public void pushToClient(Socket clientSocket) throws IOException
	{
		System.out.println( "Pushing to new client " + clientSocket.getRemoteSocketAddress() );

		JSONArray dataJSONarray = parseData( Datastore.data );
		String dataString = dataJSONarray.toString();
		sendResponse( RESPONSE_PUSH, clientSocket, dataString );
	}


	/**
	 * Send a push to notify clients data has been updated
	 */
	public void pushToClients() throws IOException
	{
		System.out.println("\nCurrent data:");

		for (Media m : Datastore.data )
		{
			System.out.println(m.toJSONObject().toString());
		}

		System.out.println( "\nPushing to " + clientSockets.size() + " clients" );

		for ( Socket clientSocket : clientSockets )
		{
			JSONArray dataJSONarray = parseData( Datastore.data );
			String dataString = dataJSONarray.toString();
			sendResponse( RESPONSE_PUSH, clientSocket, dataString );
		}
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
