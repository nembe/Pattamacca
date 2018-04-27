package nl.brickparking.listener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.brickparking.jackson.ConvertCSVTest;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventListener {
	private static int eventsHandled;
	EventBus eventBus = new EventBus();
	BufferedReader br = null;
	FileReader fr = null;

	@Subscribe
	public void stringEvent(Path newPath) {
		
		eventsHandled++;
		System.out.println(eventsHandled +" EventListener " + newPath);
		try {
			fr = new FileReader("D:\\tmp\\inbound\\" + newPath);
			br = new BufferedReader(fr);
			File f = new File("D:\\tmp\\inbound\\" + newPath);
			if (Files.isWritable(Paths.get("D:\\tmp\\inbound\\")) && f.canWrite() ) {
				ConvertCSVTest.csvReader(br);
			}
			
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
