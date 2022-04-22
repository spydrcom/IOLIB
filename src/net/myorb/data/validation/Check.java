
package net.myorb.data.validation;

import net.myorb.data.abstractions.SimpleXmlHash.Document;
import net.myorb.data.abstractions.SimpleXmlHash.Node;

import net.myorb.gui.components.SimpleScreenIO.Alert;

/**
 * data entry validation for each data type
 * @author Michael Druckman
 */
public interface Check
{
	/**
	 * @param text verify text is valid for type
	 * @param context the original document to provide context
	 * @param staging a hash of the values being set by the validation phase
	 * @param nodeId the sequence number of the node in the document
	 * @return a value adjust to be correct for type
	 * @throws Alert when invalid text
	 */
	String verify (String text, Document context, Node staging, int nodeId) throws Alert;
}
