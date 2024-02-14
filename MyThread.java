package work3_01;

import java.util.concurrent.Semaphore;

public class MyThread extends Thread {
	private static final Semaphore lock = new Semaphore(1, true);
	private static boolean aRunning = false;
	private static boolean bRunning = false;
	private static boolean cRunning = false;
	private static boolean bCompletedOnce = false;

	@Override
	public void run() {
		while (true) {
			try {
				lock.acquire();
				// runs block A
				if (!aRunning && !bRunning && !cRunning) {
					aRunning = true; // Mark A as running
					System.out.println("A");
					aRunning = false; // Mark A as not running
					bRunning = true; // Allow B to run
				}
				// runs block B, possibly multiple times
				else if (bRunning && !cRunning) {
					System.out.println("B");
					bCompletedOnce = true; // Mark that B has run at least once
					// Randomly decide if B should run again or move on to C
					if (Math.random() > 0.5) {
						cRunning = true;
						bRunning = false;
					}
				}
				// runs block C
				else if (cRunning && bCompletedOnce) {
					System.out.println("C");
					cRunning = false; // Mark C as not running
					bRunning = false; // Ensure B is marked as not running
					bCompletedOnce = false; // Reset for the next iteration
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}

			// Sleep to prevent potential CPU overutilization
			try {
				Thread.sleep(100);
				lock.release();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}
}
