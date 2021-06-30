package com.carDealership.Application.service;

import com.carDealership.Application.dto.SaleDTO;
import com.carDealership.Application.entity.Sale;
import com.carDealership.Application.entity.User;
import com.carDealership.Application.entity.UserRoleEnum;
import com.carDealership.Application.entity.Vehicle;
import com.carDealership.Application.exception.NotFoundException;
import com.carDealership.Application.repository.SaleRepository;
import com.carDealership.Application.repository.UserRepository;
import com.carDealership.Application.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.carDealership.Application.mapper.SaleMapper.INSTANCE;

@Service
public class SaleServiceImpl implements SaleService {

    SaleRepository saleRepository;
    UserRepository userRepository;
    VehicleRepository vehicleRepository;

    public SaleServiceImpl(SaleRepository saleRepository, UserRepository userRepository,
                           VehicleRepository vehicleRepository) {

        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<SaleDTO> allSales() {
        List<Sale> allSales = saleRepository.findAll();
        if (!CollectionUtils.isEmpty(allSales)) {
            return INSTANCE.allSalesToDto(allSales());
        }
        return Collections.emptyList();
    }

    public SaleDTO findSaleById(Long id) {
        Optional<Sale> foundSale = saleRepository.findById(id);
        if (foundSale.isPresent()) {
            return INSTANCE.saleToSaleDto(foundSale.get());
        }
        throw new NotFoundException(id);
    }

    public SaleDTO newSale(SaleDTO saleDTO) {
        //preguntar y hacer findbyid de los usuarios y sus roles
        Optional<User> optionalSeller =  userRepository.findByIdAndRole(saleDTO.getSellerId(), UserRoleEnum.SELLER);
        Optional<User> optionalCustomer =  userRepository.findByIdAndRole(saleDTO.getCustomerId(), UserRoleEnum.CUSTOMER);
        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(saleDTO.getVehicleId());

        if(optionalSeller.isPresent() && optionalVehicle.isPresent() && optionalCustomer.isPresent()) { //lo mismo para v
            User existingSeller = optionalSeller.get();
            User existingCustomer = optionalCustomer.get();
            Vehicle existingVehicle = optionalVehicle.get();
            Sale sale = INSTANCE.saleDtoToSale(saleDTO);
            sale.setSeller(existingSeller);
            sale.setCustomer(existingCustomer);
            sale.setSoldVehicle(existingVehicle);
            Sale savedSale = saleRepository.save(sale);

            return INSTANCE.saleToSaleDto(savedSale);
        }
        return null;
    }

    public SaleDTO updateSale(SaleDTO saleDTO) throws NotFoundException {
        Optional<Sale> saleToUpdate = saleRepository.findById(saleDTO.getId());
        if (saleToUpdate.isPresent()) {
            Sale updateSale = INSTANCE.saleDtoToSale(saleDTO);
            Sale updatedSale = saleRepository.save(updateSale);
            return INSTANCE.saleToSaleDto(updatedSale);
        }
        throw new NotFoundException(saleDTO.getId());
    }

    public boolean deleteSale(Long id) {
        try {
            saleRepository.deleteById(id);
            return true;
        } catch (Exception error) {
            return false;
        }
    }


}
