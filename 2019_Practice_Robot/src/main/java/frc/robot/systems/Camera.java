/**
 * FRC Team 3555
 * 
 * 2019 Practice Robot Code
 */

package frc.robot.systems;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * This class is in charge of managing the camera threads
 * 
 * @author Liam Poppleton
 * @author Caleb Heydon
 */

public class Camera {
	/**
	 * This method starts camera 1
	 */
	public void startCamera1() {
		// Decleare thread
		Thread cameraThread = new Thread(() -> {
			// Do not run camera if it is a simulation
			if (!RobotBase.isReal()) {
				return;
			}

			// Setup camera capture
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
			CvSink cvSink = CameraServer.getInstance().getVideo(camera);
			CvSource outputStream = CameraServer.getInstance().putVideo("Camera 1", 160, 120);

			// Create buffers
			Mat source = new Mat();
			Mat output = new Mat();

			// Filter loop
			while (true) {
				// Get current frame
				long rv = cvSink.grabFrame(source);

				if (rv != 0) {
					// Filter frame
					output.setTo(Scalar.all(0));
					Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
					outputStream.putFrame(output);
				}
			}
		});

		// Set thread properties
		cameraThread.setName("Camera Thread 1");
		cameraThread.setDaemon(true);

		// Start camera thread
		cameraThread.start();
	}

	/**
	 * This method starts camera 2
	 */
	public void startCamera2() {
		// Decleare thread
		Thread cameraThread = new Thread(() -> {
			// Do not run camera if it is a simulation
			if (!RobotBase.isReal()) {
				return;
			}
			
			// Setup camera capture
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(1);
			CvSink cvSink = CameraServer.getInstance().getVideo(camera);
			CvSource outputStream = CameraServer.getInstance().putVideo("Camera 2", 160, 120);

			// Create buffers
			Mat source = new Mat();
			Mat output = new Mat();

			// Filter loop
			while (true) {
				// Get current frame
				long rv = cvSink.grabFrame(source);

				if (rv != 0) {
					// Filter frame
					output.setTo(Scalar.all(0));
					Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
					outputStream.putFrame(output);
				}
			}
		});

		// Set thread properties
		cameraThread.setName("Camera Thread 2");
		cameraThread.setDaemon(true);

		// Start camera thread
		cameraThread.start();
	}
}
