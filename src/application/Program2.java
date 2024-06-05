package application;

import java.util.List;
import java.util.Scanner;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class Program2 {

	public static void main(String[] args) {

		Scanner sc = new Scanner(System.in);
		
		DepartmentDao depDao = DaoFactory.createDepartmentDao();
		
		System.out.println("==== Test 1: department findById ====");
		Department dep = depDao.findById(3);
		System.out.println(dep);
		
		System.out.println("\n==== Test 2: department findAll ====");
		List<Department> list = depDao.findAll();
		for (Department obj : list) {
			System.out.println(obj);
		}
		
		System.out.println("\n==== Test 3: department insert ====");
		Department newDep = new Department(null, "Music");
		depDao.insert(newDep);
		System.out.println("Inserted! New id = " + newDep.getId());
		
		System.out.println("\n==== Test 4: department update ====");
		dep = depDao.findById(1);
		dep.setName("Food");
		depDao.update(dep);
		System.out.println("Update completed!");
		
		System.out.println("\n==== Test 5: department delete ====");
		System.out.print("Enter id for delete test: ");
		int id = sc.nextInt();
		depDao.deleteById(id);
		System.out.println("Delete completed");
		
		sc.close();
	}

}
