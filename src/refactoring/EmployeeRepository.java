package refactoring;

import java.awt.Color;
import java.io.File;
import java.util.Vector;

import javax.swing.JOptionPane;

public class EmployeeRepository {
	public RandomFile application;
	// hold object start position in file
	public long currentByteStart = 0;
	private EmployeeDetails ed;
	public EmployeeRepository(RandomFile application,EmployeeDetails ed) {
		this.application = application;
		this.ed = ed;
	}

	// create vector of vectors with all Employee details
	public Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();// look for first record
		firstId = ed.currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(ed.currentEmployee.getEmployeeId()));
			empDetails.addElement(ed.currentEmployee.getPps());
			empDetails.addElement(ed.currentEmployee.getSurname());
			empDetails.addElement(ed.currentEmployee.getFirstName());
			empDetails.addElement(new Character(ed.currentEmployee.getGender()));
			empDetails.addElement(ed.currentEmployee.getDepartment());
			empDetails.addElement(new Double(ed.currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(ed.currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();// look for next record
		} while (firstId != ed.currentEmployee.getEmployeeId());// end do - while
		currentByteStart = byteStart;

		return allEmployee;
	}// end getAllEmployees

	// check if any of records in file is active - ID is not 0
	public boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		// open file for reading
		application.openReadFile(ed.file.getAbsolutePath());
		// check if any of records in file is active - ID is not 0
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();// close file for reading
		// if no records found clear all text fields and display message
		return someoneToDisplay;
	}// end isSomeoneToDisplay
		// get next free ID from Employees in the file

	public int getNextFreeId() {
		int nextFreeId = 0;
		// if file is empty or all records are empty start with ID 1 else look
		// for last active record
		if (ed.file.length() == 0 || !isSomeoneToDisplay())
			nextFreeId++;
		else {
			lastRecord();// look for last active record
			// add 1 to last active records ID to get next ID
			nextFreeId = ed.currentEmployee.getEmployeeId() + 1;
		}
		return nextFreeId;
	}// end getNextFreeId

	// find byte start in file for first active record
	public void firstRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(ed.file.getAbsolutePath());
			// get byte start in file for first record
			currentByteStart = application.getFirst();
			// assign current Employee to first record in file
			ed.currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();// close file for reading
			// if first record is inactive look for next record
			if (ed.currentEmployee.getEmployeeId() == 0)
				nextRecord();// look for next record
		} else
			ed.clearFields();
	}// end firstRecord

	// find byte start in file for previous active record
	public void previousRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(ed.file.getAbsolutePath());
			// get byte start in file for previous record
			currentByteStart = application.getPrevious(currentByteStart);
			// assign current Employee to previous record in file
			ed.currentEmployee = application.readRecords(currentByteStart);
			// loop to previous record until Employee is active - ID is not 0
			while (ed.currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for previous record
				currentByteStart = application.getPrevious(currentByteStart);
				// assign current Employee to previous record in file
				ed.currentEmployee = application.readRecords(currentByteStart);
			} // end while
			application.closeReadFile();// close file for reading
		} else
			ed.clearFields();
	}// end previousRecord

	// find byte start in file for next active record
	public void nextRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(ed.file.getAbsolutePath());
			// get byte start in file for next record
			currentByteStart = application.getNext(currentByteStart);
			// assign current Employee to record in file
			ed.currentEmployee = application.readRecords(currentByteStart);
			// loop to previous next until Employee is active - ID is not 0
			while (ed.currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for next record
				currentByteStart = application.getNext(currentByteStart);
				// assign current Employee to next record in file
				ed.currentEmployee = application.readRecords(currentByteStart);
			} // end while
			application.closeReadFile();// close file for reading
		} else
			ed.clearFields();
	}// end nextRecord

	// find byte start in file for last active record
	public void lastRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(ed.file.getAbsolutePath());
			// get byte start in file for last record
			currentByteStart = application.getLast();
			// assign current Employee to first record in file
			ed.currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();// close file for reading
			// if last record is inactive look for previous record
			if (ed.currentEmployee.getEmployeeId() == 0)
				previousRecord();// look for previous record
		} else
			ed.clearFields();
	}// end lastRecord

	// search Employee by ID
	public void searchEmployeeById() {
		boolean found = false;

		try {// try to read correct correct from input
				// if any active Employee record search for ID else do nothing
			if (isSomeoneToDisplay()) {
				firstRecord();// look for first record
				int firstId = ed.currentEmployee.getEmployeeId();
				// if ID to search is already displayed do nothing else loop
				// through records
				if (ed.searchByIdField.getText().trim().equals(ed.idField.getText().trim()))
					found = true;
				else if (ed.searchByIdField.getText().trim().equals(Integer.toString(ed.currentEmployee.getEmployeeId()))) {
					found = true;
					ed.displayRecords(ed.currentEmployee);
				} // end else if
				else {
					nextRecord();// look for next record
					// loop until Employee found or until all Employees have
					// been checked
					while (firstId != ed.currentEmployee.getEmployeeId()) {
						// if found break from loop and display Employee details
						// else look for next record
						if (Integer.parseInt(ed.searchByIdField.getText().trim()) == ed.currentEmployee.getEmployeeId()) {
							found = true;
							ed.displayRecords(ed.currentEmployee);
							break;
						} else
							nextRecord();// look for next record
					} // end while
				} // end else
					// if Employee not found display message
				if (!found)
					JOptionPane.showMessageDialog(null, "Employee not found!");
			} else
				ed.clearFields();
		} // end try
		catch (NumberFormatException e) {
			ed.searchByIdField.setBackground(new Color(255, 150, 150));
			JOptionPane.showMessageDialog(null, "Wrong ID format!");
		} // end catch
		ed.searchByIdField.setBackground(Color.WHITE);
		ed.searchByIdField.setText("");
	}// end searchEmployeeByID

	// search Employee by surname
	public void searchEmployeeBySurname() {
		boolean found = false;
		// if any active Employee record search for ID else do nothing
		if (isSomeoneToDisplay()) {
			firstRecord();// look for first record
			String firstSurname = ed.currentEmployee.getSurname().trim();
			// if ID to search is already displayed do nothing else loop through
			// records
			if (ed.searchBySurnameField.getText().trim().equalsIgnoreCase(ed.surnameField.getText().trim()))
				found = true;
			else if (ed.searchBySurnameField.getText().trim().equalsIgnoreCase(ed.currentEmployee.getSurname().trim())) {
				found = true;
				ed.displayRecords(ed.currentEmployee);
			} // end else if
			else {
				nextRecord();// look for next record
				// loop until Employee found or until all Employees have been
				// checked
				while (!firstSurname.trim().equalsIgnoreCase(ed.currentEmployee.getSurname().trim())) {
					// if found break from loop and display Employee details
					// else look for next record
					if (ed.searchBySurnameField.getText().trim().equalsIgnoreCase(ed.currentEmployee.getSurname().trim())) {
						found = true;
						ed.displayRecords(ed.currentEmployee);
						break;
					} // end if
					else
						nextRecord();// look for next record
				} // end while
			} // end else
				// if Employee not found display message
			if (!found)
				JOptionPane.showMessageDialog(null, "Employee not found!");
		} else
			ed.clearFields();
		ed.searchBySurnameField.setText("");
	}// end searchEmployeeBySurname
		// add Employee object to fail

	public void addRecord(Employee newEmployee) {
		// open file for writing
		application.openWriteFile(ed.file.getAbsolutePath());
		// write into a file
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();// close file for writing
	}// end addRecord
		// delete (make inactive - empty) record from file

	public void deleteRecord() {
		if (isSomeoneToDisplay()) {// if any active record in file display
										// message and delete record
			int returnVal = JOptionPane.showOptionDialog(null, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if answer yes delete (make inactive - empty) record
			if (returnVal == JOptionPane.YES_OPTION) {
				// open file for writing
				application.openWriteFile(ed.file.getAbsolutePath());
				// delete (make inactive - empty) record in file proper position
				application.deleteRecords(currentByteStart);
				application.closeWriteFile();// close file for writing
				// if any active record in file display next record
				if (isSomeoneToDisplay()) {
					nextRecord();// look for next record
					ed.displayRecords(ed.currentEmployee);
				} // end if
			} // end if
		} else
			ed.clearFields();
	}// end deleteDecord
		// check for correct PPS format and look if PPS already in use

	public boolean correctPps(String pps) {
		boolean ppsExist = false;
		// check for correct PPS format based on assignment description
		if (pps.length() == 8 || pps.length() == 9) {
			if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1)) && Character.isDigit(pps.charAt(2))
					&& Character.isDigit(pps.charAt(3)) && Character.isDigit(pps.charAt(4))
					&& Character.isDigit(pps.charAt(5)) && Character.isDigit(pps.charAt(6))
					&& Character.isLetter(pps.charAt(7)) && (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				// open file for reading
				application.openReadFile(ed.file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = application.isPpsExist(pps, currentByteStart);
				application.closeReadFile();// close file for reading
			} // end if
			else
				ppsExist = true;
		} // end if
		else
			ppsExist = true;

		return ppsExist;
	}// end correctPPS
}
