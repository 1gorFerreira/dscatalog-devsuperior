package com.devsuperior.dscatalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	
	@Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE "
			+ "(COALESCE(:categories) IS NULL OR cats IN :categories) AND "
			+ "(:name = '' OR LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%')) )")
	Page<Product> find(List<Category> categories, String name, Pageable pageable);
	
	//Resolvendo o problema da N+1 consultas:
	
	//Primeiro eu faço a consulta find e em seguida mando buscar as categorias apenas dos produtos encontrados na consulta anterior(List de products);
	@Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")//JOIN FETCH busca o produto juntamente com os objetos das categorias; OBS: O JF não funciona com página, só com Lista;
	List<Product> findProductsWithCategories(List<Product> products);
}
