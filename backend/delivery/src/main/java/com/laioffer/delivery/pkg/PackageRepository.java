package com.laioffer.delivery.pkg;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRepository extends JpaRepository<Package, Long> {

    List<Package> findByOrderId(Long orderId);
}
