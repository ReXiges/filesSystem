import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

public class Server {

    private static Set<User> users = new HashSet<>();
    private static Directory root= new Directory("root","root");
    private static Set<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) throws Exception {
    	
    	readDirectories(root);
    	users=xmlfier.XMLToUsers(root);
        ExecutorService pool = Executors.newFixedThreadPool(500);
        try (ServerSocket listener = new ServerSocket(59001)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }
    private static class Handler implements Runnable {
        private String name="";
        private String pass="";
        private long size =0;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private static User user;
        private static Directory currentDirectory;
        public Handler(Socket socket) {
            this.socket = socket;
        }
        
        

        public void run() {
            try {
            	currentDirectory=root;
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                while(true) {
                	String[] input=in.nextLine().split("-");
                	if(input.length==0) {
                		out.println("INVALIDCOMMAND");
                	}
                	else {
                		switch(input[0]) {
                		case "LOGIN":
                			if (!(logIn())) {
        	                    out.println("WRONGUSER");
        	                }
                			else { out.println("USERLOGGED");}
                			break;		
                		case "NEWUSER":
                			if (!(newUser())) {
        	                    out.println("USERTAKEN");
        	                }
                			else { out.println("USERLOGGED");}
                			break;
                		case "ls":
                			out.println(currentDirectory.contentToSting());
                			out.println("$");
                			break;
                		case "create":
                			if(input.length<2) {
                				out.println("INVALIDCOMMAND");
                			}
                			else {
                				String[] pathnames=input[1].split("/");
                				String fileName=pathnames[pathnames.length-1];
                				Directory holder=currentDirectory;
                				String content="test";
                				for(int i=2;i<input.length;i++) {
            						content+=input[i];
            					}
                				FileWriter writer;
                				if(pathnames.length>1) {
                					String path=pathnames[0];
                    				for(int i=1;i<pathnames.length-1;i++) {
                    					path+="/"+pathnames[i];
                    				}
                    				holder=getDirectoryDinamically(path);
                    				
                				}
                				if(holder!=null) {
                					System.out.println("holder path: "+holder.getPath());
                					if(fileName.contains(".")) {
                						File fileaux=holder.getFile(fileName);
                						if(fileaux==null) {
                							fileaux=new File(holder.getPath()+"/"+fileName);
                							fileaux.createNewFile();
                							holder.addFile(fileaux);
                							writer = new FileWriter(fileaux);
                							writer.write(content);
                							writer.close();
                						}
                						else {
                							out.println("CONFIRM");
                							if(in.nextLine().equals("Y")) {
                								writer = new FileWriter(fileaux,false);
                								writer.write(content);
                    							writer.close();
                							}
                						}
                						out.println("SUCCESS");
                					}
                					else {
                						Directory newdir=holder.getSubDirectory(fileName);
                						if(newdir==null) {
                							createNewDirectory(holder,holder.getPath()+"/"+fileName,fileName);
                						}
                						else {
                							out.println("CONFIRM");
                							if(in.nextLine().equals("Y")) {
                								deleteDirectoryStream(newdir.getPath());
                								holder.removeSubDirectory(fileName);
                								createNewDirectory(holder,holder.getPath()+"/"+fileName,fileName);
                							}
                						}
                						out.println("SUCCESS");
                					}
                				}
                				else {
                					out.println("INVALIDDIR");
                				}
                				
                			}
                			break;
                		case "remove":
                			if(input.length<2) {
                				out.println("INVALIDCOMMAND");
                			}
                			else {
                				for(int i=1;i<input.length;i++) {
                					deleteFile(input[i]);
                				}
                			}
                			out.println("SUCCESS");
                			break;
                		case "mv":
                			if(input.length<3) {
                				out.println("INVALIDCOMMAND");
                			}
                			else {
                				for(int i=1;i<input.length;i++) {
                					deleteFile(input[i]);
                				}
                			}
                			out.println("SUCCESS");
                			break;
                		case "move":
                			if(input.length<2) {
                				out.println("INVALIDCOMMAND");
                			}
                			else {
                				for(int i=1;i<input.length;i++) {
                					deleteFile(input[i]);
                				}
                			}
                			out.println("SUCCESS");
                			break;
                		case "pr":
                			if(input.length<2) {
                				out.println("INVALIDCOMMAND");
                			}
                			else {
                				File file=currentDirectory.getFile(input[1]);
                				if(file!=null && file.isFile()) {
                					out.println(printFileProperties(file));
                        			out.println("$");
                				}
                				else {
                					out.println("INVALIDFILE");
                				}
                			}
                			break;
                		case "cd":
                			Directory newDirectory;
                			if(input.length<2) {
                				out.println("INVALIDCOMMAND");
                			}
                			else if(input[1].equals("..")) {
                				String [] newpath= currentDirectory.getPath().split("/");
                				String path= currentDirectory.getPath();
                				System.out.println(path);
                				if(newpath.length>2){
                					path="root";
                					for (int i=1;i<newpath.length-1;i++) {
                						path+="/"+newpath[i];
                					}
                				}
                				System.out.println(path);
                				newDirectory=Directory.findSubFolder(root,  path);
                				currentDirectory=newDirectory;
            					out.println(currentDirectory.contentToSting());
                    			out.println("$");
                			}
                			else {
                				newDirectory=getDirectoryDinamically(input[1]);
                				if(newDirectory==null) {
                					out.println("DIRNOTFOUND");
                				}
                				else {
                					currentDirectory=newDirectory;
                					out.println(currentDirectory.contentToSting());
                        			out.println("$");
                				}
                			}
                			break;
                		default:
                			out.println("INVALIDCOMMAND");
                			break;
                			
                		}
                	}
                	
                }
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }
        public static Directory getDirectoryDinamically(String input) {
         	Directory newDirectory=Directory.findSubFolder(currentDirectory,  currentDirectory.getName()+"/"+input);
     		if(newDirectory==null) {
     			newDirectory=Directory.findSubFolder(root, "root/"+user.getName()+"/"+input);
     		}
     		return newDirectory;
         }
        
     public boolean logIn() {
    	 out.println("USER");
         name = in.nextLine();
         out.println("PASSWORD");
         pass = in.nextLine();
         synchronized (users) {
             Iterator<User> userI=users.iterator();
             while(userI.hasNext()) {
             	User actual= (User) userI.next();
             	if(actual.login(name, pass)) {
             		user=actual;
             		return true;
             	}
             }
             return false;
         }
     }
     public boolean newUser() {
    	 out.println("USER");
         name = in.nextLine();
         out.println("PASSWORD");
         pass = in.nextLine();
         out.println("SIZE");
         size = Long.parseLong(in.nextLine());
         synchronized (users) {
             Iterator<User> userI=users.iterator();
             while(userI.hasNext()) {
             	User actual= (User) userI.next();
             	if(actual.getName().equals(name)){
             		return false;
             	}
             }
             user=new User(name,pass,size);
             Directory newDrive=createNewDirectory(root,root.getPath()+"/"+name,name);
             root.addSubFolder(newDrive);
             Directory files=createNewDirectory(newDrive,newDrive.getPath()+"/"+"Files","Files");
             newDrive.addSubFolder(files);
             Directory shared=createNewDirectory(newDrive,newDrive.getPath()+"/"+"Shared With Me","Shared With Me");
             newDrive.addSubFolder(shared);
             user.createDrive(newDrive);
             users.add(user);
             usersToXML();
             return true;
         }
     }
     
     static void deleteFile(String pathString) throws IOException {
     	String[] pathnames=pathString.split("/");
 		String fileName=pathnames[pathnames.length-1];
 		Directory holder=currentDirectory;
 		if(pathnames.length>1) {
 			String path=pathnames[0];
 			for(int i=1;i<pathnames.length-1;i++) {
 				path+="/"+pathnames[i];
 			}
 			System.out.println("path: "+path);
 			holder=getDirectoryDinamically(path);
 		}
 		if(holder!=null) {
 			System.out.println("holder path: "+holder.getPath());
 			if(fileName.contains(".")) {
 				File fileaux=holder.getFile(fileName);
 				if(fileaux!=null) {
 					holder.removeFile(fileaux);
 					fileaux.delete();
 				}
 			}
 			else {
 				Directory newdir=holder.getSubDirectory(fileName);
 				deleteDirectoryStream(newdir.getPath());
 				holder.removeSubDirectory(fileName);
 				}
 			}
 		}
     
     
 		
 	} 
    private static Directory createNewDirectory(Directory parent,String path, String name) {
    	new File(path).mkdir();
    	Directory newDir=new Directory(name,path);
    	parent.addSubFolder(newDir);
    	return newDir;
    }
    
    static void deleteDirectoryStream(String pathString) throws IOException {
    	  Path path= Paths.get(pathString);
    	  Files.walk(path)
    	    .sorted(Comparator.reverseOrder())
    	    .map(Path::toFile)
    	    .forEach(File::delete);
    	}
    
    
    
    private static void usersToXML() {
    	try {
			xmlfier.userToXML(users, "users.xml");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void readDirectories(Directory current) {
    	File folder = new File(current.getPath());
    	File[] listOfFiles = folder.listFiles();
    	for (int i = 0; i < listOfFiles.length; i++) {
    		  if (listOfFiles[i].isFile()) {
    		    current.addFile(listOfFiles[i]);
    		  } else if (listOfFiles[i].isDirectory()) {
    			String path=listOfFiles[i].getPath();
    		    Directory aux= new Directory(listOfFiles[i].getName(),path.replaceAll( "\\\\","/"));
    		    readDirectories(aux);
    		    current.addSubFolder(aux);
    		  }
    	}
    }
    
    
    
    public static String printFileProperties(File file) throws IOException {
		String name=file.getName();
		System.out.println(file.getName());
	    BasicFileAttributes attrs=Files.readAttributes(file.toPath(), BasicFileAttributes.class);
	    FileTime timeC = attrs.creationTime();
	    FileTime timeM = attrs.lastModifiedTime();
	    String pattern = "yyyy-MM-dd HH:mm:ss";
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	    String creacion = simpleDateFormat.format( new Date( timeC.toMillis() ) );
	    String modificacion = simpleDateFormat.format( new Date( timeM.toMillis() ) );
	    long size=attrs.size();
	   return "Archivo$Nombre: "+name+"$Creacion: "+creacion+"$Ultima modificacion: "+modificacion+"$Tamanho: "+Long.toString(size);
	}
    
}