package nl.brickparking.watch;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import nl.brickparking.listener.EventListener;

import com.google.common.eventbus.EventBus;

public class HomeWatcher {
	
	 public static void watchDirectoryPath(Path path) {
		 
		   
		   EventBus eventBus = new EventBus();
		   EventListener listener = new EventListener();
           eventBus.register(listener);
	        // Sanity check - Check if path is a folder
	        try {
	            Boolean isFolder = (Boolean) Files.getAttribute(path,
	                    "basic:isDirectory", NOFOLLOW_LINKS);
	            if (!isFolder) {
	                throw new IllegalArgumentException("Path: " + path
	                        + " is not a folder");
	            }
	        } catch (IOException ioe) {
	            // Folder does not exists
	            ioe.printStackTrace();
	        }

	        System.out.println("Watching path: " + path);

	        // We obtain the file system of the Path
	        FileSystem fs = path.getFileSystem();

	        // We create the new WatchService using the new try() block
	        try (WatchService service = fs.newWatchService()) {

	            // We register the path to the service
	            // We watch for creation events
	            path.register(service, ENTRY_CREATE);
//	            path.register(service, ENTRY_MODIFY);
//	            path.register(service, ENTRY_DELETE);

	            // Start the infinite polling loop
	            WatchKey key = null;
	            while (true) {
	                key = service.take();

	                // Dequeueing events
	                Kind<?> kind = null;
	                for (WatchEvent<?> watchEvent : key.pollEvents()) {
	                    // Get the type of the event
	                    kind = watchEvent.kind();
	                    if (OVERFLOW == kind) {
	                        continue; // loop
	                    } else if (ENTRY_CREATE == kind) {
	                    	
	                        // A new Path was created
	                        Path newPath = ((WatchEvent<Path>) watchEvent)
	                                .context();
	                        // Output
	                        System.out.println("New path created: " + newPath);	   
	                       
	                       
	                        eventBus.post(newPath);
	                        key.reset();
	                    } else if (ENTRY_MODIFY == kind) {
	                        // modified
	                        Path newPath = ((WatchEvent<Path>) watchEvent)
	                                .context();
	                        // Output
	                        System.out.println("New path modified: " + newPath);
	                    }
	                }

	                if (!key.reset()) {
	                	System.out.println("key reset existing: ");
	                    break; // loop
	                }
	            }

	        } catch (IOException ioe) {
	            ioe.printStackTrace();
	        } catch (InterruptedException ie) {
	            ie.printStackTrace();
	        }

	    }

	    public static void main(String[] args) throws IOException,
	            InterruptedException {
	        // Folder we are going to watch
	        // Path folder =
	        // Paths.get(System.getProperty("C:\\Users\\Isuru\\Downloads"));
	        File dir = new File("D:\\tmp\\inbound");
	        watchDirectoryPath(dir.toPath());
	    }
	    

}
