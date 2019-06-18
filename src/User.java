
import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")

public class User {
	private String name;
	private String password;
	private Directory drive;
	private long maxSize;
	private ArrayList<Directory> sharedDirectories=new ArrayList<Directory>();
	private ArrayList<File> sharedFiles=new ArrayList<File>();
	
	public void addSharedDirectory(Directory directory) {
		sharedDirectories.add(directory);
	}
	
	public void addSharedFile(File file) {
		sharedFiles.add(file);
	}
	public User() {
	}
	
	public User(String name,String password, long size) {
		this.name=name;
		this.password=password;
		this.maxSize=size;
	}
	
	public User(String name,String password,long size,Directory drive) {
		this.name=name;
		this.password=password;
		this.drive=drive;
		this.maxSize=size;
	}
	
	public boolean createDrive(Directory drive) {
		if(this.drive!=null) {
			return false;
		}
		this.drive=drive;
		return true;
	}
	
	public Directory getDrive() {
		return this.drive;
	}
	
	public boolean login(String name, String pass) {
		if(this.name.equals(name) && this.password.equals(pass)) {
			return true;
		}
		return false;
	}
	public String getName() {
		return this.name;
	}

	public long getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setDrive(Directory drive) {
		this.drive = drive;
	}

	public ArrayList<Directory> getSharedDirectories() {
		return sharedDirectories;
	}

	public void setSharedDirectories(ArrayList<Directory> sharedDirectories) {
		this.sharedDirectories = sharedDirectories;
	}

	public ArrayList<File> getSharedFiles() {
		return sharedFiles;
	}

	public void setSharedFiles(ArrayList<File> sharedFiles) {
		this.sharedFiles = sharedFiles;
	}
	
	

}
