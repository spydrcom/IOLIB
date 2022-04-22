
package net.myorb.data.abstractions;

import java.util.List;
import java.util.Map;

/**
 * convert token stream to object list
 * @author Michael Druckman
 */
public class ExpressionTokenEvaluation
{

	/**
	 * list objects evaluated from a token list
	 * @param tokens the token list to be evaluated
	 * @param symbolTable a symbol lookup table for named objects
	 * @param objects the list being compiled
	 */
	public static void evaluate
		(
			CommonCommandParser.TokenList tokens,
			Map<String,Object> symbolTable,
			List<Object> objects
		)
	{
		for (CommonCommandParser.TokenDescriptor token : tokens)
		{
			switch (token.getTokenType ())
			{
				case IDN:
					objects.add (symbolTable.get (token.getTokenImage ()));
					break;
				case INT:
				case RDX:
					objects.add (new Integer (token.getTokenImage ()));
					break;
				case DEC:
				case FLT:
					objects.add (new Float (token.getTokenImage ()));
					break;
				case QOT:
					objects.add (token.getTokenImage ());
					break;
				case NUM:
				case OPR:
					break;
			}
		}
	}

}
