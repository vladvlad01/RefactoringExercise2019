package refactoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileManager {
	// holds automatically generated file name
	String generatedFileName;
	// display files in File Chooser only with extension .dat;
	public FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	// open file
	RandomFile application;

	// holds true or false if any changes are made for file content
	boolean changesMade = false;
	private EmployeeDetails ed;
	public FileManager(RandomFile application,EmployeeDetails ed)
	{
		this.application = application;
		this.ed = ed;
	}
	public void openFile(boolean change) 
	{
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save
		// old file
		if (ed.file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(null, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile(change);// save file
			} // end if
		} // end if

		int returnVal = fc.showOpenDialog(null);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name,
			// delete this file
			if (ed.file.getName().equals(generatedFileName))
				ed.file.delete();// delete file
			ed.file = newFile;// assign opened file to file
//			// open file for reading
			application.openReadFile(ed.file.getAbsolutePath());
			ed.er.firstRecord();// look for first record
			ed.displayRecords(ed.currentEmployee);
			application.closeReadFile();// close file for reading
		} // end if
	}// end openFile

	// save file
	public void saveFile(boolean change) {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (ed.file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			// if changes has been made to text field offer user to save these
			// changes
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(null, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {
					// save changes if ID field is not empty
					if (!ed.idField.getText().equals("")) {
						// open file for writing
						application.openWriteFile(ed.file.getAbsolutePath());
						// get changes for current Employee
						ed.currentEmployee = ed.getChangedDetails();
						// write changes to file for corresponding Employee
						// record
						application.changeRecords(ed.currentEmployee, ed.er.currentByteStart);
						application.closeWriteFile();// close file for writing
					} // end if
				} // end if
			} // end if

			ed.displayRecords(ed.currentEmployee);
			ed.setEnabled(false);
		} // end else
	}// end saveFile

	// save changes to current Employee
	public void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(null, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {
			// open file for writing
			application.openWriteFile(ed.file.getAbsolutePath());
			// get changes for current Employee
			ed.currentEmployee = ed.getChangedDetails();
			// write changes to file for corresponding Employee record
			application.changeRecords(ed.currentEmployee, ed.er.currentByteStart);
			application.closeWriteFile();// close file for writing
			changesMade = false;// state that all changes has bee saved
		} // end if
		ed.displayRecords(ed.currentEmployee);
		ed.setEnabled(false);
	}// end saveChanges

	// save file as 'save as'
	public void saveFileAs() {
		final JFileChooser fc = new JFileChooser();
		File newFile;
		String defaultFileName = "new_Employee.dat";
		fc.setDialogTitle("Save As");
		// display files only with .dat extension
		fc.setFileFilter(datfilter);
		fc.setApproveButtonText("Save");
		fc.setSelectedFile(new File(defaultFileName));

		int returnVal = fc.showSaveDialog(null);
		// if file has chosen or written, save old file in new file
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// check for file name
			if (!checkFileName(newFile)) {
				// add .dat extension if it was not there
				newFile = new File(newFile.getAbsolutePath() + ".dat");
				// create new file
				application.createFile(newFile.getAbsolutePath());
			} // end id
			else
				// create new file
				application.createFile(newFile.getAbsolutePath());

			try {// try to copy old file to new file
				Files.copy(ed.file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				// if old file name was generated file name, delete it
				if (ed.file.getName().equals(generatedFileName))
					ed.file.delete();// delete file
				ed.file = newFile;// assign new file to file
			} // end try
			catch (IOException e) {
			} // end catch
		} // end if
		changesMade = false;
	}// end saveFileAs
	// allow to save changes to file when exiting the application
		public void exitApp(boolean changes) {
			// if file is not empty allow to save changes
			if (ed.file.length() != 0) {
				if (changesMade) {
					int returnVal = JOptionPane.showOptionDialog(null, "Do you want to save changes?", "Save",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
					// if user chooses to save file, save file
					if (returnVal == JOptionPane.YES_OPTION) {
						saveFile(changes);// save file
						// delete generated file if user saved details to other file
						if (ed.file.getName().equals(generatedFileName))
							ed.file.delete();// delete file
						System.exit(0);// exit application
					} // end if
						// else exit application
					else if (returnVal == JOptionPane.NO_OPTION) {
						// delete generated file if user chooses not to save file
						if (ed.file.getName().equals(generatedFileName))
							ed.file.delete();// delete file
						System.exit(0);// exit application
					} // end else if
				} // end if
				else {
					// delete generated file if user chooses not to save file
					if (ed.file.getName().equals(generatedFileName))
						ed.file.delete();// delete file
					System.exit(0);// exit application
				} // end else
					// else exit application
			} else {
				// delete generated file if user chooses not to save file
				if (ed.file.getName().equals(generatedFileName))
					ed.file.delete();// delete file
				System.exit(0);// exit application
			} // end else
		}// end exitApp
		// create file with generated file name when application is opened
		public void createRandomFile() {
			generatedFileName = getFileName() + ".dat";
			// assign generated file name to file
			ed.file = new File(generatedFileName);
			// create file
			application.createFile(ed.file.getName());
		}// end createRandomFile
		
		// generate 20 character long file name
		private String getFileName() {
			String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
			StringBuilder fileName = new StringBuilder();
			Random rnd = new Random();
			// loop until 20 character long file name is generated
			while (fileName.length() < 20) {
				int index = (int) (rnd.nextFloat() * fileNameChars.length());
				fileName.append(fileNameChars.charAt(index));
			}
			String generatedfileName = fileName.toString();
			return generatedfileName;
		}// end getFileName
		// check if file name has extension .dat
		private boolean checkFileName(File fileName) {
			boolean checkFile = false;
			int length = fileName.toString().length();

			// check if last characters in file name is .dat
			if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
					&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
				checkFile = true;
			return checkFile;
		}// end checkFileName
}
