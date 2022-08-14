package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.Address;
import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final WebClient maps;
    private final WebClient pricing;
    private final ModelMapper mapper;

    public CarService(CarRepository repository, WebClient maps, WebClient pricing, ModelMapper mapper) {
        /**
         * TODO: Add the Maps and Pricing Web Clients you create
         *   in `VehiclesApiApplication` as arguments and set them here.
         */
        this.repository = repository;
        this.maps = maps;
        this.pricing = pricing;
        this.mapper = mapper;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         *   Remove the below code as part of your implementation.
         */
         var carExists = this.repository.findById(id);
         if(carExists.isEmpty()){
             throw new CarNotFoundException();
         }
        Car car = new Car();

        /**
         * TODO: Use the Pricing Web client you create in `VehiclesApiApplication`
         *   to get the price based on the `id` input'
         * TODO: Set the price of the car
         * Note: The car class file uses @transient, meaning you will need to call
         *   the pricing service each time to get the price.
         */
        Price price = this.pricing
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/services/price/")
                        .queryParam("vehicleId", id)
                        .build())
                .retrieve()
                .bodyToMono(Price.class)
                .block(Duration.ofSeconds(5));

        car.setPrice(String.format("%s %s", price.getCurrency(), price.getPrice()));

        /**
         * TODO: Use the Maps Web client you create in `VehiclesApiApplication`
         *   to get the address for the vehicle. You should access the location
         *   from the car object and feed it to the Maps service.
         * TODO: Set the location of the vehicle, including the address information
         * Note: The Location class file also uses @transient for the address,
         * meaning the Maps service needs to be called each time for the address.
         */
        Address address = this.maps
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/")
                        .queryParam("lat", car.getLocation().getLat())
                        .queryParam("lon", car.getLocation().getLon())
                        .build())
                .retrieve()
                .bodyToMono(Address.class)
                .block(Duration.ofSeconds(5));
        Location location = new Location();
        mapper.map(Objects.requireNonNull(address), location);
        car.setLocation(location);
        return car;
    }


    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        return repository.save(car);
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * TODO: Find the car by ID from the `repository` if it exists.
         *   If it does not exist, throw a CarNotFoundException
         */
        var carExists = this.repository.findById(id);
        if(carExists.isEmpty()){
            throw new CarNotFoundException();
        }

        /**
         * TODO: Delete the car from the repository.
         */
        this.repository.deleteById(id);
    }
}
