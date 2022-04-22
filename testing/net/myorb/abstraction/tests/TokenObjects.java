
package net.myorb.abstraction.tests;

import net.myorb.data.abstractions.*;

import java.util.*;

public class TokenObjects
{

	public static void main (String... args )
	{
		List<Object> objects = new ArrayList<Object>();
		Map<String,Object> symbolTable = new HashMap<String,Object>();
		symbolTable.put ("something", new Boolean ("TRUE"));
		ExpressionTokenParser.TokenSequence tokens = ExpressionTokenParser.parse
				(new StringBuffer ("123, 456.789, something, \" abcdefghi \", 987"));
		ExpressionTokenEvaluation.evaluate (tokens, symbolTable, objects);
		System.out.println (objects);
	}

}
