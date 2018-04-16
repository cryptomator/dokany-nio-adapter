package com.dokany.java.examples.mirrorfs;

import com.dokany.java.examples.mirrorfs.MirrorFSThread;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ThreadedMirrorFSTest {

	public static void main ( String [] args){
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Enter path to the directory you want to mirror:");
			String s1 = scanner.nextLine();
			Path p = Paths.get(s1);
			System.out.println("Enter drive letter to use:");
			String letter = scanner.nextLine();
			System.out.println("Enter path to the second directory you want to mirror:");
			String s2 = scanner.nextLine();
			Path p2 = Paths.get(s2);
			System.out.println("Enter second drive letter to use:");
			String letter2 = scanner.nextLine();

			com.dokany.java.examples.mirrorfs.MirrorFSThread one = new com.dokany.java.examples.mirrorfs.MirrorFSThread(s1,letter);
			com.dokany.java.examples.mirrorfs.MirrorFSThread two = new MirrorFSThread(s2,letter2);

			Thread t1 = new Thread(one);
			Thread t2 = new Thread(two);

			t1.start();
			t2.start();

			System.in.read();
			String s = scanner.nextLine();
			one.getDriver().shutdown();

			System.in.read();
			String y = scanner.nextLine();
			two.getDriver().shutdown();

			System.out.println("Ausgabe: "+s+y);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
