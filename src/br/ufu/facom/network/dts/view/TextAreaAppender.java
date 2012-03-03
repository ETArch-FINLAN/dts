package br.ufu.facom.network.dts.view;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import javax.swing.*;

public class TextAreaAppender extends WriterAppender {
	
	static private JTextArea jTextArea = null;
	
	/** Set the target JTextArea for the logging information to appear. */
	static public void setTextArea(JTextArea jTextArea) {
		TextAreaAppender.jTextArea = jTextArea;
	}
	/**
	 * Format and then append the loggingEvent to the stored
	 * JTextArea.
	 */
	public void append(LoggingEvent loggingEvent) {
		final String message = this.layout.format(loggingEvent);

		// Append formatted message to textarea using the Swing Thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(jTextArea != null)
					jTextArea.append(message);
			}
		});
	}
}