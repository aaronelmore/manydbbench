package edu.mit.csail;

import org.apache.log4j.Logger;

public class MockWorker extends WorkerThreadBase {

	static Logger log = Logger.getLogger(MockWorker.class.getSimpleName());
	public MockWorker(double onChance, double offChance, boolean onDefault,
			int msInterval, String dbName) {
		super(onChance, offChance, onDefault, msInterval, dbName);
	}

	@Override
	public void openCon() {
		log.info("Opening mock conn " + dbName);
	}

	@Override
	public void closeCon() {
		log.info("Closing mock conn " + dbName);

	}

	@Override
	public void doWork() throws Exception {
		log.info("Doing mock work" + dbName);
	}

}
