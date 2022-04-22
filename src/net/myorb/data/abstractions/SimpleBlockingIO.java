
package net.myorb.data.abstractions;

import net.myorb.data.abstractions.SimpleStreamIO.TransferError;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * control of transfers that have independent threads for stream source and sink.
 *  ArrayBlockingQueue is used to keep producer / consumer threads loosely coupled.
 * @param <B> description of a block of data
 * @author Michael Druckman
 */
public abstract class SimpleBlockingIO <B>
{


	/**
	 * get data block
	 * @return a block of data
	 * @throws InterruptedException for thread interruption
	 * @throws TransferError for errors in transfer
	 */
	public abstract B get () throws InterruptedException, TransferError;

	/**
	 * put data block
	 * @param block a block of data
	 * @throws InterruptedException for thread interruption
	 * @throws TransferError for errors in transfer
	 */
	public abstract void put (B block) throws InterruptedException, TransferError;

	/**
	 * determine if transfer has completed
	 * @param block description of a block of data
	 * @return TRUE = transfer completion has been noted
	 */
	public abstract boolean isComplete (B block);

	/**
	 * performs required tasks at point of transfer completion
	 */
	public abstract void complete ();


	/**
	 * data block queue object allocation
	 * @param capacity the maximum count of buffers
	 */
	protected SimpleBlockingIO (int capacity)
	{ queue = new ArrayBlockingQueue <> (capacity); }
	protected ArrayBlockingQueue <B> queue;


	/**
	 * start producer and consumer threads
	 */
	public void startCopy ()
	{
		new Thread (getProducer ()).start ();
		new Thread (getConsumer ()).start ();
	}


	/**
	 * abstraction of data transfer of one block 
	 */
	public interface BlockTransfer
	{
		/**
		 * implement transfer of one block
		 * @return TRUE = transfer completion has been noted
		 * @throws InterruptedException for thread interruption
		 * @throws TransferError for errors in transfer
		 */
		boolean doBlockTransfer () throws InterruptedException, TransferError;
	}


	/**
	 * block transfer loop
	 *  continues until completion or early termination event
	 * @param mechanism the implementation of a single block transfer
	 */
	public void doBlockTransfers (BlockTransfer mechanism)
	{
		try
		{
			boolean transferIsComplete = false;
			do { transferIsComplete = mechanism.doBlockTransfer (); }
			while ( ! transferIsComplete );
		}
		catch (InterruptedException e) { e.printStackTrace (); }
		catch (TransferError e) { e.printStackTrace (); }
	}


	/**
	 * a transfer resulting in a queue push
	 */
	public class BlockPush implements BlockTransfer
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleBlockingIO.BlockTransfer#doBlockTransfer()
		 */
		public boolean doBlockTransfer ()
		throws InterruptedException, TransferError
		{ return copyToQueue (get ()); }

		/**
		 * put into queue
		 * @param block a block descriptor
		 * @return TRUE = transfer completion has been noted
		 * @throws InterruptedException for queue errors
		 */
		public boolean copyToQueue (B block) throws InterruptedException
		{
			boolean
			done = isComplete (block); queue.put (block);
			return done;
		}

	}


	/**
	 * @return a Runnable method for the producer task
	 */
	Runnable getProducer ()
	{
		return new Runnable ()
		{
			public void run ()
			{
				doBlockTransfers (new BlockPush ());
			}
		};
	};


	/**
	 * a transfer resulting from queue pull
	 */
	public class BlockPull implements BlockTransfer
	{

		/* (non-Javadoc)
		 * @see net.myorb.data.abstractions.SimpleBlockingIO.BlockTransfer#doBlockTransfer()
		 */
		public boolean doBlockTransfer ()
		throws InterruptedException, TransferError
		{ return copyfromQueue (queue.take ()); }

		/**
		 * put content to sink
		 * @param block a block descriptor
		 * @return TRUE = transfer completion has been noted
		 * @throws InterruptedException for thread interruption
		 * @throws TransferError for errors in transfer
		 */
		public boolean copyfromQueue (B block)
		throws InterruptedException, TransferError
		{
			boolean
			done = isComplete (block); put (block);
			return done;
		}

	}


	/**
	 * @return a Runnable method for the consumer task
	 */
	Runnable getConsumer ()
	{
		return new Runnable ()
		{
			public void run ()
			{
				doBlockTransfers (new BlockPull ()); complete ();
			}
		};
	};


}

