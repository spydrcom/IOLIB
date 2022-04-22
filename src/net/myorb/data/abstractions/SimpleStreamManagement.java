
package net.myorb.data.abstractions;

import java.util.concurrent.ArrayBlockingQueue;
import java.io.IOException;

/**
 * manage threaded transfer of content from source to sink.
 * @author Michael Druckman
 */
public class SimpleStreamManagement extends SimpleStreamIO
{


	/**
	 * describe a buffer read and written as one block transfer
	 */
	public static class Buffer
	{
		/**
		 * @return the currently held block
		 */
		public byte[] getBlock () { return block; }

		/**
		 * @param block the new block to hold
		 */
		public void setBlock(byte[] block) { this.block = block; }
		byte[] block; // block is a simple array of bytes
	}


	/**
	 * control for transfers of block streams
	 */
	public static class Transfer extends SimpleBlockingIO <Buffer>
	{


		/**
		 * @param source the content source stream
		 * @param sink the sink for the contents
		 * @param capacity maximum buffer count
		 */
		protected Transfer
			(
				SimpleStreamIO.Source source,
				SimpleStreamIO.Sink sink,
				int capacity
			)
		{
			super (capacity);
			this.source = source;
			this.sink = sink;
		}
		ArrayBlockingQueue <Buffer> buffers;
		SimpleStreamIO.Source source;
		SimpleStreamIO.Sink sink;


		/**
		 * fill the buffer queue
		 * @param capacity the maximum buffer count
		 * @throws InterruptedException for thread interruption
		 */
		public void allocate (int capacity) throws InterruptedException
		{
			buffers = new ArrayBlockingQueue <Buffer> (capacity);
			for (int i=0; i<capacity; i++) buffers.put (new Buffer ());
		}


		/**
		 * return buffer after use
		 * @param b the buffer object to return
		 * @throws InterruptedException for queue insertion failure
		 */
		public void returnToQ (Buffer b) throws InterruptedException
		{
			b.setBlock (null); buffers.put (b);
		}


		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleBlockingIO#get()
		 */
		@Override
		public Buffer get () throws InterruptedException, TransferError
		{
			Buffer b = buffers.take ();
			b.setBlock (source.get ());
			return b;
		}


		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleBlockingIO#put(java.lang.Object)
		 */
		@Override
		public void put (Buffer b) throws InterruptedException, TransferError
		{
			sink.put (b.getBlock ());
			returnToQ (b);
		}


		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleBlockingIO#complete()
		 */
		@Override
		public void complete ()
		{
			synchronized (this) { notify (); }
		}


		/**
		 * wait for notification of transfer completion
		 * @throws InterruptedException for thread interruption
		 */
		public void completionWait () throws InterruptedException
		{
			synchronized (this) { wait (); }
		}


		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleBlockingIO#isComplete(java.lang.Object)
		 */
		@Override
		public boolean isComplete (Buffer t)
		{
			return t.block == null;
		}


	}


	/**
	 * buffers will be allocated up to capacity.
	 *  producer and consumer threads will be started.
	 *  current thread will wait for transfer threads to complete.
	 * @param source the content source stream for the transfer to be done
	 * @param sink the sink for the contents for the transfer to be done
	 * @param capacity maximum buffer count to be used during transfer
	 * @throws InterruptedException for thread interruption
	 */
	public static void doTransfer
		(
			SimpleStreamIO.Source source,
			SimpleStreamIO.Sink sink,
			int capacity
		)
	throws InterruptedException
	{
		Transfer t = new Transfer
			(source, sink, capacity);
		t.allocate (capacity); t.startCopy ();
		t.completionWait ();
	}


	/**
	 * exception thrown for stream type conflicts
	 */
	@SuppressWarnings ("serial") public static
	class StreamMisMatchException extends Exception
	{ StreamMisMatchException (String message) { super (message); } }


	/**
	 * check for stream type mis-match
	 * @param source the source being used for transfer
	 * @param sink the sink being used as destination for transfer
	 * @throws StreamMisMatchException when source and sink types are in conflict
	 */
	public static void
		checkStreams (Source source, Sink sink)
	throws StreamMisMatchException
	{
		if (source instanceof TextSource)
		{
			if ( ! (sink instanceof TextSink) )
			{
				throw new StreamMisMatchException ("Sink for TextSource must be TextSink");
			}
		}
		else if (source instanceof BinarySource)
		{
			if ( ! (sink instanceof BinarySink) )
			{
				throw new StreamMisMatchException ("Sink for BinarySource must be BinarySink");
			}
		}
		else throw new StreamMisMatchException ("Source of transfer is not recognized");
	}


	/**
	 * transfer without blocking support
	 * @param source the source of content to process
	 * @param sink the sink destination for content being processed
	 * @throws TransferError for errors in transfer
	 */
	public static void doRawTransfer
	(Source source, Sink sink)
	throws TransferError
	{
		byte[] block;
		do { sink.put (block = source.get ()); }
		while (block != null);
	}


	/**
	 * stream processing with/without blocking.
	 *  as of 1/8/19 refactor, both binary and text use this processing.
	 *  source and sink must both be either Binary or Text type object or misMatch thrown.
	 * @param source the source of content to be transferred in this operation
	 * @param sink the sink for content  to be transferred in this operation
	 * @throws StreamMisMatchException for stream type conflict
	 * @throws InterruptedException for thread interruption
	 * @throws TransferError for errors during transfer
	 */
	public static void processStream (Source source, Sink sink)
	throws TransferError, StreamMisMatchException, InterruptedException
	{
		checkStreams (source, sink);			// stream types must match
		long timeStamp = System.nanoTime ();	// base time for throughput metrics

		if ( ! USE_BLOCKING_SUPPORT )			// selected type of transfer
		{
			doRawTransfer (source, sink);		// brute force chunk copy
		}
		else
		{
			doTransfer (source, sink, BUFFER_CAPACITY);
		}

		if (PROVIDE_THROUGHPUT_METRICS) { showThroughputMetrics (source.totalRead (), timeStamp); }
	}
	public static boolean
		USE_BLOCKING_SUPPORT = true,
		PROVIDE_THROUGHPUT_METRICS = false;
	public static int BUFFER_CAPACITY = 20;


	/**
	 * display throughput calculations for transfer
	 * @param bytesProcessed count of bytes passing through source
	 * @param timeStamp the nano count seen when the transfer processing started
	 */
	public static void showThroughputMetrics (long bytesProcessed, long timeStamp)
	{
		long micros = (System.nanoTime () - timeStamp) / 1000;
		float millis = micros / 1.0E3f, kMillis = millis * 1024;
		showThroughputMetrics (bytesProcessed, millis, bytesProcessed / kMillis);
	}
	public static void showThroughputMetrics (long bytesProcessed, float millis, float kpm)
	{
		System.out.print ("Transferred " + bytesProcessed + " bytes, in ");
		System.out.print (millis + "ms, "); System.out.print (kpm + " KB/ms, ");
		System.out.print (USE_BLOCKING_SUPPORT? " with threading": " with raw block copy");
		System.out.println ();
	}


	/**
	 * text version of the stream processing
	 * @param source the source of content to process
	 * @param sink the sink for content to process
	 * @throws IOException for IO errors
	 */
	@Deprecated()
	public static void processTextStream
	(TextSource source, TextSink sink)
	throws Exception
	{
		String block;
		while ((block = source.getString ()) != null) sink.putString (block);
		sink.put (null); // send indication that source is at end
	}


}

