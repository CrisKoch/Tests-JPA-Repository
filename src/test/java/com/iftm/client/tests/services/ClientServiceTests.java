package com.iftm.client.tests.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.DatabaseException;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import com.iftm.client.tests.factory.ClientFactory;

@ExtendWith(SpringExtension.class)
public class ClientServiceTests {
	
	@InjectMocks
	private ClientService service;
	
	@Mock
	private ClientRepository repository;
	
	private long existingId;	
	private long nonExistingId;
	private long dependentId;
	private Client client;
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		
		
		nonExistingId = 1000L;
		dependentId = 4L;
		client = ClientFactory.createClient();
		//Configurando comportamento para o meu mock
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));
		
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
	
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		//Mockito.verify(repository).deleteById(existingId);
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
			
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
				
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
			
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdHasDependencyIntegrity() {
		
		Assertions.assertThrows(DatabaseException.class, () ->{
			service.delete(dependentId);
		});
		
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
			
	}
	
	
	
	
	
}
