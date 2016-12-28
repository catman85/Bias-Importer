
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

//requires some .jar files (commons.io) to be added
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;


public class Bias {
	
	private static int disOrder;
	private static String presetName;
	private static String uuid;
	
    static String cate;
    static String preset;
    static String dest;
    static String source;

	public Bias(String s,String d,String t){
		
		if(t=="BiasAmp"){
			source=s;
			checkIfExists(source);
			
			cate=d+"\\cate.idx";
			checkIfExists(cate);
			
			dest=d+"\\GlobalPresets\\";
			checkIfExists(dest);
			dest=d+"\\GlobalPresets\\"+getBiasAmpUuid(s);
			
			//inserts code in cate.idx
			insertBiasAmp();
			//copies the Amp Model to GlobalPresets
			copyDir();
		}else if(t=="BiasFx"){
			source=s;
			checkIfExists(source);
			
			preset=d+"\\Presets\\factory\\preset.json";
			checkIfExists(preset);
			
			dest=d+"\\Presets\\factory\\";
			checkIfExists(dest);
			dest=d+"\\Presets\\factory\\"+getBiasFxPrName(s)+".Preset";
			
			//inserts code in \Presets\factory\preset.json  
			insertBiasFx();
			//copies the ".Preset" file in \Presets\factory\
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
    			disOrder++; 
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            File presetBFfileDir=new File(preset);
            
            //Randomly generating a UUID for the Bias FX patch
        	uuid=getBiasFxUuid();
        	
        	//generating the code to insert inside the preset.JSON file
        	String string=getBiasFxCode();
        	
        	byte[] b = string.getBytes(StandardCharsets.UTF_8); // Java 7+ only
        	insert(preset,presetBFfileDir.length()-8,b); //8 for Bias FX 
        	
        } catch (IOException e) {
        	View.error();
            e.printStackTrace();
        }
	}
	
	private static void insertBiasAmp(){
        try {
            //finding display order
            try {
    			disOrder=findDisOrder(cate); 
    			disOrder++;//adding +1 for the new preset
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            
            File cateDir=new File(cate);
            
        	//getting the UUID of the Bias Amp Model
        	uuid=getBiasAmpUuid(dest);
        	
        	//generating the required code we need to insert inside cate.idx
        	String string=getBiasAmpCode();
        	
        	byte[] b = string.getBytes(StandardCharsets.UTF_8); // Java 7+ only
        	insert(cate,cateDir.length()-50,b); //50 for Bias Amp 
        	
        } catch (IOException e) {
        	View.error();
            e.printStackTrace();
        }
	}
	
	//copies the Bias Amp folder to GlobalPresets
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
        	
        	Path pathD=Paths.get(dest);
        	if(Files.exists(pathD)){
        		//If the file already exists we replace it with a new one.
        		d.delete();
        	}
            FileUtils.copyDirectory(s, d);
        	
        } catch (IOException e) {
        	View.error();
            e.printStackTrace();
        }
		
	}
	
	//copies the .Preset file
	private static void copyFile(){
		File s=new File(source);
		File d=new File(dest);
		
		try {
			Files.copy(s.toPath(), d.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			View.error();
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
    	//format: 8HEX-4HEX-4HEX-4HEX-12HEX
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
				View.error();
			}
		} catch (InvalidPathException e) {
			View.error();
			e.printStackTrace();
		}
    
    	
    }
}
