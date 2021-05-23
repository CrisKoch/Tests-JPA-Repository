package com.iftm.client.tests.repositories;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.tests.factory.ClientFactory;

@DataJpaTest
public class ClientRepositoryTests {
	
	@Autowired
	private ClientRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalClients;
	private long countClientByIncome;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = Long.MAX_VALUE;
		countTotalClients = 12L;
		countClientByIncome = 5L;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		
		repository.deleteById(existingId);
		
		Optional<Client> result = repository.findById(existingId);
		
		Assertions.assertFalse(result.isPresent());
	
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
			repository.deleteById(nonExistingId);
		});
		
		
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		
		Client client = ClientFactory.createClient();
		client.setId(null);
		
		client = repository.save(client);
		Optional<Client> result = repository.findById(client.getId());
		
		Assertions.assertNotNull(client.getId());
		Assertions.assertEquals(countTotalClients + 1, client.getId());
		Assertions.assertTrue(result.isPresent());
		Assertions.assertSame(result.get(), client );
		
	}
	
	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGreaterThanOrEqualsToValue() {
		Double income = 4000.0;
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<Client> result = repository.findByIncome(income, pageRequest);
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());
	}
	
	
	@Test
	public void findByNameShouldReturnClientWhenClientNameIsEqualToName() {
		Client client = ClientFactory.createClient();
		client.setName("Valeria");
		
		List<Client> result = repository.findByFirstnameIgnoreCase(client.getName());
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertSame(client, result);
	}
	
	@Test
	public void findByNameShouldReturnClientWhenClientNameIsEqualToNameIgnoreCase() {
		Client client = ClientFactory.createClient();
		client.setName("vanessa");
		
		List<Client> result = repository.findByFirstnameIgnoreCase(client.getName());
		
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertSame(client, result);
		
	}
	@Test
	public void findBynameShouldReturnAllClientsWhenClientNameIsNull() {
		Client client = ClientFactory.createClient();
		client.setName(null);
		
		List<Client> result = repository.findAll();
		
		Assertions.assertTrue(true);
		Assertions.assertEquals(result, null);
	
	}
	
	@Test
	public void findByBirthDateShouldReturnClientsWhenClientBirthDateIsGreaterThanSpecificDate() {
		Client client = ClientFactory.createClient();
		Instant date2 = Instant.parse("1950-12-25T20:30:50Z");
				
		Assertions.assertFalse(client.getBirthDate().isBefore(date2));
		Assertions.assertTrue(client.getBirthDate().isAfter(date2));
	}
	
	@Test
	public void updateShouldReturnUpdatedClientWhenDataAreUpdated() {
		Client client = ClientFactory.createClient();
		client.setName("Novo nome");
		
		client = repository.save(client);
				
		Assertions.assertTrue(true, "Dados atualizados com sucesso");
		Assertions.assertFalse(true, "Cliente não localizado; atualização impossível");
	}
	
	@Test
	public void findByIdShouldReturnClientsWhenChildrenIsZero() {
		Client client = ClientFactory.createClient();
		
		List<Client> result = repository.findByChildrenNotLike0(0);
		
		Assertions.assertEquals(result,client);
				
	}
	
}
