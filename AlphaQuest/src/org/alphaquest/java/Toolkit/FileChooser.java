package org.alphaquest.java.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser extends JFrame{
	

	private JTextField filename = new JTextField(), dir = new JTextField();

	private String fileName;
	private String filePath;

	public FileChooser() {
		JFileChooser c = new JFileChooser();
		c.requestFocus(true);
		/*c.addChoosableFileFilter(filter);
		c.setAcceptAllFileFilterUsed(false);*/
		//JFileChooser c = new JFileChooser(FileSystemView.getFileSystemView());
		//JFileChooser c = new JFileChooser(new File(getClass().getResource(getClass().getName() + ".class").toString()));
		
		//FileFilter imageFilter = new FileNameExtensionFilter("Image dykes", ImageIO.getReaderFileSuffixes());
	    //c.setFileFilter(imageFilter);
		
		// Demonstrate "Save" dialog:
		int rVal = c.showSaveDialog(FileChooser.this);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			
			fileName = c.getSelectedFile().getName();
			filePath = c.getCurrentDirectory().toString();
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			fileName = "Please Select a file.";
			filePath = "Please Select a file.";
		}
	}

	public String getFileName() {
		return fileName;
	}

	public String getFilePath() {
		return filePath;
	}
}
