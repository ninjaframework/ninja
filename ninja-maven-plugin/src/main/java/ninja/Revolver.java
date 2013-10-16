package ninja;
//package com.raba;
//
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//
//public class Revolver {
//
//	Process process0;
//
//	Process process1;
//
//	int currentChamber = 0;
//
//	// Main class that will be used to load...
//	private Class klass;
//
//	public Revolver(Class klass) {
//		this.klass = klass;
//
//		// initial startup
//		try {
//			process0 = exec(klass);
//			process1 = exec(klass);
//
//			currentChamber = 0;
//
//			// start first server
//			BufferedOutputStream buo = new BufferedOutputStream(
//					process0.getOutputStream());
//
//			buo.write(NServer.START_COMMAND.getBytes());
//			buo.flush();
//			buo.close();
//
//		} catch (IOException | InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//
//		}
//
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//			@Override
//			public void run() {
//				System.out.println("Inside Add Shutdown Hook");
//				process0.destroy();
//				process1.destroy();
//				
//			}
//		});
//		System.out.println("Shut Down Hook Attached.");
//
//	}
//
//	public synchronized void pullTheTrigger() {
//
//		try {
//
//			System.out.println("current chamber: " + currentChamber);
//
//			if (currentChamber == 0) {
//
//				if (process0 != null) {
//
//					process0.destroy();
//					process0.waitFor();
//
//				}
//				BufferedOutputStream buo = new BufferedOutputStream(
//						process1.getOutputStream());
//
//				buo.write(NServer.START_COMMAND.getBytes());
//				buo.flush();
//				buo.close();
//
//				process0 = exec(klass);
//
//				currentChamber = 1;
//
//			} else if (currentChamber == 1) {
//
//				if (process1 != null) {
//
//					process1.destroy();
//
//					process1.waitFor();
//
//				}
//
//				BufferedOutputStream buo = new BufferedOutputStream(
//						process0.getOutputStream());
//
//				buo.write(NServer.START_COMMAND.getBytes());
//				buo.flush();
//				buo.close();
//
//				process1 = exec(klass);
//
//				currentChamber = 0;
//
//			}
//
//		} catch (InterruptedException | IOException e) {
//			e.printStackTrace();
//		}
//
//		System.out.println("trigger pulled!");
//
//	}
//
//	public static Process exec(Class klass) throws IOException,
//			InterruptedException {
//		String javaHome = System.getProperty("java.home");
//		String javaBin = javaHome + File.separator + "bin" + File.separator
//				+ "java";
//		String classpath = System.getProperty("java.class.path");
//		String className = klass.getCanonicalName();
//
//		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath,
//				className);
//
//		builder.redirectErrorStream(true);
//
//		Process process = builder.start();
//
//		// StreamGobbler errorGobbler = new StreamGobbler(
//		// process.getErrorStream(), "ERROR");
//		//
//		// // any output?
//		StreamGobbler outputGobbler = new StreamGobbler(
//				process.getInputStream(), "OUTPUT");
//		//
//		// // start gobblers
//		outputGobbler.start();
//		// errorGobbler.start();
//
//		return process;
//	}
//
//	private static class StreamGobbler extends Thread {
//		InputStream is;
//		String type;
//
//		private StreamGobbler(InputStream is, String type) {
//			this.is = is;
//			this.type = type;
//		}
//
//		@Override
//		public void run() {
//			try {
//				InputStreamReader isr = new InputStreamReader(is);
//				BufferedReader br = new BufferedReader(isr);
//				String line = null;
//				while ((line = br.readLine()) != null)
//					System.out.println(type + "> " + line);
//			} catch (IOException ioe) {
//				ioe.printStackTrace();
//			}
//		}
//	}
//
//}
