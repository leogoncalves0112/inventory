package com.leonardo.inventory.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.leonardo.inventory.model.Equipment;
import com.leonardo.inventory.repository.EquipmentRepository;
import com.leonardo.inventory.resource.EquipmentResource;

@RestController
@RequestMapping("/equipments")
public class EquipmentRestController {

	@Autowired
	private EquipmentRepository repository;

	@GetMapping
	public List<EquipmentResource> list() {
		return this.repository.findAll().stream().map(EquipmentResource::fromEntity).collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<EquipmentResource> get(@PathVariable Long id) {
		Optional<Equipment> equipment = this.repository.findById(id);

		if (equipment.isPresent()) {
			return new ResponseEntity<>(EquipmentResource.fromEntity(equipment.get()), HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@PostMapping
	public EquipmentResource save(@RequestBody EquipmentResource equipment) {
		Equipment entity = this.repository.save(equipment.toEntity());

		return EquipmentResource.fromEntity(entity);
	}

	@PostMapping("/{id}/upload")
	public ResponseEntity<EquipmentResource> save(@PathVariable Long id, @RequestPart MultipartFile file)
			throws IOException {
		Optional<Equipment> equipment = this.repository.findById(id);

		if (equipment.isPresent()) {
			// Save image in storage
			String imagePath = "/home/leonardo/" + id + ".jpeg";

			// TODO Change to S3
			File newfile = new File(imagePath);
			file.transferTo(newfile);

			// Save path in database
			Equipment entity = equipment.get();
			entity.setImage(imagePath);
			entity = this.repository.save(entity);

			return new ResponseEntity<>(EquipmentResource.fromEntity(entity), HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping(path = "/{id}", consumes = MediaType.IMAGE_JPEG_VALUE, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws IOException {
		Optional<Equipment> equipment = this.repository.findById(id);

		if (equipment.isPresent()) {
			// TODO change to S3
			byte[] file = Files.readAllBytes(Paths.get(equipment.get().getImage()));

			return new ResponseEntity<>(file, HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		Optional<Equipment> equipment = this.repository.findById(id);

		if (equipment.isPresent()) {
			this.repository.delete(equipment.get());

			return new ResponseEntity<>(HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

}
