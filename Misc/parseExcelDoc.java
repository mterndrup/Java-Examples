import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import beans.Appt;

import com.mysql.jdbc.ResultSet;

//Created Jan 30, 2015

public class parseExcelDoc {

	public static void main(String[] args) throws IOException {
		try {
			FileInputStream file = new FileInputStream(
					new File("C:\\file.xlsx"));

			// Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			int newCustomerCount = 0;
			int newApptCount = 0;
			int existingCustomerCount = 0;
			int existingApptCount = 0;
			int skippedCount = 0;
			int noApptCount = 0;
			int errors = 0;
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				// Get first/desired sheet from the workbook
				XSSFSheet sheet = workbook.getSheetAt(i);

				// Iterate through each rows one by one
				Iterator<Row> rowIterator = sheet.iterator();

				// Connect to MySQL database
				String myDriver = "org.gjt.mm.mysql.Driver";
				String myUrl = "jdbc:mysql://000.000.0.00/database";
				Class.forName(myDriver);
				Connection conn = DriverManager.getConnection(myUrl, "root",
						"p@s$w0rdEx@mpLe");
				String query = " insert into client (phoneNumber, emailAddress, fName, lName, streetAddress, city, zipCode)"
						+ " values (?, ?, ?, ?, ?, ?, ?)";
				PreparedStatement preparedStmt = conn.prepareStatement(query,
						Statement.RETURN_GENERATED_KEYS);

				java.sql.ResultSet rs = null;
				Vector<Appt> newAppts = new Vector<Appt>();
				int rowCount = 0;
				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();
					// For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();
					Appt appt = new Appt();

					while (cellIterator.hasNext()) {
						try {
							//....
						} catch (Exception e) {
							errors++;
						}
					}
				}
			}
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}