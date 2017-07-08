package com.iokays.function;

import java.io.File;

public class LambdaTest {
	public static void main(String[] args) {
		File dir = new File("/an/dir/");
		File[] dirs = dir.listFiles((File f) -> f.isDirectory());
	}
}
