
package net.myorb.data.abstractions;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * support for random number generation
 * @author Michael Druckman
 */
public class Randomizer
{


	/*
	 * random operations
	 */


	/**
	 * randomize a list
	 * @param <T> type of items in list
	 * @param listOfItems any list of items with any order
	 * @return randomized list of same items
	 */
	public static <T> List<T> reorder (List<T> listOfItems)
	{
		List<T>
			pullingFrom = new ArrayList<T>(),
			reordered = new ArrayList<T>();
		pullingFrom.addAll (listOfItems);

		int remaining, chosen;
		while ((remaining = pullingFrom.size ()) > 0)
		{
			chosen = choose (remaining);
			reordered.add (pullingFrom.remove (chosen));
		}

		return reordered;
	}


	/**
	 * get a random integer
	 * @param hi the excluded highest value
	 * @return the chosen value
	 */
	public static int choose (int hi)
	{ return random.nextInt (hi); }


	/*
	 * potential seed patterns
	 */
	public static String
	E    = "27182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274274663919320030599218174135966290435729003342952605956"	// 155
				+ "30738132328627943490763233829880753195251019011573834187930702154089149934884167509244761460668082264800168477411853742345442437107539077744992069",		// 145
	ZETA = "12020569031595942853997381615114499907649862923404988817922715553418382057863130901864558736093352581461991577952607194184919959986732832137763968372079001"
				+ "61453941782949360066719191575522242494243961563909664103291159095780965514651279918405105715255988015437109781102039827532566787603522336984941661",
	PHI  = "16180339887498948482045868343656381177203091798057628621354486227052604628189024497072072041893911374847540880753868917521266338622235369317931800607667263"
				+ "54433389086595939582905638322661319928290267880675208766892501711696207032221043216269548626296313614438149758701220340805887954454749246185695364",
	PI   = "31415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284811"
				+ "1745028410270193852110555964462294895493038196442881097566593344612847564823378678316527120190914564856692346034861045432664821339360726024914";
	public static Map<String,String> SEEDS = new HashMap<String,String>();


	/*
	 * random number generator and seed processing
	 */
	public static int SEED_LENGTH = 11, SEED_DEFAULT_START = 25;
	public static int getSeedLength () { return SEED_LENGTH; }
	public static void setSeedLength (int length) { SEED_LENGTH = length; }
	public static Set<String> getSeeds () { return SEEDS.keySet (); }


	public static String SEED_DIGITS = "141592653589793238462643383279502884197169399375105820974944592307816406";
	public static void setSeedParameters (String digits, int length) { SEED_DIGITS = digits; SEED_LENGTH = length; }
	public static long computeSeedValue (String using, int starting, int length) { return Long.parseLong (using.substring (starting, starting+length)); }
	public static long computeSeedValue (int starting) { return computeSeedValue (SEED_DIGITS, starting, SEED_LENGTH); }
	public static void setSeed (int starting) { setSeedValue (computeSeedValue (starting)); }
	public static void setSeedValue (long value) { random = new Random (value); }
	public static String getSeedFor (String name) { return SEEDS.get (name); }
	static { setSeed (SEED_DEFAULT_START); }
	static Random random;


	/*
	 * seed pattern identifiers
	 */
	static
	{
		SEEDS.put ("E", E);
		SEEDS.put ("ZETA", ZETA);
		SEEDS.put ("PHI", PHI);
		SEEDS.put ("PI", PI);
	}


}

