package game.engine;

public class Timer {
	private long p_start;
	private long p_stopwatchStart;
	private long p_dt;
	
	public Timer() {
		p_start=System.currentTimeMillis();
		p_stopwatchStart=0;
		p_dt=0;
	}
	
	/**
	 * Get delta time from start
	 * @return time in ms
	 */
	public long getElapsed() {
		return System.currentTimeMillis()-p_start;
	}
	
	/**
	 * Get delta time from the last call
	 * @return time in ms
	 */
	public long getTimeDelta() {
		long tmp=p_dt;
		p_dt=getElapsed();
		return p_dt-tmp;
	}
	
	public void rest(int ms) {
		long start=getElapsed();
		while (start+ms>getElapsed()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}
	}
	
	public void resetStopwatch() {
		p_stopwatchStart=getElapsed();
	}
	
	public boolean stopwatch(long ms) {
		if (getElapsed()>p_stopwatchStart+ms) {
			resetStopwatch();
			return true;
		}
		return false;
	}
}