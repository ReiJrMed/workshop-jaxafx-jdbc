package db;

public class DBIntegrityException extends RuntimeException{
	
	private final static long serialVersionUID = 1L;
	
	public DBIntegrityException(String msg) {
		super(msg);
	}

}
