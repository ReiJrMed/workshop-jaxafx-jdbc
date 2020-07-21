package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DBException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller seller) {
		PreparedStatement pst = null;
		
		try {
			pst = conn.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);//cada interrogação corresponde a uma coluna que você vai alterar(índice inicia em 1)

			pst.setString(1, seller.getName());
			pst.setString(2, seller.getEmail());
			pst.setDate(3, new Date(seller.getBirthDate().getTime()));
			pst.setDouble(4, seller.getBaseSalary());//Date do java.sql.Date
			pst.setInt(5, seller.getDepartment().getId());
			
			int rowsAffected = pst.executeUpdate();
			
			if(rowsAffected > 0) {
				ResultSet rs = pst.getGeneratedKeys();//nesse caso gera-se uma única coluna com os Ids gerados
				if(rs.next()) {
					seller.setId(rs.getInt(1));//por ter-se gerado uma coluna, usa-se o 1 como número da coluna para pegar o ID
				}
				DB.closeResultSet(rs);
			} else {
				throw new DBException("Unexpected error!! No rows affected!");
			}
			
		} catch(SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeStatement(pst);
		}
	}

	@Override
	public void update(Seller seller) {
		PreparedStatement pst = null;
		try {
						
			pst = conn.prepareStatement("update seller set Name = ?, Email = ?, BirthDate = ?, "
					+ "BaseSalary = ?, DepartmentId = ? where seller.Id = ?");
			//cada interrogação corresponde a uma coluna que você vai alterar(índice inicia em 1)
			
			pst.setDouble(6, seller.getId());
			pst.setString(1, seller.getName());
			pst.setString(2, seller.getEmail());
			pst.setDate(3, new Date(seller.getBirthDate().getTime()));//Date() do java.sql.Date
			pst.setDouble(4, seller.getBaseSalary());
			pst.setInt(5, seller.getDepartment().getId());
									
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
			
			pst = conn.prepareStatement("DELETE FROM seller WHERE Id = ?" );
			//cada interrogação corresponde a uma coluna que você vai alterar(índice inicia em 1)
			
			pst.setInt(1, Id);
			
			pst.executeUpdate();
					
		} catch(SQLException e) {
			throw new DBException(e.getMessage());  
		} finally {
			DB.closeStatement(pst);			
		}
	}

	@Override
	public Seller findById(Integer Id) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = conn.prepareStatement("select seller.*, department.Name as Department from seller inner join department "
					+ "on seller.DepartmentId = department.Id where seller.Id = ?");
			
			pst.setInt(1, Id);
			
			rs = pst.executeQuery();
			
			if(rs.next()) {
				Department dp = instantiateDepartment(rs);
				return instantiateSeller(rs, dp); 
			} else {
			   return null;
			}
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			//não fechar a conexão pois esse objeto pode servir para fazer mais de uma operação
		}			
	}

	private Seller instantiateSeller(ResultSet rs, Department dp) throws SQLException {
		return new Seller(rs.getInt("Id"), rs.getString("Name"),rs.getString("Email"),
				rs.getDate("BirthDate"),rs.getDouble("BaseSalary"), dp);
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		return new Department(rs.getInt("DepartmentId"), rs.getString("Department"));
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = conn.prepareStatement("select seller.*, department.Name as Department from seller inner join department "
					+ "on seller.DepartmentId = department.Id order by seller.Name");
			
		    rs = pst.executeQuery();
			
			List<Seller> seller = new ArrayList<>();
			
			Map<Integer, Department> departments = new HashMap<>();
			
			while(rs.next()) {
				Department dp = departments.get(rs.getInt("DepartmentId"));
				
				if(dp == null) {
					dp = instantiateDepartment(rs);
					departments.put(rs.getInt("DepartmentId"), dp);
				}
								
				seller.add(instantiateSeller(rs, dp)); 
			}
			
			return seller;
			
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			//não fechar a conexão pois esse objeto pode servir para fazer mais de uma operação
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			pst = conn.prepareStatement("select seller.*, department.Name as Department from seller inner join department "
					+ "on seller.DepartmentId = department.Id where seller.DepartmentId = ? order by seller.Name");
			
			pst.setInt(1, department.getId());
			
			rs = pst.executeQuery();
			
			List<Seller> seller = new ArrayList<>();
			
			Map<Integer, Department> departments = new HashMap<>();
			
			while(rs.next()) {
				Department dp = departments.get(rs.getInt("DepartmentId"));
				
				if(dp == null) {
					dp = instantiateDepartment(rs);
					departments.put(rs.getInt("DepartmentId"), dp);
				}
								
				seller.add(instantiateSeller(rs, dp)); 
			}
			
			return seller;
			
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(pst);
			//não fechar a conexão pois esse objeto pode servir para fazer mais de uma operação
		}		
	}
	
}
