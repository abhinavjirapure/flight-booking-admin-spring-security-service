package com.flightApp.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightApp.DTOs.FlightDto;
import com.flightApp.proxy.FlightProxy;
import com.flightApp.ui.FlightRequestModel;
import com.flightApp.ui.FlightResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1.0/flight")
public class FlightController {

	@Autowired
    private final ModelMapper modelMapper;

	private final FlightProxy flightProxy;

	public FlightController(ModelMapper modelMapper, FlightProxy flightProxy) {
		this.modelMapper = modelMapper;
		this.flightProxy = flightProxy;
	}

	@PostMapping()
	@CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "myTestFallBack")
	public ResponseEntity<FlightResponseModel> addNewFlight(@RequestBody FlightRequestModel flightRequestModel) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		FlightDto flightDto = modelMapper.map(flightRequestModel, FlightDto.class);
		String[] uuid = UUID.randomUUID().toString().split("-");

		flightDto.setFlightId(uuid[0]);
		FlightDto res = flightProxy.addFlight(flightDto);

		return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(res, FlightResponseModel.class));

	}

	@PutMapping("/{flightId}")
	@CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "myTestFallBack")
	public ResponseEntity<FlightResponseModel> blockFlight(@PathVariable String flightId) {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		FlightDto res = flightProxy.blockFlight(flightId);
		return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(res, FlightResponseModel.class));

	}

	@GetMapping("/flights")
	@CircuitBreaker(name = "myCircuitBreaker", fallbackMethod = "myTestFallBack")
	public ResponseEntity<List<FlightResponseModel>> getAllFlights() {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		List<FlightResponseModel> flights = new ArrayList<>();
		List<FlightDto> res = flightProxy.getAllFlights();
		for (FlightDto flightDto : res) {
			flights.add(modelMapper.map(flightDto, FlightResponseModel.class));
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(flights);

	}

	public ResponseEntity<?> myTestFallBack(Exception e) {
		return ResponseEntity.ok("within myTestFallBack method. FLIGHT-WS is down" + e.toString());
	}
}
