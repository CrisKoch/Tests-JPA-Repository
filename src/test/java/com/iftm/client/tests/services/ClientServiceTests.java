package com.iftm.client.tests.services;

import static org.mockito.Mockito.mockitoSession;


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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;

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
	private ClientDTO clientDTO;
	private Pageable pageable;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		
		nonExistingId = 1000L;
		dependentId = 4L;
		client = ClientFactory.createClient();
		clientDTO = ClientFactory.createClientDTO();
		
		
		//Configurando comportamento para o meu mock
		
		Mockito.doNothing().when(repository).deleteById(existingId);
		
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));
		
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		
		//Mockito.when(repository.findAll(pageable)).thenReturn((Page<Client>) repository.findAll());
		
		
	
	}
	
	// Delete retorna vazio quando ID existir
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
			
	}
	
	// Delete lança EmptyResultDataAccesException quando ID não existir
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {
				
		Assertions.assertThrows(EmptyResultDataAccessException.class, () ->{
			service.delete(nonExistingId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
			
	}
	
	// Delete lança DataIntegrityViolationException quando deleção implicar em uma restrição de integridade
	@Test
	public void deleteShouldThrowDataIntegrityViolationExceptionWhenIdHasDependencyIntegrity() {
		
		Assertions.assertThrows(DataIntegrityViolationException.class, () ->{
			service.delete(dependentId);
		});
		
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
			
	}
	//findAllPaged deve retornar uma página (e chamar o método findAll do repository)
	
	@Test
	public void findAllPagedShouldReturnOnePageAndCallFindAllRepositoryMethod(PageRequest pageRequest) {
		
		
		Assertions.assertDoesNotThrow(() -> {
			repository.findAll(pageRequest);
		});
		Mockito.verify(repository,Mockito.times(1)).findAll(pageRequest);
	}
	
	
	//findByIncome deveria retornar uma página (e chamar o método findByIncome do repository)
	@Test 
	public void findByIncomeShouldReturnOnePageAndCallFindByIncomeRepositoryMethod() {

		Assertions.assertEquals(pageable, client);
		
		Mockito.doReturn(pageable).doCallRealMethod();
	}
	
	
	//findById deveria retornar um ClientDTO quando o id existir
	@Test 
	public void findByIdShouldReturnClientDTOWhenIdExists() {
		
		Assertions.assertEquals(clientDTO, client);
		
		Mockito.doReturn(clientDTO).when(existingId);
	}
	
	//findById deveria lançar ResourceNotFoundException quando o id não existir
	@Test
	public void findByIdShoulThrowResourceNotFoundExceptionWhenIdNotExists() {
				
			Assertions.assertThrows(ResourceNotFoundException.class, () ->{
				service.findById(nonExistingId);
			});
			
			Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);
		
	}
	
	//update deveria retornar um ClientDTO quando o id existir
	
	@Test
	public void updateShouldReturnClientDTOWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.update(existingId, clientDTO);
		});
		
		
		Mockito.verify(repository, Mockito.times(1)).existsById(existingId);
			
	}
	
	//update deveria lançar uma ResourceNotFoundException quando o id não existir
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () ->{
			service.update(null, clientDTO);
		});
		
		Mockito.verify(repository, Mockito.times(1)).existsById(nonExistingId);
				
		
	}
	//insert deveria retornar um ClientDTO ao inserir um novo cliente
	@Test
	public void insertShoulReturnClientDTOWhenInsertsNewClient() {
		
		Assertions.assertEquals(clientDTO, client);
		
		Mockito.verify(repository, Mockito.times(1)).save(null);
		
	}
	
	
	
	
	
	
}
