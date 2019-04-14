package com.leonardo.inventory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.leonardo.inventory.controller.EquipmentController;
import com.leonardo.inventory.model.Equipment;
import com.leonardo.inventory.repository.EquipmentRepository;
import com.leonardo.inventory.service.EquipmentService;
import com.leonardo.inventory.service.EquipmentServiceImpl;
import com.leonardo.inventory.service.StorageService;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { EquipmentRepository.class, EquipmentService.class, EquipmentServiceImpl.class,
		EquipmentController.class })
public class InventoryApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
    private JavaMailSender mailSender;

	@Autowired
	@MockBean
	private EquipmentRepository repository;

	@MockBean
	private StorageService storageService;

	@Test
	public void listEquipments() throws Exception {
		Equipment mock = this.createMockEquipment();
		given(repository.findAll()).willReturn(Arrays.asList(mock));

		this.mockMvc.perform(get("/api/equipments"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].id", is(mock.getId().intValue())));
	}

	@Test
	public void getEquipment() throws Exception {
		Equipment mock = this.createMockEquipment();
		given(repository.findById(mock.getId())).willReturn(Optional.of(mock));

		this.mockMvc.perform(get("/api/equipments/{id}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.model", is(mock.getModel())));
	}

	@Test
	public void getImage() throws Exception {
		Equipment mock = this.createMockEquipment();
		given(repository.findById(mock.getId())).willReturn(Optional.of(mock));
		given(storageService.getFile(mock.getImage())).willReturn("teste de bytes da imagem".getBytes());

		this.mockMvc.perform(get("/api/equipments/{id}", 1L).contentType(MediaType.IMAGE_JPEG_VALUE))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getImageNull() throws Exception {
		Equipment mock = this.createMockEquipment();
		mock.setImage(null);
		given(repository.findById(mock.getId())).willReturn(Optional.of(mock));

		this.mockMvc.perform(get("/api/equipments/{id}", 1L).contentType(MediaType.IMAGE_JPEG_VALUE))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void save() throws Exception {
		Equipment mock = this.createMockEquipment();
		given(repository.save(Mockito.any())).willReturn(mock);
		
		this.mockMvc.perform(post("/api/equipments")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.createMockEquipmentJSON()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is(1)));;
		
	}
	
	@Test
	public void delete() throws Exception {
		Equipment mock = this.createMockEquipment();
		given(repository.findById(mock.getId())).willReturn(Optional.of(mock));
		
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/equipments/{id}", mock.getId()))
			.andExpect(status().isOk());
	}
	
	@Test
	public void deleteNonExistentEquipment() throws Exception {		
		this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/equipments/{id}", 0L))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void upload() throws Exception {
		Equipment mock = this.createMockEquipment();
		given(repository.findById(mock.getId())).willReturn(Optional.of(mock));
		given(repository.save(Mockito.any())).willReturn(mock);
		
		MockMultipartFile file = new MockMultipartFile("file", "1.jpeg", MediaType.IMAGE_JPEG_VALUE,
				"some image".getBytes());

		this.mockMvc.perform(multipart("/api/equipments/{id}/upload", 1L).file(file))
			.andExpect(status().isOk());
	}

	private Equipment createMockEquipment() {
		Equipment equipment = new Equipment();
		equipment.setId(1L);
		equipment.setType("Ferramenta");
		equipment.setModel("Chave de fenda");
		equipment.setAcquired(LocalDate.now());
		equipment.setPrice(35.5D);
		equipment.setImage("1.jpeg");

		return equipment;
	}
	
	private String createMockEquipmentJSON() {
		return "{\n" + 
				"    \"id\": 1,\n" + 
				"    \"type\": \"Ferramenta\",\n" + 
				"    \"model\": \"Chave de fenda\",\n" + 
				"    \"acquired\": \"2000-06-10\",\n" + 
				"    \"price\": 35.5,\n" + 
				"    \"image\": null\n" + 
				"}";
	}

}
