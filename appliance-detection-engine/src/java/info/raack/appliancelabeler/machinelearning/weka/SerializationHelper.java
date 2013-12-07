package info.raack.appliancelabeler.machinelearning.weka;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationHelper {

	public static ModelStorage read(InputStream stream) throws IOException, ClassNotFoundException {
		ObjectInputStream 	ois;
	    Object		result;
	    
	    if (!(stream instanceof BufferedInputStream))
	      stream = new BufferedInputStream(stream);
	    
	    ois    = new ObjectInputStream(stream);
	    result = ois.readObject();
	    ois.close();
	    
	    return (ModelStorage)result;
	}

	public static void write(ByteArrayOutputStream stream,
			ModelStorage o) throws IOException {
		ObjectOutputStream	oos;
	    	    
	    oos = new ObjectOutputStream(stream);
	    oos.writeObject(o);
	    oos.flush();
	    oos.close();
	}

}
