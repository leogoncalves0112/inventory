package com.leonardo.inventory.resource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.leonardo.inventory.controller.EquipmentController;
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
	 * Valor
	 */
	private Double price;
	private Double updatedPrice;
	/**
	 * Link for image
	 */
	private Link image;
	/**
	 * Link for qrcode
	 */
	private Link qrCode;

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

	public Double getUpdatedPrice() {
		return this.updatedPrice;
	}

	public void setUpdatedPrice(Double updatedPrice) {
		this.updatedPrice = updatedPrice;
	}

	public Link getImage() {
		return image;
	}

	public void setImage(Link image) {
		this.image = image;
	}

	public Link getQrCode() {
		return qrCode;
	}

	public void setQrCode(Link qrCode) {
		this.qrCode = qrCode;
	}

	public static EquipmentResource fromEntity(Equipment entity, Double depreciation) {
		EquipmentResource resource = new EquipmentResource();
		resource.setId(entity.getId());
		resource.setType(entity.getType());
		resource.setModel(entity.getModel());
		resource.setAcquired(entity.getAcquired());
		resource.setPrice(entity.getPrice());
		resource.setUpdatedPrice(resource.getUpdatedDepreciation(depreciation));

		// Link to QR-Code
		try {
			resource.setQrCode(ControllerLinkBuilder
					.linkTo(ControllerLinkBuilder.methodOn(EquipmentController.class).getQRCode(entity.getId()))
					.withSelfRel());
		} catch (IOException e) {
			// Do nothing
		}

		// Link to Image
		if (entity.getImage() != null) {
			try {
				resource.setImage(ControllerLinkBuilder
						.linkTo(ControllerLinkBuilder.methodOn(EquipmentController.class).getImage(entity.getId()))
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

	private double getUpdatedDepreciation(Double depreciation) {
		long months = ChronoUnit.MONTHS.between(this.getAcquired(), LocalDate.now());

		double dep = months * depreciation;
		if (dep >= 100D) {
			return 0D;
		}

		return price - (price * dep / 100);
	}

}
