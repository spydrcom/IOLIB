
package net.myorb.data.abstractions;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

/**
 * token parser driven by data map rules
 * @author Michael Druckman
 */
public class CommonCommandParser extends CommonTokenProcessing
{


	/**
	 * a classification for token objects
	 */
	public enum TokenType
	{
		IDN, // an identifier
		OPR, // an operator, possible multi-character
		QOT, // a quoted body of text, a string literal
		SEQ, // a captured sequence for a lambda expression
		RDX, // an integer value with specified radix
		NUM, // any numeric value (with no sub-class)
		INT, // an integer value (no decimal point)
		DEC, // a decimal value (decimal point) 
		FLT, // a floating point value
		CMT, // a comment to EOL
		WS   // white space
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


	/**
	 * an ordered sequence of tokens
	 */
	public static class TokenList extends ArrayList <TokenDescriptor>
	{
		public TokenList () {}
		public TokenList (List <TokenDescriptor> tokens) { addAll (tokens); }
		private static final long serialVersionUID = 853517205847222956L;
	}


	/**
	 * multi-character token segment descriptions
	 */
	public interface SpecialTokenSegments
	{
		String getSequenceCaptureMarkers ();
		Collection<String> getCommentIndicators ();
		String getMultiCharacterOperator ();
		String getExtendedOperator ();
		String getWhiteSpace ();
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
			else if (belongsTo (buffer, position, segments.getSequenceCaptureMarkers ())) { position = parseSequenceCapture (buffer, position, tokens, segments); }
			else if (belongsTo (buffer, position, segments.getMultiCharacterOperator ())) { position = parseBigOperator (buffer, position, tokens, segments); }
			else if (belongsTo (buffer, position, segments.getIdnLead ())) { position = parseIdentifier (buffer, position, tokens, segments); }
			else if (belongsTo (buffer, position, segments.getOperator ())) { position = parseOperator (buffer, position, tokens); }
			else if (belongsTo (buffer, position, QUOTE_LEAD)) { position = parseQuote (buffer, position, tokens); }
			else if (belongsTo (buffer, position, NUM_LEAD)) { position = parseNumber (buffer, position, tokens); }
			else illegalCharacter (buffer, position);
		}

		return tokens;
	}


}


