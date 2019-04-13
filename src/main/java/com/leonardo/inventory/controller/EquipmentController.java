package com.leonardo.inventory.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardo.inventory.model.Equipment;
import com.leonardo.inventory.resource.EquipmentResource;
import com.leonardo.inventory.service.EquipmentService;
import com.leonardo.inventory.utilities.Utilities;

@RestController
@RequestMapping("/equipments")
public class EquipmentController {

	@Autowired
	private EquipmentService service;

	@Autowired
	private ObjectMapper mapper;

	@GetMapping
	public List<EquipmentResource> list() {
		return this.service.findAll().stream().map(EquipmentResource::fromEntity).collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EquipmentResource> get(@PathVariable Long id) {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			return new ResponseEntity<>(EquipmentResource.fromEntity(equipment), HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping
	public EquipmentResource save(@RequestBody EquipmentResource equipment) {
		Equipment entity = this.service.save(equipment.toEntity());
		EquipmentResource resource = EquipmentResource.fromEntity(entity);

		try {
			// Send email
			this.service.sendMessageWithQrcode("leo.goncalves0112@gmail.com", this.getQRCode(resource));
		} catch (Exception e) {
			// Se falhou não envia o email mas salva o equipamento
		}
		

		return resource;
	}

	@PostMapping("/{id}/upload")
	public ResponseEntity<EquipmentResource> upload(@PathVariable Long id, @RequestPart MultipartFile file)
			throws IOException {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			return new ResponseEntity<>(EquipmentResource.fromEntity(this.service.uploadImage(equipment, file)),
					HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/{id}", consumes = MediaType.IMAGE_JPEG_VALUE, produces = MediaType.IMAGE_JPEG_VALUE)
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
	public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) throws JsonProcessingException {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			EquipmentResource resource = EquipmentResource.fromEntity(equipment);

			byte[] qrcode = this.getQRCode(resource);

			if (qrcode.length > 0) {
				return new ResponseEntity<>(qrcode, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Equipment equipment = this.service.findById(id);

		if (equipment != null) {
			this.service.delete(equipment);

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	private byte[] getQRCode(EquipmentResource resource) throws JsonProcessingException {
		resource.setImage(null);
		resource.setQrCode(null);

		String json = mapper.writeValueAsString(resource);
		return Utilities.getQRCodeImage(json, 200, 200);
	}

}
