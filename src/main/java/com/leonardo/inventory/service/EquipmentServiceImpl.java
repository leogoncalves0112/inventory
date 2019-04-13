package com.leonardo.inventory.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.leonardo.inventory.model.Equipment;
import com.leonardo.inventory.repository.EquipmentRepository;

@Service
public class EquipmentServiceImpl implements EquipmentService {

	@Autowired
	private EquipmentRepository repository;

	@Autowired
	public JavaMailSender emailSender;

	@Override
	public List<Equipment> findAll() {
		return this.repository.findAll();
	}

	@Override
	public Equipment findById(Long id) {
		Optional<Equipment> equipment = this.repository.findById(id);
		if (equipment.isPresent()) {
			return equipment.get();
		}
		return null;
	}

	@Override
	public Equipment save(Equipment equipment) {
		return this.repository.save(equipment);
	}

	@Override
	public void delete(Equipment equipment) {
		this.repository.delete(equipment);
	}

	@Override
	public Equipment uploadImage(Equipment equipment, MultipartFile file) throws IOException {
		// Save image in storage
		String imageName = equipment.getId() + "_" + new Date().getTime();
		String imagePath = "/home/leonardo/" + imageName + ".jpeg";

		// TODO Change to S3
		File newfile = new File(imagePath);
		file.transferTo(newfile);

		// Save path in database
		equipment.setImage(imagePath);
		return this.save(equipment);
	}

	@Override
	public byte[] downloadImage(Equipment equipment) throws IOException {
		if (equipment.getImage() != null) {
			// TODO change to S3
			return Files.readAllBytes(Paths.get(equipment.getImage()));
		}
		return new byte[0];
	}

	@Override
	public void sendMessageWithQrcode(String to, byte[] qrCode) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject("Equipamento Cadastrado");
		helper.setText("Segue o QR-Code");

		helper.addAttachment("Invoice", new ByteArrayResource(qrCode));

		emailSender.send(message);
	}

}
