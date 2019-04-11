package com.leonardo.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leonardo.inventory.model.Equipment;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

}
