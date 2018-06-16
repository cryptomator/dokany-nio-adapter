package org.cryptomator.frontend.dokany;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ThreadedReadOnlyMirrorTest {

	public static void main(String[] args) {
		try (Scanner scanner = new Scanner(System.in)) {
			System.out.println("Enter path to the directory you want to mirror:");
			Path p1 = Paths.get(scanner.nextLine());
			System.out.println("Enter path to mount point to use:");
			Path m1 = Paths.get(scanner.nextLine());
			System.out.println("Enter path to the second directory you want to mirror:");
			Path p2 = Paths.get(scanner.nextLine());
			System.out.println("Enter path to second mount point to use:");
			Path m2 = Paths.get(scanner.nextLine());

			MirrorReadOnlyThread one = new MirrorReadOnlyThread(p1, m1);
			MirrorReadOnlyThread two = new MirrorReadOnlyThread(p2, m2);

			Thread t1 = new Thread(one);
			Thread t2 = new Thread(two);

			t1.start();
			t2.start();

			System.in.read();
			String s = scanner.nextLine();
			one.getDokanyDriver().shutdown();

			System.in.read();
			String y = scanner.nextLine();
			two.getDokanyDriver().shutdown();

			System.out.println("Ausgabe: " + s + y);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
