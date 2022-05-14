
package net.myorb.abstraction.tests;

public class LambdaTest
{

	public static void main(String... args)
	{
        Calculator myApp = new Calculator();
        IntegerMath addition = (a, b) -> a + b;
        IntegerMath subtraction = (a, b) -> a - b;
        System.out.println("40 + 2 = " +
            myApp.operateBinary(40, 2, addition));
        System.out.println("20 - 10 = " +
            myApp.operateBinary(20, 10, subtraction)); 
        xxx ( (a, b) -> a * b );
        xxx ( addition );
        xxx ( imp () );
        xxx
        (
        		(a, b) -> 
        		{
        			int x = 2*a - b;
        			int aa = x * 2;
        			int bb = x - 1;
        			return aa - bb;
        		}
        );
    }

	static IntegerMath imp ()
	{
		return (a, b) -> 12*a / b;
	}

	static void xxx (IntegerMath math)
	{
		System.out.println (math.operation (5, 6));
	}

}

interface IntegerMath
{
    int operation (int a, int b);   
}

class Calculator
{
    public int operateBinary
    	(int a, int b, IntegerMath op)
    { return op.operation (a, b); }
    public int sum (int l, int r)
    { return operateBinary (l, r, (a,b) -> a+b); }
}

