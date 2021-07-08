package com.iftm.client.tests.integration;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.repositories.ClientRepository;
import com.iftm.client.services.ClientService;
import com.iftm.client.services.exceptions.ResourceNotFoundException;
import com.iftm.client.tests.factory.ClientFactory;

@SpringBootTest
@Transactional
public class ClientServiceIT {

	@Autowired
	private ClientService service;
	private ClientDTO clientDTO;
	private Client client;
	private long existingId;
	private long nonExistingId;
	private PageRequest pageRequest;
	private long countClientByIncome;
	private long countTotalClients;


	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalClients = 12L;
		countClientByIncome = 5L;
		pageRequest = PageRequest.of(0, 6);
		client = ClientFactory.createClient();
		clientDTO = ClientFactory.createClientDTO();

	}

	@Test
	@DisplayName("Exclui id existente e decrementa a base de dados")
	public void deleteShouldDecrementTotalClientsWhenIdExists() {
		service.delete(existingId);
		List<ClientDTO> result = service.findAll();
		Assertions.assertEquals(countTotalClients - 1, result.size());
	}

	@Test
	@DisplayName("FindById deve verificar se nome e CPF sao correspondentes a id existente")
	public void findByIdShoulVerifyIfNameAndCPFAreCorrespondentsWhenIdExists() {
		
		clientDTO = service.findById(existingId);
		clientDTO.setName("Joao");
		clientDTO.setCpf("111111111-11");

		Assertions.assertEquals("Joao", clientDTO.getName());
		Assertions.assertEquals("111111111-11", clientDTO.getCpf());

		/*
		 * String cpf1 = service.findById(existingId).getCpf(); String nome1 =
		 * service.findById(existingId).getName();
		 */
		// String cpf1 = service.findById(clientDTO.getId()).getCpf();
		// String nome1 = service.findById(clientDTO.getId()).getName();
		/*
		 * Assertions.assertEquals(cpf1, clientDTO.getCpf());
		 * Assertions.assertEquals(nome1, clientDTO.getName());
		 */
	}

	@Test
	@DisplayName("Insert deve incrementar")
	public void insertShouldIncrementAndFindAllVerifyThisIncrementWhenNewClientIsCreate() {
		service.insert(clientDTO);
		List<ClientDTO> resultAfterInsert = service.findAll();
		Assertions.assertEquals(countTotalClients + 1, resultAfterInsert.size());

	}

	@Test 
	@DisplayName("Update deve atualizar dados cliente")
	public void updateShouldChangeClientData() {
		
		clientDTO.setName("NovoNome");
		service.update(existingId, clientDTO);
		Assertions.assertNotEquals(clientDTO, client);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}

	@Test
	public void findByIncomeShouldReturnClientsWhenClientIncomeIsGraterThanOrEqualsToValue() {

		Double income = 4000.00;
		Page<ClientDTO> result = service.findByIncome(income, pageRequest);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countClientByIncome, result.getTotalElements());

	}

	@Test
	public void findAllShouldReturnAllClients() {
		List<ClientDTO> result = service.findAll();
		Assertions.assertEquals(countTotalClients, result.size());

	}

}
