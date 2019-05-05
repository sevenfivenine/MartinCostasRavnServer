package martincostasravnserver;

import java.util.ArrayList;

/**
 * Volatile datastore that holds media
 */
public class Datastore
{
	static ArrayList<Media> data = new ArrayList<>();

	static {
		data.add( new Media( "Opera Buffa", "The Marriage of Figaro", "Mozart", "1786", 0, 0, 0 ));
		data.add( new Media( "Dramma Giocoso", "Don Giovanni", "Mozart", "1787", 0, 0, 0 ));
		data.add( new Media( "Opera Buffa", "Così Fan Tutte", "Mozart", "1789", 0, 0, 0 ));
		data.add( new Media( "Romanticism", "Tristan und Isolde", "Richard Wagner", "1865", 0, 0, 0 ));
		data.add( new Media( "Historical Drama", "La Traviata", "Giuseppe Verdi", "1853", 0, 0, 0 ));
		data.add( new Media( "Historical Drama", "Otello", "Giuseppe Verdi", "1887", 0, 0, 0 ));
		data.add( new Media( "Historical Drama", "Aida", "Giuseppe Verdi", "1871", 0, 0, 0 ));
		data.add( new Media( "Verism", "La Bohème", "Giacomo Puccini", "1895", 0, 0, 0 ));
		data.add( new Media( "Tragedy", "Tosca", "Giacomo Puccini", "1900", 0, 0, 0 ));
		data.add( new Media( "Exoticism", "Turandot", "Giacomo Puccini", "1926", 0, 0, 0 ));
	}

}
