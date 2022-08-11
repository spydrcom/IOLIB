
package net.myorb.data.abstractions.language;

import net.myorb.data.abstractions.ExpressionTokenParser;

import java.util.Collection;
import java.util.List;

/**
 * high level detail token parsing for language level semantics
 * @author Michael Druckman
 */
public class ContextSpecificTokenizer extends ExpressionTokenParser
{


	/*
	 * specially packaged version of the token parser
	 * data structures support higher level of detail
	 */


	/**
	 * @param buffer the text of the expression to be parsed
	 * @param segments access to data specifying special segments
	 * @param position the source position to note for tracking retention
	 * @param tokens the parallel structure to tracking holding the token description
	 * @param tracking the parallel structure to tokens holding the position description
	 * @return the position at the conclusion of the parse
	 */
	public int parseNext (StringBuffer buffer, SpecialTokenSegments segments, int position, List<TokenDescriptor> tokens, List<TokenTrack> tracking)
	{
		if (belongsTo (buffer, position, segments.getWhiteSpace ())) { position = parseWhitespace (buffer, position, segments, tokens, tracking); }
		else if (belongsTo (buffer, position, segments.getMultiCharacterOperator ())) { track (Category.MCO, position, tracking); position = parseBigOperator (buffer, position, tokens, segments); }
		else if (belongsTo (buffer, position, segments.getIdnLead ())) { track (Category.ID, position, tracking); position = parseIdentifier (buffer, position, tokens, segments); }
		else if (belongsTo (buffer, position, segments.getOperator ())) { track (Category.OP, position, tracking); position = parseOperator (buffer, position, tokens); }
		else if (belongsTo (buffer, position, QUOTE_LEAD)) { track (Category.TXT, position, tracking); position = parseQuote (buffer, position, tokens); }
		else if (belongsTo (buffer, position, NUM_LEAD)) { track (Category.NUM, position, tracking); position = parseNumber (buffer, position, tokens); }
		else illegalCharacter (buffer, position);

		return commentCheck
			(
				buffer, position, tokens, tracking, segments
			);
	}


	/*
	 * track token location within a document for LSE
	 */

	public enum Category
	{
		WS,		// white-space
		EOL,	// end-of-line
		MCO,	// multi-character operator
		ID,		// identifier
		OP,		// operator
		TXT,	// raw text
		NUM		// number
	}


	/**
	 * simple bean that connects position and category
	 */
	public static class TokenTrack
	{
		TokenTrack (Category type, int starting)
		{ this.starting = starting; this.type = type; }
		public int getLocation () { return starting; }
		public Category getType () { return type; }
		Category type; int starting;
	}


	/**
	 * @param cat the category of the token
	 * @param pos the position in the source line
	 * @param tracking the list of tracking records being constructed
	 */
	public static void track
	(Category cat, int pos, List<TokenTrack> tracking)
	{ tracking.add (new TokenTrack (cat, pos)); }


	/**
	 * comments that run to EOL
	 * @param buffer the source text being parsed
	 * @param position the position after the last token
	 * @param tokens the list of tokens compiled so far
	 * @param tracking the tracking data for the tokens
	 * @param segments data specifying special segments
	 * @return the position resulting from the check
	 */
	public static int commentCheck
		(
			StringBuffer buffer, int position,
			List<TokenDescriptor> tokens, List<TokenTrack> tracking,
			SpecialTokenSegments segments
		)
	{
		TokenDescriptor tok; TokenTrack trk; int index;
		if ((index = tokens.size () - 1) < 0) return position;

		Collection<String> comments = segments.getCommentIndicators ();
		if (comments == null) return position;

		TokenDescriptor td = tokens.get (index);
		String tokenText = td.getTokenImage ().toUpperCase ();

		if (comments.contains (tokenText))
		{
			int bufferEnd = buffer.length () - 1;
			trk = tracking.get (index);

			tok = newToken
			(
				buffer,
				trk.starting, bufferEnd,
				TokenType.CMT
			);

			tokens.set (index, tok);
			position = bufferEnd + 1;
		}

		return position;
	}


	/*
	 * special processing for white-space when this level of detail is requested
	 */


	/**
	 * determine if character at position in buffer is EOL
	 * @param buffer the text of the expression being parsed
	 * @param at the position within the buffer of the character in question
	 * @return TRUE when character at position is not EOL AKA newline AKA \n
	 */
	public static boolean
		isNotEol (StringBuffer buffer, int at)
	{ return  buffer.charAt (at) != EOL;  }
	public static final char EOL = '\n';


	/**
	 * treat white space as a token
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer of the character in question
	 * @param segments access to data specifying special segments
	 * @param tokens the list of parsed tokens collected collected to this point
	 * @param tracking the tracking data for the tokens
	 * @return the next token start position
	 */
	public int parseWhitespace
		(
			StringBuffer buffer, int position,
			SpecialTokenSegments segments,
			List<TokenDescriptor> tokens,
			List<TokenTrack> tracking
		)
	{
		if ( ! trackingWS )
		{
			return ignoreWhitespace (buffer, position, segments);
		}
		else
		{
			return tokenForWhitespace (buffer, position, segments, tokens, tracking);
		}
	}


	/**
	 * create token and tracking entries for a WS sequence
	 * @param buffer the text of the expression being parsed
	 * @param startingPosition the position within the buffer starting a token
	 * @param segments access to data specifying special segments for this context
	 * @param tokens the list of parsed tokens collected collected to this point
	 * @param tracking the tracking data for the tokens in the parallel list
	 * @return the position at the end of the WS sequence
	 */
	public static int tokenForWhitespace
		(
			StringBuffer buffer, int startingPosition,
			SpecialTokenSegments segments, List<TokenDescriptor> tokens,
			List<TokenTrack> tracking
		)
	{
		int position = traverseWhitespaceSegment (buffer, startingPosition, segments.getWhiteSpace ());
		track (position < buffer.length () && isNotEol (buffer, position) ? Category.WS : Category.EOL, position, tracking);
		addToken (buffer, startingPosition, position, tokens, TokenType.WS);
		return position;
	}


	/**
	 * seek forward in buffer for non-WS character or EOL
	 * @param buffer the text of the expression being parsed
	 * @param position the position within the buffer  starting a token
	 * @param whiteSpaceCharacters the characters to be treated as white space
	 * @return the position at the end of the traverse
	 */
	public static int traverseWhitespaceSegment (StringBuffer buffer, int position, String whiteSpaceCharacters)
	{
		do { position++; }
		while (belongsTo (buffer, position, whiteSpaceCharacters) && isNotEol (buffer, position));
		return position;
	}


	/**
	 * request white-space segments be treated as tokens
	 */
	public void trackWS () { trackingWS = true; }
	protected boolean trackingWS = false;


}
