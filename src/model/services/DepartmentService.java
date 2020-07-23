package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {
	
	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	
	public List<Department> findAll(){
		return dao.findAll();
	}
	
	public List<Department> findByName(String name){
		return dao.findByName(name);
	}
	
	public String saveOrUpdate(Department department) {
		if(department.getId() == null) {
			dao.insert(department);
			return "Saved department";
		}
		else {
			dao.update(department);
			return "Updated department";
		}
	}
	
	public void remove(Department department) {
		dao.deleteById(department.getId());
	}
}
