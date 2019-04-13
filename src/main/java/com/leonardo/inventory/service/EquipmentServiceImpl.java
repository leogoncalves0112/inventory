package com.leonardo.inventory.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private StorageService storageService;

	@Autowired
	public JavaMailSender emailSender;

	@Value("${inventory.mail.to}")
	private String mailTo;

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
		String filename = equipment.getId() + ".jpeg";

		this.storageService.putFile(file, filename);

		// Save path in database
		equipment.setImage(filename);
		return this.save(equipment);
	}

	@Override
	public byte[] downloadImage(Equipment equipment) throws IOException {
		if (equipment.getImage() != null) {
			return this.storageService.getFile(equipment.getImage());
		}
		return new byte[0];
	}

	@Override
	public void sendMessageWithQrcode(byte[] qrCode) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(mailTo);
		helper.setSubject("Equipamento Cadastrado");
		helper.setText("Segue o QR-Code");

		helper.addAttachment("Invoice", new ByteArrayResource(qrCode));

		emailSender.send(message);
	}

}
