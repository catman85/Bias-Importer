
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;


public class Bias {
	
	private static int disOrder;
	private static String presetName;
	private static String uuid;
	
	
	// directories like /(solaris) or \\windows
	//static String PRsource = "C:/Users/Jim/Documents/BIAS_FX/Presets/B606D598-14C7-EB98-AC80-77A1F114935F/CHUGG.Preset";
	//static String PRdest = "C:/Users/Jim/Desktop/CHUGG.Preset";
    static String cate;
    static String preset;
    static String dest;
    static String source;
    //static String presetBFfile = "C:/Users/Jim/Desktop/preset.json";
    //static String type="";
	/*public static void main(String[] args) {
			//insertionBiasAmp();
			//System.out.println(getBiasFxPrName(PRdest));
	}*/
	public Bias(String s,String d,String t){
		
		if(t=="BiasAmp"){
			source=s;
			//checkIfExists(source);
			
			cate=d+"\\cate.idx";
			//checkIfExists(cate);
			
			dest=d+"\\GlobalPresets\\";
			checkIfExists(dest);
			dest=d+"\\GlobalPresets\\"+getBiasAmpUuid(s);
			//System.out.printf("%s\n%s\n%s\n%s\n",source,cate,dest,t);
			
			insertBiasAmp();
			copyDir();
		}else if(t=="BiasFx"){
			source=s;
			checkIfExists(source);
			
			preset=d+"\\Presets\\factory\\preset.json";
			checkIfExists(preset);
			
			dest=d+"\\Presets\\factory\\";
			checkIfExists(dest);
			
			dest=d+"\\Presets\\factory\\"+getBiasFxPrName(s)+".Preset";
			//System.out.printf("%s\n%s\n%s\n%s\n",source,preset,dest,t);
			
			insertBiasFx();
			copyFile();
		}else{
			System.exit(0);
		}
		View.success();
	}	
	
	private static void insertBiasFx(){
        try {
            //finding display order
            try {
    			disOrder=findDisOrder(preset); 
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            File presetBFfileDir=new File(preset);
            
        	disOrder++; 
        	//System.out.printf("%d\n",disOrder);
        	
        	uuid=getBiasFxUuid();
        	//System.out.printf("%s\n",uuid);
        	
        	String string=getBiasFxCode();
        	//System.out.printf("%s %d\n",string,presetBFfileDir.length());
        	
        	
        	byte[] b = string.getBytes(StandardCharsets.UTF_8); // Java 7+ only
        	insert(preset,presetBFfileDir.length()-8,b); //8 for Bias FX 
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static void insertBiasAmp(){
        try {
            //finding display order
            try {
    			disOrder=findDisOrder(cate); 
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            File cateDir=new File(cate);
            
        	disOrder++; 
        	//System.out.printf("%d\n",disOrder);
        	
        	uuid=getBiasAmpUuid(dest);
        	//System.out.printf("%s\n",uuid);
        	
        	String string=getBiasAmpCode();
        	//System.out.printf("%s %d\n",string,cateDir.length());
        	
        	
        	byte[] b = string.getBytes(StandardCharsets.UTF_8); // Java 7+ only
        	insert(cate,cateDir.length()-50,b); //50 for Bias Amp 
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	private static void copyDir(){
		File s=new File(source);
		File d=new File(dest);
		
        //Copying an existing directory!

        // The destination directory to copy to. This directory
        // doesn't exists and will be created during the copy
        // directory process.
        try {
            //
            // Copy source directory into destination directory
            // including its child directories and files. When
            // the destination directory is not exists it will
            // be created. This copy process also preserve the
            // date information of the file.
            //
        	
        	//Check if already exists and delete it
        	Path pathD=Paths.get(dest);
        	if(Files.exists(pathD)){
        		//System.out.println("Replaced");
        		d.delete();
        	}
            FileUtils.copyDirectory(s, d);
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}
	
	private static void copyFile(){
		File s=new File(source);
		File d=new File(dest);
		
		try {
			//copyFileUsingJava7Files(s, d);
			Files.copy(s.toPath(), d.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
    //inserts required code in files
    private static void insert(String filename, long offset, byte[] content) throws IOException, FileNotFoundException {
    	
    	  RandomAccessFile r = new RandomAccessFile(new File(filename), "rw");
    	  RandomAccessFile rtemp = new RandomAccessFile(new File(filename + "~"), "rw");
    	  File f= new File(filename + "~");
    	  long fileSize = r.length();
    	  FileChannel sourceChannel = r.getChannel();
    	  FileChannel targetChannel = rtemp.getChannel();
    	  
    	  
    	  sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
    	  sourceChannel.truncate(offset);
    	  r.seek(offset);
    	  r.write(content);
    	  long newOffset = r.getFilePointer();
    	  targetChannel.position(0L);
    	  sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));
    	  
    	  
    	  sourceChannel.close();
    	  targetChannel.close();
    	  r.close();
    	  rtemp.close();
    	  f.delete();
      	
    	}
    
    //finds the last display order value in a file
    private static int findDisOrder(String path) throws IOException{
    	int order=100;
    	File file = new File(path);
    	
    	@SuppressWarnings("deprecation")
		ReversedLinesFileReader rlfr=new ReversedLinesFileReader(file);
    	String num=rlfr.readLine();

    	do{
		    num = rlfr.readLine();
		}while(!num.contains("order"));
    	
    	//replaces everything except numbers in a string with spaces
    	num = num.replaceAll("[^0-9]+", " "); //the readLine function returns something like   'ay_order" : 51,'   
    	order= Integer.parseInt(num.trim()); // with trim we are getting rid of the spaces
    	rlfr.close();
    	return order;
    }
    
    
    private static String getBiasAmpCode(){
    	String s=",\n\t\t { \n\t\t\t\"display_order\" : "+disOrder+",\n\t\t\t\"id\" : \""+uuid+"\"\n\t\t }";
    	return s;
    }
    private static String getBiasAmpUuid(String dir){ 
		return dir.substring(dir.lastIndexOf('\\')+1,dir.length()); //carefull between format / and \\!!!
    }
    private static String getBiasFxCode(){
	   	String s=",\n{\n\"display_order\" : "+disOrder+",\n\"is_favorite\" : true,\n\"preset_name\" : \""+presetName+"\",\n\"preset_uuid\" : \""+uuid+"\"\n}";     
    	return s;	
    }
    private static String getBiasFxUuid(){
    	//8HEX-4HEX-4HEX-4HEX-12HEX
    	String id = UUID.randomUUID().toString();
    	return id.toUpperCase();
    } 
    private static String getBiasFxPrName(String dir){
    	presetName=dir.substring(dir.lastIndexOf('\\')+1,dir.length()-7);
    	return dir.substring(dir.lastIndexOf('\\')+1,dir.length()-7);	
    }
    
    private static void checkIfExists(String f){
    	try {
			Path pathS=Paths.get(f);
			if(Files.notExists(pathS)){
				//System.err.println("File doesn't exist!");
				View.error();
			}
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			View.error();
			e.printStackTrace();
		}
    
    	
    }
}
