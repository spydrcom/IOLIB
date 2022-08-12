
package net.myorb.data.abstractions.language;

import net.myorb.data.abstractions.CommonCommandParser;

import java.util.ArrayList;
import java.util.List;

/**
 * line scanning parser for language sensitive contexts
 * @author Michael Druckman
 */
public class ContextSpecificParser extends ContextSpecificTokenizer
{


	/**
	 * encapsulate token parsing
	 *  and production of scan representation
	 * @param segments the application specific parser segments
	 */
	public ContextSpecificParser
	(CommonCommandParser.SpecialTokenSegments segments)
	{ prepareTokenTracking (); this.segments = segments; }


	/**
	 * use LSE parser to collect tokens
	 * @param buffer source text for parser
	 * @param position current position within the line
	 * @param tokens the list of tokens that becomes parsed
	 * @param tracking the parallel list of tracking records
	 * @return the position at the end of the parse
	 */
	public int parseNext
		(
			StringBuffer buffer, int position,
			List<TokenDescriptor> tokens,
			List<TokenTrack> tracking
		)
	{
		return parseNext (buffer, segments, position, tokens, tracking);
	}
	protected CommonCommandParser.SpecialTokenSegments segments;


	/**
	 * parse a source line into tokens
	 * @param buffer the source text buffer
	 */
	public void parseLine (StringBuffer buffer)
	{
		tokens.clear (); tracking.clear ();
		for (int pos = 0; pos < buffer.length (); )
		{
			pos = parseNext (buffer, pos, tokens, tracking);
			if (eolSeen ()) break;
		}
	}


	/**
	 * allocate token and tracking buffers
	 */
	public void prepareTokenTracking ()
	{
		this.tokens = new ArrayList<TokenDescriptor>();
		this.tracking = new ArrayList<TokenTrack>();
	}
	protected List<TokenDescriptor> tokens;
	protected List<TokenTrack> tracking;


	/**
	 * recognize tracking source contains EOL marker
	 * @return TRUE when EOL found in last tracking position
	 */
	boolean eolSeen ()
	{
		int at; if ((at = tracking.size () - 1) < 0) return false;
		else return tracking.get (at).getType () == Category.EOL;
	}


	/**
	 * wrapper for token parser output
	 */
	public static class Scan
	{
		/**
		 * @param tokens the token descriptor
		 * @param tracking tracking data from the parser
		 */
		public Scan (TokenDescriptor tokens, TokenTrack tracking)
		{
			this.tracking = tracking;
			this.tokens = tokens;
		}
		public TokenDescriptor tokens;
		public TokenTrack tracking;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString ()
		{
			StringBuffer buf = new StringBuffer ()
					.append ("txt='").append (tokens.getTokenImage ())
					.append ("' len=").append (tokens.getTokenImage ().length ())
					.append (" typ=").append (tokens.getTokenType ())
					.append (" start=").append (tracking.getLocation ())
					.append (" cat=").append (tracking.getType ());
			return buf.toString ();
		}
	}


	/**
	 * @param buffer source text for parser
	 * @return the list of scan records
	 */
	public List<Scan> ScanLine (StringBuffer buffer)
	{
		parseLine (buffer);
		
		List<Scan> scans = new ArrayList<Scan> ();
		for (int i=0; i<tokens.size(); i++)
		{
			scans.add (new Scan (tokens.get (i), tracking.get (i)));
		}

		return scans;
	}


}

