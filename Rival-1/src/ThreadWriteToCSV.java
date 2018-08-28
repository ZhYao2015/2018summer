import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ThreadWriteToCSV extends Thread {

	private Process p;
	private String csv_filepath;
	
	public ThreadWriteToCSV(Process p,String csv_filepath) {
		this.p=p;
		this.csv_filepath=csv_filepath;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		File file=new File(csv_filepath);
		BufferedReader csv_from=new BufferedReader(new InputStreamReader(p.getInputStream()));
		PrintWriter csv_to;
		try {
			csv_to=new PrintWriter(new FileWriter(file));
			String str;
			while((str=csv_from.readLine())!=null) {
				csv_to.println(str);
			}
			
			csv_to.close();
			csv_from.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
