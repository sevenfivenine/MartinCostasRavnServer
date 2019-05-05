package martincostasravnserver;

import java.io.IOException;

/**
 * Main Application class that creates a server object
 */
public class ServerApplication
{
	public static final int PORT_NUMBER = 8381;

	public static void main(String[] arg) {
		System.out.println(Datastore.data);

		Server server = new Server();

		try
		{
			// Bind port 8381 and wait for clients
			server.bindPort(PORT_NUMBER);
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

	}
}
