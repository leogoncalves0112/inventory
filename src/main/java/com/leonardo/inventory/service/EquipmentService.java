package com.leonardo.inventory.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.leonardo.inventory.model.Equipment;

public interface EquipmentService {

	/**
	 * Obtém a lista de todos os equipamentos cadastrados
	 * 
	 * @return Lista de equipamentos
	 */
	public List<Equipment> findAll();

	/**
	 * Consulta um equipamento pelo id
	 * 
	 * @param id Código do equipamento
	 * @return Equipamento encontrado
	 */
	public Equipment findById(Long id);

	/**
	 * Persiste o equipamento
	 * 
	 * @param equipment Equipamento a ser persistido
	 * @return Equipamento persistido
	 */
	public Equipment save(Equipment equipment);

	/**
	 * Remove o equipamento
	 * 
	 * @param equipment Equipamento a ser removido
	 */
	public void delete(Equipment equipment);

	/**
	 * Realiza o upload da imagem do equipamento e persiste no equipamento
	 * 
	 * @param equipment Equipamento a ser adicionado a imagem
	 * @param file      arquivo da imagem a ser persistido
	 * @return Equipamento atualizado
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public Equipment uploadImage(Equipment equipment, MultipartFile file) throws IllegalStateException, IOException;

	/**
	 * Realiza o download da imagem do equipamento
	 * 
	 * @param equipment Equipamento à consultar a imagem
	 * @return byte[] da imagem do equipamento
	 * @throws IOException
	 */
	public byte[] downloadImage(Equipment equipment) throws IOException;

	/**
	 * Envia um email com o qrcode
	 * 
	 * @param to     Destinatário
	 * @param qrCode byte[] do qrcode
	 * @throws MessagingException
	 */
	public void sendMessageWithQrcode(String to, byte[] qrCode) throws MessagingException;

}
