package com.iftm.client.tests.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
	private PageRequest pageRequest;
	private PageImpl<Client> page;
	private Double income;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		client = ClientFactory.createClient();
		clientDTO = ClientFactory.createClientDTO();
		pageRequest = PageRequest.of(0, 6);
		page = new PageImpl<>(List.of(client));
		income = 2000.00;

		// Configurando comportamento para o meu mock

		Mockito.doNothing().when(repository).deleteById(existingId);

		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);

		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));

		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		Mockito.when(repository.findAll(pageable)).thenReturn(page);

		Mockito.when(repository.findByIncome(ArgumentMatchers.anyDouble(), ArgumentMatchers.any())).thenReturn(page);

		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(client));

		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

	}

	@Test
	@DisplayName("Retorna vazio para ID existente.")
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);

	}

	@Test
	@DisplayName("EmptyResultDataAccesException para ID inexistente")
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);

	}

	@Test
	@DisplayName("DataIntegrityViolationException quando delecao implicar em restricao integridade")
	public void deleteShouldThrowDataIntegrityViolationExceptionWhenIdHasDependencyIntegrity() {

		Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);

	}

	@Test
	@DisplayName("FindAllPaged deve retornar pag e chamar findAll repository")
	public void findAllPagedShouldReturnPage() {
		Page<ClientDTO> result = service.findAllPaged(pageRequest);
		Assertions.assertNotNull(result);
		Assertions.assertFalse(result.isEmpty());

		Mockito.verify(repository, Mockito.times(1)).findAll(pageRequest);
	}

	@Test
	@DisplayName("FindByIncome deve retornar pag e chamar findByIncome do repository")
	public void findByIncomeShouldReturnPage() {
		Page<ClientDTO> result = service.findByIncome(income, pageRequest);
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findByIncome(income, pageRequest);
	}

	@Test
	@DisplayName("FindById deve retornar um ClientDTO quando o id existir")
	public void findByIdShouldReturnClientDTOWhenIdExists() {

		Assertions.assertEquals(clientDTO, client);

		Mockito.doReturn(clientDTO).when(existingId);
	}

	@Test
	@DisplayName("FindById deve lançar ResourceNotFoundException quando o id não existir")
	public void findByIdShoulThrowResourceNotFoundExceptionWhenIdNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).findById(nonExistingId);

	}

	@Test
	@DisplayName("Update deveria retornar um ClientDTO quando o id existir")
	public void updateShouldReturnClientDTOWhenIdExists() {
		long id = 1L;
		ClientDTO clientDTO = mock(ClientDTO.class);
		Client client = mock(Client.class);
		when(repository.getOne(id)).thenReturn(client);
		when(repository.save(client)).thenReturn(client);

		ClientDTO expected = service.update(id, clientDTO);
		Assertions.assertNotNull(expected);

	}

	@Test
	@DisplayName("Update deveria lançar uma ResourceNotFoundException quando o id não existir")
	public void updateShouldThrowResourceNotFoundExceptionWhenIdNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(null, clientDTO);
		});

		Mockito.verify(repository, Mockito.times(1)).existsById(nonExistingId);

	}

	@Test
	@DisplayName("Insert deve retornar um ClientDTO ao inserir um novo cliente")
	public void insertShoulReturnClientDTOWhenInsertsNewClient() {

		Assertions.assertEquals(clientDTO, client);

		Mockito.verify(repository, Mockito.times(1)).save(null);

	}

}
