package model.dao;

import db.DB;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

public class DaoFactory {
	//instanciar a implementação de SellerDao o SellerDaoJDBC sem precisar instanciar na classe principal o SellerDaoJDBC
   //dessa forma a classe principal não conhecerá a implementação de SellerDao a SellerDaoJDBC, ou seja, fica mais prático de implementar	
	
	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(DB.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(DB.getConnection());
	} 

}
