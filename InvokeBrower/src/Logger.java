import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Logger {
	
	
	private String metadata(){
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now(); 
		
		return dtf.format(now);
	}
	
	public void log (List<String> message){
		
		File file = new File("../healthCheckLog.txt");
		try {
			FileWriter fw = new FileWriter(file, true);
			PrintWriter pw = new PrintWriter(fw);
			
			pw.println(metadata());
			
			for (int i = 0; i < message.size(); i++) {
				pw.println("-"+message.get(i));
			}
			
			pw.println();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
