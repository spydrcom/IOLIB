
package net.myorb.data.abstractions;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * parse text expressions into token streams
 * @author Michael Druckman
 */
public class ExpressionTokenParser extends CommonCommandParser
	implements CommonCommandParser.SpecialTokenSegments
{

	/**
	 * lookup for translations of text to notation.
	 * specifically used for substitution of Greek
	 * characters into expression displays
	 */
	public interface Notation
	{
		/**
		 * translate text to notation
		 * @param text the source text
		 * @return the notation to use
		 */
		String lookFor (String text);
	}

	/*
	 * character collections grouped into classification sets
	 */

	public static final String
	OPERATOR = "+-*/^~!@#%&$|<>,.:;[]{}()=\\?'",
	MULTI_CHARACTER_OPERATOR = "<>\\+-~=*^!$@&|#'.:;/%",
	OPERATOR_EXTENDED = MULTI_CHARACTER_OPERATOR;

	public static final String
	IDN_LEAD = LETTER, IDN_BODY = LETTER + DIGIT + UNDERSCORE;

	public static final Collection<String> COMMENT_INDICATORS = new HashSet<String>();

	public String getIdnLead () { return IDN_LEAD; }
	public String getWhiteSpace () { return WHITE_SPACE; }
	public String getMultiCharacterOperator () { return MULTI_CHARACTER_OPERATOR; }
	public Collection<String> getCommentIndicators () { return COMMENT_INDICATORS; }
	public String getExtendedOperator () { return OPERATOR_EXTENDED; }
	public String getOperator () { return OPERATOR; }
	public String getIdnBody () { return IDN_BODY; }

	public static final String WHITE_SPACE = " \t\r\n_";

	/**
	 * @param buffer text buffer to be parsed
	 * @return list of tokens parsed from source
	 */
	public static ExpressionTokenParser.TokenSequence parse (StringBuffer buffer)
	{
		return new ExpressionTokenParser.TokenSequence (parseCommon (buffer, new ExpressionTokenParser ()));
	}

	/**
	 * build an expression from a token stream
	 * @param tokens the list of tokens to revert to expression
	 * @param lookup a Notation list to use for pretty print
	 * @return the text of the expression stream
	 */
	public static String toFormatted
	(List<TokenDescriptor> tokens, Notation lookup)
	{
		StringBuffer buffer = new StringBuffer ();
		for (TokenDescriptor token : tokens)
		{
			String t = token.getTokenImage ();
			if (lookup != null)
			{
				String notation = lookup.lookFor (t);
				if (notation != null) t = notation;
			}
			buffer.append (t).append (" ");
		}
		return buffer.toString ();
	}
	public static String toPrettyText
	(List<TokenDescriptor> tokens, Notation lookup) { return toFormatted (tokens, lookup); }
	public static String toString (List<TokenDescriptor> tokens)
	{ return toFormatted (tokens, null); }

	/*
	 * recognizable tokens in expressions
	 */

	static final Set<TokenType>
	EXPRESSION_TOKENS = new HashSet<TokenType>();
	static
	{
		EXPRESSION_TOKENS.add (TokenType.IDN);
		EXPRESSION_TOKENS.add (TokenType.OPR);
	}
	public static boolean isRecognizable (TokenType t)
	{ return EXPRESSION_TOKENS.contains (t); }


	public static class TokenSequence extends CommonCommandParser.TokenList
	{
		public TokenSequence () {}
		public TokenSequence (List<TokenDescriptor> tokens) { this.addAll (tokens); }
		public TokenSequence between (int lo, int hi) { return new TokenSequence (subList (lo, hi)); }
		private static final long serialVersionUID = -4208940819480200476L;
	}


}

