
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
	 * get control of dropped files
	 */
	public interface FileProcessor
	{
		/**
		 * @param files the files to process
		 */
		void process (List<File> files);
	}

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
