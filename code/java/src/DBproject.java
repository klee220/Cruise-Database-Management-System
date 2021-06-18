/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.*; //added
import java.time.format.*; //added

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject {
	//reference to physical database connection
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try {
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println("Connection URL: " + url + "\n");

			// obtain a physical connection
			this._connection = DriverManager.getConnection(url, user, passwd);
			System.out.println("Done");
		} catch (Exception e) {
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
			System.out.println("Make sure you started postgres on this machine");
			System.exit(-1);
		}
	}

	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate(String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the update instruction
		stmt.executeUpdate(sql);

		// close the instruction
		stmt.close();
	} //end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult(String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery(query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCol = rsmd.getColumnCount();
		int rowCount = 0;

		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()) {
			if (outputHeader) {
				for (int i = 1; i <= numCol; i++) {
					System.out.print(rsmd.getColumnName(i) + "\t");
				}
				System.out.println();
				outputHeader = false;
			}
			for (int i = 1; i <= numCol; ++i)
				System.out.print(rs.getString(i) + "\t");
			System.out.println();
			++rowCount;
		} //end while
		stmt.close();
		return rowCount;
	}

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each recordinturn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
		//creates a statement object 
		Statement stmt = this._connection.createStatement();

		//issues the query instruction 
		ResultSet rs = stmt.executeQuery(query);

		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		 */
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCol = rsmd.getColumnCount();
		int rowCount = 0;

		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result = new ArrayList<List<String>> ();
		while (rs.next()) {
			List < String > record = new ArrayList < String > ();
			for (int i = 1; i <= numCol; ++i)
				record.add(rs.getString(i));
			result.add(record);
		} //end while 
		stmt.close();
		return result;
	} //end executeQueryAndReturnResult

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery(String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery(query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if (rs.next()) {
			rowCount++;
		} //end while
		stmt.close();
		return rowCount;
	}

	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */

	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement();

		ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup() {
		try {
			if (this._connection != null) {
				this._connection.close();
			} //end if
		} catch (SQLException e) {
			// ignored.
		} //end try
	} //end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println(
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName() +
				" <dbname> <port> <user>");
			return;
		} //end if

		DBproject esql = null;

		try {
			System.out.println("(1)");

			try {
				Class.forName("org.postgresql.Driver");
			} catch (Exception e) {

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Includeinyour library path!");
				e.printStackTrace();
				return;
			}

			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];

			esql = new DBproject(dbname, dbport, user, "");

			boolean keepon = true;
			while (keepon) {
				System.out.print(ANSI_CYAN);
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Ship");
				System.out.println("2. Add Captain");
				System.out.println("3. Add Cruise");
				System.out.println("4. Book Cruise");
				System.out.println("5. List number of available seats for a given Cruise.");
				System.out.println("6. List total number of repairs per Ship in descending order");
				System.out.println("7. Find total number of passengers with a given status");
				System.out.println("8. Add customer");
				System.out.println("9. List cruises and their departure time under a given cost"); //added
				System.out.println("10. < EXIT");
				System.out.print(ANSI_RESET);

				switch (readChoice()) {
					case 1:
						AddShip(esql);
						break;
					case 2:
						AddCaptain(esql);
						break;
					case 3:
						AddCruise(esql);
						break;
					case 4:
						BookCruise(esql);
						break;
					case 5:
						ListNumberOfAvailableSeats(esql);
						break;
					case 6:
						ListsTotalNumberOfRepairsPerShip(esql);
						break;
					case 7:
						FindPassengersCountWithStatus(esql);
						break;
					case 8:
						AddCustomer(esql);
						break;
					case 9:  //added
						ListCruisesandDepartureUnderCost(esql);
						break;
					case 10:
						keepon = false;
						break;
				}

				System.out.println("");
			}
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		} finally {
			try {
				if (esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup();
					System.out.println("Done\n\nBye !");
				} //end if				
			} catch (Exception e) {
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break
				input = Integer.parseInt(in.readLine());
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + "Your input is invalid!" + ANSI_RESET);
				continue;
			} //end try
		} while (true);
		return input;
	} //end readChoice

	public static int readInt(String prompt, int lower, int upper) {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print(prompt);
			try { // read the integer, parse it and break
				input = Integer.parseInt(in.readLine());
				if (input < lower || input > upper) {
					throw new Exception("Invalid integer");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			} //end try
		} while (true);
		return input;
	}

	public static void AddShip(DBproject esql) { //1
		int ID;
		String make;
		String model;
		int age;
		int seats;

		// id
		do {
			System.out.print("\tEnter ship ID: ");
			try {
				// Read input
				ID = Integer.parseInt(in.readLine());

				if (ID < 0) {
					throw new Exception("ID cannot be negative.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Make
		do {
			System.out.print("\tEnter make: ");
			try {
				// Read input
				make = in.readLine();

				if (make.length() > 32) {
					throw new Exception("Input cannot exceed 32 characters.");
				} else if (make.length() == 0) {
					throw new Exception("Input cannot be null.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Model
		do {
			System.out.print("\tEnter model: ");
			try {
				// Read input
				model = in.readLine();

				if (model.length() > 64) {
					throw new Exception("Input cannot exceed 64 characters.");
				} else if (model.length() == 0) {
					throw new Exception("Input cannot be null.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Age
		do {
			System.out.print("\tEnter ship age: ");
			try {
				// Read input
				age = Integer.parseInt(in.readLine());

				if (age < 0) {
					throw new Exception("Age input cannot be negative.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Seats
		do {
			System.out.print("\tEnter the number of seats: ");
			try {
				// Read input
				seats = Integer.parseInt(in.readLine());

				if (seats < 0) {
					throw new Exception("Seats input cannot be negative.");
				} else if (seats > 500) {
					throw new Exception("Seats input cannot be greater than 500.");
				} else if (seats == 0) {
					throw new Exception("Number of seats cannot be 0.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Insert details into database
		try {
			String input = "INSERT INTO Ship(ID, make, model, age, seats) VALUES (" + ID + ", '" + make + "', '" + model + "', " + age + " , " + seats + ")";
			esql.executeUpdate(input);

			System.out.println(ANSI_GREEN + "Details inserted into Ship DB." + ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

	public static void AddCaptain(DBproject esql) { //2
		int id;
		String fullname;
		String nationality;

		// id
		do {
			System.out.print("\tEnter Captain's id: ");
			try {
				// Read input
				id = Integer.parseInt(in.readLine());

				if (id < 0) {
					throw new Exception("Input cannot be negative.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Full name
		do {
			System.out.print("\tEnter Captain's full name: ");
			try {
				// Read input
				fullname = in.readLine();

				if (fullname.length() > 128) {
					throw new Exception("Captain's full name cannot exceed 128 characters.");
				} else if (fullname.length() == 0) {
					throw new Exception("Input cannot be null.");
				}
				//Check if input contains digits
				else if (fullname.matches(".*\\d.*")) {
					throw new Exception("Captain's name should not contain digits.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Nationality
		do {
			System.out.print("\tEnter Captain's nationality: ");
			try {
				//Read input
				nationality = in.readLine();

				if (nationality.length() > 24) {
					throw new Exception("Nationality cannot exceed 24 characters.");
				} else if (nationality.length() == 0) {
					throw new Exception("Input cannot be null.");
				} else if (nationality.matches(".*\\d,*")) {
					throw new Exception("Nationality should not contain digits.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		//Insert details into database
		try {
			String input = "INSERT INTO Captain(id, fullname, nationality) VALUES (" + id + ", '" + fullname + "', '" + nationality + "')";
			esql.executeUpdate(input);

			System.out.println(ANSI_GREEN + "Details inserted into Captain DB." + ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

	public static void AddCruise(DBproject esql) { //3
		int cnum;
		int cost;
		int num_sold;
		int num_stops;
		String actual_departure_date;
		String actual_arrival_date;
		String arrival_port;
		String departure_port;

		// Cruise number
		do {
			System.out.print("\tEnter cruise number: ");
			try {
				// Read input
				cnum = Integer.parseInt(in.readLine());
				if (cnum < 0) {
					throw new Exception("Input cannot be negative.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Cost
		do {
			System.out.print("\tEnter cost: $");
			try {
				//Read input
				cost = Integer.parseInt(in.readLine());

				if (cost < 1) {
					throw new Exception("Cost must be greater than 0.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Number sold
		do {
			System.out.print("\tEnter number of tickets sold: ");
			try {
				//Read input
				num_sold = Integer.parseInt(in.readLine());

				if (num_sold < 0) {
					throw new Exception("Input cannot be negative.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Number of stops
		do {
			System.out.print("\tEnter number of stops: ");
			try {
				//Read input
				num_stops = Integer.parseInt(in.readLine());

				if (num_stops < 0) {
					throw new Exception("Number of stops cannot be negative.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		//Format date
		LocalDate depart_date;
		DateTimeFormatter PARSE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

		// Actual Departure Date
		do {
			System.out.print("\tEnter departure date (yyyy-mm-dd hh:mm): ");
			try {
				//Read input
				actual_departure_date = in.readLine();
				depart_date = LocalDate.parse(actual_departure_date, PARSE_FORMATTER);
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);


		LocalDate arrive_date;

		// Actual arrival date
		do {
			System.out.print("\tEnter arrival date (yyyy-mm-dd hh:mm): ");
			try {
				actual_arrival_date = in.readLine();
				arrive_date = LocalDate.parse(actual_arrival_date, PARSE_FORMATTER);

				//Check that departure date is before arrival date						
				boolean before = depart_date.isBefore(arrive_date);

				//If arrival date is before departure date, throw exception
				if (!before) {
					throw new Exception("Arrival date cannot be earlier than departure date.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Arrival port
		do {
			System.out.print("\tEnter arrival port code: ");
			try {
				// Read input
				arrival_port = in.readLine();

				boolean hasLower = !arrival_port.equals(arrival_port.toUpperCase());

				if (arrival_port.length() != 5) {
					throw new Exception("Port code must be 5 characters.");
				} else if (arrival_port.length() == 0) {
					throw new Exception("Input cannot be null.");
				} else if (arrival_port.matches(".*\\d.*")) {
					throw new Exception("Port code cannot contain digits.");
				} else if (hasLower) {
					throw new Exception("Port code must be in all uppercase characters.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Departure port
		do {
			System.out.print("\tEnter departure port code: ");
			try {
				//Read input
				departure_port = in.readLine();

				boolean hasLower = !departure_port.equals(departure_port.toUpperCase());

				if (departure_port.length() != 5) {
					throw new Exception("Port code must be 5 characters.");
				} else if (departure_port.length() == 0) {
					throw new Exception("Input cannot be null.");
				} else if (departure_port.matches(".*\\d.*")) {
					throw new Exception("Port code cannot contain digits.");
				} else if (hasLower) {
					throw new Exception("Port code must be in all uppercase characters.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		//Insert details into database
		try {
			String input = "INSERT INTO Cruise(cnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_port, departure_port) VALUES (" + cnum + ", " + cost + ", " + num_sold + ", " + num_stops + ", '" + actual_departure_date + "', '" + actual_arrival_date + "', '" + arrival_port + "', '" + departure_port + "')";
			esql.executeUpdate(input);

			System.out.println(ANSI_GREEN + "Details inserted into Cruise DB." + ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

	public static void BookCruise(DBproject esql) { //4
		try {
			// Read input
			int customerID = readInt("\tCustomer ID: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
			int cruiseNumber = readInt("\tCruise number: ", Integer.MIN_VALUE, Integer.MAX_VALUE);

			// Get seat data from database
			String query = "SELECT C.num_sold, S.seats FROM CruiseInfo CI, Cruise C, Ship S WHERE CI.cruise_id = C.cnum AND CI.ship_id = S.id AND CI.cruise_id = %d";
			List<List<String>> result = esql.executeQueryAndReturnResult(String.format(query, cruiseNumber));
			if (result.size() == 0) {
				System.out.println(ANSI_RED + "Cruise number not found" + ANSI_RESET);
				return;
			}

			// Calculate number of seats remaining
			int sold = Integer.parseInt(result.get(0).get(0));
			int seats = Integer.parseInt(result.get(0).get(1));
			int available = seats - sold;

			// If reservation already exists with the customer and cruise number then attempt to get off waitlist
			result = esql.executeQueryAndReturnResult(String.format("SELECT COUNT(*) FROM Reservation WHERE ccid = %d AND cid = %d AND status = 'W'", customerID, cruiseNumber));
			if (Integer.parseInt(result.get(0).get(0)) > 0) {
				if (available > 0) {
					esql.executeUpdate(String.format("UPDATE Reservation SET status='C' WHERE ccid = %d and cid = %d", customerID, cruiseNumber));
					System.out.println(ANSI_GREEN + "Found existing reservation with waitlisted status. Cruise currently has empty seats. Customer's reservation status will be changed from waitlisted to confirmed." + ANSI_RESET);
				} else {
					System.out.println(ANSI_YELLOW + "Cruise is full. Customer will remain on waitlist." + ANSI_RESET);
				}

				return;
			}

			// Reservation doesn't exist. Make a new one
			String status;
			if (available > 0) {
				status = "R";
			} else {
				status = "W";
			}

			// Get next highest ID and insert into database
			int currentID = Integer.parseInt(esql.executeQueryAndReturnResult("SELECT MAX(rnum) FROM Reservation").get(0).get(0));
			esql.executeUpdate(String.format("INSERT INTO Reservation (rnum, ccid, cid, status) VALUES (%d, %d, %d, '%s')", currentID + 1, customerID, cruiseNumber, status));

			System.out.println(ANSI_GREEN + String.format("Reserved customer %d for cruise %d with status %s", customerID, cruiseNumber, status) + ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) { //5
		try {
			String query = "SELECT C.num_sold, S.seats FROM CruiseInfo CI, Cruise C, Ship S, Schedule SCH WHERE CI.cruise_id = C.cnum AND CI.ship_id = S.id AND CI.cruise_id = SCH.cruiseNum AND CI.cruise_id = %d AND SCH.departure_time='%s-%s-%s'";

			// Read input
			int cruiseNumber = readInt("\tCruise number: ", Integer.MIN_VALUE, Integer.MAX_VALUE);
			String month = String.format("%2d", readInt("\tMonth: ", 1, 12)).replace(' ', '0');
			String day = String.format("%2d", readInt("\tDay: ", 1, 31)).replace(' ', '0');
			String year = String.format("%4d", readInt("\tYear: ", 0, 9999)).replace(' ', '0');

			// Try to find cruise with given input parameters
			List<List<String>> result = esql.executeQueryAndReturnResult(String.format(query, cruiseNumber, year, month, day));

			// No result found of size of result is 0
			if (result.size() == 0) {
				System.out.println(ANSI_RED + "No cruise found" + ANSI_RESET);
				return;
			}

			// Result found. Print to user
			int sold = Integer.parseInt(result.get(0).get(0));
			int seats = Integer.parseInt(result.get(0).get(1));
			System.out.println(ANSI_GREEN + String.format("Number of seats available: %d", seats - sold) + ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

	public static void ListsTotalNumberOfRepairsPerShip(DBproject esql) { //6
		try {
			System.out.print(ANSI_GREEN);
			esql.executeQueryAndPrintResult("SELECT R.ship_id, COUNT(*) FROM Repairs R GROUP BY R.ship_id ORDER BY COUNT(*) DESC");
			System.out.print(ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

	public static void FindPassengersCountWithStatus(DBproject esql) { //7
		int cruiseNumber = readInt("\tCruise number: ", Integer.MIN_VALUE, Integer.MAX_VALUE);

		String status;
		do {
			try {
				System.out.print("Select a status (W, C, R): ");
				status = in.readLine().trim().toUpperCase();
				if (status.equals("W") || status.equals("C") || status.equals("R")) {
					break;
				}

				System.out.println(ANSI_RED + "Invalid input" + ANSI_RESET);
			} catch (Exception e) {
				System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
			}
		} while (true);

		try {
			// Findindatabase with given parameter and print to user
			List<List<String>> result = esql.executeQueryAndReturnResult(String.format("SELECT COUNT(*) FROM Reservation R WHERE R.status = '%s' AND R.cid = %s", status, cruiseNumber));
			System.out.println(ANSI_GREEN + String.format("For cruise %s there are %s passengers with the status %s", cruiseNumber, result.get(0).get(0), status) + ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

	public static void AddCustomer(DBproject esql) { //8
		int ID;
		String firstName;
		String lastName;
		String gender;
		String month;
		String day;
		String year;
		String address;
		String phone;
		String zip;

		// ID
		do {
			System.out.print("\tEnter customer ID: ");
			try {
				// Read input
				ID = Integer.parseInt(in.readLine());

				if (ID < 0) {
					throw new Exception("ID cannot be negative.");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// First name
		do {
			System.out.print("\tEnter customer's first name: ");
			try {
				firstName = in.readLine();

				if (firstName.length() > 24) {
					throw new Exception("Input cannot exceed 24 characters.");
				} else if (firstName.length() == 0) {
					throw new Exception("Input cannot be null.");
				} else if (firstName.matches(".*\\d.*")) {
					throw new Exception("First name cannot contain digits.");
				}

				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Last name
		do {
			System.out.print("\tEnter customer's last name: ");
			try {
				lastName = in.readLine();

				if (lastName.length() > 24) {
					throw new Exception("Input cannot exceed 24 characters.");
				} else if (lastName.length() == 0) {
					throw new Exception("Input cannot be null.");
				} else if (lastName.matches(".*\\d.*")) {
					throw new Exception("Last name cannot contain digits.");
				}

				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Gender
		do {
			System.out.print("\tEnter customer's gender (F, M): ");

			try {
				gender = in.readLine().trim().toUpperCase();
				if (!gender.equals("F") && !gender.equals("M")) {
					throw new Exception("Input must be either F or M.");
				}

				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// DOB
		month = String.format("%2d", readInt("\tDOB Month: ", 1, 12)).replace(' ', '0');
		day = String.format("%2d", readInt("\tDOB Day: ", 1, 31)).replace(' ', '0');
		year = String.format("%4d", readInt("\tDOB Year: ", 0, 9999)).replace(' ', '0');

		// Address
		do {
			System.out.print("\tEnter customer's street address: ");
			try {
				address = in.readLine();

				if (address.length() > 256) {
					throw new Exception("Input cannot exceed 256 characters.");
				} else if (address.length() == 0) {
					throw new Exception("Input cannot be null.");
				}

				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Zipcode
		do {
			System.out.print("\tEnter customer's zip code: ");
			try {
				zip = in.readLine();

				if (zip.length() > 10) {
					throw new Exception("Input cannot exceed 10 characters.");
				} else if (zip.length() == 0) {
					throw new Exception("Input cannot be null.");
				} else if (!zip.matches("[0-9]+")) {
					throw new Exception("Input must only contain digits");
				}

				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		// Phone
		do {
			System.out.print("\tEnter customer's phone number (digits only): ");
			try {
				phone = in.readLine();

				if (phone.length() != 10) {
					throw new Exception("Input must be 10 digits long");
				} else if (!phone.matches("[0-9]+")) {
					throw new Exception("Input must only contain digits");
				}
				break;
			} catch (Exception e) {
				System.out.println(ANSI_RED + e + ANSI_RESET);
				continue;
			}
		} while (true);

		try {
			// Get next highest ID and insert into database
			int currentID = Integer.parseInt(esql.executeQueryAndReturnResult("SELECT MAX(id) FROM Customer").get(0).get(0));
			esql.executeUpdate(String.format("INSERT INTO Customer (id, fname, lname, gtype, dob, address, zipcode, phone) VALUES (%d, '%s', '%s', '%s', '%s-%s-%s', '%s', '%s', '%s')", currentID + 1, firstName, lastName, gender, year, month, day, address, zip, phone));

			System.out.println(ANSI_GREEN + "Successfully added new customer" + ANSI_RESET);
		} catch (Exception e) {
			System.err.println(ANSI_RED + e.getMessage() + ANSI_RESET);
		}
	}

   	public static void ListCruisesandDepartureUnderCost(DBproject esql){
      		int input;

		do{
			try{
         			String query = "SELECT c.cnum, s.departure_time, c.cost FROM Cruise c, Schedule s WHERE s.cruiseNum = c.cnum AND c.cost <";

				// Read input
				input = readInt("\tEnter cost: $", Integer.MIN_VALUE, Integer.MAX_VALUE);

				// Cost cannot be 0 
				if (input < 1){
					throw new Exception("Cost must be greater than 0.");
				}
	
				// Add input to query string
         			query += input;
        	 		esql.executeQueryAndPrintResult(query);	
			
				// Count number of results
				List<List<String>> rows = esql.executeQueryAndReturnResult(query);
		      		System.out.println (ANSI_GREEN + "Found  " + rows.size() + " cruise(s) with cost under $" + input + ANSI_RESET);
				break;
			}catch(Exception e){
         			System.err.println (ANSI_RED + e.getMessage() + ANSI_RESET);
			}
		} while (true);
   	}
}
