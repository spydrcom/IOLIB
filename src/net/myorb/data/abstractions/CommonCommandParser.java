
package net.myorb.data.abstractions;

import java.util.ArrayList;
import java.util.List;

public class CommonCommandParser
{

	/**
	 * a classification for token objects
	 */
	public enum TokenType
	{
		IDN, // an identifier
		OPR, // an operator, possible multi-character
		QOT, // a quoted body of text, a string literal
		NUM, // any numeric value (with no sub-class)
		INT, // an integer value (no decimal point)
		RDX, // an integer value with specified radix
		DEC, // a decimal value (decimal point) 
		FLT  // a floating point value
	}

	/**
	 * describe a token
	 */
	public interface TokenDescriptor
	{
		/**
		 * a classification for the token object
		 * @return the token type assigned to this token
		 */
		TokenType getTokenType ();

		/**
		 * the text of the parsed token
		 * @return the text image of the parsed token
		 */
		String getTokenImage ();

		/**
		 * check image for text match
		 * @param text the text of comparison
		 * @return TRUE = descriptor matches text
		 */
		boolean matches (String text);

		/**
		 * retain numeric value with type as coded in token
		 * @return the value as Number with type as coded in token
		 */
		Number getTokenValueAsCoded ();

		/**
		 * the value of the parsed token
		 * @return the numeric value of the parsed token
		 */
		Number getTokenValue ();
		
		/**
		 * see if token image matches
		 * @param text name of an identifier
		 * @return TRUE = matches token
		 */
		boolean isIdentifiedAs (String text);
	}
	public static class TokenList extends ArrayList <TokenDescriptor>
	{
		public TokenList () {}
		public TokenList (List <TokenDescriptor> tokens) { addAll (tokens); }
		private static final long serialVersionUID = 853517205847222956L;
	}


	/*
	 * character collections grouped into classification sets
	 */

	public static final String
	CAPITAL_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
	LOWER_CASE_LETTER = "abcdefghijklmnopqrstuvwxyz",
	LETTER = CAPITAL_LETTER + LOWER_CASE_LETTER,
	DIGIT = "0123456789", UNDERSCORE = "_";
	
	public static final String
	SCIENTIFIC_NOTATION_DESIGNATION = "Ee",
	RADIX_DESIGNATION = "box", HEX_DIGITS = "ABCDEFabcdef",
	RADIX_SPECIFIC = RADIX_DESIGNATION + HEX_DIGITS,
	SIGN = "+-", DOT = ".";

	public static final String
	QUOTE_LEAD = "\"", QUOTE_TERM = "\"",
	NUM_LEAD = SIGN + DIGIT, NUM_BODY = DIGIT + RADIX_SPECIFIC + DOT + SCIENTIFIC_NOTATION_DESIGNATION,
	NUM_SCIENTIFIC_EXPONENT = DIGIT;


	/**
	 * multi-character token segment descriptions
	 */
	public interface SpecialTokenSegments
	{
		String getWhiteSpace ();
		String getMultiCharacterOperator ();
		String getExtendedOperator ();
		String getOperator ();
		String getIdnLead ();
		String getIdnBody ();
	}


	/**
	 * parse a text buffer into a token list
	 * @param buffer the text of the expression to be parsed
	 * @param segments access to data specifying special segments
	 * @return an ordered list of token descriptors
	 */
	public static List<TokenDescriptor> parseCommon (StringBuffer buffer, SpecialTokenSegments segments)
	{
		List<TokenDescriptor> tokens = new ArrayList<TokenDescriptor>();
		int position = 0, last = buffer.length ();

		while (position < last)
		{
			if (belongsTo (buffer, position, segments.getWhiteSpace ())) { position = ignoreWhitespace (buffer, position, segments); }
			else if (belongsTo (buffer, position, segments.getMultiCharacterOperator ())) { position = parseBigOperator (buffer, position, tokens, segments); }
			else if (belongsTo (buffer, position, segments.getIdnLead ())) { position = parseIdentifier (buffer, position, tokens, segments); }
			else if (belongsTo (buffer, position, segments.getOperator ())) { position = parseOperator (buffer, position, tokens); }
			else if (belongsTo (buffer, position, QUOTE_LEAD)) { position = parseQuote (buffer, position, tokens); }
			else if (belongsTo (buffer, position, NUM_LEAD)) { position = parseNumber (buffer, position, tokens); }
			else
			{
				System.out.println (buffer);
				System.out.println (buffer.charAt (position-1));
				throw new RuntimeException ("Illegal character found: " + buffer.charAt (position) + " @ " + position);
			}
		}

		return tokens;
	}


	/**
	 * determine if a character
	 *  of the buffer belongs to a specified set
	 * @param buffer the text of the expression being parsed
	 * @param location the position within the buffer of the character in question
	 * @param set a string of the characters in the set
	 * @return TRUE = character belongs to set
	 */
	public static boolean belongsTo (StringBuffer buffer, int location, String set)
	{
		if (location >= buffer.length ()) return false;
		return set.indexOf (buffer.charAt (location)) >= 0;
	}


	/**
	 * locate the end of the token within the buffer
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param set a string of the characters in the set
	 * @return the next token start position
	 */
	public static int parseTokenBody (StringBuffer buffer, int position, String set)
	{
		do { position++; }
		while (belongsTo (buffer, position, set));
		return position;
	}


	/**
	 * add a new token to the token list
	 * @param buffer the text of the expression being parsed
	 * @param startingPosition the starting position of the token
	 * @param endingPosition the ending position of the token
	 * @param tokens the list of parsed tokens collected
	 * @param type the type of the token found
	 */
	public static void addToken
		(
			StringBuffer buffer,
			int startingPosition, int endingPosition,
			List<TokenDescriptor> tokens, TokenType type
		)
	{
		tokens.add (new Token (type, buffer.substring (startingPosition, endingPosition)));
	}


	/**
	 * add a newly parsed token to the token list
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param set a string of the characters in the set of the token type
	 * @param tokens the list of parsed tokens collected
	 * @param type the type of the token found
	 * @return the next token start position
	 */
	public static int parseTokenAndAdd
		(
			StringBuffer buffer, int position, String set,
			List<TokenDescriptor> tokens, TokenType type
		)
	{
		int startingPosition = position;
		int endingPosition = parseTokenBody (buffer, position, set);
		addToken (buffer, startingPosition, endingPosition, tokens, type);
		return endingPosition;
	}


	/**
	 * ignore white space found in expression
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param segments access to data specifying special segments
	 * @return the next token start position
	 */
	public static int ignoreWhitespace (StringBuffer buffer, int position, SpecialTokenSegments segments)
	{
		return parseTokenBody (buffer, position, segments.getWhiteSpace ());
	}


	/**
	 * complete the parse of an identifier token
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param tokens the list of parsed tokens collected collected to this point
	 * @param segments access to data specifying special segments
	 * @return the next token start position
	 */
	public static int parseIdentifier (StringBuffer buffer, int position, List<TokenDescriptor> tokens, SpecialTokenSegments segments)
	{
		return parseTokenAndAdd (buffer, position, segments.getIdnBody (), tokens, TokenType.IDN);
	}


	/**
	 * add operator token to list
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param tokens the list of parsed tokens collected
	 * @return the next token start position
	 */
	public static int parseOperator (StringBuffer buffer, int position, List<TokenDescriptor> tokens)
	{
		int startingPosition = position;
		addToken (buffer, startingPosition, ++position, tokens, TokenType.OPR);
		return position;
	}


	/**
	 * parse operator token and add to list
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param tokens the list of parsed tokens collected to this point
	 * @param segments access to data specifying special segments
	 * @return the next token start position
	 */
	public static int parseBigOperator (StringBuffer buffer, int position, List<TokenDescriptor> tokens, SpecialTokenSegments segments)
	{
		return parseTokenAndAdd (buffer, position, segments.getExtendedOperator (), tokens, TokenType.OPR);
	}


	/**
	 * identify a value as having specified radix
	 * @param value the character that would specify radix
	 * @return TRUE = radix is specified
	 */
	public static boolean isRadix (char value)
	{ return value == 'b' || value == 'o' || value == 'x'; }
	public static boolean isRadix (String value) { return value.length () > 1 && isRadix (value.charAt (1)); }
	public static boolean isRadix (StringBuffer buffer, int position, int ending)
	{ return ending > position + 1 && isRadix (buffer.charAt (position + 1)); }


	/**
	 * complete the parse of a value token
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param tokens the list of parsed tokens collected
	 * @return the next token start position
	 */
	public static int parseNumber (StringBuffer buffer, int position, List<TokenDescriptor> tokens)
	{
		int startingPosition = position;
		int endingPosition = parseTokenBody (buffer, position, NUM_BODY);
		if (!isRadix (buffer, position, endingPosition) && belongsTo (buffer, endingPosition-1, SCIENTIFIC_NOTATION_DESIGNATION))
		{ endingPosition = parseTokenBody (buffer, endingPosition, NUM_SCIENTIFIC_EXPONENT); }
		String image = buffer.substring (startingPosition, endingPosition);
		tokens.add (new Token (parseValueClassification (image), image));
		return endingPosition;
	}


	/**
	 * parse a quoted text string
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param tokens the list of parsed tokens collected
	 * @return the next token start position
	 */
	public static int parseQuote (StringBuffer buffer, int position, List<TokenDescriptor> tokens)
	{
		int startingPosition = position;
		int termPosition = buffer.indexOf (QUOTE_TERM, position + 1);
		int endingPosition = termPosition < 0? buffer.length (): termPosition + 1;
		String image = buffer.substring (startingPosition, endingPosition);
		if (termPosition < 0) { image += QUOTE_TERM; }
		tokens.add (new Token (TokenType.QOT, image));
		return endingPosition;
	}


	/**
	 * remove quotes from string token.
	 *  parser leaves quotes on string to be lexically consistent.
	 *  the quotes must be removed to allow the content to not contain them.
	 * @param text a string representation still showing quotes
	 * @return content of string with no quotes
	 */
	public static String stripQuotes (String text)
	{
		int length = text.length ();
		return text.substring (1, length - 1);
	}


	/**
	 * remove quotes from QOT tokens
	 * @param token a token descriptor
	 * @return text without quotes
	 */
	public static String unQuoted (TokenDescriptor token)
	{
		String image = token.getTokenImage ();
		if (token.getTokenType () == TokenType.QOT)
			return stripQuotes (image);
		else return image;
	}


	/**
	 * classify a parsed value
	 * @param image the text comprising the value
	 * @return the appropriate token type
	 */
	public static TokenType parseValueClassification (String image)
	{
		TokenType type = TokenType.INT;
		if (isRadix (image)) type = TokenType.RDX;
		else
		{
			if (image.contains (".")) type = TokenType.DEC;
			if (image.contains ("E")) type = TokenType.FLT;
			if (image.contains ("e")) type = TokenType.FLT;
		}
		return type;
	}


	/**
	 * construct a dummy identifier token
	 * @param text the text to be used
	 * @return the token descriptor
	 */
	public static TokenDescriptor make (String text)
	{
		return new Token (TokenType.IDN, text);
	}


}


/**
 * an implementation of the token descriptor
 */
class Token implements CommonCommandParser.TokenDescriptor
{

	/**
	 * a token is a combination
	 *  of a classification with its text
	 * @param type the classification of this token
	 * @param token the text image of the token
	 */
	Token (CommonCommandParser.TokenType type, String token) { this.type = type; this.image = token; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () { return type.toString () + "   " + image; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.Parser.TokenDescriptor#getTokenType()
	 */
	public CommonCommandParser.TokenType getTokenType () { return type; }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.Parser.TokenDescriptor#isIdentifiedAs(java.lang.String)
	 */
	public boolean isIdentifiedAs (String text) { return image.equalsIgnoreCase (text); }

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.CommonCommandParser.TokenDescriptor#getTokenValue()
	 */
	public Number getTokenValue ()
	{
		Double dblVersion = Double.parseDouble (image); int intVersion = dblVersion.intValue ();
		// token type is ignored and instead treatment depends on whether fraction is zero
		// NOTE: so coding 1.0 will get treated as INT and not FLT nor DEC
		if (intVersion == dblVersion) return new Integer (intVersion);
		else return dblVersion;
	}

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor#getTokenValueAsCoded()
	 */
	public Number getTokenValueAsCoded ()
	{
		switch (type)
		{
			case INT: return new Integer (image);
			case RDX: return SimpleUtilities.radixValue (image);		// INT and RDX are considered as coded as Integer
			case DEC: return new java.math.BigDecimal (image);			// DEC implies a decimal point but no exponentiation
			case FLT: return new Double (image);						// FLT is treated as double precision
			default:
		}
		throw new RuntimeException ("Coding has no numeric interpretation");
	}

	/* (non-Javadoc)
	 * @see net.myorb.math.expressions.Parser.TokenDescriptor#getTokenImage()
	 */
	public String getTokenImage () { return image; }

	/* (non-Javadoc)
	 * @see net.myorb.data.abstractions.CommonCommandParser.TokenDescriptor#matches(java.lang.String)
	 */
	public boolean matches (String text) { return image.equals (text); }

	CommonCommandParser.TokenType type;
	String image;

}

