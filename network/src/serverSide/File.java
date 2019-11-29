package serverSide;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
public class File
{
	private OutputStream output;
	private Path path;
	public File(String path) throws FileNotFoundException
	{
		output = new FileOutputStream(path);
		this.path = Paths.get(path);
	}
	public void write(String str) 
	{
		byte [] b = str.getBytes();
		try {
			output.write(b);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("file write fail!");
		}
	}
	public List<String> read()
	{
		List<String> list = new ArrayList<>();
		Charset charset = StandardCharsets.UTF_8;
		try {
			list = Files.readAllLines(path , charset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("path : "+ path.toString() +" read fail");
		}
		return list;
	}
}
