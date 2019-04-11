package com.leonardo.inventory.resource;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.leonardo.inventory.controller.EquipmentRestController;
import com.leonardo.inventory.model.Equipment;

public class EquipmentResource {

	/**
	 * ID
	 */
	private Long id;
	/**
	 * Type
	 */
	private String type;
	/**
	 * Model
	 */
	private String model;
	/**
	 * Data de aquisição
	 */
	private LocalDate acquired;
	/**
	 * Price
	 */
	private Double price;
	/**
	 * Link for image
	 */
	private Link image;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public LocalDate getAcquired() {
		return acquired;
	}

	public void setAcquired(LocalDate acquired) {
		this.acquired = acquired;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Link getImage() {
		return image;
	}

	public void setImage(Link image) {
		this.image = image;
	}

	public static EquipmentResource fromEntity(Equipment entity) {
		EquipmentResource resource = new EquipmentResource();
		resource.setId(entity.getId());
		resource.setType(entity.getType());
		resource.setModel(entity.getModel());
		resource.setAcquired(entity.getAcquired());
		resource.setPrice(entity.getPrice());
		if (entity.getImage() != null) {
			try {
				resource.setImage(ControllerLinkBuilder
						.linkTo(ControllerLinkBuilder.methodOn(EquipmentRestController.class).getImage(entity.getId()))
						.withSelfRel());
			} catch (IOException e) {
				// Do nothing
			}
		}

		return resource;
	}

	public Equipment toEntity() {
		Equipment entity = new Equipment();
		entity.setId(this.getId());
		entity.setType(this.getType());
		entity.setModel(this.getModel());
		entity.setAcquired(this.getAcquired());
		entity.setPrice(this.getPrice());

		return entity;
	}

}
