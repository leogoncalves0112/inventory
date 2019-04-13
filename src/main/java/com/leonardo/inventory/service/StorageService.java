package com.leonardo.inventory.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	/**
	 * Obtém o arquivo do storage
	 * 
	 * @param filename nome do arquivo
	 * @return byte[] do arquivo
	 * @throws IOException
	 */
	public byte[] getFile(String filename) throws IOException;

	/**
	 * Armazena o arquivo no storage
	 * 
	 * @param file     Arquivo
	 * @param filename Nome do arquivo a ser salvo
	 * @throws IOException
	 */
	public void putFile(MultipartFile file, String filename) throws IOException;

}
