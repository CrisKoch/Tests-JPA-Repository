package com.iftm.client.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iftm.client.entities.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

	@Query("SELECT DISTINCT obj FROM Client obj WHERE " + "obj.income >= :income")
	Page<Client> findByIncome(Double income, Pageable pageable);

	@Query("SELECT obj FROM Client obj WHERE UPPER(obj.name) = UPPER(?1)")
	List<Client> findByFirstnameIgnoreCase(String name);

	/*@Query("SELECT obj FROM Client obj WHERE obj.birthday > ?1")
	List<Client> findByBirthDateStartDateAfter(Instant specific_date);*/

	@Query("SELECT obj FROM Client obj WHERE obj.children != 0")
	List<Client> findByChildrenNotLike0(int children);
	
}
