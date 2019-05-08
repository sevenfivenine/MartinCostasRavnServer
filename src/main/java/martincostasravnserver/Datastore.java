package martincostasravnserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

import static martincostasravnserver.Media.KEY_AUTHOR;
import static martincostasravnserver.Media.KEY_DATE;
import static martincostasravnserver.Media.KEY_TITLE;
import static martincostasravnserver.Media.KEY_TYPE;
import static martincostasravnserver.Media.ORDER_ASCENDING;
import static martincostasravnserver.Media.ORDER_DESCENDING;
import static martincostasravnserver.Server.RESPONSE_ERROR;
import static martincostasravnserver.Server.RESPONSE_OK;

/**
 * Volatile datastore that holds media
 */
public class Datastore
{
	static ArrayList<Media> data = new ArrayList<>();

	static {
		data.add( new Media( "Opera Buffa", "The Marriage of Figaro", "Mozart", "1786"));
		data.add( new Media( "Dramma Giocoso", "Don Giovanni", "Mozart", "1787"));
		data.add( new Media( "Opera Buffa", "Così Fan Tutte", "Mozart", "1789"));
		data.add( new Media( "Romanticism", "Tristan und Isolde", "Richard Wagner", "1865"));
		data.add( new Media( "Historical Drama", "La Traviata", "Giuseppe Verdi", "1853"));
		data.add( new Media( "Historical Drama", "Otello", "Giuseppe Verdi", "1887"));
		data.add( new Media( "Historical Drama", "Aida", "Giuseppe Verdi", "1871"));
		data.add( new Media( "Verism", "La Bohème", "Giacomo Puccini", "1895"));
		data.add( new Media( "Tragedy", "Tosca", "Giacomo Puccini", "1900"));
		data.add( new Media( "Exoticism", "Turandot", "Giacomo Puccini", "1926"));
	}

	public static int addRecord(Media record)
	{
		System.out.println("Adding record: " + record.toJSONObject().toString());

		data.add( record );

		try
		{
			ServerApplication.server.pushToClients();
			return RESPONSE_OK;
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			return RESPONSE_ERROR;
		}
	}

	public static int updateRecord(Media record)
	{
		System.out.println("Updating record: " + record.toJSONObject().toString());

		for ( Media m : data )
		{
			if ( m.getId().equals( record.getId() ) )
			{
				m.setTitle( record.getTitle() );
				m.setAuthor( record.getAuthor() );
				m.setType( record.getType() );
				m.setDate( record.getDate() );
			}
		}

		try
		{
			ServerApplication.server.pushToClients();
			return RESPONSE_OK;
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			return RESPONSE_ERROR;
		}
	}


	public static int removeRecord(UUID id)
	{
		Media toRemove = null;

		for ( Media m : data )
		{
			if ( m.getId().equals( id ) )
			{
				toRemove = m;
				break;
			}
		}

		if ( toRemove != null )
		{
			try
			{
				System.out.println("Removing record: " + toRemove.toJSONObject().toString());

				data.remove( toRemove );
				ServerApplication.server.pushToClients();
				return RESPONSE_OK;
			}
			catch ( IOException e )
			{
				e.printStackTrace();
				return RESPONSE_ERROR;
			}
		}

		else
		{
			return RESPONSE_ERROR;
		}
	}


	public static int sort(String field, int order)
	{
		System.out.println("Sorting by field " + field + ", order " + order);

		try
		{
			if ( field.equals( KEY_DATE ) )
			{
				if ( order == ORDER_ASCENDING )
				{
					data.sort( Comparator.comparing( Media::getDate ) );
				}

				else if ( order == ORDER_DESCENDING )
				{
					data.sort( Comparator.comparing( Media::getDate ).reversed() );
				}

				ServerApplication.server.pushToClients();
				return RESPONSE_OK;
			}

			else if ( field.equals( KEY_TITLE ) )
			{
				if ( order == ORDER_ASCENDING )
				{
					data.sort( Comparator.comparing( Media::getTitle ) );
				}

				else if ( order == ORDER_DESCENDING )
				{
					data.sort( Comparator.comparing( Media::getTitle ).reversed() );
				}

				ServerApplication.server.pushToClients();
				return RESPONSE_OK;
			}

			else if ( field.equals( KEY_AUTHOR ) )
			{
				if ( order == ORDER_ASCENDING )
				{
					data.sort( Comparator.comparing( Media::getAuthor ) );
				}

				else if ( order == ORDER_DESCENDING )
				{
					data.sort( Comparator.comparing( Media::getAuthor ).reversed() );
				}

				ServerApplication.server.pushToClients();
				return RESPONSE_OK;
			}

			else if ( field.equals( KEY_TYPE ) )
			{
				if ( order == ORDER_ASCENDING )
				{
					data.sort( Comparator.comparing( Media::getType ) );
				}

				else if ( order == ORDER_DESCENDING )
				{
					data.sort( Comparator.comparing( Media::getType ).reversed() );
				}

				ServerApplication.server.pushToClients();
				return RESPONSE_OK;
			}

			else
			{
				return RESPONSE_ERROR;
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			return RESPONSE_ERROR;
		}
	}
}
