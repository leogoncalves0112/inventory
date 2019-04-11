package com.leonardo.inventory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.leonardo.inventory.model.Equipment;
import com.leonardo.inventory.repository.EquipmentRepository;

@RunWith(SpringRunner.class)
@WebMvcTest
public class InventoryApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private EquipmentRepository repository;

	@Test
	public void list() throws Exception {
		Equipment equipment = new Equipment();
		equipment.setId(1L);
		equipment.setType("Ferramenta");
		equipment.setModel("Chave de fenda");
		equipment.setAcquired(LocalDate.now());
		equipment.setPrice(35.5D);
		equipment.setImage("/files/1.jpeg");

		given(repository.findAll()).willReturn(Arrays.asList(equipment));

		this.mockMvc.perform(get("/equipments"))
//			.contentType(MediaType.APPLICATION_JSON)
			.andExpect(status().isOk());
//			.andExpect(jsonPath("$", hasSize(1)))
//			.andExpect(jsonPath("$[0].id", is(equipment.getId())));
	}

}
