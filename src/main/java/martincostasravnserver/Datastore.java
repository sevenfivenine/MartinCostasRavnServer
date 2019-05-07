package martincostasravnserver;

import java.util.ArrayList;

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

	public static void addRecord(Media record)
	{
		data.add( record );

		ServerApplication.server.pushToClients();
	}

	public static void updateRecord(Media record)
	{
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

		ServerApplication.server.pushToClients();
	}
}
