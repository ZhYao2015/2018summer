import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class RivalCli {
	
	public static void main(String args[]) {
		Options opt=new Options();
//		opt.addOption("data",true,"datasets for query.");
//		opt.addOption("query",true,"the sparql-query to be executed.");
		Option opt_ontology=Option.builder("o")
				.longOpt("ontology")
				.desc("ontology for query")
				.hasArg()
				.argName("FILE")
				.build();
		Option opt_query=Option.builder("q")
				.longOpt("query")
				.desc("the sparql-query to be executed.")
				.hasArg()
				.argName("FILE")
				.build();
		opt.addOption(opt_ontology);
		opt.addOption(opt_query);
		
		CommandLineParser parser=new DefaultParser();
		//throw exceptions
		try {
			CommandLine cmd=parser.parse(opt, args);
			
			String ontology_path=cmd.getOptionValue("ontology");
			String query_path=cmd.getOptionValue("query");
			Path base=Paths.get(System.getProperty("user.dir"));
			String time=Long.toString(System.nanoTime());
			
			Path models_dir=Files.createTempDirectory(base,time);
			Path csvs_dir=Files.createTempDirectory(base, "csvs_"+time);
			
			int limit=10;
			String path_wolpertinger="wolpertinger/wolpertinger-reasoner/target/wolpertinger-reasoner-1.0.jar";
			String list_wolpertinger[]= {"java","-jar",path_wolpertinger,"--abox="+time, ontology_path,"--model=10"};
			ProcessBuilder pb_wolpertinger=new ProcessBuilder(list_wolpertinger);
			Process p_wolpertinger=pb_wolpertinger.start();
			p_wolpertinger.waitFor();
			
			String path_arq="apache-jena-3.6.0/bin/arq";
			for(int i=0;i<2;i++) {
				String model_file=time+"/"+"model"+Integer.toString(i+1)+".owl";
				String list_arq[]={"sh",path_arq,"--query="+query_path,"--data="+model_file,"--results=CSV"};
				ProcessBuilder pb_arq=new ProcessBuilder(list_arq);
				Process p_arq=pb_arq.start();
				
				ThreadWriteToCSV thread=new ThreadWriteToCSV(p_arq,csvs_dir.toString()+"/"+"answer"+Integer.toString(i)+".csv");
				thread.start();
				p_arq.waitFor();
			}
			
			//environment or full python path
			//ProcessBuilder with Python
			String list_python[]= {"/home/ushigo/anaconda2/bin/python2.7","intersect.py",csvs_dir.toString()};
			
			ProcessBuilder pb_intersection=new ProcessBuilder(list_python);
			Process p_intersection=pb_intersection.start();
			
			BufferedReader br=new BufferedReader(new InputStreamReader(p_intersection.getInputStream()));
			String str;
			while((str=br.readLine())!=null) {
				System.out.println(str);
			}
			
			br.close();
			
			p_intersection.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
