
package net.myorb.data.abstractions;

import java.io.PrintStream;
import java.util.List;

/**
 * exception processing in severity layers
 * @author Michael
 */
public class ErrorHandling
{

	/**
	 * levels of severity
	 */
	public enum Level
	{
		Context,		// message attached but no action needed
		Notify,			// attached message is to be presented to user
		Terminate		// terminate any active processes
	}

	/**
	 * attach a level to a message
	 */
	public interface Severity
	{
		/**
		 * @return the level assigned to the message
		 */
		Level getLevel ();
	}

	/**
	 * flavors of error messages
	 */
	public static class Messages extends RuntimeException implements Severity
	{
		public Messages (String message) { this (message, ""); }
		public Messages (String message, String context, Exception cause)
		{ super (message, cause); this.context = context; }
		public Messages (String message, String context)
		{ super (message); this.context = context; }
		protected String context;

		public Level getLevel () { return level; }
		Level level = Level.Context;

		public void show (PrintStream stream)
		{
			if (shown) return;
			stream.println (context + getMessage ());
			shown = true;
		}

		private static final long serialVersionUID = 2220291952416924622L;
		private boolean shown = false;
	}

	/**
	 * a message type that presents no negative context
	 */
	public static class Notification extends Messages
	{
		public Notification (String message)
		{ super (message, "%%% "); this.level = Level.Notify; }
		private static final long serialVersionUID = -414148295079134040L;
	}

	/**
	 * a message type that forces processing to end
	 */
	public static class Terminator extends Messages
	{
		public Terminator (String message)
		{
			super (message, "*** ");
			this.level = Level.Terminate;
		}
		public Terminator (String message, Exception cause)
		{ super (message, "*** ", cause); this.level = Level.Terminate; }
		public Terminator () { this ("Termination event has been processed"); }
		private static final long serialVersionUID = -739626212806356283L;
	}

	/**
	 * re-throw an exception when a Terminator is found
	 * @param e an exception to be verified as a Terminator
	 * @throws Terminator when Terminator is found
	 */
	public static void checkForTermination (Exception e) throws Terminator
	{
		if (e instanceof Severity)
		{
			Severity s = (Severity) e;

			switch (s.getLevel ())
			{
				case Terminate: throw (Terminator) e;
				default:
			}
		}
	}

	/**
	 * check for severe conditions
	 * @param e exception being evaluated
	 * @param stream a stream to use for notifications
	 * @throws Terminator re-thrown when seen
	 */
	public static void process (Exception e, PrintStream stream) throws Terminator
	{
		if (e instanceof Severity)
		{
			Severity s = (Severity) e;

			switch (s.getLevel ())
			{
				case Terminate:
					throw new Terminator (e.getMessage ());
				case Notify:
					( (Messages) e ).show (stream);
				default:
			}
		}
	}

	/**
	 * access invocation ability of a process
	 */
	public interface Executable
	{
		/**
		 * invoke the process
		 * @throws Terminator for termination indications
		 */
		void process () throws Terminator;
	}

	/**
	 * handle conditions caused by a process
	 * @param e an executable process to be invoked
	 * @param stream a stream to use for notifications
	 * @throws Terminator re-thrown when seen
	 */
	public static void process (Executable e, PrintStream stream) throws Terminator
	{
		try
		{
			e.process ();
		}
		catch (Exception x)
		{
			try { process (x, stream); }
			catch (Terminator t) { t.show (stream); }
		}
	}

	/**
	 * display a line of source as part of error handling
	 * @param fromTokens the tokens of the line being shown
	 * @param toStream the stream to print the line to
	 */
	public static void showErrorLine
		(
			List<ExpressionTokenParser.TokenDescriptor> fromTokens,
			PrintStream toStream
		)
	{
		String line =
			ExpressionTokenParser.toString (fromTokens);
		toStream.println (">>> " + line);
	}

	/**
	 * handle conditions caused by a process
	 * @param fromTokens the tokens of the error source line
	 * @param exceptionSeen an executable process to be invoked
	 * @param displayStream a stream to use for generated output
	 * @param supressingErrorMessages state of error handling selected
	 * @param tracing TRUE when verbose trace is requested
	 * @throws Terminator re-thrown when seen
	 */
	public static void process
		(
			List<ExpressionTokenParser.TokenDescriptor> fromTokens,
			Exception exceptionSeen, PrintStream displayStream,
			boolean supressingErrorMessages,
			boolean tracing
		)
	throws Terminator
	{
		showErrorLine
		(
			fromTokens, displayStream
		);
		process
		(
			exceptionSeen, displayStream,
			supressingErrorMessages, tracing
		);
	}

	/**
	 * handle conditions caused during a process
	 * @param e the exception seen in invoked process
	 * @param stream a stream to use for notifications
	 * @param supressingErrorMessages state of error handling selected
	 * @param tracing TRUE when verbose trace is requested
	 * @throws Terminator re-thrown when seen
	 */
	public static void process
		(
			Exception e, PrintStream stream,
			boolean supressingErrorMessages,
			boolean tracing
		)
	throws Terminator
	{
		// determine severity
		process (e, stream);

		if (tracing)
		{
			// sys-out stack trace for debugging
			e.printStackTrace ();
		}

		if ( ! supressingErrorMessages )
		{
			// show error message to user
			stream.println ("*** " + e.getLocalizedMessage ());
			if (TRACE_FOR_ERROR) e.printStackTrace ();
		}
	}
	public static final boolean TRACE_FOR_ERROR = true;

}
