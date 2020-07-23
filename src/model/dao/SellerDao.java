package model.dao;

import java.util.List;

import model.entities.Department;
import model.entities.Seller;

public interface SellerDao {
	
    void insert(Seller seller);
	
	void update(Seller seller);
	
	void deleteById(Integer Id);
	
	Seller findById(Integer Id);
	
	List<Seller> findAll();
	
	List<Seller> findByDepartment(Department department);
	
	List<Seller> findByName(String name);
	
	List<Seller> findByOptions(String name, Department department);
}
