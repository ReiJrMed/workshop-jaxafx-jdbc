package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DBException;
import db.DBIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{

	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Department department) {
		
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("insert into department (Name) values (?)", Statement.RETURN_GENERATED_KEYS);
			
			pst.setString(1, department.getName());
			
			int rowsAffected = pst.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = pst.getGeneratedKeys();
				if(rs.next()) {
					department.setId(rs.getInt(1));
				}
				DB.closeResultSet(rs);
			} else {
				throw new DBException("No department created!");
			}
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}
		
	}

	@Override
	public void update(Department department) {
		
	PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement("update department set Name = ? where Id = ?");
			
			pst.setString(1, department.getName());
			pst.setInt(2, department.getId());
			
			pst.executeUpdate();			
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}		
	}

	@Override
	public void deleteById(Integer Id) {
		
		PreparedStatement pst = null;
				
		try {
			pst = conn.prepareStatement("delete from department where Id = ?");
			
			pst.setInt(1, Id);
			
			pst.executeUpdate();			
			
		} catch(SQLException e) {
			throw new DBIntegrityException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}		
	}

	@Override
	public Department findById(Integer Id) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = conn.prepareStatement("select department.* from department where Id = ?");
			
			pst.setInt(1, Id);
			
			rs = pst.executeQuery();
			
			if(rs.next()) {
				return instantiateDepartment(rs);
			} else {
				throw new DBException("No department encountered!");
			}			
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
		}
	}

	@Override
	public List<Department> findAll() {
     
		Statement st = null;
		ResultSet rs = null;
		
		try {
			st = conn.createStatement();
			
			rs = st.executeQuery("select department.* from department");
			
			List<Department> departments = new ArrayList<>();
			
			while(rs.next()) {
				departments.add(instantiateDepartment(rs));
			}
			
			return departments;			
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}		
	}
	
	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("Id"), rs.getString("Name"));
	}

}
