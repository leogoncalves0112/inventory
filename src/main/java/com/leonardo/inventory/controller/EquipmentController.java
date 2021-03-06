package com.leonardo.inventory.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardo.inventory.model.Equipment;
import com.leonardo.inventory.resource.EquipmentResource;
import com.leonardo.inventory.service.EquipmentService;
import com.leonardo.inventory.utilities.Utilities;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/equipments")
public class EquipmentController {

	@Autowired
	private EquipmentService service;

	@Autowired
	private ObjectMapper mapper;

	@Value("${inventory.equipment.depreciation}")
	@JsonIgnore
	private Double depreciation;

	@GetMapping
	@ApiOperation(value = "Lista todos os equipamentos")
	public List<EquipmentResource> list() {
		return this.service.findAll().stream().map(entity -> EquipmentResource.fromEntity(entity, depreciation))
				.collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "Consulta um equipamento pelo id")
	public ResponseEntity<EquipmentResource> get(@PathVariable Long id) {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			return new ResponseEntity<>(EquipmentResource.fromEntity(equipment, depreciation), HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping
	@ApiOperation(value = "Salva um equipamento")
	public EquipmentResource save(@RequestBody EquipmentResource equipment) {
		Equipment entity = this.service.save(equipment.toEntity());
		EquipmentResource resource = EquipmentResource.fromEntity(entity, depreciation);

		try {
			// Send email
			this.service.sendMessageWithQrcode(this.getQRCode(resource));
		} catch (Exception e) {
			// Se falhou não envia o email mas salva o equipamento
		}

		return resource;
	}

	@PostMapping("/{id}/upload")
	@ApiOperation(value = "Realiza o upload da imagem do equipamento")
	public ResponseEntity<EquipmentResource> upload(@PathVariable Long id, @RequestPart MultipartFile file)
			throws IOException {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			return new ResponseEntity<>(
					EquipmentResource.fromEntity(this.service.uploadImage(equipment, file), depreciation),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/{id}", consumes = MediaType.IMAGE_JPEG_VALUE, produces = MediaType.IMAGE_JPEG_VALUE)
	@ApiOperation(value = "Obtém a imagem do equipamento")
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			byte[] file = this.service.downloadImage(equipment);

			// Se existir imagem retorna
			if (file.length > 0) {
				return new ResponseEntity<>(file, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
	@ApiOperation(value = "Obtém o QR-Code do equipamento")
	public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) throws JsonProcessingException {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			EquipmentResource resource = EquipmentResource.fromEntity(equipment, depreciation);

			byte[] qrcode = this.getQRCode(resource);

			if (qrcode.length > 0) {
				return new ResponseEntity<>(qrcode, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "Remove um equipamento")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			this.service.delete(equipment);

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	private byte[] getQRCode(EquipmentResource resource) throws JsonProcessingException {
		EquipmentResource equipment = new EquipmentResource();
		equipment.setId(resource.getId());
		equipment.setType(resource.getType());
		equipment.setModel(resource.getModel());
		equipment.setAcquired(resource.getAcquired());
		equipment.setPrice(resource.getPrice());
		equipment.setUpdatedPrice(resource.getUpdatedPrice());

		String json = mapper.writeValueAsString(equipment);
		return Utilities.getQRCodeImage(json, 200, 200);
	}

}
