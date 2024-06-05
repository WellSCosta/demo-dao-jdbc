package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {
	
	Connection conn;
	
	public DepartmentDaoJDBC (Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement stm = null;
		
		try {
			String sql = "INSERT INTO department (Name) VALUES (?)";
			stm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			stm.setString(1, obj.getName());
			
			int rows = stm.executeUpdate();
			
			if (rows > 0) {
				ResultSet rs = stm.getGeneratedKeys();
				if (rs.next()) {
					obj.setId(rs.getInt(1));
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
	public void update(Department obj) {
		PreparedStatement stm = null;
		
		try {
			String sql = "UPDATE department SET Name = ? WHERE Id = ?";
			stm = conn.prepareStatement(sql);
			
			stm.setString(1, obj.getName());
			stm.setInt(2, obj.getId());
			
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
			String sql = "DELETE FROM department WHERE Id = ?";
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
	public Department findById(Integer id) {
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT department.* FROM department WHERE department.Id = ?";
			stm = conn.prepareStatement(sql);
			
			stm.setInt(1, id);
			
			rs = stm.executeQuery();
			
			if (rs.next()) {
				Department dep = new Department();
				dep.setName(rs.getString("Name"));
				dep.setId(rs.getInt("Id"));
				
				return dep;
			}
			else {
				return null;
			}
			
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
	public List<Department> findAll() {
		Statement stm = null;
		ResultSet rs = null;
		
		try {
			String sql = "SELECT * FROM department";
			stm = conn.createStatement();
			
			rs = stm.executeQuery(sql);
			
			List<Department> list = new ArrayList<>();
			
			while (rs.next()) {
				Department dep = new Department();
				dep.setName(rs.getString("Name"));
				dep.setId(rs.getInt("Id"));
				
				list.add(dep);
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
