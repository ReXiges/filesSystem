import java.io.File;
import java.util.*;

public class Directory {
	private String name;
	private String path;
	private ArrayList<File> files =new ArrayList<File>();
	private ArrayList<Directory> subfolders =new ArrayList<Directory>();
	
	
	public Directory (String name, String path) {
		this.name=name;
		this.path=path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public ArrayList<File> getFiles() {
		return files;
	}
	
	public File getFile(String fileName) {
		for(File file: files) {
			if(file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}
	
	public void removeFile(String fileName) {
		for(File file: files) {
			if(file.getName().equals(fileName)) {
				files.remove(file);
				return;
			}
		}
	}
	
	public void removeFile(File file) {
		files.remove(file);
	}
	
	public Directory getSubDirectory(String dirName) {
		for(Directory subfolder: subfolders) {
			if(subfolder.getName().equals(dirName)) {
				return subfolder;
			}
		}
		return null;
	}
	
	public void removeSubDirectory(String dirName) {
		for(Directory subfolder: subfolders) {
			if(subfolder.getName().equals(dirName)) {
				subfolders.remove(subfolder);
				return;
			}
		}
	}
	
	public void removeSubDirectory(Directory dir) {
		subfolders.remove(dir);
	}
	
	public void addFile(File file) {
		files.add(file);
	}
	

	public ArrayList<Directory> getSubfolders() {
		return subfolders;
	}
	
	public void addSubFolder(Directory subFolder) {
		subfolders.add(subFolder);
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public void setSubfolders(ArrayList<Directory> subfolders) {
		this.subfolders = subfolders;
	}
	
	
	public static Directory findSubFolder(Directory current,String path) {
		String[] pathnames=path.split("/");
		if( pathnames.length==1 && pathnames[0].equals(current.getName())) {
			return current;
		}
		else if(pathnames.length==0) {
			return null;
		}
		else if(pathnames.length>1){
			String newpath=pathnames[1];
			for (int i=2;i<pathnames.length;i++) {
				newpath+="/"+pathnames[i];
			}
			for(Directory d: current.getSubfolders()) {
				if(d.getName().equals(pathnames[1])) {
					Directory result= findSubFolder(d,newpath);
					if(result!=null) {
						return result;
					}
				}
				
			}
		}
		return null;
	}
	
	public String contentToSting() {
		String result="Currentfolder: "+this.path.split("root/")[1]+"$";
		for(Directory subfolder:this.subfolders) {
			result+="	Directory: "+subfolder.getName()+"$";
		}
		for(File file:this.files) {
			result+="	File: "+file.getName()+"$";
		}
		return result;
	}
}
