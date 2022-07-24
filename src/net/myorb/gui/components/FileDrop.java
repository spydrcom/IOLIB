
package net.myorb.gui.components;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.Component;

import java.io.File;

import java.util.List;

/**
 * implementation of the Drop Target
 *  for connecting components to files in the OS
 * @author Michael Druckman
 */
public class FileDrop  implements DropTargetListener
{


	public static DataFlavor FILES = DataFlavor.javaFileListFlavor;


	/**
	 * simple drop where all files get same processing
	 */
	public interface IndividualFileProcessor
	{
		/**
		 * @param file the file to process
		 */
		void process (File file);
	}

	/**
	 * @param component related component
	 * @param fileProcessor processing object for files
	 */
	public static void simpleFileDrop (Component component, IndividualFileProcessor fileProcessor)
	{
		new SimpleFileDrop (component, fileProcessor);
	}


	/**
	 * get control of dropped files
	 */
	public interface FileProcessor
	{
		/**
		 * @param files the files to process
		 */
		void process (List<File> files);
	}

	/**
	 * @param component related component
	 * @param fileProcessor processing object for files
	 */
	public FileDrop (Component component, FileProcessor fileProcessor)
	{
		this.fileProcessor = fileProcessor;
		new DropTarget (component, this);
	}


	/* (non-Javadoc)
	 * @see java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	@Override
	public void drop (DropTargetDropEvent evt)
	{
		try
		{
			Transferable transferable = evt.getTransferable ();
			if (transferable.isDataFlavorSupported (FILES))
			{
				drop (evt, transferable);
				return;
			}
		}
		catch (Exception e) {}
		evt.rejectDrop ();
	}

	public void drop
	(DropTargetDropEvent evt, Transferable transferable)
	throws Exception
	{
		evt.acceptDrop (DnDConstants.ACTION_COPY);
		Object dragContents = transferable.getTransferData (FILES);
		@SuppressWarnings("unchecked") List<File> files = (List<File>) dragContents;
		evt.getDropTargetContext ().dropComplete (true);
		fileProcessor.process (files);
	}
	FileProcessor fileProcessor;

	public void dragOver(DropTargetDragEvent dtde) {}
	public void dropActionChanged(DropTargetDragEvent dtde) {}
	public void dragEnter(DropTargetDragEvent dtde) {}
	public void dragExit(DropTargetEvent dte) {}

}


/**
 * most simple form of drop
 */
class SimpleFileDrop
{

	SimpleFileDrop
		(
			Component component,
			FileDrop.IndividualFileProcessor fileProcessor
		)
	{
		connectDrop (component);
		this.fileProcessor = fileProcessor;
	}
	FileDrop.IndividualFileProcessor fileProcessor;

	/**
	 * allow file drop on target
	 * @param component the drop target
	 */
	void connectDrop (Component component)
	{
		drop = new FileDrop (component, (f) -> { process (f); });
	}
	FileDrop drop;


	/**
	 * implementation of FileDrop.FileProcessor
	 * @param files the processing of dropped files
	 */
	void process (List<File> files)
	{
		for (File f : files)
		{
			fileProcessor.process (f);
		}
	}


}

