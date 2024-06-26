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
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{
	
	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Seller obj) {
		PreparedStatement stm = null;
		try {
			
			String sql = "INSERT INTO seller "
						+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
						+ "Values "
						+ "(?, ?, ?, ?, ?)";
			
			stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			stm.setString(1, obj.getName());
			stm.setString(2, obj.getEmail());
			stm.setDate(3, new Date(obj.getBirthDate().getTime()));
			stm.setDouble(4, obj.getBaseSalary());
			stm.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = stm.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet rs = stm.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected!");
			}
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatment(stm);
		}
	}

	@Override
	public void update(Seller obj) {
		PreparedStatement stm = null;
		try {
			
			String sql = "UPDATE seller "
						+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
						+ "WHERE Id = ?";
			
			stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			stm.setString(1, obj.getName());
			stm.setString(2, obj.getEmail());
			stm.setDate(3, new Date(obj.getBirthDate().getTime()));
			stm.setDouble(4, obj.getBaseSalary());
			stm.setInt(5, obj.getDepartment().getId());
			stm.setInt(6, obj.getId());
			
			stm.executeUpdate();
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatment(stm);
		}
	}

	@Override
	public void deleteById(Integer id) {
		
		PreparedStatement stm = null;
		try {
			
			String sql = "DELETE FROM seller WHERE Id = ?";
			
			stm = conn.prepareStatement(sql);
			stm.setInt(1, id);
			stm.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatment(stm);
		}
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			
			String sql = "SELECT seller.*, department.Name as DepName "
						+ "FROM seller INNER JOIN department "
						+ "ON seller.DepartmentId = department.Id "
						+ "WHERE seller.Id = ?";
			
			stm = conn.prepareStatement(sql);
			stm.setInt(1, id);
			rs = stm.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller obj = instatiateSeller(rs, dep);
				return obj;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatment(stm);
			DB.closeResultSet(rs);
		}
		
		
	}

	private Seller instatiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller obj = new Seller();
		obj.setId(rs.getInt("Id"));
		obj.setName(rs.getString("Name"));
		obj.setEmail(rs.getString("Email"));
		obj.setBaseSalary(rs.getDouble("BaseSalary"));
		obj.setBirthDate(rs.getDate("BirthDate"));
		obj.setDepartment(dep);
		return obj;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			
			String sql = "SELECT seller.*,department.Name as DepName "
						+ "FROM seller INNER JOIN department "
						+ "ON seller.DepartmentId = department.Id "
						+ "ORDER BY Name";
			
			stm = conn.prepareStatement(sql);
			rs = stm.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller obj = instatiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatment(stm);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			
			String sql = "SELECT seller.*,department.Name as DepName "
						+ "FROM seller INNER JOIN department "
						+ "ON seller.DepartmentId = department.Id "
						+ "WHERE DepartmentId = ? "
						+ "ORDER BY Name";
			
			stm = conn.prepareStatement(sql);
			stm.setInt(1, department.getId());
			rs = stm.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller obj = instatiateSeller(rs, dep);
				list.add(obj);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatment(stm);
			DB.closeResultSet(rs);
		}
	}
	
}
