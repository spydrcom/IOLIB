
package net.myorb.data.font.truetype;

import net.myorb.utilities.AccessControl;
import net.myorb.utilities.BlockManager;

import java.util.HashMap;
import java.util.Map;

/**
 * subtable records of NameTable
 * @author Michael Druckman
 */
public class NameRecord extends BlockManager implements NameTable.Entry
{

//	Type	Description
//	USHORT	Platform ID.
//	USHORT	Platform-specific encoding ID.
//	USHORT	Language ID.
//	USHORT	Name ID.
//	USHORT	String length (in bytes).
//	USHORT	String offset from start of storage area (in bytes).

//	Platform ID
//	ID	Platform		Specific encoding
//	0	Apple Unicode	none
//	1	Macintosh		Script manager code
//	2	ISO				ISO encoding
//	3	Microsoft 		Microsoft encoding

//	Name ID’s
//	Code	Meaning
//	0	Copyright notice.
//	1	Font Family name
//	2	Font Subfamily name; for purposes of definition, this is assumed to address style (italic, oblique) and weight (light, bold, black, etc.) only. A font with no particular differences in weight or style (e.g. medium weight, not italic and fsSelection bit 6 set) should have the string “Regular” stored in this position.
//	3	Unique font identifier
//	4	Full font name; this should simply be a combination of strings 1 and 2. Exception: if string 2 is “Regular,” then use only string 1. This is the font name that Windows will expose to users.
//	5	Version string. In n.nn format.
//	6	Postscript name for the font.
//	7	Trademark; this is used to save any trademark notice/information for this font. Such information should be based on legal advice. This is distinctly separate from the copyright.

//	Microsoft platform-specific encoding ID’s (platform ID = 3)
//	Code	Description
//	0		Undefined character set or indexing scheme
//	1		UGL character set  with Unicode indexing scheme (see chapter, “Character Sets.”)

//	Primary Language	Locale Name		LCID			Win CP	DOS CP
//	English (6):		American		(0409; ENU)		1252	437
//	English (6):		British			(0809; ENG)		1252	850
//	English (6):		Australian		(0C09; ENA)		1252	850
//	English (6):		Canadian		(1009; ENC)		1252	850
//	English (6):		New Zealand		(1409; ENZ)		1252	850
//	English (6):		Ireland			(1809; ENI)		1252	850

	public static final String
	platformId			= "platformId",
	encodingId			= "encodingId",
	languageId			= "languageId",
	nameId				= "nameId",
	stringLength		= "stringLength",
	stringOffset		= "stringOffset";

	public static final BlockDefinition DEFINITION = new BlockDefinition ();

	static
	{
		DEFINITION.add (	platformId,		U16		);
		DEFINITION.add (	encodingId, 	U16		);
		DEFINITION.add (	languageId, 	U16		);
		DEFINITION.add (	nameId, 		U16		);
		DEFINITION.add (	stringLength, 	U16		);
		DEFINITION.add (	stringOffset, 	U16		);
	}

	/**
	 * parse string text from table
	 * @param access TTF source for the tables
	 * @throws Exception for any errors
	 */
	public void readString (AccessControl.Access access) throws Exception
	{
		text = access.readString
		(
			determineEncoding (),
			getProperty (stringLength).intValue (),
			getStorageBase () + getProperty (stringOffset).longValue ()
		);
	}
	public String determineEncoding ()
	{ return getPlatformId () == MICROSOFT_PLATFORM_ID ? "UTF-16" : "UTF-8"; }
	public String getStringText () { return text; }
	private String text;

	/**
	 * @return offset in source to name table string storage area
	 */
	public long getStorageBase ()
	{
		long tableBase = getProperty (BLOCK_BASE_ADDRESS).longValue ();
		long storageBase = tableBase + getProperty (NameTable.stringStorageOffset).longValue ();
		return storageBase;
	}

	/**
	 * @return the ID value read from the table
	 */
	public int getNameId () { return getProperty (nameId).intValue (); }
	public int getPlatformId () { return getProperty (platformId).intValue (); }
	public int getEncodingId () { return getProperty (encodingId).intValue (); }
	public int getLanguageId () { return getProperty (languageId).intValue (); }
	public static int MACINTOSH_PLATFORM_ID = 1, MICROSOFT_PLATFORM_ID = 3;

	/**
	 * @param id the ID value found in the header
	 * @return the name associated with the ID
	 */
	public String getPlatform (int id) { return PLATFORMS[id]; }
	public static final String[] PLATFORMS = new String[]{"Apple","Macintosh","ISO","Microsoft"};

	/**
	 * @param id the encoding id from the source record
	 * @return the encoding type in MS context
	 */
	public String getMSencoding (int id) { return ENCODING[id]; }
	public static final String[] ENCODING = new String[]{"Undefined","UGL"};

	/**
	 * @param id the name id from the source record
	 * @return a name for the id
	 */
	public String getNameType (int id)
	{ return id < TYPES.length ? TYPES[id] : "UNRECOGNIZED (" + Integer.toString (id) + ")"; }
	public static final String[] TYPES = new String[]
	{
		"Copyright","FontFamilyName","FontSubfamilyName","UniqueFontIdentifier",
		"FullFontName","VersionString","PostscriptName","Trademark"
	};

	public static final Map<Integer,String> LANGUAGES = new HashMap<Integer,String>();

	static
	{
		LANGUAGES.put (0x403, "Catalan");
		LANGUAGES.put (0x405, "Czech");
		LANGUAGES.put (0x406, "Danish");
		LANGUAGES.put (0x407, "German (Standard)");
		LANGUAGES.put (0x408, "Greek");
		LANGUAGES.put (0x409, "English (American)");
		LANGUAGES.put (0x40A, "Spanish (Traditional)");
		LANGUAGES.put (0x40B, "Finnish");
		LANGUAGES.put (0x40C, "French (Standard)");
		LANGUAGES.put (0x40E, "Hungarian");
		LANGUAGES.put (0x410, "Italian (Standard)");
		LANGUAGES.put (0x413, "Dutch (Standard)");
		LANGUAGES.put (0x414, "Norwegian");
		LANGUAGES.put (0x415, "Polish");
		LANGUAGES.put (0x416, "Portuguese (Brazilian)");
		LANGUAGES.put (0x419, "Russian");
		LANGUAGES.put (0x41B, "Slovak");
		LANGUAGES.put (0x41D, "Swedish");
		LANGUAGES.put (0x41F, "Turkish");
		LANGUAGES.put (0x424, "Slovenian");
		LANGUAGES.put (0x42D, "Basque");
		LANGUAGES.put (0x80A, "Spanish (Mexican)");
		LANGUAGES.put (0x816, "Portuguese (Standard)");
		LANGUAGES.put (0xC0A, "Spanish (Modern)");
		LANGUAGES.put (0xC0C, "French (Canadian)");
	}

	/**
	 * @param platform the platform for context
	 * @param id the id of the language on this platform
	 * @return a name for the language
	 */
	public String getLanguage (int platform, int id)
	{
		if (platform == MICROSOFT_PLATFORM_ID)
		{
			if (LANGUAGES.containsKey (id)) return LANGUAGES.get (id);
			else return "UNRECOGNIZED (" + Integer.toHexString (id) + ")";
		}
		else if (platform == MACINTOSH_PLATFORM_ID)
		{
			if (id == 0) return "English";
			else return "UNRECOGNIZED";
		}
		else return "UNRECOGNIZED";
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.font.truetype.NameTable.Entry#getText()
	 */
	public String getText () { return text; }
	public String getLanguage () { return getLanguage (getPlatformId (), getLanguageId ()); }
	public String getContext () { return getNameType (getNameId ()); }

	/**
	 * display platform header for name record
	 * @return a text line with the parsed header properties
	 */
	public String formattedHeader ()
	{
		int pid;
		StringBuffer buffer = new StringBuffer ();
		buffer.append (getPlatform (pid = getPlatformId ())).append ("  {");
		if (pid == MICROSOFT_PLATFORM_ID) buffer.append (" encoding=").append (getMSencoding (getEncodingId ()));
		buffer.append (" language=").append (getLanguage (getPlatformId (), getLanguageId ()));
		return buffer.append (" }").toString ();
	}

	/**
	 * display type information about this name record
	 * @param headerLanguage the language ID from the header
	 * @return a name for the type plus language where appropriate
	 */
	public String formattedRecord (int headerLanguage)
	{
		int recordLanguage = getLanguageId ();
		StringBuffer buffer = new StringBuffer ();
		buffer.append ("[ ").append (getNameType (getNameId ()));

		if (recordLanguage != headerLanguage)
		{
			buffer.append (" language=").append (getLanguage (getPlatformId (), recordLanguage));
		}

		return buffer.append (" ]").toString ();
	}

	/* (non-Javadoc)
	 * @see net.myorb.utilities.BlockManager#toString()
	 */
	public String toString ()
	{
		return super.toString () + "; '" + text + "'";
	}

	/**
	 * describe table properties
	 */
	public NameRecord ()
	{
		super (DEFINITION); setBlockName (BLOCK_NAME);
	}
	public static final String BLOCK_NAME = "NAME";

}

