package edu.mit.csail;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class PostgresWorker extends WorkerThreadBase {

	static Logger log = Logger.getLogger(PostgresWorker.class.getSimpleName());
	private String connString;
	private static String READSQL = "select * from usertable where YCSB_KEY=?";
	private PreparedStatement readst;
	private ResultSet rs = null;
	
	public PostgresWorker(double onChance, double offChance, boolean onDefault,
			int msInterval, String dbName) {
		super(onChance, offChance, onDefault, msInterval, dbName);

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			log.error("Cannot find pg driver", e);
			throw new RuntimeException(e);
		}
		port = "5432";
		connString = String.format("jdbc:postgresql://%s:%s/%s", host,port,dbName);
	}

	@Override
	public void openCon() {
		try {
			conn = DriverManager.getConnection(connString,
					 user,
					 password);
			readst = conn.prepareStatement(READSQL);
		} catch (SQLException e) {
			log.error("Error Openning Connection");
			stopWithFailure(e.getMessage());
		}
	}

	@Override
	public void closeCon() {
		try{
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			log.error("Error closing Connection");
			stopWithFailure(e.getMessage());
		} finally {
			conn = null;
		}
	}

	@Override
	public void doWork() throws Exception {
		log.debug(String.format("(%s) doWork",dbName));
		if (readst == null){
			stopWithFailure("Unexpected null prepared statement");
			return;
		}
		readst.clearParameters();
		readst.setInt(1, 1);
		rs = readst.executeQuery();
		while(rs.next()){
			rs.getString(1);
		}

	}

}
